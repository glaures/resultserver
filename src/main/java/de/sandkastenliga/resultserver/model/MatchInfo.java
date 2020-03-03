package de.sandkastenliga.resultserver.model;

import java.text.DateFormat;
import java.util.Date;

public class MatchInfo {

    private Date start;
    private MatchState state;
    private String team1Id;
    private String team1;
    private String team2Id;
    private String team2;
    private String challenge;
    private String challengeRankingUrl;
    private String region;
    private String round;
    private int goalsTeam1;
    private int goalsTeam2;
    private String correlationId;
    private boolean exactTime;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public MatchState getState() {
        return state;
    }

    public void setState(MatchState state) {
        this.state = state;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getGoalsTeam1() {
        return goalsTeam1;
    }

    public void setGoalsTeam1(int goalsTeam1) {
        this.goalsTeam1 = goalsTeam1;
    }

    public int getGoalsTeam2() {
        return goalsTeam2;
    }

    public void setGoalsTeam2(int goalsTeam2) {
        this.goalsTeam2 = goalsTeam2;
    }

    public String getChallengeRankingUrl() {
        return challengeRankingUrl;
    }

    public void setChallengeRankingUrl(String challengeRankingUrl) {
        this.challengeRankingUrl = challengeRankingUrl;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public boolean isExactTime() {
        return exactTime;
    }

    public void setExactTime(boolean exactTime) {
        this.exactTime = exactTime;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(String team1Id) {
        this.team1Id = team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(String team2Id) {
        this.team2Id = team2Id;
    }

    @Override
    public String toString() {
        return (start != null ? DateFormat.getDateTimeInstance().format(start.getTime()) : "?")
                + " \t"
                + region
                + "/" + challenge + " \t[" + team1Id + "]" + team1 + "-" + " [" + team2Id + "]" + team2 + " " + goalsTeam1 + ":"
                + goalsTeam2 + " (" + state + ") Round: " + round + ", URL:" + challengeRankingUrl;
    }

}
