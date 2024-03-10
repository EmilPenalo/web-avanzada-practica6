package webavanzada.pucmm.practica6.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import webavanzada.pucmm.practica6.entities.Mockup;
import webavanzada.pucmm.practica6.entities.Project;
import webavanzada.pucmm.practica6.entities.User;
import webavanzada.pucmm.practica6.repositories.MockupRepository;
import webavanzada.pucmm.practica6.repositories.ProjectRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MockupRepository mockupRepository;
    private final AuthService authService;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Page<Project> getProjectsPaginated(String username, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return projectRepository.findAllByUsername(username, pageable);
    }

    public Optional<Project> getProjectById(Long id) {
        Optional<User> user = authService.getLoggedUser();
        if (user.isPresent()) {
            if (user.get().isAdmin()) {
                return projectRepository.findById(id);
            } else {
                return projectRepository.findByIdAndUsername(id, user.get().getUsername());
            }
        }
        return Optional.empty();
    }

    public Optional<Project> getProjectByIdAndPath(Long id, String path) {
        return Optional.ofNullable(projectRepository.findByIdAndPath(id, path));
    }

    public void save(Project project) {
        projectRepository.save(project);
    }

    public void edit(Long id, Project newProject) {
        Optional<Project> originalProject = getProjectById(id);

        if (originalProject.isPresent() && newProject != null) {
            Project project = originalProject.get();
            project.setName(newProject.getName());

            save(project);
        }
    }

    /*
    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }
    */

    public void delete(Long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        projectOptional.ifPresent(project -> {
            List<Mockup> mockups = project.getMockups();
            mockupRepository.deleteAll(mockups);
            projectRepository.delete(project);
        });
    }
}
