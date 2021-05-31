package com.example.semestral.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.semestral.R;
import com.example.semestral.data.DataModel;
import com.example.semestral.data.Missions;

public class MainActivity extends AppCompatActivity {
    public DataModel dataModel = null;
    public View popupViewMissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataModel = getIntent().getParcelableExtra(DataModel.PARCEL_NAME);
        if(dataModel == null){
            dataModel = new DataModel(this);
        }else{
            dataModel.setSettings(this);
            dataModel.saveGameData(this);
        }

        TextView levelText = findViewById(R.id.levelText);
        levelText.setText(getString(R.string.level)+": "+dataModel.getLevel());

        updateGoldBilance();
        updateSlowLevelText();
        updateTimeLevelText();
        updateIncomeLevelText();

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupViewMissions = inflater.inflate(R.layout.activity_missions, null);

        dataModel.tryPlayMusic(R.raw.musicbackground, this);
    }
    public void updateGoldBilance(){
        TextView textw_gold = findViewById(R.id.textw_gold);
        textw_gold.setText(dataModel.getGold()+" "+getString(R.string.goldCurrency));
    }

    public void openSetting(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupViewSetting = inflater.inflate(R.layout.activity_setting, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupViewSetting, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        ImageButton btn_closeSettings = popupViewSetting.findViewById(R.id.btn_closeSetting);
        btn_closeSettings.setOnClickListener(v -> popupWindow.dismiss());

        Switch switch_sound = popupViewSetting.findViewById(R.id.switch_sound);
        switch_sound.setChecked(dataModel.isSounds());
        switch_sound.setOnCheckedChangeListener((buttonView, isChecked) -> dataModel.setSettingSounds(isChecked));

        Switch switch_music = popupViewSetting.findViewById(R.id.switch_music);
        switch_music.setChecked(dataModel.isMusic());
        switch_music.setOnCheckedChangeListener((buttonView, isChecked) -> dataModel.setSettingMusic(isChecked));

        Switch switch_vibration = popupViewSetting.findViewById(R.id.switch_vibration);
        switch_vibration.setChecked(dataModel.isVibrations());
        switch_vibration.setOnCheckedChangeListener((buttonView, isChecked) -> dataModel.setSettingVibrations(isChecked));

    }


    public void openMissions(View view) {
        // inflate the layout of the popup window
//        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        View popupViewMissions = inflater.inflate(R.layout.activity_missions, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindowMissions = new PopupWindow(popupViewMissions, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindowMissions.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        ImageButton btn_closeSettings = popupViewMissions.findViewById(R.id.btn_closeMissions);
        btn_closeSettings.setOnClickListener(v -> popupWindowMissions.dismiss());

        updateMissions();

        ConstraintLayout layout_mission1 = popupViewMissions.findViewById(R.id.layout_mission1);
        layout_mission1.setOnClickListener(v -> completeMission(0));

        ConstraintLayout layout_mission2 = popupViewMissions.findViewById(R.id.layout_mission2);
        layout_mission2.setOnClickListener(v -> completeMission(1));

        ConstraintLayout layout_mission3 = popupViewMissions.findViewById(R.id.layout_mission3);
        layout_mission3.setOnClickListener(v -> completeMission(2));

    }

    public void updateMissions(){
        updateMissionByID(0);
        updateMissionByID(1);
        updateMissionByID(2);
    }

    public void updateMissionByID(int id){
        Missions mission = dataModel.getMissions()[id];
        TextView textw_mission_name;
        TextView textw_mission_rew;
        TextView textw_mission_rem;
        int needed = dataModel.getNeededByMission(mission);
        int counted = dataModel.getCountedByMission(mission);

        switch (id){
            case 0: textw_mission_name = popupViewMissions.findViewById(R.id.textw_mission_name1);
                    textw_mission_rew = popupViewMissions.findViewById(R.id.textw_mission_rew1);
                    textw_mission_rem = popupViewMissions.findViewById(R.id.textw_mission_rem1);
                    break;
            case 1: textw_mission_name = popupViewMissions.findViewById(R.id.textw_mission_name2);
                    textw_mission_rew = popupViewMissions.findViewById(R.id.textw_mission_rew2);
                    textw_mission_rem = popupViewMissions.findViewById(R.id.textw_mission_rem2);
                    break;
            case 2: textw_mission_name = popupViewMissions.findViewById(R.id.textw_mission_name3);
                    textw_mission_rew = popupViewMissions.findViewById(R.id.textw_mission_rew3);
                    textw_mission_rem = popupViewMissions.findViewById(R.id.textw_mission_rem3);
                    break;

            default:    throw new IllegalArgumentException();
        }
        textw_mission_name.setText(dataModel.getMissionName(mission));
        textw_mission_rew.setText(getString(R.string.reward)+": "+dataModel.getMissionReward(mission)+getString(R.string.goldCurrency));
        textw_mission_rem.setText(counted +" / "+needed);
        if(counted < needed)
            textw_mission_rem.setTextColor(Color.parseColor("#FF4444")); //red
        else
            textw_mission_rem.setTextColor(Color.parseColor("#07ba16")); //green
    }

    public void completeMission(int id){
        Missions mission = dataModel.getMissions()[id];
        int needed = dataModel.getNeededByMission(mission);
        int counted = dataModel.getCountedByMission(mission);

        if(counted >= needed){
            dataModel.resetCounterByMission(mission);
            dataModel.addGold(dataModel.getMissionReward(mission));
            dataModel.addCompleteMissionCounter();
            dataModel.getMissions()[id] = dataModel.getRandomFreeMission();
            updateGoldBilance();
            updateMissionByID(id);
            int completeMissionID = dataModel.findInMissions(Missions.COMPLETE_MISSION);
            if(completeMissionID > -1){
                updateMissionByID(completeMissionID);
            }
        }
    }

    public void openActivityGame(View view){
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(DataModel.PARCEL_NAME, dataModel);
        startActivity(i);
    }

    public void openActivitySkin(View view){
        Intent i = new Intent(this, SkinActivity.class);
        i.putExtra(DataModel.PARCEL_NAME, dataModel);
        startActivity(i);
    }

    public void updateSlowLevelText(){
        TextView textw_slowLevel = findViewById(R.id.textw_level_slow);
        if(DataModel.MAX_SLOW_LEVEL == dataModel.getSlowLevel()){
            textw_slowLevel.setText(getString(R.string.level) + ": MAX");
            Button btn_slow = findViewById(R.id.btn_slow);
            btn_slow.setEnabled(false);
        }else {
            textw_slowLevel.setText(getString(R.string.level) + ": " + dataModel.getSlowLevel() + "\n Cost: " + dataModel.getlevelUpSlowCost() + " " + getText(R.string.goldCurrency));
        }
    }
    public void updateTimeLevelText(){
        TextView textw_level_time = findViewById(R.id.textw_level_time);
        textw_level_time.setText(getString(R.string.level)+": "+dataModel.getTimeLevel()+"\n Cost: "+dataModel.getlevelUpTimeCost()+" "+getText(R.string.goldCurrency));
    }
    public void updateIncomeLevelText(){
        TextView textw_level_income = findViewById(R.id.textw_level_income);
        textw_level_income.setText(getString(R.string.level)+": "+dataModel.getIncomeLevel()+"\n Cost: "+dataModel.getlevelUpIncomeCost()+" "+getText(R.string.goldCurrency));
    }

    public void levelUpSlow(View view){
        if(dataModel.getGold() >= dataModel.getlevelUpSlowCost()) {
            dataModel.removeGold(dataModel.getlevelUpSlowCost());
            dataModel.slowLevelUp();
            updateSlowLevelText();
            updateGoldBilance();
        }
    }

    public void levelUpTime(View view){
        if(dataModel.getGold() >= dataModel.getlevelUpTimeCost()) {
            dataModel.removeGold(dataModel.getlevelUpTimeCost());
            dataModel.timeLevelUp();
            updateTimeLevelText();
            updateGoldBilance();
        }
    }

    public void levelUpIncome(View view){
        if(dataModel.getGold() >= dataModel.getlevelUpIncomeCost()) {
            dataModel.removeGold(dataModel.getlevelUpIncomeCost());
            dataModel.incomeLevelUp();
            updateIncomeLevelText();
            updateGoldBilance();
        }
    }

}