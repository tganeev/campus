package com.campus.backend.service;

import com.campus.backend.dto.UserCreateDTO;
import com.campus.backend.dto.UserDTO;
import com.campus.backend.exception.ResourceNotFoundException;
import com.campus.backend.mapper.UserMapper;
import com.campus.backend.model.User;
import com.campus.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO createUser(UserCreateDTO dto) {
        log.info("Creating user with email: {}", dto.getEmail());
        try {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("User with this email already exists");
            }

            User user = userMapper.toEntity(dto);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));

            User savedUser = userRepository.save(user);
            log.info("User created with id: {}", savedUser.getId());

            return userMapper.toDto(savedUser);

        } catch (Exception e) {
            log.error("Error creating user: ", e);
            throw e;
        }
    }

    public UserDTO getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
            return userMapper.toDto(user);

        } catch (Exception e) {
            log.error("Error getting user by id: {}", id, e);
            throw e;
        }
    }

    public List<UserDTO> getAllUsers() {
        log.info("Getting all users");
        try {
            List<UserDTO> users = userRepository.findAll().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
            log.info("Found {} users", users.size());
            return users;

        } catch (Exception e) {
            log.error("Error getting all users: ", e);
            throw e;
        }
    }

    public List<UserDTO> getPeers() {
        log.info("Getting all peers");
        try {
            List<UserDTO> peers = userRepository.findAllPeers().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
            log.info("Found {} peers", peers.size());
            return peers;

        } catch (Exception e) {
            log.error("Error getting peers: ", e);
            throw e;
        }
    }

    public User getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        } catch (Exception e) {
            log.error("Error getting user by email: {}", email, e);
            throw e;
        }
    }

    @Transactional
    public UserDTO updateUser(Long id, UserCreateDTO dto) {
        log.info("Updating user with id: {}", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setTelegramNick(dto.getTelegramNick());

            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            User updatedUser = userRepository.save(user);
            log.info("User updated with id: {}", id);

            return userMapper.toDto(updatedUser);

        } catch (Exception e) {
            log.error("Error updating user with id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        try {
            if (!userRepository.existsById(id)) {
                throw new ResourceNotFoundException("User not found with id: " + id);
            }
            userRepository.deleteById(id);
            log.info("User deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error deleting user with id: {}", id, e);
            throw e;
        }
    }
}