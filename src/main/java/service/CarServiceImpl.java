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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import model.Car;
import model.Global;
import model.LocationGoogle;
import model.Tracking;
import model_json.formatDataLocation_Home_CM;
import model_json.formatDataUserRunMost_Home_CM;

@Service
public class CarServiceImpl implements CarService {

	@Override
	public ArrayList<org.bson.Document> getListCarByGroupId(String group_id) {
		// TODO Auto-generated method stub
		ArrayList<org.bson.Document> listResult = new ArrayList<org.bson.Document>();
		try {
			FindIterable<org.bson.Document> listDocumen;
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("group_id", new ObjectId(group_id));
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_car);
			listDocumen = do_cars.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					listResult.add(document);
				}
			});
		} catch (Exception e) {
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CarService:" + e.getMessage(), Global.log_write_carpo);
		}
		return listResult;
	}

	@Override
	public ArrayList<org.bson.Document> getListCarByCampaign(String campaign_id) {
		// TODO Auto-generated method stub
		ArrayList<org.bson.Document> listResult = new ArrayList<org.bson.Document>();
		try {
			FindIterable<org.bson.Document> listDocumen;
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("campaign_id", campaign_id);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_car);
			listDocumen = do_cars.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					listResult.add(document);
				}
			});
		} catch (Exception e) {
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CarService:" + e.getMessage(), Global.log_write_carpo);
		}
		return listResult;
	}

	@Override
	public org.bson.Document getInfoDocumentCarByDevice_Id(String device_id) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			if (device_id.isEmpty())
				return result;
			query.append("device_id", device_id);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_car);
			result = do_cars.find(query).first();
		} catch (Exception e) {
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CarService:" + e.getMessage(), Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public ArrayList<org.bson.Document> getAllCar() {
		// TODO Auto-generated method stub
		ArrayList<org.bson.Document> listResult = new ArrayList<org.bson.Document>();
		try {
			FindIterable<org.bson.Document> listDocumen;
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_car);
			listDocumen = do_cars.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					listResult.add(document);
				}
			});
		} catch (Exception e) {
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CarService:" + e.getMessage(), Global.log_write_carpo);
		}
		return listResult;
	}

	@Override
	public void calculatorHomeKmBefore(String campaign_id, String user_id,
			String device_id, double total_km_yesterday,
			double total_km_30_day_before, LocationGoogle locationGoogle,double totalKMInCampaignPartDriver,double totalKmToDay,double totalKMInThreeDayBefore,double totalKMInSevenDayBefore,double persent, double totalKMInCampaignPartDriverOutCty  ) {
		// TODO Auto-generated method stub
		try {
			System.out.print("value connection:" + Global.mongoClient);
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_calculatorHomeBefore = database
					.getCollection(Global.collection_home_calculator_km_before);
			// insert
			org.bson.Document do_insert = new org.bson.Document(
					"campaign_id", campaign_id);
			do_insert.append("user_id", user_id);
			do_insert.append("device_id", device_id);
			// convert
			do_insert.append("total_km_yesterday", total_km_yesterday);
			// convert
			do_insert.append("total_km_30_day_before", total_km_30_day_before);
			do_insert.append("location_lat", locationGoogle.getLocation_lat());
			do_insert.append("location_long", locationGoogle.getLocation_long());
			// time
			// new using for home driver -
			do_insert.append("total_km_month", totalKMInCampaignPartDriver);
			do_insert.append("total_km_month_out_cty", totalKMInCampaignPartDriverOutCty);
			do_insert.append("total_km_today", totalKmToDay);
			do_insert.append("total_km_three_day_before", totalKMInThreeDayBefore);
			do_insert.append("total_km_seven_day_before", totalKMInSevenDayBefore);
			do_insert.append("total_percent_month", persent);
			// date
			Date today = Calendar.getInstance().getTime();
			do_insert.append("created_date",
					GlobalUtils.convertStringToDate(today));
			do_insert.append("created_time",
					GlobalUtils.convertStringToTime(today));
			do_calculatorHomeBefore.insertOne(do_insert);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CarService:" + e.getMessage(), Global.log_write_carpo);
		}
	}

}
