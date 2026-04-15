package br.com.omnirent.rental;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.rental.dto.RentalCreatedDTO;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.rental.dto.RentalRequestDTO;
import br.com.omnirent.security.SecurityUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/rental")
public class RentalController {

	private RentalService rentalService;
	
	@GetMapping("/find/{id}")
	public RentalDetailDTO findById(@PathVariable String id) {
		return rentalService.getRentalById(id);
	}
	
	@GetMapping("/find/rented")
	public List<RentalDisplayDTO> findUserRented() {
		return rentalService.findUserRented(SecurityUtils.currentUserId());
	}
	
	@GetMapping("/find/userRentals")
	public List<RentalDisplayDTO> findUserRentals() {
		return rentalService.findUserRentals(SecurityUtils.currentUserId());
	}
	
	@PostMapping
	public RentalCreatedDTO addRent(@RequestBody RentalRequestDTO rentalRequestDTO) {
		return rentalService.addRent(rentalRequestDTO, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/{rentId}/start-preparing")
	public RentalDisplayDTO startPreparing(@PathVariable String rentId) {
		return rentalService.startPreparing(rentId, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/{rentId}/ship")
	public RentalDisplayDTO ship(@PathVariable String rentId) {
		return rentalService.ship(rentId, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/{rentId}/in-use")
	public RentalDisplayDTO markInUse(@PathVariable String rentId) {
		return rentalService.markInUse(rentId, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/{rentId}/request-return")
	public RentalDisplayDTO requestReturn(@PathVariable String rentId) {
		return rentalService.requestReturn(rentId, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/{rentId}/return-shipped")
	public RentalDisplayDTO markReturnShipped(@PathVariable String rentId) {
		return rentalService.markReturnShipped(rentId, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/{rentId}/returned")
	public RentalDisplayDTO markReturned(@PathVariable String rentId) {
		return rentalService.markReturned(rentId, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/{rentId}/cancel")
	public RentalDisplayDTO cancel(@PathVariable String rentId) {
		return rentalService.cancel(rentId, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/{rentId}/confirm")
	public RentalDisplayDTO confirm(@PathVariable String rentId) {
		return rentalService.confirm(rentId, SecurityUtils.currentUserId());
	}
	
}
