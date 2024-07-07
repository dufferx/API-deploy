package org.luismore.hlvsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class HlvsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HlvsApiApplication.class, args);
	}

}
