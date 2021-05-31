package com.example.semestral.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.semestral.R;
import com.example.semestral.data.DataModel;
import com.example.semestral.data.GameData;

import java.util.Random;

public class SkinActivity extends AppCompatActivity {
    public DataModel dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);

        dataModel = getIntent().getParcelableExtra(DataModel.PARCEL_NAME);
        dataModel.setSettings(this);

        initiateSkinIcons();
        updateGoldBilance();
    }

    public void updateGoldBilance(){
        TextView textw_gold = findViewById(R.id.textw_gold_skin);
        textw_gold.setText(dataModel.getGold()+" "+getString(R.string.goldCurrency));
    }

    public void initiateSkinIcons(){
        for(int i = 0; i < DataModel.MAX_SKINS; i++){
            ImageButton skinBtn = findViewById(dataModel.getSkinResIDByID(i));
            int finalI = i;
            skinBtn.setOnClickListener(v -> selectSkin(finalI));
            if(dataModel.getUnlockedSkins()[i]){
                skinBtn.setImageResource(DataModel.skinSourcesIcon[i]);
            }
        }

        ImageButton selectedSkin = findViewById(dataModel.getSkinResIDByID(dataModel.getSelectedSkin()));
        selectedSkin.setColorFilter(Color.argb(128, 22, 143, 16));
    }

    public void selectSkin(int id){
        if(dataModel.getUnlockedSkins()[id]){
            ImageButton selectedSkin = findViewById(dataModel.getSkinResIDByID(dataModel.getSelectedSkin()));
            selectedSkin.setColorFilter(Color.argb(0, 0, 0, 0));

            dataModel.setSelectedSkin(id);
            selectedSkin = findViewById(dataModel.getSkinResIDByID(dataModel.getSelectedSkin()));
            selectedSkin.setColorFilter(Color.argb(128, 22, 143, 16));
        }
    }

    public void unlockSkin(View v){
        if(dataModel.getGold() >= 700){
            int skinID = dataModel.getRandomLockedSkin();

            if(skinID > -1){
                dataModel.getUnlockedSkins()[skinID] = true;
                ImageButton skinBtn = findViewById(dataModel.getSkinResIDByID(skinID));
                skinBtn.setImageResource(DataModel.skinSourcesIcon[skinID]);
                dataModel.removeGold(700);
            }else {
                Toast.makeText(getApplicationContext(),"All skins are unlocked", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Not enough gold", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToMain(View v){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(DataModel.PARCEL_NAME, dataModel);
        startActivity(i);
    }
}