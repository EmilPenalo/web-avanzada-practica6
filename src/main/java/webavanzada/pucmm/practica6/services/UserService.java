package webavanzada.pucmm.practica6.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import webavanzada.pucmm.practica6.entities.User;
import webavanzada.pucmm.practica6.repositories.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), user.get().isEnabled(), true, true, true, user.get().getAuthorities());
    }
}
