package security;

import java.util.Properties;

import model.Global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import util.FunctionUtil;
import util.MongoUtils;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import filter.JWTAuthenticationFilter;
import filter.JWTLoginFilter;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		 http.csrf().disable().authorizeRequests()
         .antMatchers("/").permitAll() // Có nghĩa là request "/" ko cần phải đc xác thực
         .antMatchers("/get-otp-by-phone").permitAll() // get otp
         .antMatchers("/created-new-password-user").permitAll() // get otp
         .antMatchers("/created-new-password-user-not-otp").permitAll() // get otp
         .antMatchers("/checkToken").permitAll() // get otp 
          .antMatchers("/save-home-customer-db").permitAll() // get otp 
          .antMatchers("/save-home-customer-db-new").permitAll()
           .antMatchers("/delete-url-confirm-car").permitAll()
         //.antMatchers("/insert-tracking").permitAll() // get otp 
         .antMatchers("/insert-tracking-gps-box").permitAll() // get otp
         .antMatchers("/insert-tracking-gps-box-many").permitAll() // get otp
         .antMatchers("/register-user-from-web").permitAll() // get otp
         .antMatchers("/refresh-Token").permitAll() // get otp
         .antMatchers("/check-otp-by-authorization-code").permitAll() // get otp
         .antMatchers("/save-home-calculator-before").permitAll() // get otp save-home-calculator-before
         .antMatchers("/update-end-point-car-in-home-calculator-km-before").permitAll()
         .antMatchers("/get-export-acount-tracking").permitAll()
         .antMatchers(Global.mapping_image_report + "/{imgName}").permitAll() // get otp
         .antMatchers(Global.mapping_image_avata + "/{imgName}").permitAll() // get otp
         .antMatchers(Global.mapping_image_confirm_car_status + "/{imgName}").permitAll() // get otp
         .antMatchers(HttpMethod.POST, "/login").permitAll() // Request dạng POST tới "/login" luôn được phép truy cập dù là đã authenticated hay chưa
         .anyRequest().authenticated() // Các request còn lại đều cần được authenticated
         .and()
         // Add các filter vào ứng dụng của chúng ta, thứ mà sẽ hứng các request để xử lý trước khi tới các xử lý trong controllers.
         // Về thứ tự của các filter, các bạn tham khảo thêm tại http://docs.spring.io/spring-security/site/docs/3.0.x/reference/security-filter-chain.html mục 7.3 Filter Ordering
         .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class) 
         .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
//	private static AuthenticationManagerBuilder authenticationManagerBuilder;;
//	
//	public static void register(){
//		FindIterable<org.bson.Document> listUser = FunctionUtil.getALlUser();
//		listUser.forEach(new Block<org.bson.Document>() {
//            @Override
//            public void apply(final org.bson.Document document) {
//            	try {
//            		String phone = document.getString("phone");
//            		String password = document.getString("password");
//            		if(!phone.isEmpty() && !password.isEmpty())
//            			authenticationManagerBuilder.inMemoryAuthentication().withUser(phone).password(password).roles("ADMIN");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }
//       });
//	}
	
	@Autowired
	public static AuthenticationManagerBuilder authenticationManagerBuilder;
	
	public static void registerNewUser(String phone, String password){
		try {
			//AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor)
//    		if(!phone.isEmpty() && !password.isEmpty())
//    			authenticationManagerBuilder.inMemoryAuthentication().withUser(phone).password(password).roles("ADMIN");
//			ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
//		        public <T> T postProcess(T object) {
//		            return object;
//		        }
//		    };
//		    new AuthenticationManagerBuilder(objectPostProcessor)
//           .inMemoryAuthentication().withUser(phone).password(password).roles("ADMIN").and().and().build();
		   // JWTLoginFilter.authManager22.
		    
		    
		    
		   
		    authenticationManagerBuilder.inMemoryAuthentication().withUser(phone).password(password).roles("ADMIN").and().and().build();
		   
//		    AuthenticationManagerBuilder builder =  new AuthenticationManagerBuilder(objectPostProcessor);
//		    
//		    builder.inMemoryAuthentication().withUser(phone).password(password).roles("ADMIN");
//		    builder.notify();
		    
		    //JWTLoginFilter.authManager22.notify();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        final Properties users = new Properties();
        users.put("user","pass,ROLE_USER,enabled"); //add whatever other user you need
        return new InMemoryUserDetailsManager(users);
    }
	
	
	
	@Override
	public void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		authenticationManagerBuilder = auth;
		// connect db
		Global.mongoClient = MongoUtils.getMongoClient_BM();
		auth.userDetailsService(inMemoryUserDetailsManager());
//		register();
		// TODO Auto-generated method stub
		FindIterable<org.bson.Document> listUser = FunctionUtil.getALlUser();
		listUser.forEach(new Block<org.bson.Document>() {
            @Override
            public void apply(final org.bson.Document document) {
            	try {
            		String phone = document.getString("phone");
            		String password = document.getString("password");
            		if(!phone.isEmpty() && !password.isEmpty())
            			auth.inMemoryAuthentication().withUser(phone).password(password).roles("ADMIN");
            		// add more if customer can logn with username
            		String role_code = document.get("role_code").toString();
            		if (role_code.equals("3") || role_code.equals("4")){
            			String userName = document.getString("username");
                		if(!userName.isEmpty() && !password.isEmpty())
                			auth.inMemoryAuthentication().withUser(userName).password(password).roles("ADMIN");
            		}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
       });
//		auth.inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN");
//		auth.inMemoryAuthentication().withUser("0981104533").password("123456").roles("ADMIN");
	}
}
