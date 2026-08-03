package br.com.omnirent.rental;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.common.enums.RentalEnums;
import br.com.omnirent.common.page.PageResponseDTO;
import br.com.omnirent.rental.dto.RentalCreatedDTO;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.rental.dto.RentalRequestDTO;
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
	public PageResponseDTO<RentalDisplayDTO> findUserRented(Pageable pageable) {
		return rentalService.findUserRented(pageable);
	}
	
	@GetMapping("/find/userRentals")
	public PageResponseDTO<RentalDisplayDTO> findUserRentals(Pageable pageable) {
		return rentalService.findUserRentals(pageable);
	}
	
	@GetMapping("/enums")
	public RentalEnums getEnums() {
		return rentalService.getEnums();
	}
	@PostMapping
	public RentalCreatedDTO addRent(@RequestBody RentalRequestDTO rentalRequestDTO) {
		return rentalService.addRent(rentalRequestDTO);
	}
	
	@PatchMapping("/{rentId}/start-preparing")
	public void startPreparing(@PathVariable String rentId) {
		rentalService.startPreparing(rentId);
	}
	
	@PatchMapping("/{rentId}/ship")
	public void ship(@PathVariable String rentId) {
		rentalService.ship(rentId);
	}
	
	@PatchMapping("/{rentId}/in-use")
	public RentalDisplayDTO markInUse(@PathVariable String rentId) {
		return rentalService.markInUse(rentId);
	}
	
	@PatchMapping("/{rentId}/request-return")
	public void requestReturn(@PathVariable String rentId) {
		rentalService.requestReturn(rentId);
	}
	
	@PatchMapping("/{rentId}/return-shipped")
	public void markReturnShipped(@PathVariable String rentId) {
		rentalService.markReturnShipped(rentId);
	}
	
	@PatchMapping("/{rentId}/returned")
	public void markReturned(@PathVariable String rentId) {
		rentalService.markReturned(rentId);
	}
	
	@PatchMapping("/{rentId}/cancel")
	public void cancel(@PathVariable String rentId) {
		rentalService.cancel(rentId);
	}
}
