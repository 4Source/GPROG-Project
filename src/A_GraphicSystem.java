
// (c) Thorsten Hasbargen


interface A_GraphicSystem 
{
  // prepare to draw a new Screen
  void clear();
  
  // draw ONE GameObject on the Screen
  void draw(A_GameObject dot);
 
  // draw ONE TextObject on the Screen
  void draw(A_TextObject obj);
  
  // display the completed Screen
  void redraw();
  
  
  // set world
  void setWorld(A_World world);  
}
