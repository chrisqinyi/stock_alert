package qinyi;
import java.security.GeneralSecurityException;

public interface IStockBean {

	public abstract void processMinutely() throws GeneralSecurityException;

	public abstract void process5Minutesly() throws GeneralSecurityException;

	public abstract void processDaily() throws GeneralSecurityException;

}