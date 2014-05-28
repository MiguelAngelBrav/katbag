/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cl.ipp.katbag.fragment;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagDrawing;
import cl.ipp.katbag.core.KatbagDrawingBuilder;

public class SlidePageBookFragment extends Fragment {

	public ViewGroup v;
	public static MainActivity mainActivity;
	public static final String ID_APP = "id_app";
	public static final String PAGE_NUMBER = "page";
	public ArrayList<String> page = new ArrayList<String>();
	public ArrayList<String> world = new ArrayList<String>();
	public String mCurrentPhotoPath = "";
	public TextView text;
	public RelativeLayout backgroundView;
	public boolean mMeasured = false;
	public int[] loc;
	private ArrayList<String> develop = new ArrayList<String>();
	private ArrayList<Integer> drawingList = new ArrayList<Integer>();

	public static final int PAGE_WORLD_ID = 0;
	public static final int PAGE_SOUND_ID = 1;
	public static final int PAGE_TEXT = 2;
	public static final int PAGE_TEXT_SIZE = 3;
	public static final int PAGE_TEXT_ALIGN = 4;
	public static final int PAGE_TEXT_COLOR = 5;
	public static final int PAGE_ORDER = 6;
	public static final int PAGE_ID = 7;

	public static final int WORLD_ARRAY_TYPE = 0;
	public static final int WORLD_ARRAY_SRC = 1;
	public static final int WORLD_ARRAY_SCALE_FACTOR = 2;

	public static final String WORLD_TYPE_COLOR = "color";
	public static final String WORLD_TYPE_CAMERA = "camera";
	public static final String WORLD_TYPE_LIBRARY = "library";

	private int mPageNumber;
	private long id_app;

