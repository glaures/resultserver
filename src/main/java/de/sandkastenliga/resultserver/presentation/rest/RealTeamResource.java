package de.sandkastenliga.resultserver.presentation.rest;

import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.services.sportsinfosource.RegionRelevanceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class RealTeamResource {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private RegionRelevanceProvider regionRelevanceProvider;

    @GetMapping("/rest/realteams")
    public List<String> findRealTeamsByName(@RequestParam("q") String query) {
        return teamRepository.fuzzyFindTeamByName("%" + query + "%", regionRelevanceProvider.getRelevantRegions(),
                PageRequest.of(0, 10));
    }

}
