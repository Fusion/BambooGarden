package com.voilaweb.mobile.bamboogarden;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.JavaCommandCapture;
import com.stericson.RootTools.execution.Shell;


public class Garden extends Activity implements BambooMessages {


    private GardenAdapter mGardenAdapter;
    private BambooListView mBambooListView;


    protected interface GardenListCallable { public void call(final GardenAdapterList list); };


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BROADCAST_BUTTONCLICK)) {
                if(intent.getIntExtra("id", 0) == R.id.delete_book_button) {
                    int position = intent.getIntExtra("position", -1);
                    if(position != -1) {
                        confirmDeleteBook(position);
                    }
                }
            }
            else if(action.equals(BROADCAST_ITEMCLICK)) {
                int position = intent.getIntExtra("position", -1);
                if(position != -1) {
                    if(mGardenAdapter.getItem(position).isActive()) {
                        maybeOpenBamboo();
                    }
                    else {
                        confirmSwitchToBook(position);
                    }
                }
            }
        }
    };


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkWeHaveRoot();
        initBamboo();
        initReceiver();

        setContentView(R.layout.main);

        QuickHandle qh    = (QuickHandle)findViewById(R.id.panehandle);
        View keyboardPane = findViewById(R.id.inputpane);
        View mainPane     = findViewById(R.id.mainpane);
        if(qh != null && keyboardPane != null && mainPane != null )
            qh.setPanes(keyboardPane, mainPane);

        mBambooListView = (BambooListView) findViewById(R.id.bamboolistview);

        mGardenAdapter = new GardenAdapter(this);

        getAllBamboos(new GardenListCallable() {
            @Override
            public void call(final GardenAdapterList list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBambooListView.setAdapter(mGardenAdapter);
                        mGardenAdapter.setAllValues(list);
                    }
                });
            }
        });

        final BambooEdit editField = (BambooEdit)findViewById(R.id.bamboonameedit);
        Button addButton = (Button)findViewById(R.id.bamboonameadd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToGarden(
                        new BambooInfo(
                                editField.getText()
                                        .toString()
                                        .replaceAll("[^A-Za-z0-9_\\s]+", "")
                                        .replaceAll("[\\s]+", "_"),
                                false));
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        forgetReceiver();
    }


    private void resetEditField() {
        ((BambooEdit)findViewById(R.id.bamboonameedit)).setText("");
    }


    private GardenAdapterList getAllBamboos(final GardenListCallable callback) {
        final GardenAdapterList list = new GardenAdapterList();
        try {
            Shell shell = RootTools.getShell(true);
            JavaCommandCapture cmd = new JavaCommandCapture(
                    1,
                    false,
                    Garden.this,
                    "com.voilaweb.mobile.bamboogarden.RootHelper"
                            + " list") {
                boolean valid = false;
                @Override
                public void commandOutput(int id, String line) {
                    super.commandOutput(id, line);
                    CLog.log("INNER RootHelper says: " + line);
                    if(valid) {
                        String[] info = line.trim().split(",");
                        list.add(new BambooInfo(info[0], info[1].equals("true")));
                    }
                    else if(line.equals("OK")) {
                        valid = true;
                    }
                }

                @Override
                public void commandCompleted(int id, int exitCode) {
                    callback.call(list);
                }
            };
            shell.add(cmd);

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    private void addToGarden(final BambooInfo littleBamboo) {
        getAllBamboos(new GardenListCallable() {
            @Override
            public void call(final GardenAdapterList list) {
                boolean dup = false;
                for(BambooInfo item:list) {
                    if(item.getName().equalsIgnoreCase(littleBamboo.getName())) {
                        dup = true;
                        break;
                    }
                }
                if(dup) { // So you were trying to open it maybe?

                }
                else {

                    createNewBamboo(littleBamboo);
                    list.add(littleBamboo);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resetEditField();
                            mGardenAdapter.setAllValues(list); // Brutal, I know
                        }
                    });
                }
            }
        });
    }


    private void initBamboo() {
        try {
            Shell shell = RootTools.getShell(true);
            shell.useCWD(this);
            JavaCommandCapture cmd = new JavaCommandCapture(
                    43,
                    false,
                    Garden.this,
                    "com.voilaweb.mobile.bamboogarden.RootHelper"
                    + " init") {

                @Override
                public void commandOutput(int id, String line) {
                    super.commandOutput(id, line);
                    CLog.log("RootHelper says: " + line);
                }
            };
            shell.add(cmd);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_BUTTONCLICK);
        filter.addAction(BROADCAST_ITEMCLICK);
        registerReceiver(mReceiver, filter);
    }


    private void forgetReceiver() {
        unregisterReceiver(mReceiver);
    }


    private void createNewBamboo(final BambooInfo bamboo) {
        try {
            Shell shell = RootTools.getShell(true);
            JavaCommandCapture cmd = new JavaCommandCapture(
                    43,
                    false,
                    Garden.this,
                    "com.voilaweb.mobile.bamboogarden.RootHelper"
                    + " create "
                    + bamboo.getName()) {

                @Override
                public void commandOutput(int id, String line) {
                    super.commandOutput(id, line);
                    CLog.log("RootHelper says: " + line);
                }
            };
            shell.add(cmd);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void confirmDeleteBook(final int position) {
        final BambooInfo bamboo = mGardenAdapter.getItem(position);
        if(bamboo.isActive()) {
            AlertDialog.Builder ab = new AlertDialog.Builder(Garden.this);
            ab.setTitle("Oh no you didn't.")
                    .setMessage("You are trying to delete the active notebook. I cannot let you do that. It's for your own good. Trust me.")
                    .setNeutralButton("Oh all right then.", null);
            ab.create().show();
        }
        else {
            AlertDialog.Builder ab = new AlertDialog.Builder(Garden.this);
            ab.setTitle("Warning!")
                    .setMessage("You are about to delete a notebook. Its content will be gone forever!\n"
                    + "DANGER! Do not delete the currently active notebook or you will lose access to Bamboo.")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteBamboo(bamboo);
                        }
                    });
            ab.create().show();
        }
    }


    private void deleteBamboo(final BambooInfo bamboo) {
        if(bamboo != null) {
            try {
                Shell shell = RootTools.getShell(true);
                JavaCommandCapture cmd = new JavaCommandCapture(
                        43,
                        false,
                        Garden.this,
                        "com.voilaweb.mobile.bamboogarden.RootHelper"
                                + " delete "
                                + bamboo.getName()) {

                    @Override
                    public void commandOutput(int id, String line) {
                        super.commandOutput(id, line);
                        CLog.log("RootHelper says: " + line);
                    }

                    @Override
                    public void commandCompleted(int id, int exitCode) {
                        refreshBambooGarden();
                    }

                };
                shell.add(cmd);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void confirmSwitchToBook(final int position) {
        final String PREFERENCES = getApplicationContext().getPackageName();
        final String DONOTSHOWSWITCHDIALOGAGAIN =
                "com.voilaweb.mobile.bamboogarden.DO_NOT_SHOW_SWITCH_DIALOG_AGAIN";
        final SharedPreferences prefs = getSharedPreferences(PREFERENCES, 0);
        if(prefs.getBoolean(DONOTSHOWSWITCHDIALOGAGAIN, false) == true) {
            switchToBook(mGardenAdapter.getItem(position));
        }
        else {
            AlertDialog.Builder ab = new AlertDialog.Builder(Garden.this);
            View dialogView = getLayoutInflater().inflate(R.layout.additional_action_checkbox, null);
            ab.setView(dialogView);
            final CheckBox doNotShowAgain = (CheckBox)dialogView.findViewById(R.id.a_a_checkbox);
            doNotShowAgain.setText("Do not display again");
            doNotShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(DONOTSHOWSWITCHDIALOGAGAIN, isChecked);
                    editor.commit();
                }
            });
            ab.setTitle("Switch to book")
                    .setMessage("Do you wish to switch to this notebook?\n"
                    + "Note: I will stop Bamboo first to avoid confusing it.")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switchToBook(mGardenAdapter.getItem(position));
                        }
                    });

            ab.create().show();
        }
    }


    private void switchToBook(final BambooInfo bamboo) {
        if(bamboo != null) {
            // Kill Bamboo so that it doesn't get
            // all mixed up.
            RootTools.killProcess("com.wacom.bamboopaper");

            try {
                Shell shell = RootTools.getShell(true);
                JavaCommandCapture cmd = new JavaCommandCapture(
                        43,
                        false,
                        Garden.this,
                        "com.voilaweb.mobile.bamboogarden.RootHelper"
                                + " switch "
                                + bamboo.getName()) {

                    @Override
                    public void commandOutput(int id, String line) {
                        super.commandOutput(id, line);
                        CLog.log("RootHelper says: " + line);
                    }

                    @Override
                    public void commandCompleted(int id, int exitCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                maybeOpenBamboo();
                            }
                        });
                        refreshBambooGarden();
                    }

                };
                shell.add(cmd);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void maybeOpenBamboo() {
        AlertDialog.Builder ab = new AlertDialog.Builder(Garden.this);
        ab.setTitle("Open")
                .setMessage("Do you wish to open Bamboo now?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent launchIntent = new Intent();
                        launchIntent.setComponent(
                                new ComponentName(
                                        "com.wacom.bamboopaper",
                                        "com.wacom.bamboopaperforsamsung.BambooPaperForSamsungActivity"));
                        startActivity(launchIntent);
                    }
                });

        ab.create().show();
    }


    private void refreshBambooGarden() {
        getAllBamboos(new GardenListCallable() {
            @Override
            public void call(final GardenAdapterList list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Yes it's a bit brutal but I have been observing
                        // strange behaviors when deleting items from SwipeListView.
                        mBambooListView.setAdapter(mGardenAdapter);
                        mGardenAdapter.setAllValues(list);
                    }
                });
            }
        });
    }


    private boolean checkWeHaveRoot() {
        boolean weHaveRoot = false;
        if (RootTools.isRootAvailable()) {
            if (RootTools.isAccessGiven()) {
                RootTools.installBinary(this, R.raw.anbuild, "anbuild.dex");
                RootTools.installBinary(this, R.raw.book, "book");
                RootTools.installBinary(this, R.raw.book_journal, "book-journal");
                weHaveRoot = true;
            }
        }
        return weHaveRoot;
    }
}
