import java.security.GeneralSecurityException;

public class BaiduMessageUtil {
	public static void main(String[] args) throws GeneralSecurityException {
		   //这个类主要是设置邮件   
	      MailSenderInfo mailInfo = new MailSenderInfo();    
//	      mailInfo.setMailServerHost("smtp.mail.me.com");    
//	      mailInfo.setMailServerPort("587"); 
	      mailInfo.setMailServerHost("10.65.61.140");    
	      mailInfo.setMailServerPort("255");   
	      mailInfo.setValidate(true);    
//	      mailInfo.setUserName("qinyichris@icloud.com");    
//	      mailInfo.setPassword("5C6E626762393B3A3F");//您的邮箱密码    
	      mailInfo.setUserName("puppet");    
	      mailInfo.setPassword("12313123");//您的邮箱密码  
	      mailInfo.setFromAddress("qinyichris@icloud.com");    
	      mailInfo.setToAddress("qinyichris@icloud.com");    
	      mailInfo.setSubject("设置邮箱标题 如http://www.guihua.org 中国桂花网");    
	      mailInfo.setContent("设置邮箱内容 如http://www.guihua.org 中国桂花网 是中国最大桂花网站==");    
	         //这个类主要来发送邮件   
	      SimpleMailSender sms = new SimpleMailSender();   
	          sms.sendTextMail(mailInfo);//发送文体格式    
	          sms.sendHtmlMail(mailInfo);//发送html格式   
	}
}
