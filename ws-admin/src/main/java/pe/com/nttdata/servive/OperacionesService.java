package pe.com.nttdata.servive;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.com.nttdata.client.CustomerClientInf;
import pe.com.nttdata.client.HistoricoClientInf;
import pe.com.nttdata.client.ProductClientInf;
import pe.com.nttdata.client.TypeCustomerClientInf;
import pe.com.nttdata.model.Bank;
import pe.com.nttdata.model.Customer;
import pe.com.nttdata.model.CustomerRequest;
import pe.com.nttdata.model.CustomerResponse;
import pe.com.nttdata.model.GenericResponse;
import pe.com.nttdata.model.Historico;
import pe.com.nttdata.model.Product;
import pe.com.nttdata.model.TypeCustomer;
import pe.com.nttdata.model.TypeProduct;
import pe.com.nttdata.request.ProductRequest;
import pe.com.nttdata.response.ConsultaSaldo;
import pe.com.nttdata.util.Constantes;
import reactor.core.publisher.Mono;

@Service

public class OperacionesService {
	private static final Logger LOG = LoggerFactory.getLogger(OperacionesService.class);
	@Autowired
	private CustomerClientInf customerClientInf;

	@Autowired
	private TypeCustomerClientInf typeCustomerClientInf;

	@Autowired
	private ProductClientInf productClient;
	
	@Autowired
	private HistoricoClientInf historicoClientInf;
	String tipoOpe=null;
	double payment=0;
	public Historico save(Historico historico) {
		return this.historicoClientInf.save(historico);
	}

	public Mono<CustomerResponse> consultarCliente(String id) {
		CustomerResponse response = new CustomerResponse();
		Customer customer = this.customerClientInf.findById(id);
		List<TypeCustomer> lst = this.typeCustomerClientInf.searchByIdCustomer(customer.getId());
		List<Product> products = this.productClient.findByIdCustomers(customer.getId());
		customer.setTypeCustomers(lst);
		customer.setProducts(products);
		response.setCustomer(customer);
		LOG.info("Response del cliente: {}", response);
		return Mono.just(response);
	}

	public Product findByIdProduct(String id) {
		return this.productClient.findById(id);
	}
	
	public Mono<ConsultaSaldo>  consultarSaldo(String id) {
		ObjectMapper mapper = new ObjectMapper();
		ConsultaSaldo response =new ConsultaSaldo();
		Product product= this.productClient.findById(id);
		if (product!=null) {
			Customer customer = this.customerClientInf.findById(product.getIdCustomer());
			List<TypeProduct> typeProducts=productClient.getAllTypeProduct();
			List<TypeProduct> listTypeProduct = mapper.convertValue(typeProducts, new TypeReference<List<TypeProduct>>() {});
			for (TypeProduct typeProduct : listTypeProduct) {
				if (typeProduct.getId().equals(product.getIdTypeProduct())) {
					response.setType(typeProduct.getType());
					response.setAccount(typeProduct.getAccount());
				}
			}
			response.setSaldoTotal(product.getAmount());
			response.setNombreUsuario(customer.getName().concat(" ").concat(customer.getSurname()));
			response.setFechaConsulta(LocalDateTime.now());
			LOG.info("Response del cliente: {}", response);
		}
		
		return Mono.just(response);
	}

	public Mono<GenericResponse> operations(ProductRequest request) throws UnknownHostException {
		GenericResponse response=new GenericResponse();
		Product product;
		ObjectMapper mapper = new ObjectMapper();
		tipoOpe=request.getAction();
		payment = request.getAmount();
		List<TypeProduct> lstTypeProduct = this.productClient.getAllTypeProduct();
		LOG.info("Tamanio de lista TypeProduct: {}", lstTypeProduct.size());
		List<TypeProduct> typeProductList = mapper.convertValue(lstTypeProduct, new TypeReference<List<TypeProduct>>() {});
		for (TypeProduct typeProduct : typeProductList) {
			if (typeProduct.isStatus()) {
				product = this.findByIdProduct(request.getId());
				LOG.info("Producto: {}",product);
				if (product!=null) {
					product.setNumberOfMovements(product.getNumberOfMovements()+1);
					product.setAction(request.getAction());
					if (request.getAction().equalsIgnoreCase(Constantes.DEPOSITO)) {
						product.setAmount(product.getAmount()+request.getAmount());
						this.setHistoric(product);
					} else if (request.getAction().equalsIgnoreCase(Constantes.RETIRO)) {
						if (product.getAmount()>0 && product.getAmount()>=request.getAmount()) {
							product.setAmount(product.getAmount() - request.getAmount());
							this.setHistoric(product);
						}else {
							response.setCodRequest("-1");
							response.setMsgRequest("Salda insuficiente");
							return Mono.just(response);
						}
						
					}else if(request.getAction().equalsIgnoreCase(Constantes.PAGO)) {
						if (product.getAmount()>0 && product.getAmount()>=request.getAmount()) {
							product.setAmount(product.getAmount() - request.getAmount());
							this.setHistoric(product);
						}else {
							response.setCodRequest("-1");
							response.setMsgRequest("Salda insuficiente para pagar");
							return Mono.just(response);
						}
					}
					this.productClient.save(product);
					response.setCodRequest("0");
					response.setMsgRequest("Opreracion exitosa ".concat(product.getAction()));
					return Mono.just(response);
				}else {
					response.setCodRequest("-2");
					response.setMsgRequest("Not data: ");
				}
			}
		}
		return Mono.just(response);

	}
	
	public Mono<GenericResponse> operationBootCoin(CustomerRequest request) {
		GenericResponse response=new GenericResponse();
		
		Bank  bank =this.productClient.getAllBankId(request.getIdBank());
		if (bank!=null&& bank.getVirtualCurrency()>0 && bank.getVirtualCurrency()>=request.getValue()) {
			if (request.getAction().equals("compra")) {
				bank.setVirtualCurrency(bank.getVirtualCurrency()-request.getValue());
				this.productClient.saveBank(bank);
				Customer customer=customerClientInf.findByNumberDocument(request.getNurDocCustomer());
				customer.setValorBootCoin(customer.getValorBootCoin()+request.getValue());
				this.customerClientInf.saveCustomer(customer);
				response.setCodRequest("0");
				response.setMsgRequest("Operacion exitosa ".concat(customer.getNumberDocument()));
			}
				
		
			
		}else {
			response.setCodRequest("-1");
			response.setMsgRequest("Error");
		}
		
		return Mono.just(response);
	}
	
	private Historico setHistoric(Product product) throws UnknownHostException{
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		String formattedDateTime = currentDateTime.format(formatter);
		Historico historico=new Historico();
		historico.setMontoActual(product.getAmount());
		historico.setIdOpreracion(product.getId());
		historico.setCommission(0);
		historico.setNumberOfMovements(product.getNumberOfMovements());
//		if (tipoOpe.equals("pago")) {
//			historico.setPaymentAmount(payment);
//		}else {
//			historico.setPaymentAmount(0);
//		}
//		
		historico.setNumberOfCredit(0);
		historico.setLimitCredit(0);
		historico.setAction(product.getAction());
		historico.setIdTypeProduct(product.getIdTypeProduct());
		historico.setIdCustomer(product.getIdCustomer());
		historico.setFechaOperacion(formattedDateTime);
		historico.setDevice(Inet4Address.getLocalHost().getHostName());
		LOG.info("Historicos==========> {}",historico);
		return historicoClientInf.save(historico);
		
	}

}
