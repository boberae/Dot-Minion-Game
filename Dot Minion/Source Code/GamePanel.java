package com.nocompany.bober.myfirstapplication;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;


/**
 * Created by bober on 8/29/2016.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    //Instance variables
    private MainThread thread;
    public static Player player;  // Player bubble
    private static DotManager dotManager;   // Dots
    public static int score;  // Game score = total number of dots generated
    private static boolean gameOver;
    private static long gameOverTime;
    private AdManager ads;
    public static int adCount;

    private int x;          // Player x position
    private int y;           // Player y position

    private OrientationData orientationData;
    private long frameTime;

    private static SharedPreferences prefs;
    public static int highScore;
    public String saveScore = "highScore";


    // Constructor
    public GamePanel (Context context)
    {
        super(context);
        getHolder().addCallback(this);

        ads = new AdManager(context);
        adCount = 0;

        Constants.CURRENT_CONTEXT = getContext();

        thread = new MainThread(getHolder(), this);
        setFocusable(true);

        x = Constants.SCREEN_WIDTH/2;
        y = Constants.SCREEN_HEIGHT/2;
        player = new Player(x, y, Constants.PLAYER_RADIUS_INITIAL, Color.rgb(255, 255, 0));
        dotManager = new DotManager(context);

        prefs = context.getSharedPreferences("com.nocompany.bober.myfirstapplication", context.MODE_PRIVATE);
        highScore = prefs.getInt(saveScore, 0);

        orientationData = new OrientationData();
        orientationData.register();
        frameTime = System.currentTimeMillis();
        score = 0;
        gameOver = false;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;

        while (retry){
            try{
                thread.setRunning(false);
                thread.join();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            retry = false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(gameOver && System.currentTimeMillis()-gameOverTime >= 1200 &&
                        (int)event.getY() < Constants.SCREEN_HEIGHT/2 && (int)event.getY() > Constants.SCREEN_HEIGHT/2-207*Constants.SCREEN_WIDTH/559) {
                    resetGame();
                    thread.newGame();
                }
                else if(gameOver && System.currentTimeMillis()-gameOverTime >= 1200 &&
                        (int)event.getY() > Constants.SCREEN_HEIGHT/2 && (int)event.getY() < Constants.SCREEN_HEIGHT/2+162*Constants.SCREEN_WIDTH/559){
                    System.exit(0);

                }
                break;
        }
        return true;
    }


    public void resetGame(){
        x = Constants.SCREEN_WIDTH/2;
        y = Constants.SCREEN_HEIGHT/2;
        player = new Player(x, y, Constants.PLAYER_RADIUS_INITIAL, Color.rgb(255, 255, 0));
        dotManager = new DotManager(getContext());
        orientationData = new OrientationData();
        orientationData.register();
        frameTime = System.currentTimeMillis();
        score = 0;
        gameOver = false;
    }

    //Updates game frame by frame
    public void update()
    {
            //Kinematics updates player orientation, acceleration, velocity, position
            kinematics();
            //Player update only changes player position.  Player's radius and color are changed in dot update
            player.update(x, y, player.getRadius(), player.getColor());
            //Dot update changes player radius and color, removes dots after collision
            dotManager.update();
    }

    //Updates screen output
    @Override
    public void draw(Canvas canvas)
    {

            super.draw(canvas);
            canvas.drawColor(Color.WHITE);
            player.draw(canvas);
            dotManager.draw(canvas);
            TextPaint paint = dotManager.getTextPaint();
            paint.setColor(Color.BLUE);
            paint.setTextSize(Constants.SCREEN_WIDTH/15);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("" + score, Constants.SCREEN_WIDTH/30, Constants.SCREEN_WIDTH/30 + paint.descent() - paint.ascent(), paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("BEST: " + highScore, Constants.SCREEN_WIDTH - Constants.SCREEN_WIDTH/30, Constants.SCREEN_WIDTH/30 + paint.descent() - paint.ascent(), paint);

        if(gameOver) {
            prefs.edit().putInt(saveScore, highScore).commit();

            Bitmap reset = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.play_again_button);
            Bitmap exit = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.exit_button);
            reset = Bitmap.createScaledBitmap(reset, Constants.SCREEN_WIDTH - 4, 207 * Constants.SCREEN_WIDTH / 559 - 2, true);
            exit = Bitmap.createScaledBitmap(exit, Constants.SCREEN_WIDTH - 4, 162 * Constants.SCREEN_WIDTH / 559 - 2, true);

            if(System.currentTimeMillis() - gameOverTime > 1000 && adCount % 3 == 0){  //This will display an add after every other game
                ads.displayAd();
                adCount++;
            }
            else if (System.currentTimeMillis() - gameOverTime < 1200 && System.currentTimeMillis() - gameOverTime > 200) {
                canvas.drawBitmap(reset, -Constants.SCREEN_WIDTH + (System.currentTimeMillis() - gameOverTime - 200) * Constants.SCREEN_WIDTH / 1000, Constants.SCREEN_HEIGHT / 2 - (207 * Constants.SCREEN_WIDTH / 559) - 5, new Paint());
                canvas.drawBitmap(exit, Constants.SCREEN_WIDTH - (System.currentTimeMillis() - gameOverTime - 200) * Constants.SCREEN_WIDTH / 1000, Constants.SCREEN_HEIGHT / 2, new Paint());
            }
            else if (System.currentTimeMillis() - gameOverTime > 1200){
                canvas.drawBitmap(reset, 2, Constants.SCREEN_HEIGHT / 2 - (207 * Constants.SCREEN_WIDTH / 559) - 5, new Paint());
                canvas.drawBitmap(exit, 2, Constants.SCREEN_HEIGHT / 2, new Paint());
            }
        }
    }

    public void kinematics(){
        //Orientation update
        int elapsedTime = (int) (System.currentTimeMillis() - frameTime);
        frameTime = System.currentTimeMillis();

        if(orientationData.getOrientation() != null){
            float pitch = orientationData.getOrientation()[1];   // range: -pi to pi
            float roll = orientationData.getOrientation()[2];    // range: -pi/2 to pi/2

            if (pitch > 0.524)   // Max pitch = 30 degrees
                pitch = 0.524f;
            else if (pitch < -0.524)    // Min pitch = -30 degrees
                pitch = -0.524f;

            if (roll > 0.524)   // Max roll = 30 degrees
                roll = 0.524f;
            else if (roll < -0.524)     // Min pitch = -30 degrees
                roll = -0.524f;

            double xDrag = -player.getX_vel()*0.0005;   // Arbitrary drag constant based on trial and error
            double yDrag = -player.getY_vel()*0.0005;

            double xAccel = Math.sin(roll)*Constants.SCREEN_WIDTH/150000;   // These are arbitrary values based on trial and error
            double yAccel = -Math.sin(pitch)*Constants.SCREEN_HEIGHT/300000;// These constants make acceleration roughly match actual gravity for average screen size

            // If acceleration is tiny, make it zero
            if(Math.sqrt(Math.pow(xAccel,2)+Math.pow(yAccel,2)) < 0.0001) {
                xAccel = 0;
                yAccel = 0;
            }

            player.setX_vel(player.getX_vel() + (xAccel+xDrag)*elapsedTime);
            player.setY_vel(player.getY_vel() + (yAccel+yDrag)*elapsedTime);

            // If velocity is tiny, and we aren't accelerating, make velocity zero so player will stop
            if(Math.sqrt(Math.pow(player.getX_vel(),2)+Math.pow(player.getY_vel(),2)) < 0.068 && xAccel == 0) {
                player.setX_vel(0);
                player.setY_vel(0);
            }

            x = (int)(player.getX() + player.getX_vel()*elapsedTime + 0.5*(xAccel+xDrag)*Math.pow(elapsedTime, 2));
            y = (int)(player.getY() + player.getY_vel()*elapsedTime + 0.5*(yAccel+yDrag)*Math.pow(elapsedTime, 2));
        }

        if(x < 0) {
            x = 0;
            player.setX_vel(0);
        }
        else if(x > Constants.SCREEN_WIDTH) {
            x = Constants.SCREEN_WIDTH;
            player.setX_vel(0);
        }

        if(y < 0) {
            y = 0;
            player.setY_vel(0);
        }
        else if(y > Constants.SCREEN_HEIGHT - Constants.PLAYER_RADIUS_INITIAL) {
            y = Constants.SCREEN_HEIGHT - Constants.PLAYER_RADIUS_INITIAL;
            player.setY_vel(0);
        }
    }

    public void generateDot(){
        dotManager.generateDot();
    }

    public static Player getPlayer() {
        return player;
    }

    public static void setGameOver(boolean gameOver){
        GamePanel.gameOver = gameOver;
    }

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void setGameOverTime(long gameOverTime) {
        GamePanel.gameOverTime = gameOverTime;
    }

}
