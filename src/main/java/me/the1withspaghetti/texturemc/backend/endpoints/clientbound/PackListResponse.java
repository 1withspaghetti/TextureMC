package me.the1withspaghetti.texturemc.backend.endpoints.clientbound;

import java.util.List;

import me.the1withspaghetti.texturemc.backend.database.AccountDB.Pack;

public class PackListResponse extends Response {
	
	public List<Pack> packs;

	public PackListResponse(List<Pack> packs) {
		super(true);
		this.packs = packs;
		
	}
}
