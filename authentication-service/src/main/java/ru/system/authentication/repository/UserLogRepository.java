package ru.system.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.system.authentication.entity.UserLog.UserLog;
import ru.system.authentication.entity.UserLog.UserLogId;

@Repository
public interface UserLogRepository extends JpaRepository<UserLog, UserLogId> {
}
