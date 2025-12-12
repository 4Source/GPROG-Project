
// (c) Thorsten Hasbargen

final class A_Main 
{
  private A_World world = null;
  
  public A_Main()
  { 
	A_Frame frame = new B_Frame();
    frame.displayOnScreen();
    
    world = new Gam20_World();
    
    world.setGraphicSystem(frame.getGraphicSystem());
    world.setInputSystem(frame.getInputSystem());
    
    A_GameObject.setWorld(world);
    A_TextObject.setWorld(world);
    frame.getGraphicSystem().setWorld(world);
    
    world.init();
    world.run();
  }
	
  public static void main(String[] args)
  { new A_Main();
  }
}
