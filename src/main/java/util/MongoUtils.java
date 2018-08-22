package util;

import java.net.UnknownHostException;
import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import model.Global;;

public class MongoUtils {
	 public static MongoClient getMongoClient_1() throws UnknownHostException {
	      MongoClient mongoClient = new MongoClient(Global.HOST, Global.PORT);
	      return mongoClient;
	  }
	 public static MongoClient getMongoClient_BM() throws UnknownHostException {
//	      MongoCredential credential = MongoCredential.createScramSha1Credential(
//	    		  Global.USERNAME, "admin", Global.PASSWORD.toCharArray());
//	      MongoClient mongoClient = new MongoClient(
//	              new ServerAddress(Global.HOST, Global.PORT), Arrays.asList(credential));
//	      
	      // test
	      MongoClient mongoClient = new MongoClient(
	    		  new MongoClientURI(Global.url_connect_db)
	    		);
	      
	      return mongoClient;
	  }
}
