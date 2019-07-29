package de.sandkastenliga.resultserver.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "rmatch")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Match {

    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private Challenge challenge;
    private String round;
    @ManyToOne
    private Team team1;
    @ManyToOne
    private Team team2;
    private Date start;
    private MatchState state = MatchState.scheduled;
    private int goalsTeam1 = -1;
    private int goalsTeam2 = -1;
    private int posTeam1 = -1;
    private int posTeam2 = -1;
    private int tendence = -1;
    private int strengthTeam1 = 1;
    private int strengthTeam2 = 1;
    private boolean featured = false;
    @NotNull
    private String correlationId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Fetch(FetchMode.JOIN)
    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getStart() {
        return start;
    }

    public void setStart(Date date) {
        this.start = date;
    }

    public MatchState getState() {
        return state;
    }

    public void setState(MatchState state) {
        this.state = state;
        updateTendence();
    }

    public int getGoalsTeam1() {
        return goalsTeam1;
    }

    public void setGoalsTeam1(int goalsTeam1) {
        this.goalsTeam1 = goalsTeam1;
        updateTendence();
    }

    public int getGoalsTeam2() {
        return goalsTeam2;
    }

    public void setGoalsTeam2(int goalsTeam2) {
        this.goalsTeam2 = goalsTeam2;
        updateTendence();
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

    public int getTendence() {
        return tendence;
    }

    public boolean getFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
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

    @Column(unique = true)
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public ChallengeMode getChallengeMode(){
        return this.challenge.getChallengeMode();
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    private void updateTendence() {
        if (this.state != MatchState.running && this.state != MatchState.finished) {
            tendence = -1;
        } else {
            tendence = goalsTeam1 == goalsTeam2 ? 0 : (goalsTeam1 > goalsTeam2 ? 1 : 2);
        }
    }

    @Transient
    public void setPos(Team t, Integer pos) {
        if (t.equals(team1))
            setPosTeam1(pos);
        else if (t.equals(team2))
            setPosTeam2(pos);
    }
}
