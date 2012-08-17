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
import org.cozyAndroid.NotesEdit.TabPlus;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewResult.Row;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class CozyItemUtils {
	private static String rev;
	private static String id;
	private static String created_at;
	private static String modified_at;
	private static ArrayList<String> tags;
	private static String titleModif;
	
	
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
	
	public static String getTitleModif() {
    	return titleModif;
    }
	
	public static void setTitleModif(String s) {
		titleModif = s;
	}
	
	public static void setRev(String Rev){
    	rev=Rev;
    }
    
    public static String getRev() {
    	return rev;
    }
    
    public static void setId(String Id){
    	id=Id;
    }
    
    public static String getId() {
    	return id;
    }
    
    public static void setListTags(String s) {
    	tags.remove(0);
    	tags.add(s);
    }
    
    public static String getDateCreation() {
    	return created_at;
    }
    
    public static String getDateModification() {
    	return modified_at;
    }
    
    public static ArrayList<String> getListTags(){
    	return tags;
    }
    
    public static void initListTags() {
    	tags= new ArrayList<String>();
		tags.add("aucune");
    }
    
    public static void setDateCreation(String dateCreation) {
    	created_at = dateCreation;
    }
    
    public static void setDateModification(String dateModif) {
    	modified_at = dateModif;
    }
	
	/**
	 * Handle click on item in list
	 */
	class clicknote implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {		
			TabPlus.modif = true;
			Row row = (Row)parent.getItemAtPosition(position);
			JsonNode item = row.getValueAsNode();
			JsonNode itemText = item.get("title");
			Log.d("title", itemText.getTextValue());
			setRev(item.get("_rev").getTextValue());
			setId(item.get("_id").getTextValue());
			setListTags(item.get("tags").getTextValue());   // Pour l'instant on ne teste qu'un tag
			created_at = item.get("created_at").getTextValue();
			modified_at = item.get("modified_at").getTextValue();
			Log.d("tags", item.get("tags").getTextValue());
	        titleModif = itemText.getTextValue();
	        TabPlus.formerActivity("tabliste");
	        CozyAndroidActivity.gettabHost().setCurrentTab(2);
			
		}
	}
}
