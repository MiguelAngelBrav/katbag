/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 *  
 * Copyright (C) 2014 The Android Open Source Project Katbag of IPP and Miguel Angel Bravo
 * Licensed under the Apache 2.0 License.
 */

package cl.ipp.katbag.fragment;

import java.util.ArrayList;
import java.util.Arrays;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.AlertDialog.Builder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagDrawing;
import cl.ipp.katbag.core.KatbagDrawingBuilder;
import cl.ipp.katbag.core.KatbagEditText;
import cl.ipp.katbag.core.KatbagHandlerSqlite;
import cl.ipp.katbag.core.KatbagUtilities;
import cl.ipp.katbag.row_adapters.DialogDevelopRowAdapter;
import cl.ipp.katbag.row_adapters.DialogSoundRowAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

public class OnePage extends SherlockFragment {
	
	private Tracker tracker;

	static RelativeLayout v = null;
	public MainActivity mainActivity;
	public long id_page = -1;
	public static long id_app = -1;
	public String name_page;
	public MenuItem menuItemPlaySound, menuItemRemoveObject, menuItemAddWorld, menuItemAddDrawing, menuItemAddText, menuItemAddSound, menuItemRemoveWorld, menuItemRemoveSound;
	public DialogDevelopRowAdapter adapterDialog;
	public ArrayList<String> page = new ArrayList<String>();
	public ArrayList<String> world = new ArrayList<String>();
	public String mCurrentPhotoPath = "";
	public RelativeLayout backgroundView;
	public ImageView alignRight, alignCenter, alignLeft, fontSizeUp, fontSizeDown, textColor;
	private ColorPicker pickerColor;
	private SVBar svBar;
	public int colorText = -16777216;
	public DialogSoundRowAdapter adapterDialogSound;
	private String soundID = "";

	private String resTitle = "";
	private ArrayList<String> dialogList = new ArrayList<String>();
	private ArrayList<String> dialogIdObjectList = new ArrayList<String>();
	private ArrayList<String> dialogHumanStatement = new ArrayList<String>();
	private MediaPlayer mPlayer;

	public int[] loc;
	public boolean mMeasured = false;
	private ArrayList<String> dev = new ArrayList<String>();
	private ArrayList<String> develop = new ArrayList<String>();

	public KatbagDrawing drawingLive;
	public KatbagEditText textLive;

	public Builder alertDialog;

	public static final int OBJECT_WORLD = 0;
	public static final int OBJECT_SOUND = 1;
	public static final int OBJECT_DRAWING = 2;

	public static final int PAGE_WORLD_ID = 0;
	public static final int PAGE_SOUND_ID = 1;
	public static final int PAGE_ORDER = 2;

	public static final int WORLD_ARRAY_TYPE = 0;
	public static final int WORLD_ARRAY_SRC = 1;
	public static final int WORLD_ARRAY_SCALE_FACTOR = 2;

