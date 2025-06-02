package ru.system.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.system.authentication.entity.Role;
import ru.system.authentication.repository.RoleRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
}
