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
import model.Customer_CM;
import model.Global;
import model.LocationGoogle;
import model.Tracking;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Override
	public ArrayList<Customer_CM> getListCustomer() {
		// TODO Auto-generated method stub
		ArrayList<Customer_CM> listResult = new ArrayList<Customer_CM>();
		FindIterable<org.bson.Document> listDocumen ;
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_customer);			
			listDocumen = do_campaigns.find();
			listDocumen.forEach(new Block<org.bson.Document>() {
	            @Override
	            public void apply(final org.bson.Document document) {
	            	Customer_CM customer_CM = new Customer_CM();
	            	customer_CM.setCustomer_id(document.get("customer_id").toString());
	            	customer_CM.setCompany_name(document.get("company_name").toString());
	            	customer_CM.setAddress(document.get("address").toString());
	                listResult.add(customer_CM);
	            }
	       });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// wite log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CustomerService:" + e.getMessage(), Global.log_write_carpo);
		}
		return listResult;
	}

	@Override
	public org.bson.Document getDocumentCustomerByUser_id(String user_id) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			ObjectId objectId = new ObjectId(user_id);
			query.append("customer_id", objectId);
			MongoCollection<org.bson.Document> do_cars = database
					.getCollection(Global.collection_customer);
			result = do_cars.find(query).first();
		} catch (Exception e) {
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CustomerService:" + e.getMessage(), Global.log_write_carpo);
		}
		return result;
	}

	

	

	

}
