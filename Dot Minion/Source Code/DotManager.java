package com.nocompany.bober.myfirstapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import java.util.ArrayList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.text.TextPaint;

/**
 * Created by bober on 8/31/2016.
 */
public class DotManager {

    private ArrayList<Dot> dots;
    public static double speedIncrease;
    private double tempSpeedIncrease;   //Stores increase value during slo-mo bonus
    private int slowFrames;

    private SoundPool sounds;
    private SoundPool.Builder soundBuilder;
    private AudioAttributes attributes;
    private AudioAttributes.Builder attributesBuilder;
    private int shrinkSound;
    private int growSound;
    private int gameOverSound;
    private int specialSound;
    private int specialFrames;
    private String specialType;
    private TextPaint textPaint;

    
    public DotManager(Context context){
        dots = new ArrayList<>();
        speedIncrease = 0;
        specialFrames = 121;    //0-30 shows specialType on canvas, 0-120 remains slow during slow-mo bonus
        specialType = "";
        createSoundPool(context);
        createTextFont(context);

    }


    public void createTextFont(Context context){
        textPaint = new TextPaint();
        textPaint.setColor(Color.GREEN);
        textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/showcard_gothic.ttf"));
        textPaint.setTextSize(Constants.SCREEN_WIDTH/10);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void createSoundPool(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            attributesBuilder = new AudioAttributes.Builder();
            attributesBuilder.setUsage(AudioAttributes.USAGE_GAME);
            attributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            attributes = attributesBuilder.build();
            soundBuilder = new SoundPool.Builder();
            soundBuilder.setAudioAttributes(attributes);
            sounds = soundBuilder.build();
        }
        else{
            sounds = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        shrinkSound = sounds.load(context, R.raw.shrink12, 1);
        growSound = sounds.load(context, R.raw.grow2, 1);
        gameOverSound = sounds.load(context, R.raw.lose5, 1);
        specialSound = sounds.load(context, R.raw.bonus, 1);
    }

    public void generateDot(){
        double rand1 = Math.random();   //Random for red/green
        double rand2 = Math.random();   //Random for edge to start on
        double rand3 = Math.random();   //Random for starting position on that edge
        double rand4 = Math.random();   //Random for initial velocity
        double rand5 = Math.random()/1.5;   //Random for initial velocity
        double rand6 = Math.random();   //Random for sign of velocity

        //Make a dot with only radius set
        Dot d = new Dot();

        //Set color
        if (rand1 < 0.15){  // 15% chance of green dot
            d.setColor(Color.GREEN);
        }
        else if (rand1 < 0.175) {   // 2.5% chance of special +
            d.setColor(Color.GREEN);
            d.setColor(Color.GREEN);
            d.setSpecial(true);
        }
        else {  // 82.5% chance of red dot
            d.setColor(Color.RED);
        }

        //Set starting edge
        //Set starting position on that edge
        //Set initial x and y velocities
        if (rand2 < 0.25){  //Top edge
            d.setY(-20);
            d.setX((int)(Constants.SCREEN_WIDTH*rand3));
            if(rand6 < 0.5)
                d.setX_vel((int)((-rand4-0.2-speedIncrease)*(Constants.SCREEN_HEIGHT/200)));
            else
                d.setX_vel((int)((rand4+0.2+speedIncrease)*(Constants.SCREEN_HEIGHT/200)));
            d.setY_vel((int)((rand5+0.6+speedIncrease)*(Constants.SCREEN_HEIGHT/150)));   //Start around 6-10, and increase
    }
        else if (rand2 < 0.5){  //Bottom edge
            d.setY(Constants.SCREEN_HEIGHT+20);
            d.setX((int)(Constants.SCREEN_WIDTH*rand3));
            if(rand6 < 0.5)
                d.setX_vel((int)((-rand4-0.2-speedIncrease)*(Constants.SCREEN_HEIGHT/200)));
            else
                d.setX_vel((int)((rand4+0.2+speedIncrease)*(Constants.SCREEN_HEIGHT/200)));
            d.setY_vel((int)((rand5+0.6+speedIncrease)*-(Constants.SCREEN_HEIGHT/150)));  //Start around -(6-10), and increase
        }
        else if (rand2 < 0.75){  //Left edge
            d.setX(-20);
            d.setY((int)(Constants.SCREEN_HEIGHT*rand3));
            d.setX_vel((int)((rand5+0.6+speedIncrease)*(Constants.SCREEN_HEIGHT/150)));   //Start around 6-10, and increase
            if(rand6 < 0.8)
                d.setY_vel((int)((-rand4-0.2-speedIncrease)*(Constants.SCREEN_HEIGHT/200)));
            else
                d.setY_vel((int)((rand4+0.2+speedIncrease)*(Constants.SCREEN_HEIGHT/200)));
        }
        else{   //Right edge
            d.setX(Constants.SCREEN_WIDTH+20);
            d.setY((int)(Constants.SCREEN_HEIGHT*rand3));
                d.setX_vel((int)((rand5+0.6+speedIncrease)*-(Constants.SCREEN_HEIGHT/150)));  //Start around -(6-10), and increase
            if(rand6 < 0.5)
                d.setY_vel((int)((-rand4-0.2-speedIncrease)*(Constants.SCREEN_HEIGHT/200)));
            else
                d.setY_vel((int)((rand4+0.2+speedIncrease)*(Constants.SCREEN_HEIGHT/200)));
        }

        dots.add(d);
    }


    public void update(){
        slowFrames++;
        specialFrames++;
        if(slowFrames < 120)
            speedIncrease = 0;
        else if(slowFrames == 120)
            speedIncrease = tempSpeedIncrease;

        moveDots();
        checkForCollisions();
        checkForOffScreen();
    }

    public void draw(Canvas canvas){
        for(int i = 0; i < dots.size(); i++)    //For all active dots
            dots.get(i).draw(canvas);           //Draw dot

        //If collision occured with special + item, write the bonus on the canvas
        if(specialFrames <= 30){
            textPaint.setColor(Color.GREEN);
            textPaint.setTextSize(Constants.SCREEN_WIDTH/10);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(specialType, Constants.SCREEN_WIDTH/2, Constants.SCREEN_WIDTH/3, textPaint);
        }

    }

    public void moveDots(){
        for(int i = 0; i < dots.size(); i++)   //For all active dots
            dots.get(i).update();
    }

    public void checkForCollisions() {
        for(int i = 0; i < dots.size(); i++) {  //For all active dots
            if(dots.get(i).collide(GamePanel.getPlayer())){ //If a collision is detected
                if(dots.get(i).getColor() == Color.GREEN){  //If the dot is green
                    greenCollision(i);
                    i--;    //Dot was removed, so now we have to decrement i
                }
                else{   //Else if the dot is red
                    redCollision(i);
                    i--;    //Dot was removed, so now we have to decrement i
                }
            }
        }
    }

    public void checkForOffScreen() {
        for(int i = 0; i < dots.size(); i++) {
            if(dots.get(i).offScreen()){
                dots.remove(i);
                i--;    //Dot was removed, so now we have to decrement i
            }
        }
    }

    public void greenCollision(int position){
        sounds.play(growSound,1,1,0,0,1);
        GamePanel.getPlayer().setCurrentFace(PlayerFaces.HAPPY);
        GamePanel.getPlayer().setBlinkFrames(0);
        int color = GamePanel.getPlayer().getColor();
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        if(red == 5) {    //If green is maxed and red is minned, don't change size or color
        }
        else if(green == 255) {    //If green is maxed, subtract from red and grow
            GamePanel.getPlayer().setRadius(GamePanel.getPlayer().getRadius() + Constants.SCREEN_HEIGHT / 350); //Player grows
            GamePanel.getPlayer().setColor(Color.rgb(red - 50, green, blue));
        }
        else{   //If green is not maxed, add to green and grow
            GamePanel.getPlayer().setRadius(GamePanel.getPlayer().getRadius() + Constants.SCREEN_HEIGHT / 350); //Player grows
            GamePanel.getPlayer().setColor(Color.rgb(red, green + 50, blue));
        }

        GamePanel.score += 10;
        if(dots.get(position).isSpecial())
            specialCollision();
        dots.remove(position);  //Remove dot after collision
    }

    public void redCollision(int position){
        sounds.play(shrinkSound,1,1,0,0,1);
        GamePanel.getPlayer().setCurrentFace(PlayerFaces.SAD);
        GamePanel.getPlayer().setBlinkFrames(0);
        int color = GamePanel.getPlayer().getColor();
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        if(green == 5) {    //If red is maxed and green is minned, player loses
            if(GamePanel.score > GamePanel.highScore)   //Set High score if applicable
                GamePanel.highScore = GamePanel.score;
            sounds.play(gameOverSound,1,1,0,0,1);   //Play game over sound
            GamePanel.setGameOver(true);
            GamePanel.adCount++;
            GamePanel.setGameOverTime(System.currentTimeMillis());
        }
        else if(red == 255) {    //If red is maxed, subtract from green and shrink
            GamePanel.getPlayer().setRadius(GamePanel.getPlayer().getRadius() - Constants.SCREEN_HEIGHT / 350); //Player shrinks
            GamePanel.getPlayer().setColor(Color.rgb(red, green - 50, blue));
        }
        else{   //If red is not maxed, add to red and shrink
            GamePanel.getPlayer().setRadius(GamePanel.getPlayer().getRadius() - Constants.SCREEN_HEIGHT / 350); //Player shrinks
            GamePanel.getPlayer().setColor(Color.rgb(red + 50, green, blue));
        }

        dots.remove(position);  //Remove dot after collision
    }

    public void specialCollision(){
        sounds.play(specialSound,1,1,0,0,1);
        double rand = Math.random();   //Random for special bonus
        specialFrames = 0;

        for(int i = 0; i < dots.size(); i++) {  //For all active dots, remove specials
            if(dots.get(i).isSpecial())
                dots.get(i).setSpecial(false);
        }

        //Turn reds on screen to greens
        if (rand < 0.33){
            for(int i = 0; i < dots.size(); i++) {  //For all active dots, turn green
                dots.get(i).setColor(Color.GREEN);
            }
            //Set special type to GREENZ
            specialType = "MORE GREENZ!";
        }

        //Slow everything down
        else if(rand < 0.67){
            for(int i = 0; i < dots.size(); i++) {  //For all active dots, cut speed in half (if they aren't already)
                if(Math.abs(dots.get(i).getY_vel()) > 0.6*(Constants.SCREEN_HEIGHT/200) || Math.abs(dots.get(i).getX_vel()) > 0.6*(Constants.SCREEN_HEIGHT/200))
                {
                    dots.get(i).setX_vel(dots.get(i).getX_vel() / 2);
                    dots.get(i).setY_vel(dots.get(i).getY_vel() / 2);
                }
            }

            specialType = "SLOW IT DOWN!";
            slowFrames = 0;
            tempSpeedIncrease = speedIncrease;
            speedIncrease = 0;
        }

        //Grow a lot
        else{
            for(int i=0; i<2; i++) {

                int color = GamePanel.getPlayer().getColor();
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                if (red == 5) {    //If green is maxed and red is minned, don't change size or color
                }
                else if (green == 255) {    //If green is maxed, subtract from red and grow
                    GamePanel.getPlayer().setRadius(GamePanel.getPlayer().getRadius() + Constants.SCREEN_HEIGHT / 350); //Player grows
                    GamePanel.getPlayer().setColor(Color.rgb(red - 50, green, blue));
                }
                else {   //If green is not maxed, add to green and grow
                    GamePanel.getPlayer().setRadius(GamePanel.getPlayer().getRadius() + Constants.SCREEN_HEIGHT / 350); //Player grows
                    GamePanel.getPlayer().setColor(Color.rgb(red, green + 50, blue));
                }

            }

            specialType = "GROWTH SPURT!";
        }

    }

    public TextPaint getTextPaint() {
        return textPaint;
    }
}
