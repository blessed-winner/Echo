package org.xenon.echo.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
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
   public void seedData(){}
}
