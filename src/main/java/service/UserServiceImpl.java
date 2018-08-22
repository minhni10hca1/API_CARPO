package service;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.taskdefs.Sync.MyCopy;
import org.bson.BSON;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import controller.CarController;
import controller.TrackingController;
import scala.annotation.meta.setter;
import util.FunctionUtil;
import util.GlobalUtils;
import util.MongoUtils;
import model.Car;
import model.Global;
import model.Tracking;
import model.User;

@Service
public class UserServiceImpl implements UserService {

	@Override
	public User getInfoUserByPhone(String phone) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		User user = new User();
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("phone", phone);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_user);
			result = do_cars.find(query).first();
			if (result != null) {
				user.set_id(result.get("_id").toString());
				user.setPhone(result.get("phone").toString());
				user.setFullname(result.get("fullname").toString());
				user.setPhoto(result.get("photo").toString());
				user.setEmail(result.get("email").toString());
				TokenAuthenticationService.status_result = true;
			}else{
				TokenAuthenticationService.status_result = false;
				TokenAuthenticationService.result_sms = "can't find info user with phone: " + phone ;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			TokenAuthenticationService.status_result = false;
			TokenAuthenticationService.result_sms = e.getMessage();
			//write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db UserService:" + e.getMessage(), Global.log_write_carpo);
		}
		return user;
	}
//	@Override
//	public Boolean createdNewPassword(String phone, String newPassword, org.bson.Document oldData) {
//		// TODO Auto-generated method stub
//		try {
//			mongoClient = MongoUtils.getMongoClient_BM();
//			MongoDatabase database = mongoClient.getDatabase(Global.DB_NAME);
//			org.bson.Document query = new org.bson.Document();
//			query.append("phone", phone);
//			MongoCollection<org.bson.Document> do_users = database
//					.getCollection(Global.collection_user);
//			oldData.append("password", newPassword);
//			do_users.replaceOne(query, oldData);
//			mongoClient.close();
//			return true;
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			mongoClient.close();
//			return false;
//		}
//	}
	@Override
	public org.bson.Document getInfoUserByUserId(String user_id) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			ObjectId objectId = new ObjectId(user_id);
			query.append("_id", objectId);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_user);
			result = do_cars.find(query).first();
		} catch (Exception e) {
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db UserService:" + e.getMessage(), Global.log_write_carpo);
		}
		return result;
	}
	@Override
	public Boolean changePassword(String user_id, String newPassword,
			org.bson.Document oldData) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("_id", new ObjectId(user_id));
			MongoCollection<org.bson.Document> do_users = database
					.getCollection(Global.collection_user);
			oldData.append("password", newPassword);
			do_users.replaceOne(query, oldData);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db UserService:" + e.getMessage(), Global.log_write_carpo);
			return false;
		}
	}
	@Override
	public Boolean changeAvata(String user_id, String pathPhoto,
			org.bson.Document oldData) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("_id", new ObjectId(user_id));
			MongoCollection<org.bson.Document> do_users = database
					.getCollection(Global.collection_user);
			oldData.append("photo",pathPhoto );
			do_users.replaceOne(query, oldData);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db UserService:" + e.getMessage(), Global.log_write_carpo);
			return false;
		}
	}
	@Override
	public Boolean insertUser(org.bson.Document user) {
		// TODO Auto-generated method stub
		Boolean result;
		try {
			System.out.print("value connection:" + Global.mongoClient);
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_user);
			do_trackings.insertOne(user);
			result = true;
		} catch (Exception ex) {
			result = false;
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db userService:" + ex.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

}
