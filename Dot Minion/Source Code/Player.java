package com.nocompany.bober.myfirstapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;

/**
 * Created by bober on 8/31/2016.
 */
public class Player {

    private int x;
    private int y;
    private double x_vel;
    private double y_vel;
    private float radius;
    private int color;
    private Paint paint;

    private Bitmap normal;          //Original Face bitmaps
    private Bitmap happy;
    private Bitmap sad;
    private Bitmap displayedFace;   //Scaled Face image to be displayed

    private PlayerFaces currentFace;    //Tracks current face
    private int blinkFrames;            //Tracks how long that face has been active


    public Player(int x, int y, int radius, int color){
        this.x = x;
        this.y = y;
        x_vel = 0;
        y_vel = 0;
        this.radius = radius;
        this.color = color;

        normal = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.normal_player);
        sad = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.sad_player);
        happy = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.happy_player);

        paint = new Paint();
        paint.setAlpha(150);
        currentFace = PlayerFaces.NORMAL;
        blinkFrames = 0;
    }


    public void draw(Canvas canvas){
        paint.setColor(color);
        paint.setMaskFilter(new BlurMaskFilter(radius/5, BlurMaskFilter.Blur.NORMAL));
        canvas.drawCircle(x,y,radius,paint);
        //Resize image of player's face based on player size
        if(currentFace == PlayerFaces.NORMAL || blinkFrames >= 18){
            currentFace = PlayerFaces.NORMAL;
            displayedFace = Bitmap.createScaledBitmap(normal, (int)(1.86*radius), (int)(1.86*radius), true);
        }
        else if(currentFace == PlayerFaces.HAPPY){
            displayedFace = Bitmap.createScaledBitmap(happy, (int)(1.86*radius), (int)(1.86*radius), true);
        }
        else{
            displayedFace = Bitmap.createScaledBitmap(sad, (int)(1.86*radius), (int)(1.86*radius), true);
        }

        canvas.drawBitmap(displayedFace,(int)(x-0.93*radius),(int)(y-0.93*radius),paint);
        blinkFrames++;
    }


    public void update(int x, int y, float radius, int color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    public float getRadius() {
        return radius;
    }

    public int getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getX_vel() {
        return x_vel;
    }

    public void setX_vel(double x_vel) {
        this.x_vel = x_vel;
    }

    public double getY_vel() {
        return y_vel;
    }

    public void setY_vel(double y_vel) {
        this.y_vel = y_vel;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setCurrentFace(PlayerFaces currentFace) {
        this.currentFace = currentFace;
    }

    public void setBlinkFrames(int blinkFrames) {
        this.blinkFrames = blinkFrames;
    }
}
