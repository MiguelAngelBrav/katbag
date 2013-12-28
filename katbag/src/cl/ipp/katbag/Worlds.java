package cl.ipp.katbag;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class Worlds extends SherlockFragment {
	
	static View v = null;
	public MainActivity mainActivity;
	public MenuItem menuItemNew, menuItemDuplicate; 
	public MenuItem mItemNew, mItemDuplicate;

	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainActivity = (MainActivity) super.getActivity();
		v = inflater.inflate(R.layout.fragment_worlds, container, false);
		mainActivity.supportInvalidateOptionsMenu();		
		
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.world, menu);
		mItemNew = menu.findItem(R.id.worlds_dropdown_menu_new);
		mItemDuplicate = menu.findItem(R.id.worlds_dropdown_menu_duplicate);
		
		mItemNew.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				newWorld();
				return true;
			}
		});
		
		mItemDuplicate.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				duplicateWorld();
				return true;
			}
		});
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	public static void newWorld() {
		Log.d("newWorld", "newWorld!!");
	}
	
	public static void duplicateWorld() {
		Log.d("duplicateWorld", "duplicateWorld!!");
	}
}
