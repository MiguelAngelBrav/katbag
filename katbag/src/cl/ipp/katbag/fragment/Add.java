package cl.ipp.katbag.fragment;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagUtilities;

public class Add extends Fragment implements OnClickListener {
	
	static View v = null;
	public String type_app;
	public EditText name_app;
	public String title;
	public ImageView image_type_app;
	public static long id_app = -1; // initial value
	public static String name_app_text = "";
	public MainActivity mainActivity;
	public LinearLayout config_app_worlds, config_app_drawings, config_app_developments;
	public Fragment mFragment;
	public static final int MAX_LENGTH = 30;
	public ProgressBar progress;
	public int score_id_and_name = 0, score = 0;
	public static final int SCORE_FOR_HAVE_ID_AND_NAME = 20;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainActivity = (MainActivity) super.getActivity();
		v = inflater.inflate(R.layout.fragment_add, container, false);
				
		// rescues parameters
		Bundle bundle = getArguments();
        if(bundle != null){
    		type_app = bundle.getString("type_app");
        }
        
        progress = (ProgressBar) v.findViewById(R.id.progress);
        
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
        name_app.setFilters(new InputFilter[]{KatbagUtilities.katbagAlphaNumericFilter, new InputFilter.LengthFilter(MAX_LENGTH)});
        name_app.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                	name_app.setText(name_app.getText().toString().trim());  
                	if (name_app.getText().toString().length() > MAX_LENGTH) {
                		name_app.setText(name_app.getText().toString().substring(0, MAX_LENGTH).trim());
					}
                	  
                	if (name_app.getText().toString().contentEquals("")) {
                		KatbagUtilities.message(mainActivity.context, getString(R.string.name_app_empty));
                	
                	} else if (name_app.getText().toString().length() < 3) {
                    		KatbagUtilities.message(mainActivity.context, getString(R.string.name_app_short));
                    		
					} else {
	                	Log.d("onEditorAction", "id_app: " + id_app);
	                	if (id_app == -1) { // insert new register
	                    	id_app = mainActivity.katbagHandler.insertApp(name_app.getText().toString(), type_app);
	                    	KatbagUtilities.message(mainActivity.context, getString(R.string.name_app_new) + " (id app: " + String.valueOf(id_app) + ")");
						} else { // update new register
							mainActivity.katbagHandler.updateNameApp(id_app, name_app.getText().toString());
							KatbagUtilities.message(mainActivity.context, getString(R.string.name_app_update) + " (id app: " + String.valueOf(id_app) + ")");
						}
	                	
	                	name_app_text = name_app.getText().toString();
	                	
	                	score_id_and_name = SCORE_FOR_HAVE_ID_AND_NAME;
	                	estimatedProgress();
					}
                }
                return false;
            }
        });
	}
	
	@Override
	public void onClick(View v) {
		
		mainActivity.hideSoftKeyboard();
		
		if (id_app == -1) {
			KatbagUtilities.message(mainActivity.context, getString(R.string.app_empty));
			
		} else {			
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
			
			Bundle bundle = new Bundle();
			bundle.putLong("id_app", id_app);
			mFragment.setArguments(bundle);
			
			FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
			t.replace(R.id.fragment_main_container, mFragment);
			t.addToBackStack(mFragment.getClass().getSimpleName());
			t.commit();
		}
	}
	
	public void estimatedProgress() {
		if (id_app == -1) {
			score_id_and_name = 0;
        	score = 0;
		} else {
        	score_id_and_name = SCORE_FOR_HAVE_ID_AND_NAME;
        	score = mainActivity.katbagHandler.estimatedProgress(String.valueOf(id_app)); 
		}
	    
	    progress.setProgress(score_id_and_name + score);		
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);
	    name_app.setText(name_app_text);

	    estimatedProgress();
	    
	    Log.d("onResume", "id_app: " + id_app);
	}
}
