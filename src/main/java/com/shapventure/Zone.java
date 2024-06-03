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
        try{
            Thread.sleep(500);
        }
        catch (Exception ignored) {
        }
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

    /*返回一条字符串，依次连接itemABC的message
     * 中间以换行符\n相隔开，结尾没有
     */
    public String zoneMessage() {
        String str = "";
        switch (zoneType) {
            case empty:
                str = "空地";
                break;
            case enemy:
                str = "敌人！\n生命值：" + health + "\n攻击力：" + attack;
                break;
            case shop:
                str = "商店！\n";
                str = str + "A. " + itemA.message(true) + "\n";
                str = str + "B. " + itemB.message(true) + "\n";
                str = str + "C. " + itemC.message(true);
                break;
            case ability:
                str = "宝箱！\n";
                str = str + "A. " + itemA.message(false) + "\n";
                str = str + "B. " + itemB.message(false) + "\n";
                str = str + "C. " + itemC.message(false);
                break;
            case coins:
                str = (health == 1)?"金币！\n":"经验！\n";
                str = str + "数量：" + attack;
                break;
            case endOfLevel:
                str = "回到基地！\n";
                break;
            default:
                break;
        }
        return str;
    }

    private void fight() {
        Random bonus = new Random();
        int takeDamage = 0, dealDamage = 0;

        while (health > 0 && geti("health") > 0) {

            //try{
            //    Thread.sleep(100);
            //} catch (Exception ignored) {
            //}
            //;

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
        if(itemA != null)
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
    empty,enemy,shop,ability,coins,endOfLevel;
    public String toString() {
        return switch (this) {
            case empty -> "空地";
            case enemy -> "敌人";
            case shop -> "商店";
            case ability -> "宝箱";
            case coins -> "金币";
            case endOfLevel -> "基地";
            default -> "错误";
        };
    }
}
