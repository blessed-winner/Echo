package org.xenon.echo.seeder;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.entities.*;
import org.xenon.echo.enums.Role;
import org.xenon.echo.repositories.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class SeedService {
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final TopicRepository topicRepository;
    private final MemoryItemRepository memoryItemRepository;
    private final PasswordEncoder passwordEncoder;
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

        // MEMORY ITEMS for User 3
        MemoryItem item1 = new MemoryItem();
        item1.setText("What is the role of OncePerRequestFilter in Spring Security?");
        item1.setSource("Spring Boot Security Note");
        item1.setUser(user3);
        item1.setNote(note1);
        item1.setTags(Set.of(springTag));
        item1.setInterval(1);
        item1.setEaseFactor(2.5f);
        item1.setReviewCount(0);
        item1.setNextReviewDate(LocalDateTime.now());

        MemoryItem item2 = new MemoryItem();
        item2.setText("Explain Dependency Injection in NestJS.");
        item2.setSource("NestJS Architecture Note");
        item2.setUser(user3);
        item2.setNote(note2);
        item2.setTags(Set.of(nestTag));
        item2.setInterval(1);
        item2.setEaseFactor(2.5f);
        item2.setReviewCount(0);
        item2.setNextReviewDate(LocalDateTime.now());

        MemoryItem item3 = new MemoryItem();
        item3.setText("What are the three types of Haki in One Piece?");
        item3.setSource("One Piece Note");
        item3.setUser(user3);
        item3.setNote(note3);
        item3.setInterval(1);
        item3.setEaseFactor(2.5f);
        item3.setReviewCount(0);
        item3.setNextReviewDate(LocalDateTime.now());

        // MEMORY ITEMS for User 4
        MemoryItem item4 = new MemoryItem();
        item4.setText("Which sub-genres are dominating EDM in 2024?");
        item4.setSource("EDM Note");
        item4.setUser(user4);
        item4.setNote(note4);
        item4.setTags(Set.of(edmTag));
        item4.setInterval(1);
        item4.setEaseFactor(2.5f);
        item4.setReviewCount(0);
        item4.setNextReviewDate(LocalDateTime.now());

        MemoryItem item5 = new MemoryItem();
        item5.setText("Who performed Bohemian Rhapsody?");
        item5.setSource("Rock Classics Note");
        item5.setUser(user4);
        item5.setNote(note5);
        item5.setTags(Set.of(rockTag));
        item5.setInterval(1);
        item5.setEaseFactor(2.5f);
        item5.setReviewCount(0);
        item5.setNextReviewDate(LocalDateTime.now().minusDays(1)); // Overdue for testing

        memoryItemRepository.saveAll(List.of(item1, item2, item3, item4, item5));
    }
}
