package stickman.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import stickman.model.GameEngine;

public class HeadsUpDisplayDrawer implements ForegroundDrawer {
  private static final double X_PADDING = 50;
  private static final double Y_PADDING = 5;
  private Pane pane;
  private GameEngine model;
  private Label timeElapsed = new Label();
  private Label health = new Label();
  private Label message = new Label();
  private Label levelScore=new Label();
  private Label totalScore= new Label();
  @Override
  public void draw(GameEngine model, Pane pane) {
    this.model = model;
    this.pane = pane;
    update();
    timeElapsed.setFont(new Font("Monospaced Regular", 20));
    health.setFont(new Font("Monospaced Regular", 20));
    message.setFont(new Font("Monospaced Regular", 50));
    levelScore.setFont(new Font("Monospaced Regular",20));
    totalScore.setFont(new Font("Monospaced Regular",20));
    this.pane.getChildren().addAll(timeElapsed, health, message,levelScore,totalScore);
  }

  @Override
  public void update() {
    // Set the positions of the labels
    timeElapsed.setLayoutX(X_PADDING / 2);
    timeElapsed.setLayoutY(Y_PADDING);

    health.setLayoutX(timeElapsed.getLayoutX() + X_PADDING + timeElapsed.getWidth());
    health.setLayoutY(Y_PADDING);
    levelScore.setLayoutX(timeElapsed.getLayoutX() + X_PADDING + timeElapsed.getWidth()+health.getWidth()+health.getLayoutX());
    levelScore.setLayoutY(Y_PADDING);
    totalScore.setLayoutX(timeElapsed.getLayoutX() + X_PADDING + timeElapsed.getWidth());
    totalScore.setLayoutY(Y_PADDING+timeElapsed.getHeight()+timeElapsed.getLayoutY());
    message.setLayoutX(pane.getWidth() / 6);
    message.setLayoutY(pane.getHeight() / 4);

    // Set the text
    timeElapsed.setText(String.format("TIME%n %03d", model.getTimeSinceStart() / 1000));
    //System.out.println(model.getTimeSinceStart()/1000);
    health.setText(String.format("HEALTH%n   %03d", model.getHeroHealth()));
    message.setText(model.getHeadsUpDisplayMessage());
    levelScore.setText(String.format("CurrentLevelScore%n %03d",model.getCurrentLevel().getLevelScore()));
    totalScore.setText(String.format("TotalLevelScore%n %03d",model.getTotalScore()));
  }
}
