package com.nocompany.bober.myfirstapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import java.lang.Math;

/**
 * Created by bober on 8/31/2016.
 */
public class Dot {

    private int x;   //X position (pixels)
    private int y;    //Y position (pixels)
    private float radius;
    private int color;
    private boolean special;
    private int x_vel;   //X velocity (pixels/second)
    private int y_vel;   //Y velocity (pixels/second)

    public Dot(){
        this.x = 0;
        this.y = 0;
        this.radius = Constants.DOT_RADIUS;
        this.color = Color.WHITE;
        this.x_vel = 0;
        this.y_vel = 0;
        this.special = false;
    }


    public void draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(color);
        if(!special) {
            canvas.drawCircle(x, y, radius, paint);
        }
        else {
            paint.setTextSize(Constants.SCREEN_HEIGHT/23);
            paint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
            canvas.drawText("+",x,y,paint);
        }
    }


    public void update(){
        setX(getX()+getX_vel());
        setY(getY()+getY_vel());
    }


    public boolean collide(Player player){
        //If distance between player center & dot center is less than the player radius, there is a collision
        if(Math.sqrt(Math.pow((player.getX()-x),2) + Math.pow((player.getY()-y),2)) < player.getRadius())
            return true;
        return false;
    }

    public boolean offScreen(){
        //If center of a dot is >10 pixels away from an edge, it has fully traveled off screen
        if(getX()<-20 || getY()<-20 || getX()>Constants.SCREEN_WIDTH+20 || getY()>Constants.SCREEN_HEIGHT+20)
            return true;
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public int getX_vel(){
        return x_vel;
    }

    public int getY_vel() {
        return y_vel;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setX_vel(int x_vel) {
        this.x_vel = x_vel;
    }

    public void setY_vel(int y_vel) {
        this.y_vel = y_vel;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    public boolean isSpecial() {
        return special;
    }
}
