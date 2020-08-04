package stickman.model.levels;

import java.util.List;
import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.character.Hero;

public interface Level {
  List<Entity> getEntities();

  List<Entity> getStaticEntities();

  List<MovableEntity> getDynamicEntities();

  double getHeight();

  double getWidth();

  void tick();

  double getFloorHeight();

  Hero getHero();

  double getTargetTime();

  void addStaticEntity(Entity entity);

  void addDynamicEntity(MovableEntity movableEntity);

  void addHero(Hero hero);

  void updateScore(int score);

  int getLevelScore();
}
