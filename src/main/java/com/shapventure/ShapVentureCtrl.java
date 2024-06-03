package com.shapventure;

import com.almasb.fxgl.dsl.FXGL;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static com.almasb.fxgl.dsl.FXGL.*;

class ShapeVentureCtrl {


    public static void handleButtonPress1() {
        // ip for Integer Property, sp for String Property, bp for Boolean Property
        // if no p, then directly get its value
        if(getb("levelFinished")){
            //Handle level finished here
            saveGame();
        }
        else{
            //Handle level proceeding here
            getbp("pressedA").set(true);
        }

    }

    public static void handleButtonPress2() {
        if(getbp("levelFinished").get()){
            //Handle level finished here
            getip("blockNum").set(1);
            getbp("levelFinished").set(false);
            getbp("wait").set(false);
        }
        else{
            //Handle level proceeding here
            //buy item
            getbp("pressedB").set(true);
        }
    }

    public static void handleButtonPress3() {
        if(getbp("levelFinished").get()){
            //Handle level finished here
            loadGame();
        }
        else{
            //Handle level proceeding here
            getbp("pressedC").set(true);
        }
    }
    static void saveGame() {
        getSaveLoadService().saveAndWriteTask("save.sav").run();
        getsp("message").set(gets("message")+"\nGame saved successfully!");
    }

    static void loadGame() {
        getSaveLoadService().readAndLoadTask("save.sav").run();
        getbp("levelFinished").set(true);
        getip("blockNum").set(4);
        getbp("wait").set(false);
        getbp("proceed").set(false);
        getbp("pressedA").set(false);
        getbp("pressedB").set(false);
        getbp("pressedC").set(false);
        getbp("gameOver").set(false);
        getsp("message").set("Game loaded successfully!\n"+gets("message"));
    }


}