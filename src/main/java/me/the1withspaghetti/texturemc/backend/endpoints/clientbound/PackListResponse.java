package me.the1withspaghetti.texturemc.backend.endpoints.clientbound;

import java.util.List;

import me.the1withspaghetti.texturemc.backend.database.AccountDB.Pack;

public class PackListResponse extends Response {
	
	public List<Pack> packs;
	public boolean isMax;

	public PackListResponse(List<Pack> packs, boolean isMax) {
		super(true);
		this.packs = packs;
		this.isMax = isMax;
	}
}
