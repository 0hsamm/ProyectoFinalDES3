package co.edu.unbosque.proyectofinal;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProyectofinalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectofinalApplication.class, args);
	}

	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
		
	}
	
}
