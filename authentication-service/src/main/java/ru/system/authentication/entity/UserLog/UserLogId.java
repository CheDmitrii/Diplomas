package ru.system.authentication.entity.UserLog;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserLogId implements Serializable {
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "time", nullable = false)
    private Timestamp time;
}
