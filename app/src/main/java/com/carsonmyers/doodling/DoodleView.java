package com.carsonmyers.doodling;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by carson on 11/4/2016.
 */

public class DoodleView extends View {

    private Paint mPaintDoole;
    private Path mPath;

    private boolean clear = false;

    public DoodleView(Context context) {
        super(context);
        init(null, 0);
    }

    public DoodleView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs, 0);
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle){

        // sets up the brush
        mPaintDoole = new Paint();
        mPaintDoole.setColor(Color.rgb(76,175,80));
        mPaintDoole.setAntiAlias(true);
        mPaintDoole.setStrokeWidth(10);
        mPaintDoole.setStyle(Paint.Style.STROKE);

        // sets up the path
       mPath = new Path();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(clear){ //clears the canvas
            mPath = new Path();
            canvas.drawPath(mPath, mPaintDoole);
            clear = false;
        } else{
            canvas.drawPath(mPath, mPaintDoole);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();
        return true;
    }

    // Doodling methods
    public void clear(){
        clear = true;
        invalidate();
    }

    public void save(){

        Bitmap bitMapToSave = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888); //creates a bitmap of size of this object and configures it to the ARGB standard
        Canvas canvasToSave = new Canvas(bitMapToSave);
        canvasToSave.drawPath(mPath, mPaintDoole); //draws the current doodle on the canvas

        //Sets up the folder to store photos
        makeDooldingDir();

        //Gets the timestring to save the file so it doesn't overwrite anything
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MdHms");

        String name = "/Doodling/doodling" + format.format(calendar.getTime()) + ".png";
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path + name); //creates a new file for the bitmap

        try {
            file.createNewFile(); //makes the file
            bitMapToSave.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file)); // compresses and saves the bitmap
            Toast.makeText(getContext(), "Saved Successfully to " + name +"!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Saved Failed! Please Check Permissions.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    // makes the directory "Doodling" in the external storage if it doesn't already exist
    private void makeDooldingDir(){
        File folder = new File(Environment.getExternalStorageDirectory() + "/Doodling"); //
        if (!folder.exists()) {
           folder.mkdir();
        }
    }

}
