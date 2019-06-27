package de.sandkastenliga.resultserver.services.schedule;

import de.sandkastenliga.resultserver.dtos.MatchInfo;
import de.sandkastenliga.resultserver.services.AbstractJpaDependentService;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.match.MatchService;
import de.sandkastenliga.resultserver.services.sportsinfosource.SportsInfoSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleService extends AbstractJpaDependentService {

    @Autowired
    private SportsInfoSource sportsInfoSource;
    @Autowired
    private MatchService matchService;

    @Autowired
    public ScheduleService(SportsInfoSource sportsInfoSource, MatchService matchService) {
        this.sportsInfoSource = sportsInfoSource;
        this.matchService = matchService;
    }

    @Transactional
    public void updateSchedule(final Date date) throws ServiceException {
        List<MatchInfo> mis = new ArrayList<MatchInfo>();
        try {
            mis = sportsInfoSource.getMatchInfoForDay(date);
        } catch (Throwable t) {
            throw new ServiceException("error.retrievalError", t);
        }
        for (MatchInfo mi : mis) {
            final MatchInfo miFinal = mi;
            matchService.handleMatchUpdate(mi.getCorrelationId(), miFinal.getRegion(), miFinal.getChallenge(),
                    miFinal.getChallengeRankingUrl(), miFinal.getRound(), miFinal.getTeam1(), miFinal.getTeam2(),
                    miFinal.getStart(), miFinal.getGoalsTeam1(), miFinal.getGoalsTeam2(),
                    miFinal.getState(), date);
        }
    }

}
