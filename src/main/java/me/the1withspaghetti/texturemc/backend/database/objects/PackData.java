package me.the1withspaghetti.texturemc.backend.database.objects;

import org.bson.Document;

public class PackData {
	
	public long _id;
	
	public Document data;
	
	public PackData(long id, Document data) {
		this._id = id;
		this.data = data;
	}
}
