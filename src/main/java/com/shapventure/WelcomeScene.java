package com.shapventure;
import com.almasb.fxgl.dsl.FXGL;
import com.shapventure.ShapVentureApp;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGL.geti;
import static com.shapventure.ShapeVentureCtrl.loadGame;

public class WelcomeScene {

    private Pane view;
    private ShapVentureApp app;
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 24px; -fx-padding: 15 30 15 30;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 24px; -fx-padding: 15 30 15 30;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 24px; -fx-padding: 15 30 15 30;"));
        return button;
    }

    public WelcomeScene(ShapVentureApp _app) {
        this.app = _app;
        VBox menuBox = new VBox(30);
        menuBox.setTranslateX(FXGL.getAppWidth() / 2.0 - 180);
        menuBox.setTranslateY(FXGL.getAppHeight() / 2.0 - 250);
        menuBox.setAlignment(javafx.geometry.Pos.CENTER);

        Text title = new Text("欢迎来到 ShapVenture！");
        title.setFill(Color.BLACK);
        title.setFont(Font.font(36));
        title.setFont(Font.font("华文仿宋", 36));

        Button btnStartGame = createStyledButton("开始游戏");
        btnStartGame.setAlignment(javafx.geometry.Pos.CENTER);
        btnStartGame.setOnAction(e -> {
            // 进入游戏主界面
            app.startGame();
        });

        Button btnLoadGame = createStyledButton("读取存档");
        btnLoadGame.setAlignment(javafx.geometry.Pos.CENTER);
        btnLoadGame.setOnAction(e -> {
            // 读取存档功能
            ShapeVentureCtrl.loadGame();
            app.startGame();
        });

        Button btnEnhance = createStyledButton("强化能力");
        btnEnhance.setAlignment(javafx.geometry.Pos.CENTER);
        btnEnhance.setOnAction(e -> {
            // 进入强化界面
            showEnhanceScreen();
        });

        menuBox.getChildren().addAll(title, btnStartGame, btnLoadGame, btnEnhance);
        view = new Pane(menuBox);
    }

    public Pane getView() {
        return view;
    }

    private void showEnhanceScreen() {
        // 创建新的窗口
        Stage enhanceStage = new EnhanceScene(app.outside);
        enhanceStage.initModality(Modality.APPLICATION_MODAL);
        enhanceStage.show();
    }
}
