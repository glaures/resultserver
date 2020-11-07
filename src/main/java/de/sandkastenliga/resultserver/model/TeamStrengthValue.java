package de.sandkastenliga.resultserver.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class TeamStrengthValue {

    @Id
    @GeneratedValue
    long id;
    @ManyToOne
    TeamStrengthSnapshot snapshot;
    float value = 1f;
    @ManyToOne
    Team team;
}
