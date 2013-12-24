package cl.ipp.katbag;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Add extends Fragment {
	
	static View v = null;
	public JSONObject app;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_add, container, false);
		
		Bundle bundle = getArguments();
        if(bundle != null){
        	try {
				app = new JSONObject(bundle.getString("NewApp"));
				Log.d("onCreateView", "JSONObject: " + app);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		return v;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_add);
	}
}
