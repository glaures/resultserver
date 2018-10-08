package de.sandkastenliga.resultserver.repositories;

import de.sandkastenliga.resultserver.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    public Challenge findChallengeByRegionAndName(String region, String name);
}
