/*
 * Author: Miguel Angel Bravo (@MiguelAngelBrav)
 *  
 * Copyright (C) 2014 The Android Open Source Project Katbag of IPP and Miguel Angel Bravo
 * Licensed under the Apache 2.0 License.
 */

package cl.ipp.katbag.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Spinner;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.TextView;
import cl.ipp.katbag.MainActivity;
import cl.ipp.katbag.R;
import cl.ipp.katbag.core.KatbagUtilities;
import cl.ipp.katbag.row_adapters.DevelopRowAdapter;
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
import com.mobeta.android.dslv.DragSortListView;

public class Develop extends SherlockFragment {

	private Tracker tracker;
	
  static View v = null;
  public MainActivity mainActivity;
  public static long id_app = -1;
  public TextView notRegister;
  public DragSortListView developListView;
  public DevelopRowAdapter adapter;
  public static boolean editMode = false;
  public MenuItem menuItemEdit, menuItemAddWorld, menuItemAddDrawing, menuItemAddMotion, menuItemAddLook, menuItemAddSound, menuItemAddControl, menuItemAddSensing, menuItemPlayer;
  public boolean changeOrder = false;
  public DialogDevelopRowAdapter adapterDialog;
  public DialogSoundRowAdapter adapterDialogSound;

  private String resTitle = "";
  private ArrayList<String> dialogList = new ArrayList<String>();
  private ArrayList<String> dialogIdObjectList = new ArrayList<String>();
  private ArrayList<String> dialogHumanStatement = new ArrayList<String>();
  private ArrayList<String> dialogHumanStatementRow = new ArrayList<String>();

  public static final int OBJECT_WORLD = 0;
  public static final int OBJECT_DRAWING = 1;
  public static final int OBJECT_MOTION = 2;
  public static final int OBJECT_LOOK = 3;
  public static final int OBJECT_SOUND = 4;
  public static final int OBJECT_CONTROL = 5;
  public static final int OBJECT_SENSING = 6;

  private Spinner spinner_drawing_1, spinner_drawing_2;
  private EditText editN, editX, editY;
  private ArrayList<String> spinnerList = new ArrayList<String>();
  private ArrayList<String> drawing1List = new ArrayList<String>();

  private String drawingText = "";
  private String drawingId = "";

  private String drawingText_1 = "";
  private String drawingId_1 = "";

  private String drawingText_2 = "";
  private String drawingId_2 = "";

  private ArrayList<String> dev = new ArrayList<String>();

