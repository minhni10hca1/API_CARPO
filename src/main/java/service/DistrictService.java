package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import model.Car;
import model.Tracking;

@Service
public interface DistrictService {
	//Car getInfoCarByUserId(String user_id);
	ArrayList<org.bson.Document> getInfoDocumentDistrictByArea_Code(String area_code);
	ArrayList<String> getInfoDocumentDistrictCalculatorByArea_Code(String area_code);


}
