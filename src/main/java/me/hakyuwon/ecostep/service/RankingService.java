package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.RankingResponse;
import me.hakyuwon.ecostep.repository.TreeRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final UserRepository userRepository;
    private final TreeRepository treeRepository;

    // 상위 10명 랭킹 조회
    public List<RankingResponse> getTop10Rankings() {
        List<Tree> trees = treeRepository.findTop10ByOrderByTreeLevelDescTreeGrowthDesc();
        if (trees.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "랭킹 데이터를 찾을 수 없습니다.");
        }
        return mapToRankingResponse(trees);
    }

    private List<RankingResponse> mapToRankingResponse(List<Tree> trees) {
        List<RankingResponse> rankings = new ArrayList<>();
        int rank = 1;

        for (Tree tree : trees) {
            rankings.add(new RankingResponse(
                    rank++,
                    tree.getTreeName(),
                    tree.getTreeLevel(),
                    tree.getTreeGrowth()
            ));
        }
        return rankings;
    }
}