package service;

import java.net.UnknownHostException;
import java.text.ParseException;
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
import org.springframework.beans.factory.annotation.Autowired;
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
public class CampaignServiceImpl implements CampaignService {

	@Autowired
	AreaService areaService;

	@Override
	public org.bson.Document getInfoCampaignById(String campaign_id) {
		// TODO Auto-generated method stub
		org.bson.Document result = new org.bson.Document();
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			if (campaign_id.isEmpty())
				return result;
			query.append("_id", new ObjectId(campaign_id));
			MongoCollection<org.bson.Document> do_campaign = database
					.getCollection(Global.collection_campaign);
			result = do_campaign.find(query).first();
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CampaignService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public ArrayList<Campaign_CM> getListCampaignByCustomerId(String customer_id) {
		// TODO Auto-generated method stub
		ArrayList<Campaign_CM> listResult = new ArrayList<Campaign_CM>();
		try {
			FindIterable<org.bson.Document> listDocumen;
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("customer_id", new ObjectId(customer_id));
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_campaign);
			listDocumen = do_campaigns.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					// listResult.add(document);
					Campaign_CM campaign_CM = new Campaign_CM();
					campaign_CM.set_id(document.get("_id").toString());
					campaign_CM.setCustomer_id(document.get("customer_id")
							.toString());
					campaign_CM.setName(document.get("name").toString());
					campaign_CM.setTotal_distance(document
							.get("total_distance").toString());
					campaign_CM.setStart_time(document.get("start_time")
							.toString());
					campaign_CM
							.setEnd_time(document.get("end_time").toString());
					campaign_CM.setTotal_car(document.get("total_car")
							.toString());
					campaign_CM.setTotal_car_advertising(document.get(
							"total_car_advertising").toString());
					campaign_CM.setArea_code(document.get("area_code")
							.toString());
					org.bson.Document documentArea = areaService
							.getInfoAreaByCode(document.get("area_code")
									.toString());
					if (documentArea != null)
						campaign_CM.setArea_name(documentArea.get("name")
								.toString());
					campaign_CM
							.setCar_wage(document.get("car_wage").toString());
					listResult.add(campaign_CM);
				}
			});
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CampaignService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return listResult;
	}

	@Override
	public ArrayList<Campaign_CM> getAllCampaign() {
		// TODO Auto-generated method stub
		ArrayList<Campaign_CM> listResult = new ArrayList<Campaign_CM>();
		try {
			FindIterable<org.bson.Document> listDocumen;
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_campaign);
			listDocumen = do_campaigns.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					// listResult.add(document);
					Campaign_CM campaign_CM = new Campaign_CM();
					campaign_CM.set_id(document.get("_id").toString());
					campaign_CM.setCustomer_id(document.get("customer_id")
							.toString());
					campaign_CM.setStart_time(document.get("start_time")
							.toString());
					campaign_CM
							.setEnd_time(document.get("end_time").toString());
					listResult.add(campaign_CM);
				}
			});
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CampaignService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return listResult;
	}
	
	

	@Override
	public void calculator_campaign_part_driver(String campaign_id) {
		// TODO Auto-generated method stub
		try {
			Date today = Calendar.getInstance().getTime();
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("_id", new ObjectId(campaign_id));
			MongoCollection<org.bson.Document> do_campaign = database
					.getCollection(Global.collection_campaign);
			org.bson.Document result = do_campaign.find(query).first();
			if(result != null){
				String fromDate = result.get("start_time").toString();
				String toDate = result.get("end_time").toString();
				Date date_fromDate = GlobalUtils.convertStrToDate(fromDate);
				Date date_toDate = GlobalUtils.convertStrToDate(toDate);
				// calculate
//				Calendar cal = Calendar.getInstance();
//				Date today = cal.getTime();
				ArrayList<org.bson.Document> listDocumentInsert = new ArrayList<org.bson.Document>();
				// date -part
				Date date_part_from = new Date();
				Date date_part_to = new Date();
				
				// ky 1 --> fromdate + 30 days
				Calendar cal = Calendar.getInstance();
				cal.setTime(date_fromDate);
				cal.add(Calendar.DATE, 30); 
				Date date_part_1 = cal.getTime();
				if (date_toDate.compareTo(date_part_1) <= 0){
					// chi co 1 part la fromdate - todate
					date_part_from = date_fromDate;
					date_part_to = date_fromDate;
					// add vao document de insert
					org.bson.Document document = new org.bson.Document();
					document.append("campaign_id",new ObjectId(campaign_id));
					document.append("from_date", GlobalUtils.convertStringToDate(date_part_from));
					document.append("to_date", GlobalUtils.convertStringToDate(date_part_to));
					document.append("created_date",
							GlobalUtils.convertStringToDate(today));
					document.append("created_time",
							GlobalUtils.convertStringToTime(today));
					listDocumentInsert.add(document);
				}else{
					Date date_from_temp = date_fromDate;
					while (date_toDate.compareTo(date_part_1) > 0){
						// part1
						date_part_from = date_from_temp;
						date_part_to = date_part_1;
						// add vao document de insert
						org.bson.Document document = new org.bson.Document();
						document.append("campaign_id", new ObjectId(campaign_id));
						document.append("from_date", GlobalUtils.convertStringToDate(date_part_from));
						document.append("to_date", GlobalUtils.convertStringToDate(date_part_to));
						document.append("created_date",
								GlobalUtils.convertStringToDate(today));
						document.append("created_time",
								GlobalUtils.convertStringToTime(today));
						listDocumentInsert.add(document);
						
						// tiep part 2
						Calendar calnext = Calendar.getInstance();
						calnext.setTime(date_part_to);
						calnext.add(Calendar.DATE, 1);
						date_from_temp = calnext.getTime();
						
						calnext.add(Calendar.DATE, 30);
						date_part_1 = calnext.getTime();
						if (date_toDate.compareTo(date_part_1) <= 0){
							// part cuoi
							date_part_from = date_from_temp;
							date_part_to = date_toDate;
							// add vao document de insert
							org.bson.Document document2 = new org.bson.Document();
							document2.append("campaign_id", new ObjectId(campaign_id));
							document2.append("from_date", GlobalUtils.convertStringToDate(date_part_from));
							document2.append("to_date", GlobalUtils.convertStringToDate(date_part_to));
							document2.append("created_date",
									GlobalUtils.convertStringToDate(today));
							document2.append("created_time",
									GlobalUtils.convertStringToTime(today));
							listDocumentInsert.add(document2);
						}						
					}
				}
				// insert many
				if (listDocumentInsert.size() > 0){
					try {						
						MongoCollection<org.bson.Document> do_campaign_part_driver = database
								.getCollection(Global.collection_campaign_part_driver);
						do_campaign_part_driver.insertMany(listDocumentInsert);
					} catch (Exception ex) {
						FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
								+ " error db trackingService:" + ex.getMessage(), Global.log_write_carpo);
					}
				}
				
				
			}
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CampaignService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		
	}

	@Override
	public ArrayList<org.bson.Document> getInfoCampaignPartDriverByCampaignId(
			String campaign_id) {
		// TODO Auto-generated method stub
		ArrayList<org.bson.Document> result = new ArrayList<org.bson.Document>();
		FindIterable<org.bson.Document> listDocumen;
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("campaign_id", new ObjectId(campaign_id));
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_campaign_part_driver);
			listDocumen = do_campaigns.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					// listResult.add(document);
					result.add(document);
				}
			});
			
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CampaignService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public ArrayList<Campaign_CM> getAllCampaignStillValidated() {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		//String strToday = GlobalUtils.convertStringToDate(today);
		ArrayList<Campaign_CM> listResult = new ArrayList<Campaign_CM>();
		try {
			FindIterable<org.bson.Document> listDocumen;
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_campaign);
			listDocumen = do_campaigns.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					// listResult.add(document);
					Campaign_CM campaign_CM = new Campaign_CM();
					campaign_CM.set_id(document.get("_id").toString());
					campaign_CM.setCustomer_id(document.get("customer_id")
							.toString());
					campaign_CM.setStart_time(document.get("start_time")
							.toString());
					campaign_CM
							.setEnd_time(document.get("end_time").toString());
					try {
						Date from_date_db = GlobalUtils.convertStrToDate(document.get(
								"start_time").toString());
						Date to_date_db = GlobalUtils.convertStrToDate(document.get(
								"end_time").toString());
						int compare1 = from_date_db.compareTo(today);
						int compare2 = today.compareTo(to_date_db);
						if (compare1 <= 0 && compare2 <= 0)
							listResult.add(campaign_CM);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			});
		} catch (Exception e) {
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db CampaignService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return listResult;
	}

}
