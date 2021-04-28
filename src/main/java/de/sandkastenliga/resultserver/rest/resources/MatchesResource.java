package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.match.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import javax.websocket.server.PathParam;
import java.util.Optional;

@RestController
@RequestMapping("/matches")
public class MatchesResource {

    private MatchService matchService;
    private MatchRepository matchRepository;

    public MatchesResource(MatchService matchService,
                           MatchRepository matchRepository) {
        this.matchService = matchService;
        this.matchRepository = matchRepository;
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

    @GetMapping()
    public MatchDto getByCorrelationId(@RequestParam("correlation-id") String correlationId){
        Optional<Match> match = matchRepository.findMatchByCorrelationId(correlationId);
        if(!match.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return new MatchDto(match.get());
    }
}
