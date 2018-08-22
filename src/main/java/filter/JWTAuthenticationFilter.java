package filter;

import java.io.IOException;

import model.Global;
import model.GlobalErrorCode;
import model.GlobalMessageScreen;
import model.JsonFormatError;
import model.JsonFormatTemplate;

import org.apache.catalina.mbeans.GlobalResourcesLifecycleListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import service.TokenAuthenticationService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

public class JWTAuthenticationFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
//		Authentication authentication = TokenAuthenticationService.getAuthentication((HttpServletRequest) servletRequest);
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//		filterChain.doFilter(servletRequest, servletResponse);
		
		Authentication authentication = TokenAuthenticationService.getAuthentication((HttpServletRequest) servletRequest);
		if(!TokenAuthenticationService.strStatusToken.isEmpty()){
			 JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
			 JsonFormatError error = new JsonFormatError();
			 if(TokenAuthenticationService.strStatusToken.equals("2")){
				 error.setCode(GlobalErrorCode.error_code_expire);
				 error.setMessage(GlobalMessageScreen.error_message_token_expire);
			 }else 
			 {
				 error.setCode(GlobalErrorCode.error_code_expire_not_match);
				 error.setMessage(GlobalMessageScreen.error_message_token_not_match); 
			 }
			 formatTemplate.setStatus(Global.status_fail);
			 formatTemplate.setError(error);
			 Gson gson = new GsonBuilder().setPrettyPrinting().create();
			 String json = gson.toJson(formatTemplate);
			 servletResponse.setCharacterEncoding( "UTF-8" );
			 servletResponse.getWriter().write(json);
		}else{
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(servletRequest, servletResponse);
		}
		
		// importance -> se lam nhung cai khong can chung thuc bi vo hien qua
		
		// if (authentication != null) {
		// SecurityContextHolder.getContext()
		// .setAuthentication(authentication);
		// filterChain.doFilter(servletRequest, servletResponse);
		// }else{
		// JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		// JsonFormatError error = new JsonFormatError();
		// error.setCode(Global.status_code_bad_request);
		// error.setMessage("Authorization is empty");
		// formatTemplate.setStatus(Global.status_fail);
		// formatTemplate.setError(error);
		// Gson gson = new GsonBuilder().setPrettyPrinting().create();
		// String json = gson.toJson(formatTemplate);
		// servletResponse.getWriter().write(json);
		// }
	}

}
