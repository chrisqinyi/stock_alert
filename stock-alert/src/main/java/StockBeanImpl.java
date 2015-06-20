import java.security.GeneralSecurityException;
import java.util.Collection;

import qinyi.IStockBean;


public class StockBeanImpl implements IStockBean {
private Integer i =0;
	public void processMinutely() throws GeneralSecurityException {
		// TODO Auto-generated method stub
System.out.println("job run"+i);
i++;
	}

	public void process5Minutesly() throws GeneralSecurityException {
		// TODO Auto-generated method stub
		System.out.println("job run"+i+""+i);
		i++;
	}

	public void processDaily() throws GeneralSecurityException {
		// TODO Auto-generated method stub

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
