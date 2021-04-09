package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.dtos.MatchDto;
import de.sandkastenliga.resultserver.jobs.RetrievalJob;
import de.sandkastenliga.resultserver.services.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/match")
public class MatchResource {

    private final MatchService matchService;
    private final RetrievalJob retrievalJob;

    public MatchResource(MatchService matchService, RetrievalJob retrievalJob) {
        this.matchService = matchService;
        this.retrievalJob = retrievalJob;
    }

    @GetMapping("/")
    public MatchDto getMatchByTeamsAndDate(@RequestParam("team1") String team1,
                                           @RequestParam("team2") String team2, @RequestParam("date") String date) {
        Date d = new Date(Long.parseLong(date));
        MatchDto res = matchService.getClosestMatchByTeams(team1, team2, d);
        if (res == null) {
            throw new RuntimeException();
        }
        return res;
    }

    @GetMapping("/{matchIds}")
    public MatchDto[] getMatchesByIdList(@PathVariable("matchIds") String matchIds) {
        List<Integer> idList = new ArrayList<Integer>();
        StringTokenizer tok = new StringTokenizer(matchIds, ",", false);
        while (tok.hasMoreTokens()) {
            idList.add(Integer.parseInt(tok.nextToken()));
        }
        List<MatchDto> resList = matchService.getMatchesByIdList(idList);
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        for (MatchDto mdto : resList) {
            if (mdto.getStart().after(yesterday.getTime())) {
                retrievalJob.prolongTurbo();
            }
        }
        MatchDto[] res = new MatchDto[resList.size()];
        for (int i = 0; i < resList.size(); i++) {
            res[i] = resList.get(i);
        }
        return res;
    }

}
