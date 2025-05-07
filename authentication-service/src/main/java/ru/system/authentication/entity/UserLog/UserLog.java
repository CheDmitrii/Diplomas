package ru.system.authentication.entity.UserLog;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(UserLogId.class)
@Table(name = "user_log")
public class UserLog {
    @Id
    private String userId;
    @Id
    private String type;
    @Id
    private Timestamp time;
}
