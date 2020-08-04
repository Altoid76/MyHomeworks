package stickman.model;

import java.util.List;
import stickman.model.Memento.Memento;
import stickman.model.config.ConfigParser;
import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.character.Hero;
import stickman.model.levels.EmptyLevel;
import stickman.model.levels.Level;
import stickman.model.levels.LevelBuilder;
import stickman.view.SoundPlayer;
/** An implementation of the Game Engine interface, allows control of the player */
public class GameEngineImpl implements GameEngine {
  private Level currentLevel;
  private List<ConfigParser> config;
  private String headsUpDisplayMessage = "";
  private boolean needsRefresh = false;
  private boolean gameFinished = false;
  private SoundPlayer soundPlayer = new SoundPlayer();
  private long startTime;
  private long endTime;
  private int levelId;
  private int totalScore;
  private int tickCount=3;//for some reason, user playing time start from 3
  public GameEngineImpl(List<ConfigParser> config) {
    this.config = config;
    this.levelId=0;
    this.totalScore=0;
  }
  @Override
  public Level getCurrentLevel() {
    return this.currentLevel;
  }

  @Override
  public void startLevel() {
    if (currentLevel != null) {
      // If we're restarting delete all the entities, so they're no longer rendered
      currentLevel.getEntities().forEach(Entity::delete);
      needsRefresh = true;
    }
    currentLevel = LevelBuilder.fromConfig(config.get(levelId)).build();
    startTime = System.currentTimeMillis();
    accumulateTotalScore();
  }
  @Override
  public boolean jump() {
    if (currentLevel.getHero().jump()) {
      soundPlayer.playJumpSound();
      return true;
    }
    return false;
  }

  @Override
  public boolean moveLeft() {
    return currentLevel.getHero().moveLeft();
  }

  @Override
  public boolean moveRight() {
    return currentLevel.getHero().moveRight();
  }

  @Override
  public boolean stopMoving() {
    return currentLevel.getHero().stopMoving();
  }

  @Override
  public long getTimeSinceStart() {
    if (gameFinished) {
      return endTime - startTime;
    }
    return System.currentTimeMillis() - startTime;
  }

  @Override
  public int getHeroHealth() {
    if (currentLevel.getHero() != null) {
      return currentLevel.getHero().getHealth();
    }
    return 0;
  }

  private void updateEntities() {
    // Collect the entities from the current level
    List<Entity> staticEntities = currentLevel.getStaticEntities();
    List<MovableEntity> dynamicEntities = currentLevel.getDynamicEntities();
    // Remove any dead entities
    staticEntities.removeIf(Entity::isDeleted);
    dynamicEntities.removeIf(Entity::isDeleted);
    // Move everything that can move
    for (MovableEntity a : dynamicEntities) {
      a.moveTick();
    }
    // Check for collisions
    for (MovableEntity a : dynamicEntities) {
      for (Entity b : currentLevel.getEntities()) {
        if (a != b && a.overlappingSameLayer(b)) {
          b.handleCollision(a);
          if((b.isDeleted()||a.isDeleted())&&!currentLevel.getHero().isDeleted()){
             increaseScore();
          }
          // Only do one collision at a time
          break;
        }
      }
    }
  }

  private void updateState() {
    Hero hero = currentLevel.getHero();
    // Check if we need to change state based on the hero.
    // if level is over and there is remaining level, we dont want to print out the ending title.
    //instead, we should print out the title when there is no remaining level and current level is over.
    if (hero.isFinished()) {
      if(isGameFinished()){
        headsUpDisplayMessage = "GAME OVER: YOU WIN!";
        endTime = System.currentTimeMillis();
        currentLevel.getEntities().forEach(Entity::delete);
        needsRefresh = true;
        gameFinished = true;
      }else{
        levelTransition();
      }
    } else if (hero.isDeleted()) {
      headsUpDisplayMessage = "YOU LOSE: TRY AGAIN!";
      this.totalScore=0;
      this.startLevel();
      tickCount=0;
    } else if (headsUpDisplayMessage != null && hero.getXVelocity() != 0) {
      headsUpDisplayMessage = null;
    }
  }

  @Override
  public void tick() {
    /*When game ends, we want to check if there is any remaining level, if there is, transit to another level. if
     no remaining levels, do nothing*/
    if(gameFinished){
        return;
    }
    if(getTimeSinceStart()/1000==tickCount&&currentLevel.getLevelScore()!=0&&totalScore!=0){
      ++tickCount;
      decreaseScore();
    }else if(getTimeSinceStart()/1000==tickCount&&(currentLevel.getLevelScore()==0||totalScore==0)){
      ++tickCount;
    }
    updateEntities();
    updateState();
    // Make the level tick if it has anything to do
    this.currentLevel.tick();
  }
  //
  private boolean isGameFinished(){
    return levelId == config.size() - 1;
}
  @Override
  public boolean needsRefresh() {
    return needsRefresh;
  }

  @Override
  public void clean() {
    needsRefresh = false;
  }

  public String getHeadsUpDisplayMessage() {
    return headsUpDisplayMessage;
  }

  public void levelTransition() {
    levelId++;
    startLevel();
    tickCount=0;//Reset the start time to zero
  }

  @Override
  public void accumulateTotalScore() {
      totalScore+=this.currentLevel.getLevelScore();
  }
  @Override
  public int getTotalScore(){
    return totalScore;
  }
  private void decreaseScore(){
    currentLevel.updateScore(currentLevel.getLevelScore()-1);
    totalScore--;
  }
  private void increaseScore(){
    currentLevel.updateScore(currentLevel.getLevelScore()+100);
    totalScore+=100;
  }
  public Memento saveMemento(){
      Level level=new EmptyLevel(currentLevel);
      return new Memento(level,totalScore,this.getHeroHealth(),this.levelId);
  }
  public void restoreMemento(Memento memento){
    if(!currentLevel.equals(memento.getLevel())) {
        currentLevel.getEntities().forEach(Entity::delete);
    }
    if(this.levelId!=memento.getLevelId()){
      levelId=memento.getLevelId();
    }
      this.currentLevel=memento.getLevel();
      this.totalScore=memento.getScore();
      this.currentLevel.getHero().setHealth(memento.getHealth());
      needsRefresh=true;
  }
}
