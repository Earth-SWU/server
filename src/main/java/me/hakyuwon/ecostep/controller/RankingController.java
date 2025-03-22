package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.RankingResponse;
import me.hakyuwon.ecostep.service.RankingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RankingController {
    private final RankingService rankingService;

    @GetMapping("/api/rankings")
    public List<RankingResponse> getRankings() {
        return rankingService.getTop10Rankings();
    }
}
