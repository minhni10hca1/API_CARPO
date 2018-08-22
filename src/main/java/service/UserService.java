package service;

import java.util.List;

import org.springframework.stereotype.Service;

import model.Car;
import model.Tracking;
import model.User;

@Service
public interface UserService {
	User getInfoUserByPhone(String phone);
	org.bson.Document getInfoUserByUserId(String user_id);
	Boolean changePassword(String user_id, String newPassword, org.bson.Document oldData);
	Boolean changeAvata(String user_id, String pathImg, org.bson.Document oldData);
	Boolean insertUser(org.bson.Document user);

}
