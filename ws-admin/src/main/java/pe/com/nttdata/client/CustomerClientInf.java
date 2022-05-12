package pe.com.nttdata.client;

import pe.com.nttdata.model.Customer;

public interface CustomerClientInf {
	public Customer  findById(String id);
	public Customer saveCustomer(Customer request);
	public Customer findByNumberDocument(String id);
}
