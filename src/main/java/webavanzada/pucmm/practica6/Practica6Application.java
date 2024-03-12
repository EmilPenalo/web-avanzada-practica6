package webavanzada.pucmm.practica6;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import webavanzada.pucmm.practica6.entities.Role;
import webavanzada.pucmm.practica6.entities.User;
import webavanzada.pucmm.practica6.repositories.UserRepository;

import java.util.Arrays;

@EnableRedisWebSession
@EnableRedisIndexedHttpSession
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