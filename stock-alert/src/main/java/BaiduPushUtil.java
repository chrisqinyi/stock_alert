import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.http.protocol.HTTP;

import net.sf.json.JSONObject;

/**
 * 
 * @author PSVM
 * 
 */
public class BaiduPushUtil {

	/**
	 * MD5����
	 * 
	 * @param s
	 * @return
	 */
	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Map����
	 * 
	 * @param unsort_map
	 * @return
	 */

	private static SortedMap<String, String> mapSortByKey(
			Map<String, String> unsort_map) {
		TreeMap<String, String> result = new TreeMap<String, String>();
		Object[] unsort_key = unsort_map.keySet().toArray();
		Arrays.sort(unsort_key);
		for (int i = 0; i < unsort_key.length; i++) {
			result.put(unsort_key[i].toString(), unsort_map.get(unsort_key[i]));
		}
		return result.tailMap(result.firstKey());
	}

	public static String getMessage(String title, String description) {
		PushMessage pushMessage = new PushMessage();
		pushMessage.setTitle(title);
		pushMessage.setDescription(description);
		String messages = JSONObject.fromObject(pushMessage).toString();
		return messages;
	}

	/**
	 * ��ȡǩ��
	 * 
	 * @param url
	 * @param parameters
	 * @param secret
	 * @return
	 */
	public static String getSignature(String url, Map<String, String> parameters,
			String secret) {
		// �Ƚ����������������ֵ��������������
		Map<String, String> sortedParams = new HashMap<String, String>(
				parameters);
		sortedParams = mapSortByKey(sortedParams);
		// �����������ֵ䣬�����в���"key=value"��ʽƴ����һ��
		StringBuilder baseString = new StringBuilder();
		baseString.append("POST");
		baseString.append(url);
		for (String key : sortedParams.keySet()) {
			if (null != key && !"".equals(key)) {
				baseString.append(key).append("=")
						.append(sortedParams.get(key));
			}
			sortedParams.get(key);
		}
		baseString.append(secret);
		String encodeString = null;
		try {
			encodeString = URLEncoder.encode(baseString.toString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sign = MD5(encodeString);
		return sign;
	}

	public String push_msg(String user_id, String title, String description) {
		return this.push_msg(user_id, null, title, description);
	}

	/**
	 * 
	 * @param user_id
	 * @param title
	 * @param description
	 * @return
	 */
	public String push_msg(String user_id, String channel_id, String title,
			String description) {
		String url = "http://channel.api.duapp.com/rest/2.0/channel/channel";
		// ������
		String method = "push_msg";

		// ��������
		String apikey = "QtZqmUvxdmHptGVXwlm0fId9";
		// �ܳ�
		String secret = "POylAasGInGPpFK1TkjLD0MrH6AA1cIo";
		// �������ͣ�ȡֵ��ΧΪ��1��3
		// 1�������ˣ�����ָ��user_id �� channel_id
		// ��ָ���û���ָ���豸������user_id��ָ���û��������豸��
		// 2��һȺ�ˣ�����ָ�� tag
		// 3�������ˣ�����ָ��tag��user_id��channel_id
		int push_type = 1;
		// �豸����
		// 1��������豸��
		// 2��PC�豸��
		// 3��Andriod�豸��
		// 4��iOS�豸��
		// 5��Windows Phone�豸��
		int device_type = 3;
		// ��Ϣ����
		// 0����Ϣ��͸����Ӧ�õ���Ϣ�壩
		// 1��֪ͨ����Ӧ�豸�ϵ���Ϣ֪ͨ��
		// Ĭ��ֵΪ0��
		int message_type = 1;
		// ������Ϣ
		String messages = getMessage(title, description);
		// ��Ϣ��ʶ��
		// ָ����Ϣ��ʶ�������messagesһһ��Ӧ����ͬ��Ϣ��ʶ����Ϣ���Զ����ǡ�
		String msg_keys = UUID.randomUUID().toString();
		// �û���������ʱ��Unixʱ�������������ǩ�����Чʱ��Ϊ��ʱ���+10���ӡ�
		String timestamp = Long.toString(new Date().getTime());

		Map<String, String> parameters = new TreeMap<String, String>();
		parameters.put("method", method);
		parameters.put("apikey", apikey);
		parameters.put("user_id", user_id);
		if (null != channel_id) {
			parameters.put("channel_id", channel_id);
		}
		parameters.put("push_type", push_type + "");
		parameters.put("device_type", device_type + "");
		parameters.put("message_type", message_type + "");
		parameters.put("messages", messages);
		parameters.put("msg_keys", msg_keys);
		parameters.put("timestamp", timestamp + "");
		String sign = getSignature(url, parameters, secret);
		parameters.put("sign", sign);
		String responseStr = HttpXmlClient.post(url, parameters);
		return responseStr;
	}

	public static void push_mailToAll(String title, String description)
			throws GeneralSecurityException {
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.sina.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("chris_qin_yi@sina.com");
		mailInfo.setPassword("5C6E626762393B3A3F");// 您的邮箱密码
		mailInfo.setFromAddress("chris_qin_yi@sina.com");
		mailInfo.setToAddress("qinyichris@icloud.com");
		mailInfo.setSubject(title);
		mailInfo.setContent(description);
		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendTextMail(mailInfo);// 发送文体格式
		//sms.sendHtmlMail(mailInfo);// 发送html格式
	}

	public static String push_msgToAll(String title, String description) {
		String url = "http://channel.api.duapp.com/rest/2.0/channel/channel";
		// ������
		String method = "push_msg";
		// ��������
		String apikey = "QtZqmUvxdmHptGVXwlm0fId9";
		// �ܳ�
		String secret = "POylAasGInGPpFK1TkjLD0MrH6AA1cIo";
		// �������ͣ�ȡֵ��ΧΪ��1��3
		// 1�������ˣ�����ָ��user_id �� channel_id
		// ��ָ���û���ָ���豸������user_id��ָ���û��������豸��
		// 2��һȺ�ˣ�����ָ�� tag
		// 3�������ˣ�����ָ��tag��user_id��channel_id
		int push_type = 3;

		// �豸����
		// 1��������豸��
		// 2��PC�豸��
		// 3��Android�豸��
		// 4��iOS�豸��
		// 5��Windows Phone�豸��
		int device_type = 3;

		// ��Ϣ����
		// 0����Ϣ��͸����Ӧ�õ���Ϣ�壩
		// 1��֪ͨ����Ӧ�豸�ϵ���Ϣ֪ͨ��
		// Ĭ��ֵΪ0��
		int message_type = 1;

		// ������Ϣ
		String messages = getMessage(title, description);

		// ��Ϣ��ʶ��
		// ָ����Ϣ��ʶ�������messagesһһ��Ӧ����ͬ��Ϣ��ʶ����Ϣ���Զ����ǡ�
		String msg_keys = UUID.randomUUID().toString();

		// �û���������ʱ��Unixʱ�������������ǩ�����Чʱ��Ϊ��ʱ���+10���ӡ�
		String timestamp = Long.toString(new Date().getTime());

		Map<String, String> parameters = new TreeMap<String, String>();
		parameters.put("method", method);
		parameters.put("apikey", apikey.toString());
		parameters.put("push_type", push_type + "");
		parameters.put("device_type", device_type + "");
		parameters.put("message_type", message_type + "");
		parameters.put("messages", messages.toString());
		parameters.put("msg_keys", msg_keys.toString());
		parameters.put("timestamp", timestamp + "");
		String sign = getSignature(url, parameters, secret);
		parameters.put("sign", sign.toString());
		String responseStr = HttpXmlClient.post(url, parameters);
		return responseStr;

	}

	public static void main(String[] args) throws GeneralSecurityException {
		// System.out.println(BaiduPushUtil.push_msg("990593941931946992",
		// "4391131411952560641", new Date().toString(), "bbb"));
		// System.out.println(new BaiduPushUtil().push_msg("818670655850242201",
		// "4222129398189191299", "���ͳɹ�", "���ͳɹ�"));
		new BaiduPushUtil().push_mailToAll("qinyi ",
				"中文终于可以用了");
	}
}