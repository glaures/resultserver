package de.sandkastenliga.resultserver.repositories;

import de.sandkastenliga.resultserver.model.TeamStrengthSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamStrengthSnapshotRepository extends JpaRepository<TeamStrengthSnapshot, Integer> {

}
