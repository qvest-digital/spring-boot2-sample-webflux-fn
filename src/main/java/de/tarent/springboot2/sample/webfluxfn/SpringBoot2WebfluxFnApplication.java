package de.tarent.springboot2.sample.webfluxfn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import de.tarent.springboot2.sample.webfluxfn.repository.ReactiveMongoFindAllPageableRepositoryImpl;

@SpringBootApplication
@EnableReactiveMongoRepositories(repositoryBaseClass = ReactiveMongoFindAllPageableRepositoryImpl.class)
public class SpringBoot2WebfluxFnApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringBoot2WebfluxFnApplication.class, args);
	}
}
