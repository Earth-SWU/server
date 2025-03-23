package me.hakyuwon.ecostep.service;

import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.TreeResponseDto;
import me.hakyuwon.ecostep.repository.TreeRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TreeService {
    private final TreeRepository treeRepository;
    private final UserRepository userRepository;

    public TreeService(TreeRepository treeRepository, UserRepository userRepository) {
        this.treeRepository = treeRepository;
        this.userRepository = userRepository;
    }

    public TreeResponseDto setTreeName(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("트리 정보가 없습니다."));

        tree.setTreeName(name);
        treeRepository.save(tree);
        user.setTree(tree);

        return new TreeResponseDto(tree);
    }

    // 나무 조회
    public TreeResponseDto getTreeInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("트리 정보가 없습니다."));

        return new TreeResponseDto(tree);
    }

    // 물 주기
    public TreeResponseDto useWater(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("트리 정보가 없습니다."));

        // 물이 0이면 예외 처리
        if (tree.getWater() <= 0) {
            throw new IllegalArgumentException("물이 부족해요!");
        }

        int water = 1;
        int growth = 15;

        tree.setWater(tree.getWater() - water); // 물 1개 차감
        tree.setTreeGrowth(tree.getTreeGrowth() + growth);

        checkLevelUp(tree);// 성장 퍼센트 증가
        treeRepository.save(tree);
        return new TreeResponseDto(tree);
    }

    /*
    // 비료 주기
    public TreeResponseDto useFertilizer(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("트리 정보가 없습니다."));

        int fert = 1;
        int growth = 30;

        tree.setFertilizer(tree.getFertilizer() - fert); // 물 1개 차감
        tree.setGrowth(tree.getGrowth() + growth);
        checkLevelUp(tree);

        return new TreeResponseDto(tree);
    }*/

    private void checkLevelUp(Tree tree) {
        while (tree.getTreeGrowth() >= 100) {
            levelUp(tree);
        }
    }

    private void levelUp(Tree tree) {
        if (tree.getTreeLevel() < 4) {  // 최대 레벨 4
            tree.setTreeLevel(tree.getTreeLevel() + 1);  // 레벨 업
            tree.setTreeGrowth(tree.getTreeGrowth() - 100);
        } else {
            tree.setTreeGrowth(100); // 끝
        }
    }
}
