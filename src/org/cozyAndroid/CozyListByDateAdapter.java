package org.cozyAndroid;
import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult.Row;
import org.ektorp.android.util.CouchbaseViewListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CozyListByDateAdapter extends CouchbaseViewListAdapter {

	private LayoutInflater inflater;
	protected NoteByDay parent;

	public CozyListByDateAdapter(NoteByDay parent, CouchDbConnector couchDbConnector, ViewQuery viewQuery, Context context) {
		super(couchDbConnector, viewQuery, true);
		this.parent = parent;
		inflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {
	   ImageView icon;
	   TextView title;
	   TextView body;
	}

	@Override
	public View getView(int position, View itemView, ViewGroup parent) {
        View v = itemView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.elem_list, null);
        }
        ViewHolder vh = new ViewHolder();
        vh.title = (TextView) v.findViewById(R.id.titre_note);
        vh.body = (TextView) v.findViewById(R.id.body_note);
        vh.icon = (ImageView) v.findViewById(R.id.icone);
        v.setTag(vh);
        TextView title = ((ViewHolder)v.getTag()).title;
        Row row = getRow(position);
        JsonNode item = row.getValueAsNode();
        JsonNode itemText = item.get("title");
        if(itemText != null) {
        	title.setText(itemText.getTextValue());
        }
        else {
        	title.setText("");
        }

        TextView body = ((ViewHolder)v.getTag()).body;
        row = getRow(position);
        item = row.getValueAsNode();
        itemText = item.get("body");
        if(itemText != null) {
        	body.setText(itemText.getTextValue());
        }
        else {
        	body.setText("");
        }
        
        ImageView icon = ((ViewHolder)v.getTag()).icon;
        JsonNode checkNode = item.get("icon");
        if(checkNode != null) {
	        if(checkNode.getBooleanValue()) {
	        	icon.setImageResource(R.drawable.list_area___checkbox___checked);
	        }
	        else {
	        	icon.setImageResource(R.drawable.list_area___checkbox___unchecked);
	        }
        }

        return v;
	}
}
