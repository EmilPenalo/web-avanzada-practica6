package webavanzada.pucmm.practica6.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webavanzada.pucmm.practica6.entities.Project;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.path = :path")
    Project findByIdAndPath(@Param("id") Long id, @Param("path") String path);

    @Query("SELECT p FROM Project p WHERE p.user.username = :username")
    Page<Project> findAllByUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.user.username = :username")
    Optional<Project> findByIdAndUsername(@Param("id") Long id, @Param("username") String username);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.user.username = :username")
    long countByUsername(@Param("username") String username);
}
