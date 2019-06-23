package de.sandkastenliga.resultserver.services.team;

import de.sandkastenliga.resultserver.dtos.TeamDto;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.services.AbstractJpaDependentService;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking.FifaRankingService;
import de.sandkastenliga.tools.projector.core.Projector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TeamService extends AbstractJpaDependentService {

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

    @Transactional
    public TeamDto getOrCreateTeam(String name) {
        Optional<Team> tOpt = teamRepository.findById(name);
        if (!tOpt.isPresent()) {
            Team t = new Team();
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
        List<String> fifaRanking = fifaRankingService.getRanking();
        int r = 1;
        for (String teamName : fifaRanking) {
            Team t = getValid(teamName, teamRepository);
            // t.setMainChallengePos(r);
            teamRepository.save(t);
            r++;
        }
    }


}
