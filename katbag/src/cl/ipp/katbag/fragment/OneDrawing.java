/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 *  
 * Copyright (C) 2014 The Android Open Source Project Katbag of IPP and Miguel Angel Bravo
 * Licensed under the Apache 2.0 License.
 */

package cl.ipp.katbag.fragment;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.AlertDialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagUtilities;
import cl.ipp.katbag.row_adapters.DialogPartRowAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class OneDrawing extends SherlockFragment {

	static View v = null;
	public MainActivity mainActivity;
	public long id_drawing = -1;
	public String type_app = "";
	public String name_drawing;
	public MenuItem menuItemBody, menuItemHat, menuItemWig, menuItemEyes, menuItemGlasses, menuItemMouth, menuItemAccesory,
			menuItemComicsAction, menuItemComicsBalloonText, menuItemComicsObject;

	public static final String PART_BODY = "katbag_monster_body_";
	public static final String PART_HAT = "katbag_monster_hat_";
	public static final String PART_WIG = "katbag_monster_wig_";
	public static final String PART_EYES = "katbag_monster_eyes_";
	public static final String PART_GLASSES = "katbag_monster_glasses_";
	public static final String PART_MOUTH = "katbag_monster_mouth_";
	public static final String PART_ACCESORY = "katbag_monster_accesory_";
	public static final String PART_COMICS_ACTION = "katbag_comics_action_";
	public static final String PART_COMICS_BALLOON_TEXT = "katbag_comics_balloon_text_";
	public static final String PART_COMICS_OBJECT = "katbag_comics_object_";

	public static final int ELEMENT_LIST_BODY = 11;
	public static final int ELEMENT_LIST_HAT = 2;
	public static final int ELEMENT_LIST_WIG = 2;
	public static final int ELEMENT_LIST_EYES = 26;
	public static final int ELEMENT_LIST_GLASSES = 4;
	public static final int ELEMENT_LIST_MOUTH = 11;
	public static final int ELEMENT_LIST_ACCESORY = 2;
	public static final int ELEMENT_LIST_COMICS_ACTION = 26;
	public static final int ELEMENT_LIST_COMICS_BALLOON_TEXT = 16;
	public static final int ELEMENT_LIST_COMICS_OBJECT = 7;

	public DialogPartRowAdapter adapter;
	public RelativeLayout one_drawing;
	public int identifier = 0;

	public ImageView bringToFront, expand, contract, trash;
	public int[] loc;
	public ImageView imageViewLive = null;
	public boolean body = false; // you can only use a body

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainActivity = (MainActivity) super.getActivity();
		v = inflater.inflate(R.layout.fragment_one_drawing, container, false);

		one_drawing = (RelativeLayout) v.findViewById(R.id.one_drawing);
		loc = new int[2];
		one_drawing.getLocationInWindow(loc);
		one_drawing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				removePropertiesParts();
			}
		});

		bringToFront = (ImageView) v.findViewById(R.id.toolbar_part_bringtofront);
		expand = (ImageView) v.findViewById(R.id.toolbar_part_expand);
		contract = (ImageView) v.findViewById(R.id.toolbar_part_contract);
		trash = (ImageView) v.findViewById(R.id.toolbar_part_trash);

		bringToFront.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				bringToFront();
			}
		});

		expand.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeSize("expand");
			}
		});

		contract.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changeSize("contract");
			}
		});

		trash.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				trash();
			}
		});

		// rescues parameters
		List<String> parts = new ArrayList<String>();
		Bundle bundle = getArguments();
		if (bundle != null) {
			id_drawing = bundle.getLong("id_drawing");
			name_drawing = bundle.getString("name_drawing");
			type_app = bundle.getString("type_app");

			parts = mainActivity.katbagHandler.selectDrawingsPartsForIdApp(id_drawing);
			for (int i = 0; i < parts.size(); i++) {
				String[] part = parts.get(i).split("&&");
				imageViewLive = addPart(part[1], Integer.parseInt(part[0]));

				RelativeLayout.LayoutParams mLayoutSize = null;
				mLayoutSize = new RelativeLayout.LayoutParams(Integer.parseInt(part[4]), Integer.parseInt(part[5]));
				mLayoutSize.topMargin = Integer.parseInt(part[2]);
				mLayoutSize.leftMargin = Integer.parseInt(part[3]);
				imageViewLive.setLayoutParams(mLayoutSize);
				one_drawing.invalidate();
				if (Integer.parseInt(part[6]) > 0)
					rotate("load", Integer.parseInt(part[6]));
			}
		}

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.one_drawing, menu);
		menuItemBody = menu.findItem(R.id.one_drawing_dropdown_menu_body);
		menuItemHat = menu.findItem(R.id.one_drawing_dropdown_menu_hat);
		menuItemWig = menu.findItem(R.id.one_drawing_dropdown_menu_wig);
		menuItemEyes = menu.findItem(R.id.one_drawing_dropdown_menu_eyes);
		menuItemGlasses = menu.findItem(R.id.one_drawing_dropdown_menu_glasses);
		menuItemMouth = menu.findItem(R.id.one_drawing_dropdown_menu_mouth);
		menuItemAccesory = menu.findItem(R.id.one_drawing_dropdown_menu_accesory);
		menuItemComicsAction = menu.findItem(R.id.one_drawing_dropdown_menu_comics_action);
		menuItemComicsBalloonText = menu.findItem(R.id.one_drawing_dropdown_menu_comics_balloon_text);
		menuItemComicsObject = menu.findItem(R.id.one_drawing_dropdown_menu_comics_object);
		
		if (!type_app.contentEquals(MainActivity.TYPE_APP_COMICS)) {
			menuItemComicsAction.setVisible(false);
			menuItemComicsBalloonText.setVisible(false);
			menuItemComicsObject.setVisible(false);
		} else {
			menuItemComicsAction.setVisible(true);
			menuItemComicsBalloonText.setVisible(true);
			menuItemComicsObject.setVisible(true);
		}

		menuItemBody.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (body == true) {
					KatbagUtilities.message(mainActivity.context, getString(R.string.one_drawing_message_only_a_body));
				} else {
					dialogSelectPartsFromList(PART_BODY, ELEMENT_LIST_BODY, getString(R.string.one_drawing_name_part_body));
				}

				return true;
			}
		});

		menuItemHat.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dialogSelectPartsFromList(PART_HAT, ELEMENT_LIST_HAT, getString(R.string.one_drawing_name_part_hat));
				return true;
			}
		});

		menuItemWig.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dialogSelectPartsFromList(PART_WIG, ELEMENT_LIST_WIG, getString(R.string.one_drawing_name_part_wig));
				return true;
			}
		});

		menuItemEyes.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dialogSelectPartsFromList(PART_EYES, ELEMENT_LIST_EYES, getString(R.string.one_drawing_name_part_eyes));
				return true;
			}
		});

		menuItemGlasses.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dialogSelectPartsFromList(PART_GLASSES, ELEMENT_LIST_GLASSES, getString(R.string.one_drawing_name_part_glasses));
				return true;
			}
		});

		menuItemMouth.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dialogSelectPartsFromList(PART_MOUTH, ELEMENT_LIST_MOUTH, getString(R.string.one_drawing_name_part_mouth));
				return true;
			}
		});

		menuItemAccesory.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dialogSelectPartsFromList(PART_ACCESORY, ELEMENT_LIST_ACCESORY, getString(R.string.one_drawing_name_part_accesory));
				return true;
			}
		});
		
		menuItemComicsAction.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dialogSelectPartsFromList(PART_COMICS_ACTION, ELEMENT_LIST_COMICS_ACTION, getString(R.string.one_drawing_name_part_comics_action));
				return true;
			}
		});
		
		menuItemComicsBalloonText.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dialogSelectPartsFromList(PART_COMICS_BALLOON_TEXT, ELEMENT_LIST_COMICS_BALLOON_TEXT, getString(R.string.one_drawing_name_part_comics_balloon_text));
				return true;
			}
		});
		
		menuItemComicsObject.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				dialogSelectPartsFromList(PART_COMICS_OBJECT, ELEMENT_LIST_COMICS_OBJECT, getString(R.string.one_drawing_name_part_comics_object));
				return true;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	public void dialogSelectPartsFromList(String part, int total_part, String name_part) {

		final List<String> partList = new ArrayList<String>();
		List<String> namePartList = new ArrayList<String>();

		partList.clear();
		namePartList.clear();
		for (int i = 1; i <= total_part; i++) {
			if (i <= 9) {
				partList.add(part + "0" + String.valueOf(i));
				namePartList.add(name_part + " 0" + String.valueOf(i));
			} else {
				partList.add(part + String.valueOf(i));
				namePartList.add(name_part + " " + String.valueOf(i));
			}
		}

		adapter = new DialogPartRowAdapter(v.getContext(), R.layout.row_dialog_part, partList, namePartList);

		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.context);
		builder.setTitle(getString(R.string.dialog_title_select));
		builder.setAdapter(adapter, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				addPart(partList.get(which), -1);
			}
		});

		builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// :)
			}
		});

		builder.show();
	}

	public ImageView addPart(String mPart, int idImageView) {
		ImageView part = new ImageView(mainActivity.context);
		if (mPart.contains(PART_BODY)) {
			body = true;
		}

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int identifier = mainActivity.context.getResources().getIdentifier(mPart, "drawable", mainActivity.context.getPackageName());
		part.setImageResource(identifier);

		if (idImageView == -1) {
			idImageView = (int) mainActivity.katbagHandler.insertDrawingPart(id_drawing, mPart, 0, 0, part.getWidth(), part.getHeight(), 0, one_drawing.getChildCount());
		}

		part.setId(idImageView);
		part.setTag(mPart);
		part.setAdjustViewBounds(true);
		part.setOnTouchListener(new mOnTouch());
		one_drawing.addView(part, layoutParams);

		return part;
	}

	public void removePropertiesParts() {
		for (int i = 0; i < one_drawing.getChildCount(); i++) {
			View vci = (View) one_drawing.getChildAt(i);
			vci.setBackgroundResource(0);
		}

		imageViewLive = null;
	}

	public class mOnTouch implements View.OnTouchListener {

		int leftIni = 0, topIni = 0, leftMax = 0, topMax = 0;
		private boolean dragging = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				leftIni = (int) event.getRawX() - (loc[0] + v.getLeft());
				topIni = (int) event.getRawY() - (loc[1] + v.getTop());
				leftMax = one_drawing.getWidth() - v.getWidth();
				topMax = one_drawing.getHeight() - v.getHeight();

				removePropertiesParts();
				v.setBackgroundResource(R.drawable.border_one_drawing_part);

				imageViewLive = (ImageView) v;

				dragging = true;

				break;

			case MotionEvent.ACTION_UP:
				mainActivity.katbagHandler.updateDrawingPartPosition(v.getId(), v.getTop(), v.getLeft(), v.getWidth(), v.getHeight());
				dragging = false;

				break;

			case MotionEvent.ACTION_MOVE:
				if (dragging) {
					int left = (int) event.getRawX() - (loc[0] + leftIni);
					int top = (int) event.getRawY() - (loc[1] + topIni);

					if (left < 0)
						left = 0;
					if (top < 0)
						top = 0;

					if (left > leftMax)
						left = leftMax;
					if (top > topMax)
						top = topMax;

					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(v.getWidth(), v.getHeight());
					lp.leftMargin = left;
					lp.topMargin = top;
					v.setLayoutParams(lp);

					one_drawing.invalidate();
				}

				break;

			}

			return dragging;
		}
	}

	public void bringToFront() {
		if (imageViewLive != null) {
			one_drawing.bringChildToFront(imageViewLive);
			one_drawing.invalidate();

			for (int i = 0; i < one_drawing.getChildCount(); i++) {
				mainActivity.katbagHandler.updateDrawingPartOrder(one_drawing.getChildAt(i).getId(), i);
			}
		}
	}

	public void changeSize(String size) {
		if (imageViewLive != null) {
			RelativeLayout.LayoutParams mLayoutSize = null;
			if (size.contentEquals("expand")) {
				mLayoutSize = new RelativeLayout.LayoutParams(imageViewLive.getWidth() + 5, imageViewLive.getHeight() + 5);

			} else if (size.contentEquals("contract")) {
				mLayoutSize = new RelativeLayout.LayoutParams(imageViewLive.getWidth() - 5, imageViewLive.getHeight() - 5);
			}

			mLayoutSize.leftMargin = imageViewLive.getLeft();
			mLayoutSize.topMargin = imageViewLive.getTop();
			imageViewLive.setLayoutParams(mLayoutSize);

			mainActivity.katbagHandler.updateDrawingPartPosition(imageViewLive.getId(), imageViewLive.getTop(), imageViewLive.getLeft(), imageViewLive.getWidth(), imageViewLive.getHeight());

			imageViewLive.invalidate();
			one_drawing.invalidate();
		}
	}

	public void rotate(String direction, int rotate) {
		if (imageViewLive != null) {

			int rotate_old;

			if (rotate > -1) {
				rotate_old = rotate;

			} else {
				rotate_old = mainActivity.katbagHandler.selectDrawingsPartsRotateForIdApp(imageViewLive.getId());
				rotate = -1;

				if (direction.contentEquals("left"))
					rotate = rotate_old - 5;
				if (direction.contentEquals("right"))
					rotate = rotate_old + 5;

				if (rotate > 360)
					rotate = 0;
				if (rotate < 0)
					rotate = 360;
			}

			imageViewLive.clearAnimation();
			Matrix matrix = imageViewLive.getImageMatrix();
			RectF dst = new RectF();
			matrix.mapRect(dst, new RectF(imageViewLive.getDrawable().getBounds()));
			RotateAnimation mAnimation = new RotateAnimation(rotate_old, rotate, imageViewLive.getWidth() / 2, imageViewLive.getHeight() / 2);
			mAnimation.setFillEnabled(true);
			mAnimation.setFillAfter(true);
			imageViewLive.startAnimation(mAnimation);
			imageViewLive.setImageMatrix(matrix);

			if (!direction.contentEquals("load")) {
				mainActivity.katbagHandler.updateDrawingPartRotate(imageViewLive.getId(), imageViewLive.getTop(), imageViewLive.getLeft(), imageViewLive.getWidth(), imageViewLive.getHeight(), rotate);
			}
		}
	}

	public void trash() {
		if (imageViewLive != null) {
			if (imageViewLive.getTag().toString().contains(PART_BODY)) {
				body = false;
			}

			mainActivity.katbagHandler.deleteDrawingPartForId(imageViewLive.getId());
			one_drawing.removeView(imageViewLive);
			one_drawing.invalidate();
		}
	}

	@Override
	public void onResume() {
		mainActivity.getSupportActionBar().setTitle(Add.name_app_text + " - " + getString(R.string.drawings_row_name) + " " + name_drawing);

		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		super.onResume();
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
