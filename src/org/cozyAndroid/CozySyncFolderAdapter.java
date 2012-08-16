package org.cozyAndroid;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.DbAccessException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.ektorp.android.util.ChangesFeedAsyncTask;
import org.ektorp.android.util.CouchbaseViewListAdapter;
import org.ektorp.android.util.EktorpAsyncTask;
import org.ektorp.changes.ChangesCommand;
import org.ektorp.changes.DocumentChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CozySyncFolderAdapter extends CouchbaseViewListAdapter {

	private LayoutInflater inflater;
	private TabDossier context;
	
	
	public CozySyncFolderAdapter(CouchDbConnector couchDbConnector, ViewQuery viewQuery, TabDossier context) {
		super(couchDbConnector, viewQuery, true);
		inflater = LayoutInflater.from(context);
		this.context = context;
	}

	public boolean isDossier(int position) {
		Row r = (Row) getItem(position);
		JsonNode type = r.getValueAsNode().get("type");
		return type != null && type.getTextValue().equals("folder");
	}
	
	private boolean isDossier(JsonNode n) {
		JsonNode type = n.get("type");
		return type != null && type.getTextValue().equals("folder");
	}
	
	public boolean contient (String name) {
		Iterator<Row> it = listRows.iterator();
		while (it.hasNext()) {
			JsonNode n = it.next().getValueAsNode().get("name");
			if (n != null && n.getTextValue().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private static class ViewHolder {
	   TextView titre;
	   TextView body;
	   ImageView icon;
	}

	@Override
	public View getView(int position, View itemView, ViewGroup parent) {
        View v = itemView;
        ViewHolder vh;
        if (v == null) {
            v = inflater.inflate(R.layout.elem_list, null);
            vh = new ViewHolder();
            vh.titre = (TextView) v.findViewById(R.id.titre_note);
    		vh.body = (TextView)v.findViewById(R.id.body_note);
    		vh.icon = (ImageView)v.findViewById(R.id.icone);
            v.setTag(vh);
        } else {
        	vh = (ViewHolder) v.getTag();
        }
        Row row = getRow(position);
        JsonNode item = row.getValueAsNode();
        if (isDossier(position)) {
			// C'est un dossier :
			// on affiche son nom et ce qu'il y a dedans
            String name = item.get("name").getTextValue();
            String shortName;
            int start = name.lastIndexOf('/');
            if (start != -1) {
            	shortName = name.substring(start + 1);
            } else {
            	shortName = name;
            }
			vh.titre.setText(shortName);
			//vh.body.setText(d.getInfos());
			vh.body.setText("");
			vh.icon.setImageResource(R.drawable.folder);
		} else {
			// C'est une note :
			// on affiche son titre et ses premiers mots
			String titre = item.get("title").getTextValue();
			String body = item.get("body").getTextValue().toString().replace("\n", " ");
			vh.titre.setText(titre);
			if (body.length() > 150) {
				vh.body.setText(body.substring(0, 149));
			} else {
				vh.body.setText(body);
			}
			vh.icon.setImageResource(R.drawable.note);
		}
        return v;
	}
	
	protected void setDossier(String name) {
		viewQuery.key(name);
		updateListItems();
		Log.d("CozySyncFolder","setDossier :" + name);
	}
	
	public void update() {
		updateListItems();
	}


	
	//Copié-collé du code de CouchbaseViewAdapter avec modification de updateListItem()
	
	private static final Logger LOG = LoggerFactory
            .getLogger(CouchbaseViewListAdapter.class);
	protected CouchbaseListChangesAsyncTask couchChangesAsyncTask;

	@Override
	protected void updateListItems() {
		//if we're not already in the process of updating the list, start a task to do so
		if(updateListItemsTask == null) {

			updateListItemsTask = new EktorpAsyncTask() {

				protected ViewResult viewResult;

				@Override
				protected void doInBackground() {
					viewResult = couchDbConnector.queryView(viewQuery);
				}

				protected void onSuccess() {
					if(viewResult != null) {
						lastUpdateView = viewResult.getUpdateSeq();
						listRows = viewResult.getRows();
						Collections.sort(listRows, new Comparator<Row>() {

							@Override
							public int compare(Row lhs, Row rhs) {
								JsonNode l = lhs.getValueAsNode();
								JsonNode r = rhs.getValueAsNode();
								if (isDossier(l)) {
									if (isDossier(r)) {
										return 0;
									}
									return -1;
								}
								return 1;
							}
						});
						notifyDataSetChanged();
					}
					updateListItemsTask = null;

					//we want to start our changes feed AFTER
					//getting our first copy of the view
					if(couchChangesAsyncTask == null && followChanges) {
						//create an ansyc task to get updates
						ChangesCommand changesCmd = new ChangesCommand.Builder().since(lastUpdateView)
								.includeDocs(false)
								.continuous(true)
								.heartbeat(5000)
								.build();

						couchChangesAsyncTask = new CouchbaseListChangesAsyncTask(couchDbConnector, changesCmd);
						couchChangesAsyncTask.execute();
					}

					if(lastUpdateChangesFeed > lastUpdateView) {
						if (LOG.isDebugEnabled()) {
				            LOG.debug("Finished, but still behind " + lastUpdateChangesFeed + " > " + lastUpdateView);
						}
						updateListItems();
					}
					context.enableButtons();
					
				}

				@Override
				protected void onDbAccessException(
						DbAccessException dbAccessException) {
					handleViewAsyncTaskDbAccessException(dbAccessException);
				}

			};

			updateListItemsTask.execute();
		}
	}

	protected void handleViewAsyncTaskDbAccessException(DbAccessException dbAccessException) {
		LOG.error("DbAccessException accessing view for list", dbAccessException);
	}

	private class CouchbaseListChangesAsyncTask extends ChangesFeedAsyncTask {

		public CouchbaseListChangesAsyncTask(CouchDbConnector couchDbConnector,
				ChangesCommand changesCommand) {
			super(couchDbConnector, changesCommand);
		}

		@Override
		protected void handleDocumentChange(DocumentChange change) {
			lastUpdateChangesFeed = change.getSequence();
			updateListItems();
		}

		@Override
		protected void onDbAccessException(DbAccessException dbAccessException) {
			handleChangesAsyncTaskDbAccessException(dbAccessException);
		}

	}

	protected void handleChangesAsyncTaskDbAccessException(DbAccessException dbAccessException) {
		LOG.error("DbAccessException following changes feed for list", dbAccessException);
	}

	/**
	 * Cancel the following of continuous changes, necessary to properly clean up resources
	 */
	public void cancelContinuous() {
		if(couchChangesAsyncTask != null) {
			couchChangesAsyncTask.cancel(true);
		}
	}
}
