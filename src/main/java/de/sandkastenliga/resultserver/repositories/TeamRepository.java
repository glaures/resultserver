package de.sandkastenliga.resultserver.repositories;

import de.sandkastenliga.resultserver.model.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {

    public List<Team> findTeamsByName(String name);

    @Query(value = "SELECT DISTINCT t.name FROM Team t JOIN Match m ON (m.team1=t OR m.team2=t) " +
            " WHERE m.challenge.region in :relevantRegions AND t.name LIKE :query")
    public List<String> fuzzyFindTeamByName(String query, String[] relevantRegions, Pageable page);

    @Query(value = "SELECT DISTINCT t FROM Team t JOIN Match m ON (m.team1=t OR m.team2=t) " +
            " WHERE m.challenge.id=:challengeId")
    List<Team> findAllByChallenge(int challengeId);
}
