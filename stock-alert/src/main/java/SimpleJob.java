import java.security.GeneralSecurityException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SimpleJob implements Job {

//��ʵ��Job�ӿڷ���

public void execute(JobExecutionContext jobCtx)throws JobExecutionException {
//	new BaiduPushUtil().push_msgToAll( "qinyi ", "中文终于可以用了通过服务器");
	try {
		new StockBean().process5Minutesly();
		new StockBean().processDaily();
	} catch (GeneralSecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//System.out.println(HttpXmlClient.get("http://www.163.com"));  
//	String str = HttpXmlClient.get("http://m.baidu.com/open/stock?lcid=stock&query=600169&srcid=3123&provider=%E8%AF%81%E5%88%B8%E4%B9%8B%E6%98%9F&callback=window.A.getData2057");
//	String str = HttpXmlClient.get("http://m.baidu.com/open/stock?lcid=stock&query=600169&srcid=3123&provider=%E8%AF%81%E5%88%B8%E4%B9%8B%E6%98%9F&callback=window.A.getData2057");
//	System.out.println(str);  
}

}