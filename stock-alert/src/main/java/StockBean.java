import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qinyi.IStockBean;

public class StockBean implements IStockBean {
	static Map<String, Queue> stockPriceMinutelyMap = new HashMap<String, Queue>();
	// static Map<String, Queue> stockPrice5MinutelyMap = new HashMap<String,
	// Queue>();
	static Map<String, Queue> stockPriceDailyMap = new HashMap<String, Queue>();
	private static final String[] STOCK_CODES = new String[] { "6011771","6003861","3003382","0025572","0024272","0022582" };
	static {
		for (String code : STOCK_CODES) {
			stockPriceMinutelyMap.put(code, new Queue(30));
			// stockPrice5MinutelyMap.put(code, new Queue(31));
			// stockPriceDailyMap.put(code, new Queue(31));
		}
	}

	/* (non-Javadoc)
	 * @see IStockBean#processMinutely()
	 */
	public void processMinutely() throws GeneralSecurityException {
		System.out.println("before process");
		// Map<String, BigDecimal> tmp = StockPriceUtil
		// .getStockPrices(STOCK_CODES);
		for (String code : STOCK_CODES) {
			// System.out.println("processing "+key);
			Queue queue = stockPriceMinutelyMap.get(code);
			StockPriceVO oldStockPriceVO1 = new StockPriceVO(1, queue);
			StockPriceVO oldStockPriceVO5 = new StockPriceVO(5, queue);
			queue.put(StockPriceUtil.getStockPrice(code));
			System.out.println("queue updated: " + queue);
			StockPriceVO newStockPriceVO1 = new StockPriceVO(1, queue);
			StockPriceVO newStockPriceVO5 = new StockPriceVO(5, queue);
			BigDecimal newShortPrice = newStockPriceVO1.getShortPrice();
			BigDecimal newMidPrice = newStockPriceVO1.getMidPrice();
			BigDecimal oldShortPrice = oldStockPriceVO1.getShortPrice();
			BigDecimal oldMidPrice = oldStockPriceVO1.getMidPrice();
			System.out.println("newShortPrice:" + newShortPrice);
			System.out.println("newMidPrice:" + newMidPrice);
			System.out.println("oldShortPrice:" + oldShortPrice);
			System.out.println("oldMidPrice:" + oldMidPrice);
			if (newShortPrice.subtract(newMidPrice)
					.multiply(oldShortPrice.subtract(oldMidPrice))
					.doubleValue() <= 0) {
				BigDecimal newLongPrice = newStockPriceVO1.getLongPrice();
				if (newShortPrice.doubleValue() - newMidPrice.doubleValue() > oldShortPrice
						.doubleValue() - oldMidPrice.doubleValue()) {
					// 股票上升
					new BaiduPushUtil().push_mailToAll(code + ":上升信号", code
							+ ":上升信号");
					System.out.println("上升信号 sent!");
				}

				if (newShortPrice.doubleValue() - newMidPrice.doubleValue() < oldShortPrice
						.doubleValue() - oldMidPrice.doubleValue()) {
					// 股票下跌
					new BaiduPushUtil().push_mailToAll(code + ":下跌信号", code
							+ ":下跌信号");
					System.out.println("下跌信号 sent!");
				}
			}

		}
		System.out.println("after process");
	}

