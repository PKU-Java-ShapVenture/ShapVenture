package com.shapventure;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getip;

public class EnhanceScene extends Stage {
    Outside outside;

    public EnhanceScene(Outside _outside) {
        this.outside = _outside;
        setTitle("强化界面");

        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-padding: 20; -fx-alignment: center;");
        // show current exp
        Label expLabel = new Label();
        expLabel.textProperty().bind(getip("exp").asString("当前经验值: %d"));
        expLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px;");
        root.getChildren().add(expLabel);
        for (Options option : Options.values()) {
            HBox attributeBox = createAttributeBox(option);
            root.getChildren().add(attributeBox);
        }

        Scene scene = new Scene(root, 400, 300);
        setScene(scene);
    }

    private HBox createAttributeBox(Options option) {
        HBox box = new HBox(10);
        box.setStyle("-fx-alignment: center;");

        Label nameLabel = new Label(option.toString());
        nameLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px;");

        Label valueLabel = new Label();
        valueLabel.textProperty().bind(getip(option.name()).asString());
        valueLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px;");

        Button btnIncrease = new Button("+");
        btnIncrease.setOnAction(e -> {
            outside.upgrade(option);
        });

        Button btnDecrease = new Button("-");
        btnDecrease.setOnAction(e -> {
            outside.degrade(option);
        });

        box.getChildren().addAll(nameLabel, valueLabel, btnIncrease, btnDecrease);
        return box;
    }
}

