package stickman.model.levels;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import stickman.model.config.ConfigParser;
import stickman.model.config.LevelConfig;
import stickman.model.config.Position;
import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.character.Hero;
import stickman.model.entities.platforms.LogPlatform;

/** An empty level, used as the basis for all other levels */
public class EmptyLevel implements Level {
  private double floorHeight;
  private double width;
  private double height;
  private Hero hero = null;
  private List<Entity> staticEntities = new ArrayList<Entity>();
  private List<MovableEntity> dynamicEntities = new ArrayList<MovableEntity>();
  private double targetTime;
  private int levelScore;
  EmptyLevel(ConfigParser configParser) {
    LevelConfig level = configParser.getLevel();
    this.floorHeight = level.getFloorHeight();
    this.height = level.getHeight();
    this.width = level.getWidth();
    this.targetTime=level.getTargetTime();
    this.levelScore=(int)targetTime;
  }
  public EmptyLevel(Level level){
    this.floorHeight=level.getFloorHeight();
    this.hero=(Hero)level.getHero().Clone();
    this.height=level.getHeight();
    this.width=level.getWidth();
    this.targetTime=level.getTargetTime();
    this.levelScore=level.getLevelScore();
    this.staticEntities=new ArrayList<>();
    for(Entity staticEn:level.getStaticEntities()){
      this.staticEntities.add(staticEn.Clone());
    }
    this.dynamicEntities= new ArrayList<>();
    this.dynamicEntities.add(this.hero);
    for(MovableEntity movable:level.getDynamicEntities()){
      if (movable.isCharacter()&&!movable.isEnemy()){
        continue;
      }
      this.dynamicEntities.add(movable.Clone());
    }
  }
  @Override
  public Hero getHero() {
    return hero;
  }

  @Override
  public double getTargetTime() {
    return 0;
  }

  @Override
  public List<Entity> getEntities() {
    // There are no static entities yet, but later there might be!
    return Stream.concat(staticEntities.stream(), dynamicEntities.stream())
        .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getStaticEntities() {
    return staticEntities;
  }

  @Override
  public List<MovableEntity> getDynamicEntities() {
    return dynamicEntities;
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
  public void tick() {

    // Ensure nothing falls into the ground with an ad-hoc floor entity
    Position<Double> floorPosition = new Position<Double>(0.0, getFloorHeight());
    LogPlatform floor = new LogPlatform(floorPosition);
    for (MovableEntity a : dynamicEntities) {
      if (a.getYPos() + a.getHeight() > getFloorHeight()) {
        a.feedbackOnTop(floor);
      }
    }
  }

  @Override
  public double getFloorHeight() {
    return floorHeight;
  }

  @Override
  public void addStaticEntity(Entity entity) {
    this.staticEntities.add(entity);
  }

  @Override
  public void addDynamicEntity(MovableEntity entity) {
    this.dynamicEntities.add(entity);
  }

  @Override
  public void addHero(Hero hero) {
    if (this.hero != null) {
      throw new Error("Hero already set for the level");
    }
    this.hero = hero;
    this.dynamicEntities.add(hero);
  }

  @Override
  public void updateScore(int score) {
    this.levelScore=score;
  }

  @Override
  public int getLevelScore(){
    return levelScore;
  }
}
