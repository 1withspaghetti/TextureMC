package me.the1withspaghetti.texturemc.generator;

import java.io.IOException;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

public class EmailTest {
	
	static final SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
	
	public static void main(String[] args) throws IOException {
		
		Email to = new Email("tylerlplace@gmail.com");
		
		Mail mail = new Mail();
		mail.setSubject("Confirm Your Email | TextureMC");
		mail.setFrom(new Email("no-reply@texturemc.com","TextureMC"));
		mail.setReplyTo(new Email("no-reply@texturemc.com","Contact TextureMC"));
		mail.setTemplateId("d-6027f99fd3ac4a68b6c4df1939bb4197");
		
		Personalization p = new Personalization();
		p.addTo(to);
		p.addDynamicTemplateData("confirm_email_url", "https://texturemc.com/test");
		mail.addPersonalization(p);

	    Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sg.api(request);
	      System.out.println(response.getStatusCode());
	      System.out.println(response.getBody());
	      System.out.println(response.getHeaders());
	    } catch (IOException ex) {
	      throw ex;
	    }
	}
}
