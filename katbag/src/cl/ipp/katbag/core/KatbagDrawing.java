package cl.ipp.katbag.core;

import java.util.Random;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cl.ipp.katbag.MainActivity;

public class KatbagDrawing extends ImageView implements OnTouchListener, SensorEventListener {

	public ImageView v = null;
	private int myWidth = 0, myHeight = 0, myMiddleWidth = 0, myMiddleHeight = 0;
	private int leftIni = 0, topIni = 0, leftMax = 0, topMax = 0, leftMin = 0, topMin = 0, leftColl = 0, topColl = 0;
	private int[] loc;
	private boolean moveOnlyAxisX = false;
	private boolean moveOnlyAxisY = false;
	private boolean onTouch = false;
	private int widthFather = 0, heightFather = 0, widthMiddleFather = 0, heightMiddleFather = 0;
	private MainActivity mainActivity;

	private static int left, top;
	public SensorManager sensorManager;
	public Sensor accelerometer;

	protected enum HorizontalDirection {
		LEFT, RIGHT
	}

	protected enum VerticalDirection {
		UP, DOWN
	}

	protected HorizontalDirection myXDirection = HorizontalDirection.RIGHT;
	protected VerticalDirection myYDirection = VerticalDirection.UP;
	public Thread bounce = null;
	public Thread randomly = null;
	public boolean interruptBounce = false;
	public boolean interruptRandomly = false;
	public RelativeLayout playerView;

	public KatbagDrawing(Context context) {
		super(context);
		v = this;
		this.mainActivity = (MainActivity) context;
	}

	public void setSizeFather(int wf, int hf, int[] loc, RelativeLayout player) {
		widthFather = wf;
		heightFather = hf;

		widthMiddleFather = (wf / 2);
		heightMiddleFather = (hf / 2);

		this.loc = loc;

		playerView = player;
	}

	public void getMySize() {
		Log.d("getMySize", "w:" + getMyWidth() + " h:" + getMyHeight());
	}

	public void setMySize(int w, int h) {
		myWidth = w;
		myHeight = h;

		myMiddleWidth = (w / 2);
		myMiddleHeight = (h / 2);

		leftMax = widthFather - ((w / 4) * 3);
		topMax = heightFather - ((h / 4) * 3);

		leftMin = -(w / 4);
		topMin = -(h / 4);

		leftColl = (w / 4);
		topColl = (h / 4);
	}

	public int getMyWidth() {
		return myWidth;
	}

	public int getMyHeight() {
		return myHeight;
	}

	public void moveSteps(final int nSteps) {
		final int s;
		if (nSteps != 0) {
			s = 10 * nSteps;

			int left = v.getLeft() + s;
			if (left > leftMax)
				left = leftMax;
			if (left < leftMin)
				left = leftMin;

			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
			lp.leftMargin = left;
			v.setLayoutParams(lp);

			Log.d("moveSteps", "s:" + s);
		}
	}

	public void moveToXY(int left, int top) {

		if (left > leftMax)
			left = leftMax;
		if (left < leftMin)
			left = leftMin;

		if (top > topMax)
			top = topMax;
		if (top < topMin)
			top = topMin;

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
		lp.leftMargin = left;
		lp.topMargin = top;
		v.setLayoutParams(lp);
	}

	public void moveOnlyAxisY() {
		moveOnlyAxisY = true;
		v.setOnTouchListener(this);
	}

	public void moveOnlyAxisX() {
		moveOnlyAxisX = true;
		v.setOnTouchListener(this);
	}

	public void moveToCenter() {
		int left = widthMiddleFather - myMiddleWidth;
		int top = heightMiddleFather - myMiddleHeight;

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
		lp.leftMargin = left;
		lp.topMargin = top;
		v.setLayoutParams(lp);
	}

	public void moveWithAccelerometer() {
		sensorManager = (SensorManager) mainActivity.context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void changeXforN(int left) {
		if (left > leftMax)
			left = leftMax;
		if (left < leftMin)
			left = leftMin;

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
		lp.leftMargin = left;
		v.setLayoutParams(lp);
	}

	public void changeYforN(int top) {
		if (top > topMax)
			top = topMax;
		if (top < topMin)
			top = topMin;

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
		lp.topMargin = top;
		v.setLayoutParams(lp);
	}

	public void moveAutomatic() {
		if (bounce == null) {
			bounce = new Thread(new Runnable() {

				@Override
				public void run() {
					while (!Thread.interrupted() && !interruptBounce) {
						try {
							Thread.sleep(10);
							mainActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									left = v.getLeft();
									top = v.getTop();

									if (left >= leftMax)
										myXDirection = HorizontalDirection.LEFT;
									else if (left <= leftMin)
										myXDirection = HorizontalDirection.RIGHT;

									if (top >= topMax)
										myYDirection = VerticalDirection.UP;
									else if (top <= topMin)
										myYDirection = VerticalDirection.DOWN;

									if (myXDirection == HorizontalDirection.RIGHT)
										left += 5;
									else
										left -= 5;

									if (myYDirection == VerticalDirection.DOWN)
										top += 5;
									else
										top -= 5;

									if (!onTouch) {
										RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
										lp.leftMargin = left;
										lp.topMargin = top;
										v.setLayoutParams(lp);
									}
								}
							});
						} catch (InterruptedException e) {
							Log.w("moveAutomatic", "InterruptedException !!");
						}
					}
				}
			});

