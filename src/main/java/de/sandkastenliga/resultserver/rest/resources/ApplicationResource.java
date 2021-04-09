package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.jobs.RetrievalJob;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/application")
public class ApplicationResource {

    private final RetrievalJob retrievalJob;

    public ApplicationResource(RetrievalJob retrievalJob) {
        this.retrievalJob = retrievalJob;
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
}
