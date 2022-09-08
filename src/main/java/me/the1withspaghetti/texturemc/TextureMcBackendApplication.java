package me.the1withspaghetti.texturemc;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import me.the1withspaghetti.texturemc.backend.util.ConsoleCommands;
import me.the1withspaghetti.texturemc.backend.util.VersionControl;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class TextureMcBackendApplication {

	public static void main(String[] args) throws SQLException, IOException {
		SpringApplication.run(TextureMcBackendApplication.class, args);
		VersionControl.init();
		ConsoleCommands.init(System.in);
	}

}
