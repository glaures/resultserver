package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.match.MatchService;
import de.sandkastenliga.resultserver.services.schedule.ScheduleService;
import de.sandkastenliga.resultserver.utils.DateUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleResource {

    private final MatchService matchService;
    private final ScheduleService scheduleService;

    public ScheduleResource(MatchService matchService, ScheduleService scheduleService) {
        this.matchService = matchService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{start}/{end}")
    public MatchDto[] getMatch(@PathVariable("start") Long start, @PathVariable("end") Long end) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(new Date(start));
        DateUtils.resetToStartOfDay(startCal);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(new Date(end));
        DateUtils.resetToStartOfDay(endCal);
        List<MatchDto> allMatches = matchService.getAllMatchesAtDays(startCal.getTime(), endCal.getTime());
        MatchDto[] res = new MatchDto[allMatches.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = allMatches.get(i);
        }
        return res;
    }

    @GetMapping("/{region}/{challenge}/{day}")
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

    @PostMapping("/update/{date}")
    public void getMatch(@PathVariable("date") Long date) throws ServiceException {
        Date startTime = new Date(date);
        scheduleService.updateSchedule(startTime);
    }

}
