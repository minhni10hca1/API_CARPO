package service;

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
import model.Otp;
import model.Tracking;

@Service
public class OtpServiceImpl implements OtpService {

	
	public org.bson.Document getDocumentOtpByUserId(String user_id){
		org.bson.Document result = null;
		try{
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_otp = database.getCollection(Global.collection_otp);
			org.bson.Document query = new org.bson.Document();
			query.append("user_id", user_id);
			result = do_otp.find(query).first();
		}catch(Exception e){
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db OtpService:" + e.getMessage(), Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public Boolean insertOtp(Otp otp) {
		// TODO Auto-generated method stub
		Boolean result;
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_otp = database.getCollection(Global.collection_otp);
			org.bson.Document resultDocument = getDocumentOtpByUserId(otp.getUser_id());
			if(resultDocument != null ){
				resultDocument.append("otp_number", otp.getOtp_number());
				resultDocument.append("expire_date", otp.getExpire_date());
				resultDocument.append("created_date", otp.getCreated_date());
				org.bson.Document query = new org.bson.Document();
				query.append("user_id", otp.getUser_id());
				do_otp.replaceOne(query, resultDocument);
			}else{
				org.bson.Document otp_document = new org.bson.Document("user_id",otp.getUser_id()  );
				otp_document.append("otp_number", otp.getOtp_number());
				otp_document.append("expire_date", otp.getExpire_date());
				otp_document.append("created_date",otp.getCreated_date());
				do_otp.insertOne(otp_document);
			}
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			TrackingController.result_sms = e.getMessage();
			result = false;
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db OtpService:" + e.getMessage(), Global.log_write_carpo);
		}
		return result;
	}
	
	public Boolean checkExpireOtp(String user_id, String otp){
		Boolean result = false;
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("user_id", user_id);
			query.append("otp_number", otp);
			Date today = Calendar.getInstance().getTime();
			BasicDBObject obj = new BasicDBObject();
			obj.append("$lt", GlobalUtils.convertStringToDate(today));
			query.append("expire_date",obj);
			MongoCollection<org.bson.Document> do_otp = database
					.getCollection(Global.collection_otp);
			org.bson.Document resultDocument = do_otp.find(query).first();
			if(resultDocument != null)
				result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result = true;
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db OtpService:" + e.getMessage(), Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public String checkOtp(String user_id, String otp) {
		// TODO Auto-generated method stub
		String str_result = "";
		// 0 -> don't have otp with user_id
		// 1 -> error otp
		// 2 -> otp het hang
		// 3 -> loi he thong
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("user_id", user_id);
			MongoCollection<org.bson.Document> do_otp = database
					.getCollection(Global.collection_otp);
			org.bson.Document result = do_otp.find(query).first();
			if (result != null) {
				String otp_number = result.get("otp_number").toString();
				if(!otp_number.equals(otp))
					str_result = "1";
				else if (checkExpireOtp(user_id, otp))
					str_result = "2";
			}else{
				str_result = "0";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			str_result = "3";
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db OtpService:" + e.getMessage(), Global.log_write_carpo);
		}
		return str_result;
	}

	

}
