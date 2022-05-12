package pe.com.nttdata.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;
import pe.com.nttdata.model.Bank;
import pe.com.nttdata.model.Product;
import pe.com.nttdata.model.TypeProduct;
@Service
@Log4j2
public class ProductClientImpl implements ProductClientInf{
	@Autowired
	private RestTemplate restTemplate; 
	

	
	@Override
	public Product  findByIdCustomer(String id) {
		return this.restTemplate.getForObject("http://localhost:8882/products/findByIdCustomer/"+id, Product.class);
	}
	
	
	
	@Override
	public Product findById(String id) {
		return this.restTemplate.getForObject("http://localhost:8882/products/"+id, Product.class);
	}
	
	@Override
	public Product save(Product product) {
		long tiempoInicio = System.currentTimeMillis();
		Product response =new Product(); 
		ObjectMapper objectMapper = new ObjectMapper();
		String responseJson = null;

		String url = "http://localhost:8882/products";
		Integer timeoutConexion= 4;
		Integer timeoutEjecucion = 4;

		try {

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	
			

			log.info("URL product: {}",  url);
			log.info("Tipo: {}", HttpMethod.POST);
			Gson gson = new Gson();
			String requesGson= gson.toJson(product);
			log.info("reques: {}",requesGson);
			HttpEntity<String> httpEntity = new HttpEntity<>(requesGson,httpHeaders);

			SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
			httpRequestFactory.setConnectTimeout(timeoutConexion);
			httpRequestFactory.setReadTimeout(timeoutEjecucion);

			log.info("Timeout conexion (ms): " + timeoutConexion);
			log.info("Timeout ejecucion (ms): " + timeoutEjecucion);

		
			RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
			ResponseEntity<String> responseEntity = restTemplate.exchange(
					url, HttpMethod.POST, httpEntity, String.class);

			log.info("Servicio REST ejecutado");

			responseJson = responseEntity.getBody();
			response = objectMapper.readValue(responseJson, Product.class);

		}catch (HttpClientErrorException e) {
			e.printStackTrace();
		
			log.error("Codigo Error:"+ e.getStatusCode());
			log.error("Mensaje Error:"+ e.getResponseBodyAsString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error en modificar: " + e.getMessage(), e);

		} finally {
			log.info("Datos de Salida:\n " + responseJson);
			log.info("Tiempo invocacion: " + (System.currentTimeMillis() - tiempoInicio)
					+ " milisegundos");
		}

		return response;
	
	}



	
	@Override
	public List<TypeProduct> getAllTypeProduct() {
		return this.restTemplate.getForObject("http://localhost:8882/type-products", List.class);
	}



	@Override
	public List<Product> findByIdCustomers(String id) {
		return this.restTemplate.getForObject("http://localhost:8882/products/findByIdCustomers/"+id, List.class);
	}



	@Override
	public Bank saveBank(Bank request) {
		long tiempoInicio = System.currentTimeMillis();
		Bank response =new Bank(); 
		ObjectMapper objectMapper = new ObjectMapper();
		String responseJson = null;

		String url = "http://localhost:8882/products/bank";
		Integer timeoutConexion= 4;
		Integer timeoutEjecucion = 4;

		try {

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	
			

			log.info("URL product: {}",  url);
			log.info("Tipo: {}", HttpMethod.POST);
			Gson gson = new Gson();
			String requesGson= gson.toJson(request);
			log.info("reques: {}",requesGson);
			HttpEntity<String> httpEntity = new HttpEntity<>(requesGson,httpHeaders);

			SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
			httpRequestFactory.setConnectTimeout(timeoutConexion);
			httpRequestFactory.setReadTimeout(timeoutEjecucion);

			log.info("Timeout conexion (ms): " + timeoutConexion);
			log.info("Timeout ejecucion (ms): " + timeoutEjecucion);

		
			RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
			ResponseEntity<String> responseEntity = restTemplate.exchange(
					url, HttpMethod.POST, httpEntity, String.class);

			log.info("Servicio REST ejecutado");

			responseJson = responseEntity.getBody();
			response = objectMapper.readValue(responseJson, Bank.class);

		}catch (HttpClientErrorException e) {
			e.printStackTrace();
		
			log.error("Codigo Error:"+ e.getStatusCode());
			log.error("Mensaje Error:"+ e.getResponseBodyAsString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error en modificar: " + e.getMessage(), e);

		} finally {
			log.info("Datos de Salida:\n " + responseJson);
			log.info("Tiempo invocacion: " + (System.currentTimeMillis() - tiempoInicio)
					+ " milisegundos");
		}

		return response;
	
	}



	@Override
	public Bank getAllBankId(String id) {
		return this.restTemplate.getForObject("http://localhost:8882/products/banks/"+id, Bank.class);
	}

	

	
}
