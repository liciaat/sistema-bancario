package br.com.ufca.sixsevenpayapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SixSevenPayApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SixSevenPayApiApplication.class, args);
	}

}
