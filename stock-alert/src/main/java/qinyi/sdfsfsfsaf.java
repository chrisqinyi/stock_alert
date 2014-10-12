package qinyi;

import java.security.GeneralSecurityException;


public class sdfsfsfsaf {
public static void main(String [] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, GeneralSecurityException{
	IStockBean stockBean=null;
	try {
		stockBean = (IStockBean) Class.forName("qinyi.StockBean").newInstance();
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	stockBean.process5Minutesly();
}
}
