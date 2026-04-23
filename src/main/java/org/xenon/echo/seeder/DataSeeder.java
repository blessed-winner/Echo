package org.xenon.echo.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.entities.Note;
import org.xenon.echo.entities.Tag;
import org.xenon.echo.entities.Topic;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.Role;
import org.xenon.echo.repositories.*;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
   private final UserRepository userRepository;
   private final NoteRepository noteRepository;
   private final MemoryItemRepository memoryItemRepository;
   private final TagRepository tagRepository;
   private final TopicRepository topicRepository;

   CommandLineRunner seed(){
       return args -> {
           seedData();
       };
   }
   @Transactional
   public void seedData(){
       //USER
       User user = new User();
       user.setName("Daniel Romans");
       user.setEmail("romans@echo.com");
       user.setPassword("Password123");
       user.setRole(Role.ADMIN);
       user.setVerified(true);

       User user2 = new User();
       user2.setName("Billy Butcher");
       user2.setEmail("butcher@echo.com");
       user2.setPassword("Password123");
       user2.setRole(Role.ADMIN);
       user2.setVerified(true);

       User user3 = new User();
       user3.setName("David Williams");
       user3.setEmail("wills@echo.com");
       user3.setPassword("Password123");
       user3.setVerified(true);

       User user4 = new User();
       user4.setName("Hugh Hill");
       user4.setEmail("hill@echo.com");
       user4.setPassword("Password123");
       user4.setVerified(true);

       userRepository.saveAll(Set.of(user,user2,user3,user4));

       //TOPIC
       Topic topic = new Topic();
       topic.setName("Programming");
       topic.setDescription("All dev stuff");
       topic.setUser(user3);


       Topic topic2 = new Topic();
       topic2.setName("Anime");
       topic2.setDescription("Anime: particularly Shounen");
       topic2.setUser(user3);

       Topic topic3 = new Topic();
       topic3.setName("Music");
       topic3.setDescription("If not a walker, don't enter");
       topic3.setUser(user4);

       topicRepository.saveAll(Set.of(topic,topic2,topic3));

       //TAG
       Tag tag1 = new Tag();
       tag1.setName("Java");
       tag1.setUser(user3);

       Tag tag2 = new Tag();
       tag2.setName("Spring");
       tag2.setUser(user3);

       Tag tag3 = new Tag();
       tag3.setName("NestJS");
       tag3.setUser(user3);

       Tag tag4 = new Tag();
       tag4.setName("EDM-music");
       tag4.setUser(user4);

       Tag tag5 = new Tag();
       tag5.setName("Shounen");
       tag5.setUser(user4);


       //NOTE
       Note note = new Note();
   }
}
