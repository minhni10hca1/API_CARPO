package controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.POST;

import model.Global;
import model.GlobalErrorCode;
import model.GlobalMessageScreen;
import model.JsonFormatError;
import model.JsonFormatTemplate;
import model.Tracking;
import model.User;

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
import com.mysql.fabric.Response;






















import service.TokenAuthenticationService;
import service.TrackingService;
import util.FunctionUtil;
import util.GlobalUtils;

@RestController
public class ProcessTokenController {

	@Autowired
	TrackingService service;
	public static String result_sms = "";
	public static Boolean status_result = false;
	public static String phone = "";

	// check token is ok or not
	// params  -> Authorization -> token
	// 1 token khong hop le
	// 2 token het hang
	@RequestMapping(value = "/checkToken", method = POST)
	public String checkToken(@RequestParam(required=false) String Authorization ){
		if(Authorization == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document documentUser = FunctionUtil.getInfoDocumentUserByToken(Authorization);
		if(documentUser == null){
			error.setCode(GlobalErrorCode.error_code_expire_not_match);
			error.setMessage(GlobalMessageScreen.error_message_token_not_match);
			formatTemplate.setStatus(Global.status_fail);
			formatTemplate.setData(error);
		}
		else if (checkExpireToken(documentUser.get("token_expire").toString())){
			 error.setCode(GlobalErrorCode.error_code_expire);
			 error.setMessage(GlobalMessageScreen.error_message_token_expire);
			 formatTemplate.setStatus(Global.status_fail);
			 formatTemplate.setData(error);
		}
		else{
			return FunctionUtil.getJsonInfoUserOnlyByUser_id(documentUser.get("_id").toString());
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}
	
	@RequestMapping(value = "/refresh-Token", method = POST)
	public String refreshToken(@RequestHeader(required=false) String AuthorizationRefresh){
		if(AuthorizationRefresh == null)
			return FunctionUtil.getJsonErrorForRequestParamsHeader();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		org.bson.Document documentUser = FunctionUtil.getInfoDocumentUserByToken(AuthorizationRefresh);
		if(documentUser == null){
			error.setCode(GlobalErrorCode.error_code_expire_not_match);
			error.setMessage(GlobalMessageScreen.error_message_token_not_match);
			formatTemplate.setStatus(Global.status_fail);
			formatTemplate.setData(error);
		}
		else {
			// random token
			String JWT = Jwts
					.builder()
					.setSubject(documentUser.get("phone").toString())
					.setExpiration(
							new Date(System.currentTimeMillis() + TokenAuthenticationService.EXPIRATIONTIME))
					.signWith(SignatureAlgorithm.HS512, TokenAuthenticationService.SECRET).compact();
			
			 FunctionUtil.updateTokenUserByPhone(documentUser.get("phone").toString(), JWT, documentUser);
			 return FunctionUtil.getJsonInfoUserOnlyByUser_id(documentUser.get("_id").toString());
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}
	
	
	
	
	public static Boolean checkExpireToken(String strDateExpireToken){
		Boolean result = false;
		DateFormat df = new SimpleDateFormat(Global.format_date);
		Date today = Calendar.getInstance().getTime();
		try{
			Date expireToken = df.parse(strDateExpireToken);
			int compare = expireToken.compareTo(today);
			if(compare <= 0)
				result = true;
		}catch(Exception ex){
			result = false;
		}
		
		return result;
	}
	

}
