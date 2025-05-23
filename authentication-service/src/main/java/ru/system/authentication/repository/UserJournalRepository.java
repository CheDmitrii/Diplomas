package ru.system.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.system.authentication.entity.UserJournal;

import java.util.UUID;

@Repository
public interface UserJournalRepository extends JpaRepository<UserJournal, UUID> {
}
