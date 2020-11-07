package de.sandkastenliga.resultserver.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "challenge")
@Data
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Challenge {

    @Id
    @GeneratedValue
    private Integer id;
    private String region;
    private String name;
    private int level = 10;
    @Column(name = "rankUrl")
    private String rankUrl;
    @Column(name = "rankUrlDate")
    @Temporal(TemporalType.DATE)
    private Date rankUrlDate;
    @Column(name = "challengeMode")
    private ChallengeMode challengeMode = ChallengeMode.points;

}
