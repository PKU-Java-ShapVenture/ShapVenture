package com.shapventure;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.Map;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ShapVentureApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ShapVenture");
    }
    /* 
    初始化游戏变量，可以修改
    如果需要加入局外的成长，可以改里面的常值为一个表达式
    */
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("health", 100);//血量只能在特殊的区域进行回复
        vars.put("maxhealth", 100);//恢复生命不得超过当前的生命上限
        vars.put("shield", 30);//护盾会优先于血量收到伤害，每次击杀敌人后恢复recovery的值
        vars.put("maxshield", 30);//不得超过最大护盾
        vars.put("recovery", 10);//恢复值
        vars.put("attack", 10);
        vars.put("bonusdamagerate", 5);//按照百分数计数，除100后为直接增伤比例，模100后为额外增伤概率。例如350即伤害首先为原来的4倍，有50%概率再额外增加一倍
        vars.put("armor", 1);//受到攻击后直接的伤害减少
        vars.put("money", 0);//每局内的货币，可以购买升级
        vars.put("exp", 0);//每局之外的货币，可以要可以不要
        vars.put("level", 1);
        vars.put("score", 0);//就按照击败之后增加怪物血量*怪物伤害的值的算法？
        vars.put("levelFinished", true);
        vars.put("message", "Welcome to ShapVenture!");
    }
    @Override
    protected void initUI() {
        // 上方区域
        BorderPane topPane = new BorderPane();

        // 左侧文字区
        VBox leftTextArea = new VBox();
        leftTextArea.setPrefSize(200, 400);
        Label healthLabel = new Label();
        healthLabel.setFont(new Font(20));
        //这里我想按照 生命值/最大生命值：health/maxhealth 的写法来显示生命值
        healthLabel.textProperty().bind(Bindings.concat("生命值：")
                .concat(getip("health").asString())
                .concat("/")
                .concat(getip("maxhealth").asString()));

        Label attackLabel = new Label();
        attackLabel.setFont(new Font(20));
        attackLabel.textProperty().bind(getip("attack").asString("攻击力: %d"));
        Label levelLabel = new Label();
        levelLabel.setFont(new Font(20));
        levelLabel.textProperty().bind(getip("level").asString("层数: %d/100"));//一共100层
        Label scoreLabel = new Label();
        scoreLabel.setFont(new Font(20));
        scoreLabel.textProperty().bind(getip("score").asString("分数: %d"));
        leftTextArea.getChildren().addAll(healthLabel, attackLabel, levelLabel, scoreLabel);
        topPane.setLeft(leftTextArea);

        // 中间显示图形区域
        VBox centerArea = new VBox();
        centerArea.setPrefSize(400, 400);
        Label centerText = new Label("图形区域");
        centerArea.getChildren().add(centerText);
        topPane.setCenter(centerArea);

        // 右侧文字区
        VBox rightTextArea = new VBox();
        rightTextArea.setPrefSize(200, 400);
        Label rightText = new Label();
        rightText.setFont(new Font(20));
        rightText.setWrapText(true);
        rightText.textProperty().bind(getsp("message"));
        rightTextArea.getChildren().add(rightText);
        topPane.setRight(rightTextArea);

        // 下方按钮区域
        HBox bottomPane = new HBox();
        bottomPane.setPrefSize(800, 200);

        Button button1 = new Button("按钮1");
        button1.setFont(new Font(20));
        button1.setPrefSize(200, 100);
        button1.textProperty().bind(Bindings.when(getbp("levelFinished")).then("存档").otherwise("A"));
        button1.setOnAction(e -> ShapeVentureCtrl.handleButtonPress1());

        Button button2 = new Button("按钮2");
        button2.setFont(new Font(20));
        button2.setPrefSize(200, 100);
        button2.textProperty().bind(Bindings.when(getbp("levelFinished")).then("下一层").otherwise("B"));
        button2.setOnAction(e -> ShapeVentureCtrl.handleButtonPress2());

        Button button3 = new Button("按钮3");
        button3.setFont(new Font(20));
        button3.setPrefSize(200, 100);
        button3.textProperty().bind(Bindings.when(getbp("levelFinished")).then("读档").otherwise("C"));
        button3.setOnAction(e -> ShapeVentureCtrl.handleButtonPress3());


        bottomPane.getChildren().addAll(button1, button2, button3);
        HBox.setHgrow(button1, Priority.ALWAYS);
        HBox.setHgrow(button2, Priority.ALWAYS);
        HBox.setHgrow(button3, Priority.ALWAYS);

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

        // 主布局
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(topPane);
        mainPane.setBottom(bottomPane);

        // 设置根节点
        FXGL.getGameScene().addUINode(mainPane);

        //init save & load service
        getSaveLoadService().addHandler(new SaveLoadHandler() {
            @Override
            public void onSave(DataFile data) {
                var bundle = new Bundle("gameData");
                int health = geti("health");
                int maxhealth = geti("maxhealth");
                int shield = geti("shield");
                int maxshield = geti("maxshield");
                int recovery = geti("recovery");
                int attack = geti("attack");
                int bonusdamagerate = geti("bonusdamagerate");
                int armor = geti("armor");
                int money = geti("money");
                int exp = geti("exp");
                int level = geti("level");
                int score = geti("score");
                boolean levelFinished = getb("levelFinished");
                String message = gets("message");
                bundle.put("health", health);
                bundle.put("maxhealth", maxhealth);
                bundle.put("shield", shield);
                bundle.put("maxshield", maxshield);
                bundle.put("recovery", recovery);
                bundle.put("attack", attack);
                bundle.put("bonusdamagerate", bonusdamagerate);
                bundle.put("armor", armor);
                bundle.put("money", money);
                bundle.put("exp", exp);
                bundle.put("level", level);
                bundle.put("score", score);
                bundle.put("levelFinished", levelFinished);
                bundle.put("message", message);
                data.putBundle(bundle);
            }

            @Override
            public void onLoad(DataFile data) {
                var bundle = data.getBundle("gameData");
                int health = bundle.get("health");
                int maxhealth = bundle.get("maxhealth");
                int shield = bundle.get("shield");
                int maxshield = bundle.get("maxshield");
                int recovery = bundle.get("recovery");
                int attack = bundle.get("attack");
                int bonusdamagerate = bundle.get("bonusdamagerate");
                int armor = bundle.get("armor");
                int money = bundle.get("money");
                int exp = bundle.get("exp");
                int level = bundle.get("level");
                int score = bundle.get("score");
                boolean levelFinished = bundle.get("levelFinished");
                String message = bundle.get("message");
                set("health", health);
                set("maxhealth", maxhealth);
                set("shield", shield);
                set("maxshield", maxshield);
                set("recovery", recovery);
                set("attack", attack);
                set("bonusdamagerate", bonusdamagerate);
                set("armor", armor);
                set("money", money);
                set("exp", exp);
                set("level", level);
                set("score", score);
                set("levelFinished", levelFinished);
                set("message", message);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
