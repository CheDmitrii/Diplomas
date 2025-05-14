package ru.system.authentication.service.user.userDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.system.authentication.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServer implements UserDetailsService {

    private final UserRepository userRepository;

    @Cacheable(cacheNames = "user", key = "#login")
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        System.out.println("EXECUTE loadUserByUsername with name: " + login);
        return userRepository.findByLogin(login).orElseThrow();
    }
}
