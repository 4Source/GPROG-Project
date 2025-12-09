
import java.awt.Color;

class Gam20_Shot extends A_GameObject
{ 
  private double lifeTime = 1.2;

  public Gam20_Shot(double x, double y, double xDest, double yDest)
  {
    super(x,y,Math.atan2(yDest-y, xDest-x),500,4,Color.YELLOW);
    this.isMoving = true;
  }
  
  public Gam20_Shot(double x, double y, double a, double s, double time)
  { super(x,y,a,s,4,Color.YELLOW);
    lifeTime = time;
    this.isMoving = true;
  }
  
  
  public void move(double diffSeconds)
  { 
	lifeTime -= diffSeconds;
	if(lifeTime<=0)
	{ this.isLiving=false;
	  return;
	}
	
	
    // handle collisions of the zombie
	A_GameObjectList collisions = world.getPhysicsSystem().getCollisions(this);
	for(int i=0; i<collisions.size(); i++)
	{
	  A_GameObject obj = collisions.get(i);
	  
	  int type = obj.type();
	  
	  // tree: shot is deleted
	  if(type==A_Const.TYPE_TREE)
	  { this.isLiving=false;
	  }
	  // Zombie: inform Zombie it is hit
	  else if(type==A_Const.TYPE_ZOMBIE && obj.isLiving)
	  { 
	    Gam20_ZombieAI zombie = (Gam20_ZombieAI)obj;
	    zombie.hasBeenShot();
        this.isLiving=false;
	  }
	}  
	
	super.move(diffSeconds);
  }
    
  public final int type() { return A_Const.TYPE_SHOT;}
}
