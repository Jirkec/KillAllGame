package com.example.semestral.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Vibrator;

import com.example.semestral.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("serial")
public class DataModel implements Parcelable{
    public static final String GAME_DATA_FILE_NAME = "GameData.txt";
    public static final String SETTINGS_FILE_NAME = "MyPref";
    public static final String PARCEL_NAME = "DATA_MODEL";

    public static final int MAX_SKINS = 9;

    public static final int MAX_SLOW_LEVEL = 9;

    public static final int MAX_MISSIONS_COUNT = 3;
    public static final int MISSIONS_TYPE_COUNT = 4;

    public static final int NEEDED_MINION_KILLED_MISSION = 10;
    public static final int NEEDED_LEVEL_UP_MISSION = 2;
    public static final int NEEDED_LEVEL_UP_ABILITY_MISSION = 2;
    public static final int NEEDED_COMPLETE_MISSION = 2;

    public static final int[] skinSources = {
            R.drawable.bad1,
            R.drawable.bad2,
            R.drawable.bad3,
            R.drawable.bad4,
            R.drawable.bad5,
            R.drawable.bad6,
            R.drawable.good1,
            R.drawable.good2,
            R.drawable.good3
    };
    public static final int[] skinSourcesIcon = {
            R.drawable.bad1,
            R.drawable.bad2,
            R.drawable.bad3,
            R.drawable.bad4,
            R.drawable.bad5,
            R.drawable.bad6,
            R.drawable.good1,
            R.drawable.good2,
            R.drawable.good3
    };

    private SharedPreferences settings = null;
    private GameData gameData;

    private boolean sounds;
    private boolean music;
    private boolean vibrations;

    private MediaPlayer mpMusic;


    protected DataModel(Parcel in) {
        sounds = in.readByte() != 0;
        music = in.readByte() != 0;
        vibrations = in.readByte() != 0;
        gameData = (GameData) in.readSerializable();
    }

    public static final Creator<DataModel> CREATOR = new Creator<DataModel>() {
        @Override
        public DataModel createFromParcel(Parcel in) {
            return new DataModel(in);
        }

        @Override
        public DataModel[] newArray(int size) {
            return new DataModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (sounds ? 1 : 0));
        dest.writeByte((byte) (music ? 1 : 0));
        dest.writeByte((byte) (vibrations ? 1 : 0));
        dest.writeSerializable(gameData);
    }

    public DataModel(Context context){
        settings = context.getSharedPreferences(SETTINGS_FILE_NAME, 0);
        initiateSetting();

        gameData = new GameData();
        generateMissions();

        if(!fileGameDataExists(context)){
            saveGameData(context);
        }else {
            loadGameData(context);
        }
    }

