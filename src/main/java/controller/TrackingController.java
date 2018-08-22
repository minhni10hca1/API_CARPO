package controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
import model.Tracking;
import model.User;
import model_json.distance_list;
import model_json.earning_list;
import model_json.formatDataGroupMember;
import model_json.formatDataInfoHome;
import model_json.formatDataInfoHomeManager;
import model_json.formatDataInfoUserDriverCar_CM;
import model_json.formatDataLocation_Home_CM;
import model_json.formatDataUserRunMost_Home_CM;
import model_json.formatJsonHistoryTrackingDataInfo;
import model_json.formatJsonHomeCustomer_CM;
import model_json.formatJsonTrackingInfo;

import org.apache.catalina.mbeans.GlobalResourcesLifecycleListener;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mysql.fabric.Response;

import service.CampaignService;
import service.CarGroupService;
import service.CarService;
import service.CustomerService;
import service.DistrictService;
import service.HomeCalculatorKmBeforeService;
import service.TokenAuthenticationService;
import service.TrackingService;
import service.UserService;
import util.FunctionUtil;
import util.GlobalUtils;

@RestController
public class TrackingController {

	@Autowired
	UserService userService;

	@Autowired
	TrackingService service;

	@Autowired
	CustomerService customerService;

	@Autowired
	DistrictService districtService;

	@Autowired
	CampaignService serviceCampaign;

	@Autowired
	CarService carService;
	
	@Autowired
	HomeCalculatorKmBeforeService homeCalculatorKmBeforeService;

	@Autowired
	CarGroupService carGroupService;

	public static String result_sms = "";
	public static Boolean status_result = false;

	private String strToday;
	private String strFromDate;
	private String strToDate;

