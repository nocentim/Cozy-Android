package org.cozyAndroid;

import org.cozyAndroid.Calendar.NoteByDay;
import org.cozyAndroid.Calendar.TabCalendrier;
import org.cozyAndroid.ListeDossiers.TabDossier;
import org.cozyAndroid.ListeNotes.TabListe;
import org.cozyAndroid.NotesEdit.TabPlus;
import org.cozyAndroid.NotesEdit.TagNote;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;

import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;

public class CozyAndroidActivity extends TabActivity{
	
	private static TabHost tabHost;      // tableau des onglets
	private int [] layoutTab;

	private static boolean ektorpStarted = false;  // booléen permettant le création des dossiers

	private static String TAG = "CozyAndroid";

	{
		TDURLStreamHandlerFactory.registerSelfIgnoreError();
	}

	public static TabHost gettabHost(){
		return tabHost;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TabCalendrier.initHashQuery();
		tabHost = getTabHost();
		layoutTab = new int[4];
		layoutTab[0] = R.layout.tab_notes;
		layoutTab[3] = R.layout.tab_calendrier;
		layoutTab[2] = R.layout.tab_plus;
		layoutTab[1] = R.layout.tab_dossier;
		setupTab("TabListe", new Intent().setClass(this, TabListe.class),0);
		setupTab("TabTags", new Intent().setClass(this, TabDossier.class),1);
		setupTab("TabPlus", new Intent().setClass(this, TabPlus.class),2);
		setupTab("TabCalendrier", new Intent().setClass(this, TabCalendrier.class),3);
		TabPlus.formerActivity("aucune");
	}

	public void onResume(){
		super.onResume();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");

		//On arrete les taches de sychronisation qui changent les affichages
		TabListe.adapter.cancelContinuous();
		NoteByDay.adapter.cancelContinuous();
		TagNote.adapter.cancelContinuous();
		TabListe.searchAdapter.cancelContinuous();
		

		// On arrete le gestionnaire de connexion http
		if(Replication.httpClient != null) {
			Replication.httpClient.shutdown();
		}

		if(Replication.server != null) {
		    Replication.server.close();
		}

		super.onDestroy();
	}
	

	private void setupTab(String tag, Intent intent, int layoutTabIndex) {
		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator( createTabView(tabHost.getContext(), layoutTabIndex)).setContent(intent));
	}

	// créé la vue associée à l'onglet considéré
	private View createTabView(final Context context, int layoutTabIndex) {
		View view = LayoutInflater.from(context).inflate(layoutTab[layoutTabIndex], null);
		view.refreshDrawableState();
		return view;
	}

	public static void notifyEktorpStarted () {
		ektorpStarted = true;
	}
	
	public static boolean ektorpStarted() {
		return ektorpStarted;
	}


}