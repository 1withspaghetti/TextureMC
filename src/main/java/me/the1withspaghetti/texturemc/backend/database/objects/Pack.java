package me.the1withspaghetti.texturemc.backend.database.objects;

import org.bson.Document;

public class Pack {
	
	public long _id;
	
	public Document data;
	
	public Pack(long id, Document data) {
		this._id = id;
		this.data = data;
	}
}