	/* (non-Javadoc)
	 * @see IStockBean#process5Minutesly()
	 */
	public void process5Minutesly() throws GeneralSecurityException {
		System.out.println("before process");
		// Map<String, BigDecimal> tmp = StockPriceUtil
		// .getStockPrices(STOCK_CODES);
		for (String code : STOCK_CODES) {
			// System.out.println("processing "+key);
			List<BigDecimal> stock5MinutesPrice = StockPriceUtil
					.getStock5MinutesPrice(code);
			if (stock5MinutesPrice.isEmpty()) {
				return;
			}
			BigDecimal dLastestPrice = stock5MinutesPrice.get(stock5MinutesPrice.size() - 1);
			Double dMacd = Indicators.getMACD(
					stock5MinutesPrice, 12, 26, 9).get("MACD");
			stock5MinutesPrice.remove(stock5MinutesPrice.size()-1);
			Double dOldMacd=Indicators.getMACD(
					stock5MinutesPrice, 12, 26, 9).get("MACD");
			
			if (dMacd >= -0.005 && dMacd <= 0.005 && (dOldMacd>0.005 || dOldMacd<-0.005)) {
				java.text.DateFormat format2 = new java.text.SimpleDateFormat(
						"yyyyMMdd HH:mm");
				String s = format2.format(new Date());
				if (dMacd > dOldMacd) {
					new BaiduPushUtil().push_mailToAll(s + " " + code, "5m price:"+dLastestPrice+";gold cross");
					System.out.println(s + " " + code + ":5 minutes MACD gold cross signal sent!");
				} else {
					new BaiduPushUtil().push_mailToAll(s + " " + code, "5m price:"+dLastestPrice+";dead cross");
					System.out.println(s + " " + code + ":5 minutes MACD dead cross signal sent!");
				}

			}
			/*
			 * Queue queue = new Queue(31);
			 * queue.put(stock5MinutesPrice.toArray()); StockPriceVO
			 * newStockPriceVO = new StockPriceVO(1, queue); // StockPriceVO
			 * newStockPriceVO5 = new StockPriceVO(5, queue);
			 * queue.removeLast(); System.out.println("queue updated: " +
			 * queue); StockPriceVO oldStockPriceVO = new StockPriceVO(1,
			 * queue); // StockPriceVO oldStockPriceVO5 = new StockPriceVO(5,
			 * queue); // queue.put(StockPriceUtil.getStockPrice(code));
			 * java.text.DateFormat format2 = new java.text.SimpleDateFormat(
			 * "yyyyMMdd HH:mm"); String s = format2.format(new Date()); if
			 * (detectChange(newStockPriceVO, oldStockPriceVO)) { new
			 * BaiduPushUtil().push_msgToAll(s + " " + code + ":5分钟线交叉", s + " "
			 * + code + ":5分钟线交叉"); System.out.println(s + " " + code +
			 * ":5分钟交叉信号  sent!"); }
			 */

		}
		System.out.println("after process");
	}

	private boolean detectChange(StockPriceVO newStockPriceVO,
			StockPriceVO oldStockPriceVO) {
		Double newShortPrice = newStockPriceVO.getShortPrice().doubleValue();
		Double newMidPrice = newStockPriceVO.getMidPrice().doubleValue();
		Double newLongPrice = newStockPriceVO.getLongPrice().doubleValue();
		Double oldShortPrice = oldStockPriceVO.getShortPrice().doubleValue();
		Double oldMidPrice = oldStockPriceVO.getMidPrice().doubleValue();
		Double oldLongPrice = oldStockPriceVO.getLongPrice().doubleValue();
		System.out.println("newShortPrice:" + newShortPrice);
		System.out.println("newMidPrice:" + newMidPrice);
		System.out.println("newLongPrice:" + newLongPrice);
		System.out.println("oldShortPrice:" + oldShortPrice);
		System.out.println("oldMidPrice:" + oldMidPrice);
		System.out.println("oldLongPrice:" + oldLongPrice);
		return detectChange(oldShortPrice, oldMidPrice, newShortPrice,
				newMidPrice)
				|| detectChange(oldLongPrice, oldMidPrice, newLongPrice,
						newMidPrice)
				|| detectChange(oldShortPrice, oldLongPrice, newShortPrice,
						newLongPrice);
	}

