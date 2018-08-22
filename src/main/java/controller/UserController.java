package controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
import model.UserSecurity;
import model_json.formatDataImage;
import model_json.formatDataInfoUser;
import model_json.formatJsonTrackingInfo;

import org.apache.catalina.mbeans.GlobalResourcesLifecycleListener;
import org.apache.commons.httpclient.HttpClient;
import org.apache.hadoop.hdfs.server.namenode.status_jsp;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.hibernate.annotations.NotFound;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;
import com.example.api_carpo.ApiCarpoApplication;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mysql.fabric.Response;

import security.WebSecurityConfig;
import service.CarService;
import service.ConfirmCarStatusService;
import service.OtpService;
import service.TrackingService;
import service.UserService;
import util.FunctionUtil;
import util.GlobalUtils;

@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@RestController
public class UserController {

	@Autowired
	UserService service;

	@Autowired
	OtpService otp_service;

	@Autowired
	CarService car_service;

	@Autowired
	TrackingService tracking_service;

	@Autowired
	ConfirmCarStatusService confirmCarStatusService;

	public static String result_sms = "";
	public static Boolean status_result = false;

	@RequestMapping(value = "/created-new-password-user", method = POST)
	public String createdNewPasswordUser(
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String newPassword,
			@RequestParam(required = false) String otp) {
		if (phone == null || newPassword == null || otp == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document userDocument = FunctionUtil
				.getInfoDocumentUserByPhone(phone);
		Date today = Calendar.getInstance().getTime();
		if (userDocument != null) {
			String user_id = userDocument.get("_id").toString();
			// have info
			// check otp
			int status = -1;
			String status_code_error = "";
			String message_error = "";
			String result = otp_service.checkOtp(user_id, otp);
			if (result.isEmpty()) {
				FunctionUtil
						.writeLogCrawler(
								GlobalUtils.convertStringToDate(today)
										+ "change password - created-new-password-user:"
										+ user_id + " begin",
								Global.log_write_carpo);
				service.changePassword(user_id, newPassword, userDocument);
				inMemoryUserDetailsManager.deleteUser(phone);
				inMemoryUserDetailsManager
						.createUser(new org.springframework.security.core.userdetails.User(
								phone, newPassword,
								new ArrayList<GrantedAuthority>()));
				FunctionUtil
						.writeLogCrawler(
								GlobalUtils.convertStringToDate(today)
										+ "change password - created-new-password-user:"
										+ user_id + " end1",
								Global.log_write_carpo);
				status = Global.status_ok;
			} else if (result.equals("0")) {
				// error
				status = Global.status_fail;
				status_code_error = GlobalErrorCode.error_code_find_data;
				message_error = GlobalMessageScreen.otp_not_found_with_user;

			} else if (result.equals("1")) {
				status = Global.status_fail;
				status_code_error = GlobalErrorCode.error_code_data_not_match;
				message_error = GlobalMessageScreen.otp_not_match;
			} else if (result.equals("2")) {
				status = Global.status_fail;
				status_code_error = GlobalErrorCode.error_code_expire;
				message_error = GlobalMessageScreen.otp_expired;
			} else {
				status = Global.status_fail;
				status_code_error = GlobalErrorCode.error_code_find_data;
				message_error = GlobalMessageScreen.user_created_new_password_fail;
			}
			formatTemplate.setStatus(status);
			if (status != Global.status_ok) {
				error.setCode(status_code_error);
				error.setMessage(message_error);
				formatTemplate.setError(error);
			} else
				formatTemplate.setData("");
		} else {
			// can't find user by phone
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalMessageScreen.user_not_found);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	@RequestMapping(value = "/created-new-password-user-not-otp", method = POST)
	public String createdNewPasswordUserNotOtp(
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String newPassword) {
		if (phone == null || newPassword == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document userDocument = FunctionUtil
				.getInfoDocumentUserByPhone(phone);
		Date today = Calendar.getInstance().getTime();
		if (userDocument != null) {
			String user_id = userDocument.get("_id").toString();
			int status = -1;
			String status_code_error = "";
			String message_error = "";
			FunctionUtil
			.writeLogCrawler(
					GlobalUtils.convertStringToDate(today)
							+ "change password - created-new-password-user-not-otp:"
							+ user_id + " begin",
					Global.log_write_carpo);
			service.changePassword(user_id, newPassword, userDocument);
			inMemoryUserDetailsManager.deleteUser(phone);
			inMemoryUserDetailsManager
					.createUser(new org.springframework.security.core.userdetails.User(
							phone, newPassword,
							new ArrayList<GrantedAuthority>()));
			FunctionUtil
			.writeLogCrawler(
					GlobalUtils.convertStringToDate(today)
							+ "change password - created-new-password-user-not-otp:"
							+ user_id + " end1",
					Global.log_write_carpo);
			status = Global.status_ok;
			formatTemplate.setStatus(status);
			if (status != Global.status_ok) {
				error.setCode(status_code_error);
				error.setMessage(message_error);
				formatTemplate.setError(error);
			} else
				formatTemplate.setData("");
		} else {
			// can't find user by phone
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalMessageScreen.user_not_found);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	@RequestMapping(value = "/get-info-member-by-user-id", method = GET)
	@ResponseBody
	public String getInfoUserByUserId(
			@RequestParam(required = false) String user_id) {
		// check params before
		if (user_id == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		formatDataInfoUser dataInfoUser = new formatDataInfoUser();
		Car car = FunctionUtil.getInfoCarByUserId(user_id);
		org.bson.Document documentUser = FunctionUtil
				.getInfoDocumentUserByUser_id(user_id);
		if (documentUser != null && !user_id.isEmpty()) {
			dataInfoUser.set_id(documentUser.get("_id").toString());
			// dataInfoUser.setRole_id(documentUser.get("role_id").toString());
			dataInfoUser.setPhone(documentUser.get("phone").toString());
			dataInfoUser.setFullname(documentUser.get("fullname").toString());
			dataInfoUser.setEmail(documentUser.get("email").toString());
			dataInfoUser.setToken(documentUser.get("token").toString());
			dataInfoUser.setPhoto(documentUser.get("photo").toString());
			dataInfoUser.setFace_id(documentUser.get("face_id").toString());
			dataInfoUser.setGoogle_id(documentUser.get("google_id").toString());
			dataInfoUser.setCreated_time(documentUser.get("created_time")
					.toString());
			// new
			dataInfoUser.setBirthday(documentUser.get("birthday").toString());
			dataInfoUser.setSex(documentUser.get("sex").toString());
			// set total distance
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -30);
			Date dateBeforeOneMonth = cal.getTime();
			String date = GlobalUtils.convertStringToDate(dateBeforeOneMonth);
			String device_id = car.getDevice_id();
			ArrayList<LocationGoogle> listLocation = tracking_service
					.getListLocationTrackingByBigerDateAndDevice_id(date,
							device_id);
			double distanceTotal = 0;
			if (listLocation.size() > 0)
				distanceTotal = FunctionUtil
						.totalDistanceByListLocation(listLocation);
			distanceTotal = FunctionUtil.roundDouble(distanceTotal);
			dataInfoUser.setTotal_distance_run_one_month(String
					.valueOf(distanceTotal));
			// more info
			if (car.get_id() != null) {
				dataInfoUser.setCar_id(car.get_id());
				dataInfoUser.setType(car.getType());
				dataInfoUser.setCampaign_id(car.getCampaign_id());
				dataInfoUser.setCar_color(car.getCar_color());
				dataInfoUser.setLicense_plate(car.getLicense_plate());
				dataInfoUser.setCar_manufacturer(car.getCar_manufacturer());
				// dataInfoUser.setDriver_id(car.getDriver_id());
				dataInfoUser.setGroup_id(car.getGroup_id());
			}
			formatTemplate.setStatus(Global.status_ok);
			formatTemplate.setData(dataInfoUser);

		} else {
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalMessageScreen.user_not_found);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	final InMemoryUserDetailsManager inMemoryUserDetailsManager;

	@Autowired
	private UserController(InMemoryUserDetailsManager inMemoryUserDetailsManager) {
		this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
	}

	// @Autowired
	// InMemoryUserDetailsManager detailsManager;

	// @RequestMapping(value = "/change-password-user", method = POST)
	// public String changePasswordUser(
	// @RequestParam(required = false) String user_id,
	// @RequestParam(required = false) String old_password,
	// @RequestParam(required = false) String new_password) {
	// if (user_id == null || old_password == null || new_password == null)
	// return FunctionUtil.getJsonErrorForRequestParams();
	// JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
	// JsonFormatError error = new JsonFormatError();
	// org.bson.Document userDocument = FunctionUtil
	// .getInfoDocumentUserByUser_id(user_id);
	// if (userDocument != null) {
	// String old_password_db = userDocument.get("password").toString();
	// // have info
	// int status = -1;
	// String status_code_error = "";
	// String message_error = "";
	// if (old_password.equals(old_password_db)) {
	// if (service.changePassword(user_id, new_password, userDocument)) {
	// status = Global.status_ok;
	// // register sys
	// // inMemoryUserDetailsManager =
	// String phone = userDocument.get("phone").toString();
	// String role_code = userDocument.get("role_code").toString();
	// String userName = userDocument.get("username").toString();
	// // WebSecurityConfig.registerNewUser(phone, new_password);
	// // WebSecurityConfig securityConfig = new
	// // WebSecurityConfig();
	// // try {
	// //
	// securityConfig.configure(WebSecurityConfig.authenticationManagerBuilder);
	// // } catch (Exception e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// inMemoryUserDetailsManager.deleteUser(phone);
	// inMemoryUserDetailsManager
	// .createUser(new org.springframework.security.core.userdetails.User(
	// phone, new_password,
	// new ArrayList<GrantedAuthority>()));
	// if(role_code.equals("3") || role_code.equals("4")){
	// inMemoryUserDetailsManager.deleteUser(userName);
	// inMemoryUserDetailsManager
	// .createUser(new org.springframework.security.core.userdetails.User(
	// userName, new_password,
	// new ArrayList<GrantedAuthority>()));
	// }
	// // ApiCarpoApplication.main(new String[]{});
	// // WebSecurityConfig securityConfig = new
	// // WebSecurityConfig();
	// // List<GrantedAuthority> authorities = new
	// // ArrayList<GrantedAuthority>();
	// // authorities.add(new SimpleGrantedAuthority("ADMIN"));
	// // UserDetails user = new UserSecurity(phone, new_password,
	// // authorities);
	// // userdetailma
	// // List<GrantedAuthority> authorities = new ArrayList<>();
	// // authorities.add(new SimpleGrantedAuthority("ADMIN"));
	// // SecurityContextHolder.getContext().setAuthentication(
	// // new UsernamePasswordAuthenticationToken(
	// // SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
	// //
	// SecurityContextHolder.getContext().getAuthentication().getCredentials(),
	// // authorities)
	// // );
	//
	// // InMemoryUserDetailsManager detailsManager = new
	// // InMemoryUserDetailsManager();
	//
	// //
	// inMemoryUserDetailsManager.createUser(org.springframework.security.core.userdetails.User.withUsername(phone).password(new_password).roles("ADMIN").build());
	//
	// } else {
	// status = Global.status_fail;
	// status_code_error = GlobalErrorCode.error_code_update_data;
	// message_error = GlobalMessageScreen.user_change_password_fail;
	// ;
	// }
	// } else {
	// // error
	// status = Global.status_fail;
	// status_code_error = GlobalErrorCode.error_code_data_not_match;
	// message_error = GlobalMessageScreen.user_input_old_password_fail;
	//
	// }
	// formatTemplate.setStatus(status);
	// if (status != Global.status_ok) {
	// error.setCode(status_code_error);
	// error.setMessage(message_error);
	// formatTemplate.setError(error);
	// } else
	// formatTemplate.setData("");
	// } else {
	// // can't find user by phone
	// formatTemplate.setStatus(Global.status_fail);
	// error.setCode(GlobalErrorCode.error_code_find_data);
	// error.setMessage(GlobalMessageScreen.user_not_found);
	// formatTemplate.setError(error);
	// }
	// Gson gson = new GsonBuilder().setPrettyPrinting().create();
	// String json = gson.toJson(formatTemplate);
	// return json;
	// }

	@RequestMapping(value = "/change-password-user", method = POST)
	public String changePasswordUser(
			@RequestParam(required = false) String user_id,
			@RequestParam(required = false) String old_password,
			@RequestParam(required = false) String new_password) {
		if (user_id == null || old_password == null || new_password == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document userDocument = FunctionUtil
				.getInfoDocumentUserByUser_id(user_id);
		if (userDocument != null) {
			String old_password_db = userDocument.get("password").toString();
			// have info
			int status = -1;
			String status_code_error = "";
			String message_error = "";
			if (old_password.equals(old_password_db)) {
				Date today = Calendar.getInstance().getTime();
				FunctionUtil.writeLogCrawler(
						GlobalUtils.convertStringToDate(today)
								+ "change password:" + user_id + " begin",
						Global.log_write_carpo);
				service.changePassword(user_id, new_password, userDocument);
				status = Global.status_ok;
				String phone = userDocument.get("phone").toString();
				String role_code = userDocument.get("role_code").toString();
				String userName = userDocument.get("username").toString();
				inMemoryUserDetailsManager.deleteUser(phone);
				inMemoryUserDetailsManager
						.createUser(new org.springframework.security.core.userdetails.User(
								phone, new_password,
								new ArrayList<GrantedAuthority>()));
				FunctionUtil.writeLogCrawler(
						GlobalUtils.convertStringToDate(today)
								+ "change password:" + user_id + " end1",
						Global.log_write_carpo);
				if (role_code.equals("3") || role_code.equals("4")) {
					inMemoryUserDetailsManager.deleteUser(userName);
					inMemoryUserDetailsManager
							.createUser(new org.springframework.security.core.userdetails.User(
									userName, new_password,
									new ArrayList<GrantedAuthority>()));
				}
				FunctionUtil.writeLogCrawler(
						GlobalUtils.convertStringToDate(today)
								+ "change password:" + user_id + " end2",
						Global.log_write_carpo);
			} else {
				// error
				status = Global.status_fail;
				status_code_error = GlobalErrorCode.error_code_data_not_match;
				message_error = GlobalMessageScreen.user_input_old_password_fail;

			}
			formatTemplate.setStatus(status);
			if (status != Global.status_ok) {
				error.setCode(status_code_error);
				error.setMessage(message_error);
				formatTemplate.setError(error);
			} else
				formatTemplate.setData("");
		} else {
			// can't find user by phone
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalMessageScreen.user_not_found);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/register-user-from-web", method = GET)
	public String registerUserFromWeb(
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String password) {
		System.out.println("phone:" + phone);
		System.out.println("password:" + password);
		if (phone == null || password == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		if (phone != null && password != null) {
			inMemoryUserDetailsManager.deleteUser(phone);
			inMemoryUserDetailsManager
					.createUser(new org.springframework.security.core.userdetails.User(
							phone, password, new ArrayList<GrantedAuthority>()));
			// update db
			org.bson.Document userDocument = FunctionUtil
					.getInfoDocumentUserByPhone(phone);
			if (userDocument != null) {
				String user_id = userDocument.get("_id").toString();
				service.changePassword(user_id, password, userDocument);
			}
			formatTemplate.setStatus(Global.status_ok);
			formatTemplate.setData("register is ok.");
		} else {
			// can't find user by phone
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalMessageScreen.user_not_found);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	private void saveImageAvata(String url_img, String img_name,
			String base_64_img) {
		Date today = Calendar.getInstance().getTime();
		try {
			String url = "";
			String[] arrImg = base_64_img.split(",");
			if (arrImg.length > 1)
				base_64_img = arrImg[1];
			byte[] decodedImg = Base64.getMimeDecoder().decode(
					base_64_img.getBytes(StandardCharsets.UTF_8));
			Path destinationFile = Paths.get(url_img, img_name + ".jpg");
			try {
				Files.write(destinationFile, decodedImg);
				FunctionUtil.writeLogCrawler(
						GlobalUtils.convertStringToDate(today)
								+ " save success", Global.log_write_img);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				String value = GlobalUtils.convertStringToDate(today)
						+ " error:" + e.getMessage();
				FunctionUtil.writeLogCrawler(value, Global.log_write_img);

			}
		} catch (Exception ex) {
			// System.out.print("error:" + ex.getMessage());
			String value = GlobalUtils.convertStringToDate(today) + " error:"
					+ ex.getMessage();
			FunctionUtil.writeLogCrawler(value, Global.log_write_img);

		}
	}

	@RequestMapping(value = "/change-avata-user", method = POST)
	public String changeAvataUser(
			@RequestParam(required = false) String user_id,
			@RequestParam(required = false) String image) {
		if (user_id == null || image == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document userDocument = FunctionUtil
				.getInfoDocumentUserByUser_id(user_id);
		if (userDocument != null) {
			Date today = Calendar.getInstance().getTime();
			String namePhoto = user_id + "_"
					+ GlobalUtils.convertStringToTime_SaveImg(today);
			// new
			FunctionUtil.createdForderReportIfNotExits(Global.url_image_avata);
			// end
			saveImageAvata(Global.url_image_avata, namePhoto, image);
			String pathImg = Global.host_domain + Global.mapping_image_avata
					+ "/" + namePhoto + Global.format_img;
			Boolean result = service
					.changeAvata(user_id, pathImg, userDocument);
			if (result) {
				// have info
				formatTemplate.setStatus(Global.status_ok);
				formatDataImage dataImage = new formatDataImage();
				dataImage.setImage_url(pathImg);
				formatTemplate.setData(dataImage);

			} else {
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_update_data);
				error.setMessage(GlobalMessageScreen.user_change_avata_fail);
				formatTemplate.setError(error);
			}
		} else {
			// can't find user by phone
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalMessageScreen.user_not_found);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	@RequestMapping(value = Global.mapping_image_avata + "/{imgName}", method = GET)
	public ResponseEntity<byte[]> viewImageAvata(
			@PathVariable("imgName") String imgName) {
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(Global.url_image_avata + "/" + imgName
					+ Global.format_img, "r");
			byte[] b = new byte[(int) f.length()];
			f.readFully(b);
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_PNG);
			return new ResponseEntity<byte[]>(b, headers, HttpStatus.CREATED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// private void MaHoa(){
	// String mahoa =
	// "$2a$10$kGCXCaaCKpjqs2M9IYFViOTos2MGNQg4fLfvSlb6E/31XT/3fjg6y";
	//
	//
	// }

	@RequestMapping(value = "/get-export-acount-tracking", method = GET)
	public String processExportAccountTracking() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		ArrayList<User> listUser = new ArrayList<User>();
		MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
		MongoCollection<org.bson.Document> do_campaigns = database
				.getCollection(Global.collection_tracking);

		List<BasicDBObject> listQuery = new ArrayList<BasicDBObject>();// =
																		// Arrays.asList(new
																		// Document("$group",
																		// new
																		// Document("_id",
																		// "$device_id")),new
																		// Document("$sort",
																		// new
																		// Document("_id.device_id",
																		// 1)));
		// group
		BasicDBObject query = new BasicDBObject();
		BasicDBObject obj1 = new BasicDBObject();
		obj1.append("_id", "$device_id");
		query.append("$group", obj1);
		// end
		// group
		BasicDBObject querySort = new BasicDBObject();
		BasicDBObject obj11 = new BasicDBObject();
		obj11.append("_id.device_id", 1);
		querySort.append("$sort", obj11);
		listQuery.add(query);
		listQuery.add(querySort);

		// AggregateIterable<Document> listDocumen =
		// do_campaigns.aggregate(Arrays.asList(new Document("$group", new
		// Document("_id", "$device_id")),new Document("$sort", new
		// Document("_id.device_id", 1))));
		AggregateIterable<Document> listDocumen = do_campaigns
				.aggregate(listQuery);

		listDocumen.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				// System.out.println(document.toJson());
				User user = new User();
				String device_id = document.get("_id").toString();
				Document documentCar = car_service
						.getInfoDocumentCarByDevice_Id(device_id);
				if (documentCar != null) {
					String user_id = documentCar.get("user_id").toString();
					Document userDocument = service
							.getInfoUserByUserId(user_id);
					user.setPhone(userDocument.get("phone").toString());
					user.setPassword(userDocument.get("password").toString());
					listUser.add(user);
				}
			}
		});

		String value = "";
		for (int index = 0; index < listUser.size(); index++) {
			value += '"' + listUser.get(index).getPhone() + '"' + ",";
		}
		System.out.println(value);

		// for (org.bson.Document document : listDocumen){
		// User user = new User();
		// String device_id = document.get("_id").toString();
		// Document documentCar =
		// car_service.getInfoDocumentCarByDevice_Id(device_id);
		// user.setPhone(documentCar.get("phone").toString());
		// user.setPassword(documentCar.get("password").toString());
		// listUser.add(user);
		// }
		// listDocumen.forEach(new Block<org.bson.Document>() {
		// @Override
		// public void apply(final org.bson.Document document) {
		// //listResult.add(document);
		// User user = new User();
		// String device_id = document.get("_id").toString();
		// Document documentCar =
		// car_service.getInfoDocumentCarByDevice_Id(device_id);
		// user.setPhone(documentCar.get("phone").toString());
		// user.setPassword(documentCar.get("password").toString());
		// listUser.add(user);
		// }
		// });
		formatTemplate.setStatus(Global.status_ok);
		JsonFormatError error2 = new JsonFormatError();
		error2.setMessage("count:" + listUser.size());
		formatTemplate.setError(error2);
		formatTemplate.setData(listUser);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	@RequestMapping(value = "/delete-url-confirm-car", method = GET)
	public String deleteUrlNotUsing() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		ArrayList<User> listUser = new ArrayList<User>();
		MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
		MongoCollection<org.bson.Document> do_campaigns = database
				.getCollection(Global.collection_confirm_car_staus);

		org.bson.Document query = new org.bson.Document();
		FindIterable<org.bson.Document> listDocumen = do_campaigns.find(query);
		listDocumen.forEach(new Block<org.bson.Document>() {
			@Override
			public void apply(final org.bson.Document document) {
				// System.out.println(document.toJson());
				User user = new User();
				String img = document.get("image").toString();
				URL url;
				// HttpClient client = HttpClientBuilder.create().build();
				// HttpGet request = new HttpGet("https://kodejava.org");
				//
				// try {
				// HttpResponse response = client.execute(request);
				// HttpEntity entity = response.getEntity();
				//
				// // Read the contents of an entity and return it as a String.
				// String content = EntityUtils.toString(entity);
				// System.out.println(content);
				// } catch (IOException e) {
				// e.printStackTrace();
				// }

			}
		});
		String json = "";
		return json;
	}

	// update change avata new
	private void saveImageAvataUpdate(String url_img, String img_name,
			MultipartFile fileImg) {
		Date today = Calendar.getInstance().getTime();
		try {
			String url = "";
			byte[] decodedImg = fileImg.getBytes();
			Path destinationFile = Paths.get(url_img, img_name + ".jpg");
			try {
				Files.write(destinationFile, decodedImg);
				FunctionUtil.writeLogCrawler(
						GlobalUtils.convertStringToDate(today)
								+ " save success", Global.log_write_img);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				String value = GlobalUtils.convertStringToDate(today)
						+ " error:" + e.getMessage();
				FunctionUtil.writeLogCrawler(value, Global.log_write_img);

			}
		} catch (Exception ex) {
			// System.out.print("error:" + ex.getMessage());
			String value = GlobalUtils.convertStringToDate(today) + " error:"
					+ ex.getMessage();
			FunctionUtil.writeLogCrawler(value, Global.log_write_img);

		}
	}

	@RequestMapping(value = "/change-avata-user-update", method = POST)
	public String changeAvataUserUpdate(
			@RequestParam(required = false) String user_id,
			@RequestParam(value = "file") MultipartFile fileImg) {
		if (user_id == null || fileImg == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		user_id = user_id.replace('"', ' ').trim();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document userDocument = FunctionUtil
				.getInfoDocumentUserByUser_id(user_id);
		if (userDocument != null) {
			Date today = Calendar.getInstance().getTime();
			String namePhoto = user_id + "_"
					+ GlobalUtils.convertStringToTime_SaveImg(today);
			// new
			FunctionUtil.createdForderReportIfNotExits(Global.url_image_avata);
			// end
			saveImageAvataUpdate(Global.url_image_avata, namePhoto, fileImg);
			String pathImg = Global.host_domain + Global.mapping_image_avata
					+ "/" + namePhoto + Global.format_img;
			Boolean result = service
					.changeAvata(user_id, pathImg, userDocument);
			if (result) {
				// have info
				formatTemplate.setStatus(Global.status_ok);
				formatDataImage dataImage = new formatDataImage();
				dataImage.setImage_url(pathImg);
				formatTemplate.setData(dataImage);

			} else {
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_update_data);
				error.setMessage(GlobalMessageScreen.user_change_avata_fail);
				formatTemplate.setError(error);
			}
		} else {
			// can't find user by phone
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalMessageScreen.user_not_found);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	@RequestMapping(value = "/register-user", method = POST)
	public String insertTracking(
			@RequestParam(required = false) String fullname,
			@RequestParam(required = false) String username,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String sex,
			@RequestParam(required = false) String total_km_begin) {
		// sex is status --> 1 la nam 2 la nư
		if (fullname == null || password == null || username == null
				|| email == null || phone == null || sex == null
				|| total_km_begin == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		Date today = Calendar.getInstance().getTime();
		org.bson.Document do_user = new org.bson.Document("password", password);
		do_user.append("fullname", fullname);
		do_user.append("username", username);
		do_user.append("email", email);
		do_user.append("phone", phone);
		do_user.append("status", "ACTIVE");
		do_user.append("branchname", "");
		do_user.append("bankname", "");
		do_user.append("bankcode", "");
		do_user.append("accountname", "");
		do_user.append("accountno", "");
		do_user.append("token_expiresIn", Integer.parseInt("1"));
		do_user.append("role_code", "2");
		do_user.append("role", "Client");
		do_user.append("created_date", GlobalUtils.convertStringToDate(today));
		do_user.append("created_time",
				GlobalUtils.convertStringToDateTime(today));
		do_user.append("google_id", "google_id");
		do_user.append("face_id", "face_id");
		if (sex.equals("1"))
			do_user.append("sex", "Nam");
		else
			do_user.append("sex", "Nữ");
		do_user.append("birthday", GlobalUtils.convertStringToDate(today));
		do_user.append("photo",
				"https://www.biber.com/dta/themes/biber/core/assets/images/no-featured-175.jpg");
		do_user.append("__v", Integer.parseInt("0"));
		do_user.append("total_km_begin", Double.parseDouble(total_km_begin));
		if (service.insertUser(do_user)) {
			// register he thong
			inMemoryUserDetailsManager.deleteUser(phone);
			inMemoryUserDetailsManager
					.createUser(new org.springframework.security.core.userdetails.User(
							phone, password, new ArrayList<GrantedAuthority>()));
			// end
			formatTemplate.setStatus(Global.status_ok);
			formatTemplate.setData("");
		} else {
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_insert_data);
			error.setMessage(GlobalMessageScreen.user_insert_unsuccessfull);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;

	}

}
