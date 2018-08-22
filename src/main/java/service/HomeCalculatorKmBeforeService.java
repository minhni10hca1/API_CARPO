package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import model.Car;
import model.LocationGoogle;
import model.Tracking;

@Service
public interface HomeCalculatorKmBeforeService {
	org.bson.Document getInfoHomeCalculatorKmBeforeCampaignAndDeviceId(String campaign_id, String device_id);
	void deleteHomeCalculatorKmBefore(String campaign_id);
	public Boolean updateEndPointForDriver(String campaign_id, String device_id, LocationGoogle endPoint, org.bson.Document oldData, String created_date_location, String created_time_location);
	


}
