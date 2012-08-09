package org.cozyAndroid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


public class SuggestionAdapter extends CouchbaseViewListAdapter  implements Filterable {

	private LayoutInflater inflater;
	private String constraint;
	private Filter mFilter = null;
	private static int black;
	
	public SuggestionAdapter(CouchDbConnector couchDbConnector, ViewQuery viewQuery, Context context) {
		super(couchDbConnector, viewQuery, true);
		inflater = LayoutInflater.from(context);
		black = context.getResources().getColor(android.R.color.black);
	}

	private static class ViewHolder {
	   TextView title;
	   ImageView button;
	}

	@Override
	public View getView(int position, View itemView, ViewGroup parent) {
        View v = itemView;
        ViewHolder vh;
        if (v == null) {
            v = inflater.inflate(R.layout.suggestion, null);
            vh = new ViewHolder();
            vh.title = (TextView) v.findViewById(R.id.textSuggestion);
            vh.button = (ImageView) v.findViewById(R.id.buttonSuggestion);
            v.setTag(vh);
        } else {
        	vh = (ViewHolder) v.getTag();
        }
        Row row = getRow(position);
        CharSequence text = row.getKey();
        if(text != null) {
			Spannable textSpan = new SpannableString(text);
        	int start = text.toString().toLowerCase().indexOf(constraint);
			if (start != -1) {
				ForegroundColorSpan blacktext = new ForegroundColorSpan(black);
				textSpan.setSpan(blacktext, start, start + constraint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
        	vh.title.setText(textSpan);
        }
        else {
        	vh.title.setText("");
        }
        return v;
	}
	
	public Filter getFilter () {
		if (mFilter == null) {
			mFilter = new SFilter();
		}
		return mFilter;
	}
	
	private class SFilter extends Filter {

		@Override
		public CharSequence convertResultToString (Object resultValue) {
			Row row = (Row) resultValue;
			return row.getKey();
		}
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults res = new FilterResults();
			// TODO Auto-generated method stub
			if (constraint == null || constraint.equals("")) {
				res.count = 0;
				return res;
			}
			SuggestionAdapter.this.constraint = constraint.toString().toLowerCase();
			String start = constraint.toString().toLowerCase();
			String end = start.toUpperCase() + "\u9999";
			SuggestionAdapter.this.viewQuery.startKey(start).endKey(end).group(true);
			SuggestionAdapter.this.updateListItems();
			res.count = getCount();	
			Log.d("filtrage suggestions","count = " + res.count);
			res.values = null;
			return res;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
		}
		
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
						viewResult = couchDbConnector.queryView(viewQuery);
					}
	
					protected void onSuccess() {
						if(viewResult != null) {
							lastUpdateView = viewResult.getUpdateSeq();
							listRows = viewResult.getRows();
							Collections.sort(listRows, new Comparator<Row>() {
	
								@Override
								public int compare(Row lhs, Row rhs) {
									Log.d("tri par valeur", "left : " + lhs.getValueAsInt() + " right : " + rhs.getValueAsInt());
									return ((Integer)rhs.getValueAsInt()).compareTo(lhs.getValueAsInt());
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
