package me.hakyuwon.ecostep.service;

import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.repository.MissionRepository;
import me.hakyuwon.ecostep.repository.UserMissionRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MissionService {

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public String completeMission(Long userId, Long missionId){
        User user = userRepository.findByEmail(email);
    }

}
