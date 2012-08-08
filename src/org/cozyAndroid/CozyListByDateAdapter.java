package org.cozyAndroid;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CozyListByDateAdapter extends CouchbaseViewListAdapter {

	private LayoutInflater inflater;
	protected NoteByDay parent;

	public CozyListByDateAdapter(CouchDbConnector couchDbConnector, ViewQuery viewQuery, Context context) {
		super(couchDbConnector, viewQuery, true);
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

	
	//Copi�-coll� du code de CouchbaseViewAdapter avec modification de updateListItem()
	
		private static final Logger LOG = LoggerFactory
	            .getLogger(CouchbaseViewListAdapter.class);
		protected CouchbaseListChangesAsyncTask couchChangesAsyncTask;

		protected void updateListItems() {
			//if we're not already in the process of updating the list, start a task to do so
			if(updateListItemsTask == null) {

				updateListItemsTask = new EktorpAsyncTask() {

					protected ViewResult viewResult;

					@Override
					protected void doInBackground() {
						viewResult = NoteByDay.vResult;
					}

					protected void onSuccess() {
						if(viewResult != null) {
							lastUpdateView = viewResult.getUpdateSeq();
							listRows = viewResult.getRows();
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

					}

					@Override
					protected void onDbAccessException(DbAccessException dbAccessException) {
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
