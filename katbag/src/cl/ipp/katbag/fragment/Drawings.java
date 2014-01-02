package cl.ipp.katbag.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagHandlerSqlite;
import cl.ipp.katbag.row_adapters.DrawingsRowAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.mobeta.android.dslv.DragSortListView;

public class Drawings extends SherlockFragment {
	
	static View v = null;
	public static MainActivity mainActivity;
	public MenuItem menuItemEdit, menuItemNew; 
	public static long id_app = -1;
	public TextView notRegister;
	public DragSortListView drawingsListView;
	public DrawingsRowAdapter adapter;
	public boolean editMode = false;
	protected KatbagHandlerSqlite handler;

	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainActivity = (MainActivity) super.getActivity();
		v = inflater.inflate(R.layout.fragment_drawings, container, false);
		mainActivity.supportInvalidateOptionsMenu();
		
		handler = new KatbagHandlerSqlite(mainActivity.getBaseContext());
		
		// rescues parameters
		Bundle bundle = getArguments();
        if(bundle != null){
    		id_app = bundle.getLong("id_app");
        }
        
        notRegister = (TextView) v.findViewById(R.id.drawings_not_register); 
        loadListView();
        
		return v;
	}
	
	public void loadListView() {	
		drawingsListView = (DragSortListView) v.findViewById(R.id.drawings_list_view);
		List<String> idList = new ArrayList<String>();
		idList.clear();
		
		idList = handler.selectDrawingsForIdApp(id_app);
			
		if (idList.size() <= 0) {
			notRegister.setVisibility(View.VISIBLE);
			
		} else {
			notRegister.setVisibility(View.GONE);

			adapter = new DrawingsRowAdapter(
					v.getContext(), 
					R.layout.row_drawings, 
					idList);
			
			drawingsListView.setRemoveListener(onRemove);
			drawingsListView.setAdapter(adapter);	
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.drawing, menu);
		menuItemNew = menu.findItem(R.id.drawings_dropdown_menu_new);
		menuItemEdit = menu.findItem(R.id.drawings_dropdown_menu_edit);
		
		menuItemEdit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				editMode();
				return true;
			}
		});
		
		menuItemNew.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				editMode = false;
				menuItemEdit.setIcon(R.drawable.ic_action_edit);
				handler.insertDrawing(id_app, "#006699");
				loadListView();
				return true;
			}
		});
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	public final void editMode() {
		Log.d("editMode", "editMode: " + editMode);
		
		View vc;
		ImageView arrow;
		ImageView remove;
		
		if (editMode) {
			editMode = false;
			menuItemEdit.setIcon(R.drawable.ic_action_edit);
		} else {
			editMode = true;
			menuItemEdit.setIcon(R.drawable.ic_action_accept);
		}
			
		if (drawingsListView.getChildCount() == 0) {
			loadListView();
			
		} else {
			for (int i = 0; i < drawingsListView.getChildCount(); i++) {
				vc = (View) drawingsListView.getChildAt(i);
				arrow = (ImageView) vc.findViewById(R.id.drawing_row_image_arrow);
				remove = (ImageView) vc.findViewById(R.id.drawing_row_image_remove);
				
				if (editMode) {
					arrow.setVisibility(View.GONE);
					remove.setVisibility(View.VISIBLE);
				} else {
					arrow.setVisibility(View.VISIBLE);
					remove.setVisibility(View.GONE);
				}
			}	
		}
	}
		
	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			Log.d("remove", "remove!!");
			String item = (String) adapter.getItem(which);
					
			View v = (View) drawingsListView.getChildAt(which);
			TextView removeDrawing = (TextView) v.findViewById(R.id.drawing_row_id);
			adapter.remove(item);
			
			handler.deleteDrawingForId(removeDrawing.getText().toString());
		}
	};
		
	@Override
	public void onResume() {
	    super.onResume();
	    ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_drawings);
	}
}
