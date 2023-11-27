package com.example.dietcommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DietCommunityApplication {

  public static void main(String[] args) {
    SpringApplication.run(DietCommunityApplication.class, args);
  }

}
