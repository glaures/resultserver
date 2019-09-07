package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.model.MatchInfo;
import de.sandkastenliga.resultserver.model.MatchState;
import de.sandkastenliga.resultserver.services.sportsinfosource.KickerSportsInfoSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public abstract class AbstractTestCase {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final NumberFormat numberFormat = new DecimalFormat("00");
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private KickerSportsInfoSource kickerSportsInfoSource;

    @Before
    public void setup() throws IOException {
        this.kickerSportsInfoSource = new KickerSportsInfoSource();
    }

    public abstract int getYear();

    public abstract int getMonth();

    public abstract int getDate();

    public abstract int getMatchCount();

    public abstract List<MatchInfoStruct> getMatchInfoStructs();

    @Test
    public void testGetMatchInfoForDay() throws Throwable {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getYear());
        cal.set(Calendar.MONTH, getMonth() - 1);
        cal.set(Calendar.DATE, getDate());
        Resource resource = new ClassPathResource(getTestFilename());
        InputStream is = resource.getInputStream();
        Document doc = Jsoup.parse(is, "UTF-8", "");
        List<MatchInfo> res = kickerSportsInfoSource.getMatchInfoForDayFromDoc(cal.getTime(), doc);
        checkResultList(res);
    }

    protected String getTestFilename(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getYear());
        cal.set(Calendar.MONTH, getMonth() - 1);
        cal.set(Calendar.DATE, getDate());
        return getYear() + "-" + numberFormat.format(getMonth()) + "-" + numberFormat.format(getDate()) + ".htm";
    }

    private void checkResultList(List<MatchInfo> matchList) {
        Assert.assertEquals(getMatchCount(), matchList.size());
        // TODO check challenge count
        for (MatchInfoStruct mis : getMatchInfoStructs()) {
            logger.info(mis.toString());
            find(matchList, mis.region, mis.challenge, mis.team1, mis.team2, mis.state, mis.goalsTeam1, mis.goalsTeam2, mis.dateString, mis.timeString);
        }
    }

    private void find(List<MatchInfo> macthList, String region, String challenge, String team1, String team2, MatchState state, int goalsTeam1, int goalsTeam2, String dateString, String timeString) {
        for (MatchInfo mi : macthList) {
            if (region == null || region.equalsIgnoreCase((mi.getRegion()))) {
                if (challenge == null || challenge.equalsIgnoreCase(mi.getChallenge())) {
                    if (team1.equalsIgnoreCase(mi.getTeam1()) && team2.equalsIgnoreCase(mi.getTeam2())) {
                        Assert.assertEquals("result is wrong " + mi, goalsTeam1, mi.getGoalsTeam1());
                        Assert.assertEquals("result is wrong " + mi, goalsTeam2, mi.getGoalsTeam2());
                        Assert.assertEquals(state, mi.getState());
                        if (timeString != null) {
                            Assert.assertEquals(timeString, timeFormat.format(mi.getStart()));
                        }
                        return;
                    }
                }
            }
        }
        Assert.fail("not found: " + challenge + "\t" + region + "\t" + team1 + "-" + team2 + "\t" + state + "\t" + goalsTeam1 + ":" + goalsTeam2);
    }
}
