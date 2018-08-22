package controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.POST;

import model.Global;
import model.GlobalErrorCode;
import model.GlobalMessageScreen;
import model.JsonFormatError;
import model.JsonFormatTemplate;
import model.LocationGoogle;
import model.Otp;
import model.Tracking;
import model.User;
import model_json.formatDataCheckOtpAuthToken;
import model_json.formatJsonTrackingInfo;

import org.apache.commons.codec.binary.Hex;
import org.apache.hadoop.hdfs.server.namenode.status_jsp;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import org.apache.hadoop.util.hash.Hash;
import org.hibernate.annotations.NotFound;
import org.json.JSONException;
import org.json.JSONObject;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mysql.fabric.Response;

import service.OtpService;
import service.TrackingService;
import service.UserService;
import util.FunctionUtil;
import util.GlobalUtils;

@RestController
public class OtpController {

	@Autowired
	OtpService service;

	@Autowired
	UserService serviceUser;
	public static String result_sms = "";
	public static Boolean status_result = false;

	@RequestMapping(value = "/get-otp-by-phone", method = GET)
	public String getOtpByPhone(@RequestParam(required = false) String phone) {
		if (phone == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		User user = serviceUser.getInfoUserByPhone(phone);
		if (user.get_id() != null) {
			formatTemplate.setStatus(Global.status_ok);
			// geny 4 number
			int randomOtpNumber = (int) (Math.random() * 9000) + 1000;
			Otp otp = new Otp();
			otp.setUser_id(user.get_id());
			otp.setOtp_number(String.valueOf(randomOtpNumber));
			Date today = Calendar.getInstance().getTime();
			otp.setCreated_date(GlobalUtils.convertStringToDate(today));
			// expire date
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(today);
			calendar.add(Calendar.DATE, 14);
			Date expire = calendar.getTime();
			otp.setExpire_date(GlobalUtils.convertStringToDate(expire));
			if (service.insertOtp(otp))
				formatTemplate.setData(randomOtpNumber);
			else {
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_insert_data);
				error.setMessage(GlobalMessageScreen.otp_save_fail);
				formatTemplate.setError(error);
			}

		} else {
			// can't find user by phone
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_find_data);
			error.setMessage(GlobalMessageScreen.phone_not_register);
			formatTemplate.setError(error);
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	public static String encode(String key, String data) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"),
				"HmacSHA256");
		sha256_HMAC.init(secret_key);
		return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
	}


	@RequestMapping(value = "/check-otp-by-authorization-code", method = GET)
	public String checkOtpByAuthorization_code(
			@RequestParam(required = false) String authorizationCode) {
		if (authorizationCode == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		String api_get_access_token = Global.api_get_access_token
				.replace("<authorization_code>", authorizationCode)
				.replace("<facebook_app_id>", Global.app_id)
				.replace("<app_secret>", Global.app_secret);
		try {
			
			JsonObject jsonObject = FunctionUtil
					.readJsonFromUrl(api_get_access_token);
			if (jsonObject != null) {
				String access_token = jsonObject.get("access_token").getAsString();
				String appsecret_proof = encode(Global.app_secret,access_token);
				String api_get_info = Global.api_get_info_from_access_token
						.replace("<access_token>", access_token).replace(
								"<appsecret_proof>", appsecret_proof);
				JsonObject jsonObjectInfo = FunctionUtil
						.readJsonFromUrl(api_get_info);
				JsonObject jsonObjPhone = jsonObjectInfo.getAsJsonObject("phone");
				String phone = jsonObjPhone.get("number").getAsString().replace("+84", "0");
				// check in db ...........
				Boolean result = FunctionUtil.checkExitsPhone(phone);
				if(result){
					formatTemplate.setStatus(Global.status_ok);
					formatDataCheckOtpAuthToken authToken = new formatDataCheckOtpAuthToken();
					authToken.setPhone(phone);
					formatTemplate.setData(authToken);
				}else{
					formatTemplate.setStatus(Global.status_fail);
					error.setCode(GlobalErrorCode.error_code_find_data);
					error.setMessage(GlobalMessageScreen.oto_not_exits_phone);
					formatTemplate.setError(error);
				}
				
			}else{
				formatTemplate.setStatus(Global.status_fail);
				error.setCode(GlobalErrorCode.error_code_find_data);
				error.setMessage(GlobalMessageScreen.oto_authorization_code_not_match);
				formatTemplate.setError(error);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);

		return json;
	}

}
