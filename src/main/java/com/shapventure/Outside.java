package com.shapventure;

import static com.almasb.fxgl.dsl.FXGL.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * 本类包含更改局外升级的各类函数和相关变量
 * 每种数值有0-5共计六个等级，每级升入下一级所需exp数量存储在对应的expRequired数组中
 * （如最大生命由LV2升级到LV3需要expRequired[2]的exp）
 * 数值x在等级y的具体量由LV2data(x,y)给出
 * 本类在src/main文件夹中创建outsideDataSave文本文件，通过字节流以固定顺序保存各类数值局外等级，彼此不隔开
 */
public class Outside {
    final File f = new File("outsideDataSave.txt");

    //请勿更改此顺序
    private byte[] level = new byte[7];

    private int expRequired[] = new int[] { 10, 50, 100, 500, 1000 };
    private int[][] data = {
            { 100, 150, 250, 500, 1000, 1500 }, // maxhealth
            { 30, 80, 150, 200, 300, 500 }, // maxshield
            { 10, 15, 25, 50, 100, 150 }, // recovery
            { 10, 15, 25, 50, 100, 150 }, // attack
            { 5, 10, 15, 25, 35, 50 }, // bonusdamagerate
            { 1, 2, 5, 8, 15, 30 }, // armor
            { 0, 20, 50, 100, 180, 360 } // money
    };

    public Outside() {
        // 如果文件不存在，创建新文件
        if (!f.exists()) {
            try {
                f.createNewFile();
                // 初始化文件内容
                OusideSave();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            OusideRead();
        }
    }

    public int LV2data(Options option, int LV) {
        return data[option.toi()][LV];
    }

    public void OusideSave() {
        try (FileOutputStream fop = new FileOutputStream(f, false)) {
            for (int i = 0; i < 7; i++) {
                fop.write(level[i]);
            }

            int EXP = geti("exp");
            for (int i = 3; i >= 0; i--) {
                fop.write((EXP >>> (i * 8)) & 0xFF);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OusideRead() {
        try (FileInputStream fip = new FileInputStream(f)) {
            for (int i = 0; i < 7; i++) {
                level[i] = (byte) fip.read();
            }

            int EXP = 0;
            for (int i = 3; i >= 0; i--) {
                EXP |= (fip.read() & 0xFF) << (i * 8);
            }
            getip("exp").set(EXP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void act() {
        getip("maxhealth").set(LV2data(Options.maxhealth, level[0]));
        getip("health").set(LV2data(Options.maxhealth, level[0]));
        getip("maxshield").set(LV2data(Options.maxshield, level[1]));
        getip("shield").set(LV2data(Options.maxshield, level[1]));
        getip("recovery").set(LV2data(Options.recovery, level[2]));
        getip("attack").set(LV2data(Options.attack, level[3]));
        getip("bonusdamagerate").set(LV2data(Options.bonusdamagerate, level[4]));
        getip("armor").set(LV2data(Options.armor, level[5]));
        getip("money").set(LV2data(Options.money, level[6]));
    }

    //当能够成功升级时返回true，否则false
    public boolean upgrade(Options option) {
        if (level[option.toi()] == 5 || geti("exp") < expRequired[level[option.toi()]]) {
            act();
            return false;
        } else {
            getip("exp").set(geti("exp") - expRequired[level[option.toi()]]);
            level[option.toi()]++;
            act();
            return true;
        }
    }

    //当能够降级时返回true，否则false
    public boolean degrade(Options option) {
        if (level[option.toi()] == 0) {
            act();
            return false;
        } else {
            level[option.toi()]--;
            getip("exp").set(geti("exp") + expRequired[level[option.toi()]]);
            act();
            return true;
        }
    }
}


enum Options {
    maxhealth, maxshield, recovery, attack, bonusdamagerate, armor, money;

    public int toi() {
        return switch (this) {
            case maxhealth -> 0;
            case maxshield -> 1;
            case recovery -> 2;
            case attack -> 3;
            case bonusdamagerate -> 4;
            case armor -> 5;
            case money -> 6;
            default -> -1;
        };
    }
    public String toString() {
        return switch (this) {
            case maxhealth -> "最大生命值";
            case maxshield -> "最大护盾值";
            case recovery -> "恢复速度";
            case attack -> "攻击力";
            case bonusdamagerate -> "额外伤害比例";
            case armor -> "护甲";
            case money -> "金钱";
            default -> "错误";
        };
    }
}
