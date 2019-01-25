package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.dtos.MatchInfo;
import de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking.FifaRankingService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class KickerSportsInfoSourceTest {

    private KickerSportsInfoSource kickerSportsInfoSource;

    @Before
    public void setup() throws IOException {
        FifaRankingService frs = new FifaRankingService();
        this.kickerSportsInfoSource = new KickerSportsInfoSource(frs);
    }


    @Test
    public void testGetAllKoChallenges() throws IOException {
        List<String[]> res = kickerSportsInfoSource.getAllKoChallenges();
        for (String[] sa : res) {
            System.out.println("---");
            for (String s : sa)
                System.out.println(s);
        }
    }

    @Test
    public void testGetMatchInfoForDay2() throws Throwable {
        List<MatchInfo> matchInfos = kickerSportsInfoSource.getMatchInfoForDay(new Date());
        for (MatchInfo mi : matchInfos) {
            System.out.println(mi);
            if (mi.getChallengeRankingUrl() != null) {
                Map<String, Integer> rankings = kickerSportsInfoSource.getTeamRankings(mi.getChallengeRankingUrl());
                for (String s : rankings.keySet()) {
                    System.out.println(rankings.get(s) + " " + s);
                }
            }
        }
    }

    @Test
    public void testGetTeamRankings() throws Throwable{
        kickerSportsInfoSource.getTeamRankings("http://www.kicker.de/news/fussball/bundesliga/spieltag/1-bundesliga/2018-19/18/0/spieltag.html");
    }
}
