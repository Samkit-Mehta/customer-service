package com.shopping.online.customerservice.resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopping.online.customerservice.model.Customer;
import com.shopping.online.customerservice.repository.CustomerRepository;

@RestController
@RequestMapping("/customerservice")
public class CustomerResource {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private KafkaTemplate<String, Customer> kafkaTemplate;
	
	private static final String TOPIC = "customer-created";
	
	@GetMapping("/customers")
	public List<Customer> getCustomers() {
		return customerRepository.findAll();
	}

	@PostMapping("/customers")
	public String add(@RequestBody final Customer customer) {
		Customer SavedCustomer = customerRepository.save(customer);
		kafkaTemplate.send(TOPIC, SavedCustomer);
		return "Customer Added Successfully";
	}

	@GetMapping("/customers/{id}")
	public Optional<Customer> getById(@PathVariable("id") Integer id) {
		return customerRepository.findById(id);
	}

	@PutMapping("customers/{id}")
	public String updateCustomer(@RequestBody Customer newCustomer, @PathVariable("id") Integer id) {
		Optional<Customer> customers = customerRepository.findById(id);
		if (customers != null) {
			Customer customer = customers.get();
			customer.setEmail(newCustomer.getEmail());
			customer.setFirstName(newCustomer.getFirstName());
			customer.setLastName(newCustomer.getLastName());

			if (customerRepository.save(customer).getId().equals(id)) {
				return "Customer updated successfully";
			}
		}
		return "Error updating Customer";
	}

	@DeleteMapping("customers/{id}")
	public String deleteCustomer(@PathVariable("id") Integer id) {
		try {
			customerRepository.deleteById(id);
			return "Customer deleted successfully";
		} catch (Exception e) {
			return "Error Deleting Customer";
		}
	}

	@GetMapping("/customersfirstname")
	public List<String> getFirstNames() {
		return customerRepository.findAll().stream().map(customer -> {
			return customer.getFirstName();
		}).collect(Collectors.toList());

	}
}
