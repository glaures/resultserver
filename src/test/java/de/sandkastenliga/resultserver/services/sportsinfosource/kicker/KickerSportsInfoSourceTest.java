package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.dtos.MatchInfo;
import de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking.FifaRankingService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class KickerSportsInfoSourceTest {

    private final Logger logger = LoggerFactory.getLogger(KickerSportsInfoSource.class);
    private KickerSportsInfoSource kickerSportsInfoSource;

    @Before
    public void setup() throws IOException {
        FifaRankingService frs = new FifaRankingService();
        this.kickerSportsInfoSource = new KickerSportsInfoSource(frs);
    }


    @Test
    public void testGetAllKoChallenges() throws IOException {
        kickerSportsInfoSource.setKoChallengesFileName("ko-challenges.csv");
        List<String[]> res = kickerSportsInfoSource.getAllKoChallenges();
        boolean bundesligaFound = false;
        boolean europeFound = false;
        boolean dfbPokalFound = false;
        boolean germanyFound = false;
        Set<String[]> allChallengesSoFar = new HashSet<>();
        for (String[] sa : res) {
            String country = sa[0];
            String challenge = sa[1];
            assertNotNull(country);
            assertNotNull(challenge);
            if (allChallengesSoFar.contains(sa)) {
                fail("Duplicate found: " + country + "/" + challenge + " already parsed");
            }
        }
    }

    @Test
    public void testGetMatchInfoForDay() throws Throwable {
        Calendar testCal = Calendar.getInstance();
        testCal.setTime(new Date());
        List<MatchInfo> matchInfos = kickerSportsInfoSource.getMatchInfoForDay(new Date());
        testCal.add(Calendar.DATE, -11);
        matchInfos = kickerSportsInfoSource.getMatchInfoForDay(testCal.getTime());
        testCal.add(Calendar.DATE, +12);
        matchInfos = kickerSportsInfoSource.getMatchInfoForDay(testCal.getTime());
        testCal.add(Calendar.DATE, +7);
        matchInfos = kickerSportsInfoSource.getMatchInfoForDay(testCal.getTime());
    }

    @Test
    public void testGetTeamRankings() throws Throwable {
        String[] urls = new String[]{"https://www.kicker.de/1-bundesliga/tabelle/2018-19/2", "https://www.kicker.de/canadian-premier-league-fb-1/tabelle/2018-19/9"};
        for (String url : urls) {
            Map<String, Integer> res = kickerSportsInfoSource.getTeamRankings(url);
            for (String team : res.keySet()) {
                logger.info(res.get(team) + ". \t" + team);
            }
        }
    }
}