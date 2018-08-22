package controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;

import model.Car;
import model.Global;
import model.GlobalErrorCode;
import model.JsonFormatError;
import model.JsonFormatTemplate;
import model.LocationGoogle;
import model.Tracking;
import model_json.formatDataGroupMember;
import model_json.formatDataInfoHome;
import model_json.formatJsonTrackingInfo;

import org.apache.hadoop.hdfs.server.namenode.status_jsp;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import org.bson.Document;
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
import com.mysql.fabric.Response;

import service.CarGroupService;
import service.CarService;
import service.TrackingService;
import service.UserService;
import util.FunctionUtil;
import util.GlobalUtils;

@RestController
public class CarGroupController {

	@Autowired
	CarService carService;

	@Autowired
	CarGroupService carGroupService;
	
	@Autowired
	TrackingService trackingService;

	@Autowired
	UserService userService;
	public static String result_sms = "";
	public static Boolean status_result = false;
	
	public Date addDateWithParams(int params, Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, params);
		return cal.getTime();
	}
	
	public double getTotalKmFromTwoDate(String strBeginDate,
			String strEndDate, String device_id) {
//		ArrayList<LocationGoogle> listLocation = trackingService
//				.getListLocationTrackingByTwoDateAndDevice_id(strBeginDate, strEndDate, device_id);
//		double distance = FunctionUtil
//				.totalDistanceByListLocation(listLocation);
//		distance = FunctionUtil.roundDouble(distance);
		double	distance = trackingService.getSumDistanceTrackingByTwoDateAndDevice_id(strBeginDate, strEndDate, device_id);
		return distance;
	}
	
	@RequestMapping(value = "/get-info-group-member-in-month", method = GET)
	public String getInfoGroupMem(@RequestParam(required=false) String leader_id) {
		if(leader_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document do_car_group = carGroupService
				.getCarGroupByLeaderId(leader_id);
		if (do_car_group != null) {
			String group_id = do_car_group.get("_id").toString();
			ArrayList<org.bson.Document> list_car = carService
					.getListCarByGroupId(group_id);
			if (list_car.size() > 0) {
				LocalDate today = LocalDate.now();
				LocalDate localDateStartMonth = today.withDayOfMonth(1);
				LocalDate localDateEndMonth = today.withDayOfMonth(today.lengthOfMonth());
				Date dateStartMonth =  Date.from(localDateStartMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
				Date dateEndMonth =  Date.from(localDateEndMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
				dateStartMonth = addDateWithParams(-1, dateStartMonth);
				dateEndMonth = addDateWithParams(1, dateEndMonth);
				ArrayList<formatDataGroupMember> arrayData = new ArrayList<formatDataGroupMember>();
				String name = "",phone = "";
				for (org.bson.Document documentCar : list_car){
					String user_id = documentCar.get("user_id").toString();
					org.bson.Document document_user = FunctionUtil.getInfoDocumentUserByUser_id(user_id);
					if(document_user != null){
						name = document_user.get("fullname").toString();
						phone = document_user.get("phone").toString();
					}
					String device_id = documentCar.get("device_id").toString();
					//String campaign_id = documentCar.get("campaign_id").toString();
					// total km in month
					double kmInMonth = getTotalKmFromTwoDate(GlobalUtils.convertStringToDate(dateStartMonth),GlobalUtils.convertStringToDate(dateEndMonth),device_id);
					formatDataGroupMember dataInfoGroupMember = new formatDataGroupMember();
					dataInfoGroupMember.setName(name);
					dataInfoGroupMember.setTotal_km_month(kmInMonth);
					//dataInfoGroupMember.setDriver_id(documentCar.get("driver_id").toString());
					dataInfoGroupMember.setUser_id(user_id);
					dataInfoGroupMember.setPhone(phone);					
					arrayData.add(dataInfoGroupMember);
				}
				formatTemplate.setStatus(Global.status_ok);
				formatTemplate.setData(arrayData);
				
			} else {
				// ko co data
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_find_data);
				error.setMessage(GlobalErrorCode.error_message_find_data);
				formatTemplate.setError(error);
			}
		} else {
			// k co data
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
