package com.carsonmyers.doodling;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button mClearButton;
    Button mSaveButton;
    Button mToolsButton;
    DoodleView mDoodleView;

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
}
