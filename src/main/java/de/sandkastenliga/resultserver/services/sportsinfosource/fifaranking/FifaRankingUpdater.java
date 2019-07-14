package de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FifaRankingUpdater {

    private final static String url = "http://de.fifa.com/worldranking/rankingtable/index.html";
    private Log log = LogFactory.getLog(FifaRankingUpdater.class);

    public static void main(String[] args) throws IOException {
        List<String> ranking = new FifaRankingUpdater().getUpdatedRanking();
        int r = 1;
        for (String s : ranking) {
            System.out.println((r++) + ". " + s);
        }
    }

    public List<String> getUpdatedRanking() throws IOException {
        log.info("retrieving Fifa ranking...");
        List<String> res = new ArrayList<String>();
        log.info("... skipped retrieving Fifa ranking.");
        return res;
        /*
        Document doc = Jsoup.connect(url).get();
        Elements tds = doc.select("td.tbl-teamname");
        for (Element e : tds) {
            res.add(e.text());
        }
        log.info("... done retrieving Fifa ranking.");
        return res;
        */
    }

}
