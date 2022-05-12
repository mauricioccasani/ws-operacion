package pe.com.nttdata.model;

import lombok.Data;

@Data
public class CustomerRequest {
	private String nurDocCustomer;
	private String idBank;
	private Double value;
	private String action;
	
}
