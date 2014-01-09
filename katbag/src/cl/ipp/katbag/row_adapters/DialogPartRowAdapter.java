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

public class DialogPartRowAdapter extends ArrayAdapter<String> {

	private List<String> items;
	private List<String> names;
	private Context context;

	public DialogPartRowAdapter(Context context, int layoutResourceId, List<String> items, List<String> names) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.items = items;
		this.names = names;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_dialog_part, parent, false);	
		}
			
		TextView id = (TextView) v.findViewById(R.id.dialog_part_row_id);
		if (id != null) {
			id.setText(items.get(position));
		}
		
		TextView name = (TextView) v.findViewById(R.id.dialog_part_row_name);
		if (name != null) {
			name.setText(names.get(position));
		}
		
		ImageView image = (ImageView) v.findViewById(R.id.dialog_part_image);
		if (image != null) {
			int identifier = context.getResources().getIdentifier(items.get(position) + "_thumb", "drawable", context.getPackageName());
			image.setImageResource(identifier);
		}
		
		return v;
	}
}
