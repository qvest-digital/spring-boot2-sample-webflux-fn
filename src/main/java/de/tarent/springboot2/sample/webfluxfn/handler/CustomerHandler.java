package de.tarent.springboot2.sample.webfluxfn.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.error;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import de.tarent.springboot2.sample.webfluxfn.model.Customer;
import de.tarent.springboot2.sample.webfluxfn.model.Paging;
import de.tarent.springboot2.sample.webfluxfn.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomerHandler {
	private CustomerRepository repository;
	private ValidatingRequestHandler requestHandler;
	
	public CustomerHandler(CustomerRepository repository, ValidatingRequestHandler requestHandler) {
		this.repository = repository;
		this.requestHandler = requestHandler;
	}
	
	@PreAuthorize("hasRole('USER')")
	public Mono<ServerResponse> listCustomers(ServerRequest request) {
		// TODO: validate?
		Mono<PageRequest> pageRequest = request.body(BodyExtractors.toMono(Paging.class))
			.defaultIfEmpty(new Paging())
			.map(paging -> PageRequest.of(paging.getPage(), paging.getSize()));
		
		Flux<Customer> customers = pageRequest.flatMapMany(pr -> repository.findAll(pr));
		
		return ok().body(customers, Customer.class);
	}
	
	@PreAuthorize("hasRole('USER')")
	public Mono<ServerResponse> findCustomer(ServerRequest request) {
		return repository.findById(request.pathVariable("id"))
			.flatMap(customer -> ok().body(fromObject(customer)))
			.switchIfEmpty(notFound().build());
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<ServerResponse> createCustomer(ServerRequest request) {
		return requestHandler.requireValidBody(
			validRequestBody -> {
				return repository.insert(request.bodyToMono(Customer.class))
					.single()
					.flatMap(customer ->
						created(
							UriComponentsBuilder.fromUri(request.uri())
								.path("{id}")
								.build()
								.expand(customer.getId()).toUri()
						).body(fromObject(customer)));
			},
			request,
			Customer.class);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public Mono<ServerResponse> updateCustomer(ServerRequest request) {
		// TODO: validate
		return request.bodyToMono(Customer.class)
			.filter(customer -> request.pathVariable("id").equals(customer.getId()))
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
			.filterWhen(customer -> repository.findById(customer.getId()).hasElement())
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
			.flatMap(customer -> repository.save(customer))
			.flatMap(customer -> ok().body(fromObject(customer)));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
		return repository.findById(request.pathVariable("id"))
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
			.flatMap(customer -> {
				log.info("deleting {}", customer);
				return repository.delete(customer).then(Mono.just(customer));
			})
			.flatMap((deletedCustomer) -> { return noContent().build();});
	}
}
