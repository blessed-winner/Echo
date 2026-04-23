package org.xenon.echo.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xenon.echo.entities.*;
import org.xenon.echo.repositories.*;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
   private final UserRepository userRepository;
   private final SeedService seedService;

   @Bean
   CommandLineRunner seed(){
       return args -> {
           if (userRepository.count() == 0) {
               seedService.seedData();
           }
       };
   }
}
