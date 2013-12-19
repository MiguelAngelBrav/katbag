package cl.ipp.katbag;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.ActionBar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends RootActivity {
	protected Fragment mainFrag, menuFrag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.fragment_activity_menu);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		
		SlidingMenu sm = getSlidingMenu();
		
		// si es tablet, no se despliega el menu slider		
		if (findViewById(R.id.fragment_menu_container) != null) {
			sm.setSlidingEnabled(false);     
		    actionBar.setDisplayHomeAsUpEnabled(false);
		    
		} else { // si es un smartphone, se muestra el menu slider
			sm.setShadowWidthRes(R.dimen.shadow_width);
			sm.setShadowDrawable(R.drawable.shadow);
			sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			sm.setFadeDegree(0.35f);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		    sm.setSlidingEnabled(true);     
		    actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

}
