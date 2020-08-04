package stickman.model.config;

import java.util.ArrayList;
import java.util.List;

public class LevelConfig {
  private double height;
  private double width;
  private double floorHeight;
  private Position<Double> stickmanPos;
  private List<EntityConfig> entities;
  private double targetTime;
  public LevelConfig(
      double height, double width, double floorHeight, Position<Double> stickmanPos,double targetTime) {
    this.floorHeight = floorHeight;
    this.height = height;
    this.width = width;
    this.stickmanPos = stickmanPos;
    this.entities = new ArrayList<>();
    this.targetTime=targetTime;
  }

  /**
   * Add a new entity to the level configuration
   *
   * @param name name of the entity
   * @param x co-ordinate
   * @param y co-ordinate
   */
  void addEntity(String name, String movement, double x, double y) {
    entities.add(new EntityConfig(name, movement, x, y));
  }

  public double getFloorHeight() {
    return floorHeight;
  }

  public Position<Double> getStickmanPos() {
    return stickmanPos;
  }

  public List<EntityConfig> getEntities() {
    return entities;
  }

  public double getHeight() {
    return height;
  }

  public double getWidth() {
    return width;
  }

  public double getTargetTime(){
    return targetTime;
  }
}
