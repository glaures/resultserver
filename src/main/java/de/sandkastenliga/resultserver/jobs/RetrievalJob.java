package de.sandkastenliga.resultserver.jobs;

import de.sandkastenliga.resultserver.dtos.ChallengeDto;
import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.model.MatchInfo;
import de.sandkastenliga.resultserver.model.MatchState;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.challenge.ChallengeService;
import de.sandkastenliga.resultserver.services.match.MatchService;
import de.sandkastenliga.resultserver.services.schedule.ScheduleService;
import de.sandkastenliga.resultserver.services.sportsinfosource.SportsInfoSource;
import de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking.FifaRankingService;
import de.sandkastenliga.resultserver.services.team.TeamService;
import de.sandkastenliga.resultserver.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class RetrievalJob {

    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private SportsInfoSource infoSource;
    @Autowired
    private FifaRankingService fifaRankingService;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private TeamService teamService;

    private Date turboStop = new Date();

    // @PostConstruct
    public void initIfEmptyDB() throws ServiceException {
        if (challengeService.getAllChallenges().size() == 0)
            updateSchedule();
    }

    @Scheduled(fixedDelayString = "${timing.every2Minutes}", initialDelayString = "${timing.initialDelay}")
    // @Scheduled(fixedDelay=1000)
    public void updateUnfinishedMatchesInTurboMode() throws ServiceException {
        if (isInTurbo())
            updateUnfinishedMatches();
    }

    @Scheduled(fixedDelayString = "${timing.every3hours}", initialDelayString = "${timing.initialDelay}")
    public void updateUnfinishedMatches() throws ServiceException {
        Calendar lastParsed = Calendar.getInstance();
        lastParsed.add(Calendar.DATE, 1);
        DateUtils.resetToStartOfDay(lastParsed);
        Calendar dayBeforeYesterday = Calendar.getInstance();
        dayBeforeYesterday.add(Calendar.DATE, -1);
        DateUtils.resetToStartOfDay(dayBeforeYesterday);
        List<MatchDto> unfinishedPastMatches = matchService.getUnfinishedMatchesStartedBetween(dayBeforeYesterday.getTime(), lastParsed.getTime());
        for (MatchDto m : unfinishedPastMatches) {
            try {
                if (m.getStart().before(lastParsed.getTime())) {
                    List<MatchInfo> mis = infoSource.getMatchInfoForDay(m.getStart());
                    for (MatchInfo mi : mis) {
                        if (mi.getStart() != null && mi.getStart().before(dayBeforeYesterday.getTime()) && mi.getGoalsTeam1() < 0) {
                            mi.setState(MatchState.postponed);
                        }
                        matchService.handleMatchUpdate(mi.getCorrelationId(), mi.getRegion(), mi.getChallenge(), mi.getChallengeRankingUrl(),
                                mi.getRound(), mi.getTeam1(), mi.getTeam2(), mi.getStart(), mi.getGoalsTeam1(),
                                mi.getGoalsTeam2(), mi.getState(), mi.getStart(), mi.isExactTime());
                    }
                }
                MatchDto mDto = matchService.getMatch(m.getId());
                if (mDto.getStart() != null && mDto.getStart().before(dayBeforeYesterday.getTime())
                        && !(MatchState.isFinishedState(MatchState.values()[mDto.getMatchStateEnum()]))) {
                    matchService.markMatchAsCanceled(m.getId());
                }
            } catch (Throwable t) {
                throw new ServiceException("error.retrieval", t);
            } finally {
                lastParsed.setTime(m.getStart());
                DateUtils.resetToStartOfDay(lastParsed);
            }
        }
    }

    @Scheduled(fixedDelayString = "${timing.every3hours}", initialDelayString = "${timing.initialDelay}")
    public void updateSchedule() throws ServiceException {
        Calendar end = Calendar.getInstance();
        end.add(Calendar.DATE, 28);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DATE, -1);
        while (cal.getTime().before(end.getTime())) {
            scheduleService.updateSchedule(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }
    }

    // @Scheduled(fixedRate = 1000 * 60 * 60 * 24 * 7)
    public void updateFifaRanking() throws IOException {
        fifaRankingService.update();
    }

    @PostConstruct
    public void updateKoChallengeInfo() throws IOException {
        List<String[]> koChallenges = koChallenges = infoSource.getAllKoChallenges();
        for (String[] sArr : koChallenges) {
            challengeService.markKoChallenge(sArr[0], sArr[1]);
        }
    }

    @Scheduled(fixedDelayString = "${timing.every3hours}", initialDelayString = "${timing.initialDelay}")
    // @PostConstruct
    public void updateTeamPositions() throws ServiceException, IOException, InterruptedException {
        List<ChallengeDto> allChallengesWithOpenMatches = matchService.getAllChallengesWithOpenMatches();
        for (ChallengeDto c : allChallengesWithOpenMatches) {
            try {
                if (c.getRankUrl() != null && readyMatchesInChallenge(c)) {
                    Map<String, Integer> ranking;
                    ranking = infoSource.getTeamRankings(c.getRankUrl());
                    matchService.updateTeamPositions(c.getId(), ranking);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "${timing.everyMorningCron}") // every day morning at 3am
    public void cleanUp() {
        // delete unfinished outdated games
    }


    private boolean readyMatchesInChallenge(ChallengeDto c) {
        return matchRepository.getReadyMatches(challengeRepository.getOne(c.getId())).size() > 0;
    }

    public Date getTurboStop() {
        return this.turboStop;
    }

    public boolean isInTurbo() {
        return new Date().before(turboStop);
    }

    public void prolongTurbo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        turboStop = cal.getTime();
    }
}
