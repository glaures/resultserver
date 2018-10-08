package de.sandkastenliga.resultserver.presentation.rest;

import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.match.MatchService;
import de.sandkastenliga.resultserver.services.schedule.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
public class ScheduleResource {

    @Autowired
    private MatchService matchService;
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/rest/schedule/{start}/{end}")
    public MatchDto[] getMatch(@PathVariable("start") Long start, @PathVariable("end") Long end) {
        Date startTime = new Date(start);
        Date endTime = new Date(end);
        List<MatchDto> allMatches = matchService.getAllMatchesAtDays(startTime, endTime);
        MatchDto[] res = new MatchDto[allMatches.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = allMatches.get(i);
        }
        return res;
    }

    @GetMapping("/rest/schedule/{region}/{challenge}/{day}")
    public MatchDto[] getMatch(@PathVariable("region") String region,
                               @PathVariable("challenge") String challenge, @PathVariable("day") String day) throws ParseException {
        List<MatchDto> allMatches = matchService.getAllMatchesByRegionChallengeAndDay(region, challenge,
                day);
        MatchDto[] res = new MatchDto[allMatches.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = allMatches.get(i);
        }
        return res;
    }

    @PostMapping("/rest/schedule/update/{date}")
    public void getMatch(@PathVariable("date") Long date) throws ServiceException {
        Date startTime = new Date(date);
        scheduleService.updateSchedule(startTime);
    }

}
