package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.MissionDto;
import me.hakyuwon.ecostep.service.MissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/missions")
public class MissionController {
    private final MissionService missionService;

    // 미션 완료
    @PostMapping("/complete")
    public ResponseEntity<String> completeMission(@RequestBody MissionDto userMissionDto) {
        String result = missionService.completeMission(userMissionDto.getUserId(), userMissionDto.getMissionType());
        return ResponseEntity.ok(result);
    }

    // 출석 체크 미션
    /*@PostMapping("/attend")
    public ResponseEntity<
    * */

    // 영수증 인증 미션

    // 텀블러 사용 미션

    // 5000보 이상 걷기

    // 친환경 물품 구매

    // 친환경 기사 읽기
}
