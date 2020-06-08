package de.sandkastenliga.resultserver.services.team;

import de.sandkastenliga.resultserver.dtos.TeamDto;
import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.Rank;
import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.repositories.RankRepository;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.services.AbstractJpaDependentService;
import de.sandkastenliga.resultserver.services.sportsinfosource.RegionRelevanceProvider;
import de.sandkastenliga.tools.projector.core.Projector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TeamService extends AbstractJpaDependentService {

    public static final int RANK_RANK_IDX = 0;
    public static final int RANK_POINTS_IDX = 1;

    private static final Log log = LogFactory.getLog(TeamService.class);

    private final TeamRepository teamRepository;
    private final ChallengeRepository challengeRepository;
    private MatchRepository matchRepository;
    private RankRepository rankRepository;
    private RegionRelevanceProvider regionRelevanceProvider;
    private Projector projector;

    @Autowired
    public TeamService(TeamRepository teamRepository, ChallengeRepository challengeRepository,
                       MatchRepository matchRepository,
                       RankRepository rankRepository,
                       RegionRelevanceProvider regionRelevanceProvider,
                       Projector projector) {
        this.teamRepository = teamRepository;
        this.challengeRepository = challengeRepository;
        this.rankRepository = rankRepository;
        this.regionRelevanceProvider = regionRelevanceProvider;
        this.matchRepository = matchRepository;
        this.projector = projector;
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
            return projector.project(t, TeamDto.class);
        }
        return projector.project(tOpt.get(), TeamDto.class);
    }

    @Transactional
    public void updateTeamRankingsForChallenge(int challengeId, Map<String, Integer[]> ranking) {
        Challenge c = challengeRepository.getOne(challengeId);
        if (regionRelevanceProvider.isRelevantRegion(c.getRegion())) {
            List<String> unfinishedRounds = challengeRepository.getUnfinishedRoundsForChallenge(challengeId);
            for (String round : unfinishedRounds) {
                int roundInt = Integer.parseInt(round);
                Date nextUnfinishedMatchDate = challengeRepository.getDateOfRoundInChallenge(challengeId,
                        round);
                Calendar cal = Calendar.getInstance();
                cal.setTime(nextUnfinishedMatchDate);
                int year = cal.get(Calendar.YEAR);
                for (String teamId : ranking.keySet()) {
                    Rank r = rankRepository.getRankByChallengeAndRoundAndYearAndTeam_Id(c, roundInt, year, teamId);
                    if (r == null) {
                        r = new Rank();
                        r.setChallenge(c);
                        r.setRound(roundInt);
                        r.setTeam(teamRepository.getOne(teamId));
                        r.setYear(year);
                    }
                    r.setRank(ranking.get(teamId)[RANK_RANK_IDX]);
                    r.setPoints(ranking.get(teamId)[RANK_POINTS_IDX]);
                    rankRepository.save(r);
                    // update legacy rankings
                    List<Match> matchesOfTeam = matchRepository.getScheduledOrReadyMatchesOfTeamInChallenge(c, teamId);
                    for(Match m : matchesOfTeam){
                        if(m.getTeam1() != null && m.getTeam1().getId().equals(teamId)){
                            m.setPosTeam1(r.getRank());
                        }
                        if(m.getTeam2() != null && m.getTeam2().getId().equals(teamId)){
                            m.setPosTeam2(r.getRank());
                        }
                    }
                }
            }
        }
    }
}

