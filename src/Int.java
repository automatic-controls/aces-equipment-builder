/**
 * Primitive type variables are always passed by value in Java, so this class encapsulates a single integer to force pass-by-reference
 */
public class Int {
  public int x = 0;
  public Int(int x){
    this.x = x;
  }
}