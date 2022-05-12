package pe.com.nttdata.model;

import lombok.Data;


@Data

public class Bank {

	private String id;
	//nombre bank 
	private String nameBank;
	//moneda virtual total
	private Double virtualCurrency;
	
}
