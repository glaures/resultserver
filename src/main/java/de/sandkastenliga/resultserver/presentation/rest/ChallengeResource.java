package de.sandkastenliga.resultserver.presentation.rest;

import de.sandkastenliga.resultserver.dtos.RankDto;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChallengeResource {

    @Autowired
    private ChallengeRepository challengeRepository;

    @GetMapping("/rest/challenge/{challengeId}/ranking")
    public List<RankDto> getChallengeRanking(@PathVariable("challengeId") int challengeId,
                                             @RequestParam("r") Integer round,
                                             @RequestParam("y") Integer year) {
        return challengeRepository.getChallengeRanking(challengeId, round, year);
    }


}
