package ru.system.authentication.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.system.authentication.DTO.user.CreateUserRequestDTO;
import ru.system.authentication.entity.Role;
import ru.system.authentication.entity.User;
import ru.system.authentication.entity.UserJournal;
import ru.system.authentication.repository.UserJournalRepository;
import ru.system.authentication.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserJournalRepository journalRepository;

    // todo: add to admin controller
    public void createUser(CreateUserRequestDTO user) {
        userRepository.saveAndFlush(User.builder()
                        .login(user.getLogin())
                        .password(user.getPassword())
                        .role(Role.builder()
                                .id(user.getRole())
                                .build())
                .build());
    }

    public void saveUserJournal(User user, String type) {
        journalRepository.saveAndFlush(UserJournal.builder()
                        .userId(user.getId())
                        .action(type)
                        .time(Timestamp.valueOf(LocalDateTime.now()))
                .build()
        );
    }
}
