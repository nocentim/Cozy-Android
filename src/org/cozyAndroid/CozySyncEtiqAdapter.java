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
import android.widget.TextView;

public class CozySyncEtiqAdapter extends CouchbaseViewListAdapter {

	private LayoutInflater inflater;
	protected TagNote parent;
	
	public CozySyncEtiqAdapter(TagNote parent, CouchDbConnector couchDbConnector, ViewQuery viewQuery, Context context) {
		super(couchDbConnector, viewQuery, true);
		this.parent = parent;
		inflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {
		   TextView name;
		}
	
	@Override
	public View getView(int position, View itemView, ViewGroup parent) {
        View v = itemView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.elem_tag, null);
        }
        ViewHolder vh = new ViewHolder();
        vh.name = (TextView) v.findViewById(R.id.tag);
        v.setTag(vh);
        TextView tag = ((ViewHolder)v.getTag()).name;
        Row row = getRow(position);
        JsonNode item = row.getValueAsNode();
        JsonNode itemText = item.get("tags");
        if(itemText != null) {
        	tag.setText(itemText.getTextValue());
        }
        else {
        	tag.setText("");
        }

        return v;
	}
	
}
