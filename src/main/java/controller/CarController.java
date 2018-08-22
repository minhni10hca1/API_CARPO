package controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;

import model.Campaign_CM;
import model.Car;
import model.Global;
import model.GlobalErrorCode;
import model.JsonFormatError;
import model.JsonFormatTemplate;
import model.LocationGoogle;
import model.Tracking;
import model_json.formatDataGroupMember;
import model_json.formatDataInfoUserDriverCarEndPoint_CM;
import model_json.formatDataInfoUserDriverCar_CM;
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

import service.CampaignService;
import service.CarGroupService;
import service.CarService;
import service.CustomerService;
import service.DistrictService;
import service.HomeCalculatorKmBeforeService;
import service.TrackingService;
import service.UserService;
import util.FunctionUtil;
import util.GlobalUtils;

@RestController
public class CarController {

	@Autowired
	CarService service;

	@Autowired
	CarGroupService carGroupService;

	@Autowired
	HomeCalculatorKmBeforeService homeCalculatorKmBeforeService;

	@Autowired
	TrackingService trackingService;

	@Autowired
	UserService userService;

	@Autowired
	CampaignService campaignService;

	@Autowired
	CustomerService customerService;
	
	@Autowired
	DistrictService districtService;

	public static String result_sms = "";
	public static Boolean status_result = false;

	// // tam thoi khong can thiet den api nay do login da tra ve full info
	// @RequestMapping(value = "/getInfoCarByUserId", method = GET)
	// public String getinfoCarByUserID(@RequestParam String user_id) {
	// JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
	// JsonFormatError error = new JsonFormatError();
	// Car result = FunctionUtil.getInfoCarByUserId(user_id);
	// if (status_result) {
	// formatTemplate.setStatus(Global.status_ok);
	// formatTemplate.setData(result);
	// error.setCode(null);
	// error.setMessage(null);
	// formatTemplate.setError(error);
	// } else {
	// formatTemplate.setStatus(Global.status_fail);
	// formatTemplate.setData(null);
	// error.setCode(Global.status_code_bad_request);
	// error.setMessage(result_sms);
	// formatTemplate.setError(error);
	// }
	// Gson gson = new GsonBuilder().setPrettyPrinting().create();
	// String json = gson.toJson(formatTemplate);
	// return json;
	//
	// }

