package br.com.omnirent.payment;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.payment.dto.PaymentHistoryResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	
	@GetMapping("/history/{rentalId}")
	public List<PaymentHistoryResponse> getPaymentHistory(@PathVariable String rentalId) {
		return paymentService.getPaymentHistory(rentalId);
	}
}
