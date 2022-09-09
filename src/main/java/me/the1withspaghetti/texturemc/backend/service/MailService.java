package me.the1withspaghetti.texturemc.backend.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGridAPI;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

@Service
public class MailService {
	
	@Autowired
    private static SendGridAPI sendGridAPI;
	
	public static boolean sendConfirmationEmail(String email, String confirmation_url) throws IOException {
		Email to = new Email(email);
		
		Mail mail = new Mail();
		mail.setSubject("Confirm Your Email | TextureMC");
		mail.setFrom(new Email("no-reply@texturemc.com","TextureMC"));
		mail.setReplyTo(new Email("no-reply@texturemc.com","Contact TextureMC"));
		mail.setTemplateId("d-6027f99fd3ac4a68b6c4df1939bb4197");
		
		Personalization p = new Personalization();
		p.addTo(to);
		p.addDynamicTemplateData("confirm_email_url", confirmation_url);
		mail.addPersonalization(p);

	    Request request = new Request();
	    request.setMethod(Method.POST);
	    request.setEndpoint("mail/send");
	    request.setBody(mail.build());
	    Response res = sendGridAPI.api(request);
		System.out.println(res.getStatusCode());
		System.out.println(res.getBody());
		System.out.println(res.getHeaders());
		return (res.getStatusCode() / 100 == 2);
	}
	
}
