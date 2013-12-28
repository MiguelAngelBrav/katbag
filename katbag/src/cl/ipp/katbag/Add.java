package cl.ipp.katbag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cl.ipp.katbag.core.KatbagHandlerSqlite;
import cl.ipp.katbag.core.KatbagUtilities;

public class Add extends Fragment implements OnClickListener {
	
	static View v = null;
	public String type_app;
	public EditText name_app;
	public String title;
	public ImageView image_type_app;
	public static long id_app;
	public MainActivity mainActivity;
	public LinearLayout config_app_worlds, config_app_drawings, config_app_developments;
	public Fragment mFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainActivity = (MainActivity) super.getActivity();
		v = inflater.inflate(R.layout.fragment_add, container, false);
		
		// initial value
		id_app = -1;
		
		// rescues parameters
		Bundle bundle = getArguments();
        if(bundle != null){
    		type_app = bundle.getString("type_app");
        }
        
        setTitleAndImageForTypeApp();
        setEditTextNameApp();        

        config_app_worlds = (LinearLayout) v.findViewById(R.id.config_app_worlds);
        config_app_drawings = (LinearLayout) v.findViewById(R.id.config_app_drawings);
        config_app_developments = (LinearLayout) v.findViewById(R.id.config_app_developments);
        
        config_app_worlds.setOnClickListener(this);
        config_app_drawings.setOnClickListener(this);
        config_app_developments.setOnClickListener(this);
        
		return v;
	}
	
	// set title and image for type app
	public void setTitleAndImageForTypeApp() {
        image_type_app = (ImageView) v.findViewById(R.id.config_image_type_app);
        
        if (type_app.contentEquals("game")) {
			title = getString(R.string.title_activity_add_game);
			image_type_app.setImageResource(R.drawable.katbag_icon_game);
		} else if (type_app.contentEquals("book")) {
			title = getString(R.string.title_activity_add_book);
			image_type_app.setImageResource(R.drawable.katbag_icon_book);
		} else if (type_app.contentEquals("comic")) {
			title = getString(R.string.title_activity_add_comic);
			image_type_app.setImageResource(R.drawable.katbag_icon_comic);
		}
	}
	
	// name app
	public void setEditTextNameApp() {
        name_app = (EditText) v.findViewById(R.id.name_app);
        name_app.setFilters(new InputFilter[]{KatbagUtilities.katbagAlphaNumericFilter});
        name_app.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                	if (name_app.getText().toString().contentEquals("")) {
                		KatbagUtilities.message(mainActivity.getBaseContext(), getString(R.string.name_app_empty));
                		
					} else {
	                	KatbagHandlerSqlite handler = new KatbagHandlerSqlite(getActivity().getBaseContext());
	                	Log.d("onEditorAction", "id_app: " + id_app);
	                	if (id_app == -1) { // insert new register
	                    	id_app = handler.insertApp(name_app.getText().toString(), type_app);                    	
						} else { // update new register
							handler.updateNameApp(id_app, name_app.getText().toString());
						}
					}
                }
                return false;
            }
        });
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);
	    Log.d("onResume", "id_app: " + id_app);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.config_app_worlds:
			mFragment = new Worlds();
			break;

		case R.id.config_app_drawings:
			mFragment = new Drawings();
			break;
			
		case R.id.config_app_developments:
			mFragment = new Developments();
			break;
		}
		
		FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragment_main_container, mFragment);
		t.addToBackStack(mFragment.getClass().getSimpleName());
		t.commit();
	}
}
