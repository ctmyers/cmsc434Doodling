package com.carsonmyers.doodling;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button mClearButton;
    Button mSaveButton;
    Button mToolsButton;
    DoodleView mDoodleView;
    PopupWindow mToolMenu;

    // popup stuff
    TextView mSelectedColorTextView;

    Button mRedButton;
    Button mOrangeButton;
    Button mYellowButton;
    Button mGreenButton;
    Button mBlueButton;
    Button mPurpleButton;
    Button mBlackButton;
    Button mWhiteButton;
    Button mGrayButton;

    SeekBar mOpacitySeekbar;
    SeekBar mStrokeSizeSeekbar;

    private boolean popupVisable = false;

    private static final String TAG = "MainActivity";



    private boolean permissionToSave = false;
    final public int MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE = 486;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sets the action bar to be pretty
        getSupportActionBar().setDisplayShowHomeEnabled(true); //shows the logo
        getSupportActionBar().setIcon(R.drawable.doodling);   // sets the icon to be the logo
        getSupportActionBar().setDisplayShowTitleEnabled(false); // remove the default text "Doodling"

        //Gets the buttons
        mClearButton = (Button) findViewById(R.id.clearButton);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mToolsButton = (Button) findViewById(R.id.toolsButton);

        //Gets the doodleView
        mDoodleView = (DoodleView) findViewById(R.id.doodleView);


        //Sets up the popup menu
        toolBarInit(); // needs to be called after its created

        //Sets the listeners on the buttons
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoodleView.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasStoragePermissions()) //make sure we can write to external storage before trying to save
                    mDoodleView.save();
            }
        });

        mToolsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!popupVisable) {

                    mToolMenu.showAtLocation((RelativeLayout) findViewById(R.id.activity_main), Gravity.CENTER, 0, 0); // displays the tool menu in the center of the screen

                    mToolsButton.setText("Dismiss");
                    popupVisable = true;


                } else{
                    mToolMenu.dismiss();
                    popupVisable = false;
                    mToolsButton.setText(" Tools");
                }
            }
        });

    }

    // checks to make sure we have permission to save
    private boolean hasStoragePermissions(){
        if (Build.VERSION.SDK_INT >= 23) { // if need to ask for permission
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { // if we already have permission
                permissionToSave = true;
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE); //asks for permission
            }
        } else{
            permissionToSave = true; // if the android sdk is < 23 then the permission is handled in the manafest and we don't need to ask
        }
        return permissionToSave;
    }

    // if the prompt asks for permissions then this is called by android
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE: { // makes sure the request code is the one we requested
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //makes sure the permission is granted
                    permissionToSave = true;
                    mDoodleView.save(); // the ActivityCompat.requestPermissions is asyncronys so save() wouldn't have been called, call it now
                } else { // we don't have permission
                    permissionToSave = false;
                }
                return;
            }
        }
    }

    private void toolBarInit(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE); //gets the layout inflator
        View toolMenuView = inflater.inflate(R.layout.tool_menu, null); // inflates the tool menu layout

        mToolMenu = new PopupWindow(toolMenuView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //attaches the tool menu layout to a popup window

        if (Build.VERSION.SDK_INT >= 21) { //prevents the app from crashing due to the window leaking
            mToolMenu.setElevation(2 * getApplicationContext().getResources().getDisplayMetrics().density); // sets the elevation correctly for the right amount of shadow
        }

        //Button and seekbar stuff
        mSelectedColorTextView = (TextView) toolMenuView.findViewById(R.id.selectedColorTextView);

        mRedButton = (Button) toolMenuView.findViewById(R.id.redButton);
        mOrangeButton = (Button) toolMenuView.findViewById(R.id.orangeButton);
        mYellowButton = (Button) toolMenuView.findViewById(R.id.yellowButton);
        mGreenButton = (Button) toolMenuView.findViewById(R.id.greenButton);
        mBlueButton = (Button) toolMenuView.findViewById(R.id.blueButton);
        mPurpleButton = (Button) toolMenuView.findViewById(R.id.purpleButton);
        mWhiteButton = (Button) toolMenuView.findViewById(R.id.whiteButton);
        mGrayButton = (Button) toolMenuView.findViewById(R.id.grayButton);
        mBlackButton = (Button) toolMenuView.findViewById(R.id.blackButton);

        mOpacitySeekbar = (SeekBar) toolMenuView.findViewById(R.id.opacitySeekBar);
        mStrokeSizeSeekbar = (SeekBar) toolMenuView.findViewById(R.id.strokeSizeSeekBar);

        mStrokeSizeSeekbar.setMax(0); //work around a bug where progress doesn't update in old android phones
        mStrokeSizeSeekbar.setMax(255);
        mStrokeSizeSeekbar.setProgress(10);
        mDoodleView.setStrokeSize(10);

        mOpacitySeekbar.setMax(0); //work around a bug where progress doesn't update in old android phones
        mOpacitySeekbar.setMax(255);
        mOpacitySeekbar.setProgress(255);
        mDoodleView.setOpacity(255);

        //sets the listeners
        mRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ((ColorDrawable) mRedButton.getBackground()).getColor();
                mDoodleView.setColor(color);
                mSelectedColorTextView.setBackgroundColor(color);
            }
        });


        mOrangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ((ColorDrawable) mOrangeButton.getBackground()).getColor();
                mDoodleView.setColor(color);
                mSelectedColorTextView.setBackgroundColor(color);
            }
        });

        mYellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ((ColorDrawable) mYellowButton.getBackground()).getColor();
                mDoodleView.setColor(color);
                mSelectedColorTextView.setBackgroundColor(color);
            }
        });

        mGreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ((ColorDrawable) mGreenButton.getBackground()).getColor();
                mDoodleView.setColor(color);
                mSelectedColorTextView.setBackgroundColor(color);
            }
        });

        mBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ((ColorDrawable) mBlueButton.getBackground()).getColor();
                mDoodleView.setColor(color);
                mSelectedColorTextView.setBackgroundColor(color);
            }
        });

        mPurpleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ((ColorDrawable) mPurpleButton.getBackground()).getColor();
                mDoodleView.setColor(color);
                mSelectedColorTextView.setBackgroundColor(color);
            }
        });

        mBlackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ((ColorDrawable) mBlackButton.getBackground()).getColor();
                mDoodleView.setColor(color);
                mSelectedColorTextView.setBackgroundColor(color);
            }
        });

        mGrayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ((ColorDrawable) mGrayButton.getBackground()).getColor();
                mDoodleView.setColor(color);
                mSelectedColorTextView.setBackgroundColor(color);
            }
        });

        mWhiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = ((ColorDrawable) mWhiteButton.getBackground()).getColor();
                mDoodleView.setColor(color);
                mSelectedColorTextView.setBackgroundColor(color);
            }
        });

        mOpacitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDoodleView.setOpacity(mOpacitySeekbar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mStrokeSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDoodleView.setStrokeSize(mStrokeSizeSeekbar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }
}
