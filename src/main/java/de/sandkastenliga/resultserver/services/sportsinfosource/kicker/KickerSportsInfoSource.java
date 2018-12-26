package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.dtos.MatchInfo;
import de.sandkastenliga.resultserver.model.MatchState;
import de.sandkastenliga.resultserver.services.sportsinfosource.SportsInfoSource;
import de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking.FifaRankingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class KickerSportsInfoSource implements SportsInfoSource {

    private static final String BASE_URL = "http://www.kicker.de";
    private static final String KO_URL = "http://www.kicker.de/news/fussball/intligen/intpokale/internationale-pokale.html";
    private static final String URL_PREFIX = "http://www.kicker.de/news/live-news/matchkalender/";
    private static final String URL_POSTFIX = "/1/matchkalender_fussball.html";
    private static final Log log = LogFactory.getLog(KickerSportsInfoSource.class);
    private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private FifaRankingService fifaRankingService;

    @Autowired
    public KickerSportsInfoSource(FifaRankingService fifaRankingService) {
        this.fifaRankingService = fifaRankingService;
    }

    public List<String[]> getAllKoChallenges() throws IOException {
        List<String[]> res = new ArrayList<String[]>();
        Document doc = Jsoup.connect(KO_URL).get();
        Element table = doc.getElementsByTag("table").get(0);
        boolean first = true;
        String currRegion = null;
        for (Element row : table.getElementsByTag("tr")) {
            if (first)
                first = false;
            else {
                if (!row.hasClass("tr_sep")) {
                    Elements cells = row.getElementsByTag("td");
                    String challenge = cells.get(1).text().trim();
                    String region = cells.get(0).text().trim();
                    if (region == null || "".equals(region)) {
                        region = currRegion;
                    }
                    String[] arr = new String[]{region, challenge};
                    res.add(arr);
                    currRegion = region;
                }
            }
        }
        // special treatment for DFB Pokal as might not be listed
        boolean dfbFound = false;
        for (String[] rc : res) {
            if (rc[1].equals("DFB-Pokal")) {
                dfbFound = true;
                break;
            }
        }
        if (!dfbFound)
            res.add(new String[]{"Deutschland", "DFB-Pokal"});
        return res;
    }

    public List<MatchInfo> getMatchInfoForDay(Date date) throws IOException {
        List<MatchInfo> res = new ArrayList<MatchInfo>();
        String url = URL_PREFIX + this.dateFormat.format(date) + URL_POSTFIX;
        log.debug("parsing " + url + "...");
        Document doc = Jsoup.connect(url).get();
        Elements allH3 = doc.getElementsByTag("h3");
        for (Element h3 : allH3) {
            if (h3.hasClass("thead580") && h3.hasClass("thead580_2")) {
                String challengeString = h3.text();
                StringTokenizer tok = new StringTokenizer(challengeString.trim(), "(,)", false);
                String region = tok.nextToken().trim();
                String challenge = tok.nextToken().trim();
                String round = "0";
                if (tok.hasMoreTokens())
                    round = tok.nextToken().trim();
                String challengeRankingUrl = getChallengeRankingUrl(h3);
                boolean koMode = false;
                Element sib = h3.nextElementSibling();
                /*
                while (!(sib instanceof HtmlElement)) {
                    sib = sib.getNextSibling();
                }
                */
                Element table = sib.getElementsByTag("table").get(0);
                Calendar currentDate = Calendar.getInstance();
                currentDate.setTime(date);
                currentDate.set(Calendar.HOUR_OF_DAY, 0);
                currentDate.set(Calendar.MINUTE, 0);
                currentDate.set(Calendar.SECOND, 0);
                currentDate.set(Calendar.MILLISECOND, 0);
                Date initialTimeOfDay = currentDate.getTime();
                boolean determined = true;
                Elements allRows = table.getElementsByTag("tr");
                for (Element row : allRows) {
                    if (!row.hasClass("tr_sep") && row.getElementsByTag("td").size() > 0) {
                        String team1 = "";
                        String team2 = "";
                        int goalsTeam1 = -1;
                        int goalsTeam2 = -1;
                        Date startTime;
                        MatchState matchState = MatchState.scheduled;
                        // start time
                        Elements cells = row.getElementsByTag("td");
                        String time = cells.get(0).text().trim();
                        if (!time.equals("")) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            cal.set(Calendar.HOUR_OF_DAY,
                                    Integer.parseInt(time.substring(0, time.indexOf(":"))));
                            cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);
                            currentDate.setTime(cal.getTime());
                            determined = true;
                        } else {
                            if (allRows.indexOf(row) == 1)
                                determined = false;
                        }
                        startTime = currentDate.getTime();
                        team1 = cells.get(1).text().trim();
                        team2 = cells.get(3).text().trim();
                        int[] teamScores = parseTeamScores(cells.get(4).html(), currentDate.getTime(),
                                koMode, determined);
                        goalsTeam1 = teamScores[0];
                        goalsTeam2 = teamScores[1];
                        String stateTxt = cells.get(5).text();
                        if (isCanceled(stateTxt)) {
                            matchState = MatchState.canceled;
                        } else if (isPostponed(stateTxt)) {
                            matchState = MatchState.postponed;
                        } else {
                            matchState = MatchState.values()[teamScores[2]];
                            if (matchState == MatchState.finished) {
                                // check if time is at least 20 minutes
                                // over official end
                                Calendar over = Calendar.getInstance();
                                over.setTime(startTime);
                                over.add(Calendar.MINUTE, 90 + 15 + 20);
                                if (new Date().before(over.getTime()) || startTime.equals(initialTimeOfDay)) {
                                    matchState = MatchState.running;
                                }
                            }
                        }
                        System.out.println(DateFormat.getDateTimeInstance().format(startTime) + " \t" + region
                                + "/" + challenge + " \t" + team1 + "-" + team2 + " " + goalsTeam1 + ":"
                                + goalsTeam2 + " (" + matchState + ")");
                        MatchInfo mi = new MatchInfo();
                        mi.setChallenge(challenge);
                        mi.setRound(round);
                        mi.setRegion(region);
                        mi.setGoalsTeam1(goalsTeam1);
                        mi.setGoalsTeam2(goalsTeam2);
                        mi.setTeam1(team1);
                        mi.setTeam2(team2);
                        mi.setChallengeRankingUrl(challengeRankingUrl);
                        mi.setStart(startTime);
                        mi.setState(matchState);
                        res.add(mi);
                    }
                }
            }
        }
        return res;
    }

    private String getChallengeRankingUrl(Element h3) {
        for (Element e : h3.getElementsByTag("a")) {
            String text = e.text().trim();
            if (text.equals("Tabelle"))
                return BASE_URL + e.attr("href");
        }
        return null;
    }


    private int[] parseTeamScores(String xml, Date matchStart, boolean koMode, boolean determined) {
        int[] res = {-1, -1, MatchState.scheduled.ordinal()};
        try {
            boolean penalty = xml.contains("i.E.");
            boolean overtime = xml.contains("n.V.");
            int colonPos = 0;
            if (!penalty) {
                // take the first colon with numbers around
                boolean finished = false;
                while (colonPos >= 0 && !finished) {
                    int nextColonOffset = xml.substring(colonPos + 1).indexOf(":");
                    if (nextColonOffset == -1) {
                        colonPos = -1;
                    } else {
                        if (Character.isDigit(xml.charAt((colonPos + 1) + nextColonOffset - 1))) {
                            finished = true;
                        }
                        colonPos = (colonPos + 1) + nextColonOffset;
                    }
                }
            } else {
                // take the last colon
                colonPos = xml.lastIndexOf(":");
            }
            if (colonPos <= 0) {
                res[2] = determined ? MatchState.ready.ordinal() : MatchState.scheduled.ordinal();
                return res;
            }
            int firstDigitPos = findFirstDigitFromColonPos(xml, colonPos);
            res[0] = Integer.parseInt(xml.substring(firstDigitPos, colonPos));
            int lastDigitPos = colonPos + 1;
            while (lastDigitPos + 1 < xml.length() && Character.isDigit(xml.charAt(lastDigitPos + 1))) {
                lastDigitPos++;
            }
            res[1] = Integer.parseInt(xml.substring(colonPos + 1, lastDigitPos + 1));
            boolean isRunning = isRunning(xml, matchStart, koMode, res);
            if (isRunning || (res[0] == res[1] && overtime)) {
                res[2] = MatchState.running.ordinal();
            } else if (res[0] >= 0) {
                res[2] = MatchState.finished.ordinal();
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return res;
    }

    public boolean isRunning(String score, Date matchStart, boolean koChallenge, int[] res) {
        if (res[0] == res[1] && koChallenge)
            return true;
        if (score.contains("font")) {
            return true;
        }
        if (score.contains("-")) {
            for (char c : score.toCharArray()) {
                if (Character.isDigit(c)) {
                    return true;
                }
            }
        }
        // no red scores, no '-' in score
        Calendar matchFinish = Calendar.getInstance();
        matchFinish.setTime(matchStart);
        matchFinish.add(Calendar.MINUTE, 90 + 15);
        if (koChallenge) {
            matchFinish.add(Calendar.MINUTE, 15);
        }
        Date now = new Date();
        if (matchStart.before(now) && now.before(matchFinish.getTime())) {
            return true;
        }
        return false;
    }

    private int findFirstDigitFromColonPos(String text, int colonPos) {
        int res = colonPos;
        do {
            res--;
        } while (res >= 0 && Character.isDigit(text.charAt(res)));
        return res + 1;
    }

    private boolean isCanceled(String s) {
        return "abgesagt".equalsIgnoreCase(s) || "annulliert".equalsIgnoreCase(s) || "abgebrochen".equalsIgnoreCase(s);
    }

    private boolean isPostponed(String s) {
        return "verlegt".equalsIgnoreCase(s);
    }

    public Map<String, Integer> getTeamRankings(String urlStr) throws IOException {
        log.info("Parsing team ranking at " + urlStr);
        Map<String, Integer> res = new HashMap<String, Integer>();
        Document doc = Jsoup.connect(urlStr).get();
        Elements tables = doc.getElementsByTag("table");
        for (Element table : tables) {
            if (table.hasAttr("summary") && table.attr("summary").equals("Tabelle")) {
                int currPos = 1;
                Elements rows = table.getElementsByTag("tr");
                for (int idx = 1; idx < rows.size(); idx++) {
                    Element row = rows.get(idx);
                    String css = row.attr("class");
                    if (css != null) {
                        css = css.trim().toLowerCase();
                        String id = row.id().toLowerCase();
                        if ((css.contains("alt") || "".equals(css) || css.contains("tablinie"))
                                && !id.contains("placeholder") && !"tr_septablinie".equals(css)) {
                            Elements cells = row.getElementsByTag("td");
                            int pos = currPos;
                            String posStr = cells.get(0).text().trim();
                            if (!"".equals(posStr)) {
                                pos = Integer.parseInt(posStr);
                                currPos = pos;
                            }
                            String team = cells.get(2).text().trim();
                            team = removeSuffixes(team);
                            res.put(team, pos);
                        }
                    }
                }
            }
        }
        // add the Fifa ranking
        List<String> fifaRanking = fifaRankingService.getRanking();
        int rank = 1;
        for (String t : fifaRanking) {
            res.put(t, rank++);
        }
        return res;
    }

    private String removeSuffixes(String t) {
        if (t.endsWith("(P)") || t.endsWith("(M)") || t.endsWith("(N)") || t.endsWith("(A)") || t.endsWith("(M, P)")) {
            return t.substring(0, t.lastIndexOf("(")).trim();
        }
        return t;
    }

}
