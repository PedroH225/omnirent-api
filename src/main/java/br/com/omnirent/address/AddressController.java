package br.com.omnirent.address;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.security.SecurityUtils;
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
	
	@GetMapping("/user")
	public List<AddressResponseDTO> findUserAdresses() {
		return addressService.getUserAddresses(SecurityUtils.currentUserId());
	}
	
	@PostMapping
	public AddressResponseDTO addAddress(@RequestBody AddressRequestDTO addressDto) {
		return addressService.addAddress(addressDto, SecurityUtils.currentUserId());
	}
	
	@PutMapping
	public AddressResponseDTO updateAddress(@RequestBody AddressRequestDTO addressDto) {
		return addressService.updateAddress(addressDto);
	}
	
	@DeleteMapping("/{addressId}")
	public void deleteAddress(@PathVariable String addressId) {
		addressService.deleteAddress(addressId);
	}
	
}
