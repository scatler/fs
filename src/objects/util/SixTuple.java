//: net/mindview/util/FiveTuple.java
package objects.util;

public class SixTuple<A,B,C,D,E,G>
extends FiveTuple<A,B,C,D,E> {
  public final G sixth;
  public SixTuple(A a, B b, C c, D d, E e, G g) {
    super(a, b, c, d, e);
    sixth = g;
  }
  public String toString() {
    return "(" + first + ", " + second + ", " +
      third + ", " + fourth + ", " + fifth + "," + sixth +  ")";
  }
} ///:~
