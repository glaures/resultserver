package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.tools.projector.core.Projection;
import de.sandkastenliga.tools.projector.core.ProjectionType;

import java.util.Date;

public class MatchDto {

    private int id;
    private Date start;
    private String team1;
    private String team2;
    private String challenge;
    private boolean ko;
    private String region;
    private String round;
    private int goalsTeam1;
    private int goalsTeam2;
    private int posTeam1;
    private int posTeam2;
    private int strengthTeam1;
    private int strengthTeam2;
    private int matchStateEnum;
    private boolean featured;
    private int challengeModeEnum;
    private Date lastUpdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getTeam1() {
        return team1;
    }

    @Projection(value = ProjectionType.property, referencePropertyName = "name")
    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    @Projection(value = ProjectionType.property, referencePropertyName = "name")
    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getChallenge() {
        return challenge;
    }

    @Projection(value = ProjectionType.property, propertyName = "challenge", referencePropertyName = "name")
    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getRegion() {
        return region;
    }

    @Projection(value = ProjectionType.property, propertyName = "challenge", referencePropertyName = "region")
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

    public boolean isKo() {
        return this.challengeModeEnum == 1;
    }

    public int getPosTeam1() {
        return posTeam1;
    }

    public void setPosTeam1(int posTeam1) {
        this.posTeam1 = posTeam1;
    }

    public int getPosTeam2() {
        return posTeam2;
    }

    public void setPosTeam2(int posTeam2) {
        this.posTeam2 = posTeam2;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getChallengeModeEnum() {
        return challengeModeEnum;
    }

    @Projection(value = ProjectionType.property, propertyName = "challengeMode", referencePropertyName = "intValue")
    public void setChallengeModeEnum(int challengeMode) {
        this.challengeModeEnum = challengeMode;
    }

    @Projection(value = ProjectionType.none)
    public void setKo(boolean ko) {
        this.ko = ko;
    }

    /*
    @Projection(value = ProjectionType.asIs, propertyName = "state")
    public void setMatchState(MatchState matchState) {
        this.matchStateEnum = matchState.getIntValue();
    }

    @JsonIgnore
    public MatchState getMatchState() {
        return MatchState.values()[matchStateEnum];
    }
    */

    public int getMatchStateEnum() {
        return matchStateEnum;
    }

    public int getStrengthTeam1() {
        return strengthTeam1;
    }

    public void setStrengthTeam1(int strengthTeam1) {
        this.strengthTeam1 = strengthTeam1;
    }

    public int getStrengthTeam2() {
        return strengthTeam2;
    }

    public void setStrengthTeam2(int strengthTeam2) {
        this.strengthTeam2 = strengthTeam2;
    }

    @Projection(value = ProjectionType.property, propertyName = "state", referencePropertyName = "intValue")
    public void setMatchStateEnum(int matchStateEnum) {
        this.matchStateEnum = matchStateEnum;
    }
}
