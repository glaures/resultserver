package de.sandkastenliga.resultserver.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.MatchState;
import lombok.Data;

import java.util.Date;

@Data
public class MatchDto {

    private int id;
    private Date start;
    private String team1;
    private String team2;
    private String team1EmblemUrl;
    private String team2EmblemUrl;
    private String challenge;
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

    public MatchDto() {
    }

    public MatchDto(Match m) {
        this.id = m.getId();
        this.challenge = m.getChallenge().getName();
        this.challengeModeEnum = m.getChallengeMode().getIntValue();
        this.featured = m.isFeatured();
        this.goalsTeam1 = m.getGoalsTeam1();
        this.goalsTeam2 = m.getGoalsTeam2();
        this.lastUpdated = m.getLastUpdated();
        this.matchStateEnum = m.getState().getIntValue();
        this.posTeam1 = m.getPosTeam1();
        this.posTeam2 = m.getPosTeam2();
        this.region = m.getChallenge().getRegion();
        this.round = m.getRound();
        this.start = m.getStart();
        this.team1 = m.getTeam1().getName();
        this.team2 = m.getTeam2().getName();
        this.strengthTeam1 = m.getStrengthTeam1();
        this.strengthTeam2 = m.getStrengthTeam2();
    }

    public boolean isKo() {
        return this.challengeModeEnum == 1;
    }

    @JsonIgnore
    public MatchState getMatchState() {
        return MatchState.values()[matchStateEnum];
    }

}
