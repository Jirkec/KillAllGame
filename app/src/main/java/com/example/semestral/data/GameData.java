package com.example.semestral.data;
import java.io.Serializable;

import static com.example.semestral.data.DataModel.MAX_MISSIONS_COUNT;
import static com.example.semestral.data.DataModel.MAX_SKINS;

public class GameData implements Serializable {
    public int level = 1;
    public int slowLevel = 1;
    public int timeLevel = 1;
    public int incomeLevel = 1;
    public int selectedSkin = 0;
    public boolean[] unlockedSkins = new boolean[MAX_SKINS];
    public int gold = 100;
    public Missions [] missions = new Missions[MAX_MISSIONS_COUNT];
    public int minionsKilledCounter = 0;    //for Missions.DESTROY
    public int levelUpCounter = 0;          //for Missions.LEVEL_UP
    public int levelUpAbilityCounter = 0;   //for Missions.LEVEL_UP_ABILITY
    public int completeMissionCounter = 0;  //for Missions.COMPLETE_MISSION

    public GameData(){
        unlockedSkins[0] = true;
    }

}
