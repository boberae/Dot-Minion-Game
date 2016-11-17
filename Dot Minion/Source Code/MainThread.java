package com.nocompany.bober.myfirstapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * Created by bober on 8/29/2016.
 */
public class MainThread extends Thread {
    public static final int MAX_FPS = 40;   //At max speed, a new frame comes every 0.025 seconds
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;
    private long startTime;
    private long timeMillis = 1000/MAX_FPS;
    private long waitTime;
    private int frameCount = 0;
    private int secondsRun = 0;
    private long targetTime = 1000/MAX_FPS;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
        canvas = new Canvas();
    }

    public void setRunning(boolean running)
    {
        this.running = running;

    }

    @Override
    public void run()
    {
        newGame();

        while (running)
        {
            startTime = System.nanoTime();

            // Try to update and redraw the gamePanel
            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized(surfaceHolder)
                {
                    if(!gamePanel.isGameOver())
                        this.gamePanel.update();    //Update gamePanel

                    this.gamePanel.draw(canvas);    //Redraw the gamePanel onto the screen
                    //Increase number of dots generated as game speed increases
                    if(!gamePanel.isGameOver()) {
                        if (frameCount % (8 - (int)(DotManager.speedIncrease*6)) == 0)  // Increase rate of dot addition
                            this.gamePanel.generateDot();
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                if (canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

            //Once update and draw are finished, continue to next frame, but wait if necessary to cap the frame rate
            timeMillis = (System.nanoTime() - startTime)/1000000;   //Time code runs during a frame
            waitTime = targetTime - timeMillis;     //Time to wait before next frame update
            try{
                if(waitTime > 0){
                    this.sleep(waitTime);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            frameCount++;
            if(frameCount == MAX_FPS)
            {
                frameCount = 0;
                secondsRun++;
                //Increase speed every two seconds
                    if(secondsRun % 2 == 0 && DotManager.speedIncrease < 0.8) {
                        DotManager.speedIncrease += 0.025;
                        System.out.println("" + DotManager.speedIncrease);
                    }
                //Increment score every second while game is still going
                    if(!gamePanel.isGameOver()){
                        gamePanel.score++;
                    }
            }
        }


    }

    public void newGame(){
        timeMillis = 1000/MAX_FPS;
        frameCount = 0;
        secondsRun = 0;
        targetTime = 1000/MAX_FPS;
    }

}
