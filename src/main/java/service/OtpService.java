package service;

import java.util.List;

import org.springframework.stereotype.Service;

import model.Car;
import model.Otp;
import model.Tracking;

@Service
public interface OtpService {
	Boolean insertOtp(Otp otp);
	String checkOtp(String user_id, String otp);


}
