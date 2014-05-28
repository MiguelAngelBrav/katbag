package cl.ipp.katbag.fragment;

import java.util.ArrayList;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagDrawing;
import cl.ipp.katbag.core.KatbagDrawingBuilder;
import cl.ipp.katbag.core.KatbagUtilities;

public class Player extends Fragment implements SensorEventListener {

	static View v = null;
	public static MainActivity mainActivity;
	public long id_app = -1;
	public String name_app_text = "";
	public boolean editMode = false;
	public RelativeLayout playerView;
	public String mCurrentPhotoPath = "";
	public boolean mMeasured = false;
	public int[] loc;
	public Button start;
	public LinearLayout scorePanel;
	public int intScore = 0;
	public TextView score;
	public SensorManager sensorManager;
	public Sensor accelerometer;
	public boolean useAccelerometer = false;
	public long lastUpdate;
	public float x = 0, y = 0, z = 0;
	public float last_x = 0, last_y = 0, last_z = 0;
	private static final int SHAKE_THRESHOLD = 800;

	private ArrayList<String> develop = new ArrayList<String>();
	public ArrayList<String> world = new ArrayList<String>();

	public ArrayList<Integer> drawingUsesAccelerometer = new ArrayList<Integer>();
	public ArrayList<Integer> drawingUsesRandomly = new ArrayList<Integer>();
	public ArrayList<Integer> drawingUsesBounceThread = new ArrayList<Integer>();

	public ArrayList<Integer> conditionId = new ArrayList<Integer>();
	public ArrayList<Integer> conditionState = new ArrayList<Integer>();
	public ArrayList<Integer> conditionLevel = new ArrayList<Integer>();

	public ArrayList<Integer> iterationsId = new ArrayList<Integer>();
	public ArrayList<Integer> iterationsN = new ArrayList<Integer>();
	public ArrayList<Integer> iterationsBgn = new ArrayList<Integer>();
	public ArrayList<Integer> iterationsLst = new ArrayList<Integer>();

	public static ArrayList<Integer> touchEventId = new ArrayList<Integer>();
	public static ArrayList<Integer> touchEventDrawing = new ArrayList<Integer>();
	public static ArrayList<Integer> touchEventBgn = new ArrayList<Integer>();
	public static ArrayList<Integer> touchEventLst = new ArrayList<Integer>();

	public static ArrayList<Integer> scoreEqualId = new ArrayList<Integer>();
	public static ArrayList<Integer> scoreEqualToN = new ArrayList<Integer>();
	public static ArrayList<Integer> scoreEqualToBgn = new ArrayList<Integer>();
	public static ArrayList<Integer> scoreEqualToLst = new ArrayList<Integer>();

	public static ArrayList<Integer> shakeId = new ArrayList<Integer>();
	public static ArrayList<Integer> shakeBgn = new ArrayList<Integer>();
	public static ArrayList<Integer> shakeLst = new ArrayList<Integer>();

	private MediaPlayer mPlayer;
	private boolean muteAll = false;
	private Handler mHandler = null;
	private Handler mHandlerPlayer = null;
	private Runnable mRunnable;
	private Runnable mRunnablePlayer;

	public static final int WORLD_ARRAY_TYPE = 0;
	public static final int WORLD_ARRAY_SRC = 1;
	public static final int WORLD_ARRAY_SCALE_FACTOR = 2;

