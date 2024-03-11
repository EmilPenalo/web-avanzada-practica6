package webavanzada.pucmm.practica6;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import webavanzada.pucmm.practica6.entities.Role;
import webavanzada.pucmm.practica6.entities.User;
import webavanzada.pucmm.practica6.repositories.UserRepository;

import java.util.Arrays;

@EnableSpringHttpSession
@SpringBootApplication
public class Practica6Application {

	public static void main(String[] args) throws InterruptedException {
		Thread.sleep(5000);
		SpringApplication.run(Practica6Application.class, args);
	}

	@Bean
	public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			User user = new User();
			user.setUsername("admin");
			user.setPassword(passwordEncoder.encode("admin"));
			user.setRoles(Arrays.asList(Role.ROLE_USER, Role.ROLE_ADMIN));
			userRepository.save(user);
		};
	}
}