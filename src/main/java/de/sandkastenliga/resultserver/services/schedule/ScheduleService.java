package de.sandkastenliga.resultserver.services.schedule;

import de.sandkastenliga.resultserver.model.MatchInfo;
import de.sandkastenliga.resultserver.services.AbstractJpaDependentService;
import de.sandkastenliga.resultserver.services.challenge.ChallengeService;
import de.sandkastenliga.resultserver.services.error.ErrorHandlingService;
import de.sandkastenliga.resultserver.services.match.MatchService;
import de.sandkastenliga.resultserver.services.sportsinfosource.KickerSportsInfoSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleService extends AbstractJpaDependentService {

    private KickerSportsInfoSource sportsInfoSource;
    private MatchService matchService;
    private ChallengeService challengeService;
    private ErrorHandlingService errorHandlingService;

    @Autowired
    public ScheduleService(KickerSportsInfoSource sportsInfoSource, MatchService matchService,
                           ChallengeService challengeService, ErrorHandlingService errorHandlingService) {
        this.sportsInfoSource = sportsInfoSource;
        this.matchService = matchService;
        this.challengeService = challengeService;
        this.errorHandlingService = errorHandlingService;
    }

    @Transactional
    public void updateSchedule(final Date date) {
        List<MatchInfo> mis = new ArrayList<>();
        try {
            mis = sportsInfoSource.getMatchInfoForDay(date);
        } catch (Throwable t) {
            errorHandlingService.handleError(t);
        }
        for (MatchInfo mi : mis) {
            if (challengeService.isRelevantRegion(mi.getRegion())) {
                matchService.handleMatchUpdate(mi.getCorrelationId(), mi.getRegion(), mi.getChallenge(),
                        mi.getChallengeRankingUrl(), mi.getRound(),
                        mi.getTeam1Id(), mi.getTeam1(), mi.getTeam2Id(), mi.getTeam2(),
                        mi.getStart(), mi.getGoalsTeam1(), mi.getGoalsTeam2(),
                        mi.getState(), date, mi.isExactTime());
            }
        }

    }

}
