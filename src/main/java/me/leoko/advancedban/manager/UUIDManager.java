package me.leoko.advancedban.manager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import me.leoko.advancedban.MethodInterface;
import me.leoko.advancedban.Universal;

public class UUIDManager {
	private Map<String, String> activeUUIDs = new HashMap<String, String>();
	private MethodInterface mi = Universal.get().getMethods();

	private static UUIDManager instance = null;
	public static UUIDManager get(){
		if(instance == null) instance = new UUIDManager();
		return instance;
	}
	
	public String getInitialUUID(String name){
		name = name.toLowerCase();
		if(!mi.getBoolean(mi.getConfig(), "UUID-Fetcher.Enabled", true)) return name;
		if(mi.getBoolean(mi.getConfig(), "UUID-Fetcher.Intern", false)) return mi.getInternUUID(name);

		String uuid = null;
		try {
			uuid = askAPI(mi.getString(mi.getConfig(), "UUID-Fetcher.REST-API.URL"), name, mi.getString(mi.getConfig(), "UUID-Fetcher.REST-API.Key"));
		} catch (IOException e) {
			System.out.println("Error -> "+e.getMessage());
			System.out.println("!! Failed fetching UUID of "+name);
			System.out.println("!! Could not connect to REST-API under "+mi.getString(mi.getConfig(), "UUID-Fetcher.REST-API.URL"));
		}

		if(uuid == null) {
			System.out.println("Trying to fetch UUID form BackUp-API...");
			try {
				uuid = askAPI(mi.getString(mi.getConfig(), "UUID-Fetcher.BackUp-API.URL"), name, mi.getString(mi.getConfig(), "UUID-Fetcher.BackUp-API.Key"));
			} catch (IOException e) {
				System.out.println("!! Failed fetching UUID of "+name);
				System.out.println("!! Could not connect to REST-API under "+mi.getString(mi.getConfig(),"UUID-Fetcher.BackUp-API.URL"));
			}
		}

		if(uuid == null) {
			System.out.println("!! !! Warning we have not been able to fetch the UUID of the Player " + name);
			System.out.println("!! Make sure that the name is spelled correctly and if it is change your UUID-Fetcher settings!");
		}

		return uuid;
	}

	public String getUUID(String name){
		if(activeUUIDs.containsKey(name)) return activeUUIDs.get(name);
		return getInitialUUID(name);
	}
	
	@SuppressWarnings("resource")
	public String getNameFromUUID(String uuid, boolean forceInitial){
		if(!mi.getBoolean(mi.getConfig(), "UUID-Fetcher.Enabled", true)) return uuid;
		if(!mi.getBoolean(mi.getConfig(), "UUID-Fetcher.Intern", false)) return mi.getName(uuid);

		if(!forceInitial)
			for(Entry<String, String> rs : activeUUIDs.entrySet())
				if(rs.getValue().equalsIgnoreCase(uuid)) return rs.getKey();
			
		try {			String s = new Scanner(new URL("https://api.mojang.com/user/profiles/"+uuid+"/names").openStream(), "UTF-8").useDelimiter("\\A").next();
			s=s.substring(s.lastIndexOf('{'), s.lastIndexOf('}')+1);
			return mi.parseJSON(s, "name");
		} catch (Exception exc) {
			return null;
		}
	}

	private String askAPI(String url, String name, String key) throws IOException{
		HttpURLConnection request = (HttpURLConnection) new URL(url.replaceAll("%NAME%", name).replaceAll("%TIMESTAMP%", new Date().getTime()+"")).openConnection();
		request.connect();

		String uuid = mi.parseJSON(new InputStreamReader(request.getInputStream()), key);

		if(uuid == null){
			System.out.println("!! Failed fetching UUID of "+name);
			System.out.println("!! Could not find key '"+key+"' in the servers response");
			System.out.println("!! Response: "+request.getResponseMessage());
		}else{
			if (activeUUIDs.containsKey(name)) activeUUIDs.remove(name);
			activeUUIDs.put(name, uuid);
		}
		return uuid;
	}
}
