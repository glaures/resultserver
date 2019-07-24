package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.model.MatchInfo;
import de.sandkastenliga.resultserver.model.MatchState;
import de.sandkastenliga.resultserver.services.sportsinfosource.SportsInfoSource;
import de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking.FifaRankingService;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class KickerSportsInfoSource implements SportsInfoSource {

    private static final String BASE_URL = "https://www.kicker.de";
    private static final String URL_PREFIX = "/fussball/matchkalender";
    private static final String URL_POSTFIX = "/1";
    private final Logger logger = LoggerFactory.getLogger(KickerSportsInfoSource.class);
    private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private FifaRankingService fifaRankingService;

    @Value("${resultserver.ko-challenges.file}")
    private String koChallengesFileName;

    @Autowired
    public KickerSportsInfoSource(FifaRankingService fifaRankingService) {
        this.fifaRankingService = fifaRankingService;
    }

    public List<String[]> getAllKoChallenges() throws IOException {
        List<String[]> res = new ArrayList<String[]>();
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(koChallengesFileName)));
        String line = null;
        while ((line = br.readLine()) != null) {
            StringTokenizer tok = new StringTokenizer(line, ",", false);
            String challenge = tok.nextToken();
            String region = tok.nextToken();
            res.add(new String[]{region, challenge});
        }
        return res;
    }

    public List<MatchInfo> getMatchInfoForDay(Date matchDate) throws IOException {
        String url = BASE_URL + URL_PREFIX + ("/" + this.dateFormat.format(matchDate)) + URL_POSTFIX;
        Document doc = Jsoup.parse(loadContentByHttpClient(url), "UTF-8", "");
        List<MatchInfo> res = getMatchInfoForDayFromDoc(matchDate, doc);
        logger.info("... done parsing " + url + ". Found " + res.size() + " matches.");
        return res;
    }

    public List<MatchInfo> getMatchInfoForDayFromDoc(Date matchDate, Document doc) throws IOException {
        List<MatchInfo> res = new ArrayList<MatchInfo>();
        Elements allGameLists = doc.getElementsByClass("kick__v100-gameList");
        for (Element gameList : allGameLists) {
            // Calendar startOfParsedGame = Calendar.getInstance();
            // startOfParsedGame.setTime(matchDate);
            // resetToStartOfDay(startOfParsedGame);
            MatchState matchState = MatchState.scheduled;
            // determine challenge
            Element gameListHeader = gameList.getElementsByClass("kick__v100-gameList__header").first();
            String challengeString = gameListHeader.text().trim();
            StringTokenizer tok = new StringTokenizer(challengeString.trim(), "(,)", false);
            String region = tok.nextToken().trim();
            String challenge = tok.nextToken().trim();
            String round = "0";
            String challengeRankingUrl = null;
            if (tok.hasMoreTokens()) {
                String roundStr = tok.nextToken();
                if (roundStr.contains(".") && roundStr.indexOf(".") < 3) {
                    round = roundStr.substring(0, roundStr.indexOf(".")).trim();
                    // if the round is set, there must be a ranking URL
                    Element flagLink = gameListHeader.select("a.kick__flag-link").first();
                    challengeRankingUrl = flagLink.attr("href").replace("spieltag", "tabelle");
                }
            }
            boolean koMode = false;
            // start parsing games
            Elements gameRows = gameList.getElementsByClass("kick__v100-gameList__gameRow");
            for (Element gameRow : gameRows) {
                String correlationId = getCorrelationIdFromGameRow(gameRow);
                Elements teamNames = gameRow.getElementsByClass("kick__v100-gameCell__team__name");
                String team1 = teamNames.get(0).text();
                String team2 = teamNames.get(1).text();
                // parse date if applicable
                Calendar matchDateCal = null;
                matchDateCal = Calendar.getInstance();
                matchDateCal.setTime(matchDate);
                resetToStartOfDay(matchDateCal);
                boolean isExactTime = false;
                Elements dateHolderElem = gameRow.getElementsByClass("kick__v100-scoreBoard__dateHolder");
                if (dateHolderElem.size() > 1) {
                    // second row carries time info
                    Date exactTime = parseDateFromResultFieldOnKIckerPage(dateHolderElem.get(0).text().trim(), dateHolderElem.get(1).text().trim(), matchDateCal);
                    if (exactTime != null) {
                        matchDateCal.setTime(exactTime);
                        matchState = MatchState.ready;
                        isExactTime = true;
                    }
                }
                // check if there is a result already
                int goalsTeam1 = -1;
                int goalsTeam2 = -1;
                Elements resultHolderElems = gameRow.getElementsByClass("kick__v100-scoreBoard__scoreHolder");
                if (resultHolderElems.size() > 0) {
                    Element resultHolderElem = resultHolderElems.first();
                    Elements scoreholderElems = resultHolderElem.select(".kick__v100-scoreBoard__scoreHolder__score");
                    if (scoreholderElems.size() == 2) {
                        String score1Str = scoreholderElems.get(0).text().trim();
                        String score2Str = scoreholderElems.get(1).text().trim();
                        if ("-".equals(score1Str)) {
                            matchState = MatchState.ready;
                        } else {
                            try {
                                goalsTeam1 = (NumberFormat.getIntegerInstance().parse(score1Str).intValue());
                                goalsTeam2 = (NumberFormat.getIntegerInstance().parse(score2Str).intValue());
                                if (resultHolderElem.getElementsByClass("kick__v100-scoreBoard__scoreHolder--live").size() > 0) {
                                    matchState = MatchState.running;
                                } else if (goalsTeam1 >= 0 && goalsTeam2 >= 0) {
                                    matchState = MatchState.finished;
                                }
                            } catch (ParseException pe) {
                                pe.printStackTrace();
                            }
                        }
                    }
                } else {
                    logger.debug("no resultholder element for match " + team1 + " - " + team2);
                }
                int i = 0;
                i++;
                MatchInfo mi = new MatchInfo();
                mi.setCorrelationId(correlationId);
                mi.setChallenge(challenge);
                mi.setRound(round);
                mi.setRegion(region);
                mi.setGoalsTeam1(goalsTeam1);
                mi.setGoalsTeam2(goalsTeam2);
                mi.setTeam1(team1);
                mi.setTeam2(team2);
                mi.setChallengeRankingUrl(challengeRankingUrl);
                mi.setExactTime(isExactTime);
                mi.setStart(matchDateCal != null ? matchDateCal.getTime() : null);
                mi.setState(matchState);
                res.add(mi);
                logger.info(mi.toString());
            }
        }
        return res;
    }

    private String getCorrelationIdFromGameRow(Element gameRow) {
        // if there is anchor of class kick__v100-scoreBoard, we can use its link
        Element anchor = gameRow.selectFirst("a.kick__v100-scoreBoard");
        String link = anchor.attr("href");
        if(link.startsWith(BASE_URL))
            link = link.substring(BASE_URL.length());
        // skip leading slash
        if(link.startsWith("/"))
            link = link.substring(1);
        return link.substring(0, link.indexOf("/"));
    }

    /**
     * Method will try to determine the date and time of the game
     * Returns null, if it was not possible and the former value will be reused
     */
    private Date parseDateFromResultFieldOnKIckerPage(String line1, String line2, Calendar requestedDayCal) {
        Calendar resultCal = Calendar.getInstance();
        resultCal.setTime(requestedDayCal.getTime());
        resetToStartOfDay(resultCal);
        if (isToday(requestedDayCal)) {
            if (line2.contains("heute") || line2.contains(":")) { // no result in line1 but the time
                setHourAndMinuteFromString(line1, resultCal);
                return resultCal.getTime();
            } else {
                return null; // do not change the time
            }
        } else if (isFuture(requestedDayCal)) {
            // line 2 contains the time
            setHourAndMinuteFromString(line2, resultCal);
            return resultCal.getTime();
        } else {
            // we are parsing a game from a past or future day
            // the field does not contain the date time anymore but the result
            // let's hope we know the game's time and result already call this
            // method with a past date
            return resultCal.getTime();
        }
    }

    private void setHourAndMinuteFromString(String str, Calendar cal) {
        final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        try {
            Date timeDate = timeFormat.parse(str);
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(timeDate);
            cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        } catch (Throwable t) {
            return;
        }
    }

    private void resetToStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private boolean isToday(Calendar day) {
        Calendar now = Calendar.getInstance();
        return day.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
                day.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    private boolean isFuture(Calendar day) {
        return day.getTime().after(new Date());
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
        logger.info("Parsing team ranking at " + urlStr);
        Map<String, Integer> res = new HashMap<String, Integer>();
        Document doc = Jsoup.connect(BASE_URL + urlStr).get();
        Element tableElem = doc.selectFirst("table.kick__table--ranking");
        if (tableElem == null)
            return res;
        int currentRank = 1;
        for (Element tableRow : tableElem.getElementsByTag("tr")) {
            int rank = currentRank;
            String team = null;
            for (Element rowCell : tableRow.getElementsByTag("td")) {
                if (rowCell.hasClass("kick__table--ranking__rank")) {
                    String rankStr = rowCell.text().trim();
                    if (rankStr.length() > 0) {
                        try {
                            rank = (NumberFormat.getIntegerInstance().parse(rankStr)).intValue();
                            currentRank = rank;
                        } catch (ParseException pe) {
                            logger.warn("parsinfg ranks at " + urlStr + " failed: " + pe.getMessage());
                        }
                    }
                } else if (rowCell.hasClass("kick__table--ranking__teamname")) {
                    team = removeSuffixes(rowCell.getElementsByClass("kick__table--show-desktop").first().text().trim());
                }
                if (team != null)
                    res.put(team, rank);
            }
        }
        return res;
    }

    private String removeSuffixes(String t) {
        if (t.endsWith("(P)") || t.endsWith("(M)") || t.endsWith("(N)") || t.endsWith("(A)") || t.endsWith("(M, P)")) {
            return t.substring(0, t.lastIndexOf("(")).trim();
        }
        return t;
    }

    public void setKoChallengesFileName(String koChallengesFileName) {
        this.koChallengesFileName = koChallengesFileName;
    }

    public static InputStream loadContentByHttpClient(String url)
            throws ClientProtocolException, IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        return response.getEntity().getContent();
    }


}
