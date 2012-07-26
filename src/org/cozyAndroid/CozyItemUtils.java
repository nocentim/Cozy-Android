package org.cozyAndroid;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;


import android.widget.EditText;

public class CozyItemUtils {
	
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public static JsonNode createOrUpdate(String title, String body, String rev, String id) {
		ObjectNode item = JsonNodeFactory.instance.objectNode();
		Calendar calendar = GregorianCalendar.getInstance();
		String currentTimeString = dateFormatter.format(calendar.getTime());
		if (rev == null) {
			UUID uuid = UUID.randomUUID(); 
	    	long currentTime = calendar.getTimeInMillis();
	    	String new_id = currentTime + "-" + uuid.toString();


	    	item.put("_id", new_id);
	    	item.put("created_at", currentTimeString);
		} else {
			item.put("_id", id);
			item.put("_rev", rev);
		}
		item.put("title", title);
    	item.put("body", body);
    	item.put("icon", Boolean.FALSE);
    	item.put("modified_at", currentTimeString);
    	return item;
	}
}
