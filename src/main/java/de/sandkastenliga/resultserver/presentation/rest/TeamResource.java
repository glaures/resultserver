package de.sandkastenliga.resultserver.presentation.rest;

import de.sandkastenliga.resultserver.dtos.ExtendedTeamDto;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamResource {

    @Autowired
    private TeamService teamService;

    @GetMapping("/rest/team/{id}")
    public ExtendedTeamDto getTeam(@PathVariable("id") String id) throws ServiceException {
        return teamService.getExtendedTeamDto(id);
    }
}
