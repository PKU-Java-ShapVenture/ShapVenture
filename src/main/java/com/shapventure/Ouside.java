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
public class Ouside {
    final File f = new File("../../../outsideDataSave.txt");

    //请勿更改此顺序
    int maxhealthLV,
            maxshieldLV,
            recoveryLV,
            attackLV,
            bonusdamagerateLV,
            armorLV,
            moneyLV;
    int expRequired[] = new int[] { 10, 50, 100, 500, 1000 };

    public Ouside() {

    }

    public int LV2data(Options option, int LV) {
        switch (option) {
            case maxhealth:
                switch (LV) {
                    case 0:
                        return 100;
                    case 1:
                        return 150;
                    case 2:
                        return 250;
                    case 3:
                        return 500;
                    case 4:
                        return 1000;
                    case 5:
                        return 1500;

                    default:
                        return 1;
                }
            case maxshield:
                switch (LV) {
                    case 0:
                        return 30;
                    case 1:
                        return 80;
                    case 2:
                        return 150;
                    case 3:
                        return 200;
                    case 4:
                        return 300;
                    case 5:
                        return 500;

                    default:
                        return 1;
                }
            case recovery:
                switch (LV) {
                    case 0:
                        return 10;
                    case 1:
                        return 15;
                    case 2:
                        return 25;
                    case 3:
                        return 50;
                    case 4:
                        return 100;
                    case 5:
                        return 150;

                    default:
                        return 1;
                }
            case attack:
                switch (LV) {
                    case 0:
                        return 10;
                    case 1:
                        return 15;
                    case 2:
                        return 25;
                    case 3:
                        return 50;
                    case 4:
                        return 100;
                    case 5:
                        return 150;

                    default:
                        return 1;
                }
            case bonusdamagerate:
                switch (LV) {
                    case 0:
                        return 5;
                    case 1:
                        return 10;
                    case 2:
                        return 15;
                    case 3:
                        return 25;
                    case 4:
                        return 35;
                    case 5:
                        return 50;

                    default:
                        return 1;
                }
            case armor:
                switch (LV) {
                    case 0:
                        return 1;
                    case 1:
                        return 2;
                    case 2:
                        return 5;
                    case 3:
                        return 8;
                    case 4:
                        return 15;
                    case 5:
                        return 30;

                    default:
                        return 1;
                }
            case money:
                switch (LV) {
                    case 0:
                        return 0;
                    case 1:
                        return 20;
                    case 2:
                        return 50;
                    case 3:
                        return 100;
                    case 4:
                        return 180;
                    case 5:
                        return 360;

                    default:
                        return 1;
                }

            default:
                return 2;
        }
    }

    public void OusideSave() throws IOException {
        FileOutputStream fop = new FileOutputStream(f, false);
        fop.write(maxhealthLV);
        fop.write(maxshieldLV);
        fop.write(recoveryLV);
        fop.write(attackLV);
        fop.write(bonusdamagerateLV);
        fop.write(armorLV);
        fop.write(moneyLV);
        fop.close();
    }

    public void OusideRead() throws IOException {
        FileInputStream fip = new FileInputStream(f);
        int oop[] = new int[6];
        byte b[] = new byte[4];
        int n = 0, i = 0;
        while (n != -1) {
            n = fip.read(b);
            oop[i] = b[3];
            i++;
        }
        set(oop[0], oop[1], oop[2], oop[3], oop[4], oop[5], oop[6]);
        act();
        fip.close();
    }

    public void act() {
        getip("maxhealth").set(LV2data(Options.maxhealth, maxhealthLV));
        getip("maxshield").set(LV2data(Options.maxshield, maxshieldLV));
        getip("recovery").set(LV2data(Options.recovery, recoveryLV));
        getip("attack").set(LV2data(Options.attack, attackLV));
        getip("bonusdamagerate").set(LV2data(Options.bonusdamagerate, bonusdamagerateLV));
        getip("armor").set(LV2data(Options.armor, armorLV));
        getip("money").set(LV2data(Options.money, moneyLV));
    }

