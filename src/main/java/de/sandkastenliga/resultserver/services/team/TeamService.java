package de.sandkastenliga.resultserver.services.team;

import de.sandkastenliga.resultserver.dtos.ExtendedTeamDto;
import de.sandkastenliga.resultserver.dtos.StrengthSnapshotDto;
import de.sandkastenliga.resultserver.dtos.TeamDto;
import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.model.TeamStrengthSnapshot;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.repositories.TeamStrengthSnapshotRepository;
import de.sandkastenliga.resultserver.services.AbstractJpaDependentService;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking.FifaRankingService;
import de.sandkastenliga.tools.projector.core.Projector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class TeamService extends AbstractJpaDependentService {

    private static final Log log = LogFactory.getLog(TeamService.class);
    @Value("${resultserver.strengthAverageSpan}")
    private int strengthAverageSpan;
    private final TeamRepository teamRepository;
    private final ChallengeRepository challengeRepository;
    private final TeamStrengthSnapshotRepository teamStrengthSnapshotRepository;
    private MatchRepository matchRepository;
    private FifaRankingService fifaRankingService;
    private Projector projector;

    @Autowired
    public TeamService(TeamRepository teamRepository, ChallengeRepository challengeRepository, MatchRepository matchRepository, FifaRankingService fifaRankingService, TeamStrengthSnapshotRepository teamStrengthSnapshotRepository, Projector projector) {
        this.teamRepository = teamRepository;
        this.challengeRepository = challengeRepository;
        this.matchRepository = matchRepository;
        this.teamStrengthSnapshotRepository = teamStrengthSnapshotRepository;
        this.fifaRankingService = fifaRankingService;
        this.projector = projector;
    }

    @Transactional
    public ExtendedTeamDto getExtendedTeamDto(String id) throws ServiceException {
        Team t = getValid(id, teamRepository);
        ExtendedTeamDto res = projector.project(teamRepository.getOne(id), ExtendedTeamDto.class);
        List<TeamStrengthSnapshot> snapshots = teamStrengthSnapshotRepository.findAllByTeamOrderBySnapshotDateDesc(t);
        res.setStrengthSnapshots(snapshots.stream().map(ss -> projector.project(ss, StrengthSnapshotDto.class)).collect(Collectors.toList()));
        return res;
    }


    public TeamDto getOneById(String id) {
        return projector.project(teamRepository.getOne(id), TeamDto.class);
    }

    @Transactional
    public TeamDto getOrCreateTeam(String id, String name) {
        Optional<Team> tOpt = teamRepository.findById(id);
        if (!tOpt.isPresent()) {
            Team t = new Team();
            t.setId(id);
            t.setName(name);
            t = teamRepository.save(t);
            generateStrengthSnapshot(t);
            return projector.project(t, TeamDto.class);
        }
        return projector.project(tOpt.get(), TeamDto.class);
    }

    private void generateStrengthSnapshot(Team t) {
        TeamStrengthSnapshot tss = new TeamStrengthSnapshot();
        tss.setStrength(t.getCurrentStrength());
        tss.setSnapshotDate(new Date());
        tss.setTeam(t);
        teamStrengthSnapshotRepository.save(tss);
    }

    @Transactional
    public void updateTeamPositions(int challengeId, Map<String, Integer> ranking) throws ServiceException {
        List<Match> matches = matchRepository.getReadyMatches(getValid(challengeId, challengeRepository));
        for (Match m : matches) {
            for (Team t : new Team[]{m.getTeam1(), m.getTeam2()}) {
                if (ranking.containsKey(t.getName())) {
                    m.setPos(t, ranking.get(t.getName()));
                    matchRepository.save(m);
                }
            }
        }
    }

    @Transactional
    public void updateFifaTeamPositions() throws ServiceException {
        // update Fifa positions
        /*
        List<String> fifaRanking = fifaRankingService.getRanking();
        int r = 1;
        for (String teamName : fifaRanking) {
            Team t = getValid(teamName, teamRepository);
            // t.setMainChallengePos(r);
            teamRepository.save(t);
            r++;
        }
        */
    }

    @Transactional
    public void updateTeamStrengths(int challengeId, Map<String, Integer> currentRanking) {
        // do nothing
        Challenge c = challengeRepository.getOne(challengeId);
        int maxRank = currentRanking.values().stream().max(Comparator.comparingInt(Integer::intValue)).get();
        int minRank = currentRanking.values().stream().min(Comparator.comparingInt(Integer::intValue)).get();
        if (maxRank == minRank)
            // do nothing, all have the same ranking
            return;
        for (String teamid : currentRanking.keySet()) {
            Optional<Team> tOpt = teamRepository.findById(teamid);
            if (tOpt.isPresent()) {
                Team t = tOpt.get();
                int currentStrength = (int) (100f - (((float) (currentRanking.get(teamid) - minRank) / (maxRank - minRank)) * 100f));
                int newStrength =  (int)(((float)(t.getCurrentStrength() * (strengthAverageSpan - 1)) + currentStrength) / strengthAverageSpan);
                if(newStrength != t.getCurrentStrength()) {
                    t.setCurrentStrength(newStrength);
                    teamRepository.save(t);
                    generateStrengthSnapshot(t);
                }
            } else {
                log.warn("can not update strength for team " + teamid + " not found in combination with provided ranking");
            }
        }
    }

    @Transactional
    public void setTeamStrength(String name, int strength) throws ServiceException {
        List<Team> teams = teamRepository.findTeamsByName(name);
        if(teams.size() == 0)
            throw new ServiceException("no such team: " + name);
        for(Team t : teams) {
            t.setCurrentStrength(strength);
            teamRepository.save(t);
            generateStrengthSnapshot(t);
        }
    }


}
