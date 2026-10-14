package com.huayapadev.rentlibraryapi.auth.service;

import com.huayapadev.rentlibraryapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Long userId = Long.parseLong(id);
        return userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
