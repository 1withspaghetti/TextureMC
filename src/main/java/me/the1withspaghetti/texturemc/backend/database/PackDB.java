package me.the1withspaghetti.texturemc.backend.database;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;

import me.the1withspaghetti.texturemc.backend.database.objects.Pack;
import me.the1withspaghetti.texturemc.backend.database.objects.Texture;
import me.the1withspaghetti.texturemc.backend.exception.ApiException;

public class PackDB {
	
	private static MongoCollection<Pack> packs;
	public static final Gson gson = new Gson();
	
	public static void init() throws IOException {
		String uri = new String(PackDB.class.getResourceAsStream("/mongodb.secret").readAllBytes());
		CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
		MongoClient mongoClient = MongoClients.create(uri);
		MongoDatabase database = mongoClient.getDatabase("texturemc").withCodecRegistry(pojoCodecRegistry);
		packs = database.getCollection("packs", Pack.class);
		System.out.println("Connection to MongoDB database has been established");
	}
	
	public static void createPack(long id) {
		Pack pack = new Pack(id, new Document());
		packs.insertOne(pack);
	}
	
	public static Texture getItem(long id, String path) {
		String newPath = path.replace('/', '.');
		Pack res = packs.find(Filters.eq("_id", id), Pack.class).limit(1).projection(Projections.include(newPath)).first();
		if (res == null) throw new ApiException("Unknown Texture Pack");
		Texture t = res.data.getEmbedded(Arrays.asList(StringUtils.split(newPath, '.')), Texture.class);
		if (t == null) throw new ApiException("Unknown Item");
		return t;
	}
	
	public static void insertItem(long id, String path, Texture item) {
		packs.updateOne(Filters.eq("_id", id), Updates.set("data."+path.replace('/', '.'), item));
	}
	
	
}
