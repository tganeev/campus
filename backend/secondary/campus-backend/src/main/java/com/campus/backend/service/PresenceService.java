package com.campus.backend.service;

import com.campus.backend.dto.PresenceDTO;
import com.campus.backend.dto.PresenceUpdateDTO;
import com.campus.backend.exception.ResourceNotFoundException;
import com.campus.backend.model.Presence;
import com.campus.backend.model.PresenceStatus;
import com.campus.backend.model.User;
import com.campus.backend.repository.PresenceRepository;
import com.campus.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public PresenceDTO updatePresence(Long userId, PresenceUpdateDTO dto) {
        log.info("Updating presence for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Presence presence = presenceRepository.findByUser(user)
                .orElse(new Presence());

        if (presence.getId() == null) {
            presence.setUser(user);
            presence.setCheckIn(LocalDateTime.now());
        }

        if (presence.getStatus() != PresenceStatus.OFFLINE &&
                dto.getStatus() == PresenceStatus.OFFLINE) {
            presence.setCheckOut(LocalDateTime.now());
        }

        if (presence.getStatus() == PresenceStatus.OFFLINE &&
                dto.getStatus() != PresenceStatus.OFFLINE) {
            presence.setCheckIn(LocalDateTime.now());
            presence.setCheckOut(null);
        }

        presence.setStatus(dto.getStatus());
        presence.setLocation(dto.getLocation());
        presence.setLastSeen(LocalDateTime.now());

        Presence savedPresence = presenceRepository.save(presence);

        PresenceDTO presenceDTO = convertToDTO(savedPresence);

        messagingTemplate.convertAndSend("/topic/presence", presenceDTO);

        return presenceDTO;
    }

    public List<PresenceDTO> getAllOnlineUsers() {
        log.info("Getting all online users");
        long start = System.currentTimeMillis();

        List<Presence> onlinePresences = presenceRepository.findAllOnline();

        List<PresenceDTO> dtos = onlinePresences.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        long time = System.currentTimeMillis() - start;
        log.info("Found {} online users in {} ms", dtos.size(), time);

        return dtos;
    }

    public PresenceDTO getCurrentPresence(Long userId) {
        log.info("Getting presence for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return presenceRepository.findByUser(user)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public long getOnlineCount() {
        return presenceRepository.countOnlineUsers();
    }

    private PresenceDTO convertToDTO(Presence presence) {
        return PresenceDTO.builder()
                .userId(presence.getUser().getId())
                .userName(presence.getUser().getName())
                .status(presence.getStatus())
                .location(presence.getLocation())
                .lastSeen(presence.getLastSeen())
                .build();
    }
}