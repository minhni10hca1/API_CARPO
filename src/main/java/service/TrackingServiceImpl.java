package service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.taskdefs.Sync.MyCopy;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.hql.ast.origin.hql.parse.HQLParser.new_key_return;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
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
import model.User;
import model_json.formatDataLocation_Home_CM;
import model_json.formatDataUserRunMost_Home_CM;

@Service
public class TrackingServiceImpl implements TrackingService {

	@Override
	public Boolean insertTracking(org.bson.Document do_tracking) {
		// TODO Auto-generated method stub
		Boolean result;
		// mongoClient = MongoUtils.getMongoClient_BM();
		try {
			System.out.print("value connection:" + Global.mongoClient);
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			// MongoCollection<Tracking> collection =
			// database.getCollection(Global.collection_tracking,Tracking.class);
			// collection.insertOne(tracking);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			// org.bson.Document do_tracking = new org.bson.Document("car_id",
			// tracking.getCar_id());
			// do_tracking.append("campaign_id", tracking.getCampaign_id());
			// do_tracking.append("location_lat", tracking.getLocation_lat());
			// do_tracking.append("location_long",tracking.getLocation_long());
			// do_tracking.append("type", tracking.getType());
			// do_tracking.append("district_code", "10" );
			// do_tracking.append("district_name", tracking.getDistrict_name());
			// do_tracking.append("device_id", tracking.getDevice_id());
			// do_tracking.append("created_date", tracking.getCreated_date());
			// do_tracking.append("created_time", tracking.getCreated_time());
			do_trackings.insertOne(do_tracking);
			result = true;
		} catch (Exception ex) {
			result = false;
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + ex.getMessage(),
					Global.log_write_carpo);
		}
		// mongoClient.close();
		return result;
	}

	@Override
	public ArrayList<LocationGoogle> getListLocationTrackingByDate(
			String strDate) {
		// TODO Auto-generated method stub
		ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
		FindIterable<org.bson.Document> listDocumen;
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			BasicDBObject obj = new BasicDBObject();
			obj.append("$eq", strDate);
			query.append("created_date", obj);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			listDocumen = do_trackings.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					LocationGoogle locationGoogle = new LocationGoogle();
					locationGoogle.setLocation_lat(document
							.getString("location_lat"));
					locationGoogle.setLocation_long(document
							.getString("location_long"));
					result.add(locationGoogle);
				}
			});
			if (result.size() > 0)
				TrackingController.status_result = true;
			else {
				TrackingController.status_result = false;
				TrackingController.result_sms = "don't have data with date: "
						+ strDate;
			}
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			TrackingController.status_result = false;
			TrackingController.result_sms = e.getMessage();
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
			return null;
		}

	}

	@Override
	public ArrayList<LocationGoogle> getListLocationTrackingByDateAndDevice_id(
			String strDate, String device_id) {
		// TODO Auto-generated method stub
		ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
		FindIterable<org.bson.Document> listDocumen;
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			BasicDBObject obj = new BasicDBObject();
			obj.append("$eq", strDate);
			query.append("created_date", obj);
			query.append("device_id", device_id);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			listDocumen = do_trackings.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					LocationGoogle locationGoogle = new LocationGoogle();
					locationGoogle.setLocation_lat(document
							.getString("location_lat"));
					locationGoogle.setLocation_long(document
							.getString("location_long"));
					result.add(locationGoogle);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public ArrayList<LocationGoogle> getListLocationTrackingByBigerDateAndDevice_id(
			String strDate, String device_id) {
		// TODO Auto-generated method stub
		ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
		FindIterable<org.bson.Document> listDocumen;
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			BasicDBObject obj = new BasicDBObject();
			obj.append("$gt", strDate);
			query.append("created_date", obj);
			query.append("device_id", device_id);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			listDocumen = do_trackings.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					LocationGoogle locationGoogle = new LocationGoogle();
					locationGoogle.setLocation_lat(document
							.getString("location_lat"));
					locationGoogle.setLocation_long(document
							.getString("location_long"));
					result.add(locationGoogle);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	// @Override
	// public ArrayList<LocationGoogle>
	// getListLocationTrackingByTwoDateAndDevice_id(
	// String strFromDate, String strToDate, String device_id) {
	// // TODO Auto-generated method stub
	// ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
	// FindIterable<org.bson.Document> listDocumen ;
	// try {
	// MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
	// BasicDBObject query = new BasicDBObject();
	// BasicDBObject obj = new BasicDBObject();
	// obj.append("$gt", strFromDate);
	// obj.append("$lt", strToDate);
	// query.append("created_date",obj);
	// // BasicDBObject obj1 = new BasicDBObject();
	// // obj.append("$lt", strToDate);
	// // query.append("created_date",obj1);
	// query.append("device_id",device_id);
	// //query.append("campaign_id",campaign_id);
	//
	// MongoCollection<org.bson.Document> do_trackings = database
	// .getCollection(Global.collection_tracking);
	// listDocumen = do_trackings.find(query);
	// listDocumen.forEach(new Block<org.bson.Document>() {
	// @Override
	// public void apply(final org.bson.Document document) {
	// LocationGoogle locationGoogle = new LocationGoogle();
	// locationGoogle.setLocation_lat(document.getString("location_lat"));
	// locationGoogle.setLocation_long(document.getString("location_long"));
	// result.add(locationGoogle);
	// }
	// });
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// // write log
	// }
	// return result;
	// }

	// @Override
	// public ArrayList<LocationGoogle>
	// getListLocationTrackingByDateAndCar_idAndCampaign_id(
	// String strDate, String car_id, String campaign_id) {
	// // TODO Auto-generated method stub
	// ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
	// FindIterable<org.bson.Document> listDocumen ;
	// try {
	// MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
	// org.bson.Document query = new org.bson.Document();
	// BasicDBObject obj = new BasicDBObject();
	// obj.append("$eq", strDate);
	// query.append("created_date",obj);
	// query.append("car_id",car_id);
	// query.append("campaign_id",campaign_id);
	// MongoCollection<org.bson.Document> do_trackings = database
	// .getCollection(Global.collection_tracking);
	// listDocumen = do_trackings.find(query);
	// listDocumen.forEach(new Block<org.bson.Document>() {
	// @Override
	// public void apply(final org.bson.Document document) {
	// LocationGoogle locationGoogle = new LocationGoogle();
	// locationGoogle.setLocation_lat(document.getString("location_lat"));
	// locationGoogle.setLocation_long(document.getString("location_long"));
	// result.add(locationGoogle);
	// }
	// });
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// // write log
	// }
	// return result;
	// }

	// @Override
	// public ArrayList<LocationGoogle> getListLocationTrackingByCampaign(
	// String campaign_id) {
	// // TODO Auto-generated method stub
	// ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
	// FindIterable<org.bson.Document> listDocumen ;
	// try {
	// MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
	// org.bson.Document query = new org.bson.Document();
	// query.append("campaign_id",campaign_id);
	// MongoCollection<org.bson.Document> do_trackings = database
	// .getCollection(Global.collection_tracking);
	// listDocumen = do_trackings.find(query);
	// listDocumen.forEach(new Block<org.bson.Document>() {
	// @Override
	// public void apply(final org.bson.Document document) {
	// LocationGoogle locationGoogle = new LocationGoogle();
	// locationGoogle.setLocation_lat(document.getString("location_lat"));
	// locationGoogle.setLocation_long(document.getString("location_long"));
	// result.add(locationGoogle);
	// }
	// });
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// // write log
	//
	// }
	// return result;
	// }

	// @Override
	// public ArrayList<LocationGoogle> getListLocationTrackingByCar_id(
	// String car_id) {
	// // TODO Auto-generated method stub
	// ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
	// FindIterable<org.bson.Document> listDocumen ;
	// try {
	// MongoDatabase database = Global.mongoClient.getDatabase(Global.DB_NAME);
	// org.bson.Document query = new org.bson.Document();
	// query.append("car_id",car_id);
	// MongoCollection<org.bson.Document> do_trackings = database
	// .getCollection(Global.collection_tracking);
	// listDocumen = do_trackings.find(query);
	// listDocumen.forEach(new Block<org.bson.Document>() {
	// @Override
	// public void apply(final org.bson.Document document) {
	// LocationGoogle locationGoogle = new LocationGoogle();
	// locationGoogle.setLocation_lat(document.getString("location_lat"));
	// locationGoogle.setLocation_long(document.getString("location_long"));
	// result.add(locationGoogle);
	// }
	// });
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// //write log
	// }
	// return result;
	// }

	@Override
	public ArrayList<LocationGoogle> getListLocationTrackingByDevice_idAndDistrictCode(
			String device_id, String district_code) {
		// TODO Auto-generated method stub
		ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
		FindIterable<org.bson.Document> listDocumen;
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("device_id", device_id);
			query.append("district_code", district_code);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			listDocumen = do_trackings.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					LocationGoogle locationGoogle = new LocationGoogle();
					locationGoogle.setLocation_lat(document
							.getString("location_lat"));
					locationGoogle.setLocation_long(document
							.getString("location_long"));
					result.add(locationGoogle);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public ArrayList<LocationGoogle> getListLocationTrackingByDevice_id(
			String device_id) {
		// TODO Auto-generated method stub
		ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
		FindIterable<org.bson.Document> listDocumen;
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			BasicDBObject query = new BasicDBObject();
			query.append("device_id", device_id);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			listDocumen = do_trackings.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					LocationGoogle locationGoogle = new LocationGoogle();
					locationGoogle.setLocation_lat(document
							.getString("location_lat"));
					locationGoogle.setLocation_long(document
							.getString("location_long"));
					result.add(locationGoogle);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return result;
	}

	@Override
	public Boolean checkCarIsRunning(String car_id) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			// them trang thai xe dang chay -> khi gps box ok gan vao
			query.append("car_id", car_id);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			org.bson.Document document = do_trackings.find(query).first();
			if (document != null)
				return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return false;
	}

	@Override
	public double getMaxDistanceByDevice_Id(String devive_id) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_tracking);
			List<BasicDBObject> listQuery = new ArrayList<BasicDBObject>();//
			// group
			BasicDBObject query = new BasicDBObject();
			BasicDBObject obj1 = new BasicDBObject();
			obj1.append("device_id", devive_id);
			BasicDBObject obj11 = new BasicDBObject();
			ArrayList<BasicDBObject> objArayEnd = new ArrayList<BasicDBObject>();
			objArayEnd.add(obj1);
			obj11.append("$and", objArayEnd);
			query.append("$match", obj11);
			// end
			// group
			BasicDBObject obj_total = new BasicDBObject();
			BasicDBObject obj_device = new BasicDBObject();
			obj_device.append("device_id", "$device_id");
			obj_device.append("created_date", "$created_date");
			BasicDBObject obj12 = new BasicDBObject();
			obj_total.append("_id", obj_device);
			//
			// objdistance
			BasicDBObject obj_$max = new BasicDBObject();
			BasicDBObject obj_Distance = new BasicDBObject();
			obj_Distance.append("$distance", 0);
			// ArrayList<BasicDBObject> objArayDistance = new
			// ArrayList<BasicDBObject>();
			BasicDBObject obj_ifnull = new BasicDBObject();
			// objArayDistance.add(obj_Distance);
			obj_ifnull.append("$ifNull", Arrays.asList("$distance", 0));
			obj_$max.append("$max", obj_ifnull);
			obj_total.append("totalDistance", obj_$max);
			// group
			BasicDBObject obj_query2 = new BasicDBObject();
			obj_query2.append("$group", obj_total);

			listQuery.add(query);
			listQuery.add(obj_query2);
			AggregateIterable<org.bson.Document> listDocumen = do_campaigns
					.aggregate(listQuery);
			double result = 0;
			for (org.bson.Document document : listDocumen) {
				String totalDistance = document.get("totalDistance").toString();
				result += Double.parseDouble(totalDistance);
			}
			return FunctionUtil.roundDouble(result / 1000);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
			return 0;
		}
	}

	@Override
	public double getMaxDistanceByListDevice_IdAndDistrictName(
			ArrayList<String> list_device_car, String districtName) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_tracking);

			List<BasicDBObject> listQuery = new ArrayList<BasicDBObject>();
			BasicDBObject query = new BasicDBObject();
			BasicDBObject obj1 = new BasicDBObject();

			BasicDBObject objIn = new BasicDBObject();
			objIn.append("$in", list_device_car);
			obj1.append("device_id", objIn);
			BasicDBObject objDistrictName = new BasicDBObject();
			objDistrictName.append("district_name", districtName);
			BasicDBObject obj11 = new BasicDBObject();
			ArrayList<BasicDBObject> objArayEnd = new ArrayList<BasicDBObject>();
			objArayEnd.add(obj1);
			objArayEnd.add(objDistrictName);
			obj11.append("$and", objArayEnd);
			// obj11.append("$and", objArayEnd);
			query.append("$match", obj11);
			// end
			// group
			BasicDBObject obj_total = new BasicDBObject();
			BasicDBObject obj_device = new BasicDBObject();
			obj_device.append("device_id", "$device_id");
			obj_device.append("created_date", "$created_date");
			BasicDBObject obj12 = new BasicDBObject();
			obj_total.append("_id", obj_device);
			//
			// objdistance
			BasicDBObject obj_$max = new BasicDBObject();
			BasicDBObject obj_Distance = new BasicDBObject();
			obj_Distance.append("$distance", 0);
			// ArrayList<BasicDBObject> objArayDistance = new
			// ArrayList<BasicDBObject>();
			BasicDBObject obj_ifnull = new BasicDBObject();
			// objArayDistance.add(obj_Distance);
			obj_ifnull.append("$ifNull", Arrays.asList("$distance", 0));
			obj_$max.append("$max", obj_ifnull);
			obj_total.append("totalDistance", obj_$max);
			// group
			BasicDBObject obj_query2 = new BasicDBObject();
			obj_query2.append("$group", obj_total);

			listQuery.add(query);
			listQuery.add(obj_query2);
			AggregateIterable<org.bson.Document> listDocumen = do_campaigns
					.aggregate(listQuery);
			double result = 0;
			for (org.bson.Document document : listDocumen) {
				String totalDistance = document.get("totalDistance").toString();
				result += Double.parseDouble(totalDistance);
			}
			return FunctionUtil.roundDouble(result / 1000);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
			return 0;
		}
	}

	@Override
	public double getMaxDistanceByDeviceIdAndDate(String devive_id, String date) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_tracking = database
					.getCollection(Global.collection_tracking);

			org.bson.Document query = new org.bson.Document();
			query.append("device_id", devive_id);
			BasicDBObject objDate = new BasicDBObject();
			objDate.append("$eq", date);
			query.append("created_date", objDate);
			// sort
			BasicDBObject objSort = new BasicDBObject();
			objSort.append("created_date", -1);
			objSort.append("created_time", -1);
			org.bson.Document resultDocumen = do_tracking.find(query)
					.sort(objSort).first();
			double result = 0;
			if (resultDocumen != null) {
				result = Double.parseDouble(resultDocumen.get("distance")
						.toString());
				result = result / 1000;
			}
			return FunctionUtil.roundDouble(result);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
			return 0;
		}
	}

	@Override
	public double getMaxDistanceByDevice_IdAndBigerDate(String devive_id,
			String date) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_tracking);

			List<BasicDBObject> listQuery = new ArrayList<BasicDBObject>();// =
			// group
			BasicDBObject query = new BasicDBObject();
			BasicDBObject obj1 = new BasicDBObject();
			obj1.append("device_id", devive_id);
			BasicDBObject obj11 = new BasicDBObject();
			ArrayList<BasicDBObject> objArayEnd = new ArrayList<BasicDBObject>();
			objArayEnd.add(obj1);
			// with date
			BasicDBObject objDate = new BasicDBObject();
			objDate.append("$gt", date);
			BasicDBObject queryDate = new BasicDBObject();
			queryDate.append("created_date", objDate);
			// date
			objArayEnd.add(queryDate);
			// end
			obj11.append("$and", objArayEnd);
			query.append("$match", obj11);
			// end
			// group
			BasicDBObject obj_total = new BasicDBObject();
			BasicDBObject obj_device = new BasicDBObject();
			obj_device.append("device_id", "$device_id");
			obj_device.append("created_date", "$created_date");
			BasicDBObject obj12 = new BasicDBObject();
			obj_total.append("_id", obj_device);
			//
			// objdistance
			BasicDBObject obj_$max = new BasicDBObject();
			BasicDBObject obj_Distance = new BasicDBObject();
			obj_Distance.append("$distance", 0);
			// ArrayList<BasicDBObject> objArayDistance = new
			// ArrayList<BasicDBObject>();
			BasicDBObject obj_ifnull = new BasicDBObject();
			// objArayDistance.add(obj_Distance);
			obj_ifnull.append("$ifNull", Arrays.asList("$distance", 0));
			obj_$max.append("$max", obj_ifnull);
			obj_total.append("totalDistance", obj_$max);
			// group
			BasicDBObject obj_query2 = new BasicDBObject();
			obj_query2.append("$group", obj_total);

			listQuery.add(query);
			listQuery.add(obj_query2);
			AggregateIterable<org.bson.Document> listDocumen = do_campaigns
					.aggregate(listQuery);
			double result = 0;

			// listDocumen.forEach(new Block<org.bson.Document>() {
			// @Override
			// public void apply(final org.bson.Document document) {
			// LocationGoogle locationGoogle = new LocationGoogle();
			// locationGoogle.setLocation_lat(document.getString("location_lat"));
			// locationGoogle.setLocation_long(document.getString("location_long"));
			//
			// }
			// });

			for (org.bson.Document document : listDocumen) {
				String totalDistance = document.get("totalDistance").toString();
				result += Double.parseDouble(totalDistance);
			}
			return FunctionUtil.roundDouble(result / 1000);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
			return 0;
		}
	}

	@Override
	public void getDistanceYesterDayAndistance30DaysBeforeByDevice_IdAndBigerDate(
			String devive_id, String date, String dateYesterDay) {
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_tracking);
			List<BasicDBObject> listQuery = new ArrayList<BasicDBObject>();// =
			// group
			BasicDBObject query = new BasicDBObject();
			BasicDBObject obj1 = new BasicDBObject();
			obj1.append("device_id", devive_id);
			BasicDBObject obj11 = new BasicDBObject();
			ArrayList<BasicDBObject> objArayEnd = new ArrayList<BasicDBObject>();
			objArayEnd.add(obj1);
			// with date
			BasicDBObject objDate = new BasicDBObject();
			objDate.append("$gt", date);
			BasicDBObject queryDate = new BasicDBObject();
			queryDate.append("created_date", objDate);
			// date
			objArayEnd.add(queryDate);
			// end
			obj11.append("$and", objArayEnd);
			query.append("$match", obj11);
			// end
			// group
			BasicDBObject obj_total = new BasicDBObject();
			BasicDBObject obj_device = new BasicDBObject();
			obj_device.append("device_id", "$device_id");
			obj_device.append("created_date", "$created_date");
			BasicDBObject obj12 = new BasicDBObject();
			obj_total.append("_id", obj_device);
			//
			// objdistance
			BasicDBObject obj_$max = new BasicDBObject();
			BasicDBObject obj_Distance = new BasicDBObject();
			obj_Distance.append("$distance", 0);
			// ArrayList<BasicDBObject> objArayDistance = new
			// ArrayList<BasicDBObject>();
			BasicDBObject obj_ifnull = new BasicDBObject();
			// objArayDistance.add(obj_Distance);
			obj_ifnull.append("$ifNull", Arrays.asList("$distance", 0));
			obj_$max.append("$max", obj_ifnull);
			obj_total.append("totalDistance", obj_$max);
			// group
			BasicDBObject obj_query2 = new BasicDBObject();
			obj_query2.append("$group", obj_total);

			listQuery.add(query);
			listQuery.add(obj_query2);
			AggregateIterable<org.bson.Document> listDocumen = do_campaigns
					.aggregate(listQuery);
			double distance30DayBefore = 0;
			double distanceYesterday = 0;
			int index = 1;
			for (org.bson.Document document : listDocumen) {
				String totalDistance = document.get("totalDistance").toString();
				org.bson.Document documentDate = (org.bson.Document) document
						.get("_id");
				String dateFind = documentDate.get("created_date").toString();
				distance30DayBefore += Double.parseDouble(totalDistance);
				if (dateFind.equals(dateYesterDay))
					distanceYesterday += Double.parseDouble(totalDistance);
				if (index == 1)
					CarController.distance_max_in_day_near = totalDistance;
				index++;

			}
			CarController.distanceYesterday = distanceYesterday / 1000;
			CarController.distance30DaysBefore = distance30DayBefore / 1000;
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}

	}

	@Override
	public void getLatLongByDevice_IdAndDistance(String devive_id,
			String distance) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("device_id", devive_id);
			query.append("distance",
					FunctionUtil.roundDouble(Double.parseDouble(distance)));
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			org.bson.Document document = do_trackings.find(query).first();
			if (document != null) {
				CarController.location_lat = document.get("location_lat")
						.toString();
				CarController.location_long = document.get("location_long")
						.toString();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}

	}

	@Override
	public void saveHomeCustomerToDatabase(String campaign_id,
			String total_km_run, String total_user_drive,
			ArrayList<formatDataUserRunMost_Home_CM> user_run_most,
			ArrayList<formatDataLocation_Home_CM> total_km_location) {
		// TODO Auto-generated method stub
		try {
			System.out.print("value connection:" + Global.mongoClient);
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			// MongoCollection<Tracking> collection =
			// database.getCollection(Global.collection_tracking,Tracking.class);
			// collection.insertOne(tracking);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_home_customer);
			// insert
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			org.bson.Document do_tracking = new org.bson.Document(
					"campaign_id", campaign_id);
			do_tracking.append("total_km_run", total_km_run);
			do_tracking.append("total_user_drive", total_user_drive);
			// convert
			ArrayList<BasicDBObject> arrayBsonTotal_km_location = new ArrayList<BasicDBObject>();
			for (formatDataLocation_Home_CM total_location : total_km_location) {
				BasicDBObject dbObject = new BasicDBObject();
				dbObject.append("district_code",
						total_location.getDistrict_code());
				dbObject.append("district_name",
						total_location.getDistrict_name());
				dbObject.append("total_distance",
						total_location.getTotal_distance());
				arrayBsonTotal_km_location.add(dbObject);
			}
			do_tracking.append("total_km_location", arrayBsonTotal_km_location);
			// convert
			ArrayList<BasicDBObject> arrayBsonUserRunMost = new ArrayList<BasicDBObject>();
			for (formatDataUserRunMost_Home_CM userRunMost : user_run_most) {
				BasicDBObject dbObject = new BasicDBObject();
				dbObject.append("_id", userRunMost.get_id());
				dbObject.append("name", userRunMost.getName());
				dbObject.append("total_km", userRunMost.getTotal_km());
				arrayBsonUserRunMost.add(dbObject);
			}
			do_tracking.append("user_run_most", arrayBsonUserRunMost);
			// time
			Date today = Calendar.getInstance().getTime();
			do_tracking.append("created_date",
					GlobalUtils.convertStringToDate(today));
			do_tracking.append("created_time",
					GlobalUtils.convertStringToTime(today));
			do_trackings.insertOne(do_tracking);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}

	}

	@Override
	public void deleteHomeCustomer() {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_home_customer);
			BasicDBObject documentRemove = new BasicDBObject();
			do_trackings.deleteMany(documentRemove);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}
	}

	@Override
	public org.bson.Document getDocumentHomeCustomerByCampaignId(
			String campaign_id) {
		// TODO Auto-generated method stub
		org.bson.Document document = null;
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			org.bson.Document query = new org.bson.Document();
			query.append("campaign_id", campaign_id);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_home_customer);
			document = do_trackings.find(query).first();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
		}
		return document;
	}

	@Override
	public double getSumDistanceTrackingByTwoDateAndDevice_id(
			String strFromDate, String strToDate, String device_id) {
		// TODO Auto-generated method stub
		// MongoDatabase database =
		// Global.mongoClient.getDatabase(Global.DB_NAME);
		// MongoCollection<org.bson.Document> do_campaigns = database
		// .getCollection(Global.collection_tracking);
		//
		// List<BasicDBObject> listQuery = new ArrayList<BasicDBObject>();//=
		// Arrays.asList(new Document("$group", new Document("_id",
		// "$device_id")),new Document("$sort", new Document("_id.device_id",
		// 1)));
		// // group
		// BasicDBObject query = new BasicDBObject();
		// BasicDBObject obj1 = new BasicDBObject();
		// obj1.append("device_id", device_id);
		// BasicDBObject obj11 = new BasicDBObject();
		// ArrayList<BasicDBObject> objArayEnd = new ArrayList<BasicDBObject>();
		// objArayEnd.add(obj1);
		// // with date
		// BasicDBObject objDate = new BasicDBObject();
		// objDate.append("$gt", strFromDate);
		// objDate.append("$lt", strToDate);
		// BasicDBObject queryDate = new BasicDBObject();
		// queryDate.append("created_date",objDate);
		// // date
		// objArayEnd.add(queryDate);
		// // end
		// obj11.append("$and", objArayEnd);
		// query.append("$match",obj11);
		// //end
		// // group
		// BasicDBObject obj_total = new BasicDBObject();
		// BasicDBObject obj_device = new BasicDBObject();
		// obj_device.append("device_id", "$device_id");
		// obj_device.append("created_date", "$created_date");
		// BasicDBObject obj12 = new BasicDBObject();
		// obj_total.append("_id", obj_device);
		// // objdistance
		// BasicDBObject obj_$max = new BasicDBObject();
		// // BasicDBObject obj_Distance = new BasicDBObject();
		// // obj_Distance.append("$distance", 0);
		// // ArrayList<BasicDBObject> objArayDistance = new
		// ArrayList<BasicDBObject>();
		// BasicDBObject obj_ifnull = new BasicDBObject();
		// // objArayDistance.add(obj_Distance);
		// obj_ifnull.append("$ifNull", Arrays.asList("$distance",0));
		// obj_$max.append("$max", obj_ifnull);
		// obj_total.append("totalDistance", obj_$max);
		// // group
		// BasicDBObject obj_query2 = new BasicDBObject();
		// obj_query2.append("$group", obj_total);
		//
		// // group sum
		// BasicDBObject obj_total1 = new BasicDBObject();
		// BasicDBObject obj_device1 = new BasicDBObject();
		// obj_device1.append("device_id", "$device_id");
		// BasicDBObject obj121 = new BasicDBObject();
		// obj_total1.append("_id", obj_device1);
		// // objdistance
		// BasicDBObject obj_$sum = new BasicDBObject();
		// obj_$sum.append("$sum", "$totalDistance");
		// obj_total1.append("sumDistance", obj_$sum);
		// BasicDBObject obj_query3 = new BasicDBObject();
		// obj_query3.append("$group", obj_total1);
		//
		// listQuery.add(query);
		// listQuery.add(obj_query2);
		// listQuery.add(obj_query3);
		// AggregateIterable<org.bson.Document> listDocumen =
		// do_campaigns.aggregate(listQuery);
		// double result = 0;
		// for (org.bson.Document document : listDocumen){
		// String totalDistance = document.get("sumDistance").toString();
		// result += Double.parseDouble(totalDistance);
		// }
		// return FunctionUtil.roundDouble(result/1000);

		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_tracking);

			List<BasicDBObject> listQuery = new ArrayList<BasicDBObject>();//
			BasicDBObject query = new BasicDBObject();
			BasicDBObject obj1 = new BasicDBObject();
			obj1.append("device_id", device_id);
			BasicDBObject obj11 = new BasicDBObject();
			ArrayList<BasicDBObject> objArayEnd = new ArrayList<BasicDBObject>();
			objArayEnd.add(obj1);
			// with date
			BasicDBObject objDate = new BasicDBObject();
			objDate.append("$gt", strFromDate);
			objDate.append("$lt", strToDate);
			BasicDBObject queryDate = new BasicDBObject();
			queryDate.append("created_date", objDate);
			// date
			objArayEnd.add(queryDate);
			// end
			obj11.append("$and", objArayEnd);
			query.append("$match", obj11);
			// end
			// group
			BasicDBObject obj_total = new BasicDBObject();
			BasicDBObject obj_device = new BasicDBObject();
			obj_device.append("device_id", "$device_id");
			obj_device.append("created_date", "$created_date");
			BasicDBObject obj12 = new BasicDBObject();
			obj_total.append("_id", obj_device);
			//
			// objdistance
			BasicDBObject obj_$max = new BasicDBObject();
			BasicDBObject obj_Distance = new BasicDBObject();
			obj_Distance.append("$distance", 0);
			// ArrayList<BasicDBObject> objArayDistance = new
			// ArrayList<BasicDBObject>();
			BasicDBObject obj_ifnull = new BasicDBObject();
			// objArayDistance.add(obj_Distance);
			obj_ifnull.append("$ifNull", Arrays.asList("$distance", 0));
			obj_$max.append("$max", obj_ifnull);
			obj_total.append("totalDistance", obj_$max);
			// group
			BasicDBObject obj_query2 = new BasicDBObject();
			obj_query2.append("$group", obj_total);

			listQuery.add(query);
			listQuery.add(obj_query2);
			AggregateIterable<org.bson.Document> listDocumen = do_campaigns
					.aggregate(listQuery);
			double result = 0;

			for (org.bson.Document document : listDocumen) {
				String totalDistance = document.get("totalDistance").toString();
				result += Double.parseDouble(totalDistance);
			}
			return FunctionUtil.roundDouble(result / 1000);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
			return 0;
		}

	}

	@Override
	public Boolean insertTrackingMany(ArrayList<org.bson.Document> trackings) {
		// TODO Auto-generated method stub
		Boolean result;
		// mongoClient = MongoUtils.getMongoClient_BM();
		try {
			System.out.print("value connection:" + Global.mongoClient);
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			do_trackings.insertMany(trackings);
			result = true;
		} catch (Exception ex) {
			result = false;
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + ex.getMessage(),
					Global.log_write_carpo);
		}
		// mongoClient.close();
		return result;
	}

	@Override
	public LocationGoogle getEndPointByDeviceId(String devive_id) {
		// TODO Auto-generated method stub
		LocationGoogle locationGoogle = new LocationGoogle();
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_tracking = database
					.getCollection(Global.collection_tracking);

			org.bson.Document query = new org.bson.Document();
			query.append("device_id", devive_id);
			// sort
			BasicDBObject objSort = new BasicDBObject();
			objSort.append("created_date", -1);
			objSort.append("created_time", -1);
			org.bson.Document resultDocumen = do_tracking.find(query)
					.sort(objSort).first();
			double result = 0;
			if (resultDocumen != null) {
				locationGoogle.setLocation_lat(resultDocumen
						.get("location_lat").toString());
				locationGoogle.setLocation_long(resultDocumen.get(
						"location_long").toString());
			}
			return locationGoogle;
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
			return locationGoogle;
		}
	}

	@Override
	public double getCountPointByListDevice_IdAndDistrictNameAndTwoDay(
			ArrayList<String> list_device_car, String districtName,
			String strFromDate, String strToDate) {
		// TODO Auto-generated method stub
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			MongoCollection<org.bson.Document> do_campaigns = database
					.getCollection(Global.collection_tracking);

			List<BasicDBObject> listQuery = new ArrayList<BasicDBObject>();
			BasicDBObject query = new BasicDBObject();
			BasicDBObject obj1 = new BasicDBObject();

			BasicDBObject objIn = new BasicDBObject();
			objIn.append("$in", list_device_car);
			obj1.append("device_id", objIn);
			BasicDBObject objDistrictName = new BasicDBObject();
			objDistrictName.append("district_name", districtName);
			BasicDBObject obj11 = new BasicDBObject();
			ArrayList<BasicDBObject> objArayEnd = new ArrayList<BasicDBObject>();
			// new -> with date
			// with date
			BasicDBObject objDate = new BasicDBObject();
			objDate.append("$gt", strFromDate);
			objDate.append("$lt", strToDate);
			BasicDBObject queryDate = new BasicDBObject();
			queryDate.append("created_date", objDate);
			// end new
			objArayEnd.add(obj1);
			objArayEnd.add(objDistrictName);
			// date
			objArayEnd.add(queryDate);
			// end date
			obj11.append("$and", objArayEnd);
			// obj11.append("$and", objArayEnd);
			query.append("$match", obj11);
			// end
			// objdistance
			BasicDBObject obj_query2 = new BasicDBObject();
			obj_query2.append("$count", "countPoint");

			listQuery.add(query);
			listQuery.add(obj_query2);
			AggregateIterable<org.bson.Document> listDocumen = do_campaigns
					.aggregate(listQuery);
			double result = 0;
			for (org.bson.Document document : listDocumen) {
				String totalDistance = document.get("countPoint").toString();
				result += Double.parseDouble(totalDistance);
			}
			return FunctionUtil.roundDouble(result);
		} catch (Exception e) {
			Date today = Calendar.getInstance().getTime();
			FunctionUtil.writeLogCrawler(GlobalUtils.convertStringToDate(today)
					+ " error db trackingService:" + e.getMessage(),
					Global.log_write_carpo);
			return 0;
		}

	}

	@Override
	public ArrayList<LocationGoogle> getListLocationTrackingByTwoDateAndDevice_idAndOutCty(
			ArrayList<String> list_district_name_cty, String strFromDate,
			String strToDate, String device_id) {
		ArrayList<LocationGoogle> result = new ArrayList<LocationGoogle>();
		FindIterable<org.bson.Document> listDocumen;
		try {
			MongoDatabase database = Global.mongoClient
					.getDatabase(Global.DB_NAME);
			BasicDBObject query = new BasicDBObject();
			BasicDBObject obj = new BasicDBObject();
			obj.append("$gt", strFromDate);
			obj.append("$lt", strToDate);
			query.append("created_date", obj);
			query.append("device_id", device_id);
			// not in
			BasicDBObject objnotIn = new BasicDBObject();
			objnotIn.append("$nin", list_district_name_cty);
			query.append("district_name", objnotIn);

			MongoCollection<org.bson.Document> do_trackings = database
					.getCollection(Global.collection_tracking);
			listDocumen = do_trackings.find(query);
			listDocumen.forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(final org.bson.Document document) {
					LocationGoogle locationGoogle = new LocationGoogle();
					locationGoogle.setLocation_lat(document
							.getString("location_lat"));
					locationGoogle.setLocation_long(document
							.getString("location_long"));
					result.add(locationGoogle);
				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// write log
		}
		return result;
	}

}
