package me.the1withspaghetti.texturemc;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import me.the1withspaghetti.texturemc.backend.database.AccountDB;
import me.the1withspaghetti.texturemc.backend.database.PackDB;
import me.the1withspaghetti.texturemc.backend.service.MailService;
import me.the1withspaghetti.texturemc.backend.util.VersionControl;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class TextureMcBackendApplication {

	public static void main(String[] args) throws SQLException, IOException {
		AccountDB.connect();
		PackDB.init();
		MailService.init();
		VersionControl.init();
		SpringApplication.run(TextureMcBackendApplication.class, args);
		
	}

}
