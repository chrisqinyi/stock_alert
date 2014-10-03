import java.math.BigDecimal;

public class StockPriceVO {
	public static final Integer FIVE_MINUTES = 5;
	public static final Integer ONE_MINUTE = 1;
	public static final Integer TWO_MINUTE2 = 2;
	private static final Integer SHORT_INTERVAL = 5;
	private static final Integer MID_INTERVAL = 10;
	private static final Integer LONG_INTERVAL = 30;
	private BigDecimal shortPrice;
	private BigDecimal midPrice;
	private BigDecimal longPrice;
	public StockPriceVO(Integer intervalUnit,Queue queue){
		this.shortPrice=queue.getAveragePrice(intervalUnit*SHORT_INTERVAL);
		this.midPrice=queue.getAveragePrice(intervalUnit*MID_INTERVAL);
		this.longPrice=queue.getAveragePrice(intervalUnit*LONG_INTERVAL);
	}
	public BigDecimal getShortPrice() {
		return shortPrice;
	}
	public BigDecimal getMidPrice() {
		return midPrice;
	}
	public BigDecimal getLongPrice() {
		return longPrice;
	}
}
