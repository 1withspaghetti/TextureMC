package me.the1withspaghetti.texturemc.backend.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SessionService {
	
	private static HashMap<UUID, SessionData> sessions = new HashMap<>();
	private static HashMap<UUID, SessionData> unverified_sessions = new HashMap<>();
	
	public static UUID newSession(long userId, boolean verified) {
		UUID id = UUID.randomUUID();
		if (verified)
			sessions.put(id, new SessionService().new SessionData(userId));
		else
			unverified_sessions.put(id, new SessionService().new SessionData(userId));
		return id;
	}
	
	public static SessionData getSession(UUID id) {
		return sessions.get(id);
	}
	
	public static SessionData getUnverifiedSession(String str) {
		UUID id = getUUID(str);
		if (id == null) return null;
		return unverified_sessions.get(id);
	}
	
	public static SessionData getSession(String str) {
		UUID id = getUUID(str);
		if (id == null) return null;
		return sessions.get(id);
	}
	
	public static UUID getUUID(String id) {
		if (id == null) return null;
		if (id.isBlank()) return null;
		try {
			return UUID.fromString(id);
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public static void removeSession(UUID id) {
		sessions.remove(id);
	}
	
	public static void removeSessionsByUser(long userId) {
		Iterator<Entry<UUID, SessionData>> i = sessions.entrySet().iterator();
		while (i.hasNext()) {
			if (i.next().getValue().userId == userId) i.remove();
		}
	}
	
	public static void purgeSessions() {
		Iterator<Entry<UUID, SessionData>> it = sessions.entrySet().iterator();
		while (it.hasNext()) {
			if (it.next().getValue().lastReq < System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1))
				it.remove();
		}
	}
	
	public class SessionData {
		
		public long lastReq;
		public long userId;
		
		public SessionData(long userId) {
			this.userId = userId;
			this.lastReq = System.currentTimeMillis();
		}
		
		public void heartbeat() {
			this.lastReq = System.currentTimeMillis();
		}
	}
}
