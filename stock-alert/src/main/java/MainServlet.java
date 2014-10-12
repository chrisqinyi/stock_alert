import java.text.ParseException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import qinyi.IStockBean;

public class MainServlet extends HttpServlet {

	private Scheduler scheduler;

	@Override
	public void destroy() {
		try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.destroy();

	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		try {
			// �ٴ���һ�� JobDetailʵ��ָ��SimpleJob

			// JobDetail jobDetail = new JobDetail("job1_1", "jGroup1",
			// SimpleJob.class);
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
			JobDetail minJob1 = new JobDetail("minJob1", "jGroup1",
					MinuteJob.class);
			JobDetail minJob2 = new JobDetail("minJob2", "jGroup1",
					MinuteJob.class);
			JobDetail minJob3 = new JobDetail("minJob3", "jGroup1",
					MinuteJob.class);
			JobDetail dailyJob = new JobDetail("dailyJob", "jGroup1",
					DailyJob.class);
			minJob1.getJobDataMap().put("stockBean", stockBean);
			minJob2.getJobDataMap().put("stockBean", stockBean);
			minJob3.getJobDataMap().put("stockBean", stockBean);
			dailyJob.getJobDataMap().put("stockBean", stockBean);
			// ��ͨ��SimpleTrigger������ȹ�������������ÿ2������һ�Σ�������100��

			// SimpleTrigger simpleTrigger = new SimpleTrigger("trigger1_1",
			// "tgroup1");

//			CronTrigger minTrigger1 = new CronTrigger("trigger1", "group1",
//					"0 0/1 * * * ?");
//			CronTrigger minTrigger2 = new CronTrigger("trigger2", "group1",
//					"0 0/1 * * * ?");
//			CronTrigger minTrigger3 = new CronTrigger("trigger3", "group1",
//					"0 0/1 * * * ?");
//			CronTrigger dailyTrigger = new CronTrigger("trigger4", "group1",
//					"0 0/1 * * * ?");
			
			CronTrigger minTrigger1 = new CronTrigger("trigger1", "group1",
					"0 30-59/5 9-10 ? * MON-FRI");
			CronTrigger minTrigger2 = new CronTrigger("trigger2", "group1",
					"0 0-30/5 10-11 ? * MON-FRI");
			CronTrigger minTrigger3 = new CronTrigger("trigger3", "group1",
					"0 0-59/5 13-14 ? * MON-FRI");
			CronTrigger dailyTrigger = new CronTrigger("trigger4", "group1",
					"0 15 9 ? * MON-FRI");
			// simpleTrigger.setStartTime(new Date());
			//
			// simpleTrigger.setRepeatInterval(60*1000);
			//
			// simpleTrigger.setRepeatCount(1);

			// ��ͨ��SchedulerFactory��ȡһ��������ʵ��

			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			scheduler = schedulerFactory.getScheduler();
			scheduler.scheduleJob(minJob1, minTrigger1);// �� ע�Ტ���е���
			scheduler.scheduleJob(minJob2, minTrigger2);
			scheduler.scheduleJob(minJob3, minTrigger3);
			scheduler.scheduleJob(dailyJob, dailyTrigger);
			scheduler.start();// �ݵ�������
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
