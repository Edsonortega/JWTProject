package com.example.JWTProject;

import com.example.JWTProject.entity.User;
import com.example.JWTProject.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.example.JWTProject")  //Scan all the packages
@EnableJpaRepositories("com.example.JWTProject.repository") //Enable the repository
public class JwtProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtProjectApplication.class, args);
		System.out.println("Love OP");
	}

	@Bean
	public CommandLineRunner setupDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("edsonortega").isEmpty()) {
				User user = new User();
				user.setUsername("edsonortega");
				user.setPassword(passwordEncoder.encode("test123"));
				user.setHasMembership(true);
				userRepository.save(user);
			}
			if (userRepository.findByUsername("regularuser").isEmpty()){
				User user = new User();
				user.setUsername("regularuser");
				user.setPassword(passwordEncoder.encode("test123"));
				user.setHasMembership(false);
				userRepository.save(user);
			}else {
				System.out.println("Users already exists");
			}
		};
	}
}
