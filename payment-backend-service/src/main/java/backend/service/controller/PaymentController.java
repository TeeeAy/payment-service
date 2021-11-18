package backend.service.controller;

import backend.service.service.PaymentService;
import backend.template.dto.PaymentDto;
import backend.template.dto.PaymentResponseDto;
import org.springframework.web.bind.annotation.*;


@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/start-payment")
    public PaymentResponseDto startPayment(@RequestBody PaymentDto paymentDto) {
        return paymentService.startPayment(paymentDto);
    }


    @GetMapping("/get-payment/{type}/{id}")
    public PaymentResponseDto getPayment(@PathVariable String type, @PathVariable String id) {
        return paymentService.getPayment(type, id);
    }


    @DeleteMapping("/cancel/{type}/{id}")
    public PaymentResponseDto cancelPayment(@PathVariable String type, @PathVariable String id) {
        return paymentService.cancelPayment(type, id);

    }

    @DeleteMapping("/refund/{type}/{id}")
    public PaymentResponseDto refundPayment(@PathVariable String type, @PathVariable String id) {
        return paymentService.refundPayment(type, id);
    }


    @PutMapping("/update-payment")
    public PaymentResponseDto updatePayment(@RequestBody PaymentDto paymentDto) {
        return paymentService.updatePayment(paymentDto);
    }


    @DeleteMapping("/capture-payment/{type}/{id}")
    public PaymentResponseDto capturePayment(@PathVariable String type, @PathVariable String id) {
        return paymentService.capturePayment(type, id);
    }

}
