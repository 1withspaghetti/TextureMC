package me.the1withspaghetti.texturemc.backend.database.objects;

import org.bson.Document;

public class PackData {
	
	public long _id;
	public long userId;
	
	public Document data;
	
	public PackData(long id, long userId, Document data) {
		this._id = id;
		this.userId = userId;
		this.data = data;
	}
}
