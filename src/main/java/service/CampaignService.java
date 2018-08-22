package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import model.Campaign_CM;
import model.Car;
import model.Tracking;

@Service
public interface CampaignService {
	//Car getInfoCarByUserId(String user_id);
	org.bson.Document getInfoCampaignById(String campaign_id);
	//org.bson.Document getInfoCampaignByCustomerId(String customer_id);
	ArrayList<Campaign_CM> getListCampaignByCustomerId(String customer_id);
	ArrayList<Campaign_CM> getAllCampaign();
	ArrayList<Campaign_CM> getAllCampaignStillValidated();
	void calculator_campaign_part_driver(String campaign_id);
	ArrayList<org.bson.Document> getInfoCampaignPartDriverByCampaignId(String campaign_id);


}
