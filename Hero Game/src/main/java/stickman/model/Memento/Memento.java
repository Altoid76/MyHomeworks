package stickman.model.Memento;

import stickman.model.levels.Level;

public class Memento {
  private Level level;
  private int score;
  private int health;
  private int levelId;
  public Memento(Level level,int score,int health,int levelId){
      this.level=level;
      this.score=score;
      this.health=health;
      this.levelId=levelId;
  }
  public Level getLevel(){
    return this.level;
  }
  public int getScore(){
    return this.score;
  }
  public int getHealth(){return this.health;}
  public int getLevelId(){
    return this.levelId;
  }
}
