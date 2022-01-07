/*
  Copyright (c) 2021, Automatic Controls Equipment Systems, Inc.
  Author: Cameron Vogt (@cvogt729)
*/
import java.io.*;
import java.awt.*;
/**
 * Represents a LogicSymbol file
 */
public class Symbol extends Item {
  public Symbol(File file){
    super(file);
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
      x+=1;
      y+=1;
      r2-=2;
      if (selected){
        g.setColor(Color.BLUE);
      }else{
        g.setColor(ACESEquipmentBuilder.background);
      }
      g.fillOval(x,y,r2,r2);
    }else if (selected){
      g.setColor(Color.BLUE);
      g.fillOval(x,y,r2,r2);
    }
    g.setColor(ACESEquipmentBuilder.foreground);
    g.drawOval(x,y,r2,r2);
  }
}