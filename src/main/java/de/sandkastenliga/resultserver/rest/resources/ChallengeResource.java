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

    private final ChallengeRepository challengeRepository;
    private final RegionRelevanceProvider regionRelevanceProvider;

    public ChallengeResource(ChallengeRepository challengeRepository, RegionRelevanceProvider regionRelevanceProvider) {
        this.challengeRepository = challengeRepository;
        this.regionRelevanceProvider = regionRelevanceProvider;
    }

    @GetMapping("/challenges/ranking")
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

    @GetMapping("/challenges")
    public List<ChallengeDto> getAllChallenges(@RequestParam("region") String region) {
        if(region.startsWith("("))
            region = region.substring(1, region.length() - 1);
        return challengeRepository.getAllChallengesByRegion(region);
    }

    @GetMapping("/regions")
    public String[] getAllRelevantRegions() {
        return regionRelevanceProvider.getRelevantRegions();
    }


}
