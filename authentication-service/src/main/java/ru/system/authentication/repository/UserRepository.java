package ru.system.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.system.authentication.DTO.user.SingleUserDTO;
import ru.system.authentication.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLogin(String username);

    @Query("SELECT new ru.system.authentication.DTO.user.SingleUserDTO(u.id, u.login, u.firstName, u.lastName) " +
            "FROM User u WHERE u.login=:value OR u.firstName=:value OR u.lastName=:value")
    Optional<SingleUserDTO> findUser(String value);

    @Query("SELECT r.name FROM Role r " +
            "JOIN User u ON u.role.id=r.id WHERE u.id=:id")
    String getUserRoleById(UUID id);
}