	@RequestMapping(value = "/get-group-member-by-leader-id", method = GET)
	public String getGroupMemberByLeaderId(
			@RequestParam(required = false) String leader_id) {
		if (leader_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document do_car_group = carGroupService
				.getCarGroupByLeaderId(leader_id);
		if (do_car_group != null) {
			String group_id = do_car_group.get("_id").toString();
			ArrayList<org.bson.Document> list_car = service
					.getListCarByGroupId(group_id);
			if (list_car.size() > 0) {
				// co data
				ArrayList<formatDataGroupMember> listData = new ArrayList<formatDataGroupMember>();
				for (org.bson.Document document_car : list_car) {
					formatDataGroupMember dataGroupMember = new formatDataGroupMember();
					// dataGroupMember.setDriver_id(document_car.get("driver_id")
					// .toString());
					String user_id = document_car.get("user_id").toString();
					org.bson.Document document_user = userService
							.getInfoUserByUserId(user_id);
					if (document_user != null) {
						dataGroupMember.setName(document_user.get("fullname")
								.toString());
						dataGroupMember.setPhone(document_user.get("phone")
								.toString());
						dataGroupMember.setUser_id(user_id);
						listData.add(dataGroupMember);
					}

				}
				formatTemplate.setStatus(Global.status_ok);
				formatTemplate.setData(listData);
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

	@RequestMapping(value = "/get-group-member-history-by-date", method = GET)
	public String getMemberHistoryByDate(
			@RequestParam(required = false) String date,
			@RequestParam(required = false) String user_id) {
		if (date == null || user_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		formatJsonTrackingInfo formatJsonTrackingInfo = new formatJsonTrackingInfo();
		Car car = FunctionUtil.getInfoCarByUserId(user_id);
		if (car.get_id() != null) {
			String device_id = car.getDevice_id();
			ArrayList<LocationGoogle> listLocation = trackingService
					.getListLocationTrackingByDateAndDevice_id(date, device_id);
			if (listLocation.size() > 0) {
				// co data
				formatTemplate.setStatus(Global.status_ok);
				// double distanceTotal = FunctionUtil
				// .totalDistanceByListLocation(listLocation);
				double distanceTotal = trackingService
						.getMaxDistanceByDeviceIdAndDate(device_id, date);
				formatJsonTrackingInfo.setTotal_distace(distanceTotal);
				formatJsonTrackingInfo.setList_location(listLocation);
				// new -> using for customer
				org.bson.Document documentUser = FunctionUtil
						.getInfoDocumentUserByUser_id(user_id);
				if (documentUser != null)
					formatJsonTrackingInfo.setName(documentUser.get("fullname")
							.toString());
				// end
				formatTemplate.setData(formatJsonTrackingInfo);
			} else {
				// khong co data
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_find_data);
				error.setMessage(GlobalErrorCode.error_message_find_data);
				formatTemplate.setError(error);
			}
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

	/* using for C-M */
	@RequestMapping(value = "/get-list-user-by-customer-id", method = GET)
	public String getListUserByCustomer(
			@RequestParam(required = false) String customer_id) {
		if (customer_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document documenCustomer = customerService
				.getDocumentCustomerByUser_id(customer_id);
		if (documenCustomer != null)
			customer_id = documenCustomer.get("_id").toString();
		else
			customer_id = "";
		if (!customer_id.isEmpty()) {
			// org.bson.Document do_campaign = campaignService
			// .getInfoCampaignByCustomerId(customer_id);
			ArrayList<Campaign_CM> listCampaignByCustomer = campaignService
					.getListCampaignByCustomerId(customer_id);
			if (listCampaignByCustomer.size() > 0) {
				ArrayList<formatDataInfoUserDriverCar_CM> listData = new ArrayList<formatDataInfoUserDriverCar_CM>();
				for (Campaign_CM campaignCm : listCampaignByCustomer) {
					String campaign_id = campaignCm.get_id();// do_campaign.get("_id").toString();
					ArrayList<org.bson.Document> list_car = service
							.getListCarByCampaign(campaign_id);
					if (list_car.size() > 0) {
						// co data

						for (org.bson.Document document_car : list_car) {
							try {
								formatDataInfoUserDriverCar_CM car_CM = new formatDataInfoUserDriverCar_CM();
								String user_id = document_car.get("user_id")
										.toString();
								// car_CM.setDriver_id(document_car.get("driver_id")
								// .toString());
								car_CM.setType(document_car.get("type")
										.toString());
								car_CM.setUser_id(document_car.get("user_id")
										.toString());
								car_CM.setCampaign_id(document_car.get(
										"campaign_id").toString());
								car_CM.setCar_color(document_car.get(
										"car_color").toString());
								car_CM.setLicense_plate(document_car.get(
										"license_plate").toString());
								car_CM.setCar_manufacturer(document_car.get(
										"car_manufacturer").toString());
								org.bson.Document document_user = userService
										.getInfoUserByUserId(user_id);
								if (document_user != null) {
									car_CM.setName(document_user
											.get("fullname").toString());
									car_CM.setPhone(document_user.get("phone")
											.toString());
									listData.add(car_CM);
								}
							} catch (Exception ex) {
								System.out.print(document_car);
								;
							}
						}

						// formatTemplate.setStatus(Global.status_ok);
						// formatTemplate.setData(listData);
					}
					// else {
					// // ko co data
					// formatTemplate.setStatus(Global.status_fail);
					// error.setCode(GlobalErrorCode.error_code_find_data);
					// error.setMessage(GlobalErrorCode.error_message_find_data);
					// formatTemplate.setError(error);
					// }
				}
				formatTemplate.setStatus(Global.status_ok);
				formatTemplate.setData(listData);
			} else {
				// k co data
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

	private String getStrDateYesterDay() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date dateBeforeOneMonth = cal.getTime();
		String date = GlobalUtils.convertStringToDate(dateBeforeOneMonth);
		return date;
	}

	private String getStrDateToDay() {
		Calendar cal = Calendar.getInstance();
		Date dateBeforeOneMonth = cal.getTime();
		String date = GlobalUtils.convertStringToDate(dateBeforeOneMonth);
		return date;
	}

	private String getStr30DaysBefore() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -31);
		Date dateBeforeOneMonth = cal.getTime();
		String date = GlobalUtils.convertStringToDate(dateBeforeOneMonth);
		return date;
	}

	/* using for C-M */
	@RequestMapping(value = "/get-list-user-detail-by-customer-id", method = GET)
	public String getListUserDetailByCustomer(
			@RequestParam(required = false) String customer_id) {
		if (customer_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		String strDateYesterday = getStrDateYesterDay();
		String strDate30DaysBefore = getStr30DaysBefore();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document documenCustomer = customerService
				.getDocumentCustomerByUser_id(customer_id);
		if (documenCustomer != null)
			customer_id = documenCustomer.get("_id").toString();
		else
			customer_id = "";
		if (!customer_id.isEmpty()) {
			// org.bson.Document do_campaign = campaignService
			// .getInfoCampaignByCustomerId(customer_id);
			ArrayList<Campaign_CM> listCampaignByCustomer = campaignService
					.getListCampaignByCustomerId(customer_id);
			if (listCampaignByCustomer.size() > 0) {
				ArrayList<formatDataInfoUserDriverCar_CM> listData = new ArrayList<formatDataInfoUserDriverCar_CM>();
				for (Campaign_CM campaignCM : listCampaignByCustomer) {
					String campaign_id = campaignCM.get_id();// do_campaign.get("_id").toString();
					ArrayList<org.bson.Document> list_car = service
							.getListCarByCampaign(campaign_id);
					if (list_car.size() > 0) {
						// co data
						int status_user = 1;
						for (org.bson.Document document_car : list_car) {
							try {
								formatDataInfoUserDriverCar_CM car_CM = new formatDataInfoUserDriverCar_CM();
								String user_id = document_car.get("user_id")
										.toString();
								String device_id = document_car
										.get("device_id").toString();
								// car_CM.setDriver_id(document_car.get("driver_id")
								// .toString());
								car_CM.setType(document_car.get("type")
										.toString());
								car_CM.setUser_id(document_car.get("user_id")
										.toString());
								car_CM.setCampaign_id(document_car.get(
										"campaign_id").toString());
								car_CM.setCar_color(document_car.get(
										"car_color").toString());
								car_CM.setLicense_plate(document_car.get(
										"license_plate").toString());
								car_CM.setCar_manufacturer(document_car.get(
										"car_manufacturer").toString());
								if (status_user > 3)
									status_user = 1;
								car_CM.setStatus_user(String
										.valueOf(status_user));
								status_user += 1;
								org.bson.Document document_user = userService
										.getInfoUserByUserId(user_id);
								if (document_user != null) {
									car_CM.setName(document_user
											.get("fullname").toString());
									car_CM.setPhone(document_user.get("phone")
											.toString());
									// listData.add(car_CM);
								}
								// more new
								// date yesterday
								;
								// trackingService
								// .getDistanceYesterDayAndistance30DaysBeforeByDevice_IdAndBigerDate(
								// device_id, strDate30DaysBefore,
								// strDateYesterday);
								org.bson.Document documenBefore = homeCalculatorKmBeforeService
										.getInfoHomeCalculatorKmBeforeCampaignAndDeviceId(
												campaign_id, device_id);
								if (documenBefore != null) {
									car_CM.setTotal_km_yesterday(String
											.valueOf(documenBefore.get(
													"total_km_yesterday")
													.toString()));
									car_CM.setTotal_km_30_day_before(String
											.valueOf(documenBefore.get(
													"total_km_30_day_before")
													.toString()));
								} else {
									car_CM.setTotal_km_yesterday(String
											.valueOf("0"));
									car_CM.setTotal_km_30_day_before(String
											.valueOf("0"));
								}
								// ; car_CM.setTotal_km_yesterday(String
								// .valueOf(distanceYesterday));
								// car_CM.setTotal_km_30_day_before(String
								// .valueOf(distance30DaysBefore));
								listData.add(car_CM);
								// distance yesterdat
								// ArrayList<LocationGoogle> listYesterday =
								// trackingService.getListLocationTrackingByDateAndDevice_id(strDateYesterday,
								// device_id);
								// double distanceYesterday =
								// FunctionUtil.totalDistanceByListLocation(listYesterday);
								// double distanceYesterday =
								// trackingService.getMaxDistanceByDevice_IdAndDate(device_id,strDateYesterday);
								// car_CM.setTotal_km_yesterday(String.valueOf(distanceYesterday));
								// distance in 30 day before
								// ArrayList<LocationGoogle> list30DaysBefore =
								// trackingService.getListLocationTrackingByBigerDateAndDevice_id(strDate30DaysBefore,
								// device_id);
								// double distance30DaysBefore =
								// FunctionUtil.totalDistanceByListLocation(list30DaysBefore);
								// double distance30DaysBefore =
								// trackingService.getMaxDistanceByDevice_IdAndBigerDate(device_id,strDate30DaysBefore);
								// car_CM.setTotal_km_30_day_before(String.valueOf(distance30DaysBefore));
							} catch (Exception ex) {

							}
						}
						// formatTemplate.setStatus(Global.status_ok);
						// formatTemplate.setData(listData);
					}
					// else {
					// // ko co data
					// formatTemplate.setStatus(Global.status_fail);
					// error.setCode(GlobalErrorCode.error_code_find_data);
					// error.setMessage(GlobalErrorCode.error_message_find_data);
					// formatTemplate.setError(error);
					// }
				}
				formatTemplate.setStatus(Global.status_ok);
				formatTemplate.setData(listData);
			} else {
				// k co data
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

	//
	String strToday, strFromDate, strToDate;
	double totalKMInCampaignPartDriver = 0;
	double totalKMInCampaignPartDriverOutCty = 0;
	double totalKmToDay = 0;
	double totalKMInThreeDayBefore = 0;
	double totalKMInSevenDayBefore = 0;
	double persent = 0;
	Date date_from_part = new Date();
	Date date_to_part = new Date();
	Boolean status = false;

	public void getFromDatePartAndToDatePart(String campaign_id)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today_part = sdf.parse(sdf.format(new Date()));
		ArrayList<org.bson.Document> documentPartCampaign = campaignService
				.getInfoCampaignPartDriverByCampaignId(campaign_id);
		if (documentPartCampaign.size() <= 0) {
			campaignService.calculator_campaign_part_driver(campaign_id);
			documentPartCampaign = campaignService
					.getInfoCampaignPartDriverByCampaignId(campaign_id);
		}
		for (org.bson.Document document : documentPartCampaign) {
			try {
				Date from_date_db = GlobalUtils.convertStrToDate(document.get(
						"from_date").toString());
				Date to_date_db = GlobalUtils.convertStrToDate(document.get(
						"to_date").toString());
				// from <= today <= to
				int compare1 = from_date_db.compareTo(today_part);
				int compare2 = today_part.compareTo(to_date_db);
				if (compare1 <= 0 && compare2 <= 0) {
					date_from_part = from_date_db;
					date_to_part = to_date_db;
					status = true;
					// set -1 +1 for query chinh sat -> chi 1 lan
					Calendar cal_new = Calendar.getInstance();
					cal_new.setTime(date_from_part);
					cal_new.add(Calendar.DATE, -1); // because < > not =
					date_from_part = cal_new.getTime();
					// + 1
					cal_new.setTime(date_to_part);
					cal_new.add(Calendar.DATE, 1); // because < > not =
					date_to_part = cal_new.getTime();
					break;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public double getTotalKmFromTwoDate(String strBeginDate, String strEndDate,
			String device_id) {
		// ArrayList<LocationGoogle> listLocation = service
		// .getListLocationTrackingByTwoDateAndDevice_id(strBeginDate,
		// strEndDate, device_id);
		// double distance = FunctionUtil
		// .totalDistanceByListLocation(listLocation);
		// distance = FunctionUtil.roundDouble(distance);
		double distance = trackingService
				.getSumDistanceTrackingByTwoDateAndDevice_id(strBeginDate,
						strEndDate, device_id);
		return distance;
	}

	public double getTotalKmFromTwoDateOutCty(
			String area_code, String strBeginDate,
			String strEndDate, String device_id) {
		// get list
		ArrayList<String> list_district_name_cty = districtService.getInfoDocumentDistrictCalculatorByArea_Code(area_code);
		ArrayList<LocationGoogle> listLocation = trackingService
				.getListLocationTrackingByTwoDateAndDevice_idAndOutCty(
						list_district_name_cty, strBeginDate, strEndDate,
						device_id);
		double distance = FunctionUtil
				.totalDistanceByListLocation(listLocation);
		distance = FunctionUtil.roundDouble(distance);
		return distance;
	}

	// get total km from today
	public double getTotalKmFromToday(String strToday, String device_id) {
		ArrayList<LocationGoogle> listLocation = trackingService
				.getListLocationTrackingByDateAndDevice_id(strToday, device_id);
		double distance = FunctionUtil
				.totalDistanceByListLocation(listLocation);
		distance = FunctionUtil.roundDouble(distance);
		return distance;
	}

	private void calculatorKmForHomeDriver(String campaign_id, String device_id) {
		if (status) {
			// + 1 AND -1
			double totalKMInMonth = getTotalKmFromTwoDate(
					GlobalUtils.convertStringToDate(date_from_part),
					GlobalUtils.convertStringToDate(date_to_part), device_id);
			// new -> calculator total km
			// percent km in month
			org.bson.Document documentCampaign = campaignService
					.getInfoCampaignById(campaign_id);
			double total_distance_campaign = 0;
			double totalKMInMonthOutCty = 0;
			String area_code = "";
			if (documentCampaign != null) {
				try {
					total_distance_campaign = Double
							.parseDouble(documentCampaign.get("total_distance")
									.toString());
					area_code = documentCampaign.get("area_code").toString();
				} catch (Exception ex) {
					total_distance_campaign = 0;
				}
			}
			if(!area_code.isEmpty()){
				totalKMInMonthOutCty = getTotalKmFromTwoDateOutCty(area_code, GlobalUtils.convertStringToDate(date_from_part), GlobalUtils.convertStringToDate(date_to_part), device_id);
			}
			double persent = (totalKMInMonth * 100) / total_distance_campaign;
			if (persent > 100)
				persent = 100;
			// total km today
			setDateToDay();
			// double totalKmToDay = getTotalKmFromToday(strToday, device_id);
			double totalKmToDay = trackingService
					.getMaxDistanceByDeviceIdAndDate(device_id, strToday);
			// total km 3 day before
			setDateThreeDayBefore();
			double totalKMInThreeDayBefore = getTotalKmFromTwoDate(strFromDate,
					strToDate, device_id);
			// total km 7 day before
			setDateSevenDayBefore();
			double totalKMInSevenDayBefore = getTotalKmFromTwoDate(strFromDate,
					strToDate, device_id);
			// dataInfoHome.setTotal_km_month(FunctionUtil
			// .roundDouble(totalKMInMonth));
			// dataInfoHome.setTotal_percent_month(FunctionUtil
			// .roundDouble(persent));
			// dataInfoHome.setTotal_km_today(FunctionUtil
			// .roundDouble(totalKmToDay));
			// dataInfoHome.setTotal_km_three_day_before(FunctionUtil
			// .roundDouble(totalKMInThreeDayBefore));
			// dataInfoHome.setTotal_km_seven_day_before(FunctionUtil
			// .roundDouble(totalKMInSevenDayBefore));
			// dataInfoHome
			this.totalKMInCampaignPartDriver = totalKMInMonth;
			this.persent = persent;
			this.totalKmToDay = totalKmToDay;
			this.totalKMInThreeDayBefore = totalKMInThreeDayBefore;
			this.totalKMInSevenDayBefore = totalKMInSevenDayBefore;
			this.totalKMInCampaignPartDriverOutCty = totalKMInMonthOutCty;

		} else {
			this.totalKMInCampaignPartDriver = 0;
			this.totalKmToDay = 0;
			this.totalKMInThreeDayBefore = 0;
			this.totalKMInSevenDayBefore = 0;
			this.persent = 0;
		}
	}

	// script calculator km before -> calculator at 01 AM in day
	@RequestMapping(value = "/save-home-calculator-before", method = GET)
	public String saveHomeCalculatorKmBefore() throws ParseException {
		String strDateYesterday = getStrDateYesterDay();
		String strDate30DaysBefore = getStr30DaysBefore();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		ArrayList<Campaign_CM> listCampaignByCustomer = campaignService
				.getAllCampaignStillValidated();
		if (listCampaignByCustomer.size() > 0) {
			ArrayList<formatDataInfoUserDriverCar_CM> listData = new ArrayList<formatDataInfoUserDriverCar_CM>();
			for (Campaign_CM campaignCM : listCampaignByCustomer) {
				String campaign_id = campaignCM.get_id();// do_campaign.get("_id").toString();
				getFromDatePartAndToDatePart(campaign_id); // lay fromdate
															// todate part trong
															// hd
				ArrayList<org.bson.Document> list_car = service
						.getListCarByCampaign(campaign_id);
				if (list_car.size() > 0) {
					// co data
					homeCalculatorKmBeforeService
							.deleteHomeCalculatorKmBefore(campaign_id);
					int status_user = 1;
					for (org.bson.Document document_car : list_car) {
						formatDataInfoUserDriverCar_CM car_CM = new formatDataInfoUserDriverCar_CM();
						String user_id = document_car.get("user_id").toString();
						String device_id = document_car.get("device_id")
								.toString();
						trackingService
								.getDistanceYesterDayAndistance30DaysBeforeByDevice_IdAndBigerDate(
										device_id, strDate30DaysBefore,
										strDateYesterday);
						LocationGoogle endPoint_db = trackingService
								.getEndPointByDeviceId(device_id);
						// new function update 29/03 - luu them so km cua tai xe
						// theo part ngay trong HD - 3 ngay - 7 ngay - tong km
						// trong HD
						calculatorKmForHomeDriver(campaign_id, device_id);
						System.out.println("device" + device_id);
						System.out.println("today" + totalKmToDay);
						System.out.println("month"
								+ totalKMInCampaignPartDriver);

						// end
						service.calculatorHomeKmBefore(campaign_id, user_id,
								device_id, distanceYesterday,
								distance30DaysBefore, endPoint_db,
								totalKMInCampaignPartDriver, totalKmToDay,
								totalKMInThreeDayBefore,
								totalKMInSevenDayBefore, persent,totalKMInCampaignPartDriverOutCty);
						// listData.add(car_CM);
					}
					formatTemplate.setStatus(Global.status_ok);
					formatTemplate.setData(listData);
				} else {
					// k co data
					formatTemplate.setStatus(Global.status_fail);
					error.setCode(GlobalErrorCode.error_code_find_data);
					error.setMessage(GlobalErrorCode.error_message_find_data);
					formatTemplate.setError(error);
				}
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

	// script update endPoint car -> update
	@RequestMapping(value = "/update-end-point-car-in-home-calculator-km-before", method = GET)
	public String updateEndPointCar() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
//		ArrayList<Campaign_CM> listCampaignByCustomer = campaignService
//				.getAllCampaign();
		ArrayList<Campaign_CM> listCampaignByCustomer = campaignService
				.getAllCampaignStillValidated();
		if (listCampaignByCustomer.size() > 0) {
			ArrayList<formatDataInfoUserDriverCar_CM> listData = new ArrayList<formatDataInfoUserDriverCar_CM>();
			for (Campaign_CM campaignCM : listCampaignByCustomer) {
				String campaign_id = campaignCM.get_id();// do_campaign.get("_id").toString();
				ArrayList<org.bson.Document> list_car = service
						.getListCarByCampaign(campaign_id);
				if (list_car.size() > 0) {
					// co data
					for (org.bson.Document document_car : list_car) {
						formatDataInfoUserDriverCar_CM car_CM = new formatDataInfoUserDriverCar_CM();
						String user_id = document_car.get("user_id").toString();
						String device_id = document_car.get("device_id")
								.toString();
						org.bson.Document oldData = homeCalculatorKmBeforeService
								.getInfoHomeCalculatorKmBeforeCampaignAndDeviceId(
										campaign_id, device_id);
						LocationGoogle endPoint = trackingService
								.getEndPointByDeviceId(device_id);
						Date today = Calendar.getInstance().getTime();
						if (endPoint.getLocation_lat() != null && oldData != null)
							homeCalculatorKmBeforeService
									.updateEndPointForDriver(
											campaign_id,
											device_id,
											endPoint,
											oldData,
											GlobalUtils
													.convertStringToDate(today),
											GlobalUtils
													.convertStringToTime(today));
					}
					formatTemplate.setStatus(Global.status_ok);
					formatTemplate.setData(listData);
				} else {
					// k co data
					formatTemplate.setStatus(Global.status_fail);
					error.setCode(GlobalErrorCode.error_code_find_data);
					error.setMessage(GlobalErrorCode.error_message_find_data);
					formatTemplate.setError(error);
				}
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

	public static double distanceYesterday = 0;
	public static double distance30DaysBefore = 0;
	public static String location_lat;
	public static String location_long;
	public static String distance_max_in_day_near = "";

	/* using for C-M */
	@RequestMapping(value = "/get-list-user-end-point-by-customer-id", method = GET)
	public String getListUserEndPointByCustomer(
			@RequestParam(required = false) String customer_id) {
		String strDateYesterday = getStrDateYesterDay();
		String strDate30DaysBefore = getStr30DaysBefore();
		if (customer_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document documenCustomer = customerService
				.getDocumentCustomerByUser_id(customer_id);
		if (documenCustomer != null)
			customer_id = documenCustomer.get("_id").toString();
		else
			customer_id = "";
		if (!customer_id.isEmpty()) {
			ArrayList<Campaign_CM> do_campaign_list = campaignService
					.getListCampaignByCustomerId(customer_id);
			if (do_campaign_list.size() > 0) {
				ArrayList<formatDataInfoUserDriverCarEndPoint_CM> listData = new ArrayList<formatDataInfoUserDriverCarEndPoint_CM>();
				for (Campaign_CM do_campaign : do_campaign_list) {
					String campaign_id = do_campaign.get_id();
					ArrayList<org.bson.Document> list_car = service
							.getListCarByCampaign(campaign_id);
					if (list_car.size() > 0) {
						// co data
						int status_user = 1;
						for (org.bson.Document document_car : list_car) {
							try {
								formatDataInfoUserDriverCarEndPoint_CM car_CM = new formatDataInfoUserDriverCarEndPoint_CM();
								String user_id = document_car.get("user_id")
										.toString();
								String device_id = document_car
										.get("device_id").toString();
								car_CM.setUser_id(user_id);
								// new
								// String strDateYesterday =
								// getStrDateYesterDay();
								// distance yesterdat
								// ArrayList<LocationGoogle> listYesterday =
								// trackingService.getListLocationTrackingByDateAndDevice_id(strDateYesterday,
								// device_id);
								// double distanceYesterday =
								// FunctionUtil.totalDistanceByListLocation(listYesterday);
								// double distanceYesterday =
								// trackingService.getMaxDistanceByDevice_IdAndDate(device_id,strDateYesterday);
								// car_CM.setTotal_km_yesterday(String.valueOf(distanceYesterday));
								// distance in 30 day before
								// String strDate30DaysBefore =
								// getStr30DaysBefore();
								// ArrayList<LocationGoogle> list30DaysBefore =
								// trackingService.getListLocationTrackingByBigerDateAndDevice_id(strDate30DaysBefore,
								// device_id);
								// double distance30DaysBefore =
								// FunctionUtil.totalDistanceByListLocation(list30DaysBefore);
								// double distance30DaysBefore =
								// trackingService.getMaxDistanceByDevice_IdAndBigerDate(device_id,strDate30DaysBefore);
								// car_CM.setTotal_km_30_day_before(String.valueOf(distance30DaysBefore));

								// before
								// trackingService
								// .getDistanceYesterDayAndistance30DaysBeforeByDevice_IdAndBigerDate(
								// device_id, strDate30DaysBefore,
								// strDateYesterday);
								// car_CM.setTotal_km_yesterday(String
								// .valueOf(distanceYesterday));
								// car_CM.setTotal_km_30_day_before(String
								// .valueOf(distance30DaysBefore));
								// end

								org.bson.Document documenBefore = homeCalculatorKmBeforeService
										.getInfoHomeCalculatorKmBeforeCampaignAndDeviceId(
												campaign_id, device_id);
								LocationGoogle endPoint = new LocationGoogle();
								if (documenBefore != null) {
									car_CM.setTotal_km_yesterday(String
											.valueOf(documenBefore.get(
													"total_km_yesterday")
													.toString()));
									car_CM.setTotal_km_30_day_before(String
											.valueOf(documenBefore.get(
													"total_km_30_day_before")
													.toString()));
									// end point

									endPoint.setLocation_lat(documenBefore.get(
											"location_lat").toString());
									endPoint.setLocation_long(documenBefore
											.get("location_long").toString());
									car_CM.setLocation(endPoint);
								} else {
									car_CM.setTotal_km_yesterday(String
											.valueOf("0"));
									car_CM.setTotal_km_30_day_before(String
											.valueOf("0"));
									car_CM.setLocation(endPoint);
								}

								// end
								car_CM.setLicense_plate(document_car.get(
										"license_plate").toString());
								// end
								org.bson.Document documentUser = userService
										.getInfoUserByUserId(user_id);
								if (documentUser != null)
									car_CM.setName(documentUser.get("fullname")
											.toString());
								// ArrayList<LocationGoogle> listLocation =
								// trackingService
								// .getListLocationTrackingByDevice_id(
								// device_id);
								// get latlong by distance and device_id near
								// day ->
								// get
								// only one

								// before
								// trackingService.getLatLongByDevice_IdAndDistance(
								// device_id, distance_max_in_day_near);
								// end
								// LocationGoogle endPoint_db =
								// trackingService.getEndPointByDeviceId(device_id);
								// if (listLocation.size() > 0)
								// endPoint = listLocation
								// .get(listLocation.size() - 1);
								// car_CM.setLocation(endPoint_db);
								// more new
								if (status_user > 3)
									status_user = 1;
								car_CM.setStatus_user(String
										.valueOf(status_user));
								status_user += 1;
								listData.add(car_CM);
							} catch (Exception ex) {
								System.out.print("error:" + ex.getMessage());
							}
						}

					}
				}
				if (listData.size() > 0) {
					formatTemplate.setStatus(Global.status_ok);
					formatTemplate.setData(listData);
				} else {
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

}
