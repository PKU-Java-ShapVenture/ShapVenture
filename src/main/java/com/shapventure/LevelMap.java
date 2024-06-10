package com.shapventure;
import static com.almasb.fxgl.dsl.FXGL.geti;

import java.util.Random;

public class LevelMap {
    private Random aRandom=new Random();
    protected Zone zoneA=new Zone(),zoneB=new Zone(),zoneC=new Zone(),zoneLast=new Zone();
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
                recentcoin=Math.max(randomNumber*5-1, 0);
                break;
            case 4:
                randomNumber=recentcoin/25;
                recentcoin=randomNumber*25;
                break;
            case 6:
                randomNumber=recentcoin/75;
                recentcoin=randomNumber*75;
                break;
        }
        ShopItem back=new ShopItem(type, randomNumber, recentcoin);
        return back;
    }

    private Zone randomShop(int recentcoin)
    {
        Zone newzone=new Zone();
        int chance[]={100,100,(geti("maxhealth")-geti("health"))*200/geti("maxhealth"),100,100,100,50};
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
                enemyhealth=1000;
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
                        break;
                    case 1:
                        tagA=Entry.b;
                        enemyhealth=(int)(50.0*levelrate);
                        enemyattack=5+recentLevel*5/6;
                        break;
                    case 2:
                        tagA=Entry.c;
                        enemyhealth=(int)(200.0*levelrate);
                        enemyattack=1+recentLevel/4;
                        break;
                }
                switch(aRandom.nextInt(3))
                {
                    case 0:
                        tagB=Entry.a;
                        break;
                    case 1:
                        tagB=Entry.b;
                        break;
                    case 2:
                        tagB=Entry.c;
                        break;
                }
                break;
        }
        newzone.set(Type.enemy, enemyhealth, enemyattack, tagA, tagB, null, null, null);
        return newzone;
    }

    private Zone randomAbility()
    {
        Zone newZone=new Zone();
        int shoptype1=randomShopType(100, 100, 0, 100, 100, 100, 100);
        int shoptype2=randomShopType(100, 100, 0, 100, 100, 100, 100);
        int shoptype3=randomShopType(100, 100, 0, 100, 100, 100, 100);
        int randomNumber1=aRandom.nextInt(200)+100,randomNumber2=aRandom.nextInt(200)+100,randomNumber3=aRandom.nextInt(200)+100;
        int weight[]={20,4,1000,5,25,20,75};
        ShopItem aShopItem=new ShopItem(shoptype1, randomNumber1/weight[shoptype1], 0);
        ShopItem bShopItem=new ShopItem(shoptype2, randomNumber2/weight[shoptype2], 0);
        ShopItem cShopItem=new ShopItem(shoptype3, randomNumber3/weight[shoptype3], 0);
        newZone.set(Type.ability, 0, 0, Entry.none, Entry.none, aShopItem, bShopItem, cShopItem);
        return newZone;
    }
    /*
     * coinsType决定生成经验还是金币：0为随机，1为经验，2为金币
     * 随机时90%概率为金币
     */
    private Zone randomCoins(int coinsType)
    {
        Zone newZone=new Zone();
        if(coinsType==0)
        {
            if(aRandom.nextInt(10)==0)
            coinsType=1;
            else coinsType=2;
        }
        
        if(coinsType==1)
        newZone.set(Type.coins, 1, 10, Entry.every, Entry.every, null, null, null);
        else newZone.set(Type.coins, 2, 20, Entry.every, Entry.every, null, null, null);

        return newZone;
    }

    
    /*
     * 传入当前层数（作为生成质量）、生成的区域处于的位置、
     * 当前的货币数量（在生成商店的时候可以决定生成的商品价值）
     * 33层、66层、99层作为固定的boss层，100层作为最终奖励层
     * boss层按照shop+enemy+ability的固定格式生成
     * zoneChance[]中：0为empty生成概率、1为enemy生成概率、2为shop生成概率、3为ability生成概率、4为coins生成概率
     */
    private Zone randomZone(int recentLevel,int zonePlace,int recentcoin)
    {
        Zone newZone=new Zone();
        int zoneChance[]={0,0,0,0,0};
        switch (recentLevel) {
            case 33: case 66: case 99:
                switch (zonePlace) {
                    case 1:
                        newZone=randomShop(recentcoin);
                        break;
                    case 2:
                        newZone=randomEnemy(recentLevel,zonePlace);
                        break;
                    case 3:
                        newZone=randomAbility();
                        break;
                    }
                break;
            case 100:
                newZone=randomCoins(1);
                break;
            default:
                switch(zonePlace){
                    case 1:
                        int chance1[]={0,100,0,0,10};
                        zoneChance=chance1;
                        break;
                    case 2:
                        int chance2[]={5,100,15,10,5};
                        zoneChance=chance2;
                        break;
                    case 3:
                        int chance3[]={5,100,10,15,5};
                        zoneChance=chance3;
                        break;
                }
                newZone = normalRandomZone(zoneChance, recentLevel, recentcoin,zonePlace);
                break;
        }
        return newZone;
    }
    private Zone normalRandomZone(int chance[],int recentLevel,int recentcoin,int zonePlace)
    {
        Zone newZone=new Zone();
        int totalChance=chance[0]+chance[1]+chance[2]+chance[3]+chance[4];
        int randomNum=aRandom.nextInt(totalChance);
        if(randomNum<chance[0])
        {
            newZone.set(Type.empty, 0, 0, Entry.every, Entry.every, null, null, null);
            return newZone;
        }
        randomNum-=chance[0];
        if(randomNum<chance[1])
            return randomEnemy(recentLevel, zonePlace);
        randomNum-=chance[1];
        if(randomNum<chance[2])
            return randomShop(recentcoin);
        randomNum-=chance[2];
        if(randomNum<chance[3])
            return randomAbility();
        return randomCoins(0);
    }
    /*
     * 奖励层获得3份经验？
     * A、B、C分别生成
     */
    private int taghash(Entry a)
    {
        switch (a) {
            case every:
                return 0;
            case none:
                return 1;
            case a:
                return 31;
            case b:
                return 43;
            case c:
                return 47;
            default:
                return 419;
        }
    }
    /*
     * 这里every表示全为通配符，a表示全为a词条，none表示无搭配
     */
    private Entry countEntry(int entrytag)
    {
        if(entrytag==0)
        return Entry.every;
        if(entrytag%31==0)
        return Entry.a;
        if(entrytag%43==0)
        return Entry.b;
        if(entrytag%47==0)
        return Entry.c;
        return Entry.none;
    }
    /*
     * 如果生成的金币词条该做什么判断？
     * 初步想法是在最后为金币词条的时候attack数值会传为1，health数值为奖励金币值
     * 非金币词条则还是返回到Zone的ItemA里面，attack数值为0
     * 无奖励会让attack值为2，防止不合法调用
     * 
     * 这里需要考虑一下不同词条搭配的奖励是什么，每一行的词条为第一个词条（决定了怪物的类型）
     *                   通配符        A词条        B词条         C词条         无搭配
     * 通配符            护甲+4        不存在       不存在        不存在         不存在
     * A词条             不存在        恢复+10      恢复+10       护甲+2        金币+25
     * B词条             不存在        攻击力+8     奖励攻击率+10  攻击力+8      金币+25
     * C词条             不存在        护盾+75      生命+75       生命+75       金币+25
     * 无搭配            不存在        金币+20      金币+20       金币+20        无奖励
     */
    public void randommap()
    {
        int currentLevel=geti("level");
        int currentCoin=geti("money");
        zoneA=randomZone(currentLevel, 1, currentCoin);
        zoneB=randomZone(currentLevel, 2, currentCoin);
        zoneC=randomZone(currentLevel, 3, currentCoin);
        int entrya=taghash(zoneA.entry1)+taghash(zoneB.entry1)+taghash(zoneC.entry1);
        int entryb=taghash(zoneA.entry2)+taghash(zoneB.entry2)+taghash(zoneC.entry2);
        Entry xa=countEntry(entrya),xb=countEntry(entryb);
        ShopItem zoneLastItem;
        int ifcoins=0,coinsnum=0;
        switch (xa) {
            case every:
                zoneLastItem=new ShopItem(6, 4, 0);
                break;
            case a:
                switch (xb) {
                    case a: case b:
                        zoneLastItem=new ShopItem(4, 10, 0);
                        break;
                    case c:
                        zoneLastItem=new ShopItem(6, 2, 0);
                        break;
                    default:
                        zoneLastItem=null;
                        ifcoins=1;
                        coinsnum=25;
                        break;
                }
                break;
            case b:
                switch (xb) {
                    case a: case c:
                        zoneLastItem=new ShopItem(0, 8, 0);
                        break;
                    case b:
                        zoneLastItem=new ShopItem(5, 10, 0);
                        break;
                    default:
                        zoneLastItem=null;
                        ifcoins=1;
                        coinsnum=25;
                        break;
                }
                break;
            case c:
                switch (xb) {
                    case a:
                        zoneLastItem=new ShopItem(3, 75, 0);
                        break;
                    case b: case c:
                        zoneLastItem=new ShopItem(1, 75, 0);
                        break;
                    default:
                        zoneLastItem=null;
                        ifcoins=1;
                        coinsnum=25;    
                        break;
                }
                break;
            default:
                zoneLastItem=null;
                if(xb==Entry.none)
                    ifcoins=2;
                else
                {
                    ifcoins=1;
                    coinsnum=20;
                }
                break;
        }
        zoneLast.set(Type.endOfLevel, coinsnum, ifcoins, Entry.none, Entry.none, zoneLastItem, null,null);
    }

    public Zone getZone(int zoneNum){
        switch(zoneNum){
            case 1:
                return zoneA;
            case 2:
                return zoneB;
            case 3:
                return zoneC;
            case 4:
                return zoneLast;
            default:
                return null;
        }
    }
}
