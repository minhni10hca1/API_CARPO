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
import model.Report;
import model.Tracking;

@Service
public class ReportServiceImpl implements ReportService {

	
	@Override
	public String insertReport(Report pReport) {
		// TODO Auto-generated method stub
		String result = "";
		try {
			MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_reports = database.getCollection(Global.collection_report);
			org.bson.Document report = new org.bson.Document("user_id", new ObjectId(pReport.getUser_id()));
			//report.append("role_code", pReport.getRole_code());
			report.append("message", pReport.getMessage());
			report.append("image", pReport.getImage());
			report.append("status", pReport.getStatus());
			report.append("created_date", pReport.getCreated_date());
			report.append("created_time", pReport.getCreated_time());
			do_reports.insertOne(report);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			TrackingController.result_sms = e.getMessage();
			result = "102";
			// writelog
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db ReportService:" + e.getMessage(), Global.log_write_carpo);
		}
		return result;
	}


}
