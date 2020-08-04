package stickman.model.entities;

import stickman.model.config.Position;

public abstract class StaticEntity extends Entity {
  private Position<Double> position;
  private double height;
  private double width;
  private Layer layer;

  protected StaticEntity(
      Position<Double> position, String imagePath, Layer layer, double height, double width) {
    super.setImagePath(imagePath);
    this.position = position;
    this.height = height;
    this.width = width;
    this.layer = layer;
  }

  @Override
  public double getXPos() {
    return position.getX();
  }

  @Override
  public double getYPos() {
    return position.getY();
  }

  @Override
  public double getHeight() {
    return height;
  }

  @Override
  public double getWidth() {
    return width;
  }

  @Override
  public Layer getLayer() {
    return layer;
  }

  public abstract StaticEntity Clone();
}
