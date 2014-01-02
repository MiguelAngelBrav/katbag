package cl.ipp.katbag.row_adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cl.ipp.katbag.R;

public class DrawingsRowAdapter extends ArrayAdapter<String> {

	private List<String> items;
	private Context context;

	public DrawingsRowAdapter(Context context, int layoutResourceId, List<String> items) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.items = items;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_drawings, parent, false);	
		}
		
		TextView id = (TextView) v.findViewById(R.id.drawing_row_id);
		if (id != null) {
			id.setText(items.get(position));
		}
		
		TextView name = (TextView) v.findViewById(R.id.drawing_row_name);
		if (name != null) {
			name.setText(context.getString(R.string.drawings_row_name) + " " + items.get(position));
		}
				
		
		return v;
	}
}