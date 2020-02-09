package de.sandkastenliga.resultserver.services.team;

import de.sandkastenliga.resultserver.dtos.ExtendedTeamDto;
import de.sandkastenliga.resultserver.dtos.TeamStrengthSnapshotDto;
import de.sandkastenliga.resultserver.dtos.TeamDto;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.model.TeamStrengthSnapshot;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.repositories.TeamStrengthSnapshotRepository;
import de.sandkastenliga.resultserver.services.AbstractJpaDependentService;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.tools.projector.core.Projector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
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
    private Projector projector;

    @Autowired
    public TeamService(TeamRepository teamRepository, ChallengeRepository challengeRepository,
                       MatchRepository matchRepository,
                       TeamStrengthSnapshotRepository teamStrengthSnapshotRepository,
                       Projector projector) {
        this.teamRepository = teamRepository;
        this.challengeRepository = challengeRepository;
        this.matchRepository = matchRepository;
        this.teamStrengthSnapshotRepository = teamStrengthSnapshotRepository;
        this.projector = projector;
    }

    @Transactional
    public ExtendedTeamDto getExtendedTeamDto(String id) throws ServiceException {
        Team t = getValid(id, teamRepository);
        ExtendedTeamDto res = projector.project(teamRepository.getOne(id), ExtendedTeamDto.class);
        List<TeamStrengthSnapshot> snapshots = teamStrengthSnapshotRepository.findAllByTeamOrderBySnapshotDateDesc(t);
        res.setTeamStrengthSnapshots(snapshots.stream().map(ss -> projector.project(ss, TeamStrengthSnapshotDto.class)).collect(Collectors.toList()));
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

    public void setInitialTeamStrengths() throws IOException {
        Resource initialStrengthResource = new ClassPathResource("/initialstrengths.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(initialStrengthResource.getInputStream()));
        String line = null;
        while ((line = br.readLine()) != null) {
            StringTokenizer tok = new StringTokenizer(line, ";", false);
            int strength = Integer.parseInt(tok.nextToken());
            String teamName = tok.nextToken().trim();
            try {
                System.out.println("setting team strength of " + teamName + " to " + strength);
                setTeamStrength(teamName, strength);
            } catch (ServiceException se) {
                System.out.println(se.getMessage());
            }
        }
    }

    @Transactional
    public void updateTeamStrengths(Map<String, Integer> currentRanking) {
        if (currentRanking.size() == 0)
            return;
        // do nothing
        int maxRank = currentRanking.values().stream().max(Comparator.comparingInt(Integer::intValue)).get();
        int minRank = currentRanking.values().stream().min(Comparator.comparingInt(Integer::intValue)).get();
        if (maxRank == minRank)
            // do nothing, all have the same ranking
            return;
        for (String teamid : currentRanking.keySet()) {
            Optional<Team> tOpt = teamRepository.findById(teamid);
            if (tOpt.isPresent()) {
                Team t = tOpt.get();
                int recentAverageStrength = getRecentAverageStrength(t);
                int strengthBasedOnCurrentRanking =
                        (int) (100f - (((float) (currentRanking.get(teamid) - minRank) / (maxRank - minRank)) * 100f));
                int newStrength =
                        (int) ((recentAverageStrength * (strengthAverageSpan - 1) + strengthBasedOnCurrentRanking) / strengthAverageSpan);
                if (newStrength != t.getCurrentStrength()) {
                    t.setCurrentStrength(newStrength);
                    teamRepository.save(t);
                    generateStrengthSnapshot(t);
                }
            } else {
                log.warn("can not update strength for team " + teamid + " not found in combination with provided ranking");
            }
        }
    }

    /**
     * Diese Methode schaut sich die letzten <pre>strengthAverageSpan-1</pre>
     * StrengthSnapshots eines Teams an und gibt den Mittelwert zurück.
     * Wird benötigt zur Berechnung der neuen Stärke.
     */
    private int getRecentAverageStrength(Team t) {
        List<TeamStrengthSnapshot> strengthSnapshots =
                teamStrengthSnapshotRepository.findAllByTeamOrderBySnapshotDateDesc(t);
        if (strengthSnapshots.size() == 0)
            return t.getCurrentStrength(); // vordefinierte Stärke wird wiederverwendet
        int strengthSum = 0;
        int snapshotsConsidered = 0;
        for (TeamStrengthSnapshot tss : strengthSnapshots) {
            strengthSum += tss.getStrength();
            snapshotsConsidered++;
            if (snapshotsConsidered == strengthAverageSpan - 1)
                break;
        }
        return (int) (strengthSum / snapshotsConsidered);
    }

    @Transactional
    public void setTeamStrength(String name, int strength) throws ServiceException {
        List<Team> teams = teamRepository.findTeamsByName(name);
        if (teams.size() == 0)
            throw new ServiceException("no such team: " + name);
        for (Team t : teams) {
            t.setCurrentStrength(strength);
            teamRepository.save(t);
            generateStrengthSnapshot(t);
        }
    }

    @Transactional
    public void cleanUpStrengthSnapshots() {
        List<Team> allTeams = teamRepository.findAll();
        for(Team t : allTeams){
            List<TeamStrengthSnapshot> allTss = teamStrengthSnapshotRepository.findAllByTeamOrderBySnapshotDateDesc(t);
            if(allTss.size() > 1){
                allTss.remove(0);
                teamStrengthSnapshotRepository.deleteAll(allTss);
            }
        }
    }
}
