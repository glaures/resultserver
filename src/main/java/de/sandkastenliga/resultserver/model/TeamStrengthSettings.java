package de.sandkastenliga.resultserver.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class TeamStrengthSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @ManyToOne
    Challenge challenge;
    int maxStrength = 100;
    int minStrength;
    int flatFactor;
}
