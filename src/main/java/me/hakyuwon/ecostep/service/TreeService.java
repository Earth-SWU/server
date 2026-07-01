package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.TreeResponseDto;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;
import me.hakyuwon.ecostep.repository.TreeRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TreeService {
    private final TreeRepository treeRepository;
    private final UserRepository userRepository;

    // 나무 조회
    public TreeResponseDto getTreeInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        return new TreeResponseDto(tree);
    }

    // 물 주기
    public TreeResponseDto useWater(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // 물이 0이면 예외 처리
        if (tree.getWater() <= 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_WATER);
        }

        tree.useWater();
        return new TreeResponseDto(tree);
    }

    // 비료 주기
    public TreeResponseDto useFertilizer(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        if (tree.getFertilizer() <= 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_FERTILIZER);
        }

        tree.useFertilizer();
        return new TreeResponseDto(tree);
    }
}
