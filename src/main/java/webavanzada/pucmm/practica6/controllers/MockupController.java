package webavanzada.pucmm.practica6.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webavanzada.pucmm.practica6.entities.*;
import webavanzada.pucmm.practica6.services.JwtService;
import webavanzada.pucmm.practica6.services.MockupService;
import webavanzada.pucmm.practica6.services.ProjectService;
import webavanzada.pucmm.practica6.utils.UrlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/mockups")
public class MockupController {

    private final MockupService mockupService;
    private final ProjectService projectService;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    private int getPageSize() {
        return 10;
    }

    public static String redirectToCurrentProject(HttpSession session, Optional<String> errorMessage) {
        String redirect = (String) session.getAttribute("currentProject");
        if (redirect != null && !redirect.isEmpty()) {
            session.removeAttribute("currentProject");
            if (errorMessage.isPresent()) {
                String encodedErrorMessage = UrlUtils.encode(errorMessage.get());
                return "redirect:" + redirect + "?error=" + encodedErrorMessage;
            } else {
                return "redirect:" + redirect;
            }
        }

        return "redirect:/projects";
    }

    @RequestMapping(path = "/{project_id}")
    public String getMockupsPaginated(
            Model model,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @PathVariable Long project_id,
            @RequestParam Optional<String> error,
            HttpSession session
    ) {
        Optional<Project> projectOptional = projectService.getProjectById(project_id);

        if (projectOptional.isPresent()) {

            Project project = projectOptional.get();
            Page<Mockup> mockupPage = mockupService.getMockupsByProjectPaginated(project, page, getPageSize());

            model.addAttribute("mockups", mockupPage.getContent());
            model.addAttribute("totalPages", mockupPage.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("project", project);

            session.setAttribute("currentProject", "/mockups/" + project_id);

            error.ifPresent(errorMessage -> model.addAttribute("error", errorMessage));
            return "mockups/mockupList";
        } else {
            return "redirect:/projects";
        }
    }

    @GetMapping("/{project_id}/new")
    public String createMockupForm(@PathVariable("project_id") Long project_id, Model model, HttpSession session) {
        Optional<Project> projectOptional = projectService.getProjectById(project_id);

        if (projectOptional.isPresent()) {
            model.addAttribute("mockup", new Mockup());
            model.addAttribute("edit", false);
            model.addAttribute("project", projectOptional.get());
            model.addAttribute("http_methods", HttpMethod.values());
            model.addAttribute("content_types", ContentType.values());
            model.addAttribute("expiration_time", ExpirationTime.values());

            List<HttpStatus> httpStatusList = Arrays.asList(HttpStatus.values());
            model.addAttribute("http_status_codes", httpStatusList);

            return "mockups/mockupDetails";
        } else {
            String errorMessage = "The project doesn't exist or you don't have access to it.";
            return redirectToCurrentProject(session, Optional.of(errorMessage));
        }
    }

    @GetMapping("/{id}/edit")
    public String editMockupForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        Optional<Mockup> mockup = mockupService.getMockupById(id);

        if (mockup.isEmpty()) {
            String errorMessage = "There was an error when looking for the mockup to edit.";
            return redirectToCurrentProject(session, Optional.of(errorMessage));
        }

        Optional<Project> projectOptional = projectService.getProjectById(mockup.get().getProject().getId());

        if (projectOptional.isEmpty()) {
            String errorMessage = "There was an error when looking for the project's mockup to edit";
            return redirectToCurrentProject(session, Optional.of(errorMessage));
        }

        try {
            model.addAttribute("headers", objectMapper.readValue(mockup.get().getHeaders(), Map.class));
        } catch (Exception ignored) {}

        model.addAttribute("mockup", mockup.get());
        model.addAttribute("edit", true);
        model.addAttribute("project", projectOptional.get());
        model.addAttribute("http_methods", HttpMethod.values());
        model.addAttribute("content_types", ContentType.values());
        model.addAttribute("expiration_time", ExpirationTime.values());

        List<HttpStatus> httpStatusList = Arrays.asList(HttpStatus.values());
        model.addAttribute("http_status_codes", httpStatusList);

        return "mockups/mockupDetails";
    }

    @PostMapping("/{project_id}/save")
    public String addMockupToProject(@ModelAttribute("mockup") Mockup mockup,
                                     @PathVariable Long project_id,
                                     @RequestParam Map<String, String> formData,
                                     HttpSession session
    ) {
        Instant now = Instant.now();

        Map<String, String> headers = getHeaders(formData);
        Optional<Project> projectOptional = projectService.getProjectById(project_id);

        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            project.addMockup(mockup);
            mockup.setProject(project);

            mockup.setPath(UrlUtils.urlify(mockup.getPath()));

            try {
                String headersJson = objectMapper.writeValueAsString(headers);
                mockup.setHeaders(headersJson);
            } catch (Exception ignored) {}

            mockup.setCreationTime(now);

            if (mockup.isJwt_validation()) {
                mockup.setToken(jwtService.getToken(project.getUser()));
            }

            mockupService.save(mockup);
            return "redirect:/mockups/" + project_id;
        }

        String errorMessage = "The project doesn't exist or you don't have access to it.";
        return redirectToCurrentProject(session, Optional.of(errorMessage));
    }

    @PostMapping("/{project_id}/edit")
    public String editMockup(
            @PathVariable Long project_id,
            @RequestParam("id") Long id,
            @RequestParam Map<String, String> formData,
            @ModelAttribute("mockup") Mockup mockup,
            Model model
    ) {
        Map<String, String> headers = getHeaders(formData);
        Optional<Mockup> optionalMockup = mockupService.getMockupById(id);

        if (optionalMockup.isEmpty()) {
            model.addAttribute("error", "Mockup no longer exists");

            return "redirect:/mockups/" + project_id;
        }

        Mockup existingMockup = optionalMockup.get();
        existingMockup.setPath(UrlUtils.urlify(mockup.getPath()));
        existingMockup.setMethod(mockup.getMethod());
        existingMockup.setCode(mockup.getCode());
        existingMockup.setContent_type(mockup.getContent_type());
        existingMockup.setBody(mockup.getBody());
        existingMockup.setName(mockup.getName());
        existingMockup.setDescription(mockup.getDescription());
        existingMockup.setExpirationTime(mockup.getExpirationTime());
        existingMockup.setDelay(mockup.getDelay());
        existingMockup.setJwt_validation(mockup.isJwt_validation());
        existingMockup.setCreationTime(Instant.now());

        try {
            String headersJson = objectMapper.writeValueAsString(headers);
            optionalMockup.get().setHeaders(headersJson);
        } catch (Exception ignored) {}

        mockupService.save(existingMockup);

        return "redirect:/mockups/" + project_id;
    }

    private Map<String, String> getHeaders(@RequestParam Map<String, String> formData) {
        Map<String, String> headers = new HashMap<>();
        int index = 0;
        while (formData.containsKey("header-key-" + index)) {
            String key = formData.get("header-key-" + index);
            String value = formData.get("header-value-" + index);
            headers.put(key, value);
            index++;
        }
        return headers;
    }

    @RequestMapping("/{project_id}/{id}/delete")
    public String deleteMockup(@PathVariable Long project_id, @PathVariable Long id, Model model) {
        Optional<Mockup> optionalMockup = mockupService.getMockupById(id);

        if (optionalMockup.isEmpty()) {
            model.addAttribute("error", "Mockup not found.");

            return "redirect:/mockups/" + project_id;
        }
        mockupService.delete(id);

        return "redirect:/mockups/" + project_id;
    }
}