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
public class AreaServiceImpl implements AreaService {

	@Override
	public org.bson.Document getInfoAreaByCode(String code) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("code", code);
			MongoCollection<org.bson.Document> do_area = database
					.getCollection(Global.collection_area);
			result = do_area.find(query).first();
		} catch (Exception ex) {
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db AreaService:" + ex.getMessage(), Global.log_write_carpo);
		}
		return result;
	}

}
