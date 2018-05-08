package de.tarent.springboot2.sample.webfluxfn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import de.tarent.springboot2.sample.webfluxfn.handler.CustomerHandler;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CustomerRoutingConfiguration {
	@Bean
	public RouterFunction<ServerResponse> monoRouterFunction(CustomerHandler handler) {
		return nest(path("/customers"),
			nest(path("/{id}"),
				route(method(HttpMethod.GET).and(accept(APPLICATION_JSON)), handler::findCustomer)
					.andRoute(method(HttpMethod.PUT).and(accept(APPLICATION_JSON)), handler:: updateCustomer)
					.andRoute(method(HttpMethod.DELETE), handler::deleteCustomer)
				)
				.andRoute(method(HttpMethod.GET), handler::listCustomers)
				.andRoute(method(HttpMethod.POST).and(accept(APPLICATION_JSON)), handler::createCustomer)
		);
	}
}