	public static SlidePageBookFragment create(long id_app, int pageNumber) {
		SlidePageBookFragment fragment = new SlidePageBookFragment();
		Bundle args = new Bundle();
		args.putLong(ID_APP, id_app);
		args.putInt(PAGE_NUMBER, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public SlidePageBookFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		id_app = getArguments().getLong(ID_APP);
		mPageNumber = getArguments().getInt(PAGE_NUMBER);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		v = (ViewGroup) inflater.inflate(R.layout.fragment_slide_page_book, container, false);
		mainActivity = (MainActivity) super.getActivity();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		backgroundView = (RelativeLayout) v.findViewById(R.id.one_page_background);
		backgroundView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (!mMeasured) {
					loc = new int[2];
					backgroundView.getLocationInWindow(loc);
					mMeasured = true;
					setDevelopBook();
				}
			}
		});

		text = (TextView) v.findViewById(R.id.slider_page_text);
		Typeface face = Typeface.createFromAsset(mainActivity.getAssets(), getString(R.string.one_page_default_font));
		text.setTypeface(face);

		page.clear();
		page = mainActivity.katbagHandler.selectOnePageForIdAndOrder(id_app, mPageNumber);

		if (page.size() > 0) {
			if (page.get(PAGE_TEXT) != null)
				text.setText(page.get(PAGE_TEXT));

			if (page.get(PAGE_WORLD_ID) != null)
				setWorld(Long.parseLong(page.get(PAGE_WORLD_ID)));

			if (page.get(PAGE_TEXT_ALIGN) != null)
				setAlignText(page.get(PAGE_TEXT_ALIGN));

			if (page.get(PAGE_TEXT_SIZE) != null)
				setFontSize(Integer.valueOf(page.get(PAGE_TEXT_SIZE)));

			if (page.get(PAGE_TEXT_COLOR) != null)
				setColorText(Integer.valueOf(page.get(PAGE_TEXT_COLOR)));
		}
	}

	public void setDevelopBook() {
		drawingList.clear();

		develop.clear();
		develop = mainActivity.katbagHandler.selectDevelopBookForIdAppAndPageId(id_app, Integer.parseInt(page.get(PAGE_ID)));
		for (int i = 0; i < develop.size(); i++) {
			String[] line = develop.get(i).split("&&");

			if (line[1].contentEquals("drawing")) {
				setDrawing(line);
			}
		}

		for (int i = 0; i < develop.size(); i++) {
			String[] line = develop.get(i).split("&&");

			if (line[1].contentEquals("motion")) {
				KatbagDrawing drawingMove = (KatbagDrawing) backgroundView.findViewById(Integer.parseInt(line[4]));
				if (drawingMove != null)
					drawingMove.moveToXY(Integer.parseInt(line[5]), Integer.parseInt(line[6]));
			}
		}
	}

	public void setDrawing(String[] line) {
		boolean exist = false;
		ArrayList<String> drw = new ArrayList<String>();
		drw = mainActivity.katbagHandler.selectDrawingsForIdApp(id_app);
		for (int i = 0; i < drw.size(); i++) {
			if (drw.get(i).contentEquals(line[3])) {
				exist = true;
				break;
			}
		}

		if (exist) {
			KatbagDrawingBuilder drawingBuilder = new KatbagDrawingBuilder(mainActivity.context);
			drawingBuilder.setIdDrawing(Long.parseLong(line[3])); // this build
																	// the
																	// drawing
			Bitmap bitmap = createBitmapFromRelativeLayout(drawingBuilder);
			KatbagDrawing drawing = new KatbagDrawing(mainActivity.context);
			drawing.setImageBitmap(bitmap);
			drawing.setId(Integer.parseInt(line[3]));
			drawing.setSizeFather(backgroundView.getWidth(), backgroundView.getHeight(), loc, backgroundView);
			drawing.setMySize(drawingBuilder.getMyWidth(), drawingBuilder.getMyHeight());
			drawing.setScaleType(ScaleType.MATRIX);
			backgroundView.addView(drawing);
			drawingList.add(Integer.parseInt(line[3]));
		}
	}

	public Bitmap createBitmapFromRelativeLayout(RelativeLayout view) {

		view.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas c = new Canvas(bitmap);
		view.draw(c);

		return bitmap;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}

	public void setColorText(int color) {
		text.setTextColor(color);
	}

	public void setFontSize(int size) {
		text.setTextSize(size);
	}

	public void setAlignText(String align) {
		if (align.contentEquals("left"))
			text.setGravity(Gravity.TOP | Gravity.LEFT);
		else if (align.contentEquals("center"))
			text.setGravity(Gravity.TOP | Gravity.CENTER);
		else if (align.contentEquals("right"))
			text.setGravity(Gravity.TOP | Gravity.RIGHT);
	}

	public void setWorld(long id_world) {
		world.clear();
		world = mainActivity.katbagHandler.selectWorldTypeSrcAndScaleFactorWorldForId(id_world);
		if (world.size() > 0) {
			if (world.get(WORLD_ARRAY_TYPE).contentEquals(WORLD_TYPE_COLOR)) {
				setColorBackground(Integer.parseInt(world.get(WORLD_ARRAY_SRC)));
			} else if (world.get(WORLD_ARRAY_TYPE).contentEquals(WORLD_TYPE_CAMERA) || world.get(WORLD_ARRAY_TYPE).contentEquals(WORLD_TYPE_LIBRARY)) {
				mCurrentPhotoPath = world.get(WORLD_ARRAY_SRC);
				setPictureBackground(world.get(WORLD_ARRAY_TYPE), Integer.parseInt(world.get(WORLD_ARRAY_SCALE_FACTOR)), id_world);
			}
		}
	}

	public void setColorBackground(int color) {
		backgroundView.setBackgroundColor(color);
	}

	@SuppressWarnings("deprecation")
	public void setPictureBackground(String type_world, int scaleFactor, long id_world) {
		// Get the dimensions of the View
		int targetW = backgroundView.getWidth();
		int targetH = backgroundView.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		if (scaleFactor == -1) {
			if (photoW != 0 && targetW != 0 && photoH != 0 && targetH != 0)
				scaleFactor = Math.min(photoW / targetW, photoH / targetH);
			else
				scaleFactor = 1;

			mainActivity.katbagHandler.updateWorld(id_world, type_world, mCurrentPhotoPath, scaleFactor);
		}

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();

			try {
				bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
			} catch (OutOfMemoryError e2) {
				e2.printStackTrace();
				// handle gracefully.
			}
		}

		BitmapDrawable background = new BitmapDrawable(mainActivity.context.getResources(), bitmap);

		backgroundView.setBackgroundDrawable(background);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		System.gc();
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		System.gc();
		super.onStop();
	}
}