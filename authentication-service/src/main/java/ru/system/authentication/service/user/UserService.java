package ru.system.authentication.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.system.authentication.entity.User;
import ru.system.authentication.entity.UserJournal;
import ru.system.authentication.repository.UserJournalRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserJournalRepository userJournalRepository;

    public void saveUserJournal(User user, String type) {
        userJournalRepository.saveAndFlush(UserJournal.builder()
                        .userId(user.getId())
                        .action(type)
                        .time(Timestamp.valueOf(LocalDateTime.now()))
                .build()
        );
    }
}
