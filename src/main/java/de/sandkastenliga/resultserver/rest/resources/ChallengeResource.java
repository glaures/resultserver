package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.dtos.ChallengeDto;
import de.sandkastenliga.resultserver.dtos.RankDto;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.services.sportsinfosource.RegionRelevanceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChallengeResource {

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private RegionRelevanceProvider regionRelevanceProvider;

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

    @GetMapping("/rest/challenges")
    public List<ChallengeDto> getAllChallenges(@RequestParam("region") String region) {
        return challengeRepository.getAllChallengesByRegion(region);
    }

    @GetMapping("/rest/regions")
    public String[] getAllRelevantRegions() {
        return regionRelevanceProvider.getRelevantRegions();
    }


}
