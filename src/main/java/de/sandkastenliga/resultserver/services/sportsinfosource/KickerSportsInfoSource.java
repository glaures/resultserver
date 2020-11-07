package de.sandkastenliga.resultserver.services.sportsinfosource;

import de.sandkastenliga.resultserver.model.MatchInfo;
import de.sandkastenliga.resultserver.model.MatchState;
import de.sandkastenliga.resultserver.utils.DateUtils;
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
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class KickerSportsInfoSource {

    private static final String BASE_URL = "https://www.kicker.de";
    private static final String URL_PREFIX = "/fussball/matchkalender";
    private static final String URL_POSTFIX = "/1";
    private final Logger logger = LoggerFactory.getLogger(KickerSportsInfoSource.class);
    private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private RegionRelevanceProvider regionRelevanceProvider;

    @Autowired
    public KickerSportsInfoSource(RegionRelevanceProvider regionRelevanceProvider) {
        this.regionRelevanceProvider = regionRelevanceProvider;
    }

    @Value("${resultserver.ko-challenges.file}")
    private String koChallengesFileName;

    public List<String[]> getAllKoChallenges() throws IOException {
        List<String[]> res = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(koChallengesFileName)));
        String line = null;
        while ((line = br.readLine()) != null) {
            StringTokenizer tok = new StringTokenizer(line, ",", false);
            String challenge = tok.nextToken();
            String region = tok.nextToken();
            if (regionRelevanceProvider.isRelevantRegion(region))
                res.add(new String[]{region, challenge});
        }
        return res;
    }

    public List<MatchInfo> getMatchInfoForDay(Date matchDate) throws IOException {
        String url = BASE_URL + URL_PREFIX + ("/" + this.dateFormat.format(matchDate)) + URL_POSTFIX;
        Document doc = Jsoup.parse(loadContentByHttpClient(url), "UTF-8", "");
        List<MatchInfo> res = getMatchInfoForDayFromDoc(matchDate, doc);
        logger.debug("... done parsing " + url + ". Found " + res.size() + " matches.");
        return res;
    }

    public List<MatchInfo> getMatchInfoForDayFromDoc(Date matchDate, Document doc) {
        matchDate = DateUtils.resetToStartOfDay(matchDate);
        List<MatchInfo> res = new ArrayList<>();
        Elements allGameLists = doc.getElementsByClass("kick__v100-gameList");
        for (Element gameList : allGameLists) {
            // Calendar startOfParsedGame = Calendar.getInstance();
            // startOfParsedGame.setTime(matchDate);
            // resetToStartOfDay(startOfParsedGame);
            // determine challenge
            Element gameListHeader = gameList.getElementsByClass("kick__v100-gameList__header").first();
            String challengeString = gameListHeader.text().trim();
            StringTokenizer tok = new StringTokenizer(challengeString.trim(), "(,)", false);
            String region = tok.nextToken().trim();
            if (regionRelevanceProvider.isRelevantRegion(region)) {
                String challenge = tok.nextToken().trim();
                String round = "0";
                String challengeRankingUrl = null;
                if (tok.hasMoreTokens()) {
                    String roundStr = tok.nextToken().trim();
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
                    MatchState matchState = MatchState.scheduled;
                    String correlationId = getCorrelationIdFromGameRow(gameRow);
                    Elements teamNameElements = gameRow.getElementsByClass("kick__v100-gameCell__team__name");
                    String team1 = teamNameElements.get(0).text();
                    String team2 = teamNameElements.get(1).text();
                    Elements teamGameCells = gameRow.getElementsByClass("kick__v100-gameCell__team");
                    String team1Id = findTeamIdInTeamGameCell(teamGameCells.get(0), team1);
                    String team2Id = findTeamIdInTeamGameCell(teamGameCells.get(1), team2);
                    // parse date if applicable
                    Calendar matchDateCal = null;
                    matchDateCal = Calendar.getInstance();
                    matchDateCal.setTime(matchDate);
                    resetToStartOfDay(matchDateCal);
                    boolean isExactTime = false;
                    Elements dateHolderElem = gameRow.getElementsByClass("kick__v100-scoreBoard__dateHolder");
                    // test is match has been canceled
                    String dateHolderElemText = dateHolderElem.text();
                    if (dateHolderElemText.contains("abges")) {
                        matchState = MatchState.canceled;
                    } else {
                        if (dateHolderElem.size() > 1) {
                            // second row carries time info
                            Date exactTime = parseDateFromResultFieldOnKIckerPage(dateHolderElem.get(0).text().trim(), dateHolderElem.get(1).text().trim(), matchDateCal);
                            if (exactTime != null) {
                                matchDateCal.setTime(exactTime);
                                matchState = MatchState.ready;
                                isExactTime = true;
                            }
                        }
                    }
                    // check if there is a result already
                    int goalsTeam1 = -1;
                    int goalsTeam2 = -1;
                    Elements resultHolderElems = gameRow.getElementsByClass("kick__v100-scoreBoard__scoreHolder");
                    if (resultHolderElems.size() > 0) {
                        Element resultHolderElem = resultHolderElems.first();
                        Elements scoreholderElems = resultHolderElem.select(".kick__v100-scoreBoard__scoreHolder__score");
                        if (resultHolderElem.hasClass("kick__v100-scoreBoard__scoreHolder--prelive")) {
                            // countdown 1h vor dem Spiel --> ready
                            matchState = MatchState.ready;
                        } else if (scoreholderElems.size() == 2) {
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
                    mi.setTeam1Id(team1Id);
                    mi.setTeam1(team1);
                    mi.setTeam2Id(team2Id);
                    mi.setTeam2(team2);
                    mi.setChallengeRankingUrl(challengeRankingUrl);
                    mi.setExactTime(isExactTime);
                    mi.setStart(matchDateCal != null ? matchDateCal.getTime() : null);
                    mi.setState(matchState);
                    res.add(mi);
                    logger.debug(mi.toString());
                }
            }
        }
        return res;
    }

    private String getCorrelationIdFromGameRow(Element gameRow) {
        // if there is anchor of class kick__v100-scoreBoard, we can use its link
        Element anchor = gameRow.selectFirst("a.kick__v100-scoreBoard");
        String link = anchor.attr("href");
        if (link.startsWith(BASE_URL))
            link = link.substring(BASE_URL.length());
        // skip leading slash
        if (link.startsWith("/"))
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

    public Map<String, Integer[]> getTeamRankings(String urlStr) throws IOException {
        logger.debug("Parsing team ranking at " + urlStr);
        Map<String, Integer[]> res = new HashMap<String, Integer[]>();
        Document doc = Jsoup.connect(BASE_URL + urlStr).get();
        Elements tableElems = doc.getElementsByClass("kick__table");
        if (tableElems.size() == 0)
            return res;
        int currentRank = 1;
        Element tableElem = tableElems.first();
        for (Element tableRow : tableElem.getElementsByTag("tr")) {
            int rank = currentRank;
            int points = 0;
            String teamId = null;
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
                    String teamName = removeSuffixes(rowCell.text());
                    teamId = findTeamIdInTeamGameCell(rowCell, teamName);
                } else if(rowCell.hasClass("kick__table--ranking__master kick__respt-m-o-5")){
                    points = Integer.parseInt(rowCell.text());
                }
                if (teamId != null)
                    res.put(teamId, new Integer[]{rank, points});
            }
        }
        return res;
    }

    private String findTeamIdInTeamGameCell(Element teamGameCell, String teamName) {
        Elements anchors = teamGameCell.getElementsByTag("a");
        if (anchors.size() == 1) {
            String link = anchors.get(0).attr("href");
            // the id is the number before the second slash
            StringTokenizer tok = new StringTokenizer(link, "/", false);
            if (tok.hasMoreTokens()) {
                return tok.nextToken();
            }
        }
        String res = teamName.replace(" ", "-");
        return res.toLowerCase();
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
