package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.jobs.RetrievalJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@RestController
public class ApplicationResource {

    @Autowired
    private RetrievalJob retrievalJob;

    @GetMapping("/rest/application/turbo")
    public String getTurboStop() {
        final DateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        return df.format(retrievalJob.getTurboStop());
    }

    @GetMapping("/rest/application/turbo/prolong")
    public String startTurbo() {
        retrievalJob.prolongTurbo();
        return "turbo prolonged";
    }

    @GetMapping("/rest/application/update-rankings")
    public String updateRankings() {
        retrievalJob.updateRankings();
        return "done without error";
    }
}
