package com.example.ziffi.airhockey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    CustomSurfaceViewClass csvc;
    float x, y;
    //For motion action and events
    float sx, sy, fx, fy, dx, dy, scaledX, scaledY, animX, animY;
    float screenwidth, screenheight;
    float topLeftX, topLeftY;
    float strikerTopLeftX, strikerTopLeftY;
    int signFactorX = 1, signFactorY = 1, touchIndicator = 0;
    Canvas canvas;

    Bitmap test, plus, striker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        csvc = new CustomSurfaceViewClass(this);
        test = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
        plus = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        striker = BitmapFactory.decodeResource(getResources(), R.drawable.striker2);
        x = 0; y = 0;
        sx = sy = fx = fy = dx = dy = scaledX = scaledY = animX = animY = 0;


        signFactorX = signFactorY = 1;

        csvc.setOnTouchListener(this);
        setContentView(csvc);
    }


    @Override
    protected void onPause() {
        super.onPause();
        csvc.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        csvc.resume();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){

        // very 1st time scaled X & Y should be 10 then it should be increase subsequently and scaled should not come down to 10 when user touch striker next time. .
        if(touchIndicator == 0){
            scaledX = 10;
            scaledY = -10;
        }
        touchIndicator = 1;
        strikerTopLeftX = event.getX() - striker.getWidth()/2;
        sy = fy = strikerTopLeftY - test.getHeight();

        return  true;

    }

    //other class

    public class CustomSurfaceViewClass extends SurfaceView implements Runnable{


        SurfaceHolder ourHolder;
        Thread ourThread = null;
        boolean isRunning = false;
        public CustomSurfaceViewClass(Context context) {
            super(context);
            ourHolder = getHolder();
            ourThread = new Thread(this);
            ourThread.start();
        }

        public void pause(){
            isRunning = false;
            while (true){
                try {
                    ourThread.join();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                break;
            }
            ourThread = null;
        }
        public void resume(){
            isRunning = true;
            ourThread = new Thread(this);
            ourThread.start();

        }

        @Override
        public void run() {

//            canvas.drawBitmap(striker, (x2/2-striker.getWidth()/2), y3-striker.getHeight(), null);

            while (isRunning){
                if(!ourHolder.getSurface().isValid())
                    continue;

                canvas = ourHolder.lockCanvas();
                canvas.drawRGB(204, 204, 255);

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(0xFF33B5E5);
                paint.setStrokeWidth(4);
                int x1 = 0, y1 = 0;
                int x2 = getWidth()-0, y2 = 0;
                int x3 = getWidth()-0, y3 = getHeight()-0;
                int x4 = 0, y4 = getHeight()-0;

                canvas.drawLine(x1, y1, x2, y2, paint);
                canvas.drawLine(x2, y2, x3, y3, paint);
                canvas.drawLine(x3, y3, x4, y4, paint);
                canvas.drawLine(x4, y4, x1, y1, paint);

                if(touchIndicator == 0){
                    strikerTopLeftX = getWidth()/2-striker.getWidth()/2;
                    strikerTopLeftY = getHeight()-striker.getHeight();

                    sx = fx = strikerTopLeftX + striker.getWidth()/2-test.getWidth()/2;
                    sy = fy = strikerTopLeftY - test.getHeight();

                    canvas.drawBitmap(striker, strikerTopLeftX, strikerTopLeftY, null);
                    canvas.drawBitmap(test, (strikerTopLeftX+striker.getWidth()/2-test.getWidth()/2), (strikerTopLeftY-test.getHeight()), null);

                }
                else if(touchIndicator ==1) {
//
                    if(strikerTopLeftX < 0){
                        strikerTopLeftX = 0;
                    }
                    else if (strikerTopLeftX > (getWidth() - striker.getWidth())){
                        strikerTopLeftX = (getWidth() - striker.getWidth());
                    }
                    else{
                        canvas.drawBitmap(striker, strikerTopLeftX, strikerTopLeftY, null);
                    }


                    if (fx != 0 && fy != 0) {
                        topLeftX = fx + animX;
                        topLeftY = fy + animY;
                        canvas.drawBitmap(test, topLeftX, topLeftY, null);
                        Log.d("reached : ", topLeftX+", "+topLeftY);

                        if (topLeftX + test.getWidth() >= x2) {
                            signFactorX = -1 * signFactorX;
                            Log.d("CenterX, x2, width = ", "" + topLeftX + ", " + x2 + ", " + getWidth());
                        }
                        if (topLeftX <= x1) {
                            signFactorX = -1 * signFactorX;
                            Log.d("X = ", "0");
                        }
                        if (topLeftY > y3) {
                            signFactorY = -1 * signFactorY;

                            animX = animY = scaledX = scaledY = 0;
                            isRunning = false;


                            Log.d("Y = ", "screen height");
                        }
                        if (topLeftY <= y1) {
                            signFactorY = -1 * signFactorY;
                            Log.d("Y = ", "0");
                        }
                        if((topLeftX > strikerTopLeftX && topLeftX < (strikerTopLeftX + striker.getWidth())) && ((topLeftY+test.getHeight()) > strikerTopLeftY)){
                            signFactorY = -1 * signFactorY;
                            if(scaledX < 20){
                                scaledX += 2;
                                scaledY += 2;
                            }
                            else if(scaledX < 40){
                                scaledX += 5;
                                scaledY += 5;
                            }

                        }

                    }

                    animX = animX + signFactorX * scaledX;
                    animY = animY + signFactorY * scaledY;
                }

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
    }


}
