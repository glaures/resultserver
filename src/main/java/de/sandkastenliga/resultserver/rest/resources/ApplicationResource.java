package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.jobs.RetrievalJob;
import de.sandkastenliga.resultserver.services.schedule.ScheduleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/application")
public class ApplicationResource {

    private final RetrievalJob retrievalJob;
    private final ScheduleService scheduleService;

    public ApplicationResource(RetrievalJob retrievalJob, ScheduleService scheduleService) {
        this.retrievalJob = retrievalJob;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/turbo")
    public String getTurboStop() {
        final DateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        return df.format(retrievalJob.getTurboStop());
    }

    @GetMapping("/turbo/prolong")
    public String startTurbo() {
        retrievalJob.prolongTurbo();
        return "turbo prolonged";
    }

    @GetMapping("/update-rankings")
    public String updateRankings() {
        retrievalJob.updateRankings();
        return "done without error";
    }

    @GetMapping("/retrieve")
    public String retreiveMatches(@RequestParam("date") String dateStr) throws ParseException {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(dateStr);
        scheduleService.updateSchedule(date);
        return "done without error";
    }

}
