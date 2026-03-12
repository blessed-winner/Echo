package org.xenon.knowspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KnowSpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowSpaceApplication.class, args);
        System.out.print(SpringBootVersion.getVersion());
    }

}
