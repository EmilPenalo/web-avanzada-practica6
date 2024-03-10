package webavanzada.pucmm.practica6.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webavanzada.pucmm.practica6.entities.Project;
import webavanzada.pucmm.practica6.entities.User;
import webavanzada.pucmm.practica6.repositories.UserRepository;
import webavanzada.pucmm.practica6.utils.UrlUtils;
import webavanzada.pucmm.practica6.services.AuthService;
import webavanzada.pucmm.practica6.services.ProjectService;

import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final AuthService authService;
    private final UserRepository userRepository;

    private int getPageSize() {
        return 10;
    }

    @RequestMapping
    public String getProjectPaginated(Model model,
                                      @RequestParam Optional<String> username,
                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                      @RequestParam Optional<String> error,
                                      HttpSession session) {

        Optional<User> loggedUser = authService.getLoggedUser();
        if (loggedUser.isEmpty()) {
            String errorMessage = "There was an error validating your session.";
            String encodedErrorMessage = UrlUtils.encode(errorMessage);
            return "redirect:/login?error=" + encodedErrorMessage;
        }
        String fetchUsername = loggedUser.get().isAdmin() ? username.orElse(loggedUser.get().getUsername()) : loggedUser.get().getUsername();
        boolean adminView = loggedUser.get().isAdmin() && (username.filter(s -> !s.equals(loggedUser.get().getUsername())).isPresent());

        Page<Project> projectPage = projectService.getProjectsPaginated(fetchUsername, page, getPageSize());

        model.addAttribute("projects", projectPage.getContent());
        model.addAttribute("totalPages", projectPage.getTotalPages());
        model.addAttribute("currentPage", page);

        model.addAttribute("adminView", adminView);
        if (adminView) {
            model.addAttribute("username", fetchUsername);
            session.setAttribute("successRedirect", "/projects?username=" + fetchUsername);
        } else {
            session.setAttribute("successRedirect", "/projects");
        }

        error.ifPresent(errorMessage -> model.addAttribute("error", errorMessage));
        return "projects/projectList";
    }

    @GetMapping("/new")
    public String createProjectForm(@RequestParam Optional<String> username, Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("edit", false);

        Optional<User> loggedUser = authService.getLoggedUser();
        if (username.isPresent() && (loggedUser.isPresent() && loggedUser.get().isAdmin())) {
            model.addAttribute("forUser", username.get());
        }

        return "projects/projectDetails";
    }

    @GetMapping("{id}/edit")
    public String editProjectForm(@PathVariable("id") Long id, Model model) {
        Optional<Project> project = projectService.getProjectById(id);

        if (project.isEmpty()) {
            String errorMessage = "Project no longer exists or you don't have access to it.";
            String encodedErrorMessage = UrlUtils.encode(errorMessage);
            return "redirect:/projects?error=" + encodedErrorMessage;
        }

        model.addAttribute("project", project.get());
        model.addAttribute("edit", true);

        return "projects/projectDetails";
    }

    @PostMapping("/save")
    public String createUser(@ModelAttribute("project") Project project,
                             @RequestParam("forUser") Optional<String> forUser) {

        Optional<User> user = authService.getLoggedUser();
        if (user.isEmpty()) {
            String errorMessage = "There was an error validating your session.";
            String encodedErrorMessage = UrlUtils.encode(errorMessage);
            return "redirect:/login?error=" + encodedErrorMessage;
        }

        String redirect = "";
        if (forUser.isPresent() && (user.get().isAdmin())) {
            Optional<User> newUser = userRepository.findByUsername(forUser.get());
            if (newUser.isPresent()) {
                user = newUser;
                redirect = "?username=" + forUser.get();
            }
        }

        project.setUser(user.get());
        project.setPath(UrlUtils.urlify(project.getPath()));

        projectService.save(project);
        return "redirect:/projects" + redirect;
    }

    @PostMapping("/edit")
    public String editUser(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("path") String path,
            HttpSession session,
            Model model
    ) {
        Optional<Project> optionalProject = projectService.getProjectById(id);

        if (optionalProject.isEmpty()) {
            model.addAttribute("error", "Project no longer exists or you don't have access to it.");

            return "projects/projectDetails";
        }

        optionalProject.get().setName(name);
        optionalProject.get().setPath(UrlUtils.urlify(path));

        projectService.save(optionalProject.get());

        String successRedirect = (String) session.getAttribute("successRedirect");
        if (successRedirect != null && !successRedirect.isEmpty()) {
            session.removeAttribute("successRedirect");
            return "redirect:" + successRedirect;
        }
        return "redirect:/projects";
    }

    @RequestMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id, HttpSession session) {
        Optional<Project> optionalProject = projectService.getProjectById(id);

        if (optionalProject.isEmpty()) {
            String errorMessage = "Project no longer exists or you don't have access to it.";
            String encodedErrorMessage = UrlUtils.encode(errorMessage);
            return "redirect:/projects?error=" + encodedErrorMessage;
        }

        projectService.delete(id);

        String successRedirect = (String) session.getAttribute("successRedirect");
        if (successRedirect != null && !successRedirect.isEmpty()) {
            session.removeAttribute("successRedirect");
            return "redirect:" + successRedirect;
        }
        return "redirect:/projects";
    }
}