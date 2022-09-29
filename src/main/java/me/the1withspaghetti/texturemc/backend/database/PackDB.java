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
import me.the1withspaghetti.texturemc.backend.database.objects.PackData;
import me.the1withspaghetti.texturemc.backend.database.objects.Texture;

public class PackDB {
	
	private static MongoCollection<PackData> packs;
	public static final Gson gson = new Gson();
	
	public static void init() throws IOException {
		String uri = new String(PackDB.class.getResourceAsStream("/mongodb.secret").readAllBytes());
		CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
		MongoClient mongoClient = MongoClients.create(uri);
		MongoDatabase database = mongoClient.getDatabase("texturemc").withCodecRegistry(pojoCodecRegistry);
		packs = database.getCollection("packs", PackData.class);
		System.out.println("Connection to MongoDB database has been established");
	}
	
	public static void createPack(long id, long userId) {
		PackData pack = new PackData(id, userId, new Document());
		packs.insertOne(pack);
	}
	
	public static PackData getPack(long id, long userId) {
		return packs.find(Filters.and(Filters.eq("_id", id), Filters.eq("userId", userId))).first();
	}
	
	public static void duplicatePack(long id, long userId, long newId) {
		PackData pack = getPack(id, userId);
		pack._id = newId;
		packs.insertOne(pack);
	}
	
	public static boolean deletePack(long id, long userId) {
		return packs.deleteOne(Filters.and(Filters.eq("_id", id), Filters.eq("userId", userId))).getDeletedCount() > 0;
	}
	
	public static Texture getItem(long id, long userId, String path) {
		String newPath = path.replace('/', '.');
		PackData res = packs.find(Filters.and(Filters.eq("_id", id), Filters.eq("userId", userId))).limit(1).projection(Projections.include(newPath)).first();
		if (res == null || res.data == null) return null;
		Texture t = res.data.getEmbedded(Arrays.asList(StringUtils.split(newPath, '.')), Texture.class);
		return t;
	}
	
	public static void insertItem(long id, long userId, String path, Texture item) {
		packs.updateOne(Filters.and(Filters.eq("_id", id), Filters.eq("userId", userId)), Updates.set("data."+path.replace('/', '.'), item));
	}
	
	public static PackData getFullPackData(long id, long userId) {
		PackData res = packs.find(Filters.and(Filters.eq("_id", id), Filters.eq("userId", userId))).limit(1).first();
		return res;
	}
	
	
}