	private boolean detectChange(Double oldPrice1, Double oldPrice2,
			Double newPrice1, Double newPrice2) {
		if (newPrice1 - oldPrice1 == newPrice2 - oldPrice2) {
			return false;
		}
		if (newPrice1 - newPrice2 == oldPrice1 - oldPrice2) {
			return false;
		}
		if ((newPrice1 - newPrice2) * (oldPrice1 - oldPrice2) <= 0) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see IStockBean#processDaily()
	 */
	public void processDaily() throws GeneralSecurityException {
		System.out.println("before process");
		// Map<String, BigDecimal> tmp = StockPriceUtil
		// .getStockPrices(STOCK_CODES);
		List<String> stockCodes = new ArrayList<String>(
				Arrays.asList(STOCK_CODES));
		stockCodes.add("0000011");
		stockCodes.add("3990012");
		for (String code : stockCodes) {
			// System.out.println("processing "+key);
			List<BigDecimal> stockDailyPrice = StockPriceUtil
					.getStockDailyPrice(code);
			BigDecimal dLastestPrice = stockDailyPrice.get(stockDailyPrice.size() - 1);
			Double dMacd = Indicators.getMACD(stockDailyPrice,
					12, 26, 9).get("MACD");
			stockDailyPrice.remove(stockDailyPrice.size()-1);
			Double dOldMacd=Indicators.getMACD(
					stockDailyPrice, 12, 26, 9).get("MACD");
			if (dMacd >= -0.005 && dMacd <= 0.005 && (dOldMacd>0.005 || dOldMacd<-0.005)) {
				java.text.DateFormat format2 = new java.text.SimpleDateFormat(
						"yyyyMMdd HH:mm");
				String s = format2.format(new Date());
				if (dMacd > dOldMacd) {
					new BaiduPushUtil().push_mailToAll(s + " " + code, "daily price:"+dLastestPrice+";gold cross");
					System.out.println(s + " " + code + ":daily MACD gold cross signal sent!");
				} else {
					new BaiduPushUtil().push_mailToAll(s + " " + code, "daily price:"+dLastestPrice+";dead cross");
					System.out.println(s + " " + code + ":daily MACD dead cross signal sent!");
				}

			}
			String stockSuggestion = StockPriceUtil.getStockSuggestion(code);
			if(!"".equals(stockSuggestion)){
				java.text.DateFormat format2 = new java.text.SimpleDateFormat(
						"yyyyMMdd HH:mm");
				String s = format2.format(new Date());
				new BaiduPushUtil().push_mailToAll(s + " " + code, "suggestion:"+stockSuggestion);
				System.out.println(s + " " + code + ":suggestion signal sent!");
			}
			/*
			 * Queue queue = new Queue(31);
			 * queue.put(stockDailyPrice.toArray()); StockPriceVO
			 * newStockPriceVO = new StockPriceVO(1, queue); // StockPriceVO
			 * newStockPriceVO5 = new StockPriceVO(5, queue);
			 * queue.removeLast(); System.out.println("queue updated: " +
			 * queue); StockPriceVO oldStockPriceVO = new StockPriceVO(1,
			 * queue); // StockPriceVO oldStockPriceVO5 = new StockPriceVO(5,
			 * queue); // queue.put(StockPriceUtil.getStockPrice(code));
			 * java.text.DateFormat format2 = new java.text.SimpleDateFormat(
			 * "yyyyMMdd HH:mm"); String s = format2.format(new Date()); if
			 * (detectChange(newStockPriceVO, oldStockPriceVO)) { new
			 * BaiduPushUtil().push_msgToAll(s + " " + code + ":日线交叉", s + " " +
			 * code + ":日线交叉"); System.out.println(s + " " + code +
			 * ":日线交叉信号  sent!"); }
			 */

		}
		System.out.println("after process");
	}

	public static void main(String[] args) throws GeneralSecurityException {
		// new StockBean().processMinutely();
		//new StockBean().process5Minutesly();
		new StockBean().processDaily();
		// new StockBean().processDailyIndex();
	}

	public Collection<String> getStockCodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStockCodes(Collection<String> stockCodes) {
		// TODO Auto-generated method stub
		
	}

	public Collection<String> getStockCodesSell() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStockCodesSell(Collection<String> stockCodesSell) {
		// TODO Auto-generated method stub
		
	}

	public Collection<String> getStockCodesNew() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStockCodesNew(Collection<String> stockCodesNew) {
		// TODO Auto-generated method stub
		
	}
}
