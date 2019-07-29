package de.sandkastenliga.resultserver.services.team;

import de.sandkastenliga.resultserver.dtos.TeamDto;
import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TeamService extends AbstractJpaDependentService {

    private static final Log log = LogFactory.getLog(TeamService.class);
    @Value("${resultserver.strengthAverageSpan}")
    private int strengthAverageSpan;
    private final TeamRepository teamRepository;
    private final ChallengeRepository challengeRepository;
    private MatchRepository matchRepository;
    private FifaRankingService fifaRankingService;
    private Projector projector;

    @Autowired
    public TeamService(TeamRepository teamRepository, ChallengeRepository challengeRepository, MatchRepository matchRepository, FifaRankingService fifaRankingService, Projector projector) {
        this.teamRepository = teamRepository;
        this.challengeRepository = challengeRepository;
        this.matchRepository = matchRepository;
        this.fifaRankingService = fifaRankingService;
        this.projector = projector;
    }

    public TeamDto findTeamByName(String name) {
        Optional<Team> tOpt = teamRepository.findTeamByName(name);
        if (tOpt.isPresent()) {
            return projector.project(tOpt.get(), TeamDto.class);
        } else {
            return null;
        }
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
            return projector.project(teamRepository.save(t), TeamDto.class);
        }
        return projector.project(tOpt.get(), TeamDto.class);
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
            if(tOpt.isPresent()) {
                Team t = tOpt.get();
                int strength = (int) (100f - (((float) currentRanking.get(teamid) / (maxRank - minRank)) * 100f));
                t.setCurrentStrength((t.getCurrentStrength() * (strengthAverageSpan - 1) + strength) / strengthAverageSpan);
                teamRepository.save(t);
            } else {
                log.warn("can not update strength for team " + teamid + " not foudn in combination with provided ranking");
            }
        }
    }


}
