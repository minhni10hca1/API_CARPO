package com.example.api_carpo;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import model.Global;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import security.WebSecurityConfig;
import util.GlobalUtils;
import util.MongoUtils;

//@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan({"controller","service"})
@Import({WebSecurityConfig.class})
public class ApiCarpoApplication {

	public static void main(String[] args) {
		try {
//			System.out.print("da vaoooo");
//			Date today = Calendar.getInstance().getTime();
//			Global.mongoClient = MongoUtils.getMongoClient_BM();
//			Global.dateToDay = GlobalUtils.convertStringToDate(today);
//			Global.timeToDay = GlobalUtils.convertStringToTime(today);
			BasicPasswordEncryptor basicPasswordEncryptor = new BasicPasswordEncryptor();
			String encryptedPassword = basicPasswordEncryptor.encryptPassword("123456");
			System.out.println(encryptedPassword);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SpringApplication.run(ApiCarpoApplication.class, args);
	}
}