    private void set(int _maxhealthLV,
            int _maxshieldLV,
            int _recoveryLV,
            int _attackLV,
            int _bonusdamagerateLV,
            int _armorLV,
            int _moneyLV) {
        maxhealthLV = _maxhealthLV;
        maxshieldLV = _maxshieldLV;
        recoveryLV = _recoveryLV;
        attackLV = _attackLV;
        bonusdamagerateLV = _bonusdamagerateLV;
        armorLV = _armorLV;
        moneyLV = _moneyLV;
    }

    //当能够成功升级时返回true，否则false
    public boolean upgrade(Options option) {
        switch (option) {
            case maxhealth:
                if (geti("exp") < expRequired[maxhealthLV]
                        || maxhealthLV == 5) {
                    return false;
                } else {
                    getip("exp").set(geti("exp") - expRequired[maxhealthLV]);
                    maxhealthLV += 1;
                    break;
                }
            case maxshield:
                if (geti("exp") < expRequired[maxshieldLV]
                        || maxshieldLV == 5) {
                    return false;
                } else {
                    getip("exp").set(geti("exp") - expRequired[maxshieldLV]);
                    maxshieldLV += 1;
                    break;
                }
            case recovery:
                if (geti("exp") < expRequired[recoveryLV]
                        || recoveryLV == 5) {
                    return false;
                } else {
                    getip("exp").set(geti("exp") - expRequired[recoveryLV]);
                    recoveryLV += 1;
                    break;
                }
            case attack:
                if (geti("exp") < expRequired[attackLV]
                        || attackLV == 5) {
                    return false;
                } else {
                    getip("exp").set(geti("exp") - expRequired[attackLV]);
                    attackLV += 1;
                    break;
                }
            case bonusdamagerate:
                if (geti("exp") < expRequired[bonusdamagerateLV]
                        || bonusdamagerateLV == 5) {
                    return false;
                } else {
                    getip("exp").set(geti("exp") - expRequired[bonusdamagerateLV]);
                    bonusdamagerateLV += 1;
                    break;
                }
            case armor:
                if (geti("exp") < expRequired[armorLV]
                        || armorLV == 5) {
                    return false;
                } else {
                    getip("exp").set(geti("exp") - expRequired[armorLV]);
                    armorLV += 1;
                    break;
                }
            case money:
                if (geti("exp") < expRequired[moneyLV]
                        || moneyLV == 5) {
                    return false;
                } else {
                    getip("exp").set(geti("exp") - expRequired[moneyLV]);
                    moneyLV += 1;
                    break;
                }

            default:
                return false;
        }
        act();
        return true;
    }

    //当能够降级升级时返回true，否则false
    public boolean degrade(Options option) {
        switch (option) {
            case maxhealth:
                if (maxhealthLV == 0) {
                    return false;
                } else {
                    maxhealthLV -= 1;
                    getip("exp").set(geti("exp") + expRequired[maxhealthLV]);
                    break;
                }
            case maxshield:
                if (maxshieldLV == 0) {
                    return false;
                } else {
                    maxshieldLV -= 1;
                    getip("exp").set(geti("exp") + expRequired[maxshieldLV]);
                    break;
                }
            case recovery:
                if (recoveryLV == 0) {
                    return false;
                } else {
                    recoveryLV -= 1;
                    getip("exp").set(geti("exp") + expRequired[recoveryLV]);
                    break;
                }
            case attack:
                if (attackLV == 0) {
                    return false;
                } else {
                    attackLV -= 1;
                    getip("exp").set(geti("exp") + expRequired[attackLV]);
                    break;
                }
            case bonusdamagerate:
                if (bonusdamagerateLV == 0) {
                    return false;
                } else {
                    bonusdamagerateLV -= 1;
                    getip("exp").set(geti("exp") + expRequired[bonusdamagerateLV]);
                    break;
                }
            case armor:
                if (armorLV == 0) {
                    return false;
                } else {
                    armorLV -= 1;
                    getip("exp").set(geti("exp") + expRequired[armorLV]);
                    break;
                }
            case money:
                if (moneyLV == 0) {
                    return false;
                } else {
                    moneyLV -= 1;
                    getip("exp").set(geti("exp") + expRequired[moneyLV]);
                    break;
                }

            default:
                return false;
        }
        act();
        return true;
    }
}

enum Options {
    maxhealth,maxshield,recovery,attack,bonusdamagerate,armor,money;
}
