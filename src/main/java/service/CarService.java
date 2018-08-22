package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import model.Car;
import model.LocationGoogle;
import model.Tracking;

@Service
public interface CarService {
	//Car getInfoCarByUserId(String user_id);
	ArrayList<org.bson.Document> getAllCar();
	ArrayList<org.bson.Document> getListCarByGroupId(String group_id);
	ArrayList<org.bson.Document> getListCarByCampaign(String campaign_id);
	org.bson.Document getInfoDocumentCarByDevice_Id(String device_id);
	void calculatorHomeKmBefore(String campaign_id, String user_id, String device_id, double total_km_yesterday, double total_km_30_day_before,LocationGoogle locationGoogle,double totalKMInCampaignPartDriver,double totalKmToDay,double totalKMInThreeDayBefore,double totalKMInSevenDayBefore,double persent, double totalKMInCampaignPartDriverOutCty );


}
