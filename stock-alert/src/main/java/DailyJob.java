import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import qinyi.IStockBean;

public class DailyJob implements Job {

	// ��ʵ��Job�ӿڷ���
	public void execute(IStockBean stockBean) {
		try {
			DataPersistUtil.beforeProcess(stockBean);
			stockBean.processDaily();
			DataPersistUtil.afterProcess(stockBean);

		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(HttpXmlClient.get("http://www.163.com"));
		// String str =
		// HttpXmlClient.get("http://m.baidu.com/open/stock?lcid=stock&query=600169&srcid=3123&provider=%E8%AF%81%E5%88%B8%E4%B9%8B%E6%98%9F&callback=window.A.getData2057");
		// String str =
		// HttpXmlClient.get("http://m.baidu.com/open/stock?lcid=stock&query=600169&srcid=3123&provider=%E8%AF%81%E5%88%B8%E4%B9%8B%E6%98%9F&callback=window.A.getData2057");
		// System.out.println(str);
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void execute(JobExecutionContext jobCtx)
			throws JobExecutionException {
		IStockBean stockBean = (IStockBean) jobCtx.getJobDetail()
				.getJobDataMap().get("stockBean");
		execute(stockBean);

	}

}