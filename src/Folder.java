import java.io.*;
import java.awt.*;
/**
 * Represents a directory
 */
public class Folder extends Item {
  public Folder(File file){
    super(file);
    tab = 25;
  }
  public void drawIcon(Graphics2D g, int x, int y){
    int r2 = Item.r2;
    if (valueChanger){
      if (lastClicked==this){
        g.setColor(Color.ORANGE);
      }else{
        g.setColor(Color.DARK_GRAY);
      }
      g.fillRect(x,y,r2,r2);
      g.setColor(ACESEquipmentBuilder.foreground);
      g.drawRect(x,y,r2,r2);
      x+=2;
      y+=2;
      r2-=4;
      if (selected){
        g.setColor(Color.GREEN);
      }else{
        g.setColor(ACESEquipmentBuilder.background);
      }
      g.fillRect(x,y,r2,r2);
    }else if (selected){
      g.setColor(Color.GREEN);
      g.fillRect(x,y,r2,r2);
    }
    g.setColor(ACESEquipmentBuilder.foreground);
    g.drawRect(x,y,r2,r2);
  }
}