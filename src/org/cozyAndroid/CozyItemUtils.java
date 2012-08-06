package org.cozyAndroid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class CozyItemUtils {
	
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    public static JsonNode createOrUpdateNote(String title, String body, String rev, String id, ArrayList<String> tags) {
		return createOrUpdateNote(title, body, rev, id, tags, ""); 
	}
    
	public static JsonNode createOrUpdateNote(String title, String body, String rev, String id, ArrayList<String> tags, String folder) {
		ObjectNode item = JsonNodeFactory.instance.objectNode();
		Calendar calendar = GregorianCalendar.getInstance();
		String currentTimeString = dateFormatter.format(calendar.getTime());
		if (rev == null) {
			UUID uuid = UUID.randomUUID();
	    	long currentTime = calendar.getTimeInMillis();
	    	String new_id = currentTime + "-" + uuid.toString();
	    	item.put("_id", new_id);
	    	item.put("type","note");
	    	item.put("created_at", currentTimeString);
		} else {
			item.put("_id", id);
			item.put("_rev", rev);
		}
		item.put("title", title);
		if (tags!=null) {
			
			Iterator<String> it = tags.iterator();
			item.put("tags", it.next());
		} else {
			item.put("tags", "aucun");
		}
    	item.put("body", body);
    	item.put("parent", folder);
    	item.put("icon", Boolean.FALSE);
    	item.put("modified_at", currentTimeString);
    	return item;
	}
	
	public static JsonNode createOrUpdateFolder(String name, String parent, String rev, String id) {
		ObjectNode item = JsonNodeFactory.instance.objectNode();
		Calendar calendar = GregorianCalendar.getInstance();
		String currentTimeString = dateFormatter.format(calendar.getTime());
		if (rev == null) {
			UUID uuid = UUID.randomUUID();
	    	long currentTime = calendar.getTimeInMillis();
	    	String new_id = currentTime + "-" + uuid.toString();
	    	item.put("_id", new_id);
	    	item.put("type","folder");
	    	item.put("created_at", currentTimeString);
		} else {
			item.put("_id", id);
			item.put("_rev", rev);
		}
		item.put("name", name);
		item.put("parent", parent);
    	item.put("modified_at", currentTimeString);
		return item;
	}
}
