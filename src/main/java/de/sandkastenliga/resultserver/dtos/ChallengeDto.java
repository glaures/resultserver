package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.resultserver.model.Challenge;
import lombok.Data;

import java.util.Date;

@Data
public class ChallengeDto {

    private Integer id;
    private String region;
    private String name;
    private String rankUrl;
    private Date rankUrlDate;
    private int level = 1;
    private int challengeModeEnum;

    public ChallengeDto(){}

    public ChallengeDto(Challenge c){
        this.id = c.getId();
        this.level = c.getLevel();
        this.region = c.getRegion();
        this.name = c.getName();
        this.rankUrl = c.getRankUrl();
        this.rankUrlDate = c.getRankUrlDate();
        this.challengeModeEnum = c.getChallengeMode().getIntValue();

    }
}
