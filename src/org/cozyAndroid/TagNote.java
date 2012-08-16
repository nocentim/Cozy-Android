package org.cozyAndroid;

import org.codehaus.jackson.JsonNode;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.ektorp.impl.StdCouchDbInstance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.couchbase.touchdb.ektorp.TouchDBHttpClient;


public class TagNote extends Activity implements View.OnClickListener{
	private ListView listeTags;
	public static final String TAG = "TagNote";
	static CozySyncEtiqAdapter adapter;
	private EditText tag;
	private Bundle param;
	public static ViewResult vResult;
	public static ViewQuery vQuery = new ViewQuery()
			.designDocId(Replication.dDocId).viewName(Replication.byTagsViewName).descending(true);
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_note);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	
		listeTags = (ListView) findViewById(R.id.listTags);
		vResult= Replication.couchDbConnector.queryView(vQuery);
		listeTags.setAdapter(adapter);
		adapter.updateListItems();
		tag = (EditText) findViewById(R.id.TagEdition);
		param = this.getIntent().getExtras();
		findViewById(R.id.oktag).setOnClickListener(this);
		Replication.startReplications(this);
	}
	
	@Override
	public void onClick(View v) {
		//ArrayList<String> l = new ArrayList<String>();
		//l.add(tag.getText().toString());
		//TabPlus.createOrUpdateItem(param.getString("title"), param.getString("body"), param.getString("rev"), param.getString("id"), l);
		TabPlus.addTag(tag.getText().toString());
		Properties.tag = tag.getText().toString();
		tag.setText(" ");
		finish();
	}
	
}