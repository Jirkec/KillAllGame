package com.example.semestral.utility;

import android.widget.TextView;

import com.example.semestral.game.GameView;

public class CountDown extends Thread {
    private GameView gameView;
    private TextView textView;
    private String text;
    private int time;
    private boolean isRunning = true;
    public long finish;

    public CountDown(GameView gameView, TextView textView, String text, int time){
        this.gameView = gameView;
        this.textView = textView;
        this.text = text;
        this.time = time;
    }

    @Override
    public void run() {
        finish = System.currentTimeMillis() + time;
        while(finish > System.currentTimeMillis() && isRunning) {
            try {
                sleep(300);
                textView.setText(text + (Math.round((time/1000.0)*100)/100) );
                time -= 300;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(isRunning)
            gameView.getGameActivity().runOnUiThread(() -> gameView.gameFinished(false));
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void addTime(int amount) {
        time += amount;
        finish += amount;
    }
}
