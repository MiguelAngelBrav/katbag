/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 *  
 * Copyright (C) 2014 The Android Open Source Project Katbag of IPP and Miguel Angel Bravo
 * Licensed under the Apache 2.0 License.
 */

package cl.ipp.katbag;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.AlertDialog.Builder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import cl.ipp.katbag.core.KatbagHandlerSqlite;
import cl.ipp.katbag.fragment.About;
import cl.ipp.katbag.fragment.Add;
import cl.ipp.katbag.fragment.Board;
import cl.ipp.katbag.fragment.Edit;
import cl.ipp.katbag.fragment.SelectType;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;

public class MainActivity extends RootActivity {

	public final Context context = this;
	public Fragment mFragment;
	public TextView version_app;
	public static boolean TABLET;
	public Builder alertDialog;
	public KatbagHandlerSqlite katbagHandler;
	public boolean inBackground = false;
	public FragmentManager m;
	public SlidingMenu slidingMenu;
	
	public static final String TYPE_APP_GAME = "game";
	public static final String TYPE_APP_BOOK = "book";
	public static final String TYPE_APP_COMICS = "comics";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.fragment_menu);

		katbagHandler = new KatbagHandlerSqlite(context);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);

		slidingMenu = getSlidingMenu();

		// tablet
		if (findViewById(R.id.fragment_menu_container) != null) {
			slidingMenu.setSlidingEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(false);
			TABLET = true;// tablet
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		} else { // smartphone
			slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
			slidingMenu.setShadowDrawable(R.drawable.shadow);
			slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			slidingMenu.setFadeDegree(0.35f);
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			slidingMenu.setSlidingEnabled(true);
			slidingMenu.setOnOpenListener(new OnOpenListener() {

				@Override
				public void onOpen() {
					hideSoftKeyboard();
				}
			});

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			actionBar.setDisplayHomeAsUpEnabled(true);
			TABLET = false;// tablet
		}

		mFragment = new Board();
		m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragment_main_container, mFragment);
		t.addToBackStack(mFragment.getClass().getSimpleName());
		getSupportFragmentManager();
		m.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		t.commit();

		setTextVersion();
	}

	public void setTextVersion() {
		try {
			version_app = (TextView) findViewById(R.id.version_app);
			version_app.setText(getString(R.string.version_app) + " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("onCreateView", e.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	// click home menu button
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		hideSoftKeyboard();

		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			if (m.getBackStackEntryCount() <= 1)
				toggle();
			else
				onBackPressed();
			break;
		}

		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (m.getBackStackEntryCount() <= 1) {
			alertDialog = new AlertDialog.Builder(MainActivity.this);
			alertDialog.setTitle(getString(R.string.dialog_exit_title));
			alertDialog.setMessage(getString(R.string.dialog_exit_text));

			alertDialog.setNegativeButton(getString(R.string.dialog_exit_button_no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// :)
				}
			});

			alertDialog.setPositiveButton(getString(R.string.dialog_exit_button_yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			alertDialog.show();

			return;

		} else {
			super.onBackPressed();
		}
	}

	public void changeFragment(View v) {

		hideSoftKeyboard();

		switch (v.getId()) {
		case R.id.menu_item_add:
			mFragment = new SelectType();
			break;

		case R.id.menu_item_board:
			mFragment = new Board();
			break;

		case R.id.menu_item_edit:
			mFragment = new Edit();

			// reset parameters
			Add.id_app = -1;
			Add.name_app_text = "";

			break;

		case R.id.menu_item_about:
			mFragment = new About();
			break;

		default:
			mFragment = new Board();
			break;
		}

		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragment_main_container, mFragment);
		t.addToBackStack(mFragment.getClass().getSimpleName());
		m.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		t.commit();

		if (!TABLET) {
			toggle();
		}
	}

	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		inBackground = false;
		super.onResume();
	}

	@Override
	public void onUserLeaveHint() {
		inBackground = true;
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
}
