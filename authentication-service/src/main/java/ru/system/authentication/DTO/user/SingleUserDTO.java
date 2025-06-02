package ru.system.authentication.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SingleUserDTO {
    private UUID id;
    private String login;
    private String firstName;
    private String lastName;
}
