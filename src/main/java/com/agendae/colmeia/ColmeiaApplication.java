package com.agendae.colmeia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ColmeiaApplication {

	public static void main(String[] args) {
        SpringApplication.run(ColmeiaApplication.class, args);
	}

}
