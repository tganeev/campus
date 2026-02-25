package com.campus.backend.service;

import com.campus.backend.dto.PresenceDTO;
import com.campus.backend.dto.PresenceUpdateDTO;
import com.campus.backend.exception.ResourceNotFoundException;  // <-- ВАЖНО: добавьте этот импорт
import com.campus.backend.model.Presence;
import com.campus.backend.model.PresenceStatus;
import com.campus.backend.model.User;
import com.campus.backend.repository.PresenceRepository;
import com.campus.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PresenceService(PresenceRepository presenceRepository,
                           UserRepository userRepository,
                           SimpMessagingTemplate messagingTemplate) {
        this.presenceRepository = presenceRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public PresenceDTO updatePresence(Long userId, PresenceUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Presence presence = presenceRepository.findByUser(user)
                .orElse(new Presence());

        if (presence.getId() == null) {
            presence.setUser(user);
            presence.setCheckIn(LocalDateTime.now());
        }

        // Если пользователь уходит - записываем время выхода
        if (presence.getStatus() != PresenceStatus.OFFLINE &&
                dto.getStatus() == PresenceStatus.OFFLINE) {
            presence.setCheckOut(LocalDateTime.now());
        }

        // Если пользователь заходит - создаем новую запись
        if (presence.getStatus() == PresenceStatus.OFFLINE &&
                dto.getStatus() != PresenceStatus.OFFLINE) {
            presence.setCheckIn(LocalDateTime.now());
            presence.setCheckOut(null);
        }

        presence.setStatus(dto.getStatus());
        presence.setLocation(dto.getLocation());
        presence.setLastSeen(LocalDateTime.now());

        Presence savedPresence = presenceRepository.save(presence);

        PresenceDTO presenceDTO = PresenceDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .status(savedPresence.getStatus())
                .location(savedPresence.getLocation())
                .lastSeen(savedPresence.getLastSeen())
                .build();

        // Отправляем обновление через WebSocket
        messagingTemplate.convertAndSend("/topic/presence", presenceDTO);

        return presenceDTO;
    }

    public PresenceDTO getCurrentPresence(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return presenceRepository.findByUser(user)
                .map(presence -> PresenceDTO.builder()
                        .userId(user.getId())
                        .userName(user.getName())
                        .status(presence.getStatus())
                        .location(presence.getLocation())
                        .lastSeen(presence.getLastSeen())
                        .build())
                .orElse(null);
    }

    public List<PresenceDTO> getAllOnlineUsers() {
        return presenceRepository.findAllOnline().stream()
                .map(presence -> PresenceDTO.builder()
                        .userId(presence.getUser().getId())
                        .userName(presence.getUser().getName())
                        .status(presence.getStatus())
                        .location(presence.getLocation())
                        .lastSeen(presence.getLastSeen())
                        .build())
                .collect(Collectors.toList());
    }

    public long getOnlineCount() {
        return presenceRepository.countOnlineUsers();
    }
}