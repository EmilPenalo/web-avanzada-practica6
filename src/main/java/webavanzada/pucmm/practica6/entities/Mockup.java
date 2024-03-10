package webavanzada.pucmm.practica6.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Mockup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethod method;

    private String headers;

    @Column(nullable = false)
    private int code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType content_type;

    @Column(nullable = false)
    private String body;

    private String name;

    private String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private ExpirationTime expirationTime;

    private int delay;

    private boolean jwt_validation;

    @Column(nullable = false)
    private Instant creationTime;

    private String token;

    public Mockup(String path, HttpMethod method, String headers, int code, ContentType content_type, String body, String name, String description, ExpirationTime expirationTime, int delay, boolean jwt_validation, Instant creationTime, String token) {
        this.path = path;
        this.method = method;
        this.headers = headers;
        this.code = code;
        this.content_type = content_type;
        this.body = body;
        this.name = name;
        this.description = description;
        this.expirationTime = expirationTime;
        this.delay = delay;
        this.jwt_validation = jwt_validation;
        this.creationTime = creationTime;
        this.token = token;
    }

    public String getFullPath() {
        return String.join("/", "access", project.getId().toString(), project.getPath(), id.toString(), path);
    }
}

