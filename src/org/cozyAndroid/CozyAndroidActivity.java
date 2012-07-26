package org.cozyAndroid;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;



public class CozyAndroidActivity extends TabActivity{
	/** Called when the activity is first created. */
	private static TabHost tabHost;
	private int [] layoutTab;

	private static CozyAndroidActivity instance;


	public static String TAG = "CozyAndroid";

	{
		TDURLStreamHandlerFactory.registerSelfIgnoreError();
	}

	public CozyAndroidActivity() {
		instance = this;
	}

	public static Context getContext() {
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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
	}
	
    @Override
    public void onNewIntent (Intent intent) {
    	int dossierId = intent.getIntExtra("ouvreDossier", -1);
    	if (dossierId != -1) {
    		//On veut basculer vers TabDossier et ouvrir le dossier choisi
    		Dossier d = Dossier.getDossierParId(dossierId);
    		if (d != null) {
	    		getTabHost().setCurrentTab(1);
	    		((TabDossier) getCurrentActivity()).ouvreDossier(Dossier.getDossierParId(dossierId));
    		}
    	}
    }

	public void onResume(){
		super.onResume();
	}

	public static TabHost gettabHost(){
		return tabHost;
	}

	private void setupTab(String tag, Intent intent, int layoutTabIndex) {
		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator( createTabView(tabHost.getContext(), layoutTabIndex)).setContent(intent));
	}

	// créé la vue associée à l'onglet considéré
	private View createTabView(final Context context, int layoutTabIndex) {
		View view = LayoutInflater.from(context).inflate(layoutTab[layoutTabIndex], null);
		view.refreshDrawableState();
		//view.setBackgroundResource(R.color.Ensimag);
		return view;
	}




}