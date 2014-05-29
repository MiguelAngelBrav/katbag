package cl.ipp.katbag.row_adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;

public class BoardRowAdapter extends ArrayAdapter<String> {

	private List<String> items;
	private Context context;
	private String[] array;

	public BoardRowAdapter(Context context, int layoutResourceId, List<String> items) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_board, parent, false);
		}

		array = items.get(position).toString().split("&&");
		String mId = array[0];
		String mName = array[1];
		String mTypeApp = array[2];

		ImageView image = (ImageView) v.findViewById(R.id.board_row_image);
		if (mTypeApp.contentEquals(MainActivity.TYPE_APP_GAME)) {
			image.setImageResource(R.drawable.katbag_icon_game);
		} else if (mTypeApp.contentEquals(MainActivity.TYPE_APP_BOOK)) {
			image.setImageResource(R.drawable.katbag_icon_book);
		} else if (mTypeApp.contentEquals(MainActivity.TYPE_APP_COMICS)) {
			image.setImageResource(R.drawable.katbag_icon_comics);
		}

		TextView name = (TextView) v.findViewById(R.id.board_row_name);
		if (name != null) {
			name.setText(mName);
		}

		TextView id = (TextView) v.findViewById(R.id.board_row_id);
		if (id != null) {
			id.setText(mId);
		}

		TextView type = (TextView) v.findViewById(R.id.board_row_type);
		if (type != null) {
			type.setText(mTypeApp);
		}

		return v;
	}
}