  public Fragment mFragment;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    
    this.tracker = EasyTracker.getInstance(this.getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mainActivity = (MainActivity) super.getActivity();
    v = inflater.inflate(R.layout.fragment_develop, container, false);

    // rescues parameters
    Bundle bundle = getArguments();
    if (bundle != null) {
      id_app = bundle.getLong("id_app");
    }

    notRegister = (TextView) v.findViewById(R.id.develop_not_register);
    editMode = false;
    loadListView();

    developListView.setOnItemLongClickListener(new OnItemLongClickListener() {

      @Override
      public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
        if (!editMode) {
          dev.clear();
          dev = mainActivity.katbagHandler.selectDevelopForId(Long.parseLong(adapter.items.get(position)));

          dialogIdObjectList.clear();
          dialogHumanStatement.clear();
          dialogHumanStatementRow.clear();

          if (dev.get(0).contentEquals("world")) {
            showAlertDialog(OBJECT_WORLD, Long.parseLong(adapter.items.get(position)));

          } else if (dev.get(0).contentEquals("drawing")) {
            showAlertDialog(OBJECT_DRAWING, Long.parseLong(adapter.items.get(position)));

          } else if (dev.get(0).contentEquals("motion")) {
            dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.motion_id)));
            dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.motion_name)));
            dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.motion_row)));

            setMotion(getString(R.string.dialog_title_select) + " " + getString(R.string.develop_dropdown_menu_add_motion), dialogHumanStatementRow.get(Integer.parseInt(dev.get(2))), Integer.parseInt(dialogIdObjectList.get(Integer.parseInt(dev.get(2)))), Long.parseLong(adapter.items.get(position)));

          } else if (dev.get(0).contentEquals("look")) {
            dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.look_id)));
            dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.look_name)));
            dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.look_row)));

            setLook(getString(R.string.dialog_title_select) + " " + getString(R.string.develop_dropdown_menu_add_look), dialogHumanStatementRow.get(Integer.parseInt(dev.get(2))), Integer.parseInt(dialogIdObjectList.get(Integer.parseInt(dev.get(2)))), Long.parseLong(adapter.items.get(position)));

          } else if (dev.get(0).contentEquals("sound")) {
            dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sound_id)));
            dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sound_name)));
            dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sound_row)));

            setSound(getString(R.string.dialog_title_select) + " " + getString(R.string.develop_dropdown_menu_add_sound), dialogHumanStatementRow.get(Integer.parseInt(dev.get(2))), Integer.parseInt(dialogIdObjectList.get(Integer.parseInt(dev.get(2)))), Long.parseLong(adapter.items.get(position)));

          } else if (dev.get(0).contentEquals("control")) {
            dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.control_id)));
            dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.control_name)));
            dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.control_row)));

            setControl(getString(R.string.dialog_title_select) + " " + getString(R.string.develop_dropdown_menu_add_control), dialogHumanStatementRow.get(Integer.parseInt(dev.get(2))), Integer.parseInt(dialogIdObjectList.get(Integer.parseInt(dev.get(2)))), Long.parseLong(adapter.items.get(position)));

          } else if (dev.get(0).contentEquals("sensing")) {
            dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sensing_id)));
            dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sensing_name)));
            dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sensing_row)));

            setSensing(getString(R.string.dialog_title_select) + " " + getString(R.string.develop_dropdown_menu_add_sensing), dialogHumanStatementRow.get(Integer.parseInt(dev.get(2))), Integer.parseInt(dialogIdObjectList.get(Integer.parseInt(dev.get(2)))), Long.parseLong(adapter.items.get(position)));
          }

          return true;
        } else
          return false;
      }
    });

    developListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
        indentCode(position);
        loadListView();
      }
    });

    return v;
  }

  public void indentCode(int position) {
    if (position > 0) {

      dev.clear();
      dev = mainActivity.katbagHandler.selectDevelopForId(Long.parseLong(adapter.items.get(position - 1)));
      int beforeLevel = Integer.parseInt(dev.get(7));

      boolean beforeIndent = canIndent(dev.get(0), dev.get(2), position);

      dev.clear();
      dev = mainActivity.katbagHandler.selectDevelopForId(Long.parseLong(adapter.items.get(position)));
      int level = Integer.parseInt(dev.get(7));

      boolean indent = canIndent(dev.get(0), dev.get(2), position);

      int newLevel = level;
      if ((level == beforeLevel) && (beforeIndent)) {
        newLevel++;
      } else if ((level == beforeLevel) && (indent)) {
        newLevel = 0;
      } else if ((level == beforeLevel) && (!indent)) {
        newLevel = 0;
      } else if (level < beforeLevel) {
        newLevel++;
      } else if (level > beforeLevel) {
        newLevel = 0;
      }

      if (newLevel != level) {
        mainActivity.katbagHandler.updateDevelopLevel(Long.parseLong(adapter.items.get(position)), newLevel);
        int variation = -1;
        variation = newLevel - level;

        if (canIndent(dev.get(0), dev.get(2), position)) {
          for (int i = (position + 1); i <= adapter.getCount() - 1; i++) {
            dev.clear();
            dev = mainActivity.katbagHandler.selectDevelopForId(Long.parseLong(adapter.items.get(i)));
            int levelNext = Integer.parseInt(dev.get(7));
            if (levelNext > level) {
              mainActivity.katbagHandler.updateDevelopLevel(Long.parseLong(adapter.items.get(i)), levelNext + variation);
            } else {
              break;
            }
          }
        }
      }

      reIndent();
    }
  }

  public void reIndentOne(int position) {
    int newLevel = 0;
    if (position > 0) {
      dev.clear();
      dev = mainActivity.katbagHandler.selectDevelopForId(Long.parseLong(adapter.items.get(position - 1)));
      int levelPrevious = Integer.parseInt(dev.get(7));

      boolean preIndent = canIndent(dev.get(0), dev.get(2), position);
      if (preIndent) {
        newLevel = levelPrevious + 1;
      } else {
        newLevel = levelPrevious;
      }
    }

    mainActivity.katbagHandler.updateDevelopLevel(Long.parseLong(adapter.items.get(position)), newLevel);
  }

  public void reIndent() {
    int level = 0;
    int beforeLevel = 0;
    int diff = 0;
    boolean beforeCanIdent = false;
    for (int i = 0; i <= adapter.getCount() - 1; i++) {
      dev.clear();
      dev = mainActivity.katbagHandler.selectDevelopForId(Long.parseLong(adapter.items.get(i)));
      int mLevel = Integer.parseInt(dev.get(7));

      if ((i == 0) && (mLevel != 0)) {
        level = 0;
      } else {
        if (beforeLevel < mLevel) {
          diff = mLevel - beforeLevel;
          if (diff >= 1) {
            if (beforeCanIdent)
              mLevel = (mLevel - diff) + 1;
            else
              mLevel = (mLevel - diff);
          }
        }

        level = mLevel;
      }

      beforeCanIdent = canIndent(dev.get(0), dev.get(2), i);
      beforeLevel = level;

      mainActivity.katbagHandler.updateDevelopLevel(Long.parseLong(adapter.items.get(i)), level);
    }
  }

  public boolean canIndent(String statement, String value, int position) {
    dialogList.clear();
    if (statement.contentEquals("motion")) {

    } else if (statement.contentEquals("look")) {

    } else if (statement.contentEquals("sound")) {

    } else if (statement.contentEquals("control")) {
      dialogList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.control_indent)));

    } else if (statement.contentEquals("sensing")) {
      dialogList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sensing_indent)));
    }

    boolean indent = false;
    if (dialogList.size() > 0) {
      for (int i = 0; i < dialogList.size(); i++) {
        if (value.contentEquals(dialogList.get(i))) {
          indent = true;
          break;
        }
      }
    }

    return indent;
  }

  public void loadListView() {
    developListView = (DragSortListView) v.findViewById(R.id.develop_list_view);

    List<String> list = new ArrayList<String>();
    List<String> idList = new ArrayList<String>();
    List<String> statementList = new ArrayList<String>();
    List<String> humanStatementList = new ArrayList<String>();
    List<String> levelList = new ArrayList<String>();

    list.clear();
    list = mainActivity.katbagHandler.selectDevelopForIdApp(id_app);

    idList.clear();
    statementList.clear();
    humanStatementList.clear();
    levelList.clear();

    if (list.size() <= 0) {
      notRegister.setVisibility(View.VISIBLE);

    } else {
      notRegister.setVisibility(View.GONE);

      for (int i = 0; i < list.size(); i++) {
        String[] arr = list.get(i).toString().split("&&");
        idList.add(arr[0]);
        statementList.add(arr[1]);
        humanStatementList.add(arr[2]);
        levelList.add(arr[8]);
      }

      Parcelable state = developListView.onSaveInstanceState();
      adapter = new DevelopRowAdapter(v.getContext(), mainActivity, R.layout.row_develop, idList);

      developListView.setAdapter(adapter);
      developListView.setRemoveListener(onRemove);
      developListView.setDragScrollProfile(ssProfile);
      developListView.setDropListener(onDrop);
      developListView.onRestoreInstanceState(state);
    }
  }

  private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {

    @Override
    public void remove(int which) {
      String item = (String) adapter.getItem(which);
      adapter.remove(item);

      mainActivity.katbagHandler.deleteDevelopForId(Long.parseLong(item));

      reIndent();

      adapter.notifyDataSetChanged();
      developListView.refreshDrawableState();
    }
  };

  private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
    @Override
    public void drop(int from, int to) {
      if (from != to) {

        String item = (String) adapter.getItem(from);
        adapter.remove(item);
        adapter.insert(item, to);

        reIndent();

        adapter.notifyDataSetChanged();
        developListView.refreshDrawableState();
      }
    }
  };

  private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
    @Override
    public float getSpeed(float w, long t) {
      if (w > 0.8f) {
        // Traverse all views in a millisecond
        return ((float) developListView.getCount()) / 0.001f;
      } else {
        return 10.0f * w;
      }
    }
  };

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.clear();
    inflater.inflate(R.menu.develop, menu);

    menuItemEdit = menu.findItem(R.id.develop_dropdown_menu_edit);
    menuItemAddWorld = menu.findItem(R.id.develop_dropdown_menu_add_world);
    menuItemAddDrawing = menu.findItem(R.id.develop_dropdown_menu_add_drawing);
    menuItemAddMotion = menu.findItem(R.id.develop_dropdown_menu_add_motion);
    menuItemAddLook = menu.findItem(R.id.develop_dropdown_menu_add_look);
    menuItemAddSound = menu.findItem(R.id.develop_dropdown_menu_add_sound);
    menuItemAddControl = menu.findItem(R.id.develop_dropdown_menu_add_control);
    menuItemAddSensing = menu.findItem(R.id.develop_dropdown_menu_add_sensing);
    menuItemPlayer = menu.findItem(R.id.develop_dropdown_menu_player);

    menuItemEdit.setOnMenuItemClickListener(new OnMenuItemClickListener() {

      @Override
      public boolean onMenuItemClick(MenuItem item) {
        if (editMode) {
          editMode = false;
          menuItemEdit.setIcon(R.drawable.ic_action_edit);
        } else {
          editMode = true;
          menuItemEdit.setIcon(R.drawable.ic_action_accept);
        }

        loadListView();

        return true;
      }
    });

    menuItemAddWorld.setOnMenuItemClickListener(new OnMenuItemClickListener() {

      @Override
      public boolean onMenuItemClick(MenuItem item) {
        showAlertDialog(OBJECT_WORLD, -1);
        return true;
      }
    });

    menuItemAddDrawing.setOnMenuItemClickListener(new OnMenuItemClickListener() {

      @Override
      public boolean onMenuItemClick(MenuItem item) {
        showAlertDialog(OBJECT_DRAWING, -1);
        return true;
      }
    });

    menuItemAddMotion.setOnMenuItemClickListener(new OnMenuItemClickListener() {

      @Override
      public boolean onMenuItemClick(MenuItem item) {
        showAlertDialog(OBJECT_MOTION, -1);
        return true;
      }
    });

    menuItemAddLook.setOnMenuItemClickListener(new OnMenuItemClickListener() {

      @Override
      public boolean onMenuItemClick(MenuItem item) {
        showAlertDialog(OBJECT_LOOK, -1);
        return true;
      }
    });

    menuItemAddSound.setOnMenuItemClickListener(new OnMenuItemClickListener() {

      @Override
      public boolean onMenuItemClick(MenuItem item) {
        showAlertDialog(OBJECT_SOUND, -1);
        return true;
      }
    });

    menuItemAddControl.setOnMenuItemClickListener(new OnMenuItemClickListener() {

      @Override
      public boolean onMenuItemClick(MenuItem item) {
        showAlertDialog(OBJECT_CONTROL, -1);
        return true;
      }
    });

    menuItemAddSensing.setOnMenuItemClickListener(new OnMenuItemClickListener() {

      @Override
      public boolean onMenuItemClick(MenuItem item) {
        showAlertDialog(OBJECT_SENSING, -1);
        return true;
      }
    });

    menuItemPlayer.setOnMenuItemClickListener(new OnMenuItemClickListener() {

      @Override
      public boolean onMenuItemClick(MenuItem item) {
        mFragment = new Player();

        Bundle bundle = new Bundle();
        bundle.putLong("id_app", id_app);
        bundle.putBoolean("editMode", true);
        bundle.putString("name_app", Add.name_app_text);
        mFragment.setArguments(bundle);

        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.fragment_main_container, mFragment);
        t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        t.addToBackStack(mFragment.getClass().getSimpleName());
        t.commit();
        return true;
      }
    });

    super.onCreateOptionsMenu(menu, inflater);
  }

  public void showAlertDialog(final int object, final long object_id) {

    int countDrawings = mainActivity.katbagHandler.selectDevelopStatementCount("drawing", id_app);
    if ((countDrawings == 0) && (object != OBJECT_DRAWING) && (object != OBJECT_WORLD)) {
      KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_not_drawing));

    } else {
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
        if (object_id == -1) { // only insert, not update
          resTitle = getString(R.string.develop_dropdown_menu_add_drawing);
          dialogList = mainActivity.katbagHandler.selectDrawingsForIdApp(id_app);
          for (int i = 0; i < dialogList.size(); i++) {
            dialogIdObjectList.add(dialogList.get(i));
            dialogHumanStatement.add(getString(R.string.drawings_row_name) + " " + dialogList.get(i));
          }
        } else {
          return;
        }

        break;

      case OBJECT_MOTION:
        resTitle = getString(R.string.develop_dropdown_menu_add_motion);
        dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.motion_id)));
        dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.motion_name)));
        dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.motion_row)));
        break;

      case OBJECT_LOOK:
        resTitle = getString(R.string.develop_dropdown_menu_add_look);
        dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.look_id)));
        dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.look_name)));
        dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.look_row)));
        break;

      case OBJECT_SOUND:
        resTitle = getString(R.string.develop_dropdown_menu_add_sound);
        dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sound_id)));
        dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sound_name)));
        dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sound_row)));
        break;

      case OBJECT_CONTROL:
        resTitle = getString(R.string.develop_dropdown_menu_add_control);
        dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.control_id)));
        dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.control_name)));
        dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.control_row)));
        break;

      case OBJECT_SENSING:
        resTitle = getString(R.string.develop_dropdown_menu_add_sensing);
        dialogIdObjectList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sensing_id)));
        dialogHumanStatement = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sensing_name)));
        dialogHumanStatementRow = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sensing_row)));
        break;
      }

      adapterDialog = new DialogDevelopRowAdapter(v.getContext(), R.layout.row_dialog_develop, dialogIdObjectList, dialogHumanStatement);

      AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.context);
      builder.setTitle(getString(R.string.dialog_title_select) + " " + resTitle);
      builder.setAdapter(adapterDialog, new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
          switch (object) {
          case OBJECT_WORLD:
            if (object_id == -1) {
              mainActivity.katbagHandler.insertDevelop(id_app, "world", dialogHumanStatement.get(which), dialogIdObjectList.get(which), "", "", "", "", "", "", "", 0, 0);
            } else {
              mainActivity.katbagHandler.updateDevelop(object_id, "world", dialogHumanStatement.get(which), dialogIdObjectList.get(which), "", "", "", "", "", "", "");
            }
            break;

          case OBJECT_DRAWING: // only insert, not update
            if (mainActivity.katbagHandler.selectDevelopDrawingExist(dialogIdObjectList.get(which), id_app)) {
              KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_drawing_exist));

            } else {
              if (object_id == -1) {
                mainActivity.katbagHandler.insertDevelop(id_app, "drawing", dialogHumanStatement.get(which), dialogIdObjectList.get(which), "", "", "", "", "", "", "", 0, 0);
              }
            }

            break;

          case OBJECT_MOTION:
            setMotion(getString(R.string.dialog_title_select) + " " + resTitle, dialogHumanStatementRow.get(which), Integer.valueOf(dialogIdObjectList.get(which)), object_id);
            break;

          case OBJECT_LOOK:
            setLook(getString(R.string.dialog_title_select) + " " + resTitle, dialogHumanStatementRow.get(which), Integer.valueOf(dialogIdObjectList.get(which)), object_id);
            break;

          case OBJECT_SOUND:
            setSound(getString(R.string.dialog_title_select) + " " + resTitle, dialogHumanStatementRow.get(which), Integer.valueOf(dialogIdObjectList.get(which)), object_id);
            break;

          case OBJECT_CONTROL:
            setControl(getString(R.string.dialog_title_select) + " " + resTitle, dialogHumanStatementRow.get(which), Integer.valueOf(dialogIdObjectList.get(which)), object_id);
            break;

          case OBJECT_SENSING:
            setSensing(getString(R.string.dialog_title_select) + " " + resTitle, dialogHumanStatementRow.get(which), Integer.valueOf(dialogIdObjectList.get(which)), object_id);
            break;
          }

          loadListView();

        }
      });

      builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          // :)
        }
      });

      builder.show();
    }
  }

  public void setMotion(String title, final String humanTextRow, final int dialogIdObjectItemList, final long object_id) {
    int resource = getResources().getIdentifier("dialog_motion_" + dialogIdObjectItemList, "layout", mainActivity.getPackageName());
    LayoutInflater inflater = LayoutInflater.from(mainActivity.context);
    final View dialog_layout = inflater.inflate(resource, null);

    if (object_id != -1) {
      dev.clear();
      dev = mainActivity.katbagHandler.selectDevelopForId(object_id);
    }

    spinner_drawing_1 = (Spinner) dialog_layout.findViewById(R.id.dialog_drawing);

    spinnerList.clear();
    drawing1List.clear();
    spinnerList = mainActivity.katbagHandler.selectDevelopAllDrawing(id_app);
    if (spinnerList.size() == 0) {
      KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_not_drawing));
      return;
    }

    for (int i = 0; i < spinnerList.size(); i++) {
      drawing1List.add(getString(R.string.drawings_row_name) + " " + spinnerList.get(i));
    }

    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity.context, R.layout.simple_spinner_item_custom, drawing1List);
    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom);
    spinner_drawing_1.setAdapter(arrayAdapter);

    switch (dialogIdObjectItemList) {
    case 0:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_n);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_value));
        editN.setSelection(editN.getText().length());
      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
        editN.setText(dev.get(4));
        editN.setSelection(editN.getText().length());
      }
      break;

    case 1:
      editX = (EditText) dialog_layout.findViewById(R.id.dialog_x);
      editY = (EditText) dialog_layout.findViewById(R.id.dialog_y);

      if (object_id == -1) {
        editX.setText(getString(R.string.develop_edittext_number_default_value));
        editX.setSelection(editX.getText().length());

        editY.setText(getString(R.string.develop_edittext_number_default_value));
        editY.setSelection(editY.getText().length());
      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
        editX.setText(dev.get(4));
        editX.setSelection(editX.getText().length());

        editY.setText(dev.get(5));
        editY.setSelection(editY.getText().length());
      }
      break;

    case 2:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 3:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 4:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 5:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 6:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_n);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_value));
        editN.setSelection(editN.getText().length());
      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(4))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
        editN.setText(dev.get(3));
        editN.setSelection(editN.getText().length());
      }
      break;

    case 7:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_n);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_value));
        editN.setSelection(editN.getText().length());
      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(4))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
        editN.setText(dev.get(3));
        editN.setSelection(editN.getText().length());
      }
      break;

    case 8:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 9:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 10:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 11:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 12:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.context);
    builder.setView(dialog_layout);
    builder.setTitle(Html.fromHtml(title));
    builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        mainActivity.hideSoftKeyboard();
      }
    });

    builder.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        mainActivity.hideSoftKeyboard();

        drawingText = spinner_drawing_1.getSelectedItem().toString();
        drawingId = spinnerList.get(spinner_drawing_1.getSelectedItemPosition());

        switch (dialogIdObjectItemList) {
        case 0:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                editN.getText().toString(), // number of steps
                "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                editN.getText().toString(), // number of steps
                "", "", "", "", "");
          }
          break;

        case 1:
          // validate field
          if (editX.getText().toString().contentEquals("") || editY.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText, editX.getText().toString(), editY.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                editX.getText().toString(), // X coordinates
                editY.getText().toString(), // Y coordinates
                "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText, editX.getText().toString(), editY.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                editX.getText().toString(), // X coordinates
                editY.getText().toString(), // Y coordinates
                "", "", "", "");
          }
          break;

        case 2:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 3:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 4:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 5:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 6:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, editN.getText().toString(), drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                editN.getText().toString(), // X degrees
                drawingId, // id of drawing selected
                "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, editN.getText().toString(), drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                editN.getText().toString(), // X degrees
                drawingId, // id of drawing selected
                "", "", "", "", "");
          }
          break;

        case 7:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, editN.getText().toString(), drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                editN.getText().toString(), // Y degrees
                drawingId, // id of drawing selected
                "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, editN.getText().toString(), drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                editN.getText().toString(), // Y degrees
                drawingId, // id of drawing selected
                "", "", "", "", "");
          }
          break;

        case 8:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 9:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 10:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 11:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 12:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "motion", String.format(humanTextRow, drawingText), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type motion
                drawingId, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;
        }

        loadListView();
      }
    });

    builder.show();
  }

  public void setLook(String title, final String humanTextRow, final int dialogIdObjectItemList, final long object_id) {
    int resource = getResources().getIdentifier("dialog_look_" + dialogIdObjectItemList, "layout", mainActivity.getPackageName());
    LayoutInflater inflater = LayoutInflater.from(mainActivity.context);
    final View dialog_layout = inflater.inflate(resource, null);

    if (object_id != -1) {
      dev.clear();
      dev = mainActivity.katbagHandler.selectDevelopForId(object_id);
    }

    spinner_drawing_1 = (Spinner) dialog_layout.findViewById(R.id.dialog_drawing_1);
    spinner_drawing_2 = (Spinner) dialog_layout.findViewById(R.id.dialog_drawing_2);

    spinnerList.clear();
    drawing1List.clear();
    spinnerList = mainActivity.katbagHandler.selectDevelopAllDrawing(id_app);
    if (spinnerList.size() == 0) {
      KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_not_drawing));
      return;
    }

    for (int i = 0; i < spinnerList.size(); i++) {
      drawing1List.add(getString(R.string.drawings_row_name) + " " + spinnerList.get(i));
    }

    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity.context, R.layout.simple_spinner_item_custom, drawing1List);
    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom);
    spinner_drawing_1.setAdapter(arrayAdapter);

    switch (dialogIdObjectItemList) {
    case 0:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 1:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 2:
      spinner_drawing_2.setAdapter(arrayAdapter);

      if (object_id == -1) {
        if (spinnerList.size() > 1) {
          spinner_drawing_2.setSelection(1);
        }

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }

        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(4))) {
            spinner_drawing_2.setSelection(i);
            break;
          }
        }
      }
      break;
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.context);
    builder.setView(dialog_layout);
    builder.setTitle(Html.fromHtml(title));
    builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        // :)
      }
    });

    builder.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        drawingText_1 = spinner_drawing_1.getSelectedItem().toString();
        drawingId_1 = spinnerList.get(spinner_drawing_1.getSelectedItemPosition());

        switch (dialogIdObjectItemList) {
        case 0:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "look", String.format(humanTextRow, drawingText_1), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type look
                drawingId_1, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "look", String.format(humanTextRow, drawingText_1), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type look
                drawingId_1, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 1:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "look", String.format(humanTextRow, drawingText_1), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type look
                drawingId_1, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "look", String.format(humanTextRow, drawingText_1), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type look
                drawingId_1, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;

        case 2:
          // validate field
          if (spinner_drawing_1.getSelectedItemPosition() == spinner_drawing_2.getSelectedItemPosition()) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_not_different));
            return;
          }

          drawingText_2 = spinner_drawing_2.getSelectedItem().toString();
          drawingId_2 = spinnerList.get(spinner_drawing_2.getSelectedItemPosition());

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "look", String.format(humanTextRow, drawingText_1, drawingText_2), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type look
                drawingId_1, // id of first drawing selected
                drawingId_2, // id of second drawing selected
                "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "look", String.format(humanTextRow, drawingText_1, drawingText_2), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type look
                drawingId_1, // id of first drawing selected
                drawingId_2, // id of second drawing selected
                "", "", "", "", "");
          }
          break;
        }

        loadListView();
      }
    });

    builder.show();
  }

  public void setSound(String title, final String humanTextRow, final int dialogIdObjectItemList, final long object_id) {
    if (object_id != -1) {
      dev.clear();
      dev = mainActivity.katbagHandler.selectDevelopForId(object_id);
    }

    switch (dialogIdObjectItemList) {
    case 0:
      String nameSound = "";
      dialogList.clear();
      dialogHumanStatement.clear();
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

      adapterDialogSound = new DialogSoundRowAdapter(v.getContext(), R.layout.row_dialog_sound, dialogList, dialogHumanStatement);

      AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.context);
      builder.setTitle(title);
      builder.setAdapter(adapterDialogSound, new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "sound", String.format(humanTextRow, dialogHumanStatement.get(which)), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type sound
                dialogList.get(which), // id of sound selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "sound", String.format(humanTextRow, dialogHumanStatement.get(which)), // row
                                                                          // text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type sound
                dialogList.get(which), // id of sound selected
                "", "", "", "", "", "");
          }
          adapterDialogSound.stopPlayer();
          loadListView();
        }
      });

      builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          // :)
          adapterDialogSound.stopPlayer();
        }
      });

      builder.show();

      break;

    case 1:
      if (object_id == -1) {
        mainActivity.katbagHandler.insertDevelop(id_app, "sound", String.format(humanTextRow), // row text
            String.valueOf(dialogIdObjectItemList), // first parameter is the type control
            "", "", "", "", "", "", "", 0, 0);
      }

      loadListView();
      return;

    case 2:
      if (object_id == -1) {
        mainActivity.katbagHandler.insertDevelop(id_app, "sound", String.format(humanTextRow), // row text
            String.valueOf(dialogIdObjectItemList), // first parameter is the type control
            "", "", "", "", "", "", "", 0, 0);
      }

      loadListView();
      return;
    }
  }

  public void setControl(String title, final String humanTextRow, final int dialogIdObjectItemList, final long object_id) {
    int resource = 0;
    View dialog_layout = null;
    resource = getResources().getIdentifier("dialog_control_" + dialogIdObjectItemList, "layout", mainActivity.getPackageName());
    LayoutInflater inflater = LayoutInflater.from(mainActivity.context);
    dialog_layout = inflater.inflate(resource, null);

    if (object_id != -1) {
      dev.clear();
      dev = mainActivity.katbagHandler.selectDevelopForId(object_id);
    }

    switch (dialogIdObjectItemList) {
    case 0:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_n);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_value));
      } else {
        editN.setText(dev.get(3));
      }

      editN.setSelection(editN.getText().length());

      break;

    case 1:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_n);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_value));
      } else {
        editN.setText(dev.get(3));
      }

      editN.setSelection(editN.getText().length());

      break;

    case 2:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_n);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_value));
      } else {
        editN.setText(dev.get(3));
      }

      editN.setSelection(editN.getText().length());

      break;

    case 3:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_n);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_value));
      } else {
        editN.setText(dev.get(3));
      }

      editN.setSelection(editN.getText().length());

      break;

    case 4:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_n);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_value));
      } else {
        editN.setText(dev.get(3));
      }

      editN.setSelection(editN.getText().length());

      break;

    case 5:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_message);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_string));
      } else {
        editN.setText(dev.get(3));
      }

      editN.setSelection(editN.getText().length());

      break;

    case 6:
      editN = (EditText) dialog_layout.findViewById(R.id.dialog_n);

      if (object_id == -1) {
        editN.setText(getString(R.string.develop_edittext_number_default_value));
      } else {
        editN.setText(dev.get(3));
      }

      editN.setSelection(editN.getText().length());

      break;

    }

    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.context);
    builder.setView(dialog_layout);
    builder.setTitle(Html.fromHtml(title));
    builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        // :)
      }
    });

    builder.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        switch (dialogIdObjectItemList) {
        case 0:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "");
          }
          break;

        case 1:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "");
          }
          break;

        case 2:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "");
          }

          break;

        case 3:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "");
          }

          break;

        case 4:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "");
          }

          break;

        case 5:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "");
          }

          break;

        case 6:
          // validate field
          if (editN.getText().toString().contentEquals("")) {
            KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_empty_n));
            return;
          }

          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "control", String.format(humanTextRow, editN.getText().toString()), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type control
                editN.getText().toString(), // n
                "", "", "", "", "", "");
          }

          break;

        }

        loadListView();
      }
    });

    builder.show();
  }

  public void setSensing(String title, final String humanTextRow, final int dialogIdObjectItemList, final long object_id) {
    int resource = 0;
    View dialog_layout = null;
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity.context, 0);
    if (dialogIdObjectItemList != 1) {
      resource = getResources().getIdentifier("dialog_sensing_" + dialogIdObjectItemList, "layout", mainActivity.getPackageName());
      LayoutInflater inflater = LayoutInflater.from(mainActivity.context);
      dialog_layout = inflater.inflate(resource, null);
    }

    if (object_id != -1) {
      dev.clear();
      dev = mainActivity.katbagHandler.selectDevelopForId(object_id);
    }

    if (dialogIdObjectItemList != 1) {
      spinner_drawing_1 = (Spinner) dialog_layout.findViewById(R.id.dialog_drawing_1);
      spinner_drawing_2 = (Spinner) dialog_layout.findViewById(R.id.dialog_drawing_2);

      spinnerList.clear();
      drawing1List.clear();
      spinnerList = mainActivity.katbagHandler.selectDevelopAllDrawing(id_app);
      if (spinnerList.size() == 0) {
        KatbagUtilities.message(mainActivity.context, getString(R.string.develop_message_not_drawing));
        return;
      }

      for (int i = 0; i < spinnerList.size(); i++) {
        drawing1List.add(getString(R.string.drawings_row_name) + " " + spinnerList.get(i));
      }

      arrayAdapter = new ArrayAdapter<String>(mainActivity.context, R.layout.simple_spinner_item_custom, drawing1List);
      arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom);
      spinner_drawing_1.setAdapter(arrayAdapter);
    }

    switch (dialogIdObjectItemList) {
    case 0:
      if (object_id == -1) {

      } else {
        for (int i = 0; i < spinnerList.size(); i++) {
          if (spinnerList.get(i).contentEquals(dev.get(3))) {
            spinner_drawing_1.setSelection(i);
            break;
          }
        }
      }
      break;

    case 1:
      if (object_id == -1) {
        mainActivity.katbagHandler.insertDevelop(id_app, "sensing", String.format(humanTextRow), // row text
            String.valueOf(dialogIdObjectItemList), // first parameter is the type control
            "", "", "", "", "", "", "", 0, 0);
      }

      loadListView();
      return;
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.context);
    builder.setView(dialog_layout);
    builder.setTitle(Html.fromHtml(title));
    builder.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        // :)
      }
    });

    builder.setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        drawingText_1 = spinner_drawing_1.getSelectedItem().toString();
        drawingId_1 = spinnerList.get(spinner_drawing_1.getSelectedItemPosition());

        switch (dialogIdObjectItemList) {
        case 0:
          if (object_id == -1) {
            mainActivity.katbagHandler.insertDevelop(id_app, "sensing", String.format(humanTextRow, drawingText_1), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type sensing
                drawingId_1, // id of drawing selected
                "", "", "", "", "", "", 0, 0);
          } else {
            mainActivity.katbagHandler.updateDevelop(object_id, "sensing", String.format(humanTextRow, drawingText_1), // row text
                String.valueOf(dialogIdObjectItemList), // first parameter is the type sensing
                drawingId_1, // id of drawing selected
                "", "", "", "", "", "");
          }
          break;
        }

        loadListView();
      }
    });

    builder.show();
  }

  @Override
  public void onResume() {
    mainActivity.getSupportActionBar().setTitle(Add.name_app_text + " - " + getString(R.string.title_activity_developments));

    if (!MainActivity.TABLET)
      mainActivity.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

    super.onResume();
    
    this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
    this.tracker.send( MapBuilder.createAppView().build() );
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
