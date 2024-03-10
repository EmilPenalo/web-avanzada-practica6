package webavanzada.pucmm.practica6.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webavanzada.pucmm.practica6.entities.Role;
import webavanzada.pucmm.practica6.entities.User;
import webavanzada.pucmm.practica6.repositories.ProjectRepository;
import webavanzada.pucmm.practica6.repositories.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller()
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;

    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users/userList";
    }

    @GetMapping("/new")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("edit", false);
        return "users/userDetails";
    }

    @GetMapping("{username}/edit")
    public String createUserForm(@PathVariable("username") String username, Model model) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            model.addAttribute("error", "There was an error when looking for the user to edit");
            List<User> users = userRepository.findAll();
            model.addAttribute("users", users);
            return "users/userList";
        }

        model.addAttribute("user", user.get());
        model.addAttribute("edit", true);
        return "users/userDetails";
    }

    @PostMapping("/save")
    public String createUser(@ModelAttribute("user") User user, Model model) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "users/userDetails";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "redirect:/users";
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam("username") String username,
                           @RequestParam("roles") List<Role> roles,
                           @RequestParam("new_password") String newPassword,
                           Model model) {

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            model.addAttribute("error", "User no longer exists");
            return "users/userDetails";
        }

        if (!newPassword.isEmpty()) {
            user.get().setPassword(passwordEncoder.encode(newPassword));
        }

        user.get().setRoles(roles);

        userRepository.save(user.get());
        return "redirect:/users";
    }

    @PostMapping("/{username}/delete")
    public String deleteUser(@PathVariable("username") String username, Model model) {

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            model.addAttribute("error", "User not found.");
        } else if (Objects.equals(user.get().getUsername(), "admin")) {
            model.addAttribute("error", "Default admin user cannot be deleted.");
        } else if (projectRepository.countByUsername(username) != 0) {
            model.addAttribute("error", "Cannot delete a user with active projects.");
        } else {
            userRepository.delete(user.get());
        }

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users/userList";
    }
}
