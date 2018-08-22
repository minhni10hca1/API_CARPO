package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import model.Car;
import model.Global;
import model.GlobalErrorCode;
import model.JsonFormatError;
import model.JsonFormatTemplate;
import model.LocationGoogle;
import model.User;
import model_json.formatDataInfoUser;
import service.TokenAuthenticationService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import controller.CarController;

public class FunctionUtil {

	public static User getInfoUserByPhone(String phone) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		User user = new User();
		try {

			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("phone", phone);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_user);
			result = do_cars.find(query).first();
			if (result != null) {
				user.set_id(result.get("_id").toString());
				user.setRole_code(result.get("role_code").toString());
				user.setPhone(result.get("phone").toString());
				user.setPassword(result.get("password").toString());
				user.setFullname(result.get("fullname").toString());
				user.setPhoto(result.get("photo").toString());
				user.setEmail(result.get("email").toString());
				TokenAuthenticationService.status_result = true;
				TokenAuthenticationService.documen_user = result;
			} else {
				TokenAuthenticationService.status_result = false;
				TokenAuthenticationService.result_sms = "can't find info user with phone: "
						+ phone;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			TokenAuthenticationService.status_result = false;
			TokenAuthenticationService.result_sms = e.getMessage();
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
			// wrirte log
		}
		return user;
	}
	
	public static User getInfoUserByUsername(String userName) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		User user = new User();
		try {

			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("username", userName);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_user);
			result = do_cars.find(query).first();
			if (result != null) {
				user.set_id(result.get("_id").toString());
				user.setRole_code(result.get("role_code").toString());
				user.setPhone(result.get("phone").toString());
				user.setPassword(result.get("password").toString());
				user.setFullname(result.get("fullname").toString());
				user.setPhoto(result.get("photo").toString());
				user.setEmail(result.get("email").toString());
				TokenAuthenticationService.status_result = true;
				TokenAuthenticationService.documen_user = result;
			} else {
				TokenAuthenticationService.status_result = false;
				TokenAuthenticationService.result_sms = "can't find info user with userName: "
						+ userName;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			TokenAuthenticationService.status_result = false;
			TokenAuthenticationService.result_sms = e.getMessage();
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
			// wrirte log
		}
		return user;
	}

	public static void writeLogCrawler(String value, String nameFile) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(nameFile, true)));
			out.println(value);
			out.close();

		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static Boolean checkExitsPhone(String phone) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		Boolean bResult = false;
		;
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("phone", phone);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_user);
			result = do_cars.find(query).first();
			if (result != null) {
				bResult = true;
			}
		} catch (Exception e) {
			bResult = false;
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return bResult;
	}

	public static org.bson.Document getInfoDocumentUserByPhone(String phone) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("phone", phone);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_user);
			result = do_cars.find(query).first();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	public static org.bson.Document getInfoDocumentUserByToken(String token) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			// mongoClient = MongoUtils.getMongoClient_BM();
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("token", token);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_user);
			result = do_cars.find(query).first();
			// mongoClient.close();
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	public static Car getInfoCarByUserId(String user_id) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		Car car = new Car();
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("user_id", new ObjectId(user_id));
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_car);
			result = do_cars.find(query).first();
			if (result != null) {
				car.set_id(result.get("_id").toString());
				car.setType(result.get("type").toString());
				car.setDevice_id(result.get("device_id").toString());
				car.setUser_id(result.get("user_id").toString());
				car.setCampaign_id(result.get("campaign_id").toString());
				car.setCar_color(result.get("car_color").toString());
				car.setLicense_plate(result.get("license_plate").toString());
				car.setCar_manufacturer(result.get("car_manufacturer")
						.toString());
				// car.setStart_time(result.get("start_time").toString());
				// car.setEnd_time(result.get("end_time").toString());
				// car.setCreated_time(result.get("created_time").toString());
				// car.setDriver_id(result.get("driver_id").toString());
				car.setGroup_id(result.get("group_id").toString());
				CarController.status_result = true;
			} else {
				CarController.status_result = false;
				CarController.result_sms = "can't find info car with user_id: "
						+ user_id;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			CarController.status_result = false;
			CarController.result_sms = e.getMessage();
			// writelog
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return car;
	}

	public static org.bson.Document getInfoDocumentUserByUser_id(String user_id) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			if (user_id.isEmpty())
				return result;
			query.append("_id", new ObjectId(user_id));
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_user);
			result = do_cars.find(query).first();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log;
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	public static void updateTokenUserByPhone(String phone, String token,
			org.bson.Document oldData) {
		// TODO Auto-generated method stub
		Date today = Calendar.getInstance().getTime();
		String strToday = GlobalUtils.convertStringToDate(today);
		Calendar cal = Calendar.getInstance();
		//cal.add(Calendar.DATE, 8);
		cal.add(Calendar.DATE, TokenAuthenticationService.dayExpToken);
		Date dateExpireToken = cal.getTime();
		String strDateExp = GlobalUtils.convertStringToDate(dateExpireToken);
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("phone", phone);
			MongoCollection<org.bson.Document> do_users = database
					.getCollection(Global.collection_user);
			oldData.append("token", token);
			oldData.append("token_expire", strDateExp);
			oldData.append("updated_date", strToday);
			do_users.replaceOne(query, oldData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
		}

	}
	
	public static void updateTokenUserByUserName(String userName, String token,
			org.bson.Document oldData) {
		// TODO Auto-generated method stub
		Date today = Calendar.getInstance().getTime();
		String strToday = GlobalUtils.convertStringToDate(today);
		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DATE, 8);
		cal.add(Calendar.DATE, TokenAuthenticationService.dayExpToken);
		Date dateExpireToken = cal.getTime();
		String strDateExp = GlobalUtils.convertStringToDate(dateExpireToken);
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("username", userName);
			MongoCollection<org.bson.Document> do_users = database
					.getCollection(Global.collection_user);
			oldData.append("token", token);
			oldData.append("token_expire", strDateExp);
			oldData.append("updated_date", strToday);
			do_users.replaceOne(query, oldData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
		}

	}

	public static FindIterable<org.bson.Document> getALlUser() {
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> collectiona = database
					.getCollection(Global.collection_user);
			FindIterable<org.bson.Document> listUser = collectiona.find();
			return listUser;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
			return null;
			// e.printStackTrace();
		}
	}

	public static org.bson.Document getInfoDocumentCarGroupByLeader_id(
			String user_id) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			if (user_id.isEmpty())
				return result;
			query.append("leader_id", new ObjectId(user_id));
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_car_group);
			result = do_cars.find(query).first();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	public static double distanceBetween2Points(double la1, double lo1,
			double la2, double lo2) {
		double R = 6371.01;
		double dLat = (la2 - la1) * (Math.PI / 180);
		double dLon = (lo2 - lo1) * (Math.PI / 180);
		double la1ToRad = la1 * (Math.PI / 180);
		double la2ToRad = la2 * (Math.PI / 180);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(la1ToRad)
				* Math.cos(la2ToRad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		// round
		return (double) Math.round(d * 10) / 10;
	}

	public static double roundDouble(double number) {
		return (double) Math.round(number * 10) / 10;
	}

	public static double totalDistanceByListLocation(
			ArrayList<LocationGoogle> listLocation) {
		double result = 0;
		for (int index = 0; index < listLocation.size() - 1; index++) {
			result += distanceBetween2Points(Double.parseDouble(listLocation
					.get(index).getLocation_lat()),
					Double.parseDouble(listLocation.get(index)
							.getLocation_long()),
					Double.parseDouble(listLocation.get(index + 1)
							.getLocation_lat()),
					Double.parseDouble(listLocation.get(index + 1)
							.getLocation_long()));
		}
		return roundDouble(result);
	}

	// get model user
	public static String getJsonInfoUserOnlyByUser_id(String user_id) {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		try {
			formatDataInfoUser dataInfoUser = new formatDataInfoUser();
			Car car = FunctionUtil.getInfoCarByUserId(user_id);
			org.bson.Document documentUser = FunctionUtil
					.getInfoDocumentUserByUser_id(user_id);
			if (documentUser != null && !user_id.isEmpty()) {
				dataInfoUser.set_id(documentUser.get("_id").toString());
				// dataInfoUser.setRole_id(documentUser.get("role_id").toString());
				dataInfoUser.setPhone(documentUser.get("phone").toString());
				dataInfoUser.setFullname(documentUser.get("fullname")
						.toString());
				dataInfoUser.setEmail(documentUser.get("email").toString());
				dataInfoUser.setToken(documentUser.get("token").toString());
				dataInfoUser.setToken_expire(documentUser.get("token_expire")
						.toString());
				dataInfoUser.setPhoto(documentUser.get("photo").toString());
				dataInfoUser.setFace_id(documentUser.get("face_id").toString());
				dataInfoUser.setGoogle_id(documentUser.get("google_id")
						.toString());
				dataInfoUser.setCreated_time(documentUser.get("created_time")
						.toString());
				// new
				dataInfoUser.setBirthday(documentUser.get("birthday")
						.toString());
				dataInfoUser.setSex(documentUser.get("sex").toString());
				// set total distance
				// new
				dataInfoUser.setRole_code(documentUser.get("role_code")
						.toString());
				org.bson.Document documenCarGroup = getInfoDocumentCarGroupByLeader_id(user_id);
				if (documenCarGroup != null)
					dataInfoUser.setStatus_leader("1");
				else
					dataInfoUser.setStatus_leader("0");
				dataInfoUser.setTotal_distance_run_one_month("");
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
				error.setMessage(GlobalErrorCode.error_message_find_data);
				formatTemplate.setError(error);
			}
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db FunctionUtil:" + e.getMessage(),
					Global.log_write_carpo);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	public static String getJsonErrorForRequestParams() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		formatDataInfoUser dataInfoUser = new formatDataInfoUser();
		formatTemplate.setStatus(Global.status_fail);
		error.setCode(GlobalErrorCode.error_code_input_params);
		error.setMessage(GlobalErrorCode.error_message_input_params);
		formatTemplate.setError(error);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	public static String getJsonErrorForRequestParamsHeader() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		formatDataInfoUser dataInfoUser = new formatDataInfoUser();
		formatTemplate.setStatus(Global.status_fail);
		error.setCode(GlobalErrorCode.error_code_input_params_header);
		error.setMessage(GlobalErrorCode.error_message_input_params_header);
		formatTemplate.setError(error);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	public static String getJsonErrorForAccessUser() {
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		formatDataInfoUser dataInfoUser = new formatDataInfoUser();
		formatTemplate.setStatus(Global.status_fail);
		error.setCode(GlobalErrorCode.error_code_not_accept_app);
		error.setMessage(GlobalErrorCode.error_message_not_accept_app);
		formatTemplate.setError(error);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	public static void createdForderReportIfNotExits(String url) {
		File file = new File(Global.url_image_report);
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JsonObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		// url = "https://www.binance.com/api/v1/ticker/allPrices";
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);

			JsonElement jelement = new com.google.gson.JsonParser()
					.parse(jsonText);
			JsonObject jobject = jelement.getAsJsonObject();
			// JsonArray jobject = jelement.getAsJsonArray();
			// JSONObject json = new JSONObject(jsonText);
			return jobject;
		} finally {
			is.close();
		}
	}

}
