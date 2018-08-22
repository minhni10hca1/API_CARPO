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
import model.Car;
import model.Global;
import model.LocationGoogle;
import model.Tracking;

@Service
public class DistrictServiceImpl implements DistrictService {


	@Override
	public ArrayList<org.bson.Document> getInfoDocumentDistrictByArea_Code(String area_code) {
		// TODO Auto-generated method stub
		ArrayList<org.bson.Document> listResult = new ArrayList<org.bson.Document>();
		FindIterable<org.bson.Document> listDocumen ;
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("area_code",area_code);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_district);
			listDocumen = do_cars.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
	            @Override
	            public void apply(final org.bson.Document document) {
	                listResult.add(document);
	            }
	       });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db DistrictService:" + e.getMessage(), Global.log_write_carpo);
		}
		return listResult;
	}

	@Override
	public ArrayList<String> getInfoDocumentDistrictCalculatorByArea_Code(
			String area_code) {
		// TODO Auto-generated method stub
		ArrayList<String> listResult = new ArrayList<String>();
		FindIterable<org.bson.Document> listDocumen ;
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("area_code",area_code);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_district_calculator);
			listDocumen = do_cars.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
	            @Override
	            public void apply(final org.bson.Document document) {
	                listResult.add(document.get("name").toString());
	            }
	       });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db DistrictService:" + e.getMessage(), Global.log_write_carpo);
		}
		return listResult;
	}

}
