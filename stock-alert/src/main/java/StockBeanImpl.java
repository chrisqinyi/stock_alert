import java.security.GeneralSecurityException;

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

}
