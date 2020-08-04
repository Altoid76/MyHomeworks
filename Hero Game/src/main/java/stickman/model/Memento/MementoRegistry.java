package stickman.model.Memento;

public class MementoRegistry {
  private Memento memento;
  public void setMemento(Memento memento){
    this.memento=memento;
  }
  public Memento getMemento(){
    return this.memento;
  }
}
