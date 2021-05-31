package com.example.semestral.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.semestral.R;
import com.example.semestral.activity.GameActivity;
import com.example.semestral.data.DataModel;
import com.example.semestral.data.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView {

    private final GameLoopThread gameLoopThread;
    private final List<Sprite> sprites = new ArrayList<>();
    private final List<TempSprite> temps = new ArrayList<>();
    private long lastClick;
    private final Bitmap bmpBlood;
    private GameActivity gameActivity;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameLoopThread = new GameLoopThread(this);

        getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                createSprites();
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
        });

        bmpBlood = BitmapFactory.decodeResource(getResources(), R.drawable.blood2);
    }

    public void slow(double coefficient){
        sprites.forEach((sprite) -> {
            int newXSpeed = (int) Math.round(sprite.getxSpeed() * coefficient);
            int newYSpeed = (int) Math.round(sprite.getySpeed() * coefficient);
            sprite.setxSpeed(newXSpeed == 0 ? 1 : newXSpeed);
            sprite.setySpeed(newYSpeed == 0 ? 1 : newYSpeed);
        });
    }

    public void gameFinished(boolean victory) {
        gameActivity.endCountDown();
        gameActivity.openFinishDialog(victory, sprites.size());
        if(victory){
            gameActivity.dataModel.levelUp();
        }
    }

    private void createSprites() {
        for(int i = 0; i < gameActivity.numOfMinions; i++) {
            sprites.add(createSprite(DataModel.skinSources[gameActivity.dataModel.getSelectedSkin()]));
        }
    }

    private Sprite createSprite(int resouce) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
        return new Sprite(this, bmp, gameActivity.dataModel.getLevel() );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        for (int i = temps.size() - 1; i >= 0; i--) {
            temps.get(i).onDraw(canvas);
        }

        for (Sprite sprite : sprites) {
            sprite.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - lastClick > 300) {
            lastClick = System.currentTimeMillis();
            float x = event.getX();
            float y = event.getY();

            synchronized (getHolder()) {
                for (int i = sprites.size() - 1; i >= 0; i--) {
                    Sprite sprite = sprites.get(i);
                    if (sprite.isCollition(x, y)) {
                        sprites.remove(sprite);
                        temps.add(new TempSprite(temps, this, x, y, bmpBlood));

                        gameActivity.dataModel.tryPlaySound(R.raw.scream9, gameActivity);
                        gameActivity.dataModel.tryVibrate(gameActivity);

                        if(sprites.size() == 0){
                            gameFinished(true);
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    public GameActivity getGameActivity() {
        return gameActivity;
    }

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }
}
