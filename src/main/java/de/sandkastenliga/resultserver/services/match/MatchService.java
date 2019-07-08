package de.sandkastenliga.resultserver.services.match;

import de.sandkastenliga.resultserver.dtos.ChallengeDto;
import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.dtos.MatchInfo;
import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.MatchState;
import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.services.AbstractJpaDependentService;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.challenge.ChallengeService;
import de.sandkastenliga.resultserver.services.team.TeamService;
import de.sandkastenliga.tools.projector.core.Projector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService extends AbstractJpaDependentService {

    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    private MatchRepository matchRepository;
    private ChallengeRepository challengeRepository;
    private ChallengeService challengeService;
    private TeamService teamService;
    private TeamRepository teamRepository;
    private Projector projector;

    public MatchService(MatchRepository matchRepository, ChallengeRepository challengeDao, ChallengeService challengeService, TeamService teamService, TeamRepository teamRepository, Projector projector) {
        this.matchRepository = matchRepository;
        this.challengeRepository = challengeDao;
        this.challengeService = challengeService;
        this.teamService = teamService;
        this.teamRepository = teamRepository;
        this.projector = projector;
    }

    // retrieval methods

    public MatchDto getClosestMatchByTeams(String team1, String team2, Date date) {
        List<Match> matches = matchRepository.findClosestMatchesByTeams(team1, team2);
        if (matches.size() == 0) {
            return null;
        }
        Match closest = matches.get(0);
        long dateTime = date.getTime();
        long timeDiff = Math.abs(closest.getStart().getTime() - dateTime);
        for (Match m : matches) {
            long td = Math.abs(m.getStart().getTime() - dateTime);
            if (td < timeDiff) {
                timeDiff = td;
                closest = m;
            } else {
                break;
            }
        }
        return projector.project(closest, MatchDto.class);
    }

    public List<MatchDto> getAllMatchesAtDays(Date startDay, Date endDay) {
        return matchRepository.getAllMatchesAtDays(getStartOfDay(startDay), getEndOfDay(endDay)).stream().map(m -> projector.project(m, MatchDto.class)).collect(Collectors.toList());
    }

    public MatchDto getMatch(Integer matchId) throws ServiceException {
        return projector.project(getValid(matchId, matchRepository), MatchDto.class);
    }

    public List<MatchDto> getUnfinishedMatchesStartedBetween(Date start, Date end) {
        // return matchRepository.getUnfinishedMatchesStartedBefore(date).stream().map(m -> projector.project(m, MatchDto.class)).collect(Collectors.toList());
        List<Match> matches = matchRepository.findMatchesByStartAfterAndStartBeforeAndStateInOrderByStartDesc(start, end, MatchState.getUnfinishedStates());
        return matches.stream().map(m -> projector.project(m, MatchDto.class)).collect(Collectors.toList());
    }

    public List<Match> getReadyMatches(int challengeId) throws ServiceException {
        Challenge c = getValid(challengeId, challengeRepository);
        return matchRepository.getReadyMatches(c);
    }

    public List<MatchDto> getMatchesByIdList(List<Integer> idList) {
        return matchRepository.getMatchesByIdList(idList).stream().map(m -> projector.project(m, MatchDto.class)).collect(Collectors.toList());

    }

    public List<ChallengeDto> getAllChallengesWithOpenMatches() {
        return matchRepository.getAllChallengesWithOpenMatches().stream().map(c -> projector.project(c, ChallengeDto.class)).collect(Collectors.toList());
    }


    // manipulative methods

    @Transactional
    public int handleMatchUpdate(String correlationId, String region, String challenge, String challengeRankingUrl, String round,
                                 String team1, String team2, Date date, int goalsTeam1, int goalsTeam2, MatchState matchState, Date start, boolean exactTime) throws ServiceException {
        if (!challengeService.isRelevantRegion(region))
            return -1;
        Challenge c = challengeService.getOrCreateChallenge(region, challenge, challengeRankingUrl, date);
        Team t1 = teamRepository.getOne(teamService.getOrCreateTeam(team1).getName());
        Team t2 = teamRepository.getOne(teamService.getOrCreateTeam(team2).getName());
        Optional<Match> mOpt = matchRepository.findMatchByCorrelationId(correlationId);
        Match m = null;
        if (!mOpt.isPresent()) {
            m = new Match();
            m.setChallenge(c);
            m.setRound(round);
        } else {
            m = mOpt.get();
            if (MatchState.isFinishedState(m.getState()))
                return m.getId();
        }
        if (!mOpt.isPresent() || exactTime) {
            m.setStart(date);
        }
        m.setCorrelationId(correlationId);
        m.setTeam1(t1);
        m.setTeam2(t2);
        m.setGoalsTeam1(goalsTeam1);
        m.setGoalsTeam2(goalsTeam2);
        if (m.getStart() != null && threeHoursPassedSinceStart(m.getStart()) && goalsTeam1 >= 0 && !MatchState.isFinishedState(m.getState())) {
            m.setState(MatchState.finished);
        } else {
            m.setState(matchState);
        }
        m.setLastUpdated(new Date());
        matchRepository.save(m);
        return m.getId();
    }

    private boolean threeHoursPassedSinceStart(Date d) {
        return new Date().getTime() - d.getTime() > 1000 * 60 * 60 * 3;
    }

    /*
    private boolean isMoreExactDate(Date newDate, Date currentDate) {
        if (currentDate == null) {
            return true;
        } else if (newDate == null) {
            return false;
        } else {
            Calendar newDateCal = Calendar.getInstance();
            newDateCal.setTime(newDate);
            Calendar currentDateCal = Calendar.getInstance();
            currentDateCal.setTime(currentDate);
            if (currentDateCal.get(Calendar.DATE) != newDateCal.get(Calendar.DATE)) {
                return true;
            } else if (currentDateCal.get(Calendar.HOUR_OF_DAY) == 0) {
                return true;
            }
        }
        return false;
    }
    */

    @Transactional
    public void markMatchAsCanceled(Integer matchId) throws ServiceException {
        Match m = getValid(matchId, matchRepository);
        m.setState(MatchState.canceled);
        matchRepository.save(m);
    }

    public List<MatchDto> getAllMatchesByRegionChallengeAndDay(String country, String challengeName, String day) throws ParseException {
        Date d = df.parse(day);
        return matchRepository.findMatchesByChallenge_RegionAndChallenge_NameAndStart(country, challengeName, d).stream().map(m -> projector.project(m, MatchDto.class)).collect(Collectors.toList());
    }

    public void updateTeamPositions(Integer challengeId, Map<String, Integer> ranking) throws ServiceException {
        List<Match> allOpenMatchesForChallenge = matchRepository.getReadyMatches(getValid(challengeId, challengeRepository));
        for (Match m : allOpenMatchesForChallenge) {
            int team = 1;
            for (Team t : new Team[]{m.getTeam1(), m.getTeam2()}) {
                if (ranking.containsKey(t.getName())) {
                    if (team == 1)
                        m.setPosTeam1(ranking.get(t.getName()));
                    else
                        m.setPosTeam2(ranking.get(t.getName()));
                }
                team++;
            }
        }
    }


    // private helpers

    private Match fuzzyFindMatch(Challenge c, Team t1, Team t2, String round, Date start) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        Date minStart = cal.getTime();
        cal.add(Calendar.MONTH, 4);
        Date maxStart = cal.getTime();
        List<Match> matches = matchRepository.findMatchByChallengeAndTeamsAndTimeframe(c, t1, t2, round, minStart, maxStart);
        if (matches.size() > 0)
            return matches.get(0);
        return null;
    }

    private Date getStartOfDay(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfDay(Date d) {
        Date start = getStartOfDay(d);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

}