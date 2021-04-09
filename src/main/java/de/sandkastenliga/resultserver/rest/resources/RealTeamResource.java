package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.services.sportsinfosource.RegionRelevanceProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/realteams")
public class RealTeamResource {

    private final TeamRepository teamRepository;
    private final RegionRelevanceProvider regionRelevanceProvider;

    public RealTeamResource(TeamRepository teamRepository, RegionRelevanceProvider regionRelevanceProvider) {
        this.teamRepository = teamRepository;
        this.regionRelevanceProvider = regionRelevanceProvider;
    }

    @GetMapping("/")
    public List<String> findRealTeamsByName(@RequestParam("q") String query) {
        return teamRepository.fuzzyFindTeamByName("%" + query + "%", regionRelevanceProvider.getRelevantRegions(),
                PageRequest.of(0, 10));
    }

}
