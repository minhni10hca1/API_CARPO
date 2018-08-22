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
import model.Customer_CM;
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

import service.CampaignService;
import service.CustomerService;
import service.OtpService;
import service.TrackingService;
import service.UserService;
import util.FunctionUtil;
import util.GlobalUtils;

@RestController
public class Customer_CM_Controller {

	@Autowired
	CustomerService customerService;
	
	
	@Autowired
	TrackingService trackingService;

	@Autowired
	UserService serviceUser;
	public static String result_sms = "";
	public static Boolean status_result = false;

	@RequestMapping(value = "/get-list-customer", method = GET)
	public String getListCustomer() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		ArrayList<Customer_CM> listCampaign = new ArrayList<Customer_CM>();
		listCampaign = customerService.getListCustomer();
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
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;

	}
}
