package hieu.game.flappybird;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;


public class GameView extends View {

    //our custom View class
    Handler handler; // xu ly yeu cau
    Runnable runnable;
    final int DELAY_SECONDS = 90;
    Bitmap background, gameOvers;
    Bitmap topTube, bottomTube;
    Display display;
    Point point;
    int dWidth, dHeight;
    Rect rect;
    //Bitmap array for the bird
    Bitmap[] birds;
    int birdFrame = 0;
    int velocity=0, gravity=3; // let play around with these value
    //vi tri cua bird
    int birdX, birdY;
    boolean gameState = true;
    int gap = 400;
    int minTubeOffset, maxTubeOffset;
    int numberOfTubes = 4;
    int distanceBetweenTubes;
    int[] tubeX = new int[numberOfTubes];
    int[] topTubeY = new int[numberOfTubes];
    Random random;
    int tubeVelocity = 8;

    public GameView(Context context) {
        super(context);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        background = BitmapFactory.decodeResource(getResources(), R.drawable.editedflappybirdbackgroundnight);
        topTube = BitmapFactory.decodeResource(getResources(), R.drawable.columnspritereverse_128);
        bottomTube = BitmapFactory.decodeResource(getResources(), R.drawable.columnsprite_128);
        gameOvers = BitmapFactory.decodeResource(getResources(), R.drawable.photo_18);
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth = point.x;
        dHeight = point.y;
        rect = new Rect(0,0,dWidth,dHeight);
        birds = new Bitmap[2];
        birds[0] = BitmapFactory.decodeResource(getResources(),R.drawable.birdenemyflapsprite_32);
        birds[1] = BitmapFactory.decodeResource(getResources(),R.drawable.birdenemyidlesprite_32);
        birdX = dWidth/2 - birds[0].getWidth()/2;
        birdY = dHeight/2 - birds[1].getHeight()/2;
        distanceBetweenTubes = dWidth*3/4;
        minTubeOffset = gap;
        maxTubeOffset = dHeight - minTubeOffset - gap;
        random = new Random();
        for(int i = 0; i < numberOfTubes; i++) {
            tubeX[i] = dWidth + i*distanceBetweenTubes;
            topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rect, null);

        if (gameState){
            if (birdFrame == 0) {
                birdFrame = 1;
            } else {
                birdFrame = 0;
            }
            if (birdY < dHeight - birds[0].getHeight() || velocity < 0) { //con trong khung hinh
                velocity += gravity; //roi theo gia tri gravity
                birdY += velocity; //cap nhat lai
            } else gameState = false;
            canvas.drawBitmap(birds[birdFrame], birdX, birdY, null);
            for(int i = 0; i < numberOfTubes; i++) {

                tubeX[i] -= tubeVelocity;
                if(tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
                }
                if (isCollision(tubeX[i], topTubeY[i], topTube.getWidth(), gap)) {
                    gameState = false;
                }
                canvas.drawBitmap(topTube, tubeX[i], topTubeY[i] - topTube.getHeight(), null);
                canvas.drawBitmap(bottomTube, tubeX[i], topTubeY[i] + gap, null);
            }
        } else {
            canvas.drawBitmap(gameOvers,dWidth /2 - gameOvers.getWidth()/2, dHeight/2 - gameOvers.getHeight()/2, null);
        }
        canvas.drawBitmap(birds[0], birdX, birdY, null);
        //Both bird[0] and birds[1] have same dimension
        handler.postDelayed(runnable, DELAY_SECONDS);
    }
    // bat su kien click

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN) { // neu thay nhan chuot
            velocity = -30; //click thi bay len
            //gameState = true;
        }

        return true;
    }
    private boolean isCollision(int tubeX, int topTubeY, int widthPipe, int gap){
        if (birdX + birds[0].getWidth() >= tubeX  && birdX + birds[0].getWidth() <= tubeX + widthPipe){
            if (birdY >= topTubeY && birdY <= topTubeY + gap){
                return false;
            }
            else
                return true;
        }
        return false;
    }
}
