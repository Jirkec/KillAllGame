package com.example.semestral.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.semestral.data.DataModel;
import com.example.semestral.data.Missions;
import com.example.semestral.game.GameView;
import com.example.semestral.R;
import com.example.semestral.utility.CountDown;

public class GameActivity extends AppCompatActivity {

    public DataModel dataModel;
    private CountDown countDown;
    private GameView gameView;

    public int numOfMinions;
    public int timeForMinion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dataModel = getIntent().getParcelableExtra(DataModel.PARCEL_NAME);
        dataModel.setSettings(this);

        gameView = findViewById(R.id.view_game);
        gameView.setGameActivity(this);

        int level = dataModel.getLevel();
        timeForMinion = (int) ((10.0/level) * 1000);
        numOfMinions = (int) level/3 + (level%3==1 || level%3==2 ? 1 : 0);

        TextView textw_time = findViewById(R.id.textw_time);
        countDown = new CountDown(gameView, textw_time, getText(R.string.time)+": ", numOfMinions * timeForMinion);
        countDown.start();
    }

    public void openFinishDialog(boolean victory, int numOfSurvived){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_finish, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(gameView, Gravity.CENTER, 0, 0);

        TextView title = popupView.findViewById(R.id.textw_setting_header);
        title.setText(victory ? R.string.victory : R.string.defeat);

        int numOfKilled = numOfMinions-numOfSurvived;
        TextView killed = popupView.findViewById(R.id.textw_killed);
        killed.setText(killed.getText()+" "+numOfKilled);

        int bonusGold = (victory? dataModel.getLevel() : 0);
        TextView bonus = popupView.findViewById(R.id.textw_bonus);
        bonus.setText(bonus.getText()+" "+bonusGold);

        int totalGold = numOfKilled * dataModel.getIncomeLevel() + bonusGold;
        TextView total = popupView.findViewById(R.id.textw_total_gold);
        total.setText(total.getText()+" "+totalGold);

        if(dataModel.isInMissions(Missions.DESTROY))
            dataModel.addMinionsKilledCounter(numOfKilled);

        Button btn_closeSettings = popupView.findViewById(R.id.btn_claim);
        btn_closeSettings.setOnClickListener(v -> handleClaim(totalGold));
    }

    public void handleClaim(int totalGold){
        dataModel.addGold(totalGold);
        changeActivityToMain();
    }

    public void abilitySlow(View v){
        Button slow = findViewById(R.id.btn_slow);
        slow.setEnabled(false);
        gameView.slow(1 - (dataModel.getSlowLevel() * 0.1) );
    }

    public void abilityTime(View v){
        Button time = findViewById(R.id.btn_ability_time);
        time.setEnabled(false);
        countDown.addTime(dataModel.getTimeLevel() * 1000);
    }

    public void backToMain(View v){
        endCountDown();
        changeActivityToMain();
    }

    public void changeActivityToMain(){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(DataModel.PARCEL_NAME, dataModel);
        startActivity(i);
    }

    public void endCountDown() {
        countDown.setRunning(false);
    }
}
