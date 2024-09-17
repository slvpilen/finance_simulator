package springboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import springboot.login.dtos.SignUpDto;
import springboot.login.services.UserService;

/**
 * This class is used to start the Spring Boot application. When the application
 * is started, the Spring Boot application will automatically start the server.
 */
@EnableScheduling
@SpringBootApplication

public class FinanceApp {

  public static void main(String[] args) {

    SpringApplication.run(FinanceApp.class, args);
  }

  @Bean
  public CommandLineRunner init(UserService userService) {
    return args -> {

      SignUpDto user1 = new SignUpDto("Oskar", "Wavold",
          "slvpilen", "jeglikereggogbacon".toCharArray());
      SignUpDto user2 = new SignUpDto("Kjetil", "Rånes",
          "kjetil", "norgeirødthvittogblått".toCharArray());
      try {
        userService.register(user1);
      } catch (Exception e) {
        System.out.println("User1 already exists");
      }
      try {
        userService.register(user2);
      } catch (Exception e) {
        System.out.println("User2 already exists");
      }
    };

  }
}