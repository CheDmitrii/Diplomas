package ru.system.authentication.repository;

import org.springframework.data.jpa.repository.Query;
import ru.system.authentication.DTO.user.SingleUserDTO;
import ru.system.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLogin(String username);

    @Query("SELECT new ru.system.authentication.DTO.user.SingleUserDTO(u.id, u.login, u.firstName, u.lastName) FROM User u")
    List<SingleUserDTO> getAllUsers();
}
