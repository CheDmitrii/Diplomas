package ru.system.authentication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_journal")
public class UserJournal {
    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @NotNull(message = "user_id не должно быть равно null")
    @Column(name = "user_id")
    private UUID userId;

    @NotNull
    @Size(max = 255)
    @Column(name = "action")
    private String action;

    @NotNull
    @Column(name = "time")
    private Timestamp time;
}
