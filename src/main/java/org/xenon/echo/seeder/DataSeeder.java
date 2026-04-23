package org.xenon.echo.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.entities.Topic;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.Role;
import org.xenon.echo.repositories.*;

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
       userRepository.save(user);

       User user2 = new User();
       user2.setName("Billy Butcher");
       user2.setEmail("butcher@echo.com");
       user2.setPassword("Password123");
       user2.setRole(Role.ADMIN);
       user2.setVerified(true);
       userRepository.save(user2);

       User user3 = new User();
       user3.setName("David Williams");
       user3.setEmail("wills@echo.com");
       user3.setPassword("Password123");
       user3.setVerified(true);
       userRepository.save(user3);

       User user4 = new User();
       user4.setName("Hugh Hill");
       user4.setEmail("hill@echo.com");
       user4.setPassword("Password123");
       user4.setVerified(true);
       userRepository.save(user4);

       //TOPIC
       Topic topic = new Topic();
       topic.setName("Programming");
       topic.setDescription("All dev stuff");
       topic.setUser(user3);
       topicRepository.save(topic);

       Topic topic2 = new Topic();
       topic2.setName("Anime");
       topic2.setDescription("Anime: particularly Shounen");
       topic2.setUser(user3);
       topicRepository.save(topic2);

       Topic topic3 = new Topic();
       topic3.setName("Music");
       topic3.setDescription("If not a walker, don't enter");
       topic3.setUser(user4);
       topicRepository.save(topic3);
   }
}
