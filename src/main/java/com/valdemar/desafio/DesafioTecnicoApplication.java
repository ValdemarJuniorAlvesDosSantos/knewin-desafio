package com.valdemar.desafio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.valdemar.desafio")
public class DesafioTecnicoApplication {

	public static void main(String[] args) {		
		SpringApplication.run(DesafioTecnicoApplication.class, args);
	}

}