	public static final String WORLD_TYPE_COLOR = "color";
	public static final String WORLD_TYPE_CAMERA = "camera";
	public static final String WORLD_TYPE_LIBRARY = "library";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_player, container, false);
		mainActivity = (MainActivity) super.getActivity();

		// rescues parameters
		Bundle bundle = getArguments();
		if (bundle != null) {
			id_app = bundle.getLong("id_app");
			editMode = bundle.getBoolean("editMode");
			name_app_text = bundle.getString("name_app");
		}

		scorePanel = (LinearLayout) v.findViewById(R.id.player_score_panel);
		score = (TextView) v.findViewById(R.id.player_score);

		start = (Button) v.findViewById(R.id.player_button_start);
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scorePanel.setVisibility(View.VISIBLE);
				start.setVisibility(View.GONE);

				drawingUsesAccelerometer.clear();
				drawingUsesBounceThread.clear();

				conditionId.clear();
				conditionState.clear();
				conditionLevel.clear();

				iterationsId.clear();
				iterationsN.clear();
				iterationsBgn.clear();
				iterationsLst.clear();

				touchEventId.clear();
				touchEventDrawing.clear();
				touchEventBgn.clear();
				touchEventLst.clear();

				scoreEqualId.clear();
				scoreEqualToN.clear();
				scoreEqualToBgn.clear();
				scoreEqualToLst.clear();

				shakeId.clear();
				shakeBgn.clear();
				shakeLst.clear();

				develop = mainActivity.katbagHandler.selectDevelopForIdApp(id_app);

				for (int i = 0; i < develop.size(); i++) {
					String[] line = develop.get(i).split("&&");

					if (line[1].contentEquals("sensing")) {
						switch (Integer.parseInt(line[3])) {
						case 0:
							conditionId.add(Integer.valueOf(line[0]));
							conditionState.add(0);
							conditionLevel.add(Integer.valueOf(line[8]));
							break;

						case 1: // shake
							conditionId.add(Integer.valueOf(line[0]));
							conditionState.add(0);
							conditionLevel.add(Integer.valueOf(line[8]));
							break;
						}
					}
				}

				setScore(intScore);
				play(0, develop.size());

			}
		});

		playerView = (RelativeLayout) v.findViewById(R.id.player_view);
		playerView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (!mMeasured) {
					loc = new int[2];
					playerView.getLocationInWindow(loc);
					mMeasured = true;
					start.setVisibility(View.VISIBLE);
				}
			}
		});

		return v;
	}

	public void setScore(int s) {
		score.setText(String.valueOf(s));

		for (int i = 0; i < scoreEqualToN.size(); i++) {
			if (scoreEqualToN.get(i) == s) {

				Log.d("ply", "setScore - ini:" + scoreEqualToBgn.get(i) + ", last:" + scoreEqualToLst.get(i));
				play(scoreEqualToBgn.get(i), scoreEqualToLst.get(i) + 1);
			}
		}
	}

	public void play(final int first, final int last) {
		int f = 0;
		int i = first;

		while (i < last) {
			String[] line = develop.get(i).split("&&");
			Log.d("ply", "exe:" + i + ", size:" + iterationsN.size());

			if (runByLineType(line, i)) {
				break;
			}

			if (iterationsN.size() > 0) {
				f = iterationsN.size() - 1;
				if (i == iterationsLst.get(f)) {
					iterationsN.set(f, iterationsN.get(f) - 1);
					i = iterationsBgn.get(f);

					if (iterationsN.get(f) == 1) {
						iterationsId.remove(f);
						iterationsN.remove(f);
						iterationsBgn.remove(f);
						iterationsLst.remove(f);
					}
				} else
					i++;

			} else
				i++;

			Log.d("ply", "--------------------------------------");
		}
	}

	public boolean runByLineType(String[] line, int lineNumber) {
		boolean mBreak = false;
		if (line[1].contentEquals("world")) {
			setWorld(Long.parseLong(line[3]));
			mBreak = false;

		} else if (line[1].contentEquals("drawing")) {
			setDrawing(line);
			mBreak = false;

		} else if (line[1].contentEquals("motion")) {
			setMotion(line);
			mBreak = false;

		} else if (line[1].contentEquals("look")) {
			setLook(line);
			mBreak = false;

		} else if (line[1].contentEquals("sound")) {
			setSound(line);
			mBreak = false;

		} else if (line[1].contentEquals("control")) {
			setControl(line, lineNumber);
			switch (Integer.parseInt(line[3])) {
			case 0:
				mBreak = true;
				break;

			case 6:
				mBreak = true;
				break;

			default:
				mBreak = false;
				break;
			}

		} else if (line[1].contentEquals("sensing")) {
			setSensing(line, lineNumber);
			mBreak = true;
		}

		return mBreak;
	}

	public void setSensing(String[] line, int lineNumber) {
		switch (Integer.parseInt(line[3])) {
		case 0:
			boolean ex = false;
			for (int z = 0; z < touchEventId.size(); z++) {
				if (line[0].contentEquals(String.valueOf(touchEventId.get(z)))) {
					ex = true;
					break;
				}
			}

			if (!ex) {
				int last = -1;
				for (int i = lineNumber + 1; i < develop.size(); i++) {
					String[] it = develop.get(i).split("&&");

					last = i;
					if (Integer.parseInt(line[8]) >= Integer.parseInt(it[8])) {
						last--;
						break;
					}
				}

				if ((lineNumber + 1) <= last) {
					touchEventId.add(Integer.valueOf(line[0]));
					touchEventDrawing.add(Integer.valueOf(line[4])); // drawing
					touchEventBgn.add(lineNumber + 1); // begin in
					touchEventLst.add(last); // ends

					final KatbagDrawing drawing = (KatbagDrawing) v.findViewById(Integer.valueOf(line[4]));
					if (drawing != null) {
						drawing.setOnTouchListener(new OnTouchListener() {

							@Override
							public boolean onTouch(View v, MotionEvent event) {
								switch (event.getAction()) {
								case MotionEvent.ACTION_DOWN:
									for (int i = 0; i < touchEventDrawing.size(); i++) {
										if (touchEventDrawing.get(i) == drawing.getId()) {
											setBranchTreeCondition(touchEventId.get(i), 1);
											if (checkTreeCondition(touchEventId.get(i))) {
												Log.d("ply", "touchEventDrawing - ini:" + touchEventBgn.get(i) + ", last:" + touchEventLst.get(i));
												play(touchEventBgn.get(i), touchEventLst.get(i) + 1);
												break;
											}
										}
									}

									break;

								case MotionEvent.ACTION_UP:
									for (int i = 0; i < touchEventDrawing.size(); i++) {
										if (touchEventDrawing.get(i) == drawing.getId()) {
											setBranchTreeCondition(touchEventId.get(i), 0);
											break;
										}
									}

									break;
								}

								return true;
							}
						});

						Log.d("ply", "setSensing - touchEvent add! count:" + touchEventId.size() + ", drawing:" + touchEventDrawing.get(touchEventDrawing.size() - 1) + " ini:" + touchEventBgn.get(touchEventBgn.size() - 1) + " last:" + touchEventLst.get(touchEventLst.size() - 1));
					}
				}

				if (last > -1)
					if ((last + 1) < develop.size())
						play((last + 1), develop.size());
			}

			break;

		case 1:
			boolean ex1 = false;
			for (int z = 0; z < shakeId.size(); z++) {
				if (line[0].contentEquals(String.valueOf(shakeId.get(z)))) {
					ex1 = true;
					break;
				}
			}

			if (!ex1) {
				int last1 = -1;
				for (int i = lineNumber + 1; i < develop.size(); i++) {
					String[] it = develop.get(i).split("&&");

					last1 = i;
					if (Integer.parseInt(line[8]) >= Integer.parseInt(it[8])) {
						last1--;
						break;
					}
				}

				if ((lineNumber + 1) <= last1) {
					shakeId.add(Integer.valueOf(line[0]));
					shakeBgn.add(lineNumber + 1); // begin in
					shakeLst.add(last1); // ends

					sensorManager = (SensorManager) mainActivity.context.getSystemService(Context.SENSOR_SERVICE);
					accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
					sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
					useAccelerometer = true;

					Log.d("ply", "setSensing - shake add! count:" + shakeId.size() + ", ini:" + shakeBgn.get(shakeBgn.size() - 1) + " last:" + shakeLst.get(shakeLst.size() - 1));
				}

				if (last1 > -1)
					if ((last1 + 1) < develop.size())
						play((last1 + 1), develop.size());
			}

			break;
		}
	}

	public boolean checkTreeCondition(int id) {
		int pos = -1;
		for (int i = 0; i < conditionId.size(); i++) {
			if (conditionId.get(i) == id) {
				pos = i;
				break;
			}
		}

		boolean sw = true;
		for (int i = pos; i >= 0; i--) {
			if ((conditionLevel.get(i) > 0) && (conditionState.get(i) == 1))
				sw = true;

			else if ((conditionLevel.get(i) == 0) && (conditionState.get(i) == 1)) {
				sw = true;
				break;

			} else {
				sw = false;
				break;
			}
		}

		Log.d("ply", "checkTC! id:" + id + ", sw:" + sw);

		return sw;
	}

	public void setBranchTreeCondition(int id, int sw) {
		for (int i = 0; i < conditionId.size(); i++) {
			if (conditionId.get(i) == id) {
				Log.d("ply", "setTC! id:" + conditionId.get(i) + ", state:" + sw);
				conditionState.set(i, sw);
				break;
			}
		}
	}

	public void setControl(String[] line, final int lineNumber) {
		switch (Integer.parseInt(line[3])) {
		case 0:
			mHandler = new Handler();
			mRunnable = new Runnable() {

				@Override
				public void run() {
					Log.d("ply", "executeRunnable - Coninuar ejecuci—n desde linea:" + (lineNumber + 1) + " hasta:" + develop.size());
					play(lineNumber + 1, develop.size());
				}
			};

			mHandler.postDelayed(mRunnable, (long) (Float.parseFloat(line[4]) * 1000));

			break;

		case 1:
			boolean ex = false;
			for (int z = 0; z < iterationsId.size(); z++) {
				if (line[0].contentEquals(String.valueOf(iterationsId.get(z)))) {
					ex = true;
					break;
				}
			}

			if (!ex) {
				int last = -1;
				for (int i = lineNumber + 1; i < develop.size(); i++) {
					String[] it = develop.get(i).split("&&");

					last = i;
					if (Integer.parseInt(line[8]) >= Integer.parseInt(it[8])) {
						last--;
						break;
					}
				}

				if (((lineNumber + 1) <= last) && (Integer.valueOf(line[4]) > 1)) {
					iterationsId.add(Integer.valueOf(line[0]));
					iterationsN.add(Integer.valueOf(line[4])); // number of
																// iterations
					iterationsBgn.add(lineNumber + 1); // iterations begin in
					iterationsLst.add(last); // iterations ends

					Log.d("ply", "setControl - iterator add! count:" + iterationsId.size() + ", n:" + iterationsN.get(iterationsN.size() - 1) + " ini:" + iterationsBgn.get(iterationsBgn.size() - 1) + " last:" + iterationsLst.get(iterationsLst.size() - 1));
				}
			}

			break;

		case 2:
			intScore = Integer.valueOf(line[4]);
			setScore(intScore);

			break;

		case 3:
			intScore = intScore + Integer.valueOf(line[4]);
			setScore(intScore);

			break;

		case 4:
			intScore = intScore - Integer.valueOf(line[4]);
			setScore(intScore);

			break;

		case 5:
			KatbagUtilities.message(mainActivity.context, line[4]);

			break;

		case 6:
			boolean ex1 = false;
			for (int z = 0; z < scoreEqualId.size(); z++) {
				if (line[0].contentEquals(String.valueOf(scoreEqualId.get(z)))) {
					ex1 = true;
					break;
				}
			}

			if (!ex1) {
				int last1 = -1;
				for (int i = lineNumber + 1; i < develop.size(); i++) {
					String[] it = develop.get(i).split("&&");

					last1 = i;
					if (Integer.parseInt(line[8]) >= Integer.parseInt(it[8])) {
						last1--;
						break;
					}
				}

				if ((lineNumber + 1) <= last1) {
					scoreEqualId.add(Integer.valueOf(line[0]));
					scoreEqualToN.add(Integer.valueOf(line[4])); // n
					scoreEqualToBgn.add(lineNumber + 1); // begin in
					scoreEqualToLst.add(last1); // ends

					Log.d("ply", "setControl - scoreEqual add! count:" + scoreEqualId.size() + ", n:" + scoreEqualToN.get(scoreEqualToN.size() - 1) + " ini:" + scoreEqualToBgn.get(scoreEqualToBgn.size() - 1) + " last:" + scoreEqualToLst.get(scoreEqualToLst.size() - 1));
				}

				if (last1 > -1)
					if ((last1 + 1) < develop.size())
						play((last1 + 1), develop.size());

			}

			break;
		}
	}

	public void setDrawing(String[] line) {
		KatbagDrawingBuilder drawingBuilder = new KatbagDrawingBuilder(mainActivity.context);
		drawingBuilder.setIdDrawing(Long.parseLong(line[3])); // this build the
																// drawing
		Bitmap bitmap = createBitmapFromRelativeLayout(drawingBuilder);
		KatbagDrawing drawing = new KatbagDrawing(mainActivity.context);
		drawing.setImageBitmap(bitmap);
		drawing.setId(Integer.parseInt(line[3]));
		drawing.setSizeFather(playerView.getWidth(), playerView.getHeight(), loc, playerView);
		drawing.setMySize(drawingBuilder.getMyWidth(), drawingBuilder.getMyHeight());
		if (editMode)
			drawing.setBackgroundResource(R.drawable.border_one_drawing_part);
		drawing.setScaleType(ScaleType.MATRIX);
		playerView.addView(drawing);
	}

	public void setMotion(String[] line) {
		KatbagDrawing drawingMove;
		switch (Integer.parseInt(line[3])) {
		case 0:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.moveSteps(Integer.parseInt(line[5]));
			break;

		case 1:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.moveToXY(Integer.parseInt(line[5]), Integer.parseInt(line[6]));
			break;

		case 2:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.moveOnlyAxisX();
			break;

		case 3:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.moveOnlyAxisY();
			break;

		case 4:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.moveToCenter();
			break;

		case 5:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			drawingUsesAccelerometer.add(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.moveWithAccelerometer();
			break;

		case 6:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[5]));
			if (drawingMove != null)
				drawingMove.changeXforN(Integer.parseInt(line[4]));
			break;

		case 7:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[5]));
			if (drawingMove != null)
				drawingMove.changeYforN(Integer.parseInt(line[4]));
			break;

		case 8:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			drawingUsesBounceThread.add(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.moveAutomatic();
			break;

		case 9:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			drawingUsesRandomly.add(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.moveRandomly();
			break;

		case 10:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			drawingUsesRandomly.add(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.cancelMotionWithAccelerometer();
			break;

		case 11:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			drawingUsesRandomly.add(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.cancelMotionAutomatic();
			break;

		case 12:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			drawingUsesRandomly.add(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.cancelMotionRandomly();
			break;
		}
	}

	public void setLook(String[] line) {
		KatbagDrawing drawingMove;
		switch (Integer.parseInt(line[3])) {
		case 0:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.hide();
			break;

		case 1:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));
			if (drawingMove != null)
				drawingMove.show();
			break;

		case 2:
			drawingMove = (KatbagDrawing) playerView.findViewById(Integer.parseInt(line[4]));

			KatbagDrawingBuilder drawingBuilder = new KatbagDrawingBuilder(mainActivity.context);
			drawingBuilder.setIdDrawing(Long.parseLong(line[5])); // this build
																	// the
																	// drawing
			Bitmap bitmap = createBitmapFromRelativeLayout(drawingBuilder);
			drawingMove.setImageBitmap(bitmap);
			drawingMove.setSizeFather(playerView.getWidth(), playerView.getHeight(), loc, playerView);
			drawingMove.setMySize(drawingBuilder.getMyWidth(), drawingBuilder.getMyHeight());
			if (editMode)
				drawingMove.setBackgroundResource(R.drawable.border_one_drawing_part);
			drawingMove.setScaleType(ScaleType.MATRIX);

			break;
		}
	}

	public void setSound(String[] line) {
		switch (Integer.parseInt(line[3])) {
		case 0:
			playSound(line[4]);
			break;

		case 1:
			muteAll = true;
			break;

		case 2:
			muteAll = false;
			break;
		}
	}

	public void playSound(String identifier) {
		Log.d("ply", "playSound - identifier");

		int sound = mainActivity.getResources().getIdentifier(identifier, "raw", mainActivity.getPackageName());
		stopPlayer();

		if (!muteAll) {
			mPlayer = MediaPlayer.create(mainActivity.context, sound);
			mPlayer.start();
		}
	}

	public void stopPlayer() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
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

	public void setWorld(long id_world) {
		world.clear();
		world = mainActivity.katbagHandler.selectWorldTypeSrcAndScaleFactorWorldForId(id_world);
		Log.d("ply", "setWorld - world size:" + world.size());
		if (world.size() > 0) {
			if (world.get(WORLD_ARRAY_TYPE).contentEquals(WORLD_TYPE_COLOR)) {
				setColorBackground(Integer.parseInt(world.get(WORLD_ARRAY_SRC)));
			} else if (world.get(WORLD_ARRAY_TYPE).contentEquals(WORLD_TYPE_CAMERA) || world.get(WORLD_ARRAY_TYPE).contentEquals(WORLD_TYPE_LIBRARY)) {
				mCurrentPhotoPath = world.get(WORLD_ARRAY_SRC);
				setPictureBackground(world.get(WORLD_ARRAY_TYPE), Integer.parseInt(world.get(WORLD_ARRAY_SCALE_FACTOR)), id_world);
			}
		}
	}

	public void editMode() {
		View line_vertical = (View) v.findViewById(R.id.player_line_vertical_editmode);
		View line_horizontal = (View) v.findViewById(R.id.player_line_horizontal_editmode);

		if (editMode) {
			line_vertical.setVisibility(View.VISIBLE);
			line_horizontal.setVisibility(View.VISIBLE);
		} else {
			line_vertical.setVisibility(View.GONE);
			line_horizontal.setVisibility(View.GONE);
		}
	}

	public void setColorBackground(int color) {
		playerView.setBackgroundColor(color);
	}

	@SuppressWarnings("deprecation")
	public void setPictureBackground(String type_world, int scaleFactor, long id_world) {
		// Get the dimensions of the View
		int targetW = playerView.getWidth();
		int targetH = playerView.getHeight();

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

		playerView.setBackgroundDrawable(background);
	}

	@Override
	public void onResume() {
		String title = "";
		if (editMode) {
			title = name_app_text + " - " + getString(R.string.title_activity_player) + " " + getString(R.string.player_title_editmode);
		} else {
			title = name_app_text + " - " + getString(R.string.title_activity_player);
		}

		mainActivity.getSupportActionBar().setTitle(title);

		editMode();

		if (!MainActivity.TABLET)
			mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		Log.d("onResume", "id_app: " + id_app);
		super.onResume();
	}

	@Override
	public void onPause() {
		if (mainActivity.inBackground) {
			getActivity().getSupportFragmentManager().popBackStack();
		}
		System.gc();
		super.onPause();
	}

	@Override
	public void onStop() {

		if (useAccelerometer)
			sensorManager.unregisterListener((SensorEventListener) this, accelerometer);

		if (drawingUsesAccelerometer.size() > 0) {
			for (int i = 0; i < drawingUsesAccelerometer.size(); i++) {
				KatbagDrawing drawing = (KatbagDrawing) playerView.findViewById(drawingUsesAccelerometer.get(i));
				drawing.sensorManager.unregisterListener((SensorEventListener) drawing.v, drawing.accelerometer);
			}
		}

		if (drawingUsesAccelerometer.size() > 0) {
			for (int i = 0; i < drawingUsesAccelerometer.size(); i++) {
				KatbagDrawing drawing = (KatbagDrawing) playerView.findViewById(drawingUsesAccelerometer.get(i));
				drawing.sensorManager.unregisterListener((SensorEventListener) drawing.v, drawing.accelerometer);
			}
		}

		if (drawingUsesBounceThread.size() > 0) {
			for (int i = 0; i < drawingUsesBounceThread.size(); i++) {
				KatbagDrawing drawing = (KatbagDrawing) playerView.findViewById(drawingUsesBounceThread.get(i));
				drawing.interruptBounce = true;
				if (drawing.bounce != null) {
					drawing.bounce.interrupt();
				}
			}
		}

		if (drawingUsesRandomly.size() > 0) {
			for (int i = 0; i < drawingUsesRandomly.size(); i++) {
				KatbagDrawing drawing = (KatbagDrawing) playerView.findViewById(drawingUsesRandomly.get(i));
				drawing.interruptRandomly = true;
				if (drawing.randomly != null) {
					drawing.randomly.interrupt();
				}
			}
		}

		stopPlayer();

		if (mHandler != null) {
			mHandler.removeCallbacks(mRunnable);
		}

		if (mHandlerPlayer != null) {
			mHandlerPlayer.removeCallbacks(mRunnablePlayer);
		}

		System.gc();
		super.onStop();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				long curTime = System.currentTimeMillis();
				// only allow one update every 100ms.
				if ((curTime - lastUpdate) > 100) {
					long diffTime = (curTime - lastUpdate);
					lastUpdate = curTime;

					x = event.values[0];
					y = event.values[1];
					z = event.values[2];

					float speed = (float) Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

					if (speed > SHAKE_THRESHOLD) {

						for (int i = 0; i < shakeBgn.size(); i++)
							setBranchTreeCondition(shakeId.get(i), 1);

						for (int i = 0; i < shakeBgn.size(); i++) {

							if (checkTreeCondition(shakeId.get(i))) {
								Log.d("ply", "shake - ini:" + shakeBgn.get(i) + ", last:" + shakeLst.get(i));
								play(shakeBgn.get(i), shakeLst.get(i) + 1);
							}
						}

						for (int i = 0; i < shakeBgn.size(); i++)
							setBranchTreeCondition(shakeId.get(i), 0);
					}

					last_x = x;
					last_y = y;
					last_z = z;
				}
			}
		}
	}

}