    public void saveGameData(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(GAME_DATA_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(gameData);
            os.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadGameData(Context context){
        try {
            FileInputStream fis = context.openFileInput(GAME_DATA_FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            gameData = (GameData) is.readObject();
            is.close();
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean fileGameDataExists(Context context){
        String [] files = context.fileList();
        for(int i = 0; i < files.length; i ++){
            if(files[i].equals(GAME_DATA_FILE_NAME))
                return true;
        }
        return false;
    }

    public void setSettings(Context context) {
        settings = context.getSharedPreferences(SETTINGS_FILE_NAME, 0);;
    }

    public SharedPreferences getSettings() {
        return settings;
    }

    public boolean isSounds() {
        return sounds;
    }

    public boolean isMusic() {
        return music;
    }

    public boolean isVibrations() {
        return vibrations;
    }

    public void initiateSetting(){
        loadSettingMusic();
        loadSettingSounds();
        loadSettingVibration();
    }

    public void loadSettingSounds(){
        sounds = settings.getBoolean("sounds", true);
    }
    public void loadSettingMusic(){
        music = settings.getBoolean("music", true);
    }
    public void loadSettingVibration(){
        vibrations = settings.getBoolean("vibrations", true);
    }

    public void setSettingSounds(boolean state){
        sounds = state;
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putBoolean("sounds", state);
        preferencesEditor.apply();
    }

    public void setSettingMusic(boolean state){
        music = state;
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putBoolean("music", state);
        preferencesEditor.apply();

        if(state)
            mpMusic.start();
        else
            mpMusic.pause();
    }

    public void setSettingVibrations(boolean state){
        vibrations = state;
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putBoolean("vibrations", state);
        preferencesEditor.apply();
    }

    public void tryPlaySound(int resid, Context context){
        MediaPlayer mp = MediaPlayer.create(context, resid);
        if(isSounds()) {
            mp.start();
        }
    }
    public void tryPlayMusic(int resid, Context context){
        mpMusic = MediaPlayer.create(context, resid);
        mpMusic.setLooping(true);
        if(isMusic()) {
            mpMusic.start();
        }
    }
    public void tryVibrate(Context context){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(isVibrations()) {
            vibrator.vibrate(300);
        }
    }


    public int getSelectedSkin() {
        return gameData.selectedSkin;
    }

    public void setSelectedSkin(int selectedSkin) {
        gameData.selectedSkin = selectedSkin;
    }

    public boolean[] getUnlockedSkins() {
        return gameData.unlockedSkins;
    }

    public int getSkinResIDByID(int id){
        int skinResID = -1;
        switch (id){
            case 0:  skinResID = R.id.btn_skin1;break;
            case 1:  skinResID = R.id.btn_skin2;break;
            case 2:  skinResID = R.id.btn_skin3;break;
            case 3:  skinResID = R.id.btn_skin4;break;
            case 4:  skinResID = R.id.btn_skin5;break;
            case 5:  skinResID = R.id.btn_skin6;break;
            case 6:  skinResID = R.id.btn_skin7;break;
            case 7:  skinResID = R.id.btn_skin8;break;
            case 8:  skinResID = R.id.btn_skin9;break;
        }
        return skinResID;
    }

    public int getRandomLockedSkin(){
        List<Integer> lockedSkins = new ArrayList<>();
        for(int i = 0; i < MAX_SKINS; i++){
            if(!gameData.unlockedSkins[i]){
                lockedSkins.add(i);
            }
        }
        if(lockedSkins.size() > 0) {
            Random rnd = new Random();
            return lockedSkins.get(rnd.nextInt(lockedSkins.size()));
        }else{
            return -1;
        }
    }


    public int getLevel() {
        return gameData.level;
    }

    public void levelUp(){
        gameData.level++;
        addLevelUpCounter();
    }

    public int getGold() {
        return gameData.gold;
    }

    public void addGold(int gold) {
        gameData.gold += gold;
    }

    public void removeGold(int gold) {
        gameData.gold -= gold;
    }


    public int getSlowLevel() {
        return gameData.slowLevel;
    }

    public void slowLevelUp() {
        gameData.slowLevel ++;
        addLevelUpAbilityCounter();
    }

    public int getTimeLevel() {
        return gameData.timeLevel;
    }

    public void timeLevelUp() {
        gameData.timeLevel ++;
        addLevelUpAbilityCounter();
    }

    public int getIncomeLevel() {
        return gameData.incomeLevel;
    }

    public void incomeLevelUp() {
        gameData.incomeLevel ++;
        addLevelUpAbilityCounter();
    }

    public int getlevelUpSlowCost(){
        return gameData.slowLevel * 100;
    }

    public int getlevelUpTimeCost(){
        return gameData.timeLevel * 50;
    }

    public int getlevelUpIncomeCost(){
        return gameData.incomeLevel * 60;
    }


    public Missions[] getMissions() {
        return gameData.missions;
    }

    public void generateMissions(){
        for(int i = 0; i < MAX_MISSIONS_COUNT; i++){
            gameData.missions[i] = getRandomFreeMission();
        }
    }

    public Missions getRandomFreeMission(){
        Random rnd = new Random();
        while (true) {
            int id = rnd.nextInt(MISSIONS_TYPE_COUNT);
            if (isMissionTypeFree(getMissionTypeByID(id))){
                return getMissionTypeByID(id);
            }
        }
    }

    public boolean isMissionTypeFree(Missions missions){
        for(int i = 0; i < MAX_MISSIONS_COUNT; i++){
            if(gameData.missions[i] == missions)
                return false;
        }
        return true;
    }

    public boolean isInMissions(Missions missions){
        for(int i = 0; i < MAX_MISSIONS_COUNT; i++){
            if(gameData.missions[i] == missions)
                return true;
        }
        return false;
    }

    public int findInMissions(Missions missions){
        for(int i = 0; i < MAX_MISSIONS_COUNT; i++){
            if(gameData.missions[i] == missions)
                return i;
        }
        return -1;
    }

    public Missions getMissionTypeByID(int ID){
        switch (ID){
            case 0: return Missions.DESTROY;
            case 1: return Missions.LEVEL_UP;
            case 2: return Missions.LEVEL_UP_ABILITY;
            case 3: return Missions.COMPLETE_MISSION;
        }
        return null;
    }

    public int getMinionsKilledCounter() {
        return gameData.minionsKilledCounter;
    }

    public void addMinionsKilledCounter(int minionsKilled) {
        gameData.minionsKilledCounter += minionsKilled;
    }

    public void resetMinionsKilledCounter(){
        gameData.minionsKilledCounter = 0;
    }

    public int getLevelUpCounter() {
        return gameData.levelUpCounter;
    }
    
    public void addLevelUpCounter() {
        gameData.levelUpCounter ++;
    }

    public void resetLevelUpCounter(){
        gameData.levelUpCounter = 0;
    }

    public int getLevelUpAbilityCounter() {
        return gameData.levelUpAbilityCounter;
    }

    public void addLevelUpAbilityCounter() {
        gameData.levelUpAbilityCounter ++;
    }

    public void resetLevelUpAbilityCounter(){
        gameData.levelUpAbilityCounter = 0;
    }

    public int getCompleteMissionCounter() {
        return gameData.completeMissionCounter;
    }

    public void addCompleteMissionCounter() {
        gameData.completeMissionCounter+=1;
    }

    public void resetCompleteMissionCounter(){
        gameData.completeMissionCounter = 0;
    }

    public String getMissionName(Missions missions){
        switch (missions){
            case DESTROY: return "Kill minions";
            case LEVEL_UP: return "Level up";
            case LEVEL_UP_ABILITY: return "Level up abilities";
            case COMPLETE_MISSION: return "Complete missions";

            default: return "Unknown mission";
        }
    }

    public int getCountedByMission(Missions missions){
        switch (missions){
            case DESTROY: return gameData.minionsKilledCounter;
            case LEVEL_UP: return gameData.levelUpCounter;
            case LEVEL_UP_ABILITY: return gameData.levelUpAbilityCounter;
            case COMPLETE_MISSION: return gameData.completeMissionCounter;

            default: return -1;
        }
    }

    public int getNeededByMission(Missions missions){
        switch (missions){
            case DESTROY: return NEEDED_MINION_KILLED_MISSION;
            case LEVEL_UP: return NEEDED_LEVEL_UP_MISSION;
            case LEVEL_UP_ABILITY: return NEEDED_LEVEL_UP_ABILITY_MISSION;
            case COMPLETE_MISSION: return NEEDED_COMPLETE_MISSION;

            default: return -1;
        }
    }

    public void resetCounterByMission(Missions missions){
        switch (missions){
            case DESTROY: resetMinionsKilledCounter();break;
            case LEVEL_UP: resetLevelUpCounter();break;
            case LEVEL_UP_ABILITY: resetLevelUpAbilityCounter();break;
            case COMPLETE_MISSION: resetCompleteMissionCounter();break;
        }
    }

    public int getMissionReward(Missions missions){
        switch (missions){
            case DESTROY: return 50;
            case LEVEL_UP: return 60;
            case LEVEL_UP_ABILITY: return 140;
            case COMPLETE_MISSION: return 65;

            default: return 0;
        }
    }

    public String gameDataToString() {
        return "GameData{" +
                "level=" + gameData.level +
                ", slowLevel=" + gameData.slowLevel +
                ", timeLevel=" + gameData.timeLevel +
                ", incomeLevel=" + gameData.incomeLevel +
                ", selectedSkin=" + gameData.selectedSkin +
                ", unlockedSkins=" + Arrays.toString(gameData.unlockedSkins) +
                ", gold=" + gameData.gold +
                ", mission="+Arrays.toString(gameData.missions)+
                '}';
    }
}
