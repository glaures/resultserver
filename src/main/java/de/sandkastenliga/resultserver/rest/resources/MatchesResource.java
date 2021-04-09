package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.match.MatchService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/matches")
public class MatchesResource {

    private MatchService matchService;

    public MatchesResource(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public MatchDto toggleFeatured(@PathVariable("id") Integer matchId,
                                   @RequestParam(name = "featured", required = false) Boolean featured) throws ServiceException {
        MatchDto m = matchService.getMatch(matchId);
        if (featured != null) {
            return matchService.setFeatured(m.getId(), featured);
        }
        return null;
    }
}
