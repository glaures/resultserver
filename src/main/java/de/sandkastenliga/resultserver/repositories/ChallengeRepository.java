package de.sandkastenliga.resultserver.repositories;

import de.sandkastenliga.resultserver.dtos.RankDto;
import de.sandkastenliga.resultserver.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    public Challenge findChallengeByRegionAndName(String region, String name);

    @Query("SELECT new de.sandkastenliga.resultserver.dtos.RankDto(r) "
            + "FROM Rank r "
            + "WHERE r.challenge.id = :challengeId "
            + "AND r.round = :round "
            + "AND r.year = :year "
            + "ORDER BY r.rank ASC")
    public List<RankDto> getChallengeRanking(@Param("challengeId") int challengeId, @Param("round") int round,
                                             @Param("year") int year);



}
