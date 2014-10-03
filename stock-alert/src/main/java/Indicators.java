import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Indicators {
	/**
	 * Calculate EMA,
	 * 
	 * @param list
	 *            :Price list to calculate，the first at head, the last at tail.
	 * @return
	 */
	public static final Double getEXPMA(final List<BigDecimal> list, final int number) {
		// 开始计算EMA值，
		Double k = 2.0 / (number + 1.0);// 计算出序数
		Double ema = list.get(0).doubleValue();// 第一天ema等于当天收盘价
		for (int i = 1; i < list.size(); i++) {
			// 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
			ema = list.get(i).doubleValue() * k + ema * (1 - k);
		}
		return ema;
	}

	/**
	 * calculate MACD values
	 * 
	 * @param list
	 *            :Price list to calculate，the first at head, the last at tail.
	 * @param shortPeriod
	 *            :the short period value.
	 * @param longPeriod
	 *            :the long period value.
	 * @param midPeriod
	 *            :the mid period value.
	 * @return
	 */
	public static final HashMap<String, Double> getMACD(final List<BigDecimal> list, final int shortPeriod, final int longPeriod, int midPeriod) {
		HashMap<String, Double> macdData = new HashMap<String, Double>();
		List<BigDecimal> diffList = new ArrayList<BigDecimal>();
		Double shortEMA = 0.0;
		Double longEMA = 0.0;
		Double dif = 0.0;
		Double dea = 0.0;

		for (int i = list.size() - 1; i >= 0; i--) {
			List<BigDecimal> sublist = list.subList(0, list.size() - i);
			shortEMA = Indicators.getEXPMA(sublist, shortPeriod);
			longEMA = Indicators.getEXPMA(sublist, longPeriod);
			dif = shortEMA - longEMA;
			diffList.add(BigDecimal.valueOf(dif));
		}
		dea = Indicators.getEXPMA(diffList, midPeriod);
		macdData.put("DIF", dif);
		macdData.put("DEA", dea);
		macdData.put("MACD", (dif - dea) * 2);
		return macdData;
	}
	
	public static void main(String [] args){
		List<BigDecimal> stock5MinutesPrice1 = StockPriceUtil.getStock5MinutesPrice("6000831");
		System.out.println(getMACD(stock5MinutesPrice1,12,26,9));
		List<BigDecimal> stock5MinutesPrice2 = StockPriceUtil.getStock5MinutesPrice("6011771");
		System.out.println(getMACD(stock5MinutesPrice2,12,26,9));
		List<BigDecimal> stock5MinutesPrice3 = StockPriceUtil.getStock5MinutesPrice("6033661");
		System.out.println(getMACD(stock5MinutesPrice3,12,26,9));
	}
}
