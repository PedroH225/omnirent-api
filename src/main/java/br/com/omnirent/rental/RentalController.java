package br.com.omnirent.rental;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
}
