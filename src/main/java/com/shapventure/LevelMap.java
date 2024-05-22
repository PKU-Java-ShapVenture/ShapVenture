package com.shapventure;

import static com.almasb.fxgl.dsl.FXGL.geti;
import static com.almasb.fxgl.dsl.FXGL.random;

import java.util.Random;

public class LevelMap {
    Random aRandom=new Random();
    Zone zoneA=new Zone(),zoneB=new Zone(),zoneC=new Zone(),zoneLast=new Zone();
    private Zone randomShop(int recentcoin)
    {
        Zone newzone=new Zone();
        return newzone;
    }
    /*
     * Boss的数值生成需要仔细考虑，其他的敌人生成应该随层数增强，线性增强即可
     */
    private Zone randomenemy(int recentLevel,int zonePlace)
    {
        Zone newzone=new Zone();
        int enemyhealth,enemyattack;
        Entry tagA,tagB;
        switch (recentLevel) {
            case 33:
                tagA=Entry.boss;
                tagB=Entry.boss;
                break;
            case 66:
                tagA=Entry.boss;
                tagB=Entry.boss;
                break;
            case 99:
                tagA=Entry.boss;
                tagB=Entry.boss;
                break;
            default:
                switch(aRandom.nextInt(3))
                {
                    case 0:
                        tagA=Entry.a;
                    case 1:
                        tagA=Entry.b;
                    case 2:
                        tagA=Entry.c;
                }
                switch(aRandom.nextInt(3))
                {
                    case 0:
                        tagB=Entry.a;
                    case 1:
                        tagB=Entry.b;
                    case 2:
                        tagB=Entry.c;
                }
                break;
        }
        return newzone;
    }
    /*
     * 传入当前层数（作为生成质量）、生成的区域处于的位置、
     * 当前的货币数量（在生成商店的时候可以决定生成的商品价值）
     * 33层、66层、99层作为固定的boss层，100层作为最终奖励层
     * boss层按照shop+enemy+ability的固定格式生成
     */
    private Zone randomZone(int recentLevel,int zonePlace,int recentcoin)
    {
        Zone newZone=new Zone();
        Type zoneType=Type.empty;
        switch (recentLevel) {
            case 33: case 66: case 99:
                switch (zonePlace) {
                    case 1:
                        zoneType=Type.shop;
                        randomShop(recentcoin);
                        break;
                    case 2:
                        zoneType=Type.enemy;
                        randomenemy(recentLevel,zonePlace);
                        break;
                    case 3:

                        break;
                    }
                break;
            case 100:

                break;
            default:

                break;
        }
        return newZone;
    }
    /*
     * 奖励层获得3份经验？
     * A、B、C分别生成
     */
    public void randommap()
    {
        int currentLevel=geti("level");
        int currentCoin=geti("money");
        zoneA=randomZone(currentLevel, 0, currentCoin);
        zoneB=randomZone(currentLevel, 1, currentCoin);
        zoneC=randomZone(currentLevel, 2, currentCoin);
    }
}
