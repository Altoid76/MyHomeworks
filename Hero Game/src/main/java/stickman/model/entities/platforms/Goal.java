package stickman.model.entities.platforms;

import stickman.model.config.Position;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.StaticEntity;

/** A special platform that */
public class Goal extends StaticEntity {
  private static final String GOAL_PATH = "/end.png";
  private Position<Double> position;
  public Goal(Position<Double> position) {
    super(position, GOAL_PATH, Layer.FOREGROUND, 20, 20);
    this.position=position;
  }

  @Override
  public void handleCollision(MovableEntity e) {
    e.touchedGoal();
  }
  public Position<Double> getPosition(){
    return position;
  }
  @Override
  public StaticEntity Clone() {
    return new Goal(this.position);
  }
}
