package service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.taskdefs.Sync.MyCopy;
import org.bson.types.ObjectId;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.hql.ast.origin.hql.parse.HQLParser.new_key_return;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.DBCollection;
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
import model.ConfirmCarStatus;
import model.Global;
import model.LocationGoogle;
import model.Tracking;

@Service
public class ConfirmCarStatusServiceImpl implements ConfirmCarStatusService {

	
	@Override
	public Boolean insertConfirmCarStatus(ConfirmCarStatus confirmCarStatus) {
		// TODO Auto-generated method stub
		Boolean result;
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_confirmCars = database.getCollection(Global.collection_confirm_car_staus);
			org.bson.Document confirmCar = new org.bson.Document("user_id", new ObjectId(confirmCarStatus.getUser_id()));
			confirmCar.append("image", confirmCarStatus.getImage());
			confirmCar.append("created_date", confirmCarStatus.getCreated_date());
			confirmCar.append("created_time", confirmCarStatus.getCreated_time());
			confirmCar.append("type","1");
			do_confirmCars.insertOne(confirmCar);
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			TrackingController.result_sms = e.getMessage();
			result = false;
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db ConfirmCarStatusService:" + e.getMessage(), Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public Boolean deleteConfirmCarStatusByImg(String img) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_confirm_car_staus);
			BasicDBObject documentRemove = new BasicDBObject();
			documentRemove.append("image", img);
			do_trackings.deleteMany(documentRemove);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return true;
	}

}
