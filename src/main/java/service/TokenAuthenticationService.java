package service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import model.Global;
import model.GlobalErrorCode;
import model.GlobalMessageScreen;
import model.JsonFormatError;
import model.JsonFormatTemplate;
import model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import util.FunctionUtil;
import util.MongoUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import controller.ProcessTokenController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Collections.emptyList;

public class TokenAuthenticationService {
//	public static final long EXPIRATIONTIME = 864_000_000; // 10 days
	public static final long EXPIRATIONTIME = 31_536_000_000L; // 365 days
	public static final int dayExpToken = 365;
	public static final String SECRET = "ThisIsASecret";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";

	public static String result_sms = "";
	public static Boolean status_result = false;
	public static org.bson.Document documen_user;

	@Autowired
	UserService userService;
	
	public void getdataaa(){
		
	}

	public static void addAuthentication(HttpServletResponse res,
			String username, String app_type, String password) {
		
		String JWT = Jwts
				.builder()
				.setSubject(username)
				.setExpiration(
						new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		// res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
		try {
			User user = FunctionUtil.getInfoUserByPhone(username);
			if (user.getPassword() == null)
				user = FunctionUtil.getInfoUserByUsername(username);
			String user_id = user.get_id();
			String role_code = user.getRole_code();
			String passwordDB = user.getPassword();
			if(!password.equals(passwordDB)){
				JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
	        	formatTemplate.setStatus(Global.status_fail);
	        	JsonFormatError error = new JsonFormatError();
	        	error.setCode(GlobalErrorCode.error_code_user_login);
	        	error.setMessage(GlobalMessageScreen.user_login_fail);
	        	formatTemplate.setError(error);
	        	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    		String json = gson.toJson(formatTemplate);
	    		res.setCharacterEncoding( "UTF-8" );
	    		res.getWriter().write(json);
				return;
			}
			if(!app_type.equals("0") && !app_type.equals("1"))
			{
				String json = FunctionUtil.getJsonErrorForRequestParams();
				res.setCharacterEncoding( "UTF-8" );
				res.getWriter().write(json);
				return;
			}
			if((app_type.equals("0") && role_code.equals("2")) || (app_type.equals("1") && role_code.equals("1")) || (app_type.equals("1") && role_code.equals("3")) || (app_type.equals("1") && role_code.equals("4")) ){
				FunctionUtil.updateTokenUserByPhone(username, JWT, documen_user);
				// new for using login username or pass
				if(app_type.equals("1"))
					FunctionUtil.updateTokenUserByUserName(username, JWT, documen_user);
				// return data
				String json = FunctionUtil.getJsonInfoUserOnlyByUser_id(user_id);
				res.setCharacterEncoding( "UTF-8" );
				res.getWriter().write(json);
			}else{
				String json = FunctionUtil.getJsonErrorForAccessUser();
				res.setCharacterEncoding( "UTF-8" );
				res.getWriter().write(json);
			}
			// update token
			//org.bson.Document documen_user = FunctionUtil.getInfoDocumentUserByUser_id(user_id);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String strStatusToken = "";
	// 1 token khong hop le
	// 2 token het hang
	
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
	public static Authentication getAuthentication(HttpServletRequest request) {
		strStatusToken = "";
		String token = request.getHeader(HEADER_STRING);
		if (token != null) {
			// parse the token.
			// check token in db
			org.bson.Document documentUser = FunctionUtil.getInfoDocumentUserByToken(token);
			if(documentUser == null)
				strStatusToken = "1";
			else if (checkExpireToken(documentUser.get("token_expire").toString()))
				strStatusToken = "2";
			else{
				// token ok
				String user = Jwts.parser().setSigningKey(SECRET)
						.parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
						.getSubject();
				ProcessTokenController.phone = user;
				return user != null ? new UsernamePasswordAuthenticationToken(user,
						null, emptyList()) : null;
			}
//			String user = Jwts.parser().setSigningKey(SECRET)
//			.parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
//			.getSubject();
////			ProcessTokenController.phone = user;
//			return user != null ? new UsernamePasswordAuthenticationToken(user,
//					null, emptyList()) : null;
		}
		return null;
	}
}
