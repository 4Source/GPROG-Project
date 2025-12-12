
// (c) Thorsten Hasbargen


import java.awt.Color;

class Gam20_Grenade extends A_GameObject
{
  double life = A_Const.LIFE_GRENADE;
  
  public Gam20_Grenade(double x, double y)
  {
    super(x,y,0,0,15,Color.ORANGE);
  }
  
  public void move(double diffSeconds)
  {
    life -= diffSeconds;
    if(life<0)
    { this.isLiving=false;
      return;
    }
    
  }
  
  public int type() { return A_Const.TYPE_GRENADE; }
}
