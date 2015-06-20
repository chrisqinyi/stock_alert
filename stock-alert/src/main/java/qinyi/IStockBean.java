package qinyi;
import java.security.GeneralSecurityException;
import java.util.Collection;

public interface IStockBean {

	public abstract void processMinutely() throws GeneralSecurityException;

	public abstract void process5Minutesly() throws GeneralSecurityException;

	public abstract void processDaily() throws GeneralSecurityException;
	
	public abstract Collection<String> getStockCodes();
	
	public abstract void setStockCodes(Collection<String> stockCodes);
	
	public abstract Collection<String> getStockCodesSell();
	
	public abstract void setStockCodesSell(Collection<String> stockCodesSell);
	
	public abstract Collection<String> getStockCodesNew();
	
	public abstract void setStockCodesNew(Collection<String> stockCodesNew);

}