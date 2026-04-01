package br.com.omnirent.rental;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.rental.domain.RentalRequestDTO;
import br.com.omnirent.rental.domain.RentalResponseDTO;
import br.com.omnirent.security.SecurityUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/rental")
public class RentalController {

	private RentalService rentalService;
	
	@GetMapping("/find/{id}")
	public RentalResponseDTO findById(@PathVariable String id) {
		return rentalService.getRentalById(id);
	}
	
	@PostMapping
	public RentalResponseDTO addRent(@RequestBody RentalRequestDTO rentalRequestDTO) {
		return rentalService.addRent(rentalRequestDTO, SecurityUtils.currentUserId());
	}
	
	@PatchMapping("/updateStatus/{rentId}/{status}")
	public RentalResponseDTO updateStatus(@PathVariable String rentId, @PathVariable String status) {
		return rentalService.updateStatus(rentId, status);
	}
}
