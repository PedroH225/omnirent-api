package br.com.omnirent.address;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/address")
public class AddressController {

	private AddressService addressService;
	
	@GetMapping("/find/{id}")
	public Address findById(@PathVariable String id) {
		return addressService.findById(id);
	}
	
}
