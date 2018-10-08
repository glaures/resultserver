package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.tools.projector.core.Projection;
import de.sandkastenliga.tools.projector.core.ProjectionType;

public class TeamDto {

    private String name;
    private Integer mainChallengeId;
    private String mainChallengeName;
    private int mainChallengePos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMainChallengeId() {
        return mainChallengeId;
    }

    @Projection(value = ProjectionType.property, propertyName = "mainChallenge")
    public void setMainChallengeId(Integer mainChallengeId) {
        this.mainChallengeId = mainChallengeId;
    }

    public String getMainChallengeName() {
        return mainChallengeName;
    }

    @Projection(value = ProjectionType.property, propertyName = "mainChallenge", referencePropertyName = "name")
    public void setMainChallengeName(String mainChallengeName) {
        this.mainChallengeName = mainChallengeName;
    }

    public int getMainChallengePos() {
        return mainChallengePos;
    }

    public void setMainChallengePos(int mainChallengePos) {
        this.mainChallengePos = mainChallengePos;
    }
}