	public static final String WORLD_TYPE_COLOR = "color";
	public static final String WORLD_TYPE_CAMERA = "camera";
	public static final String WORLD_TYPE_LIBRARY = "library";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		this.tracker = EasyTracker.getInstance(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = (RelativeLayout) inflater.inflate(R.layout.fragment_one_page, container, false);
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

		// rescues parameters
		Bundle bundle = getArguments();
		if (bundle != null) {
			id_app = bundle.getLong("id_app");
			id_page = bundle.getLong("id_page");
			name_page = bundle.getString("name_page");
		}

		alignLeft = (ImageView) v.findViewById(R.id.toolbar_alignleft);
		alignCenter = (ImageView) v.findViewById(R.id.toolbar_aligncenter);
		alignRight = (ImageView) v.findViewById(R.id.toolbar_alignright);
		fontSizeUp = (ImageView) v.findViewById(R.id.toolbar_fontsizeup);
		fontSizeDown = (ImageView) v.findViewById(R.id.toolbar_fontsizedown);
		textColor = (ImageView) v.findViewById(R.id.toolbar_textcolor);

		page.clear();
		page = mainActivity.katbagHandler.selectOnePageForId(id_app, id_page);

		if (page.size() > 0) {
			if ((page.get(PAGE_WORLD_ID) != null) && (!page.get(PAGE_WORLD_ID).contentEquals("")))
				setWorld(Long.parseLong(page.get(PAGE_WORLD_ID)));

			if (page.get(PAGE_SOUND_ID) != null) {
				soundID = page.get(PAGE_SOUND_ID);
			}
			playSound(String.valueOf(soundID));

		}

		alignLeft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (textLive != null) {
					mainActivity.katbagHandler.updateOneTextAlignFromPage(id_app, id_page, textLive.getId(), "left");
					textLive.setTextAlign("left");
				}
			}
		});

		alignCenter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (textLive != null) {
					mainActivity.katbagHandler.updateOneTextAlignFromPage(id_app, id_page, textLive.getId(), "center");
					textLive.setTextAlign("center");
				}
			}
		});

		alignRight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (textLive != null) {
					mainActivity.katbagHandler.updateOneTextAlignFromPage(id_app, id_page, textLive.getId(), "right");
					textLive.setTextAlign("right");
				}
			}
		});

		fontSizeUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveFontSize("up");
			}
		});

		fontSizeDown.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveFontSize("down");
			}
		});

		textColor.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAndSaveColor();
			}
		});

		backgroundView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				removeBorderToItemSelected();
				backgroundView.clearFocus();
				if (mainActivity.getCurrentFocus() != null) mainActivity.getCurrentFocus().clearFocus();
				mainActivity.hideSoftKeyboard();
			}
		});
	}

	public void setDevelopBook() {
		getData();

		// add first drawing, then text, then motion
		develop.clear();
		develop = mainActivity.katbagHandler.selectDevelopBookForIdAppAndPageId(id_app, id_page);
		for (int i = 0; i < develop.size(); i++) {
			String[] line = develop.get(i).split("&&");
			if (line[1].contentEquals("drawing")) {
				setDrawing(line);
			}
		}
		
		for (int i = 0; i < develop.size(); i++) {
			String[] line = develop.get(i).split("&&");
			if (line[1].contentEquals("text")) {
				setText(line);
			}
		}
		
		for (int i = 0; i < develop.size(); i++) {
			String[] line = develop.get(i).split("&&");
			if (line[1].contentEquals("motion")) {
				if (backgroundView.findViewById(Integer.parseInt(line[4])) instanceof KatbagDrawing) {
					KatbagDrawing drawingMove = (KatbagDrawing) backgroundView.findViewById(Integer.parseInt(line[4]));
					if (drawingMove != null) drawingMove.moveToXY(Integer.parseInt(line[5]), Integer.parseInt(line[6]));
				
				} else if (backgroundView.findViewById(Integer.parseInt(line[4])) instanceof KatbagEditText) {
					KatbagEditText textMove = (KatbagEditText) backgroundView.findViewById(Integer.parseInt(line[4]));
					if (textMove != null) textMove.moveToXY(Integer.parseInt(line[5]), Integer.parseInt(line[6]));
				}
			}
		}
	}

	public void selectAndSaveColor() {

		LayoutInflater inflater = LayoutInflater.from(mainActivity.context);
		final View dialog_layout = inflater.inflate(R.layout.dialog_picker_color, null);

		svBar = (SVBar) dialog_layout.findViewById(R.id.custom_valuebar);
		pickerColor = (ColorPicker) dialog_layout.findViewById(R.id.custom_picker_color);
		pickerColor.addSVBar(svBar);
		pickerColor.setOldCenterColor(colorText);
		pickerColor.setColor(colorText);

		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.context);
		builder.setView(dialog_layout);
		builder.setTitle(getString(R.string.dialog_title_select_picker_color));

		builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// :)
			}
		});

		builder.setPositiveButton(getString(R.string.dialog_button_select), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (textLive != null) {
					mainActivity.katbagHandler.updateOneTextColorFromPage(id_app, id_page, textLive.getId(), String.valueOf(pickerColor.getColor()));
					textLive.setColorText(pickerColor.getColor());
				}
			}
		});

		builder.show();
	}

	public void saveFontSize(String direction) {
		if (textLive != null) {
			int fontSize = (int) convertPixelsToSp(textLive.getTextSize(), mainActivity.context) ;
			if (direction.contentEquals("up"))
				fontSize = fontSize + 2;
			else if (direction.contentEquals("down"))
				fontSize = fontSize - 2;

			if (fontSize <= 10)
				fontSize = 10;
			if (fontSize > 70)
				fontSize = 70;		
			
			mainActivity.katbagHandler.updateOneTextSizeFromPage(id_app, id_page, textLive.getId(), String.valueOf(fontSize));
			textLive.setFontSize(fontSize);
		}
	}
	
	public static float convertPixelsToSp(Float px, Context context) {
	    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	    return px/scaledDensity;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.one_page, menu);

		menuItemPlaySound = menu.findItem(R.id.one_page_dropdown_menu_play_sound);
		menuItemRemoveObject = menu.findItem(R.id.one_page_remove_object);
		menuItemAddWorld = menu.findItem(R.id.one_page_dropdown_menu_add_world);
		menuItemAddDrawing = menu.findItem(R.id.one_page_dropdown_menu_add_drawing);
		menuItemAddText = menu.findItem(R.id.one_page_dropdown_menu_add_text);
		menuItemAddSound = menu.findItem(R.id.one_page_dropdown_menu_add_sound);
		menuItemRemoveWorld = menu.findItem(R.id.one_page_dropdown_menu_remove_world);
		menuItemRemoveSound = menu.findItem(R.id.one_page_dropdown_menu_remove_sound);

		setIconMenuItemPlaySound();

		menuItemPlaySound.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (!soundID.contentEquals(""))
					playSound(soundID);
				else
					KatbagUtilities.message(mainActivity.context, getString(R.string.one_page_message_sound_not_set));

				return true;
			}
		});

		menuItemRemoveObject.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			int id = 0;

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if ((drawingLive != null) || (textLive != null)) {
					alertDialog = new AlertDialog.Builder(mainActivity.context);
					if (drawingLive != null) {
						alertDialog.setTitle(getString(R.string.one_page_dialog_remove_drawing_title));
						alertDialog.setMessage(String.format(getString(R.string.one_page_dialog_remove_drawing_text), drawingLive.getId()));
						id = drawingLive.getId();
					} else if (textLive != null) {
						alertDialog.setTitle(getString(R.string.one_page_dialog_remove_text_title));
						alertDialog.setMessage(String.format(getString(R.string.one_page_dialog_remove_text_text), textLive.getId()));
						id = textLive.getId();
					}

					alertDialog.setNegativeButton(getString(R.string.one_page_dialog_remove_object_button_no), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// :)
						}
					});

					alertDialog.setPositiveButton(getString(R.string.one_page_dialog_remove_object_button_yes), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							View view = null;
							if (drawingLive != null) {
								if (mainActivity.katbagHandler.deleteDrawingFromPageForId(id_app, id_page, id)) view = (View) backgroundView.findViewById(id);
							} else if (textLive != null) {
								if (mainActivity.katbagHandler.deleteOneTextFromPageForId(id_app, id_page, id)) view = (View) backgroundView.findViewById(id);
							}
							
							if (view != null) backgroundView.removeView(view);
							removeBorderToItemSelected();
							mainActivity.hideSoftKeyboard();
						}
					});

					alertDialog.show();
				}

				return true;
			}
		});

		menuItemAddWorld.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showAlertDialog(OBJECT_WORLD);
				return true;
			}
		});

		menuItemAddDrawing.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showAlertDialog(OBJECT_DRAWING);
				return true;
			}
		});
		
		menuItemAddText.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				long object_id = mainActivity.katbagHandler.insertDevelop(
						id_app, 
						"text", 
						"Este es un texto de ejemplo", 
						"20", // text size 
						"center", // text align  
						String.valueOf(colorText), // text color 
						"", 
						String.valueOf(id_page), 
						"", 
						"", 
						"", 
						0, 0);
				
				dev.clear();
				dev = mainActivity.katbagHandler.selectDevelopBookForId(object_id);
				if (dev.size() > 0) {
					String[] line = dev.get(0).split("&&");
					int top = 100;
					int left = 100;

					if (line[1].contentEquals("text")) {
						setText(line);
						
						KatbagEditText textMove = (KatbagEditText) backgroundView.findViewById(Integer.parseInt(line[0]));
						if (textMove != null) textMove.moveToXY(left, top);
						
						mainActivity.katbagHandler.insertDevelop(
								id_app, 
								"motion", 
								"", 
								"", // first parameter is the type motion
								String.valueOf(Integer.parseInt(line[0])), // id of selected
								String.valueOf(left), // X coordinates
								String.valueOf(top), // Y coordinates
								String.valueOf(id_page), // page id
								"", "", "", 0, 0);
					}
				}
				
				getData();
				
				return true;
			}
		});

		menuItemAddSound.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showAlertDialog(OBJECT_SOUND);
				return true;
			}
		});
		
		menuItemRemoveWorld.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				mainActivity.katbagHandler.updatePageWorld(id_app, id_page, "");
				backgroundView.setBackgroundResource(0);
				return true;
			}
		});

		menuItemRemoveSound.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				stopPlayer();
				mainActivity.katbagHandler.updatePageSound(id_app, id_page, "");
				soundID = "";
				setIconMenuItemPlaySound();
				return true;
			}
		});
	}

	public void setIconMenuItemPlaySound() {
		if (menuItemPlaySound != null) {
			if (!soundID.contentEquals(""))
				menuItemPlaySound.setIcon(R.drawable.ic_action_volume_on);
			else
				menuItemPlaySound.setIcon(R.drawable.ic_action_volume_muted);
		}
	}

	public void playSound(String identifier) {
		if (!identifier.contentEquals("")) {
			int sound = mainActivity.getResources().getIdentifier(identifier, "raw", mainActivity.getPackageName());
			stopPlayer();
			mPlayer = MediaPlayer.create(mainActivity.context, sound);
			mPlayer.start();
		}

		setIconMenuItemPlaySound();
	}

	public void stopPlayer() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}

	public void showAlertDialog(final int object) {
		dialogList.clear();
		dialogIdObjectList.clear();
		dialogHumanStatement.clear();

		switch (object) {
		case OBJECT_WORLD:
			resTitle = getString(R.string.develop_dropdown_menu_add_world);
			dialogList = mainActivity.katbagHandler.selectWorldsForIdApp(id_app);
			for (int i = 0; i < dialogList.size(); i++) {
				dialogIdObjectList.add(dialogList.get(i));
				dialogHumanStatement.add(getString(R.string.worlds_row_name) + " " + dialogList.get(i));
			}

			break;

		case OBJECT_DRAWING:
			// only insert, not update
			resTitle = getString(R.string.develop_dropdown_menu_add_drawing);
			dialogList = mainActivity.katbagHandler.selectDrawingsForIdApp(id_app);
			for (int i = 0; i < dialogList.size(); i++) {
				dialogIdObjectList.add(dialogList.get(i));
				dialogHumanStatement.add(getString(R.string.drawings_row_name) + " " + dialogList.get(i));
			}

			break;

		case OBJECT_SOUND:
			String nameSound = "";
			resTitle = getString(R.string.develop_dropdown_menu_add_sound);
			dialogList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sound_list)));
			for (int i = 0; i < dialogList.size(); i++) {
				nameSound = dialogList.get(i).substring(9, dialogList.get(i).length());
				nameSound = nameSound.replace("animales_", "(Animal) ");
				nameSound = nameSound.replace("juegos_", "(Juego) ");
				nameSound = nameSound.replace("humanos_", "(Humano) ");
				nameSound = nameSound.replace("fondo_", "(Fondo) ");
				nameSound = nameSound.replace("series_", "(Serie) ");
				nameSound = nameSound.replace("efectos_", "(Efecto) ");
				nameSound = nameSound.replace("_", " ");
				// nameSound = KatbagUtilities.capitalizeString(nameSound);
				dialogHumanStatement.add(nameSound);
			}

			break;
		}

		switch (object) {
		case OBJECT_SOUND:
			adapterDialogSound = new DialogSoundRowAdapter(v.getContext(), R.layout.row_dialog_sound, dialogList, dialogHumanStatement);

			AlertDialog.Builder builder_sound = new AlertDialog.Builder(mainActivity.context);
			builder_sound.setTitle(getString(R.string.dialog_title_select) + " " + resTitle);
			builder_sound.setAdapter(adapterDialogSound, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mainActivity.katbagHandler.updatePageSound(id_app, id_page, dialogList.get(which));
					soundID = dialogList.get(which);
					setIconMenuItemPlaySound();
					adapterDialogSound.stopPlayer();
				}
			});

			builder_sound.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// :)
					adapterDialogSound.stopPlayer();
				}
			});

			builder_sound.show();
			break;

		default:
			adapterDialog = new DialogDevelopRowAdapter(v.getContext(), R.layout.row_dialog_develop, dialogIdObjectList, dialogHumanStatement);

			AlertDialog.Builder builder_default = new AlertDialog.Builder(mainActivity.context);
			builder_default.setTitle(getString(R.string.dialog_title_select) + " " + resTitle);
			builder_default.setAdapter(adapterDialog, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (object) {
					case OBJECT_WORLD:
						mainActivity.katbagHandler.updatePageWorld(id_app, id_page, dialogIdObjectList.get(which));
						setWorld(Long.valueOf(dialogIdObjectList.get(which)));

						break;

					case OBJECT_DRAWING: // only insert, not update
						if (mainActivity.katbagHandler.selectDevelopBookDrawingExist(dialogIdObjectList.get(which), id_app, id_page)) {
							KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_drawing_exist));

						} else {
							long object_id = mainActivity.katbagHandler.insertDevelop(
									id_app, 
									"drawing", 
									dialogHumanStatement.get(which), 
									dialogIdObjectList.get(which), 
									"", 
									"", 
									"", 
									String.valueOf(id_page), 
									"", "", "", 0, 0);

							dev.clear();
							dev = mainActivity.katbagHandler.selectDevelopBookForId(object_id);
							if (dev.size() > 0) {
								String[] line = dev.get(0).split("&&");

								if (line[1].contentEquals("drawing")) {
									int top = 200;
									int left = 100;
									
									setDrawing(line);
									
									KatbagDrawing drawingMove = (KatbagDrawing) backgroundView.findViewById(Integer.parseInt(line[3]));
									if (drawingMove != null) drawingMove.moveToXY(left, top);
									
									mainActivity.katbagHandler.insertDevelop(
											id_app, 
											"motion", 
											"", 
											"", // first parameter is the type motion
											String.valueOf(Integer.parseInt(line[3])), // id of selected
											String.valueOf(left), // X coordinates
											String.valueOf(top), // Y coordinates
											String.valueOf(id_page), // page id
											"", "", "", 0, 0);
								}
							}
							
							// reload develop of page
							develop.clear();
							develop = mainActivity.katbagHandler.selectDevelopBookForIdAppAndPageId(id_app, id_page);
							
							getData();
						}

						break;
					}
				}
			});

			builder_default.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// :)
				}
			});

			builder_default.show();
			break;
		}
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
			drawingBuilder.setIdDrawing(Long.parseLong(line[3])); // this build the drawing
			Bitmap bitmap = createBitmapFromRelativeLayout(drawingBuilder);
			KatbagDrawing drawing = new KatbagDrawing(mainActivity.context);
			drawing.setImageBitmap(bitmap);
			drawing.setId(Integer.parseInt(line[3]));
			drawing.setSizeFather(backgroundView.getWidth(), backgroundView.getHeight(), loc, backgroundView);
			drawing.setMySize(drawingBuilder.getMyWidth(), drawingBuilder.getMyHeight());
			drawing.setScaleType(ScaleType.MATRIX);
			drawing.setOnTouchListener(new mOnTouchDrawing());
			backgroundView.addView(drawing);
		}
	}
	
	public void setText(String[] line) {	
		KatbagEditText text = new KatbagEditText(mainActivity.context);
		text.setId(Integer.parseInt(line[0]));
		text.setText(line[2]);
		text.setTextAlign(line[4]);
		text.setFontSize(Integer.parseInt(line[3]));
		text.setColorText(Integer.parseInt(line[5]));
		text.setBackgroundResource(R.drawable.border_one_drawing_part_default);
		text.setOnTouchListener(new mOnTouchEditText());
		text.addTextChangedListener(new mTextWatcher(text));
		backgroundView.addView(text);
	}

	public class mOnTouchDrawing implements View.OnTouchListener {

		int leftIni = 0, topIni = 0, leftMax = 0, topMax = 0;
		private boolean dragging = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				leftIni = (int) event.getRawX() - (loc[0] + v.getLeft());
				topIni = (int) event.getRawY() - (loc[1] + v.getTop());
				leftMax = backgroundView.getWidth() - v.getWidth();
				topMax = backgroundView.getHeight() - v.getHeight();

				removeBorderToItemSelected();
				v.setBackgroundResource(R.drawable.border_one_drawing_part);

				drawingLive = (KatbagDrawing) v;

				dragging = true;

				break;

			case MotionEvent.ACTION_UP:
				int object_id = mainActivity.katbagHandler.selectDevelopMotionExist(id_app, id_page, drawingLive.getId());

				if (object_id == -1) {
					mainActivity.katbagHandler.insertDevelop(
							id_app, 
							"motion", 
							"", 
							"", // first parameter is the type motion
							String.valueOf(drawingLive.getId()), // id of selected
							String.valueOf(v.getLeft()), // X coordinates
							String.valueOf(v.getTop()), // Y coordinates
							String.valueOf(id_page), // page id
							"", "", "", 0, 0);
				} else {
					mainActivity.katbagHandler.updateDevelop(
							object_id, 
							"motion", 
							"", 
							"", // first parameter is the type motion
							String.valueOf(drawingLive.getId()), // id of drawing selected
							String.valueOf(v.getLeft()), // X coordinates
							String.valueOf(v.getTop()), // Y coordinates
							String.valueOf(id_page),
							"", "", ""); // page id
				}

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

					backgroundView.invalidate();
				}

				break;

			}

			return dragging;
		}
	}
	
	public class mOnTouchEditText implements View.OnTouchListener {

		int leftIni = 0, topIni = 0, leftMax = 0, topMax = 0;
		private boolean dragging = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				leftIni = (int) event.getRawX() - (loc[0] + v.getLeft());
				topIni = (int) event.getRawY() - (loc[1] + v.getTop());
				leftMax = backgroundView.getWidth() - v.getWidth();
				topMax = backgroundView.getHeight() - v.getHeight();

				removeBorderToItemSelected();
				v.setBackgroundResource(R.drawable.border_one_drawing_part);

				textLive = (KatbagEditText) v;
				textLive.requestFocus();				
				
				dragging = true;

				break;

			case MotionEvent.ACTION_UP:
				int object_id = mainActivity.katbagHandler.selectDevelopMotionExist(id_app, id_page, textLive.getId());

				if (object_id == -1) {
					mainActivity.katbagHandler.insertDevelop(
							id_app, 
							"motion", 
							"", 
							"", // first parameter is the type motion
							String.valueOf(textLive.getId()), // id of selected
							String.valueOf(v.getLeft()), // X coordinates
							String.valueOf(v.getTop()), // Y coordinates
							String.valueOf(id_page), // page id
							"", "", "", 0, 0);
				} else {
					mainActivity.katbagHandler.updateDevelop(
							object_id, 
							"motion", 
							"", 
							"", // first parameter is the type motion
							String.valueOf(textLive.getId()), // id of drawing selected
							String.valueOf(v.getLeft()), // X coordinates
							String.valueOf(v.getTop()), // Y coordinates
							String.valueOf(id_page),
							"", "", ""); // page id
				}

				dragging = false;

				break;

			case MotionEvent.ACTION_MOVE:
				textLive.clearFocus();
				mainActivity.hideSoftKeyboard();
				
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

					ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
					lp.leftMargin = left;
					lp.topMargin = top;
					v.setLayoutParams(lp);

					backgroundView.invalidate();
				}

				break;

			}

			return dragging;
		}
	}
	
	public class mTextWatcher implements TextWatcher {
		
		KatbagEditText katbagEditText;
		
		public mTextWatcher(KatbagEditText e) { 
			katbagEditText = e;
	    }

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			mainActivity.katbagHandler.updateOneTextFromPage(id_app, id_page, katbagEditText.getId(), katbagEditText.getText().toString());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public void removeBorderToItemSelected() {
		for (int j = 0; j < backgroundView.getChildCount(); j++) {
			if (backgroundView.getChildAt(j) instanceof KatbagDrawing) backgroundView.getChildAt(j).setBackgroundResource(0);
			else if (backgroundView.getChildAt(j) instanceof KatbagEditText) backgroundView.getChildAt(j).setBackgroundResource(R.drawable.border_one_drawing_part_default);
			backgroundView.getChildAt(j).clearFocus();
		}

		drawingLive = null;
		textLive = null;
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
	public void onResume() {
		mainActivity.getSupportActionBar().setTitle(getString(R.string.pages_row_name) + " " + name_page);

		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		super.onResume();
		
		this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
        this.tracker.send( MapBuilder.createAppView().build() );
	}

	@Override
	public void onPause() {
		stopPlayer();
		System.gc();
		super.onPause();
	}

	@Override
	public void onStop() {
		stopPlayer();
		System.gc();
		super.onStop();
	}

	public void getData() {
		String query = "";
		query += "select * " + 
				"from " + KatbagHandlerSqlite.TABLE_DEVELOP + " " + 
				"where " + KatbagHandlerSqlite.FIELD_DEVELOP_APP_ID + " = " + id_app;
		mainActivity.katbagHandler.select(query, true);
		
	}
}
