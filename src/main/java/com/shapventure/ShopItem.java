package com.shapventure;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ShopItem {
    /*ShopItem内部类给出商店物品的种类、数量、价格参数
     * 天赋格除zoneType不同外，沿用商店的其他方法
     * itemType: 0:attack 1:maxhealth 2:health(这里只能回复失去的生命值) 3:maxshield 4:recovery 5:bonusdamagerate 6:armor
     */
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
            switch (itemType) {
                case 0:
                    getip("attack").set(geti("attack") + number);
                    break;
                case 1:
                    getip("maxhealth").set(geti("maxhealth") + number);
                    getip("health").set(Math.min(geti("maxhealth"), geti("health") + number));
                    break;
                case 2:
                    getip("health").set(Math.min(geti("maxhealth"), geti("health") + number));
                    break;
                case 3:
                    getip("maxshield").set(geti("maxshield") + number);
                    getip("shield").set(Math.min(geti("maxshield"), geti("shield") + number));
                    break;
                case 4:
                    getip("recovery").set(geti("recovery") + number);
                    break;
                case 5:
                    getip("bonusdamagerate").set(geti("bonusdamagerate") + number);
                    break;
                case 6:
                    getip("armor").set(geti("armor") + number);
                    break;

                default:
                    break;
            }
        }
        
        public void purchase() {
            getip("money").set(geti("money") - cost);
            work();
        }
    }
