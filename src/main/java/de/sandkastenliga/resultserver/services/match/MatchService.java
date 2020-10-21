package de.sandkastenliga.resultserver.services.match;

import de.sandkastenliga.resultserver.dtos.ChallengeDto;
import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.MatchState;
import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.services.AbstractJpaDependentService;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.challenge.ChallengeService;
import de.sandkastenliga.resultserver.services.sportsinfosource.RegionRelevanceProvider;
import de.sandkastenliga.resultserver.services.team.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchService extends AbstractJpaDependentService {

    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    private MatchRepository matchRepository;
    private ChallengeService challengeService;
    private RegionRelevanceProvider regionRelevanceProvider;
    private TeamService teamService;
    private TeamRepository teamRepository;

    public MatchService(MatchRepository matchRepository,
                        ChallengeService challengeService,
                        RegionRelevanceProvider regionRelevanceProvider,
                        TeamService teamService,
                        TeamRepository teamRepository) {
        this.matchRepository = matchRepository;
        this.challengeService = challengeService;
        this.regionRelevanceProvider = regionRelevanceProvider;
        this.teamService = teamService;
        this.teamRepository = teamRepository;
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
        return new MatchDto(closest);
    }

    public List<MatchDto> getAllMatchesAtDays(Date startDay, Date endDay) {
        return matchRepository.getAllMatchesAtDays(getStartOfDay(startDay), getEndOfDay(endDay))
                .stream()
                .map(m -> new MatchDto(m))
                .collect(Collectors.toList());
    }

    public MatchDto getMatch(Integer matchId) throws ServiceException {
        return new MatchDto(getValid(matchId, matchRepository));
    }

    public List<MatchDto> getUnfinishedMatchesStartedBetween(Date start, Date end) {
        List<Match> matches = matchRepository.findMatchesByStartAfterAndStartBeforeAndStateInOrderByStartDesc(start, end, MatchState.getUnfinishedStates());
        return matches.stream()
                .map(m -> new MatchDto(m))
                .collect(Collectors.toList());
    }

    public List<MatchDto> getMatchesByIdList(List<Integer> idList) {
        return matchRepository.getMatchesByIdList(idList)
                .stream()
                .map(m -> new MatchDto(m))
                .collect(Collectors.toList());
    }

    public List<ChallengeDto> getAllChallengesWithOpenMatches() {
        return matchRepository.getAllChallengesWithOpenMatches(MatchState.getUnfinishedStates())
                .stream()
                .map(c -> new ChallengeDto(c))
                .collect(Collectors.toList());
    }

    public List<MatchDto> getAllMatchesByRegionChallengeAndDay(String country, String challengeName, String day) throws ParseException {
        Date d = df.parse(day);
        return matchRepository.findMatchesByChallenge_RegionAndChallenge_NameAndStart(country, challengeName, d)
                .stream()
                .map(m -> new MatchDto(m))
                .collect(Collectors.toList());
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
        m.setStrengthTeam1(t1.getCurrentStrength());
        m.setStrengthTeam2(t2.getCurrentStrength());
        // bei einem manuell gesetzen Spiel soll nicht wieder von ready auf scheduled gesprungen werden
        m.setState(matchState == MatchState.scheduled && m.isManuallyScheduled() ? MatchState.ready : matchState);
        m.setLastUpdated(new Date());
        matchRepository.save(m);
        return m.getId();
    }

    @Transactional
    public void markMatchAsCanceled(Integer matchId) throws ServiceException {
        Match m = getValid(matchId, matchRepository);
        m.setState(MatchState.canceled);
        matchRepository.save(m);
    }

    // private helpers
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