	@RequestMapping(value = "/insert-tracking", method = POST)
	public String insertTracking(
			@RequestParam(required = false) String location_lat,
			@RequestParam(required = false) String location_long,
			@RequestParam(required = false) String car_id,
			@RequestParam(required = false) String campaign_id,
			@RequestParam(required = false) String type) {
		if (location_lat == null || location_long == null || car_id == null
				|| campaign_id == null || type == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		// Tracking tracking = new Tracking();
		// tracking.setCar_id(car_id);
		// tracking.setCampaign_id(campaign_id);
		// tracking.setType(type);
		Date today = Calendar.getInstance().getTime();
		// tracking.setCreated_date(GlobalUtils.convertStringToDate(today));
		// tracking.setCreated_time(GlobalUtils.convertStringToTime(today));
		// tracking.setLocation_lat(location_lat);
		// tracking.setLocation_long(location_long);
		org.bson.Document do_tracking = new org.bson.Document("car_id", car_id);
		do_tracking.append("campaign_id", campaign_id);
		do_tracking.append("location_lat", location_lat);
		do_tracking.append("location_long", location_long);
		do_tracking.append("type", type);
		do_tracking.append("district_code", "10");
		do_tracking.append("district_name", "demo test");
		do_tracking.append("device_id", "demo test");
		do_tracking.append("created_date", Global.dateToDay);
		do_tracking.append("created_time", Global.timeToDay);

		if (service.insertTracking(do_tracking)) {
			formatTemplate.setStatus(Global.status_ok);
			formatTemplate.setData("");
		} else {
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_insert_data);
			error.setMessage(GlobalMessageScreen.tracking_insert_unsuccessfull);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;

	}

	// private String getDistrictByDistrictname(String district_name){
	// if (district_name.equals(" Q.1"))
	// return "1";
	// else if (district_name.equals(" Q.2"))
	// return "2";
	// else if (district_name.equals(" Q.3"))
	// return "3";
	// else if (district_name.equals(" Q.4"))
	// return "4";
	// else if (district_name.equals(" Q.5"))
	// return "5";
	// else if (district_name.equals(" Q.6"))
	// return "6";
	// else if (district_name.equals(" Q.7"))
	// return "7";
	// else if (district_name.equals(" Q.8"))
	// return "8";
	// else if (district_name.equals(" Q.8"))
	// return "9";
	// else if (district_name.equals(" Q.10"))
	// return "10";
	// else if (district_name.equals(" Q.11"))
	// return "11";
	// else if (district_name.equals(" Q.12"))
	// return "12";
	// else
	// return "100";
	// }

	@RequestMapping(value = "/insert-tracking-gps-box", method = POST)
	public String insertTrackingGPSBox(
			@RequestParam(required = false) String location_lat,
			@RequestParam(required = false) String location_long,
			@RequestParam(required = false) String device_id,
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String district_name,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String speed,
			@RequestParam(required = false) String heading,
			@RequestParam(required = false) String distance,
			@RequestHeader(required = false) String AuthorizationGPS) {
		// bo campaign, carid
		Object a = "";
		org.bson.Document b = (org.bson.Document) a;
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		// check token expire
		if (!AuthorizationGPS.equals(Global.token_gps_box)
				|| TokenAuthenticationService
						.checkExpireToken(Global.token_expire)) {
			error.setCode(GlobalErrorCode.error_code_expire_not_match);
			error.setMessage(GlobalMessageScreen.error_message_token_not_match_or_expire);
			formatTemplate.setStatus(Global.status_fail);
			formatTemplate.setError(error);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(formatTemplate);
			return json;
		} else {
			org.bson.Document do_tracking = new org.bson.Document(
					"location_lat", location_lat);
			do_tracking.append("location_long", location_long);
			do_tracking.append("device_id", device_id);
			do_tracking.append("type", type);
			do_tracking.append("district_name", district_name.trim());
			// do_tracking.append("district_code",
			// getDistrictByDistrictname(district_name));
			do_tracking.append("status", Double.parseDouble(status));
			do_tracking.append("speed", Double.parseDouble(speed));
			do_tracking.append("heading", Double.parseDouble(heading));
			do_tracking.append("distance", Double.parseDouble(distance));
			Date today = Calendar.getInstance().getTime();
			do_tracking.append("created_date",
					GlobalUtils.convertStringToDate(today));
			do_tracking.append("created_time",
					GlobalUtils.convertStringToTime(today));
			;
			if (service.insertTracking(do_tracking)) {
				formatTemplate.setStatus(Global.status_ok);
				formatTemplate.setData("");
			} else {
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_insert_data);
				error.setMessage(GlobalMessageScreen.tracking_insert_unsuccessfull);
				formatTemplate.setError(error);
			}
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	@RequestMapping(value = "/insert-tracking-gps-box-many", method = POST)
	public String insertTrackingGPSBoxMany(
			@RequestParam(required = false) String listObj,
			@RequestHeader(required = false) String AuthorizationGPS) {
		// bo campaign, carid
		//Date today = Calendar.getInstance().getTime();
//		FunctionUtil.writeLogCrawler(
//			" list obj:" + listObj, Global.log_write_carpo);
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		// check token expire
		if (!AuthorizationGPS.equals(Global.token_gps_box)
				|| TokenAuthenticationService
						.checkExpireToken(Global.token_expire)) {
			error.setCode(GlobalErrorCode.error_code_expire_not_match);
			error.setMessage(GlobalMessageScreen.error_message_token_not_match_or_expire);
			formatTemplate.setStatus(Global.status_fail);
			formatTemplate.setError(error);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(formatTemplate);
			return json;
		} else {
			// org.bson.Document do_tracking = new org.bson.Document(
			// "location_lat", location_lat);
			// do_tracking.append("location_long", location_long);
			// do_tracking.append("device_id", device_id);
			// do_tracking.append("type", type);
			// do_tracking.append("district_name", district_name.trim());
			// // do_tracking.append("district_code",
			// // getDistrictByDistrictname(district_name));
			// do_tracking.append("status", Double.parseDouble(status));
			// do_tracking.append("speed", Double.parseDouble(speed));
			// do_tracking.append("heading", Double.parseDouble(heading));
			// do_tracking.append("distance", Double.parseDouble(distance));
			// Date today = Calendar.getInstance().getTime();
			// do_tracking.append("created_date",
			// GlobalUtils.convertStringToDate(today));
			// do_tracking.append("created_time",
			// GlobalUtils.convertStringToTime(today));
			// ;
			ArrayList<org.bson.Document> list_tracking = new ArrayList<org.bson.Document>();
			com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
			com.google.gson.JsonElement tradeElement = parser.parse(listObj);
			JsonArray trackingArray = tradeElement.getAsJsonArray();
			for (com.google.gson.JsonElement tracking : trackingArray) {
				com.google.gson.JsonObject paymentObj = tracking
						.getAsJsonObject();
				// String device_id = paymentObj.get("device_id").getAsString();
				// String a = device_id;
				org.bson.Document do_tracking = new org.bson.Document(
						"location_lat", paymentObj.get("location_lat")
								.getAsString());
				do_tracking.append("location_long",
						paymentObj.get("location_long").getAsString());
				do_tracking.append("device_id", paymentObj.get("device_id")
						.getAsString());
				do_tracking
						.append("type", paymentObj.get("type").getAsString());
				do_tracking.append("district_name",
						paymentObj.get("district_name").getAsString().trim());
				// do_tracking.append("district_code",
				// getDistrictByDistrictname(district_name));
				do_tracking.append("status", Double.parseDouble(paymentObj.get(
						"status").getAsString()));
				do_tracking.append("speed", Double.parseDouble(paymentObj.get(
						"speed").getAsString()));
				do_tracking.append("heading", Double.parseDouble(paymentObj
						.get("heading").getAsString()));
				do_tracking.append("distance", Double.parseDouble(paymentObj
						.get("distance").getAsString()));
				do_tracking.append("address", paymentObj.get("address")
						.getAsString());
				Date today = Calendar.getInstance().getTime();
				do_tracking.append("created_date",
						GlobalUtils.convertStringToDate(today));
				do_tracking.append("created_time",
						GlobalUtils.convertStringToTime(today));
				list_tracking.add(do_tracking);
			}
			if (service.insertTrackingMany(list_tracking)) {
				formatTemplate.setStatus(Global.status_ok);
				formatTemplate.setData("");
			} else {
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_insert_data);
				error.setMessage(GlobalMessageScreen.tracking_insert_unsuccessfull);
				formatTemplate.setError(error);
			}
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	// @RequestMapping(value = "/get-info-tracking-by-date", method = GET)
	// public String getInfoTrackingByDate(
	// @RequestParam(required = false) String date) {
	// if (date == null)
	// return FunctionUtil.getJsonErrorForRequestParams();
	// // format date -> yyyy-MM-dd
	// JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
	// JsonFormatError error = new JsonFormatError();
	// formatJsonTrackingInfo formatJsonTrackingInfo = new
	// formatJsonTrackingInfo();
	// ArrayList<LocationGoogle> listLocation = service
	// .getListLocationTrackingByDate(date);
	// if (status_result) {
	// formatTemplate.setStatus(Global.status_ok);
	// double distanceTotal = FunctionUtil
	// .totalDistanceByListLocation(listLocation);
	// distanceTotal = FunctionUtil.roundDouble(distanceTotal);
	// formatJsonTrackingInfo.setTotal_distace(distanceTotal);
	// formatJsonTrackingInfo.setList_location(listLocation);
	// formatTemplate.setData(formatJsonTrackingInfo);
	// } else {
	// formatTemplate.setStatus(Global.status_fail);
	// error.setCode(GlobalErrorCode.error_code_find_data);
	// error.setMessage(GlobalErrorCode.error_message_find_data);
	// formatTemplate.setError(error);
	// }
	// Gson gson = new GsonBuilder().setPrettyPrinting().create();
	// String json = gson.toJson(formatTemplate);
	// return json;
	// }

	// public JsonFormatTemplate getDataHistoryTrackingByParams(String user_id,
	// int indexDayBegin, int numberDayWantToFind, String car_id) {
	// JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
	// JsonFormatError error = new JsonFormatError();
	// formatJsonHistoryTrackingDataInfo historyTrackingDataInfo = new
	// formatJsonHistoryTrackingDataInfo();
	// earning_list earning_list = new earning_list(); // now not use
	// distance_list distance_list;
	// ArrayList<distance_list> array_distance_list = new
	// ArrayList<distance_list>();
	// Calendar cal = Calendar.getInstance();
	// if (indexDayBegin != 0)
	// cal.add(Calendar.DATE, indexDayBegin);
	// int indexDayBefore = -1;
	// double total_distance = 0;
	// for (int indexDay = 1; indexDay <= numberDayWantToFind; indexDay++) {
	// cal.add(Calendar.DATE, indexDayBefore);
	// Date day = cal.getTime();
	// String strDay = GlobalUtils.convertStringToDate(day);
	// ArrayList<LocationGoogle> listLocation = service
	// .getListLocationTrackingByDateAndCar_id(strDay, car_id);
	// double distance = FunctionUtil
	// .totalDistanceByListLocation(listLocation);
	// distance = FunctionUtil.roundDouble(distance);
	// if (distance > 0) {
	// distance_list = new distance_list();
	// distance_list.setDate(strDay);
	// distance_list.setDistance(String.valueOf(distance));
	// array_distance_list.add(distance_list);
	// }
	// total_distance += distance;
	// }
	// if (total_distance > 0) {
	// // co data
	// formatTemplate.setStatus(Global.status_ok);
	// total_distance = FunctionUtil.roundDouble(total_distance);
	// historyTrackingDataInfo.setTotal_distance(String
	// .valueOf(total_distance));
	// historyTrackingDataInfo.setTotal_earning("");
	// historyTrackingDataInfo.setDistance_list(array_distance_list);
	// historyTrackingDataInfo.setEarning_list("");
	// formatTemplate.setData(historyTrackingDataInfo);
	//
	// } else {
	// // data empty
	// formatTemplate.setStatus(Global.status_fail);
	// error.setCode(GlobalErrorCode.error_code_find_data);
	// error.setMessage(GlobalErrorCode.error_message_find_data);
	// formatTemplate.setError(error);
	// }
	// return formatTemplate;
	// }

	// @RequestMapping(value = "/get-info-home-driver-history-by-date", method =
	// GET)
	// public String getInfoHistoryTrackingByDate(
	// @RequestParam(required = false) String params_day,
	// @RequestParam(required = false) String user_id) {
	// if (params_day == null || user_id == null)
	// return FunctionUtil.getJsonErrorForRequestParams();
	// // format date -> yyyy-MM-dd
	// // params = 1: 7 ngay gan nhat
	// // params = 2: 30 ngay gan nhat
	// // params = 3: thang truoc
	// JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
	// JsonFormatError error = new JsonFormatError();
	// Car car = FunctionUtil.getInfoCarByUserId(user_id);
	// if (car.get_id() != null) {
	// if (params_day.equals("1")) {
	// // 7 ngay gan 1
	// formatTemplate = getDataHistoryTrackingByParams(user_id, 0, 7,
	// car.get_id());
	// } else if (params_day.equals("2")) {
	// // 30 ngay gan nhat
	// formatTemplate = getDataHistoryTrackingByParams(user_id, 0, 30,
	// car.get_id());
	// } else if (params_day.equals("3")) {
	// // thang truoc
	// formatTemplate = getDataHistoryTrackingByParams(user_id, -30,
	// 30, car.get_id());
	// }
	// } else {
	// // data empty -> chua so huu chiec xe nao
	// formatTemplate.setStatus(Global.status_fail);
	// error.setCode(GlobalErrorCode.error_code_find_data);
	// error.setMessage(GlobalErrorCode.error_message_find_data);
	// formatTemplate.setError(error);
	// }
	//
	// Gson gson = new GsonBuilder().setPrettyPrinting().create();
	// String json = gson.toJson(formatTemplate);
	// return json;
	// }

	/* new function home */
	// get total km from two days
	public double getTotalKmFromTwoDate(String strBeginDate, String strEndDate,
			String device_id) {
		// ArrayList<LocationGoogle> listLocation = service
		// .getListLocationTrackingByTwoDateAndDevice_id(strBeginDate,
		// strEndDate, device_id);
		// double distance = FunctionUtil
		// .totalDistanceByListLocation(listLocation);
		// distance = FunctionUtil.roundDouble(distance);
		double distance = service.getSumDistanceTrackingByTwoDateAndDevice_id(
				strBeginDate, strEndDate, device_id);
		return distance;
	}

	// get total km from today
	public double getTotalKmFromToday(String strToday, String device_id) {
		ArrayList<LocationGoogle> listLocation = service
				.getListLocationTrackingByDateAndDevice_id(strToday, device_id);
		double distance = FunctionUtil
				.totalDistanceByListLocation(listLocation);
		distance = FunctionUtil.roundDouble(distance);
		return distance;
	}

	public Date addDateWithParams(int params, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, params);
		return cal.getTime();
	}

	public void setDateToDay() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		strToday = GlobalUtils.convertStringToDate(today);
	}

	public void setDateThreeDayBefore() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.DATE, -4); // because < > not =
		Date dateBefore = cal.getTime();
		strFromDate = GlobalUtils.convertStringToDate(dateBefore);
		strToDate = GlobalUtils.convertStringToDate(today);

	}

	public void setDateSevenDayBefore() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.DATE, -8); // because < > not =
		Date dateBefore = cal.getTime();
		strFromDate = GlobalUtils.convertStringToDate(dateBefore);
		strToDate = GlobalUtils.convertStringToDate(today);
	}

	// test delete
	// test delete
	public void testDeleteCar() {
		FindIterable<org.bson.Document> listDocumen;
		MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
		org.bson.Document query = new org.bson.Document();
		MongoCollection<org.bson.Document> do_trackings = database
				.getCollection(Global.collection_user);
		listDocumen = do_trackings.find(query);
		listDocumen.forEach(new Block<org.bson.Document>() {
			@Override
			public void apply(final org.bson.Document document) {

			}
		});
	}

