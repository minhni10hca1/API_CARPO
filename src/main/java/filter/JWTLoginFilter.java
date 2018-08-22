package filter;

import java.io.IOException;

import model.AccountCredentials;
import model.Global;
import model.GlobalErrorCode;
import model.GlobalMessageScreen;
import model.JsonFormatError;
import model.JsonFormatTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import security.WebSecurityConfig;
import service.TokenAuthenticationService;
import util.FunctionUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
	
	@Autowired
	WebSecurityConfig webSecurity;
	public static AuthenticationManager authManager22;
	public JWTLoginFilter(String url, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
		authManager22 = authManager;
//		try {
//			webSecurity.configure(Global.authen);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse arg1) throws AuthenticationException,
			IOException, ServletException {
		// TODO Auto-generated method stub
		
		//WebSecurityConfig.register();
		// new - register
//		try {
//			webSecurity.configure(Global.authen);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		FindIterable<org.bson.Document> listUser = FunctionUtil.getALlUser();
//		listUser.forEach(new Block<org.bson.Document>() {
//            @Override
//            public void apply(final org.bson.Document document) {
//            	try {
//            		String phone = document.getString("phone");
//            		String password = document.getString("password");
//            		if(!phone.isEmpty() && !password.isEmpty())
//            			Global.authen.inMemoryAuthentication().withUser(phone).password(password).roles("ADMIN");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }
//       });
		// new
		arg1.addHeader("Access-Control-Allow-Origin", "*");
		arg1.addHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
		arg1.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
		//end
		AccountCredentials credentials = new AccountCredentials(request.getParameter("username"), request.getParameter("password"));
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        credentials.getPassword(),
                        Collections.emptyList()
                )
        );
	}
	
	
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// TODO Auto-generated method stub
	    String app_type = request.getParameter("app_type");
	    // 0 la app driver 
	    // 1 la app mamager - customer
	    String password = request.getParameter("password");
		TokenAuthenticationService.addAuthentication(response, authResult.getName(), app_type,password);
	}
	 @Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
//		super.unsuccessfulAuthentication(request, response, failed);
		 try {
	        	JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
	        	formatTemplate.setStatus(Global.status_fail);
	        	JsonFormatError error = new JsonFormatError();
	        	error.setCode(GlobalErrorCode.error_code_user_login);
	        	error.setMessage(GlobalMessageScreen.user_login_fail);
	        	formatTemplate.setError(error);
	        	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    		String json = gson.toJson(formatTemplate);
	    		response.setCharacterEncoding( "UTF-8" );
	    		response.getWriter().write(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	


}
