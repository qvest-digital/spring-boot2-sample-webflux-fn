package de.tarent.springboot2.sample.webfluxfn.repository;

import de.tarent.springboot2.sample.webfluxfn.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository
		extends ReactiveMongoFindAllPageableRepository<Customer, String> {
	Mono<Customer> findByFirstName(String firstName);
	Flux<Customer> findByLastName(String lastName);
}
