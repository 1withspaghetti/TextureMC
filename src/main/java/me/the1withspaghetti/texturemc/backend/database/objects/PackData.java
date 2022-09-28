package me.the1withspaghetti.texturemc.backend.database.objects;

import org.bson.Document;

public class PackData {
	
	public long _id;
	public long userId;
	
	public Document data;
	
	public PackData() {}
	
	public PackData(long _id, long userId, Document data) {
		this._id = _id;
		this.userId = userId;
		this.data = data;
	}
}
