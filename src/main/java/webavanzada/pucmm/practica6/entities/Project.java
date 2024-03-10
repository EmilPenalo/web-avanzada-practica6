package webavanzada.pucmm.practica6.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String path;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Mockup> mockups = new ArrayList<>();

    public Project(User user, String name, String path) {
        this.user = user;
        this.name = name;
        this.path = path;
    }

    public void addMockup(Mockup mockup) {
        mockups.add(mockup);
    }

    public void removeMockup(Mockup mockup) {
        mockups.remove(mockup);
    }
}