package stickman.model.entities.platforms;

import stickman.model.config.Position;
import stickman.model.entities.StaticEntity;

public class LogPlatform extends StaticEntity {
  private static final String LOG_PATH = "/log.png";
  private Position<Double> position;
  public LogPlatform(Position<Double> position) {
    super(position, LOG_PATH, Layer.FOREGROUND, 9, 76);
    this.position=position;
  }

  @Override
  public StaticEntity Clone() {
    return new LogPlatform(position);
  }
}