//	@RequestMapping(value = "/get-info-home-driver", method = GET)
//	public String getInfoHomeDriver(
//			@RequestParam(required = false) String user_id) {
//		if (user_id == null)
//			return FunctionUtil.getJsonErrorForRequestParams();
//		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
//		JsonFormatError error = new JsonFormatError();
//		formatDataInfoHome dataInfoHome = new formatDataInfoHome();
//		Car car = FunctionUtil.getInfoCarByUserId(user_id);
//		if (car.get_id() != null) {
//			String device_id = car.getDevice_id();
//			String campaign_id = car.getCampaign_id();
//			Boolean status = false;
//			Calendar cal = Calendar.getInstance();
//			Date today_part = cal.getTime();
//			Date date_from_part = new Date();
//			Date date_to_part = new Date();
//			ArrayList<org.bson.Document> documentPartCampaign = serviceCampaign
//					.getInfoCampaignPartDriverByCampaignId(campaign_id);
//			if (documentPartCampaign.size() <= 0) {
//				serviceCampaign.calculator_campaign_part_driver(campaign_id);
//				documentPartCampaign = serviceCampaign
//						.getInfoCampaignPartDriverByCampaignId(campaign_id);
//			}
//			for (org.bson.Document document : documentPartCampaign) {
//				try {
//					Date from_date_db = GlobalUtils.convertStrToDate(document
//							.get("from_date").toString());
//					Date to_date_db = GlobalUtils.convertStrToDate(document
//							.get("to_date").toString());
//					if (!from_date_db.after(today_part)
//							&& !to_date_db.before(today_part)) {
//						date_from_part = from_date_db;
//						date_to_part = to_date_db;
//						status = true;
//						break;
//					}
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//			// tinh campaign_part_driver de tinh theo ky -- function new if not
//			// end
//			// LocalDate today = LocalDate.now();
//			// LocalDate localDateStartMonth = today.withDayOfMonth(1);
//			// LocalDate localDateEndMonth = today.withDayOfMonth(today
//			// .lengthOfMonth());
//			// Date dateStartMonth =
//			// Date.from(localDateStartMonth.atStartOfDay()
//			// .atZone(ZoneId.systemDefault()).toInstant());
//			// Date dateEndMonth = Date.from(localDateEndMonth.atStartOfDay()
//			// .atZone(ZoneId.systemDefault()).toInstant());
//			// dateStartMonth = addDateWithParams(-1, dateStartMonth);
//			// dateEndMonth = addDateWithParams(1, dateEndMonth);
//			// // total km in month
//			// double totalKMInMonth = getTotalKmFromTwoDate(
//			// GlobalUtils.convertStringToDate(dateStartMonth),
//			// GlobalUtils.convertStringToDate(dateEndMonth), device_id);
//			if (status) {
//				// + 1 AND -1
//				Calendar cal_new = Calendar.getInstance();
//				cal_new.setTime(date_from_part);
//				cal_new.add(Calendar.DATE, -1); // because < > not =
//				date_from_part = cal_new.getTime();
//				// + 1
//				cal_new.setTime(date_to_part);
//				cal_new.add(Calendar.DATE, 1); // because < > not =
//				date_to_part = cal_new.getTime();
//				double totalKMInMonth = getTotalKmFromTwoDate(
//						GlobalUtils.convertStringToDate(date_from_part),
//						GlobalUtils.convertStringToDate(date_to_part),
//						device_id);
//
//				// percent km in month
//				org.bson.Document documentCampaign = serviceCampaign
//						.getInfoCampaignById(campaign_id);
//				double total_distance_campaign = 0;
//				if (documentCampaign != null)
//					try {
//						total_distance_campaign = Double
//								.parseDouble(documentCampaign.get(
//										"total_distance").toString());
//					} catch (Exception ex) {
//						total_distance_campaign = 0;
//					}
//				double persent = (totalKMInMonth * 100)
//						/ total_distance_campaign;
//				if (persent > 100)
//					persent = 100;
//				// total km today
//				setDateToDay();
////				double totalKmToDay = getTotalKmFromToday(strToday, device_id);
//				double totalKmToDay = service.getMaxDistanceByDeviceIdAndDate(device_id, strToday);
//				// total km 3 day before
//				setDateThreeDayBefore();
//				double totalKMInThreeDayBefore = getTotalKmFromTwoDate(
//						strFromDate, strToDate, device_id);
//				// total km 7 day before
//				setDateSevenDayBefore();
//				double totalKMInSevenDayBefore = getTotalKmFromTwoDate(
//						strFromDate, strToDate, device_id);
//				dataInfoHome.setTotal_km_month(FunctionUtil
//						.roundDouble(totalKMInMonth));
//				dataInfoHome.setTotal_percent_month(FunctionUtil
//						.roundDouble(persent));
//				dataInfoHome.setTotal_km_today(FunctionUtil
//						.roundDouble(totalKmToDay));
//				dataInfoHome.setTotal_km_three_day_before(FunctionUtil
//						.roundDouble(totalKMInThreeDayBefore));
//				dataInfoHome.setTotal_km_seven_day_before(FunctionUtil
//						.roundDouble(totalKMInSevenDayBefore));
//				// dataInfoHome
//				formatTemplate.setStatus(Global.status_ok);
//				formatTemplate.setData(dataInfoHome);
//			}else{
//				formatTemplate.setStatus(Global.status_fail);
//				error.setCode(GlobalErrorCode.error_code_find_data);
//				error.setMessage(GlobalErrorCode.error_message_find_data);
//				formatTemplate.setError(error);
//			}
//		} else {
//			// data empty -> chua so huu chiec xe nao
//			formatTemplate.setStatus(Global.status_fail);
//			error.setCode(GlobalErrorCode.error_code_find_data);
//			error.setMessage(GlobalErrorCode.error_message_find_data);
//			formatTemplate.setError(error);
//		}
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		String json = gson.toJson(formatTemplate);
//		return json;
//	}
	
	
	// new
	
	@RequestMapping(value = "/get-info-home-driver", method = GET)
	public String getInfoHomeDriver(
			@RequestParam(required = false) String user_id) {
		if (user_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		Car car = FunctionUtil.getInfoCarByUserId(user_id);
		if (car.get_id() != null) {
			String device_id = car.getDevice_id();
			String campaign_id = car.getCampaign_id();
			// if empty
			org.bson.Document documenData = homeCalculatorKmBeforeService.getInfoHomeCalculatorKmBeforeCampaignAndDeviceId(campaign_id, device_id);
			if (documenData.get("campaign_id").toString() != null) {
				formatTemplate.setStatus(Global.status_ok);
				formatTemplate.setData(documenData);
			}else{
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_find_data);
				error.setMessage(GlobalErrorCode.error_message_find_data);
				formatTemplate.setError(error);
			}
		} else {
			// data empty -> chua so huu chiec xe nao
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalErrorCode.error_message_find_data);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}


	private void sortGiam(formatDataUserRunMost_Home_CM[] list) {
		for (int i = 0; i < list.length - 1; i++) {
			for (int j = i + 1; j < list.length; j++) {
				if (list[i].getTotal_km() < list[j].getTotal_km()) {
					formatDataUserRunMost_Home_CM temp = list[i];
					list[i] = list[j];
					list[j] = temp;
				}
			}
		}
	}
	
	private void sortGiamLocation(formatDataLocation_Home_CM[] list) {
		for (int i = 0; i < list.length - 1; i++) {
			for (int j = i + 1; j < list.length; j++) {
				if ( Double.parseDouble(list[i].getTotal_distance()) < Double.parseDouble(list[j].getTotal_distance())) {
					formatDataLocation_Home_CM temp = list[i];
					list[i] = list[j];
					list[j] = temp;
				}
			}
		}
	}

	private ArrayList<formatDataLocation_Home_CM> getDataUserDriveInArea(
			String area_code, String campaign_id,
			ArrayList<String> listDeviceCar) {
		ArrayList<formatDataLocation_Home_CM> arrayListData = new ArrayList<formatDataLocation_Home_CM>();
		ArrayList<org.bson.Document> listDocumentDistrict = districtService
				.getInfoDocumentDistrictByArea_Code(area_code);
		for (org.bson.Document documentDistrict : listDocumentDistrict) {
			String district_code = documentDistrict.get("code").toString();
			String district_name = documentDistrict.get("name").toString();
			double distance = service
					.getMaxDistanceByListDevice_IdAndDistrictName(
							listDeviceCar, district_name);
			formatDataLocation_Home_CM location_Home_CM = new formatDataLocation_Home_CM();
			location_Home_CM.setDistrict_name(district_name);
			location_Home_CM.setDistrict_code(district_code);
			location_Home_CM.setTotal_distance(String.valueOf(distance));
			if (distance > 0)
				arrayListData.add(location_Home_CM);
		}		
		return arrayListData;
	}
	
	private ArrayList<formatDataLocation_Home_CM> getDataUserDriveInArea_CountPoint(
			String area_code, String campaign_id,
			ArrayList<String> listDeviceCar, String startDate, String endDate) {
		ArrayList<formatDataLocation_Home_CM> arrayListData = new ArrayList<formatDataLocation_Home_CM>();
		ArrayList<org.bson.Document> listDocumentDistrict = districtService
				.getInfoDocumentDistrictByArea_Code(area_code);
		for (org.bson.Document documentDistrict : listDocumentDistrict) {
			String district_code = documentDistrict.get("code").toString();
			String district_name = documentDistrict.get("name").toString();
			double countPoint = service
					.getCountPointByListDevice_IdAndDistrictNameAndTwoDay(
							listDeviceCar, district_name,startDate,endDate);
			formatDataLocation_Home_CM location_Home_CM = new formatDataLocation_Home_CM();
			location_Home_CM.setDistrict_name(district_name);
			location_Home_CM.setDistrict_code(district_code);
			//location_Home_CM.setTotal_distance(String.valueOf(distance));
			location_Home_CM.setCount_point(String.valueOf(countPoint));
			if (countPoint > 0)
				arrayListData.add(location_Home_CM);
		}

		return arrayListData;
	}

	public double getDistanceTrackingByCampaign(String campaign_id) {
		double distanceresult = 0;
		ArrayList<org.bson.Document> arrayListCar = carService
				.getListCarByCampaign(campaign_id);
		for (org.bson.Document documentCar : arrayListCar) {
			String device_id = documentCar.get("device_id").toString();
			ArrayList<LocationGoogle> listLocation = service
					.getListLocationTrackingByDevice_id(device_id);
			distanceresult += FunctionUtil
					.totalDistanceByListLocation(listLocation);
		}
		return distanceresult;
	}

	@RequestMapping(value = "/save-home-customer-db", method = GET)
	public String saveHomeCustomerDatabase() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		// limix 10 user
		service.deleteHomeCustomer();
		ArrayList<Campaign_CM> arrayListCampaign = serviceCampaign
				.getAllCampaign(); // fix get all campaign co hieu luc
		for (Campaign_CM campaign_cm : arrayListCampaign) {
			formatJsonHomeCustomer_CM formatJsonHomeCustomer_CM = new formatJsonHomeCustomer_CM();
			ArrayList<formatDataUserRunMost_Home_CM> arrayListUserRunMost = new ArrayList<formatDataUserRunMost_Home_CM>();
			String campaign_id = campaign_cm.get_id();
			org.bson.Document documentCampaign = serviceCampaign
					.getInfoCampaignById(campaign_id);
			if (documentCampaign != null) {
				int impressionNo = Integer.parseInt(documentCampaign.get(
						"impressionNo").toString());
				if (impressionNo < 1)
					impressionNo = 75;
				ArrayList<org.bson.Document> arrayListCar = carService
						.getListCarByCampaign(campaign_id);
				formatTemplate.setStatus(Global.status_ok);
				formatJsonHomeCustomer_CM.setTotal_user_drive(String
						.valueOf(arrayListCar.size()));

				double distance = 0;// getDistanceTrackingByCampaign(campaign_id);
				double distance_total_km_run = 0;
				// formatJsonHomeCustomer_CM.setTotal_km_run(String.valueOf(distance));
				// process for set total km
				// ArrayList<LocationGoogle> listLocation = new
				// ArrayList<LocationGoogle>();
				// process for set list user run most
				ArrayList<String> listDevice = new ArrayList<String>();
				for (org.bson.Document documentCar : arrayListCar) {
					// String car_id = documentCar.get("_id").toString();
					String device_id = documentCar.get("device_id").toString();
					listDevice.add(device_id);
					// listLocation =
					// service.getListLocationTrackingByDevice_id(device_id);
					// distance = FunctionUtil
					// .totalDistanceByListLocation(listLocation);
//					distance = service.getMaxDistanceByDevice_Id(device_id);
					// update 03/03/2018
					distance = service.getSumDistanceTrackingByTwoDateAndDevice_id(campaign_cm.getStart_time(), campaign_cm.getEnd_time(), device_id);
					distance_total_km_run += distance;
					formatDataUserRunMost_Home_CM cm = new formatDataUserRunMost_Home_CM();
					String user_id = documentCar.get("user_id").toString();
					cm.set_id(user_id);
					org.bson.Document documentUser = userService
							.getInfoUserByUserId(user_id);
					if (documentUser != null)
						cm.setName(documentUser.get("fullname").toString());
					else
						cm.setName("");
					// new -> end
					cm.setTotal_km(distance * impressionNo);
					// end
					arrayListUserRunMost.add(cm);
				}
				formatJsonHomeCustomer_CM.setTotal_km_run(String
						.valueOf(FunctionUtil
								.roundDouble(distance_total_km_run)));
				String area_code = documentCampaign.get("area_code").toString();
				ArrayList<formatDataLocation_Home_CM> dataUserRunInDistrict = getDataUserDriveInArea(
						area_code, campaign_id, listDevice);
				// sort giam
				formatDataLocation_Home_CM[] arrLocation = new formatDataLocation_Home_CM[dataUserRunInDistrict
				                                          							.size()];
				for (int index = 0; index < dataUserRunInDistrict.size(); index++) {
					arrLocation[index] = dataUserRunInDistrict.get(index);
				}
				// sort
				sortGiamLocation(arrLocation);
				// lay max 10
				dataUserRunInDistrict = new ArrayList<formatDataLocation_Home_CM>();
				for (int index = 0; index < arrLocation.length; index++) {
					if (index < 10)
						dataUserRunInDistrict.add(arrLocation[index]);
				}
				formatJsonHomeCustomer_CM
						.setTotal_kim_location(dataUserRunInDistrict);
				ArrayList<formatDataUserRunMost_Home_CM> listResult = new ArrayList<formatDataUserRunMost_Home_CM>();
				if (arrayListUserRunMost.size() > 0) {
					formatDataUserRunMost_Home_CM[] arr = new formatDataUserRunMost_Home_CM[arrayListUserRunMost
							.size()];
					for (int index = 0; index < arrayListUserRunMost.size(); index++) {
						arr[index] = arrayListUserRunMost.get(index);
					}
					// sort
					sortGiam(arr);
					// max 10 person
					listResult = new ArrayList<formatDataUserRunMost_Home_CM>();
					for (int index = 0; index < arr.length; index++) {
						if (index < 10)
							listResult.add(arr[index]);
					}
					formatJsonHomeCustomer_CM.setUser_run_most(listResult);
				} else
					formatJsonHomeCustomer_CM
							.setUser_run_most(arrayListUserRunMost);
				// process save
				service.saveHomeCustomerToDatabase(campaign_id, String
						.valueOf(FunctionUtil
								.roundDouble(distance_total_km_run)), String
						.valueOf(arrayListCar.size()), listResult,
						dataUserRunInDistrict);
				// formatTemplate.setData(formatJsonHomeCustomer_CM);
			} else {
				// formatTemplate.setStatus(Global.status_fail);
				// error.setCode(GlobalErrorCode.error_code_find_data);
				// error.setMessage(GlobalErrorCode.error_message_find_data);
				// formatTemplate.setError(error);
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}
	
	@RequestMapping(value = "/save-home-customer-db-new", method = GET)
	public String saveHomeCustomerDatabaseNew() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		// limix 10 user
		service.deleteHomeCustomer();
		ArrayList<Campaign_CM> arrayListCampaign = serviceCampaign
				.getAllCampaign(); // fix get all campaign co hieu luc
		for (Campaign_CM campaign_cm : arrayListCampaign) {
			formatJsonHomeCustomer_CM formatJsonHomeCustomer_CM = new formatJsonHomeCustomer_CM();
			ArrayList<formatDataUserRunMost_Home_CM> arrayListUserRunMost = new ArrayList<formatDataUserRunMost_Home_CM>();
			String campaign_id = campaign_cm.get_id();
			org.bson.Document documentCampaign = serviceCampaign
					.getInfoCampaignById(campaign_id);
			if (documentCampaign != null) {
				int impressionNo = Integer.parseInt(documentCampaign.get(
						"impressionNo").toString());
				if (impressionNo < 1)
					impressionNo = 75;
				ArrayList<org.bson.Document> arrayListCar = carService
						.getListCarByCampaign(campaign_id);
				formatTemplate.setStatus(Global.status_ok);
				formatJsonHomeCustomer_CM.setTotal_user_drive(String
						.valueOf(arrayListCar.size()));

				double distance = 0;// getDistanceTrackingByCampaign(campaign_id);
				double distance_total_km_run = 0;
				// formatJsonHomeCustomer_CM.setTotal_km_run(String.valueOf(distance));
				// process for set total km
				// ArrayList<LocationGoogle> listLocation = new
				// ArrayList<LocationGoogle>();
				// process for set list user run most
				ArrayList<String> listDevice = new ArrayList<String>();
				for (org.bson.Document documentCar : arrayListCar) {
					// String car_id = documentCar.get("_id").toString();
					String device_id = documentCar.get("device_id").toString();
					listDevice.add(device_id);
					// listLocation =
					// service.getListLocationTrackingByDevice_id(device_id);
					// distance = FunctionUtil
					// .totalDistanceByListLocation(listLocation);
//					distance = service.getMaxDistanceByDevice_Id(device_id);
					// update 03/03/2018
					distance = service.getSumDistanceTrackingByTwoDateAndDevice_id(campaign_cm.getStart_time(), campaign_cm.getEnd_time(), device_id);
					distance_total_km_run += distance;
					formatDataUserRunMost_Home_CM cm = new formatDataUserRunMost_Home_CM();
					String user_id = documentCar.get("user_id").toString();
					cm.set_id(user_id);
					org.bson.Document documentUser = userService
							.getInfoUserByUserId(user_id);
					if (documentUser != null)
						cm.setName(documentUser.get("fullname").toString());
					else
						cm.setName("");
					// new -> end
					cm.setTotal_km(distance * impressionNo);
					// end
					arrayListUserRunMost.add(cm);
				}
				formatJsonHomeCustomer_CM.setTotal_km_run(String
						.valueOf(FunctionUtil
								.roundDouble(distance_total_km_run)));
				String area_code = documentCampaign.get("area_code").toString();
				ArrayList<formatDataLocation_Home_CM> dataUserRunInDistrict = getDataUserDriveInArea_CountPoint(
						area_code, campaign_id, listDevice,campaign_cm.getStart_time(), campaign_cm.getEnd_time());
				formatJsonHomeCustomer_CM
						.setTotal_kim_location(dataUserRunInDistrict);
				ArrayList<formatDataUserRunMost_Home_CM> listResult = new ArrayList<formatDataUserRunMost_Home_CM>();
				if (arrayListUserRunMost.size() > 0) {
					formatDataUserRunMost_Home_CM[] arr = new formatDataUserRunMost_Home_CM[arrayListUserRunMost
							.size()];
					for (int index = 0; index < arrayListUserRunMost.size(); index++) {
						arr[index] = arrayListUserRunMost.get(index);
					}
					// sort
					sortGiam(arr);
					// max 10 person
					listResult = new ArrayList<formatDataUserRunMost_Home_CM>();
					for (int index = 0; index < arr.length; index++) {
						if (index < 10)
							listResult.add(arr[index]);
					}
					formatJsonHomeCustomer_CM.setUser_run_most(listResult);
				} else
					formatJsonHomeCustomer_CM
							.setUser_run_most(arrayListUserRunMost);
				// process save
				service.saveHomeCustomerToDatabase(campaign_id, String
						.valueOf(FunctionUtil
								.roundDouble(distance_total_km_run)), String
						.valueOf(arrayListCar.size()), listResult,
						dataUserRunInDistrict);
				// formatTemplate.setData(formatJsonHomeCustomer_CM);
			} else {
				// formatTemplate.setStatus(Global.status_fail);
				// error.setCode(GlobalErrorCode.error_code_find_data);
				// error.setMessage(GlobalErrorCode.error_message_find_data);
				// formatTemplate.setError(error);
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	// get info home customer
	// @RequestMapping(value = "/get-info-home-customer", method = GET)
	// public String getInfoHomeCustomer(
	// @RequestParam(required = false) String campaign_id) {
	// if (campaign_id == null)
	// return FunctionUtil.getJsonErrorForRequestParams();
	// JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
	// JsonFormatError error = new JsonFormatError();
	// formatJsonHomeCustomer_CM formatJsonHomeCustomer_CM = new
	// formatJsonHomeCustomer_CM();
	// ArrayList<formatDataUserRunMost_Home_CM> arrayListUserRunMost = new
	// ArrayList<formatDataUserRunMost_Home_CM>();
	// // limix 10 user
	// org.bson.Document documentCampaign = serviceCampaign
	// .getInfoCampaignById(campaign_id);
	// if (documentCampaign != null) {
	// int impressionNo = Integer.parseInt(documentCampaign.get(
	// "impressionNo").toString());
	// if (impressionNo < 1)
	// impressionNo = 75;
	// ArrayList<org.bson.Document> arrayListCar = carService
	// .getListCarByCampaign(campaign_id);
	// formatTemplate.setStatus(Global.status_ok);
	// formatJsonHomeCustomer_CM.setTotal_user_drive(String
	// .valueOf(arrayListCar.size()));
	//
	// double distance = 0;// getDistanceTrackingByCampaign(campaign_id);
	// double distance_total_km_run = 0;
	// // formatJsonHomeCustomer_CM.setTotal_km_run(String.valueOf(distance));
	// // process for set total km
	// // ArrayList<LocationGoogle> listLocation = new
	// // ArrayList<LocationGoogle>();
	// // process for set list user run most
	// ArrayList<String> listDevice = new ArrayList<String>();
	// for (org.bson.Document documentCar : arrayListCar) {
	// // String car_id = documentCar.get("_id").toString();
	// String device_id = documentCar.get("device_id").toString();
	// listDevice.add(device_id);
	// // listLocation =
	// // service.getListLocationTrackingByDevice_id(device_id);
	// // distance = FunctionUtil
	// // .totalDistanceByListLocation(listLocation);
	// distance = service.getMaxDistanceByDevice_Id(device_id);
	// distance_total_km_run += distance;
	// formatDataUserRunMost_Home_CM cm = new formatDataUserRunMost_Home_CM();
	// String user_id = documentCar.get("user_id").toString();
	// cm.set_id(user_id);
	// org.bson.Document documentUser = userService
	// .getInfoUserByUserId(user_id);
	// if (documentUser != null)
	// cm.setName(documentUser.get("fullname").toString());
	// else
	// cm.setName("");
	// // new -> end
	// cm.setTotal_km(distance * impressionNo);
	// // end
	// arrayListUserRunMost.add(cm);
	// }
	// formatJsonHomeCustomer_CM.setTotal_km_run(String
	// .valueOf(distance_total_km_run));
	// String area_code = documentCampaign.get("area_code").toString();
	// ArrayList<formatDataLocation_Home_CM> dataUserRunInDistrict =
	// getDataUserDriveInArea(
	// area_code, campaign_id, listDevice);
	// formatJsonHomeCustomer_CM
	// .setTotal_kim_location(dataUserRunInDistrict);
	// if (arrayListUserRunMost.size() > 0) {
	// formatDataUserRunMost_Home_CM[] arr = new
	// formatDataUserRunMost_Home_CM[arrayListUserRunMost
	// .size()];
	// for (int index = 0; index < arrayListUserRunMost.size(); index++) {
	// arr[index] = arrayListUserRunMost.get(index);
	// }
	// // sort
	// sortGiam(arr);
	// // max 10 person
	// ArrayList<formatDataUserRunMost_Home_CM> listResult = new
	// ArrayList<formatDataUserRunMost_Home_CM>();
	// for (int index = 0; index < arr.length; index++) {
	// if (index < 10)
	// listResult.add(arr[index]);
	// }
	// formatJsonHomeCustomer_CM.setUser_run_most(listResult);
	// } else
	// formatJsonHomeCustomer_CM
	// .setUser_run_most(arrayListUserRunMost);
	// formatTemplate.setData(formatJsonHomeCustomer_CM);
	// } else {
	// formatTemplate.setStatus(Global.status_fail);
	// error.setCode(GlobalErrorCode.error_code_find_data);
	// error.setMessage(GlobalErrorCode.error_message_find_data);
	// formatTemplate.setError(error);
	// }
	//
	// Gson gson = new GsonBuilder().setPrettyPrinting().create();
	// String json = gson.toJson(formatTemplate);
	// return json;
	// }

	@RequestMapping(value = "/get-info-home-customer", method = GET)
	public String getInfoHomeCustomer(
			@RequestParam(required = false) String campaign_id) {
		if (campaign_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document documentResult = service
				.getDocumentHomeCustomerByCampaignId(campaign_id);
		if (documentResult != null) {
			formatTemplate.setStatus(Global.status_ok);
			formatTemplate.setData(documentResult);
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

	@RequestMapping(value = "/get-info-screen-manager-group", method = GET)
	public String getInfoScreenManagerGroup(
			@RequestParam(required = false) String leader_id) {
		if (leader_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document do_car_group = carGroupService
				.getCarGroupByLeaderId(leader_id);
		formatDataInfoHome dataInfoHome = new formatDataInfoHome();
		if (do_car_group != null) {
			String group_id = do_car_group.get("_id").toString();
			ArrayList<org.bson.Document> list_car = carService
					.getListCarByGroupId(group_id);
			if (list_car.size() > 0) {
				LocalDate today = LocalDate.now();
				LocalDate localDateStartMonth = today.withDayOfMonth(1);
				LocalDate localDateEndMonth = today.withDayOfMonth(today
						.lengthOfMonth());
				Date dateStartMonth = Date.from(localDateStartMonth
						.atStartOfDay().atZone(ZoneId.systemDefault())
						.toInstant());
				Date dateEndMonth = Date.from(localDateEndMonth.atStartOfDay()
						.atZone(ZoneId.systemDefault()).toInstant());
				dateStartMonth = addDateWithParams(-1, dateStartMonth);
				dateEndMonth = addDateWithParams(1, dateEndMonth);
				// co data
				double totalKmInMonth = 0;
				double totalPersent = 0;
				double totalKmToday = 0;
				double totalKmThreeDayBefore = 0;
				double totalKmSevenDayBefore = 0;
				int index = 1;
				double total_distance_campaign = 0;
				for (org.bson.Document document : list_car) {
					String device_id = document.get("device_id").toString();
					String campaign_id = document.get("campaign_id").toString();
					// total km in month
					double kmInMonth = getTotalKmFromTwoDate(
							GlobalUtils.convertStringToDate(dateStartMonth),
							GlobalUtils.convertStringToDate(dateEndMonth),
							device_id);
					// percent km in month
					org.bson.Document documentCampaign = serviceCampaign
							.getInfoCampaignById(campaign_id);
					if (documentCampaign != null) {
						try {
							if (index == 1)
								total_distance_campaign = Double
										.parseDouble(documentCampaign.get(
												"total_distance").toString());
							index++;
						} catch (Exception ex) {
							total_distance_campaign = 0;
						}
					}
					double persent = 0;
					if (total_distance_campaign > 0)
						persent = (kmInMonth * 100) / total_distance_campaign;
					// total km today
					setDateToDay();
					// double kmToDay = getTotalKmFromToday(strToday,
					// device_id);
					double kmToDay = service.getMaxDistanceByDeviceIdAndDate(
							device_id, strToday);
					// total km 3 day before
					setDateThreeDayBefore();
					double kmThreeDayBefore = getTotalKmFromTwoDate(
							strFromDate, strToDate, device_id);
					// total km 7 day before
					setDateSevenDayBefore();
					double kmSevenDayBefore = getTotalKmFromTwoDate(
							strFromDate, strToDate, device_id);
					totalKmInMonth += kmInMonth;
					totalPersent += persent;
					totalKmToday += kmToDay;
					totalKmThreeDayBefore += kmThreeDayBefore;
					totalKmSevenDayBefore += kmSevenDayBefore;
				}
				// set data
				dataInfoHome.setTotal_km_month(FunctionUtil
						.roundDouble(totalKmInMonth));
				if (totalPersent > 100)
					totalPersent = 100;
				dataInfoHome.setTotal_percent_month(FunctionUtil
						.roundDouble(totalPersent));
				dataInfoHome.setTotal_km_today(FunctionUtil
						.roundDouble(totalKmToday));
				dataInfoHome.setTotal_km_three_day_before(FunctionUtil
						.roundDouble(totalKmThreeDayBefore));
				dataInfoHome.setTotal_km_seven_day_before(FunctionUtil
						.roundDouble(totalKmSevenDayBefore));
				// dataInfoHome
				formatTemplate.setStatus(Global.status_ok);
				formatTemplate.setData(dataInfoHome);

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

	private int countCompaignStillValidated() {
		int count = 0;
		ArrayList<Campaign_CM> listCampaign = serviceCampaign.getAllCampaign();
		Date today = Calendar.getInstance().getTime();
		for (Campaign_CM campaign_CM : listCampaign) {
			try {
				Date start = GlobalUtils.convertStrToDate(campaign_CM
						.getStart_time());
				Date end = GlobalUtils.convertStrToDate(campaign_CM
						.getEnd_time());
				if (today.after(start) && today.before(end)) {
					count++;
				}
			} catch (Exception ex) {

			}
		}
		return count;
	}

	private int countCarIsRunning() {
		int count = 0;
		ArrayList<org.bson.Document> listCar = carService.getAllCar();
		for (org.bson.Document document : listCar) {
			String car_id = document.get("_id").toString();
			if (service.checkCarIsRunning(car_id))
				count++;
		}
		return count;
	}

	// new
	@RequestMapping(value = "/get-info-home-manager", method = GET)
	public String getInfoHomeManager() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		formatDataInfoHomeManager dataInfoHomeManager = new formatDataInfoHomeManager();
		// count customer
		ArrayList<Customer_CM> listCampaign = new ArrayList<Customer_CM>();
		listCampaign = customerService.getListCustomer();
		dataInfoHomeManager.setCount_customer(String.valueOf(listCampaign
				.size()));
		// count campaign still validated
		int countCampaignStillValidated = countCompaignStillValidated();
		dataInfoHomeManager.setCount_campaign_still_validated(String
				.valueOf(countCampaignStillValidated));
		// total car is running
		int countCarRunning = countCarIsRunning();
		dataInfoHomeManager.setCount_car_is_running(String
				.valueOf(countCarRunning));
		formatTemplate.setStatus(Global.status_ok);
		formatTemplate.setData(dataInfoHomeManager);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

}
