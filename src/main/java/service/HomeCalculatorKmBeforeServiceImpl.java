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
import org.bson.types.ObjectId;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
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
import model.Campaign_CM;
import model.Car;
import model.Global;
import model.LocationGoogle;
import model.Tracking;

@Service
public class HomeCalculatorKmBeforeServiceImpl implements HomeCalculatorKmBeforeService {

	@Override
	public org.bson.Document getInfoHomeCalculatorKmBeforeCampaignAndDeviceId(
			String campaign_id, String device_id) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("campaign_id", campaign_id);
			query.append("device_id", device_id);
			MongoCollection<org.bson.Document> do_area = database
					.getCollection(Global.collection_home_calculator_km_before);
			result = do_area.find(query).first();
		} catch (Exception ex) {
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db HomecalculatorKmBefore:" + ex.getMessage(), Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public void deleteHomeCalculatorKmBefore(String campaign_id) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_home_calculator_km_before);
			BasicDBObject documentRemove = new BasicDBObject();
			documentRemove.append("campaign_id", campaign_id);
			do_trackings.deleteMany(documentRemove);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(), Global.log_write_carpo);
		}
	}

	@Override
	public Boolean updateEndPointForDriver(String campaign_id,
			String device_id, LocationGoogle endPoint, org.bson.Document oldData, String created_date_location, String created_time_location) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("campaign_id", campaign_id);
			query.append("device_id", device_id);
			MongoCollection<org.bson.Document> do_users = database
					.getCollection(Global.collection_home_calculator_km_before);
			oldData.append("location_lat", endPoint.getLocation_lat());
			oldData.append("location_long", endPoint.getLocation_long());
			oldData.append("created_date_location", created_date_location);
			oldData.append("created_time_location", created_time_location);
			do_users.replaceOne(query, oldData);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db HomeCalculatorKmBefore:" + e.getMessage(), Global.log_write_carpo);
			return false;
		}
	}

	

}
