package de.tarent.springboot2.sample.webfluxfn.handler;

import java.util.function.Function;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class ValidatingRequestHandler {
	private Validator validator;
	
	public ValidatingRequestHandler(Validator validator) {
		this.validator = validator;
	}
	
	public <Body> Mono<ServerResponse> requireValidBody(
			Function<Mono<Body>, Mono<ServerResponse>> onSuccess,
			ServerRequest request,
			Class<Body> requestBodyClass) {
		return request.bodyToMono(requestBodyClass)
			.flatMap(requestBody -> {
				DataBinder binder = new DataBinder(requestBody);
				binder.setValidator(validator);
				binder.validate();
				
				if (binder.getBindingResult().hasErrors()) {
					return ServerResponse.badRequest()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(BodyInserters.fromObject(binder.getBindingResult().getAllErrors()));
				} else {
					return onSuccess.apply(Mono.just(requestBody));
				}
			});
	}
}
