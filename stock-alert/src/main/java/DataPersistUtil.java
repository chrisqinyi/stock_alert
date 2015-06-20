import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import qinyi.IStockBean;
import redis.clients.jedis.Jedis;

public class DataPersistUtil {
	private static final String P_OYL_AAS_G_IN_G_PP_FK1_TKJ_LD0_MR_H6AA1C_IO = "POylAasGInGPpFK1TkjLD0MrH6AA1cIo";
	private static final String QT_ZQM_UVXDM_HPT_GV_XWLM0F_ID9 = "QtZqmUvxdmHptGVXwlm0fId9";
	private static final String CMYIRCUA_FS_JN_MQBV_U_URK = "cmyircuaFsJNMqbvUUrk";
	private static final String TEST_MONGO = "test_mongo";

	// public static List<String> getData() throws IOException{
	// List <String> result= new ArrayList<String>();
	// System.out.println(new File("Test.txt").getAbsolutePath());
	// BufferedReader br = new BufferedReader(new FileReader("Test.txt"));
	// String data = br.readLine();//一次读入一行，直到读入null为文件结束
	// while( data!=null){
	// result.add(data);
	// data = br.readLine(); //接着读下一行
	// }
	// br.close();
	// return result;
	// }
	// public static void putData(Collection<String> args) throws IOException {
	// FileWriter fw = new FileWriter("Test.txt");
	// for(String s:args){
	// fw.write(s,0,s.length());
	// fw.write("\n");
	// }
	// fw.flush();
	// fw.close();
	// }

	// public static Collection<String> getData(String key) throws IOException {
	// Collection<String> result = null;
	// Jedis jedis = getJedis();
	// // 删除所有redis数据库中的key-value
	// // jedis.flushDB();
	// // 简单的key-value设置
	// // jedis.set("name", "bae");
	// // System.out.println("default | " + jedis.get("default"));
	// if(jedis.exists(key)){
	// result = jedis.smembers(key);
	// }
	// closeJedis(jedis);
	// return result;
	// }

	public static Collection<String> getData(String key) throws IOException {
		Collection<String> result = null;
		MongoClient mongoClient = MongoDBUtils.getMongoDB();
		DB mongoDB = mongoClient.getDB(CMYIRCUA_FS_JN_MQBV_U_URK);
		mongoDB.authenticate(QT_ZQM_UVXDM_HPT_GV_XWLM0F_ID9,
				P_OYL_AAS_G_IN_G_PP_FK1_TKJ_LD0_MR_H6AA1C_IO.toCharArray());
		DBCollection mongoCollection = mongoDB.getCollection(TEST_MONGO);
		DBObject data1 = new BasicDBObject();
		data1.put("_id", key);
		data1 = mongoCollection.findOne(data1);
		result = (Collection<String>) data1.get("content");
		mongoClient.close();
		return result;
	}

	public static Jedis getJedis() {
		/***** 1. 填写数据库相关信息(请查找数据库详情页) *****/
		String databaseName = "XFgzJUCCKRwZtQkmkDwe";
		String host = "redis.duapp.com";
		String portStr = "80";
		int port = Integer.parseInt(portStr);
		String username = QT_ZQM_UVXDM_HPT_GV_XWLM0F_ID9;// 用户名(api key);
		String password = P_OYL_AAS_G_IN_G_PP_FK1_TKJ_LD0_MR_H6AA1C_IO;// 密码(secret
																		// key)

		/****** 2. 接着连接并选择数据库名为databaseName的服务器 ******/
		Jedis jedis = new Jedis(host, port);
		jedis.connect();
		jedis.auth(username + "-" + password + "-" + databaseName);
		/* 至此连接已完全建立，就可对当前数据库进行相应的操作了 */
		return jedis;
	}

