package de.sandkastenliga.resultserver.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.services.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Controller
public class MatchListView {

    private final DateFormat df = new SimpleDateFormat("d.M.");
    @Autowired
    private MatchService matchService;

    @GetMapping("/views/match-list")
    public String getMatches(Model model) throws JsonProcessingException {
        Calendar cal = Calendar.getInstance();
        model.addAttribute("startDate", df.format(cal.getTime()));
        model.addAttribute("endDate", df.format(cal.getTime()));
        List<MatchDto> matchList = matchService.getAllMatchesAtDays(cal.getTime(), cal.getTime());
        model.addAttribute("matchList", matchList);
        ObjectMapper om = new ObjectMapper();
        model.addAttribute("matchListJson", om.writeValueAsString(matchList));
        return "match-list";
    }
}
