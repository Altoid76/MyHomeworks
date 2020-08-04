package stickman.model.config;

/**
 * An immutable position: a simple data class that has an x and a y
 *
 * @param <T>: The type of the x and y co-ordinate, usually Double.
 */
public class Position<T> {
  private T x;
  private T y;

  public Position(T x, T y) {
    this.x = x;
    this.y = y;
  }

  public T getX() {
    return x;
  }

  public void setX(T x) {
    this.x = x;
  }

  public T getY() {
    return y;
  }

  public void setY(T y) {
    this.y = y;
  }
}
