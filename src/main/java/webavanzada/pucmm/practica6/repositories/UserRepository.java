package webavanzada.pucmm.practica6.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webavanzada.pucmm.practica6.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
