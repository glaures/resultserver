package de.sandkastenliga.resultserver.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class TeamStrengthSnapshot {

    @Id
    @GeneratedValue
    int id;
    @Temporal(TemporalType.TIMESTAMP)
    Date snapshotDate;
    @ManyToOne
    Challenge challenge;

}
