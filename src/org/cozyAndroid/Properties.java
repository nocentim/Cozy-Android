package org.cozyAndroid;


import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Properties extends Activity implements View.OnClickListener{
	
	private Bundle param;
	private TextView list_tags;
	private TextView created_at;
	private TextView modified_at;
	public static String tag;
	
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.properties);
		tag = "aucune";
		TabPlus.retour=true;
		
		// Listener sur le bouton etiquettes
		findViewById(R.id.click_etiquettes).setOnClickListener(this); 
	}
	
	public void onResume() {
		super.onResume();
		list_tags = (TextView) findViewById(R.id.etiq);
		created_at = (TextView) findViewById(R.id.creation);
		modified_at = (TextView) findViewById(R.id.modification);
		if ((TabPlus.modif) && (TabPlus.formerActivity()=="tabliste")) {
			param = this.getIntent().getExtras();
			if (CozyItemUtils.getListTags()!=null) {
				Iterator<?> i = CozyItemUtils.getListTags().iterator();
				list_tags.setText((CharSequence) i.next());
				created_at.setText(CozyItemUtils.getDateCreation());
				modified_at.setText(CozyItemUtils.getDateModification());
			} else {
				list_tags.setText(tag);
			}
		} else {
			list_tags.setText(tag);
		}
		TabPlus.formerActivity("properties");
	}

	@Override
	public void onClick(View v) {
		Intent tag = new Intent(Properties.this, TagNote.class);
		if ((TabPlus.modif) && (TabPlus.formerActivity()=="tabliste")) {
			tag.putExtra("id", param.getString("id"));
			tag.putExtra("rev", param.getString("rev"));
			tag.putExtra("title", param.getString("title"));
			tag.putExtra("body", param.getString("body"));
		}
		Properties.this.startActivity(tag);
	}
}