package cl.ipp.katbag.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagHandlerSqlite;
import cl.ipp.katbag.row_adapters.BoardRowAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.mobeta.android.dslv.DragSortListView;

public class Board extends SherlockFragment {
	
	static View v = null;
	public DragSortListView boardListView;
	protected KatbagHandlerSqlite handler;
	public static MainActivity mainActivity;
	public TextView notRegister;
	public BoardRowAdapter adapter;	
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_board, container, false);
		mainActivity = (MainActivity) super.getActivity();
		
		handler = new KatbagHandlerSqlite(mainActivity.getBaseContext());
		notRegister = (TextView) v.findViewById(R.id.board_not_register); 
        loadListView();
		
		return v;
	}
	
	public void loadListView() {	
		boardListView = (DragSortListView) v.findViewById(R.id.board_list_view);
		List<String> idList = new ArrayList<String>();
		idList.clear();
		
		idList = handler.selectAllApps();
			
		if (idList.size() <= 0) {
			notRegister.setVisibility(View.VISIBLE);
			
		} else {		
			notRegister.setVisibility(View.GONE);

			adapter = new BoardRowAdapter(
					v.getContext(), 
					R.layout.row_board, 
					idList);
			
			boardListView.setAdapter(adapter);	
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_board);
	}
}