package de.sandkastenliga.resultserver.views;

import de.sandkastenliga.resultserver.dtos.ExtendedTeamDto;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TeamDetailView {

    @Autowired
    private TeamService teamService;

    @GetMapping("/ui/teamdetail")
    public String getTeamDetailView(@RequestParam("id") String teamId, Model model) {
        ExtendedTeamDto team = null;
        try {
            team = teamService.getExtendedTeamDto(teamId);
        } catch (ServiceException se) {
            team = new ExtendedTeamDto();
            team.setName("Ich kann kein Team mit der ID " + teamId + " finden.");
        }
        model.addAttribute("team", team);
        return "team-detail";
    }
}
