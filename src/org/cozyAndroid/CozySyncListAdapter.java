package org.cozyAndroid;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.DbAccessException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.ektorp.android.util.CouchbaseViewListAdapter;
import org.ektorp.android.util.EktorpAsyncTask;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CozySyncListAdapter extends CouchbaseViewListAdapter {

	private LayoutInflater inflater;
	protected TabListe parent;

	public CozySyncListAdapter(TabListe parent, CouchDbConnector couchDbConnector, ViewQuery viewQuery, Context context) {
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
	
	public void lanceRecherche(String constraint, int tri) {
		if (tri == TabListe.TRI_PERTINENCE) {
			Log.w("CozySyncListAdapter", "recherche : tri par pertinence non implemente");
			return;
		}
		final String [] constraints = constraint.split(" +");
		if (constraint.equals("") || constraints.length == 0 || constraints[0].equals("")) {
			//On se contente d'afficher toutes les notes triés comme il faut
			Log.d("CozySyncListAdapter","recherche : pas de filtrage");
			String view;
			if (tri == TabListe.TRI_CHEMIN) {
				view = Replication.byPathViewName;
			} else {
				view = Replication.byDateViewName;
			}
			viewQuery.designDocId(Replication.dDocId).viewName(view).descending(false).startKey(null).endKey(null);
			updateListItems();
			return;
		}
		String view;
		if (tri == TabListe.TRI_CHEMIN) {
			view = Replication.byWordAndPathViewName;
		} else {
			view = Replication.byWordAndDateViewName;
		}
		//On fait un pré-filtrage sur le premier mot de la recherche
		//(couchDB n'est pas prévu pour rechercher plusieurs mots, il faudra filter sur les autres mots à la main)
		Log.d("CozySyncListAdapter","recherche : premier mot = \'" + constraints[0] + "\'");
		viewQuery.designDocId(Replication.dDocId).viewName(view).startKey(new String [] {constraints[0].toLowerCase()})
										         .endKey(new String [] {constraints[0].toUpperCase()+ "\u9999", null}).descending(false);
		if(updateListItemsTask == null) {
			
			updateListItemsTask = new EktorpAsyncTask() {

				protected ViewResult viewResult;

				@Override
				protected void doInBackground() {
					viewResult = couchDbConnector.queryView(viewQuery);
					if(viewResult != null) {
						lastUpdateView = viewResult.getUpdateSeq();
						listRows = viewResult.getRows();
						Log.d("CozySyncListAdapter", "recherche : avant filtrage, " + listRows.size() + " doc trouves");
						//Filtrage des resultats de recherche :
						// 1) on enleve les doublons
						// 2) on supprime les documents qui ne contiennent pas tout les mots
						// (on sait qu'ils contiennent tous le premier mot (c'etait la key de la query)
						HashMap<String,Integer> occurencesById = new HashMap<String, Integer>();
						Iterator<Row> it = listRows.iterator();
						while (it.hasNext()) {
							Row cour = it.next();
							String id = cour.getValueAsNode().get("_id").getTextValue();
							if (occurencesById.containsKey(id)) {
								//doublon : on le supprime
								it.remove();
							} else {
								//nouveau document : on regarde s'il contient tous les mots
								boolean supprimer = false;
								JsonNode node = cour.getValueAsNode();
								String title = node.get("title").getTextValue();
								String body = node.get("body").getTextValue();
								for (int i = 1; i < constraints.length; i++) {
									if (!(title.contains(constraints[i]) || body.contains(constraints[i]))) {
										supprimer = true;
										it.remove();
										break;
									}
								}
								if (!supprimer) {
									occurencesById.put(id, 1);
								}
							}
						}
					}
				}

				protected void onSuccess() {
					notifyDataSetChanged();
					updateListItemsTask = null;
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
}
