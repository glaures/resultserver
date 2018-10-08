package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.resultserver.model.ChallengeMode;

import java.util.Date;

public class ChallengeDto {

    private Integer id;
    private String region;
    private String name;
    private String rankUrl;
    private Date rankUrlDate;
    private ChallengeMode challengeMode = ChallengeMode.points;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRankUrl() {
        return rankUrl;
    }

    public void setRankUrl(String rankUrl) {
        this.rankUrl = rankUrl;
    }

    public Date getRankUrlDate() {
        return rankUrlDate;
    }

    public void setRankUrlDate(Date rankUrlDate) {
        this.rankUrlDate = rankUrlDate;
    }

    public ChallengeMode getChallengeMode() {
        return challengeMode;
    }

    public void setChallengeMode(ChallengeMode challengeMode) {
        this.challengeMode = challengeMode;
    }
}
