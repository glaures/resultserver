package de.sandkastenliga.resultserver.services.match;

import de.sandkastenliga.resultserver.dtos.ChallengeDto;
import de.sandkastenliga.resultserver.dtos.MatchDto;
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
import de.sandkastenliga.resultserver.services.sportsinfosource.RegionRelevanceProvider;
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
    private RegionRelevanceProvider regionRelevanceProvider;
    private TeamService teamService;
    private TeamRepository teamRepository;
    private Projector projector;

    public MatchService(MatchRepository matchRepository,
                        ChallengeRepository challengeDao,
                        ChallengeService challengeService,
                        RegionRelevanceProvider regionRelevanceProvider,
                        TeamService teamService,
                        TeamRepository teamRepository,
                        Projector projector) {
        this.matchRepository = matchRepository;
        this.challengeRepository = challengeDao;
        this.challengeService = challengeService;
        this.regionRelevanceProvider = regionRelevanceProvider;
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
        return matchRepository.getAllChallengesWithOpenMatches(MatchState.getUnfinishedStates()).stream().map(c -> projector.project(c, ChallengeDto.class)).collect(Collectors.toList());
    }


    // manipulative methods

    @Transactional
    public int handleMatchUpdate(String correlationId, String region, String challenge, String challengeRankingUrl, String round,
                                 String team1Id, String team1Name, String team2Id, String team2Name, Date date, int goalsTeam1, int goalsTeam2, MatchState matchState, Date start, boolean exactTime) {
        if (!regionRelevanceProvider.isRelevantRegion(region))
            return -1;
        // create teams if they do not exists in the DB yet
        teamService.getOrCreateTeam(team1Id, team1Name);
        teamService.getOrCreateTeam(team2Id, team2Name);
        Challenge c = challengeService.getOrCreateChallenge(region, challenge, challengeRankingUrl, new Date());
        Team t1 = teamRepository.getOne(team1Id);
        Team t2 = teamRepository.getOne(team2Id);
        Optional<Match> mOpt = matchRepository.findMatchByCorrelationId(correlationId);
        Match m = null;
        if (!mOpt.isPresent()) {
            m = new Match();
            m.setChallenge(c);
            m.setRound(round);
            m.setCorrelationId(correlationId);
        } else {
            m = mOpt.get();
        }
        if (!mOpt.isPresent() || exactTime) {
            m.setStart(date);
        }
        m.setTeam1(t1);
        m.setTeam2(t2);
        m.setGoalsTeam1(goalsTeam1);
        m.setGoalsTeam2(goalsTeam2);
        m.setState(matchState);
        m.setLastUpdated(new Date());
        matchRepository.save(m);
        return m.getId();
    }

    private boolean threeHoursPassedSinceStart(Date d) {
        return new Date().getTime() - d.getTime() > 1000 * 60 * 60 * 3;
    }

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

    @Transactional
    public void updateTeamStrengthsAndPositions(Integer challengeId, Map<String, Integer> ranking) throws ServiceException {
        List<Match> allOpenMatchesForChallenge = matchRepository.getReadyMatches(getValid(challengeId, challengeRepository));
        for (Match m : allOpenMatchesForChallenge) {
            int team = 1;
            for (Team t : new Team[]{m.getTeam1(), m.getTeam2()}) {
                if (ranking.containsKey(t.getId())) {
                    if (team == 1) {
                        m.setPosTeam1(ranking.get(t.getId()));
                        m.setStrengthTeam1(t.getCurrentStrength());
                    } else {
                        m.setPosTeam2(ranking.get(t.getId()));
                        m.setStrengthTeam2(t.getCurrentStrength());
                    }
                }
                team++;
            }
        }
    }

    @Transactional
    public void updateTeamStrengthsForTeamsWithoutRanking(Integer challengeId) throws ServiceException {
        List<Match> allOpenMatchesForChallenge = matchRepository.getReadyMatches(getValid(challengeId, challengeRepository));
        for (Match m : allOpenMatchesForChallenge) {
            m.setStrengthTeam1(m.getTeam1().getCurrentStrength());
            m.setStrengthTeam2(m.getTeam2().getCurrentStrength());
            matchRepository.save(m);
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