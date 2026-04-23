package org.xenon.echo.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.entities.Note;
import org.xenon.echo.entities.Tag;
import org.xenon.echo.entities.Topic;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.Role;
import org.xenon.echo.repositories.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
   private final UserRepository userRepository;
   private final NoteRepository noteRepository;
   private final TagRepository tagRepository;
   private final TopicRepository topicRepository;
   private final PasswordEncoder passwordEncoder;

   @Bean
   CommandLineRunner seed(){
       return args -> {
           if (userRepository.count() == 0) {
               seedData();
           }
       };
   }

   @Transactional
   public void seedData(){
       // USERS (Non-Admins: user3 and user4)
       User user1 = new User();
       user1.setName("Daniel Romans");
       user1.setEmail("romans@echo.com");
       user1.setPassword(passwordEncoder.encode("Password123"));
       user1.setRole(Role.ADMIN);
       user1.setVerified(true);

       User user2 = new User();
       user2.setName("Billy Butcher");
       user2.setEmail("butcher@echo.com");
       user2.setPassword(passwordEncoder.encode("Password123"));
       user2.setRole(Role.ADMIN);
       user2.setVerified(true);

       User user3 = new User();
       user3.setName("David Williams");
       user3.setEmail("wills@echo.com");
       user3.setPassword(passwordEncoder.encode("Password123"));
       user3.setRole(Role.USER);
       user3.setVerified(true);

       User user4 = new User();
       user4.setName("Hugh Hill");
       user4.setEmail("hill@echo.com");
       user4.setPassword(passwordEncoder.encode("Password123"));
       user4.setRole(Role.USER);
       user4.setVerified(true);

       userRepository.saveAll(List.of(user1, user2, user3, user4));

       // TOPICS
       Topic progTopic = new Topic();
       progTopic.setName("Programming");
       progTopic.setDescription("All dev stuff");
       progTopic.setUser(user3);

       Topic animeTopic = new Topic();
       animeTopic.setName("Anime");
       animeTopic.setDescription("Anime: particularly Shounen");
       animeTopic.setUser(user3);

       Topic musicTopic = new Topic();
       musicTopic.setName("Music");
       musicTopic.setDescription("If not a walker, don't enter");
       musicTopic.setUser(user4);

       topicRepository.saveAll(List.of(progTopic, animeTopic, musicTopic));

       // TAGS for user3
       Tag javaTag = new Tag();
       javaTag.setName("Java");
       javaTag.setUser(user3);

       Tag springTag = new Tag();
       springTag.setName("Spring");
       springTag.setUser(user3);

       Tag nestTag = new Tag();
       nestTag.setName("NestJS");
       nestTag.setUser(user3);

       // TAGS for user4
       Tag edmTag = new Tag();
       edmTag.setName("EDM");
       edmTag.setUser(user4);

       Tag rockTag = new Tag();
       rockTag.setName("Rock");
       rockTag.setUser(user4);

       tagRepository.saveAll(List.of(javaTag, springTag, nestTag, edmTag, rockTag));

       // NOTES for User 3 (Programming & Anime)
       Note note1 = new Note();
       note1.setTitle("Spring Boot Security");
       note1.setContent("Spring Security provides a robust framework for authentication and authorization. " +
               "By implementing a custom OncePerRequestFilter, we can intercept incoming JWT tokens, " +
               "validate them against our secret key, and populate the SecurityContextHolder with user details " +
               "to enable protected route access across the application. This ensures that only authenticated " +
               "and authorized users can access sensitive endpoints, maintaining the integrity and security of the system.");
       note1.setTopic(progTopic);
       note1.setTags(Set.of(javaTag, springTag));

       Note note2 = new Note();
       note2.setTitle("NestJS Architecture");
       note2.setContent("NestJS is built on top of Express (or Fastify) and brings a modular architecture inspired by Angular. " +
               "It uses Decorators to define metadata for Classes, allowing for efficient Dependency Injection. " +
               "The core pillars are Modules, which organize code; Controllers, which handle requests; and Providers, " +
               "which encapsulate business logic. This structure promotes maintainability, scalability, and testability " +
               "in large-scale applications.");
       note2.setTopic(progTopic);
       note2.setTags(Set.of(nestTag));

       Note note3 = new Note();
       note3.setTitle("One Piece: Haki Levels");
       note3.setContent("Haki is a mysterious power that allows users to utilize their spiritual energy. " +
               "Observation Haki grants extrasensory perception, Armament Haki allows for physical hardening and " +
               "attacking Logia users, while Conqueror's Haki—the rarest form—allows the user to exert their willpower " +
               "over others, even knocking them unconscious without physical contact. Mastering these forms is crucial " +
               "for navigating the New World and challenging the strongest pirates.");
       note3.setTopic(animeTopic);

       // NOTES for User 4 (Music)
       Note note4 = new Note();
       note4.setTitle("Best EDM Tracks 2024");
       note4.setContent("Electronic Dance Music continues to evolve with high-energy sub-genres like Melodic Techno " +
               "and Phonk dominating the charts. Tracks like 'Stardust' and 'Neon Pulse' are essential for high-focus " +
               "deep work sessions, providing rhythmic consistency that helps maintain cognitive flow during long " +
               "coding or study periods. The intricate beats and synthesizers create an immersive soundscape perfect " +
               "for concentration.");
       note4.setTopic(musicTopic);
       note4.setTags(Set.of(edmTag));

       Note note5 = new Note();
       note5.setTitle("Rock Classics");
       note5.setContent("The golden age of rock from the late 60s to the 80s defined modern music. " +
               "Bands like Led Zeppelin introduced heavy blues-rock riffs, while Queen pushed the boundaries " +
               "with operatic arrangements in songs like Bohemian Rhapsody. These classics remain timeless, " +
               "influencing countless artists across genres even decades later, proving their enduring appeal and " +
               "musical innovation.");
       note5.setTopic(musicTopic);
       note5.setTags(Set.of(rockTag));

       noteRepository.saveAll(List.of(note1, note2, note3, note4, note5));
   }
}
