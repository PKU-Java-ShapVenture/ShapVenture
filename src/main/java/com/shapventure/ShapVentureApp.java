package com.shapventure;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.texture.Texture;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Map;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.shapventure.ShapeVentureCtrl.loadGame;

public class ShapVentureApp extends GameApplication {

    private Entity player;
    private Entity block1, block2, block3;
    private LevelMap levelMap = new LevelMap();
    private Stage gameOverStage;

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
        vars.put("blockNum", 4);//用于记录当前所在的方块编号, 1-3为关卡，4为基地
        vars.put("wait", false);
        vars.put("proceed", false);
        vars.put("pressedA", false);
        vars.put("pressedB", false);
        vars.put("pressedC", false);
        vars.put("gameOver", false);
        vars.put("message", "欢迎来到ShapVenture!");
    }

    @Override
    protected void initUI() {
        // 上方区域
        BorderPane topPane = new BorderPane();

        // 左侧文字区
        int leftFontSize = 18;
        VBox leftTextArea = new VBox();
        leftTextArea.setPrefSize(200, 450);
        Label healthLabel = new Label();
        healthLabel.setFont(new Font(leftFontSize));
        //这里我想按照 生命值/最大生命值：health/maxhealth 的写法来显示生命值
        healthLabel.textProperty().bind(Bindings.concat("生命值：")
                .concat(getip("health").asString())
                .concat("/")
                .concat(getip("maxhealth").asString()));
        Label shieldLabel = new Label();
        shieldLabel.setFont(new Font(leftFontSize));
        shieldLabel.textProperty().bind(Bindings.concat("护盾值：")
                .concat(getip("shield").asString())
                .concat("/")
                .concat(getip("maxshield").asString()));
        Label recoveryLabel = new Label();
        recoveryLabel.setFont(new Font(leftFontSize));
        recoveryLabel.textProperty().bind(getip("recovery").asString("恢复值: %d"));
        Label attackLabel = new Label();
        attackLabel.setFont(new Font(leftFontSize));
        attackLabel.textProperty().bind(getip("attack").asString("攻击力: %d"));
        Label bonusdamagerateLabel = new Label();
        bonusdamagerateLabel.setFont(new Font(leftFontSize));
        bonusdamagerateLabel.textProperty().bind(getip("bonusdamagerate").asString("暴击率: %d"));
        Label armorLabel = new Label();
        armorLabel.setFont(new Font(leftFontSize));
        armorLabel.textProperty().bind(getip("armor").asString("护甲值: %d"));
        Label moneyLabel = new Label();
        moneyLabel.setFont(new Font(leftFontSize));
        moneyLabel.textProperty().bind(getip("money").asString("金币: %d"));
        Label expLabel = new Label();
        expLabel.setFont(new Font(leftFontSize));
        expLabel.textProperty().bind(getip("exp").asString("经验: %d"));
        Label levelLabel = new Label();
        levelLabel.setFont(new Font(leftFontSize));
        levelLabel.textProperty().bind(getip("level").asString("层数: %d/100"));//一共100层
        Label scoreLabel = new Label();
        scoreLabel.setFont(new Font(leftFontSize));
        scoreLabel.textProperty().bind(getip("score").asString("分数: %d"));
        leftTextArea.getChildren().addAll(healthLabel, shieldLabel, recoveryLabel, attackLabel, bonusdamagerateLabel, armorLabel, moneyLabel, expLabel, levelLabel, scoreLabel);
        topPane.setLeft(leftTextArea);

        // 中间显示图形区域
        VBox centerArea = new VBox();
        centerArea.setPrefSize(300, 450);
        drawCentreAreaRect(centerArea);
        topPane.setCenter(centerArea);

        // 右侧文字区
        VBox rightTextArea = new VBox();
        rightTextArea.setPrefSize(300, 450);
        Label rightText = new Label();
        rightText.setFont(new Font(20));
        rightText.setWrapText(true);
        rightText.textProperty().bind(getsp("message"));
        rightTextArea.getChildren().add(rightText);
        topPane.setRight(rightTextArea);

        // 下方按钮区域
        HBox bottomPane = new HBox();
        bottomPane.setPrefSize(800, 150);

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

        // 贴图
        Texture baseTexture = FXGL.getAssetLoader().loadTexture("home.png");
        baseTexture.setFitWidth(80);
        baseTexture.setFitHeight(80);
        baseTexture.setTranslateX(250);
        baseTexture.setTranslateY(320);
        FXGL.getGameScene().addUINode(baseTexture);
    }

    private void initSaveLoadService() {
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
                set("message", message);
            }
        });
    }

    private void drawCentreAreaRect(VBox centerArea) {
        Rectangle[] blocks = new Rectangle[4];
        blocks[0] = new Rectangle(0, 0, 250, 100);
        blocks[1] = new Rectangle(0, 100, 250, 100);
        blocks[2] = new Rectangle(0, 200, 250, 100);
        blocks[3] = new Rectangle(0, 300, 250, 100);

        for (Rectangle block : blocks) {
            block.setFill(Color.TRANSPARENT);
            block.setStroke(Color.BLACK);
            centerArea.getChildren().add(block);
        }
    }

    protected void initGame() {
        //init save & load service
        initSaveLoadService();
        //init map
        levelMap.randommap();
        //init player and block
        Texture playerTexture = FXGL.getAssetLoader().loadTexture("knight.png");
        playerTexture.setFitHeight(60);
        playerTexture.setFitWidth(60);
        Texture block1Texture = FXGL.getAssetLoader().loadTexture("enemy.png");
        block1Texture.setFitHeight(80);
        block1Texture.setFitWidth(80);
        Texture block2Texture = FXGL.getAssetLoader().loadTexture("enemy.png");
        block2Texture.setFitHeight(80);
        block2Texture.setFitWidth(80);
        Texture block3Texture = FXGL.getAssetLoader().loadTexture("enemy.png");
        block3Texture.setFitHeight(80);
        block3Texture.setFitWidth(80);
        player = entityBuilder()
                .at(350, 340)
                .view(playerTexture)
                .buildAndAttach();

        block1 = entityBuilder()
                .at(250, 220)
                .view(block1Texture)
                .buildAndAttach();

        block2 = entityBuilder()
                .at(250, 120)
                .view(block2Texture)
                .buildAndAttach();

        block3 = entityBuilder()
                .at(250, 20)
                .view(block3Texture)
                .buildAndAttach();


    }

    @Override
    protected void onUpdate(double tpf) {
        if(getb("gameOver")){
            return;
        }
        // update UI
        block1.getViewComponent().clearChildren();
        block1.getViewComponent().addChild(getBlockTexture(levelMap.getZone(1).zoneType));
        block2.getViewComponent().clearChildren();
        block2.getViewComponent().addChild(getBlockTexture(levelMap.getZone(2).zoneType));
        block3.getViewComponent().clearChildren();
        block3.getViewComponent().addChild(getBlockTexture(levelMap.getZone(3).zoneType));
        player.setY(geti("blockNum") < 4 ? 340 - geti("blockNum") * 100 : 340);
        if(geti("health") <= 0 || geti("level") >= 100){
            getbp("gameOver").setValue(true);
            showGameOverPopup();
        }
        if (!getb("levelFinished")) {
            if (!getb("wait")) {
                proceed();
            }
            else{
                handleWait();
            }
        }
    }


    private void proceed() {
        int n = geti("blockNum");
        Zone currentZone = levelMap.getZone(n);
        getsp("message").setValue(currentZone.zoneMessage());
        getbp("wait").setValue(true);
    }
    private void handleWait(){
        int n = geti("blockNum");
        Zone currentZone = levelMap.getZone(n);
        if(currentZone.zoneType != Type.shop && currentZone.zoneType != Type.ability){
            currentZone.interact();
        }
        else{
            if(getb("pressedA")){
                levelMap.getZone(n).itemA.purchase();
                getbp("pressedA").setValue(false);
            }
            else if(getb("pressedB")){
                levelMap.getZone(n).itemB.purchase();
                getbp("pressedB").setValue(false);
            }
            else if(getb("pressedC")){
                levelMap.getZone(n).itemC.purchase();
                getbp("pressedC").setValue(false);
            }
            else{
                return;
            }
        }

        getip("blockNum").setValue(n < 4 ? n + 1 : 4);
        getbp("wait").setValue(false);

        if (n == 4) {
            getbp("levelFinished").setValue(true);
            levelMap.randommap();
            getip("level").setValue(geti("level") + 1);
        }
    }

    private void showGameOverPopup() {
        // 创建新的窗口
        gameOverStage = new Stage();
        gameOverStage.initModality(Modality.APPLICATION_MODAL);
        gameOverStage.setTitle("游戏结束");

        // 创建游戏结束弹窗内容
        String overMsg;
        if(geti("level") == 100){
            overMsg = "你已经到达了第100层，游戏结束";

        }
        else{
            overMsg = "你的生命值已经耗尽，游戏结束";
        }
        Label gameOverLabel = new Label(overMsg);
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Button loadButton = new Button("读档");
        loadButton.setFont(new Font(20));
        loadButton.setPrefSize(100, 50);
        loadButton.setOnAction(e -> {
            loadGame();
            gameOverStage.close();
        });

        VBox vbox = new VBox(10, gameOverLabel, loadButton);
        vbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(vbox, 300, 200);
        gameOverStage.setScene(scene);
        gameOverStage.show();
    }
    private Texture getBlockTexture(Type type){
        Texture blockTexture = switch (type) {
            case enemy -> FXGL.getAssetLoader().loadTexture("enemy.png");
            case coins -> FXGL.getAssetLoader().loadTexture("coins.png");
            case shop -> FXGL.getAssetLoader().loadTexture("store.png");
            case ability -> FXGL.getAssetLoader().loadTexture("ability.png");
            default -> FXGL.getAssetLoader().loadTexture("tree.png");
        };
        blockTexture.setFitWidth(80);
        blockTexture.setFitHeight(80);
        return blockTexture;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
