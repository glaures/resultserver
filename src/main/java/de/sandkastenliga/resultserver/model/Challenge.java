package de.sandkastenliga.resultserver.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "challenge")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Challenge {

    @Id
    @GeneratedValue
    private Integer id;
    private String region;
    private String name;
    @Column(name = "rankUrl")
    private String rankUrl;
    @Column(name = "rankUrlDate")
    private Date rankUrlDate;
    @Column(name = "challengeMode")
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

    public ChallengeMode getChallengeMode() {
        return challengeMode;
    }

    public void setChallengeMode(ChallengeMode challengeMode) {
        this.challengeMode = challengeMode;
    }

    public String getRankUrl() {
        return rankUrl;
    }

    public void setRankUrl(String rankUrl) {
        this.rankUrl = rankUrl;
    }

    @Temporal(TemporalType.DATE)
    public Date getRankUrlDate() {
        return rankUrlDate;
    }

    public void setRankUrlDate(Date rankUrlDate) {
        this.rankUrlDate = rankUrlDate;
    }

}
