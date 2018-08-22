package model;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import com.mongodb.MongoClient;

public class Global {
	public static String status_code_bad_request = "400";
	public static int status_ok = 1;
	public static int status_fail = 0;
	public static String mesage_ok = "successfull";
	/* info connect mongo */
	// -> fix before deloy
	//public static final String DB_NAME ="carpo_test";
	public static final String DB_NAME ="carpo";
	// end
//	public static final String DB_NAME ="carpo";
	//public static final String HOST = "45.119.81.181";
	// host local
	//public static final String HOST = "127.0.0.1";
	// for deloy server
	public static final String HOST = "127.0.0.1";
	public static final int PORT = 27017;
	public static final String USERNAME = "minhni";
	public static final String PASSWORD = "minhni123";
	
	// seever
	public static final String url_connect_db = "mongodb://minhni:minhni123@127.0.0.1:27017/carpo";
	// seever
	
	// server test
//	public static final String url_connect_db = "mongodb://minhni:minhni123@45.119.81.181:27017/carpo_test";
	// server test deloy
	//public static final String url_connect_db = "mongodb://minhni:minhni123@127.0.0.1:27017/carpo_test";
	// test localhost
	//public static final String url_connect_db = "mongodb://minhni:minhni123@127.0.0.1:27017/carpo";
	/* token */
	public static final String token = "";
	public static AuthenticationManagerBuilder authen;
	/* format datetime */
	public static final String format_date = "yyyy-MM-dd";
	public static final String format_time = "HH:mm:ss";
	public static final String format_time_save_img = "HHmmss";
	public static final String format_date_time = "yyyy-MM-dd HH:mm:ss";
	/* info table */
	public static final String collection_tracking = "tracking";
	public static final String collection_car = "car";
	public static final String collection_car_group = "car_group";
	public static final String collection_user = "user";
	public static final String collection_otp = "otp";
	public static final String collection_report = "report";
	public static final String collection_confirm_car_staus = "confirm_car_status";
	public static final String collection_campaign = "campaign";
	public static final String collection_customer = "customer";
	public static final String collection_district = "district";
	public static final String collection_area = "area";
	public static final String collection_home_customer = "home_customer";
	public static final String collection_campaign_part_driver = "campaign_part_driver";
	public static final String collection_home_calculator_km_before = "home_calculator_km_before";
	public static final String collection_district_calculator = "district_calculator";
	/* url save image avata */
	//public static final String url_image_avata = "../API_CARPO/ImageAvata";
	public static final String url_image_avata = "ImageAvata";
	public static final String mapping_image_avata = "/ImageAvata"; 
	/* url save image confirm car */
	//public static final String url_image_confirm_car_status = "../API_CARPO/ImageConfirmCar";
	public static final String url_image_confirm_car_status = "ImageConfirmCar";
	public static final String mapping_image_confirm_car_status = "/ImageConfirmCar";
	/* url save image report */
	//public static final String url_image_report = "../API_CARPO/ImageReport";
	public static final String url_image_report = "ImageReport";
	public static final String mapping_image_report = "/ImageReport";
	
	//public static final String host_domain = "http://192.168.1.184:88";
	// real
	public static final String host_domain = "http://45.119.81.181:8080/API_CARPO";
	// test
	//public static final String host_domain = "http://45.119.81.181:8080/API_CARPO_TEST";
	public static final String format_img = ".jpg";
	// otp
	public static final String app_id = "374475319631367";
	public static final String app_secret = "96057e670424b1ee488545420a03ee5b";
	public static final String api_get_access_token = "https://graph.accountkit.com/v1.2/access_token?grant_type=authorization_code&code=<authorization_code>&access_token=AA|<facebook_app_id>|<app_secret>";
	public static final String api_get_info_from_access_token = "https://graph.accountkit.com/v1.0/me/?access_token=<access_token>&appsecret_proof=<appsecret_proof>";
	// static connection mongoDB
	public static MongoClient mongoClient = null;
	public static String dateToDay;
	public static String timeToDay;
	// save token gps box
	public static String token_gps_box = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwOTgyMjIyMjIiLCJleHAiOjE1MTQ3MTQyNDR9.EHYx79kTlQyvLhZPfWGumGRAWC1z9LkE086n0zgGfxaQXUghdgC3SnPyqneurgyRyvk7_Nxn8KnyY_T1AavYVQ";
	public static String token_expire = "2020-12-31";
	// log name
	public static String log_write_img = "save_img.txt";
	public static String log_write_carpo = "log_carpo.txt";
	

}
