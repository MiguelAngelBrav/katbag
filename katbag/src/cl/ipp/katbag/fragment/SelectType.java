/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 *  
 * Copyright (C) 2014 The Android Open Source Project Katbag of IPP and Miguel Angel Bravo
 * Licensed under the Apache 2.0 License.
 */

package cl.ipp.katbag.fragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;

public class SelectType extends Fragment implements OnClickListener {
	
	private Tracker tracker;

	static View v = null;
	public static MainActivity mainActivity;
	public RelativeLayout type_app_game, type_app_book, type_app_comics;
	public Fragment mFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.tracker = EasyTracker.getInstance(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_select_type, container, false);
		mainActivity = (MainActivity) super.getActivity();

		type_app_game = (RelativeLayout) v.findViewById(R.id.type_app_game);
		type_app_book = (RelativeLayout) v.findViewById(R.id.type_app_book);
		type_app_comics = (RelativeLayout) v.findViewById(R.id.type_app_comics);

		type_app_game.setOnClickListener(this);
		type_app_book.setOnClickListener(this);
		type_app_comics.setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		String type_app = "";
		mainActivity.hideSoftKeyboard();

		switch (v.getId()) {
		case R.id.type_app_game:
			type_app = MainActivity.TYPE_APP_GAME;
			break;

		case R.id.type_app_book:
			type_app = MainActivity.TYPE_APP_BOOK;
			break;

		case R.id.type_app_comics:
			type_app = MainActivity.TYPE_APP_COMICS;
			break;
		}

		Bundle bundle = new Bundle();
		bundle.putString("type_app", type_app);

		// initialize parameters of add class
		Add.id_app = -1;
		Add.name_app_text = "";

		mFragment = new Add();
		mFragment.setArguments(bundle);
		FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragment_main_container, mFragment);
		t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		t.addToBackStack(mFragment.getClass().getSimpleName());
		t.commit();
	}

	@Override
	public void onResume() {
		mainActivity.getSupportActionBar().setTitle(R.string.title_activity_select_type);

		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		super.onResume();
		
		this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
        this.tracker.send( MapBuilder.createAppView().build() );
	}
}
