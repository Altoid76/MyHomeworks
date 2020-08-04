package stickman.model;

import stickman.model.Memento.Memento;
import stickman.model.levels.Level;

public interface GameEngine {
  Level getCurrentLevel();

  void startLevel();

  // Hero inputs - boolean for success (possibly for sound feedback)
  boolean jump();

  boolean moveLeft();

  boolean moveRight();

  boolean stopMoving();

  long getTimeSinceStart();

  int getHeroHealth();

  void tick();

  /**
   * When a level is restarted we need to clean out the old entity views, this is how we signal to
   * the
   *
   * @return true if the game engine needs the entities refreshed
   */
  boolean needsRefresh();

  /** After a refresh is completed, the clean method must be called to reset the flag */
  void clean();

  String getHeadsUpDisplayMessage();

  void levelTransition();

  void accumulateTotalScore();

  int getTotalScore();

  Memento saveMemento();

  void restoreMemento(Memento memento);
}
