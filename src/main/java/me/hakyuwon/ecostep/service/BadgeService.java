package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Badge;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.BadgeDto;
import me.hakyuwon.ecostep.repository.BadgeRepository;
import me.hakyuwon.ecostep.repository.UserBadgeRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;

    public List<BadgeDto> getAllBadges(Long userId) {
        List<Badge> badges = badgeRepository.findAll(); // 모든 뱃지 조회
        List<BadgeDto> badgeDtos = new ArrayList<>();
        User user = userRepository.findById(userId).orElse(null);

        // 사용자가 획득한 뱃지 목록을 한 번에 조회
        List<Badge> acquiredBadges = userBadgeRepository.findBadgesByUser(userId);
        Set<Long> acquiredBadgeIds = acquiredBadges.stream()
                .map(Badge::getId)
                .collect(Collectors.toSet());

        // 모든 뱃지들에 대해 획득 여부 체크
        for (Badge badge : badges) {
            boolean isAcquired = acquiredBadgeIds.contains(badge.getId()); // 사용자가 획득했으면 true, 아니면 false
            badgeDtos.add(new BadgeDto(badge.getId(), badge.getName(), isAcquired));
        }
        return badgeDtos;
    }
}
