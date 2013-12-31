package cl.ipp.katbag;

import org.holoeverywhere.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import cl.ipp.katbag.fragment.Board;
import cl.ipp.katbag.fragment.SelectType;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;

public class MainActivity extends RootActivity {
	
	public Fragment mFragment;
	public TextView version_app;
	public static boolean TABLET; 
	public org.holoeverywhere.app.AlertDialog.Builder alertDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.fragment_menu);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		
		SlidingMenu sm = getSlidingMenu();
		
		// if is tablet, hide menu slider		
		if (findViewById(R.id.fragment_menu_container) != null) {
			sm.setSlidingEnabled(false);     
		    actionBar.setDisplayHomeAsUpEnabled(false);
		    TABLET = true;//tablet
		    
		} else { // if is smartphone, show menu slider
			sm.setShadowWidthRes(R.dimen.shadow_width);
			sm.setShadowDrawable(R.drawable.shadow);
			sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			sm.setFadeDegree(0.35f);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		    sm.setSlidingEnabled(true);
		    sm.setOnOpenListener(new OnOpenListener() {
				
				@Override
				public void onOpen() {
					hideSoftKeyboard();					
				}
			});
		        		    
		    
		    actionBar.setDisplayHomeAsUpEnabled(true);
		    TABLET = false;//tablet
		}
		
		mFragment = new Board();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragment_main_container, mFragment);
		t.addToBackStack(mFragment.getClass().getSimpleName());
		t.commit();
		
		setTextVersion();
	}
	
	public void setTextVersion() {
		try {
			version_app = (TextView) findViewById(R.id.version_app);
			version_app.setText(getString(R.string.version_app) + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
			
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
			int itemId = item.getItemId();
		    switch (itemId) {
		    case android.R.id.home:
		        toggle();
		        break;
		    }
		    
		    return true;
		}
		
	public void changeFragment(View v) {
		
		hideSoftKeyboard();

		switch (v.getId()) {
		case R.id.menu_item_board:
			mFragment = new Board();
			break;

		case R.id.menu_item_add:
			mFragment = new SelectType();
			break;

//			case R.id.menu_item_edit:
//				Log.d("changeFragment", "Agrega el fragment al menu!!!");
//				mTitle = "...";
//				break;
//	
//			case R.id.menu_item_info:
//				Log.d("changeFragment", "Agrega el fragment al menu!!!");
//				break;

		default:
			mFragment = new Board();
			break;
		}
		
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragment_main_container, mFragment);
		t.addToBackStack(mFragment.getClass().getSimpleName());
		m.popBackStack();
		t.commit();
		
		if (!TABLET) {
			toggle();	
		}		
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		FragmentManager m = getSupportFragmentManager();
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	    	if (m.getBackStackEntryCount() <= 1) {
	    		mFragment = new Board();
	    		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
	    		t.replace(R.id.fragment_main_container, mFragment);
	    		t.addToBackStack(mFragment.getClass().getSimpleName());
	    		m.popBackStack();
	    		t.commit();
	    			    		
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
		        
		        return true;
		        
			} else {
				return super.onKeyDown(keyCode, event);	
			}
			
	    } else {
	        return super.onKeyDown(keyCode, event);
	    }
	}
	
	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);	
		}
    }
}
