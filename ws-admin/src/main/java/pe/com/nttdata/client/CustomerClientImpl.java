package pe.com.nttdata.client;

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
import pe.com.nttdata.model.Customer;
@Log4j2
@Service
public class CustomerClientImpl implements CustomerClientInf{
	@Autowired
	private RestTemplate restTemplate; 
	@Override
	public Customer findById(String id) {
		return this.restTemplate.getForObject("http://localhost:8882/customers/"+id, Customer.class);
	}
	@Override
	public Customer findByNumberDocument(String id) {
		return this.restTemplate.getForObject("http://localhost:8882/customers/numberDocument/"+id, Customer.class);
	}
	@Override
	public Customer saveCustomer(Customer request) {

		long tiempoInicio = System.currentTimeMillis();
		Customer response =new Customer(); 
		ObjectMapper objectMapper = new ObjectMapper();
		String responseJson = null;

		String url = "http://localhost:8882/customers";
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
			response = objectMapper.readValue(responseJson, Customer.class);

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

}
