package org.xenon.knowspace.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.repositories.UserRepository;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        var user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return new User(
                user.getEmail(),
                user.getName(),
                Collections.emptyList()
        );
    }
}
