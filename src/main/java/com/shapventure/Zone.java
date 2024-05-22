package com.shapventure;

import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.Random;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

public class Zone {
    
    //ShopItem内部类给出商店物品的种类、数量、价格参数
    //天赋格除zoneType不同外，沿用商店的其他方法
    public class ShopItem {
        protected int itemType, number, cost;

        ShopItem() {
            itemType = 0;
            number = 0;
            cost = 0;
        }

        ShopItem(int _itemType, int _number, int _cost) {
            itemType = _itemType;
            number = _number;
            cost = _cost;
        }

        public void set(int _itemType, int _number, int _cost) {
            itemType = _itemType;
            number = _number;
            cost = _cost;
        }

        public void work() {

        }
        
        public void purchase() {
            getip("money").set(geti("money") - cost);
            work();
        }
    }

    protected Type zoneType;
    protected int health, attack;
    protected Entry entry1, entry2;
    protected ShopItem itemA = new ShopItem(), itemB = new ShopItem(), itemC = new ShopItem();

    Zone() {

    };

    public void set(Type _zoneType,
            int _health,int _attack,
            Entry _entry1,Entry _entry2,
            ShopItem _itemA, ShopItem _itemB, ShopItem _itemC) {
        zoneType = _zoneType;
        health = _health;
        attack = _attack;
        entry1 = _entry1;
        entry2 = _entry2;
        itemA = _itemA;
        itemB = _itemB;
        itemC = _itemC;
    }
    
    public void interact() {
        switch (zoneType) {
            case empty:
                break;

            case enemy:
                int score = health * attack;
                fight();
                if (health <= 0)
                    getip("score").set(score);
                break;

            case shop:
                shopping();
                break;

            case ability:
                shopping();
                break;

            case endOfLevel:
                levelBonus();
                break;

            case coins:
                pickup();
                break;

            default:
                break;
        }
        return;
    }

    private void fight() {
        Random bonus = new Random();
        int takeDamage = 0, dealDamage = 0;

        while (health >= 0 && geti("health") >= 0) {

            try{
                Thread.sleep(100);
            } catch (Exception e) {
            }
            ;

            //玩家受到的伤害
            takeDamage = attack - geti("armor");
            getip("shield").set(geti("shield") - takeDamage);
            if (geti("shield") < 0) {
                takeDamage = 0 - geti("shield");
                getip("shield").set(0);
                getip("health").set(geti("health") - takeDamage);
            }

            //玩家造成的伤害
            dealDamage = geti("attack");
            if ((geti("bonusdamagerate") % 100) < bonus.nextInt(100))
                dealDamage *= geti("bonusdamagerate") / 100 + 1;
            else
                dealDamage *= geti("bonusdamagerate") / 100;
            health -= dealDamage;
        }

        return;
    }

    private void shopping() {
        // 下方按钮区域
        HBox bottomPane = new HBox();
        bottomPane.setPrefSize(800, 200);

        Button button1 = new Button("按钮1");
        button1.setFont(new Font(20));
        button1.setPrefSize(200, 100);
        button1.textProperty().bind(Bindings.when(getbp("levelFinished")).then("购买A").otherwise("购买A"));
        button1.setOnAction(e -> ShapeVentureCtrl.handleButtonPress1());

        Button button2 = new Button("按钮2");
        button2.setFont(new Font(20));
        button2.setPrefSize(200, 100);
        button2.textProperty().bind(Bindings.when(getbp("levelFinished")).then("购买B").otherwise("购买B"));
        button2.setOnAction(e -> ShapeVentureCtrl.handleButtonPress2());

        Button button3 = new Button("按钮3");
        button3.setFont(new Font(20));
        button3.setPrefSize(200, 100);
        button3.textProperty().bind(Bindings.when(getbp("levelFinished")).then("购买C").otherwise("购买C"));
        button3.setOnAction(e -> ShapeVentureCtrl.handleButtonPress3());

        // 用于均匀分布按钮的间隔
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        Region spacer3 = new Region();
        Region spacer4 = new Region();
        bottomPane.getChildren().add(0, spacer1);
        bottomPane.getChildren().add(2, spacer2);
        bottomPane.getChildren().add(4, spacer3);
        bottomPane.getChildren().add(6, spacer4);
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        HBox.setHgrow(spacer4, Priority.ALWAYS);

    }
    
    private void levelBonus() {
        itemA.work();
    }

    private void pickup() {
        getip("money").set(geti("money") + itemA.number);
        return;
    }
}

//Entry包含敌人的各种词条
enum Entry {
    a, b, c
}

//Type包含各种可能的地格类型
enum Type {
    empty,enemy,shop,ability,coins,endOfLevel
}
