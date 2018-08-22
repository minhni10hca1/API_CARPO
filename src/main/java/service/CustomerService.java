package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import model.Campaign_CM;
import model.Car;
import model.Customer_CM;
import model.Tracking;

@Service
public interface CustomerService {
	//Car getInfoCarByUserId(String user_id);
	ArrayList<Customer_CM> getListCustomer();
	org.bson.Document getDocumentCustomerByUser_id (String user_id);
	


}