			bounce.start();
		}
	}

	public void moveRandomly() {
		if (randomly == null) {
			randomly = new Thread(new Runnable() {

				@Override
				public void run() {
					while (!Thread.interrupted() && !interruptRandomly) {
						try {
							Thread.sleep(500);
							mainActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									left = v.getLeft();
									top = v.getTop();

									Random lf = new Random();
									Random rg = new Random();

									left = lf.nextInt(leftMax - leftMin + 1) + leftMin;
									top = rg.nextInt(topMax - topMin + 1) + topMin;

									if (!onTouch) {
										RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
										lp.leftMargin = left;
										lp.topMargin = top;
										v.setLayoutParams(lp);
									}
								}
							});
						} catch (InterruptedException e) {
							Log.w("moveRandomly", "InterruptedException !!");
						}
					}
				}
			});

			randomly.start();
		}
	}

	public void cancelMotionWithAccelerometer() {
		this.sensorManager.unregisterListener((SensorEventListener) this, accelerometer);
	}

	public void cancelMotionAutomatic() {
		this.interruptBounce = true;
		this.bounce.interrupt();
		this.bounce = null;
	}

	public void cancelMotionRandomly() {
		this.interruptRandomly = true;
		this.randomly.interrupt();
		this.randomly = null;
	}

	public void hide() {
		v.setVisibility(View.GONE);
	}

	public void show() {
		v.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onTouch = true;
			leftIni = (int) event.getRawX() - (loc[0] + v.getLeft());
			topIni = (int) event.getRawY() - (loc[1] + v.getTop());

			break;

		case MotionEvent.ACTION_UP:
			onTouch = false;
			break;

		case MotionEvent.ACTION_MOVE:
			onTouch = true;
			int left = (int) event.getRawX() - (loc[0] + leftIni);
			int top = (int) event.getRawY() - (loc[1] + topIni);

			if (left < leftMin)
				left = leftMin;
			if (top < topMin)
				top = topMin;
			if (left > leftMax)
				left = leftMax;
			if (top > topMax)
				top = topMax;

			if (moveOnlyAxisX && moveOnlyAxisY) {

			} else {
				if (moveOnlyAxisX)
					top = v.getTop();
				if (moveOnlyAxisY)
					left = v.getLeft();
			}

			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
			lp.leftMargin = left;
			lp.topMargin = top;
			v.setLayoutParams(lp);

			super.invalidate();

			break;

		}

		if (moveOnlyAxisX || moveOnlyAxisY)
			return true;
		else
			return false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				left = v.getLeft();
				top = v.getTop();

				if (MainActivity.TABLET) { // landscape
					if (event.values[1] > 0) {
						if (left <= leftMax)
							left = left + (int) Math.pow(event.values[1], 2);
					} else {
						if (left >= leftMin)
							left = left - (int) Math.pow(event.values[1], 2);
					}

					if (event.values[0] > 0) {
						if (top <= topMax)
							top = top + (int) Math.pow(event.values[0], 2);
					} else {
						if (top >= topMin)
							top = top - (int) Math.pow(event.values[0], 2);
					}

				} else { // portrait
					if (event.values[0] < 0) {
						if (left <= leftMax)
							left = left + (int) Math.pow(event.values[0], 2);
					} else {
						if (left >= leftMin)
							left = left - (int) Math.pow(event.values[0], 2);
					}

					if (event.values[1] > 0) {
						if (top <= topMax)
							top = top + (int) Math.pow(event.values[1], 2);
					} else {
						if (top >= topMin)
							top = top - (int) Math.pow(event.values[1], 2);
					}
				}

				if (!onTouch) {
					RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
					lp.leftMargin = left;
					lp.topMargin = top;
					v.setLayoutParams(lp);

					super.invalidate();
				}
			}
		}
	}

	public int collisionDetection() {
		Rect other = new Rect();
		Rect iAm = new Rect();
		int left = 0, top = 0, right = 0, bottom = 0;

		left = (v.getLeft() - leftColl);
		top = (v.getTop() - topColl);
		right = (left + (getMyWidth() - leftColl));
		bottom = (top + (getMyHeight() - topColl));

		iAm.set(left, top, right, bottom);

		int idDrw = -1;

		for (int i = 0; i < playerView.getChildCount(); i++) {
			KatbagDrawing drw = (KatbagDrawing) playerView.getChildAt(i);
			if (drw.getId() != getId()) {

				left = (drw.getLeft() - drw.leftColl);
				top = (drw.getTop() - drw.topColl);
				right = (left + (drw.getMyWidth() - drw.leftColl));
				bottom = (top + (drw.getMyHeight() - drw.topColl));

				other.set(left, top, right, bottom);
				if (iAm.intersect(other))
					idDrw = drw.getId();
			}

		}

		Log.d("collisionDetection", "idDrw:" + idDrw);

		return idDrw;
	}

}
