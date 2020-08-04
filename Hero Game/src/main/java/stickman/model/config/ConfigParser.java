package stickman.model.config;

import java.util.Map;
import stickman.model.entities.character.Hero;

/**
 * The java representation of the configuration, no matter the format it comes in at the moment only
 * JSON is supported, but in the future YAML or JSON5 may be supported and we want to have a
 * consistent interface to get this information out
 *
 * <p>As the configuration gets more complex, this abstract class (and hence those that wish to
 * inherit from it) will need to get more complex too.
 */
public abstract class ConfigParser {
  static final Map<String, Hero.Size> sizeValidator =
      Map.of(
          "tiny", Hero.Size.TINY,
          "normal", Hero.Size.NORMAL,
          "large", Hero.Size.LARGE,
          "giant", Hero.Size.GIANT);
  Hero.Size stickmanSize;
  double cloudVelocity;
  LevelConfig level;

  public Hero.Size getStickmanSize() {
    return stickmanSize;
  }

  public double getCloudVelocity() {
    return cloudVelocity;
  }

  public LevelConfig getLevel() {
    return level;
  }
}
