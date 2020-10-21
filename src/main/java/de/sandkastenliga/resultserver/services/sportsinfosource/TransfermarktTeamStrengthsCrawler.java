package de.sandkastenliga.resultserver.services.sportsinfosource;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Component
public class TransfermarktTeamStrengthsCrawler {

    private static String BASE_URL = "https://www.transfermarkt.de/vereins-statistik/wertvollstemannschaften" +
            "/marktwertetop?kontinent_id=0&land_id=40&page={0}&plus=1";
    private final double FLATTEN_EXP = 1.8;

    public Map<String, Integer> getGermanTeamStrenghts() throws IOException {
        Map<String, Integer> res = new HashMap<>();
        int maxMarketValue = 0;
        final HttpClient client = HttpClientBuilder.create().build();
        for (int page = 1; page < 2; page++) {
            HttpGet request = new HttpGet(MessageFormat.format(BASE_URL, String.valueOf(page)));
            Document doc = Jsoup.parse(new URL(MessageFormat.format(BASE_URL, String.valueOf(page))), 10000);
            Element table = doc.getElementsByClass("items").first();
            Elements tableRows = table.getElementsByTag("tbody").first().getElementsByTag("tr");
            boolean first = true;
            for(Element row : tableRows){
                Elements columns = row.getElementsByTag("td");
                String teamName = columns.get(2).text();
                String valueInMioStr = columns.get(6).text();
                int valueInMio = Integer.parseInt(valueInMioStr.substring(0, valueInMioStr.indexOf(",")));
                res.put(teamName, valueInMio);
                if(page == 1 && first) {
                    maxMarketValue = valueInMio;
                    first = false;
                }
            }
        }
        for(String team : res.keySet()){
            double sqrtValue = Math.pow(res.get(team), 1 / FLATTEN_EXP);
            res.put(team, (int)(sqrtValue / Math.pow(maxMarketValue, 1 / FLATTEN_EXP) * 100d));
            System.out.println(team + "/ " + res.get(team));
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        new TransfermarktTeamStrengthsCrawler().getGermanTeamStrenghts();
    }

}
