
// (c) Thorsten Hasbargen


import java.awt.Color;

class Gam20_Tree extends A_GameObject
{
  public Gam20_Tree(double x, double y, int r)
  {
    super(x,y,0,0,r,new Color(64,160,64));
    this.isMoving = false;
  }
  
  public int type() { return A_Const.TYPE_TREE; }
}
