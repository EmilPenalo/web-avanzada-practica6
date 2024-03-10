package webavanzada.pucmm.practica6.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webavanzada.pucmm.practica6.entities.Mockup;
import webavanzada.pucmm.practica6.entities.Project;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MockupRepository extends JpaRepository<Mockup, Long> {
    Page<Mockup> findByProject(Project project, Pageable pageable);

    @Query("SELECT m FROM Mockup m JOIN m.project p WHERE m.id = :id AND p.user.username = :username")
    Optional<Mockup> findByIdAndUsername(@Param("id") Long id, @Param("username") String username);

    @Query("SELECT m FROM Mockup m JOIN m.project p JOIN p.user u WHERE u.username = :username")
    List<Mockup> findByUsername(@Param("username") String username);
}