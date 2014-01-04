package cl.ipp.katbag.row_adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cl.ipp.katbag.R;
import cl.ipp.katbag.fragment.Edit;

public class EditRowAdapter extends ArrayAdapter<String> {

	private List<String> items;
	private Context context;
	private String[] array;

	public EditRowAdapter(Context context, int layoutResourceId, List<String> items) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.items = items;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_edit, parent, false);	
		}
		
		array = items.get(position).toString().split("&&");
		String mId = array[0];
		String mName = array[1];
		String mTypeApp = array[2];
		
		ImageView image = (ImageView) v.findViewById(R.id.edit_row_image);
		if (mTypeApp.contentEquals("game")) {
			image.setImageResource(R.drawable.katbag_icon_game);
		} else if (mTypeApp.contentEquals("book")) {
			image.setImageResource(R.drawable.katbag_icon_book);
		} else if (mTypeApp.contentEquals("comic")) {
			image.setImageResource(R.drawable.katbag_icon_comic);
		}
		
		TextView name = (TextView) v.findViewById(R.id.edit_row_name);
		if (name != null) {
			name.setText(mName);
		}
		
		TextView id = (TextView) v.findViewById(R.id.edit_row_id);
		if (id != null) {
			id.setText(mId);
		}
		
		TextView type_app = (TextView) v.findViewById(R.id.edit_row_type_app);
		if (type_app != null) {
			type_app.setText(mTypeApp);
		}
		
		ImageView arrow = (ImageView) v.findViewById(R.id.edit_row_image_arrow);
		ImageView remove = (ImageView) v.findViewById(R.id.edit_row_image_remove);
		
		if (Edit.editMode) {
			arrow.setVisibility(View.GONE);
			remove.setVisibility(View.VISIBLE);
		} else {
			arrow.setVisibility(View.VISIBLE);
			remove.setVisibility(View.GONE);
		}
				
		
		return v;
	}
}
