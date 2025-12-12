
// (c) Thorsten Hasbargen

import java.awt.Color;


abstract class A_TextObject
{
  protected static A_World world;
  
  // yes, public :(
  protected int     x,y;
  protected Color color;
  
  public A_TextObject(int x_, int y_, Color color_)
  { x=x_; y=y_; color=color_;
  }
  
  public abstract String toString();
  
  protected static void setWorld(A_World w){world=w;}
}
