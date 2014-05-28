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
import cl.ipp.katbag.fragment.Worlds;

public class WorldsRowAdapter extends ArrayAdapter<String> {

	private List<String> items;
	private Context context;

	public WorldsRowAdapter(Context context, int layoutResourceId, List<String> items) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_worlds, parent, false);
		}

		TextView id = (TextView) v.findViewById(R.id.world_row_id);
		if (id != null) {
			id.setText(items.get(position));
		}

		TextView name = (TextView) v.findViewById(R.id.world_row_name);
		if (name != null) {
			name.setText(context.getString(R.string.worlds_row_name) + " " + items.get(position));
		}

		ImageView arrow = (ImageView) v.findViewById(R.id.world_row_image_arrow);
		ImageView remove = (ImageView) v.findViewById(R.id.world_row_image_remove);

		if (Worlds.editMode) {
			arrow.setVisibility(View.GONE);
			remove.setVisibility(View.VISIBLE);
		} else {
			arrow.setVisibility(View.VISIBLE);
			remove.setVisibility(View.GONE);
		}

		return v;
	}
}
