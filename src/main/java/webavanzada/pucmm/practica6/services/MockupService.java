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

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MockupService {

    private final MockupRepository mockupRepository;
    private final AuthService authService;

    public List<Mockup> getAllMockups() {
        return mockupRepository.findAll();
    }

    public Page<Mockup> getMockupsPaginated(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return mockupRepository.findAll(pageable);
    }

    public Page<Mockup> getMockupsByProjectPaginated(Project project, int page, int pageSize) {
        return mockupRepository.findByProject(project, PageRequest.of(page - 1, pageSize));
    }

    public Optional<Mockup> getMockupById(Long id) {
        Optional<User> user = authService.getLoggedUser();
        if (user.isPresent()) {
            if (user.get().isAdmin()) {
                return mockupRepository.findById(id);
            } else {
                return mockupRepository.findByIdAndUsername(id, user.get().getUsername());
            }
        }
        return Optional.empty();
    }

    public Optional<Mockup> getMockupByIdForEndpoint(Long id) {
        return mockupRepository.findById(id);
    }


    public void save(Mockup mockup) {
        mockupRepository.save(mockup);
    }

    public void update(Long id, Mockup newMockup) {
        Optional<Mockup> originalMockup = getMockupById(id);

        if (originalMockup.isPresent() && newMockup != null) {
            Mockup mockup = originalMockup.get();

            save(mockup);
        }
    }

    public void delete(Long id) {
        mockupRepository.deleteById(id);
    }
}
