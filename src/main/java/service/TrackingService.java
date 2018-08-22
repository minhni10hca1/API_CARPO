package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import model.LocationGoogle;
import model.Tracking;
import model_json.formatDataLocation_Home_CM;
import model_json.formatDataUserRunMost_Home_CM;

@Service
public interface TrackingService {
	Boolean insertTracking(org.bson.Document tracking);
	Boolean insertTrackingMany(ArrayList<org.bson.Document> tracking);
	ArrayList<LocationGoogle> getListLocationTrackingByDate(String strDate);
	ArrayList<LocationGoogle> getListLocationTrackingByDateAndDevice_id(String strDate, String device_id);
	ArrayList<LocationGoogle> getListLocationTrackingByBigerDateAndDevice_id(String strDate, String device_id);
//	ArrayList<LocationGoogle> getListLocationTrackingByTwoDateAndDevice_id(String strFromDate,String strToDate, String device_id);
	double getSumDistanceTrackingByTwoDateAndDevice_id(String strFromDate,String strToDate, String device_id);
	//ArrayList<LocationGoogle> getListLocationTrackingByDateAndCar_idAndCampaign_id(String strDate, String car_id, String campaign_id);
	//ArrayList<LocationGoogle> getListLocationTrackingByCampaign(String campaign_id);
	//ArrayList<LocationGoogle> getListLocationTrackingByCar_id(String car_id);
	ArrayList<LocationGoogle> getListLocationTrackingByDevice_idAndDistrictCode(String device_id, String district_code);
	ArrayList<LocationGoogle> getListLocationTrackingByDevice_id(String device_id);
	Boolean checkCarIsRunning(String car_id);
	double getMaxDistanceByDevice_Id(String devive_id);
	double getMaxDistanceByDeviceIdAndDate(String devive_id, String date);
	LocationGoogle getEndPointByDeviceId(String devive_id);
	double getMaxDistanceByDevice_IdAndBigerDate(String devive_id, String date);
	double getMaxDistanceByListDevice_IdAndDistrictName(ArrayList<String> list_device_car,String districtName);
	double getCountPointByListDevice_IdAndDistrictNameAndTwoDay(ArrayList<String> list_device_car,String districtName, String startDate, String endDate);
	void getDistanceYesterDayAndistance30DaysBeforeByDevice_IdAndBigerDate(String devive_id, String date, String dateYesterDay);
	void getLatLongByDevice_IdAndDistance(String devive_id, String distance);
	void saveHomeCustomerToDatabase(String campaign_id,String total_km_run, String total_user_drive, ArrayList<formatDataUserRunMost_Home_CM> user_run_most, ArrayList<formatDataLocation_Home_CM> total_km_location);
	void deleteHomeCustomer();
	org.bson.Document getDocumentHomeCustomerByCampaignId(String campaign_id);
	// new
	ArrayList<LocationGoogle> getListLocationTrackingByTwoDateAndDevice_idAndOutCty(ArrayList<String> list_district_name_cty,String strFromDate,String strToDate, String device_id);


}
