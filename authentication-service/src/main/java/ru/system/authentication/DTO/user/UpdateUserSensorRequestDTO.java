package ru.system.authentication.DTO.user;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateUserSensorRequestDTO {
    List<UUID> add;
    List<UUID> delete;
}
