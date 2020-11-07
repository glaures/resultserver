package de.sandkastenliga.resultserver.repositories;

import de.sandkastenliga.resultserver.dtos.ChallengeDto;
import de.sandkastenliga.resultserver.dtos.RankDto;
import de.sandkastenliga.resultserver.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    public Challenge findChallengeByRegionAndName(String region, String name);

    @Query("SELECT new de.sandkastenliga.resultserver.dtos.ChallengeDto(c) "
            + "FROM Challenge c "
            + "WHERE c.region = :region "
            + "ORDER by c.level ASC")
    public List<ChallengeDto> getAllChallengesByRegion(@Param("region") String region);

    @Query("SELECT new de.sandkastenliga.resultserver.dtos.RankDto(r.rank, r.team.id, r.team.name, r.points) "
            + "FROM Rank r "
            + "WHERE r.challenge.name like :challenge "
            + "AND r.challenge.region like :region "
            + "AND r.round = :round "
            + "AND r.year = :year "
            + "ORDER BY r.rank ASC")
    public List<RankDto> getChallengeRanking(@Param("challenge") String challenge,
                                             @Param("region") String region,
                                             @Param("round") int round,
                                             @Param("year") int year);


    @Query("SELECT distinct(m.round) from Match m "
            + "WHERE m.challenge.id=:challengeId "
            + "AND m.state<=2 "
            + "AND m.start>=current_date "
            + "ORDER BY m.round ASC")
    List<String> getUnfinishedRoundsForChallenge(@Param("challengeId") int challengeId);

    @Query("SELECT min(m.start) from Match m "
            + "WHERE m.challenge.id=:challengeId "
            + "AND m.round=:round "
            + "AND m.start>=current_date")
    Date getDateOfRoundInChallenge(@Param("challengeId") int challengeId, @Param("round") String round);
}
