package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Badge;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserBadge;
import me.hakyuwon.ecostep.dto.BadgeDto;
import me.hakyuwon.ecostep.repository.BadgeRepository;
import me.hakyuwon.ecostep.repository.UserBadgeRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;

    public List<BadgeDto> getAllBadges(Long userId) {
        List<Badge> badges = badgeRepository.findAll();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자가 획득한 뱃지 목록 조회
        Set<Long> acquiredBadgeIds = userBadgeRepository.findByUser(user).stream()
                .map(userBadge -> userBadge.getBadge().getId())
                .filter(Objects::nonNull) // badge가 null일 경우 방지
                .collect(Collectors.toSet());

        // 뱃지 DTO 변환 (획득 여부 포함)
        return badges.stream()
                .map(badge -> new BadgeDto(badge.getId(), badge.getName(), acquiredBadgeIds.contains(badge.getId())))
                .collect(Collectors.toList());

    }
}
