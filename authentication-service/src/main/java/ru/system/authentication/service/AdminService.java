package ru.system.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.system.authentication.DTO.user.CreateUserRequestDTO;
import ru.system.authentication.DTO.user.SingleUserDTO;
import ru.system.authentication.DTO.user.UpdateUserSensorRequestDTO;
import ru.system.authentication.entity.Role;
import ru.system.authentication.entity.User;
import ru.system.authentication.repository.RoleRepository;
import ru.system.authentication.repository.UserRepository;
import ru.system.library.exception.HttpResponseEntityException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final SensorPermissionService permissionService;
    private final RoleRepository roleRepository;

    public List<SingleUserDTO> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UUID createUser(CreateUserRequestDTO createUserDTO) {
        Role role = roleRepository.findById(createUserDTO.getRole())
                .orElseThrow(() -> new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Role not found with id: " + createUserDTO.getRole()));
        User user = userRepository.saveAndFlush(User.builder()
                .login(createUserDTO.getLogin())
                .password(createUserDTO.getPassword())
                .firstName(createUserDTO.getFirstName())
                .lastName(createUserDTO.getLastName())
                .role(role)
                .build());
        if (!role.getName().equalsIgnoreCase("admin")) {
            permissionService.addSensorsForUser(user.getId(), createUserDTO.getSensors());
        }
        return user.getId();
    }

    public void changeUserSensors(UUID userId, UpdateUserSensorRequestDTO updateUserSensorRequestDTO) {
        permissionService.addSensorsForUser(userId, updateUserSensorRequestDTO.getAdd());
        permissionService.removeSensorsForUser(userId, updateUserSensorRequestDTO.getDelete());
    }
}
