
// (c) Thorsten Hasbargen


interface A_Frame 
{
  // appear on Screen
  void displayOnScreen();
  
  // return Subsystems
  A_GraphicSystem getGraphicSystem();
  A_InputSystem   getInputSystem();
}
