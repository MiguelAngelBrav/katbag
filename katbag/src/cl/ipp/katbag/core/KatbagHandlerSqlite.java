package cl.ipp.katbag.core;

import static android.provider.BaseColumns._ID;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class KatbagHandlerSqlite extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "katbag_db.sqlite3";
	private static final int DATABASE_VERSION = 12;

	public static final String TABLE_APPLICATIONS = "applications";
	public static final String FIELD_APP_ID = "_ID";
	public static final String FIELD_APP_NAME = "app_name";
	public static final String FIELD_APP_TYPE = "app_type";

	public static final String TABLE_WORLDS = "worlds";
	public static final String FIELD_WORLD_ID = "_ID";
	public static final String FIELD_WORLD_APP_ID = "world_app_id";
	public static final String FIELD_WORLD_TYPE = "world_type";
	public static final String FIELD_WORLD_SRC = "world_src";
	public static final String FIELD_WORLD_SCALE_FACTOR = "world_scale_factor";

	public static final String TABLE_DRAWINGS = "drawings";
	public static final String FIELD_DRAWING_ID = "_ID";
	public static final String FIELD_DRAWING_APP_ID = "drawing_app_id";

	public static final String TABLE_DRAWING_PARTS = "drawing_parts";
	public static final String FIELD_DRAWING_PART_ID = "_ID";
	public static final String FIELD_DRAWING_PART_DRAWING_ID = "drawing_part_drawing_id";
	public static final String FIELD_DRAWING_PART_NAME = "drawing_part_name";
	public static final String FIELD_DRAWING_PART_TOP = "drawing_part_top";
	public static final String FIELD_DRAWING_PART_LEFT = "drawing_part_left";
	public static final String FIELD_DRAWING_PART_WIDTH = "drawing_part_width";
	public static final String FIELD_DRAWING_PART_HEIGHT = "drawing_part_height";
	public static final String FIELD_DRAWING_PART_ROTATE = "drawing_part_rotate";
	public static final String FIELD_DRAWING_PART_ORDER = "drawing_part_order";

	public static final String TABLE_DEVELOP = "develop";
	public static final String FIELD_DEVELOP_ID = "_ID";
	public static final String FIELD_DEVELOP_APP_ID = "develop_app_id";
	public static final String FIELD_DEVELOP_STATEMENT = "develop_statement";
	public static final String FIELD_DEVELOP_HUMAN_STATEMENT = "develop_huma_statement";
	public static final String FIELD_DEVELOP_VALUE_01 = "develop_value_01";
	public static final String FIELD_DEVELOP_VALUE_02 = "develop_value_02";
	public static final String FIELD_DEVELOP_VALUE_03 = "develop_value_03";
	public static final String FIELD_DEVELOP_VALUE_04 = "develop_value_04";
	public static final String FIELD_DEVELOP_VALUE_05 = "develop_value_05";
	public static final String FIELD_DEVELOP_LEVEL = "develop_level";
	public static final String FIELD_DEVELOP_ORDER = "develop_order";

	public static final String TABLE_PAGES = "pages";
	public static final String FIELD_PAGE_ID = "_ID";
	public static final String FIELD_PAGE_APP_ID = "page_app_id";
	public static final String FIELD_PAGE_WORLD_ID = "page_world_id";
	public static final String FIELD_PAGE_SOUND_ID = "page_sound_id";
	public static final String FIELD_PAGE_TEXT = "page_text";
	public static final String FIELD_PAGE_TEXT_SIZE = "page_text_size";
	public static final String FIELD_PAGE_TEXT_ALIGN = "page_text_align";
	public static final String FIELD_PAGE_TEXT_COLOR = "page_text_color";
	public static final String FIELD_PAGE_ORDER = "page_order";

	public static final int SCORE_FOR_HAVE_WORLDS = 20;
	public static final int SCORE_FOR_HAVE_DRAWINGS = 20;
	public static final int SCORE_FOR_HAVE_DEVELOPMENTS = 40;

	// ==================================================================================
	// GLOBAL
	// ==================================================================================

	public KatbagHandlerSqlite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE " + TABLE_APPLICATIONS + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_APP_NAME + " TEXT NOT NULL, " + FIELD_APP_TYPE + " TEXT NOT NULL " + ");";
		db.execSQL(query);

		query = "CREATE TABLE " + TABLE_WORLDS + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_WORLD_APP_ID + " INTEGER NOT NULL, " + FIELD_WORLD_TYPE + " TEXT NOT NULL, " + FIELD_WORLD_SRC + " TEXT NOT NULL, " + FIELD_WORLD_SCALE_FACTOR + " INTEGER NULL, " + "FOREIGN KEY(" + FIELD_WORLD_APP_ID + ") REFERENCES " + TABLE_APPLICATIONS + "(" + FIELD_APP_ID + ") ON DELETE CASCADE" + ");";
		db.execSQL(query);

		query = "CREATE TABLE " + TABLE_DRAWINGS + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_DRAWING_APP_ID + " INTEGER NOT NULL, " + "FOREIGN KEY(" + FIELD_DRAWING_APP_ID + ") REFERENCES " + TABLE_APPLICATIONS + "(" + FIELD_APP_ID + ") ON DELETE CASCADE" + ");";
		db.execSQL(query);

		query = "CREATE TABLE " + TABLE_DRAWING_PARTS + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_DRAWING_PART_DRAWING_ID + " INTEGER NOT NULL, " + FIELD_DRAWING_PART_NAME + " TEXT NOT NULL, " + FIELD_DRAWING_PART_TOP + " INTEGER NOT NULL, " + FIELD_DRAWING_PART_LEFT + " INTEGER NOT NULL, " + FIELD_DRAWING_PART_WIDTH + " INTEGER NOT NULL, " + FIELD_DRAWING_PART_HEIGHT + " INTEGER NOT NULL, " + FIELD_DRAWING_PART_ROTATE + " INTEGER NOT NULL, " + FIELD_DRAWING_PART_ORDER + " INTEGER NOT NULL, " + "FOREIGN KEY(" + FIELD_DRAWING_PART_DRAWING_ID + ") REFERENCES " + TABLE_DRAWINGS + "(" + FIELD_DRAWING_ID + ") ON DELETE CASCADE" + ");";
		db.execSQL(query);

		query = "CREATE TABLE " + TABLE_DEVELOP + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_DEVELOP_APP_ID + " INTEGER NOT NULL, " + FIELD_DEVELOP_STATEMENT + " TEXT NOT NULL, " + FIELD_DEVELOP_HUMAN_STATEMENT + " TEXT NOT NULL, " + FIELD_DEVELOP_VALUE_01 + " TEXT NOT NULL, " + FIELD_DEVELOP_VALUE_02 + " TEXT NULL, " + FIELD_DEVELOP_VALUE_03 + " TEXT NULL, " + FIELD_DEVELOP_VALUE_04 + " TEXT NULL, " + FIELD_DEVELOP_VALUE_05 + " TEXT NULL, " + FIELD_DEVELOP_LEVEL + " INTEGER NOT NULL, " + FIELD_DEVELOP_ORDER + " INTEGER NOT NULL, " + "FOREIGN KEY(" + FIELD_DEVELOP_APP_ID + ") REFERENCES " + TABLE_APPLICATIONS + "(" + FIELD_APP_ID + ") ON DELETE CASCADE" + ");";
		db.execSQL(query);

		query = "CREATE TABLE " + TABLE_PAGES + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_PAGE_APP_ID + " INTEGER NOT NULL, " + FIELD_PAGE_WORLD_ID + " INTEGER NULL, " + FIELD_PAGE_SOUND_ID + " TEXT NULL, " + FIELD_PAGE_TEXT + " TEXT NULL, " + FIELD_PAGE_TEXT_SIZE + " TEXT NULL, " + FIELD_PAGE_TEXT_ALIGN + " TEXT NULL, " + FIELD_PAGE_TEXT_COLOR + " TEXT NULL, " + FIELD_PAGE_ORDER + " INTEGER NOT NULL, " + "FOREIGN KEY(" + FIELD_PAGE_APP_ID + ") REFERENCES " + TABLE_APPLICATIONS + "(" + FIELD_APP_ID + ") ON DELETE CASCADE" + ");";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORLDS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAWINGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAWING_PARTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVELOP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGES);
		onCreate(db);
	}

	// ==================================================================================
	// GENERIC
	// ==================================================================================

	public int estimatedProgress(long id_app) {
		Cursor cursor;
		String query;
		int result = 0;

		// have a world?
		query = "select " + FIELD_WORLD_ID + " from " + TABLE_WORLDS + " where " + FIELD_WORLD_APP_ID + " = " + id_app;
		cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0)
			result = result + SCORE_FOR_HAVE_WORLDS;
		cursor.close();

		// have a drawing?
		query = "select " + FIELD_DRAWING_ID + " from " + TABLE_DRAWINGS + " where " + FIELD_DRAWING_APP_ID + " = " + id_app;
		cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0)
			result = result + SCORE_FOR_HAVE_DRAWINGS;
		cursor.close();

		// have a develop
		query = "select " + FIELD_DEVELOP_ID + " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_APP_ID + " = " + id_app;
		cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0)
			result = result + SCORE_FOR_HAVE_DEVELOPMENTS;
		cursor.close();

		return result;
	}

	// ==================================================================================
	// APP
	// ==================================================================================

	// insert new app
	public long insertApp(String name, String type) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(FIELD_APP_NAME, name);
		values.put(FIELD_APP_TYPE, type);
		id = this.getWritableDatabase().insert(TABLE_APPLICATIONS, null, values);
		this.close();

		Log.d("insertApp", "name: " + name + ", id: " + id + ", save!");

		return id;
	}

	// update name app
	public void updateNameApp(long id, String name) {
		String filter = FIELD_APP_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_APP_NAME, name);
		this.getWritableDatabase().update(TABLE_APPLICATIONS, values, filter, null);
		this.close();

		Log.d("updateNameApp", "name: " + name + ", id: " + id + ", update!");
	}

	// select all apps
	public ArrayList<String> selectAllApps() {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_APP_ID + ", " + FIELD_APP_NAME + ", " + FIELD_APP_TYPE + " from " + TABLE_APPLICATIONS + " order by " + FIELD_APP_ID + " desc ";
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0) + "&&" + cursor.getString(1) + "&&" + cursor.getString(2));
			} while (cursor.moveToNext());
			cursor.close();
		}

		return results;
	}

	// delete app for id
	public boolean deleteAppForId(long id) {
		String query = FIELD_APP_ID + " = " + id;

		Log.d("deleteAppForId", "id: " + id + ", delete!");

		return this.getWritableDatabase().delete(TABLE_APPLICATIONS, query, null) > 0;
	}

	// ==================================================================================
	// WORLD
	// ==================================================================================

	// insert new world
	public long insertWorld(long id_app, String type, String src) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(FIELD_WORLD_APP_ID, id_app);
		values.put(FIELD_WORLD_TYPE, type);
		values.put(FIELD_WORLD_SRC, src);
		id = this.getWritableDatabase().insert(TABLE_WORLDS, null, values);
		this.close();

		Log.d("insertWorld", "type: " + type + ", id: " + id + ", save!");

		return id;
	}

	// update world
	public long updateWorld(long id, String type, String src, int scale_factor) {
		String filter = FIELD_WORLD_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_WORLD_TYPE, type);
		values.put(FIELD_WORLD_SRC, src);
		values.put(FIELD_WORLD_SCALE_FACTOR, scale_factor);
		this.getWritableDatabase().update(TABLE_WORLDS, values, filter, null);
		this.close();

		Log.d("updateWorld", "type: " + type + ", src: " + src + ", scale_factor: " + scale_factor + ", id: " + id + ", update!");

		return id;
	}

	// select worlds
	public ArrayList<String> selectWorldsForIdApp(long id_app) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_WORLD_ID + " from " + TABLE_WORLDS + " where " + FIELD_WORLD_APP_ID + " = " + id_app;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0));
			} while (cursor.moveToNext());
			cursor.close();
		}

		return results;
	}

	// select worlds
	public ArrayList<String> selectWorldTypeSrcAndScaleFactorWorldForId(long id_world) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_WORLD_TYPE + ", " + FIELD_WORLD_SRC + ", " + FIELD_WORLD_SCALE_FACTOR + " from " + TABLE_WORLDS + " where " + FIELD_WORLD_ID + " = " + id_world;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			results.add(cursor.getString(0));
			results.add(cursor.getString(1));
			results.add(cursor.getString(2));
			cursor.close();
		}

		return results;
	}

	// delete world for id
	public boolean deleteWorldForId(long id) {
		String query = FIELD_WORLD_ID + " = " + id;

		Log.d("deleteWorldForId", "id: " + id + ", delete!");

		return this.getWritableDatabase().delete(TABLE_WORLDS, query, null) > 0;
	}

	// ==================================================================================
	// DRAWING
	// ==================================================================================

	// insert new drawing
	public long insertDrawing(long id_app) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(FIELD_DRAWING_APP_ID, id_app);
		id = this.getWritableDatabase().insert(TABLE_DRAWINGS, null, values);
		this.close();

		Log.d("insertDrawing", "id: " + id + ", save!");

		return id;
	}

	// select drawig
	public ArrayList<String> selectDrawingsForIdApp(long id_app) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DRAWING_ID + " from " + TABLE_DRAWINGS + " where " + FIELD_DRAWING_APP_ID + " = " + id_app;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0));
			} while (cursor.moveToNext());
			cursor.close();
		}

		return results;
	}

	// delete drawing for id
	public boolean deleteDrawingForId(long id) {
		String query = FIELD_DRAWING_ID + " = " + id;

		Log.d("deleteDrawingForId", "id: " + id + ", delete!");

		return this.getWritableDatabase().delete(TABLE_DRAWINGS, query, null) > 0;
	}

	// ==================================================================================
	// DRAWING PART
	// ==================================================================================

	// insert new drawing part
	public long insertDrawingPart(long id_drawing, String name, int top, int left, int width, int height, int rotate, int order) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(FIELD_DRAWING_PART_DRAWING_ID, id_drawing);
		values.put(FIELD_DRAWING_PART_NAME, name);
		values.put(FIELD_DRAWING_PART_TOP, top);
		values.put(FIELD_DRAWING_PART_LEFT, left);
		values.put(FIELD_DRAWING_PART_WIDTH, width);
		values.put(FIELD_DRAWING_PART_HEIGHT, height);
		values.put(FIELD_DRAWING_PART_ROTATE, rotate);
		values.put(FIELD_DRAWING_PART_ORDER, order);

		id = this.getWritableDatabase().insert(TABLE_DRAWING_PARTS, null, values);
		this.close();

		Log.d("insertDrawingPart", "id_drawing: " + id_drawing + ", id: " + id + ", save!");

		return id;
	}

	// update drawing part
	public long updateDrawingPart(long id, int top, int left, int width, int height, int rotate) {
		String filter = FIELD_DRAWING_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_DRAWING_PART_TOP, top);
		values.put(FIELD_DRAWING_PART_LEFT, left);
		values.put(FIELD_DRAWING_PART_WIDTH, width);
		values.put(FIELD_DRAWING_PART_HEIGHT, height);
		values.put(FIELD_DRAWING_PART_ROTATE, rotate);
		this.getWritableDatabase().update(TABLE_DRAWING_PARTS, values, filter, null);
		this.close();

		Log.d("updateDrawingPart", "top:" + top + ", left:" + left + ", w:" + width + ", h:" + height + ", id:" + id + ", update!");

		return id;
	}

	// update drawing part position
	public long updateDrawingPartPosition(long id, int top, int left, int width, int height) {
		String filter = FIELD_DRAWING_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_DRAWING_PART_TOP, top);
		values.put(FIELD_DRAWING_PART_LEFT, left);
		values.put(FIELD_DRAWING_PART_WIDTH, width);
		values.put(FIELD_DRAWING_PART_HEIGHT, height);
		this.getWritableDatabase().update(TABLE_DRAWING_PARTS, values, filter, null);
		this.close();

		Log.d("updateDrawingPartPosition", "top:" + top + ", left:" + left + ", w:" + width + ", h:" + height + ", id:" + id + ", update!");

		return id;
	}

	// update drawing part rotate
	public long updateDrawingPartRotate(long id, int top, int left, int width, int height, int rotate) {
		String filter = FIELD_DRAWING_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_DRAWING_PART_TOP, top);
		values.put(FIELD_DRAWING_PART_LEFT, left);
		values.put(FIELD_DRAWING_PART_WIDTH, width);
		values.put(FIELD_DRAWING_PART_HEIGHT, height);
		values.put(FIELD_DRAWING_PART_ROTATE, rotate);
		this.getWritableDatabase().update(TABLE_DRAWING_PARTS, values, filter, null);
		this.close();

		Log.d("updateDrawingPartRotate", "top:" + top + ", left:" + left + ", w:" + width + ", h:" + height + ", r:" + rotate + ", id:" + id + ", update!");

		return id;
	}

	// update drawing part order
	public long updateDrawingPartOrder(long id, int order) {
		String filter = FIELD_DRAWING_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_DRAWING_PART_ORDER, order);
		this.getWritableDatabase().update(TABLE_DRAWING_PARTS, values, filter, null);
		this.close();

		Log.d("updateDrawingPartOrder", "order:" + order + ", id:" + id + ", update!");

		return id;
	}

	// select drawig parts
	public ArrayList<String> selectDrawingsPartsForIdApp(long id_drawing) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DRAWING_PART_ID + ", " + FIELD_DRAWING_PART_NAME + ", " + FIELD_DRAWING_PART_TOP + ", " + FIELD_DRAWING_PART_LEFT + ", " + FIELD_DRAWING_PART_WIDTH + ", " + FIELD_DRAWING_PART_HEIGHT + ", " + FIELD_DRAWING_PART_ROTATE + ", " + FIELD_DRAWING_PART_DRAWING_ID + " from " + TABLE_DRAWING_PARTS + " where " + FIELD_DRAWING_PART_DRAWING_ID + " = " + id_drawing + " order by " + FIELD_DRAWING_PART_ORDER;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0) + "&&" + cursor.getString(1) + "&&" + cursor.getString(2) + "&&" + cursor.getString(3) + "&&" + cursor.getString(4) + "&&" + cursor.getString(5) + "&&" + cursor.getString(6) + "&&" + cursor.getString(7));
			} while (cursor.moveToNext());
			cursor.close();
		}

		return results;
	}

	// select rotate drawig parts
	public int selectDrawingsPartsRotateForIdApp(long id_drawing_part) {
		int result = -1;
		String query = "select " + FIELD_DRAWING_PART_ROTATE + " from " + TABLE_DRAWING_PARTS + " where " + FIELD_DRAWING_PART_ID + " = " + id_drawing_part;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			result = cursor.getInt(0);
			cursor.close();
		}

		return result;
	}

	// delete drawing part for id
	public boolean deleteDrawingPartForId(long id) {
		String query = FIELD_DRAWING_PART_ID + " = " + id;

		Log.d("deleteDrawingPartForId", "id: " + id + ", delete!");

		return this.getWritableDatabase().delete(TABLE_DRAWING_PARTS, query, null) > 0;
	}

	// ==================================================================================
	// DEVELOP
	// ==================================================================================

	// insert new develop
	public long insertDevelop(long id_app, String statement, String human_statement, String value1, String value2, String value3, String value4, String value5, int level, int order) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(FIELD_DEVELOP_APP_ID, id_app);
		values.put(FIELD_DEVELOP_STATEMENT, statement);
		values.put(FIELD_DEVELOP_HUMAN_STATEMENT, human_statement);
		values.put(FIELD_DEVELOP_VALUE_01, value1);
		values.put(FIELD_DEVELOP_VALUE_02, value2);
		values.put(FIELD_DEVELOP_VALUE_03, value3);
		values.put(FIELD_DEVELOP_VALUE_04, value4);
		values.put(FIELD_DEVELOP_VALUE_05, value5);
		values.put(FIELD_DEVELOP_LEVEL, level);
		values.put(FIELD_DEVELOP_ORDER, order);

		id = this.getWritableDatabase().insert(TABLE_DEVELOP, null, values);
		this.close();

		Log.d("insertDevelop", "id_app: " + id_app + ", id: " + id + ", save!");

		updateDevelopOrder(id, id);

		return id;
	}

	// update develop
	public long updateDevelop(long id, String statement, String human_statement, String value1, String value2, String value3, String value4, String value5) {
		String filter = FIELD_DEVELOP_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_DEVELOP_STATEMENT, statement);
		values.put(FIELD_DEVELOP_HUMAN_STATEMENT, human_statement);
		values.put(FIELD_DEVELOP_VALUE_01, value1);
		values.put(FIELD_DEVELOP_VALUE_02, value2);
		values.put(FIELD_DEVELOP_VALUE_03, value3);
		values.put(FIELD_DEVELOP_VALUE_04, value4);
		values.put(FIELD_DEVELOP_VALUE_05, value5);

		this.getWritableDatabase().update(TABLE_DEVELOP, values, filter, null);
		this.close();

		Log.d("updateDevelop", "id:" + id + ", update!");

		return id;
	}

	// update develop level
	public long updateDevelopLevel(long id, int level) {
		String filter = FIELD_DEVELOP_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_DEVELOP_LEVEL, level);
		this.getWritableDatabase().update(TABLE_DEVELOP, values, filter, null);
		this.close();

		Log.d("updateDevelopLevel", "level:" + level + ", id:" + id + ", update!");

		return id;
	}

	// update develop order
	public long updateDevelopOrder(long id, long order) {
		String filter = FIELD_DEVELOP_ID + " = " + id;
		ContentValues values = new ContentValues();
		values.put(FIELD_DEVELOP_ORDER, order);
		this.getWritableDatabase().update(TABLE_DEVELOP, values, filter, null);
		this.close();

		Log.d("updateDevelopOrder", "order:" + order + ", id:" + id + ", update!");

		return id;
	}

	// select develop
	public ArrayList<String> selectDevelopForIdApp(long id_app) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DEVELOP_ID + ", " + FIELD_DEVELOP_STATEMENT + ", " + FIELD_DEVELOP_HUMAN_STATEMENT + ", " + FIELD_DEVELOP_VALUE_01 + ", " + FIELD_DEVELOP_VALUE_02 + ", " + FIELD_DEVELOP_VALUE_03 + ", " + FIELD_DEVELOP_VALUE_04 + ", " + FIELD_DEVELOP_VALUE_05 + ", " + FIELD_DEVELOP_LEVEL + ", " + FIELD_DEVELOP_ORDER

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_APP_ID + " = " + id_app + " order by " + FIELD_DEVELOP_ORDER + " ASC";
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0) + "&&" + cursor.getString(1) + "&&" + cursor.getString(2) + "&&" + cursor.getString(3) + "&&" + cursor.getString(4) + "&&" + cursor.getString(5) + "&&" + cursor.getString(6) + "&&" + cursor.getString(7) + "&&" + cursor.getString(8) + "&&" + cursor.getString(9));
			} while (cursor.moveToNext());
			cursor.close();
		}

		return results;
	}

	// select develop book
	public ArrayList<String> selectDevelopBookForIdAppAndPageId(long id_app, long id_page) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DEVELOP_ID + ", " + FIELD_DEVELOP_STATEMENT + ", " + FIELD_DEVELOP_HUMAN_STATEMENT + ", " + FIELD_DEVELOP_VALUE_01 + ", " + FIELD_DEVELOP_VALUE_02 + ", " + FIELD_DEVELOP_VALUE_03 + ", " + FIELD_DEVELOP_VALUE_04 + ", " + FIELD_DEVELOP_VALUE_05 + ", " + FIELD_DEVELOP_LEVEL + ", " + FIELD_DEVELOP_ORDER

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_APP_ID + " = " + id_app + " and " + FIELD_DEVELOP_VALUE_05 + " = " + id_page + " order by " + FIELD_DEVELOP_ORDER + " ASC";

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0) + "&&" + cursor.getString(1) + "&&" + cursor.getString(2) + "&&" + cursor.getString(3) + "&&" + cursor.getString(4) + "&&" + cursor.getString(5) + "&&" + cursor.getString(6) + "&&" + cursor.getString(7) + "&&" + cursor.getString(8) + "&&" + cursor.getString(9));
			} while (cursor.moveToNext());
			cursor.close();
		}

		return results;
	}

	// select develop
	public ArrayList<String> selectDevelopForId(long id) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DEVELOP_STATEMENT + ", " + FIELD_DEVELOP_HUMAN_STATEMENT + ", " + FIELD_DEVELOP_VALUE_01 + ", " + FIELD_DEVELOP_VALUE_02 + ", " + FIELD_DEVELOP_VALUE_03 + ", " + FIELD_DEVELOP_VALUE_04 + ", " + FIELD_DEVELOP_VALUE_05 + ", " + FIELD_DEVELOP_LEVEL + ", " + FIELD_DEVELOP_ORDER

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_ID + " = " + id + " order by " + FIELD_DEVELOP_ORDER;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			results.add(cursor.getString(0));
			results.add(cursor.getString(1));
			results.add(cursor.getString(2));
			results.add(cursor.getString(3));
			results.add(cursor.getString(4));
			results.add(cursor.getString(5));
			results.add(cursor.getString(6));
			results.add(cursor.getString(7));
			results.add(cursor.getString(8));
			cursor.close();
		}

		return results;
	}

	// select develop for book
	public ArrayList<String> selectDevelopBookForId(long id) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DEVELOP_ID + ", " + FIELD_DEVELOP_STATEMENT + ", " + FIELD_DEVELOP_HUMAN_STATEMENT + ", " + FIELD_DEVELOP_VALUE_01 + ", " + FIELD_DEVELOP_VALUE_02 + ", " + FIELD_DEVELOP_VALUE_03 + ", " + FIELD_DEVELOP_VALUE_04 + ", " + FIELD_DEVELOP_VALUE_05 + ", " + FIELD_DEVELOP_LEVEL + ", " + FIELD_DEVELOP_ORDER

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_ID + " = " + id + " order by " + FIELD_DEVELOP_ORDER;

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			results.add(cursor.getString(0) + "&&" + cursor.getString(1) + "&&" + cursor.getString(2) + "&&" + cursor.getString(3) + "&&" + cursor.getString(4) + "&&" + cursor.getString(5) + "&&" + cursor.getString(6) + "&&" + cursor.getString(7) + "&&" + cursor.getString(8) + "&&" + cursor.getString(9));
			cursor.close();
		}

		return results;
	}

	// select develop
	public ArrayList<String> selectDevelopAllDrawing(long id_app) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DEVELOP_VALUE_01

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_STATEMENT + " = \"drawing\"" + " and " + FIELD_DEVELOP_APP_ID + " = " + id_app + " order by " + FIELD_DEVELOP_VALUE_01 + " ASC";

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0));

			} while (cursor.moveToNext());
			cursor.close();
		}

		return results;
	}

	// select develop
	public int selectDevelopStatementCount(String statement, long id_app) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DEVELOP_ID

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_STATEMENT + " = \"" + statement + "\"" + " and " + FIELD_DEVELOP_APP_ID + " = " + id_app;

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		return cursor.getCount();
	}

	// select develop
	public boolean selectDevelopDrawingExist(String value, long id_app) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DEVELOP_ID

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_STATEMENT + " = \"drawing\"" + " and " + FIELD_DEVELOP_VALUE_01 + " = \"" + value + "\"" + " and " + FIELD_DEVELOP_APP_ID + " = " + id_app;

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0)
			return true;
		else
			return false;
	}

	// select develop
	public boolean selectDevelopBookDrawingExist(String value, long id_app, long id_page) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DEVELOP_ID

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_STATEMENT + " = \"drawing\"" + " and " + FIELD_DEVELOP_VALUE_01 + " = \"" + value + "\"" + " and " + FIELD_DEVELOP_APP_ID + " = " + id_app + " and " + FIELD_DEVELOP_VALUE_05 + " = " + id_page;

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0)
			return true;
		else
			return false;
	}

	// select develop
	public boolean selectDevelopDrawingInUse(String value, long id_app) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_DEVELOP_ID

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_STATEMENT + " = \"drawing\"" + " and " + FIELD_DEVELOP_VALUE_01 + " = \"" + value + "\"" + " and " + FIELD_DEVELOP_APP_ID + " = " + id_app;

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0)
			return true;

		query = "select " + FIELD_DEVELOP_ID

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_STATEMENT + " = \"drawing\"" + " and " + FIELD_DEVELOP_VALUE_01 + " = \"" + value + "\"" + " and " + FIELD_DEVELOP_APP_ID + " = " + id_app;

		cursor = this.getReadableDatabase().rawQuery(query, null);
		if (cursor.getCount() > 0)
			return true;
		else
			return false;
	}

	// select develop book
	public int selectDevelopMotionExist(long id_app, long id_page, long id_drawing) {
		int result = -1;

		String query = "select " + FIELD_DEVELOP_ID + ", " + FIELD_DEVELOP_STATEMENT + ", " + FIELD_DEVELOP_HUMAN_STATEMENT + ", " + FIELD_DEVELOP_VALUE_01 + ", " + FIELD_DEVELOP_VALUE_02 + ", " + FIELD_DEVELOP_VALUE_03 + ", " + FIELD_DEVELOP_VALUE_04 + ", " + FIELD_DEVELOP_VALUE_05 + ", " + FIELD_DEVELOP_LEVEL + ", " + FIELD_DEVELOP_ORDER

		+ " from " + TABLE_DEVELOP + " where " + FIELD_DEVELOP_APP_ID + " = " + id_app + " and " + FIELD_DEVELOP_STATEMENT + " = " + "\"motion\"" + " and " + FIELD_DEVELOP_VALUE_02 + " = " + id_drawing + " and " + FIELD_DEVELOP_VALUE_05 + " = " + id_page + " order by " + FIELD_DEVELOP_ORDER + " ASC";

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			result = cursor.getInt(0);
			cursor.close();
		}

		return result;
	}

	// delete develop
	public boolean deleteDevelopForId(long id) {
		String query = FIELD_DEVELOP_ID + " = " + id;

		Log.d("deleteDevelopForId", "id: " + id + ", delete!");

		return this.getWritableDatabase().delete(TABLE_DEVELOP, query, null) > 0;
	}

	// ==================================================================================
	// PAGE
	// ==================================================================================

	// select pages
	public ArrayList<String> selectPagesForIdApp(long id_app) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_PAGE_ID + " from " + TABLE_PAGES + " where " + FIELD_PAGE_APP_ID + " = " + id_app + " order by " + FIELD_PAGE_ORDER + " ASC";
		;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				results.add(cursor.getString(0));
			} while (cursor.moveToNext());
			cursor.close();
		}

		return results;
	}

	// select pages
	public int countPagesForIdApp(long id_app) {
		int count = 0;
		String query = "select " + FIELD_PAGE_ID + " from " + TABLE_PAGES + " where " + FIELD_PAGE_APP_ID + " = " + id_app;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0)
			count = cursor.getCount();

		return count;
	}

	// select one page
	public ArrayList<String> selectOnePageForId(long id_app, long id_page) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_PAGE_WORLD_ID + ", " + FIELD_PAGE_SOUND_ID + ", " + FIELD_PAGE_TEXT + ", " + FIELD_PAGE_TEXT_SIZE + ", " + FIELD_PAGE_TEXT_ALIGN + ", " + FIELD_PAGE_TEXT_COLOR + ", " + FIELD_PAGE_ORDER + " from " + TABLE_PAGES + " where " + FIELD_PAGE_ID + " = " + id_page + " and " + FIELD_PAGE_APP_ID + " = " + id_app;

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			results.add(cursor.getString(0));
			results.add(cursor.getString(1));
			results.add(cursor.getString(2));
			results.add(cursor.getString(3));
			results.add(cursor.getString(4));
			results.add(cursor.getString(5));
			results.add(cursor.getString(6));
			cursor.close();
		}

		return results;
	}

	public ArrayList<String> selectOnePageForIdAndOrder(long id_app, long order) {
		ArrayList<String> results = new ArrayList<String>();
		results.clear();

		String query = "select " + FIELD_PAGE_WORLD_ID + ", " + FIELD_PAGE_SOUND_ID + ", " + FIELD_PAGE_TEXT + ", " + FIELD_PAGE_TEXT_SIZE + ", " + FIELD_PAGE_TEXT_ALIGN + ", " + FIELD_PAGE_TEXT_COLOR + ", " + FIELD_PAGE_ORDER + ", " + FIELD_PAGE_ID + " from " + TABLE_PAGES + " where " + FIELD_PAGE_APP_ID + " = " + id_app + " and " + FIELD_PAGE_ORDER + " = " + order;

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			results.add(cursor.getString(0));
			results.add(cursor.getString(1));
			results.add(cursor.getString(2));
			results.add(cursor.getString(3));
			results.add(cursor.getString(4));
			results.add(cursor.getString(5));
			results.add(cursor.getString(6));
			results.add(cursor.getString(7));
			cursor.close();
		}

		return results;
	}

	// delete page for id
	public boolean deletePageForId(long id_app, long id_page) {
		String query = FIELD_PAGE_ID + " = " + id_page + " and " + FIELD_PAGE_APP_ID + " = " + id_app;

		Log.d("deletePageForId", "id: " + id_page + ", delete!");

		return this.getWritableDatabase().delete(TABLE_PAGES, query, null) > 0;
	}

	// insert new page
	public long insertPage(long id_app, String text, long order) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(FIELD_PAGE_APP_ID, id_app);
		values.put(FIELD_PAGE_TEXT, text);
		values.put(FIELD_PAGE_ORDER, order);

		id = this.getWritableDatabase().insert(TABLE_PAGES, null, values);
		this.close();

		Log.d("insertPage", "id: " + id + ", save!");

		String query = "select " + FIELD_PAGE_APP_ID + " from " + TABLE_PAGES + " where " + FIELD_PAGE_APP_ID + " = " + id_app;
		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		updatePageOrder(id_app, id, cursor.getCount() - 1);

		return id;
	}

	// update page order
	public long updatePageOrder(long id_app, long id, long order) {
		String filter = FIELD_PAGE_ID + " = " + id + " and " + FIELD_PAGE_APP_ID + " = " + id_app;
		ContentValues values = new ContentValues();
		values.put(FIELD_PAGE_ORDER, order);
		this.getWritableDatabase().update(TABLE_PAGES, values, filter, null);
		this.close();

		Log.d("updatePageOrder", "order:" + order + ", id:" + id + ", update!");

		return id;
	}

	// update page world
	public long updatePageWorld(long id_app, long id, long id_world) {
		String filter = FIELD_PAGE_ID + " = " + id + " and " + FIELD_PAGE_APP_ID + " = " + id_app;
		ContentValues values = new ContentValues();
		values.put(FIELD_PAGE_WORLD_ID, id_world);
		this.getWritableDatabase().update(TABLE_PAGES, values, filter, null);
		this.close();

		Log.d("updatePageWorld", "world:" + id_world + ", id:" + id + ", update!");

		return id;
	}

	// update page sound
	public long updatePageSound(long id_app, long id, String id_sound) {
		String filter = FIELD_PAGE_ID + " = " + id + " and " + FIELD_PAGE_APP_ID + " = " + id_app;
		ContentValues values = new ContentValues();
		values.put(FIELD_PAGE_SOUND_ID, id_sound);
		this.getWritableDatabase().update(TABLE_PAGES, values, filter, null);
		this.close();

		Log.d("updatePageWorld", "sound:" + id_sound + ", id:" + id + ", update!");

		return id;
	}

	// update page text
	public long updatePageText(long id_app, long id, String text) {
		String filter = FIELD_PAGE_ID + " = " + id + " and " + FIELD_PAGE_APP_ID + " = " + id_app;
		ContentValues values = new ContentValues();
		values.put(FIELD_PAGE_TEXT, text);
		this.getWritableDatabase().update(TABLE_PAGES, values, filter, null);
		this.close();

		Log.d("updatePageTextAlign", "text:" + text + ", id:" + id + ", update!");

		return id;
	}

	// update page text align
	public long updatePageTextAlign(long id_app, long id, String align) {
		String filter = FIELD_PAGE_ID + " = " + id + " and " + FIELD_PAGE_APP_ID + " = " + id_app;
		ContentValues values = new ContentValues();
		values.put(FIELD_PAGE_TEXT_ALIGN, align);
		this.getWritableDatabase().update(TABLE_PAGES, values, filter, null);
		this.close();

		Log.d("updatePageTextAlign", "align:" + align + ", id:" + id + ", update!");

		return id;
	}

	// update page text size
	public long updatePageTextSize(long id_app, long id, int size) {
		String filter = FIELD_PAGE_ID + " = " + id + " and " + FIELD_PAGE_APP_ID + " = " + id_app;
		ContentValues values = new ContentValues();
		values.put(FIELD_PAGE_TEXT_SIZE, size);
		this.getWritableDatabase().update(TABLE_PAGES, values, filter, null);
		this.close();

		Log.d("updatePageTextAlign", "size:" + size + ", id:" + id + ", update!");

		return id;
	}

	// update page text color
	public long updatePageTextColor(long id_app, long id, int color) {
		String filter = FIELD_PAGE_ID + " = " + id + " and " + FIELD_PAGE_APP_ID + " = " + id_app;
		ContentValues values = new ContentValues();
		values.put(FIELD_PAGE_TEXT_COLOR, color);
		this.getWritableDatabase().update(TABLE_PAGES, values, filter, null);
		this.close();

		Log.d("updatePageTextAlign", "color:" + color + ", id:" + id + ", update!");

		return id;
	}

	// delete develop
	public boolean deleteDrawingFromPageForId(long id_app, long id_page, long id_drawing) {
		String query = FIELD_DEVELOP_APP_ID + " = " + id_app + " and " + FIELD_DEVELOP_VALUE_01 + " = " + id_drawing + " and " + FIELD_DEVELOP_VALUE_05 + " = " + id_page;
		boolean result = false;
		if (this.getWritableDatabase().delete(TABLE_DEVELOP, query, null) > 0) {
			query = FIELD_DEVELOP_APP_ID + " = " + id_app + " and " + FIELD_DEVELOP_VALUE_02 + " = " + id_drawing + " and " + FIELD_DEVELOP_VALUE_05 + " = " + id_page;
			if (this.getWritableDatabase().delete(TABLE_DEVELOP, query, null) > 0) {
				result = true;
				Log.d("deleteDrawingFromPageForId", "id_app:" + id_app + ", id_page:" + id_page + ", delete!");
			}
		}

		return result;
	}

	// ==================================================================================
	// DEVELOPER SECTION
	// ==================================================================================

	// develop database
	public void select(String query, boolean onlyRow) {
		Log.w("select", "==================================================================================");

		if (!onlyRow) {
			Log.w("select", "Select: " + query);
			Log.w("select", "----------------------------------------------------------------------------------");
		}

		Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

		String result = "";
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			if (!onlyRow) {
				Log.w("select", "Column_count: " + cursor.getColumnCount() + " - Row_count: " + cursor.getCount());
				Log.w("select", "----------------------------------------------------------------------------------");
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					result += cursor.getColumnName(i) + " |";
				}

				result = result.substring(0, result.length() - 1);
				Log.w("select", result);
				Log.w("select", "----------------------------------------------------------------------------------");
			}

			do {
				result = "";
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					result += cursor.getString(i) + " |";
				}
				result = result.substring(0, result.length() - 1);
				Log.w("select", result);
				result = "";

			} while (cursor.moveToNext());
			cursor.close();
		}
	}
}
