package me.hakyuwon.ecostep.dto;

import jakarta.persistence.NamedStoredProcedureQueries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.domain.User;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDto {
    private String treeName;
    private int badgeCount;
    private int missionCount;
    private int treeLevel;
    // 뱃지 개수, 오늘 미션 몇개 수행, 나무 레벨
}
