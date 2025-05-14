package ru.system.authentication.DTO.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

@Data
public class CreateUserRequestDTO {
    @NotNull
    @NotBlank
    private String login;
    @NotNull
    @NotBlank
    @Length(min = 6)
    private String password;
    @NotNull
    @NotBlank
    private String firstName;
    @NotNull
    @NotBlank
    private String lastName;
    @NotNull
    private UUID role;
    List<UUID> sensors;
}
