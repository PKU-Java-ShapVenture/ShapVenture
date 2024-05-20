package com.shapventure;

import com.almasb.fxgl.dsl.FXGL;

import java.util.Map;
import static com.almasb.fxgl.dsl.FXGL.*;

class ShapeVentureCtrl {

    public static void handleButtonPress1() {
        // ip for Integer Property, sp for String Property, bp for Boolean Property
        // if no p, then directly get its value
        int currentHealth = geti("health");
        int currentAttack = geti("attack");
        getip("health").set(currentHealth - 10);
        getip("attack").set(currentAttack + 5);
        getsp("message").setValue("You pressed Button 1!");
        if(getb("levelFinished")){
            //Handle level finished here
            saveGame();
        }
        else{
            //Handle level proceeding here
            getbp("levelFinished").setValue(true);
        }

    }

    public static void handleButtonPress2() {
        int currentHealth = geti("health");
        int currentAttack = geti("attack");
        getip("health").set(currentHealth + 10);
        getip("attack").set(currentAttack - 5);
        getsp("message").setValue("You pressed Button 2!");
        if(getbp("levelFinished").get()){
            //Handle level finished here
            getbp("levelFinished").setValue(false);
        }
        else{
            //Handle level proceeding here
            getbp("levelFinished").setValue(true);
        }
    }

    public static void handleButtonPress3() {
        int currentHealth = geti("health");
        int currentAttack = geti("attack");
        getip("health").set(currentHealth - 5);
        getip("attack").set(currentAttack + 10);
        getsp("message").setValue("You pressed Button 3!");
        if(getbp("levelFinished").get()){
            //Handle level finished here
            loadGame();
        }
        else{
            //Handle level proceeding here
            getbp("levelFinished").setValue(false);
        }
    }
    private static void saveGame() {
        // TODO: save game to file
        getsp("message").set("Game saved successfully!");
    }

    private static void loadGame() {
        // TODO: load game from file
        getsp("message").set("Game loaded successfully!");
    }
}