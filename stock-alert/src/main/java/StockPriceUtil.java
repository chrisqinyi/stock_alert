import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class StockPriceUtil {
	public static Map<String, BigDecimal> getStockPrices(String[] codes) {
		// 五分钟
		// http://183.136.160.2/EM_HTML5/quote.aspx?id=6001691&type=HM5&jsname=em_data_Minute5_1404558145282
		// "201407041125,3.06,3.03,3.06,3.03,3.05,1416042"
		// 日K
		// http://183.136.160.2/EM_HTML5/quote.aspx?id=6001691&type=HD&jsname=em_data_DailyK_1404559419762
		// "20140704,2.96,3.03,3.17,2.95,2.88,120390691"
		//指数
		//"http://183.136.160.2/EM_HTML5/quote.aspx?id=0000011&type=HD&jsname=em_data_DailyK_1404722304114"
		//"20140707,2058.13,2059.93,2064.04,2050.89,2059.38,97168754"
		HashMap<String, BigDecimal> result = new HashMap<String, BigDecimal>();
		String join = StringUtils.join(codes, ',');
		String str = HttpXmlClient.get("http://hq.sinajs.cn/list=" + join);
		// String str =
		// HttpXmlClient.get("http://m.baidu.com/open/stock?lcid=stock&query=600169&srcid=3123&provider=%E8%AF%81%E5%88%B8%E4%B9%8B%E6%98%9F&callback=window.A.getData2057"
		// + join);
		System.out.println(str);
		Pattern pattern = Pattern
				.compile("var hq_str_((sh|sz)\\d{6})=\"\\S*?,\\d{1,2}.\\d{2},(\\d{1,2}.\\d{2}),(\\d{1,2}.\\d{2})");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			result.put(matcher.group(1),
					BigDecimal.valueOf(Double.valueOf(matcher.group(4))));
		}
		return result;
	}

	public static BigDecimal getStockPrice(String code) {
		BigDecimal result = BigDecimal.ZERO;
		// HashMap<String, BigDecimal> result = new HashMap<String,
		// BigDecimal>();
		// String join = StringUtils.join(codes, ',');
		// String str = HttpXmlClient.get("http://hq.sinajs.cn/list=" + join);
		String str;
		Pattern pattern = Pattern.compile("\"base_info\":\"1," + code
				+ ",\\S*?,\\d{1,2}.\\d{2},\\d{1,2}.\\d{2},(\\d{1,2}.\\d{2}),");
		Matcher matcher;
		do {
			str = HttpXmlClient
					.get("http://datainterface.eastmoney.com/EM_DataCenterMobile/JS.aspx?type=MD&sty=MD&code="
							+ code + "1");
			System.out.println(str);
			matcher = pattern.matcher(str);
		} while (!matcher.find());
		result = BigDecimal.valueOf(Double.valueOf(matcher.group(1)));
		return result;
	}

	public static List<BigDecimal> getStock5MinutesPrice(String code) {
		List<BigDecimal> result = new LinkedList<BigDecimal>();
		// HashMap<String, BigDecimal> result = new HashMap<String,
		// BigDecimal>();
		// String join = StringUtils.join(codes, ',');
		// String str = HttpXmlClient.get("http://hq.sinajs.cn/list=" + join);
		String str = "";
		Pattern pattern = Pattern
				.compile("\"(\\d{8}\\d{4}),\\d{1,2}.\\d{2},(\\d{1,2}.\\d{2}),\\d{1,2}.\\d{2},\\d{1,2}.\\d{2},\\d{1,2}.\\d{2},\\d+?\"");
		Matcher matcher;
		java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMdd");
        String s = format2.format(new Date());
		do {
			str = HttpXmlClient
					.get("http://183.136.160.2/EM_HTML5/quote.aspx?id="+code+"&type=HM5&jsname=em_data_Minute5_1404558145282");
		} while (str.isEmpty());
		System.out.println(str);
		matcher = pattern.matcher(str);
		while (str.contains(s) && matcher.find()) {
			result.add(BigDecimal.valueOf(Double.valueOf(matcher.group(2))));
		}
		return result;
	}

	public static List<BigDecimal> getStockDailyPrice(String code) {
		List<BigDecimal> result = new LinkedList<BigDecimal>();
		// HashMap<String, BigDecimal> result = new HashMap<String,
		// BigDecimal>();
		// String join = StringUtils.join(codes, ',');
		// String str = HttpXmlClient.get("http://hq.sinajs.cn/list=" + join);
		String str = "";
		Pattern pattern = Pattern
				.compile("\"\\d{8},\\d{1,5}.\\d{2},(\\d{1,5}.\\d{2}),\\d{1,5}.\\d{2},\\d{1,5}.\\d{2},\\d{1,5}.\\d{2},\\d+?\"");
		Matcher matcher;
		do {
			str = HttpXmlClient
					.get("http://183.136.160.2/EM_HTML5/quote.aspx?id="+code+"&type=HD&jsname=em_data_DailyK_1404559419762");
			System.out.println(str);
			matcher = pattern.matcher(str);
		} while (str.isEmpty());

		while (matcher.find()) {
			result.add(BigDecimal.valueOf(Double.valueOf(matcher.group(1))));
		}
		return result;
	}

	public static String getStockSuggestion(String code) {
		List<BigDecimal> result = new LinkedList<BigDecimal>();
		// HashMap<String, BigDecimal> result = new HashMap<String,
		// BigDecimal>();
		// String join = StringUtils.join(codes, ',');
		// String str = HttpXmlClient.get("http://hq.sinajs.cn/list=" + join);
		String str = "";
		Pattern pattern = Pattern
				.compile(".*\"(buy|sell)\".*");
		Matcher matcher;
		do {
			str = HttpXmlClient
					.get("http://www.iwencai.com/stockpick/search?preParams=&ts=1&f=1&qs=1&selfsectsn=&querytype=&searchfilter=&tid=stockpick&w="+code.substring(0,6));
			System.out.println(str);
			matcher = pattern.matcher(str);
		} while (str.isEmpty());

		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}
	
	public static void main(String[] args) {
		// System.out
		// .println(getStockPrices(new String[] { "sh601006", "sz000518" }));
		// System.out
		// .println(getStockPrice("601006"));
//		System.out.println(getStock5MinutesPrice("601006"));
//		System.out.println(getStockDailyPrice("601006"));
		
//		java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMdd");
//        String s = format2.format(new Date());
//        System.out.println(s);
		//System.out.println(getStockDailyPrice("000001"));
		System.out.println(getStockSuggestion("6011771"));
	}
}
