/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 * The Android Open Source Project Katbag is licensed under the General GPLv3.
 * 
 */

package cl.ipp.katbag.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cl.ipp.katbag.MainActivity;

public class KatbagDrawingBuilder extends RelativeLayout {

	private RelativeLayout v;
	private long id_drawing = -1;
	private List<String> parts = new ArrayList<String>();
	private MainActivity mainActivity;
	public int myWidth = 0, myHeight = 0;

	public KatbagDrawingBuilder(Context context) {
		super(context);
		mainActivity = (MainActivity) context;
		v = this;
	}

	public void setIdDrawing(long id) {
		id_drawing = id;
		createDrawing();
	}

	public long getIdDrawing() {
		return id_drawing;
	}

	public int getMyWidth() {
		return myWidth;
	}

	public int getMyHeight() {
		return myHeight;
	}

	private void createDrawing() {
		parts = mainActivity.katbagHandler.selectDrawingsPartsForIdApp(id_drawing);
		if (parts.size() > 0) {
			String[] part = parts.get(0).split("&&");

			int minTop = Integer.parseInt(part[2]);
			int minLeft = Integer.parseInt(part[3]);
			int maxWidth = (Integer.parseInt(part[3]) + Integer.parseInt(part[4]));
			int maxHeight = (Integer.parseInt(part[2]) + Integer.parseInt(part[5]));
			for (int i = 1; i < parts.size(); i++) {
				part = parts.get(i).split("&&");
				if (minTop > Integer.parseInt(part[2]))
					minTop = Integer.parseInt(part[2]);
				if (minLeft > Integer.parseInt(part[3]))
					minLeft = Integer.parseInt(part[3]);
				if (maxWidth < (Integer.parseInt(part[3]) + Integer.parseInt(part[4])))
					maxWidth = (Integer.parseInt(part[3]) + Integer.parseInt(part[4]));
				if (maxHeight < (Integer.parseInt(part[2]) + Integer.parseInt(part[5])))
					maxHeight = (Integer.parseInt(part[2]) + Integer.parseInt(part[5]));
			}

			myWidth = (maxWidth - minLeft);
			myHeight = (maxHeight - minTop);

			for (int i = 0; i < parts.size(); i++) {
				part = parts.get(i).split("&&");
				ImageView image = addPart(part[1], Integer.parseInt(part[0]));

				RelativeLayout.LayoutParams mLayoutSize = new RelativeLayout.LayoutParams(Integer.parseInt(part[4]), Integer.parseInt(part[5]));
				mLayoutSize.topMargin = (Integer.parseInt(part[2]) - minTop);
				mLayoutSize.leftMargin = (Integer.parseInt(part[3]) - minLeft);
				image.setLayoutParams(mLayoutSize);
				if (Integer.parseInt(part[6]) > 0)
					rotate(image, Integer.parseInt(part[6]));
				v.addView(image);
			}
		}
	}

	public ImageView addPart(String mPart, int idImageView) {
		ImageView part = new ImageView(mainActivity.context);
		int identifier = mainActivity.context.getResources().getIdentifier(mPart, "drawable", mainActivity.context.getPackageName());
		part.setImageResource(identifier);
		part.setId(idImageView);
		part.setTag(mPart);
		part.setAdjustViewBounds(true);
		return part;
	}

	public void rotate(ImageView part, int rotate) {
		if (part != null) {
			part.clearAnimation();
			Matrix matrix = part.getImageMatrix();
			RectF dst = new RectF();
			matrix.mapRect(dst, new RectF(part.getDrawable().getBounds()));
			RotateAnimation mAnimation = new RotateAnimation(0, rotate, part.getWidth() / 2, part.getHeight() / 2);
			mAnimation.setFillEnabled(true);
			mAnimation.setFillAfter(true);
			part.startAnimation(mAnimation);
			part.setImageMatrix(matrix);
		}
	}

}