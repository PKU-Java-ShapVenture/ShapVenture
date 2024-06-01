package com.shapventure;

import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.Random;

public class Zone {
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
                    getip("score").set(geti("score") + score);
                getip("shield").set(Math.min(geti("maxshield"), geti("shield") + geti("recovery")));
                break;
            case endOfLevel:
                switch (attack) {
                    case 0:
                        levelBonus();
                        break;
                    case 1:
                        getip("money").set(geti("money") + health);
                    case 2:
                        break;
                    default:
                        break;
                }
                break;

            case coins:
                pickup();
                break;

            case shop:
                System.out.println("错误调用");
                break;

            case ability:
                System.out.println("错误调用");
                break;

            default:
                break;
        }
        return;
    }

    private void fight() {
        Random bonus = new Random();
        int takeDamage = 0, dealDamage = 0;

        while (health > 0 && geti("health") > 0) {

            try{
                Thread.sleep(100);
            } catch (Exception e) {
            }
            ;

            //玩家受到的伤害
            takeDamage = Math.max(1, attack - geti("armor"));
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
    
    private void levelBonus() {
        itemA.work();
    }

    private void pickup() {
        if(health==1)
            getip("money").set(geti("money") + attack);
        else
            getip("exp").set(geti("exp") + attack);
        return;
    }
}

//Entry包含敌人的各种词条，every为通配符，none为无词条配组（其他格类型可能使用），boss为boss专属词条
enum Entry {
    a, b, c, none, every, boss
}

//Type包含各种可能的地格类型
enum Type {
    empty,enemy,shop,ability,coins,endOfLevel
}
