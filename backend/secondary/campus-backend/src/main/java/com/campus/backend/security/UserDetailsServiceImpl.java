package com.campus.backend.security;

import com.campus.backend.model.User;
import com.campus.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Теперь ищем по school21Login, а не по email
        User user = userRepository.findBySchool21Login(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with School21 login: " + username));

        return UserPrincipal.create(user);
    }
}