	// public static Jedis getJedis() {
	// Jedis jedis = JedisUtil.getJedis("redis.duapp.com", 80);
	// jedis.connect();
	// jedis.auth("QtZqmUvxdmHptGVXwlm0fId9" + "-"
	// + "POylAasGInGPpFK1TkjLD0MrH6AA1cIo" + "-"
	// + "XFgzJUCCKRwZtQkmkDwe");
	// return jedis;
	// }

	public static void beforeProcess(IStockBean stockBean) throws IOException {
		Collection<String> stockCodes = getData("stockCodes");
		System.out.println("stockCodes before:" + stockCodes);
		if (null != stockCodes) {
			stockBean.setStockCodes(stockCodes);
		}
		Collection<String> stockCodesNew = getData("stockCodesNew");
		System.out.println("stockCodesNew before:" + stockCodesNew);
		if (null != stockCodesNew) {
			stockBean.setStockCodesNew(stockCodesNew);
		}
		Collection<String> stockCodesSell = getData("stockCodesSell");
		System.out.println("stockCodesSell before:" + stockCodesSell);
		if (null != stockCodesSell) {
			stockBean.setStockCodesSell(stockCodesSell);
		}
	}

	public static void afterProcess(IStockBean stockBean) throws IOException {
		Collection<String> stockCodes = stockBean.getStockCodes();
		putData("stockCodes", stockCodes);
		System.out.println("stockCodes after:" + getData("stockCodes"));
		Collection<String> stockCodesNew = stockBean.getStockCodesNew();
		putData("stockCodesNew", stockCodesNew);
		System.out.println("stockCodesNew after:" + getData("stockCodesNew"));
		Collection<String> stockCodesSell = stockBean.getStockCodesSell();
		putData("stockCodesSell", stockCodesSell);
		System.out.println("stockCodesSell after:" + getData("stockCodesSell"));
	}

	public static void closeJedis(Jedis jedis) {
		// JedisUtil.closeJedis(jedis, "redis.duapp.com", 80);
		jedis.close();
		// jedis.disconnect()
	}

	public static void addData(String key, Collection<String> args)
			throws IOException {
		Jedis jedis = getJedis();
		// 删除所有redis数据库中的key-value
		// jedis.flushDB();
		// 简单的key-value设置
		for (String code : args) {
			jedis.sadd(key, code);
		}
		closeJedis(jedis);
	}

	// public static void putData(String key, Collection<String> args)
	// throws IOException {
	// Jedis jedis = getJedis();
	// // 删除所有redis数据库中的key-value
	// // jedis.flushDB();
	// if(jedis.exists(key)){
	// jedis.del(key);
	// }
	// // 简单的key-value设置
	// for (String code : args) {
	// jedis.sadd(key, code);
	// }
	// closeJedis(jedis);
	// }

	public static void putData(String key, Collection<String> args)
			throws IOException {
		MongoClient mongoClient = MongoDBUtils.getMongoDB();
		DB mongoDB = mongoClient.getDB(CMYIRCUA_FS_JN_MQBV_U_URK);
		mongoDB.authenticate(QT_ZQM_UVXDM_HPT_GV_XWLM0F_ID9,
				P_OYL_AAS_G_IN_G_PP_FK1_TKJ_LD0_MR_H6AA1C_IO.toCharArray());
		DBCollection mongoCollection = mongoDB.getCollection(TEST_MONGO);
		DBObject data1 = new BasicDBObject();
		data1.put("_id", key);
		DBObject data2 = new BasicDBObject();
		data2.put("content", args);
		mongoCollection.update(data1, data2);
		mongoClient.close();
	}

	public static void main(String[] args) throws IOException {
		// ArrayList args2 = new ArrayList();
		// args2.add("asdasdad");
		// args2.add("asdasdad");
		// args2.add("asdasdad");
		// putData(args2);
		//
		System.out.println(getData("STOCK_CODES"));
		System.out.println(new File("").getAbsolutePath());
		System.out.println("ghnghnhgn");

	}

}
