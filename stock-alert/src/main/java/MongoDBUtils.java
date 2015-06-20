import java.io.IOException;
import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoDBUtils {

	public static MongoClient getMongoDB() throws IOException {

		/***** 1. 填写数据库相关信息(请查找数据库详情页) *****/
		String databaseName = "cmyircuaFsJNMqbvUUrk";
		String host = "mongo.duapp.com";
		String port = "8908";
		String username = "QtZqmUvxdmHptGVXwlm0fId9";// 用户名(api key);
		String password = "POylAasGInGPpFK1TkjLD0MrH6AA1cIo";// 密码(secret key)
		String serverName = host + ":" + port;

		/****** 2. 接着连接并选择数据库名为databaseName的服务器 ******/
		MongoClient mongoClient = new MongoClient(
				new ServerAddress(serverName), Arrays.asList(MongoCredential
						.createMongoCRCredential(username, databaseName,
								password.toCharArray())),
				new MongoClientOptions.Builder().cursorFinalizerEnabled(false)
						.build());
		return mongoClient;

	}

}