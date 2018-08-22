package controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.POST;

import model.Campaign_CM;
import model.Car;
import model.Global;
import model.GlobalErrorCode;
import model.GlobalMessageScreen;
import model.JsonFormatError;
import model.JsonFormatTemplate;
import model.LocationGoogle;
import model.Otp;
import model.Tracking;
import model.User;
import model_json.formatDataCampaignDetail_CM;
import model_json.formatJsonTrackingInfo;

import org.apache.hadoop.hdfs.server.namenode.status_jsp;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import org.hibernate.annotations.NotFound;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mysql.fabric.Response;

import service.AreaService;
import service.CampaignService;
import service.CarService;
import service.CustomerService;
import service.OtpService;
import service.TrackingService;
import service.UserService;
import util.FunctionUtil;
import util.GlobalUtils;

@RestController
public class Campaign_CM_Controller {

	@Autowired
	CampaignService campaignService;

	@Autowired
	TrackingService trackingService;

	@Autowired
	AreaService areaService;

	@Autowired
	CarService carService;

	@Autowired
	CustomerService customerService;

	@Autowired
	UserService serviceUser;
	public static String result_sms = "";
	public static Boolean status_result = false;

	@RequestMapping(value = "/get-list-campaign-by-customer-id", method = GET)
	public String getListCampaignByCustomer(
			@RequestParam(required = false) String customer_id) {
		if (customer_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		ArrayList<Campaign_CM> listCampaign = new ArrayList<Campaign_CM>();
		// get customer_id by user_id(customer_id)
		org.bson.Document documenCustomer = customerService
				.getDocumentCustomerByUser_id(customer_id);
		if (documenCustomer != null)
			customer_id = documenCustomer.get("_id").toString();
		else
			customer_id = "";
		if (!customer_id.isEmpty()) {
			listCampaign = campaignService
					.getListCampaignByCustomerId(customer_id);
			if (listCampaign.size() > 0) {
				// co data
				formatTemplate.setStatus(Global.status_ok);
				formatTemplate.setData(listCampaign);
			} else {
				// khong co data
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_find_data);
				error.setMessage(GlobalErrorCode.error_message_find_data);
				formatTemplate.setError(error);
			}
		} else {
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalErrorCode.error_message_find_data);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;

	}

	public double getDistanceTrackingByCampaign(String campaign_id) {
		double distanceresult = 0;
		ArrayList<org.bson.Document> arrayListCar = carService
				.getListCarByCampaign(campaign_id);
		for (org.bson.Document documentCar : arrayListCar) {
			String device_id = documentCar.get("device_id").toString();
			ArrayList<LocationGoogle> listLocation = trackingService
					.getListLocationTrackingByDevice_id(device_id);
			distanceresult += FunctionUtil
					.totalDistanceByListLocation(listLocation);
		}
		return distanceresult;
	}

	@RequestMapping(value = "/get-info-detail-campaign-by-campaign-id", method = GET)
	public String getInfoDetailCampaignByCampaignId(
			@RequestParam(required = false) String campaign_id) {
		if (campaign_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		formatDataCampaignDetail_CM campaignDetail = new formatDataCampaignDetail_CM();
		org.bson.Document documentCampaign = campaignService
				.getInfoCampaignById(campaign_id);
		if (documentCampaign != null) {
			// co data
			campaignDetail.setName(documentCampaign.get("name").toString());
			// fix
			//double distanceTotal = getDistanceTrackingByCampaign(campaign_id);
			// fix
			String value = "0.0";
			org.bson.Document documentResult = trackingService
					.getDocumentHomeCustomerByCampaignId(campaign_id);
			if (documentResult != null)
				value = documentResult.get("total_km_run").toString();			
			campaignDetail.setTotal_km_run(value);
			// end fix
			campaignDetail.setTotal_car(documentCampaign.get("total_car")
					.toString());
			campaignDetail.setTotal_car_advertising(documentCampaign.get(
					"total_car_advertising").toString());
			campaignDetail.setStart_time(documentCampaign.get("start_time")
					.toString());
			campaignDetail.setEnd_time(documentCampaign.get("end_time")
					.toString());
			campaignDetail.setArea_code(documentCampaign.get("area_code")
					.toString());
			org.bson.Document documentArea = areaService
					.getInfoAreaByCode(documentCampaign.get("area_code")
							.toString());
			if (documentArea != null)
				campaignDetail
						.setArea_name(documentArea.get("name").toString());
			formatTemplate.setStatus(Global.status_ok);
			formatTemplate.setData(campaignDetail);
		} else {
			// khong co data
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalErrorCode.error_message_find_data);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;

	}

}
