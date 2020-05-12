package de.sandkastenliga.resultserver.presentation.rest;

import de.sandkastenliga.resultserver.dtos.RankDto;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChallengeResource {

    @Autowired
    private ChallengeRepository challengeRepository;

    @GetMapping("/rest/challenge/ranking")
    public List<RankDto> getChallengeRanking(@RequestParam("challenge") String challenge,
                                             @RequestParam("round") Integer round,
                                             @RequestParam("year") Integer year) {
        // challenge "region/challenge"
        int slashIdx = challenge.indexOf('/');
        String r = challenge.substring(0, slashIdx);
        String c = challenge.substring(slashIdx + 1, challenge.length());
        List<RankDto> res = challengeRepository.getChallengeRanking(c, r, round, year);
        return res;
    }


}
