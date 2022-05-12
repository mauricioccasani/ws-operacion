package pe.com.nttdata.controller;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import pe.com.nttdata.model.CustomerRequest;
import pe.com.nttdata.model.CustomerResponse;
import pe.com.nttdata.model.GenericResponse;
import pe.com.nttdata.request.ProductRequest;
import pe.com.nttdata.response.ConsultaSaldo;
import pe.com.nttdata.servive.OperacionesService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/servicios")
@Log4j2
public class ServicioController {
	
	@Autowired
	private OperacionesService customerService;

	
	@GetMapping("/customer/{id}")
	public Mono<CustomerResponse> getCustomer(@PathVariable String id) {
		return this.customerService.consultarCliente(id);
	}


	
	@PostMapping
	public Mono<GenericResponse>  getCustomerOpreracion(@RequestBody ProductRequest request) throws UnknownHostException {
		return  this.customerService.operations(request);
	}
	@GetMapping("/saldo/{id}")
	public Mono<ConsultaSaldo> total(@PathVariable String id) {
		return this.customerService.consultarSaldo(id);
	}
	
	@PostMapping("/bank")
	public Mono<GenericResponse>  getBankOpreracion(@RequestBody CustomerRequest request)  {
		log.info("Rquest======================================> {}",request);
		return this.customerService.operationBootCoin(request);
	}
	
	


}
