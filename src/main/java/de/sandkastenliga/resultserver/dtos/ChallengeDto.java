package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.tools.projector.core.Projection;
import de.sandkastenliga.tools.projector.core.ProjectionType;

import java.util.Date;

public class ChallengeDto {

    private Integer id;
    private String region;
    private String name;
    private String rankUrl;
    private Date rankUrlDate;
    private int challengeModeEnum;

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

    public int getChallengeModeEnum() {
        return challengeModeEnum;
    }

    @Projection(value = ProjectionType.property, propertyName = "challengeMode", referencePropertyName = "intValue")
    public void setChallengeMode(int challengeMode) {
        this.challengeModeEnum = challengeMode;
    }
}
