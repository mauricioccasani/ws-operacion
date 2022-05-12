package pe.com.nttdata.model;

import java.util.List;

import lombok.Data;

@Data
public class Customer {
	private String id;
	private String name;
	private String surname;
	private String numberDocument;
	private String phone;
	private String email;
	private double valorBootCoin;
	private List<TypeCustomer>typeCustomers;
	private List<Product>  products;
}
