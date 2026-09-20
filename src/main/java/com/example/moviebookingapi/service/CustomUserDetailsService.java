package com.example.moviebookingapi.service;

import com.example.moviebookingapi.model.User;
import com.example.moviebookingapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
//                .roles(user.getRole()) // adds ROLE_ to the actual role (ex: USER) but stores "USER" only in db
                .authorities(user.getRole()) // adds the actual role directly, so we can pass ROLE_USER clearly to store in db
                .build() ;
    }
}
