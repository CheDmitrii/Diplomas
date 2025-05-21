package ru.system.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.system.authentication.DTO.user.CreateUserRequestDTO;
import ru.system.authentication.DTO.user.UpdateUserSensorRequestDTO;
import ru.system.authentication.entity.User;
import ru.system.authentication.repository.RoleRepository;
import ru.system.authentication.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final SensorPermissionService permissionService;
    private final RoleRepository roleRepository;

    public UUID createUser(CreateUserRequestDTO createUserDTO) {
        User user = userRepository.saveAndFlush(User.builder()
                .login(createUserDTO.getLogin())
                .password(createUserDTO.getPassword())
                .firstName(createUserDTO.getFirstName())
                .lastName(createUserDTO.getLastName())
                .build());
        if (!roleRepository.findById(createUserDTO.getRole()).orElseThrow().getName().equalsIgnoreCase("admin")) {
            permissionService.addSensorsForUser(user.getId(), createUserDTO.getSensors()); // todo if create admin add him all sensors
        }
        return user.getId();
    }

    public void changeUserSensors(UUID userId, UpdateUserSensorRequestDTO updateUserSensorRequestDTO) {
        permissionService.addSensorsForUser(userId, updateUserSensorRequestDTO.getAdd());
        permissionService.removeSensorsForUser(userId, updateUserSensorRequestDTO.getDelete());
    }
}
