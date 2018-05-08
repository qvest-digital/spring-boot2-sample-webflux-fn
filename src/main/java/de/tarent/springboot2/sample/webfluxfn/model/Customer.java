package de.tarent.springboot2.sample.webfluxfn.model;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document
public class Customer implements Cloneable {
	@Id
	private String id;
	
	@NotEmpty
	private String firstName;
	@NotEmpty
	private String lastName;
	
	public Customer(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	@Override
	public Customer clone() {
		Customer clone = new Customer();
		clone.setId(id);
		clone.setFirstName(firstName);
		clone.setLastName(lastName);
		
		return clone;
	}
}
