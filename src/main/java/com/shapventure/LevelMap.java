package com.shapventure;

import static com.almasb.fxgl.dsl.FXGL.geti;

import java.util.Random;

public class LevelMap {
    Random aRandom=new Random();
    Zone zoneA=new Zone(),zoneB=new Zone(),zoneC=new Zone(),zoneLast=new Zone();
    /*
     * 生成商店区间的函数，将类型生成和数值生成单独取出
     */
    private int randomShopType(int a0,int a1,int a2,int a3,int a4,int a5,int a6)
    {
        int totalnum=a0+a1+a2+a3+a4+a5+a6;
        int randomNum=aRandom.nextInt(totalnum);
        if(randomNum<a0)
            return 0;
        randomNum-=a0;
        if(randomNum<a1)
            return 1;
        randomNum-=a1;
        if(randomNum<a2)
            return 2;
        randomNum-=a2;
        if(randomNum<a3)
            return 3;
        randomNum-=a3;
        if(randomNum<a4)
            return 4;
        randomNum-=a4;
        if(randomNum<a5)
            return 5;
        return 6;
    }
    private ShopItem randomCost(int type,int recentcoin)
    {
        int randomNumber=0;
        switch(type)
        {
            case 0: case 5:
                randomNumber=recentcoin/20;
                recentcoin=randomNumber*20;
                break;
            case 1:
                randomNumber=recentcoin/4;
                recentcoin=randomNumber*4;
                break;
            case 2:
                randomNumber=recentcoin/2;
                if(randomNumber>geti("maxhealth")-geti("health"))
                randomNumber=geti("maxhealth")-geti("health");
                recentcoin=randomNumber*2;
                break;
            case 3:
                randomNumber=recentcoin/5;
                recentcoin=randomNumber*5-1;
                break;
            case 4:
                randomNumber=recentcoin/25;
                recentcoin=randomNumber*25;
                break;
            case 6:
                randomNumber=recentcoin/50;
                recentcoin=randomNumber*50;
                break;
        }
        ShopItem back=new ShopItem(type, randomNumber, recentcoin);
        return back;
    }
    private Zone randomShop(int recentcoin)
    {
        Zone newzone=new Zone();
        int chance[]={100,100,geti("health")*200/geti("maxhealth"),100,100,100,80};
        int type1=randomShopType(chance[0],chance[1],chance[2],chance[3],chance[4],chance[5],chance[6]);
        chance[type1]=chance[type1]/11;
        int type2=randomShopType(chance[0],chance[1],chance[2],chance[3],chance[4],chance[5],chance[6]);
        chance[type2]=chance[type2]/11;
        int type3=randomShopType(chance[0],chance[1],chance[2],chance[3],chance[4],chance[5],chance[6]);
        ShopItem item1=randomCost(type1,recentcoin/4),item2=randomCost(type2, recentcoin/2),item3=randomCost(type3, recentcoin);
        newzone.set(Type.shop, 0, 0, Entry.none, Entry.none, item1, item2, item3);
        return newzone;
    }
    /*
     * Boss的数值生成需要仔细考虑，其他的敌人生成应该随层数增强，线性+指数增强即可
     * A词条决定攻击与血量基础值，B词条不做影响
     */
    private Zone randomEnemy(int recentLevel,int zonePlace)
    {
        Zone newzone=new Zone();
        int enemyhealth=0,enemyattack=0;
        Entry tagA=Entry.a,tagB=Entry.b;
        switch (recentLevel) {
            case 33:
                tagA=Entry.boss;
                tagB=Entry.boss;
                enemyhealth=400;
                enemyattack=30;
                break;
            case 66:
                tagA=Entry.boss;
                tagB=Entry.boss;
                enemyhealth=700;
                enemyattack=60;
                break;
            case 99:
                tagA=Entry.boss;
                tagB=Entry.boss;
                enemyhealth=100;
                enemyattack=80;
                break;
            default:
                double leveldouble=Double.valueOf(recentLevel);
                double levelrate=Math.pow(1.01, leveldouble);
                //a为平衡型，b为进攻型，c为防御型
                switch(aRandom.nextInt(3))
                {
                    case 0:
                        tagA=Entry.a;
                        enemyhealth=(int)(100.0*levelrate);
                        enemyattack=3+recentLevel/2;
                    case 1:
                        tagA=Entry.b;
                        enemyhealth=(int)(50.0*levelrate);
                        enemyattack=5+recentLevel*5/6;
                    case 2:
                        tagA=Entry.c;
                        enemyhealth=(int)(200.0*levelrate);
                        enemyattack=1+recentLevel/4;
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
        newzone.set(Type.enemy, enemyhealth, enemyattack, tagA, tagB, null, null, null);
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
                        newZone=randomShop(recentcoin);
                        break;
                    case 2:
                        zoneType=Type.enemy;
                        newZone=randomEnemy(recentLevel,zonePlace);
                        break;
                    case 3:
                        zoneType=Type.ability;

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
