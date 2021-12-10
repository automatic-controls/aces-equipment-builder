import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.awt.*;
/**
 * Base class for Folder and Symbol
 */
public abstract class Item implements Comparable<Item>{
  /** Used to control valueChangers */
  public static volatile Item lastClicked = null;
  /** Used to pass the equipment name from the gui to this class */
  public static volatile String equipmentName = "";
  /** The foreground used to paint items which are a member of an ItemGroup whose minimum is not satisfied */
  protected final static Color requiredColor = Color.RED;
  public final static int entryHeight = 30;
  protected final static int entryHeight2 = entryHeight>>1;
  protected final static int r = 6;
  public final static int r2 = r<<1;
  protected final static int spacing = r2+10;
  protected final static int yStringOffset = ACESEquipmentBuilder.fontSize+3;
  /** Specifies the y offset of children when painting to the screen */
  protected int tab = 0;
  protected int entryWidth = 0;
  /** If this is the result of an initialization reference, original will be the item referenced */
  public Item original = null;
  public Item parent = null;
  /** The corresponding locally stored file */
  public File file = null;
  /** The corresponding remotely stored file */
  public File origin = null;
  /** A reference name used to identify this item */
  public String refName = null;
  /** The string painted to the screen */
  public String name = null;
  /** Used to compute name */
  public String originalName = null;
  public boolean selected = false;
  public boolean visible = true;
  public boolean locked = false;
  /** An array of children */
  public Item[] items = null;
  public int len = 0;
  public ItemGroup[] groups = null;
  public int groupLen = 0;
  public SuggestGroup[] suggestGroups = null;
  public int sugLen = 0;
  public String preScript = "";
  public String postScript = "";
  /** Whether an item is a valueChanger */
  public boolean valueChanger = false;
  public int value = 0;
  /** min<=value */
  public int min = 0;
  /** max>=value */
  public int max = 0;
  /** The increment used to change value when arrow keys are pressed */
  public int increment = 0;
  /** Used to determine if a script is ready to be generated */
  public String[] conditions = null;
  public String[] failMsg = null;
  public int conLen = 0;
  /** Used to convey the failure message */
  public static StringBuilder failureMessage = new StringBuilder();
  private static int numItems = 0;
  private int ID;
  public Item(File file){
    this.file = file;
    ID = ++numItems;
  }
  @Override public int compareTo(Item x){
    return ID-x.ID;
  }
  public void reset(){
    items = null;
    groups = null;
    suggestGroups = null;
    conditions = null;
    failMsg = null;
    lastClicked = this;
    len = 0;
    groupLen = 0;
    sugLen = 0;
    conLen = 0;
    preScript = "";
    postScript = "";
    numItems = 0;
    System.gc();
  }
  public String getRelativeDisplayPath(Item root){
    if (root==null){
      root = ACESEquipmentBuilder.lib;
    }
    StringBuilder sb = new StringBuilder();
    getRelativeDisplayPath(root,sb);
    return sb.toString();
  }
  private void getRelativeDisplayPath(Item root, StringBuilder sb){
    if (this!=root && parent!=null){
      parent.getRelativeDisplayPath(root, sb);
      if (sb.length()!=0){
        sb.append('\\');
      }
      sb.append(name==null?refName:name);
    }
  }
  public String getRelativeReferencePath(Item root){
    if (root==null){
      root = ACESEquipmentBuilder.lib;
    }
    StringBuilder sb = new StringBuilder();
    getRelativeReferencePath(root,sb);
    return sb.toString();
  }
  private void getRelativeReferencePath(Item root, StringBuilder sb){
    if (this!=root && parent!=null){
      parent.getRelativeReferencePath(root, sb);
      if (sb.length()!=0){
        sb.append('\\');
      }
      sb.append(refName);
    }
  }
  public Item find(CharSequence seq){
    return find(seq,0,seq.length());
  }
  /**
   * Finds an item using a reference name path.
   */
  public Item find(CharSequence seq, int start, int end){
    if (start>=end){
      return this;
    }
    char c;
    int i, j;
    i = -1;
    for (j=start;j<end;++j){
      c = seq.charAt(j);
      if (c=='\\' || c=='/'){
        i = j;
        break;
      }
    }
    String str;
    int newStart;
    if (i==start){
      return ACESEquipmentBuilder.lib.find(seq, start+1, end);
    }else if (i==-1){
      str = seq.subSequence(start,end).toString();
      newStart = end;
    }else{
      str = seq.subSequence(start,i).toString();
      newStart = i+1;
    }
    if (str.trim().equals("~")){
      return parent==null?null:parent.find(seq,newStart,end);
    }else{
      Matcher m = Patterns.group.matcher(str);
      if (m.matches()){
        try{
          i = Integer.parseUnsignedInt(m.group(1))-1;
          if (i<0 || i>=groupLen){
            return null;
          }
          ItemGroup grp = groups[i];
          i = Integer.parseUnsignedInt(m.group(2));
          if (i<=0){
            return null;
          }
          for (j=0;j<grp.len;++j){
            if (grp.items[j].selected && --i==0){
              return grp.items[j].find(seq,newStart,end);
            }
          }
          return null;
        }catch(Exception e){
          return null;
        }
      }
    }
    for (i=0;i<len;i++){
      if (str.equals(items[i].refName)){
        return items[i].find(seq,newStart,end);
      }
    }
    return null;
  }
  /**
   * Finds an item using a reference name.
   */
  public Item findSimple(String ref){
    for (int i=0;i<len;i++){
      if (items[i].refName.equals(ref)){
        return items[i];
      }
    }
    return null;
  }
  public TreeSet<Item> findReferences(boolean indirect){
    TreeSet<Item> set = new TreeSet<Item>();
    TreeSet<Item> set1 = new TreeSet<Item>();
    TreeSet<Item> set2 = new TreeSet<Item>();
    TreeSet<Item> tmp;
    set.add(this);
    set1.add(this);
    do {
      ACESEquipmentBuilder.lib.findReferences(set1, set2);
      set2.removeAll(set);
      if (set2.isEmpty()){
        break;
      }
      set.addAll(set2);
      tmp = set1;
      set1 = set2;
      set2 = tmp;
      set2.clear();
    } while (indirect);
    set.remove(this);
    return set;
  }
  protected void findReferences(TreeSet<Item> set, TreeSet<Item> newSet){
    if (original!=null){
      Item y;
      for (Item x:set){
        for (y=x;y!=null;y=y.parent){
          if (y==original){
            newSet.add(this);
          }
        }
        for (y=original.parent;y!=null;y=y.parent){
          if (x==y){
            newSet.add(this);
          }
        }
      }
    }
    for (int i=0;i<len;++i){
      items[i].findReferences(set,newSet);
    }
  }
  public void add(String con, String failMSG){
    if (conLen==0){
      conditions = new String[]{con};
      failMsg = new String[]{failMSG};
    }else if (conLen==conditions.length){
      String[] arr = new String[conLen<<1];
      String[] arr2 = new String[conLen<<1];
      for (int i=0;i<conLen;i++){
        arr[i] = conditions[i];
        arr2[i] = failMsg[i];
      }
      arr[conLen] = con;
      arr2[conLen] = failMSG;
      conditions = arr;
      failMsg = arr2;
    }else{
      conditions[conLen] = con;
      failMsg[conLen] = failMSG;
    }
    conLen++;
  }
  public void add(Item item){
    if (len==0){
      items = new Item[]{item};
    }else if (len==items.length){
      Item[] arr = new Item[len<<1];
      for (int i=0;i<len;i++){
        arr[i] = items[i];
      }
      arr[len] = item;
      items = arr;
    }else{
      items[len] = item;
    }
    item.parent = this;
    len++;
  }
  public void add(ItemGroup group){
    if (groupLen==0){
      groups = new ItemGroup[]{group};
    }else if (groupLen==groups.length){
      ItemGroup[] arr = new ItemGroup[groupLen<<1];
      for (int i=0;i<groupLen;i++){
        arr[i] = groups[i];
      }
      arr[groupLen] = group;
      groups = arr;
    }else{
      groups[groupLen] = group;
    }
    groupLen++;
  }
  public void add(SuggestGroup group){
    if (sugLen==0){
      suggestGroups = new SuggestGroup[]{group};
    }else if (sugLen==suggestGroups.length){
      SuggestGroup[] arr = new SuggestGroup[sugLen<<1];
      for (int i=0;i<sugLen;i++){
        arr[i] = suggestGroups[i];
      }
      arr[sugLen] = group;
      suggestGroups = arr;
    }else{
      suggestGroups[sugLen] = group;
    }
    sugLen++;
    group.parent = this;
  }
  public void removeSuggestGroup(int i){
    suggestGroups[i] = null;
    sugLen--;
    if (i!=sugLen){
      suggestGroups[i] = suggestGroups[sugLen];
      suggestGroups[sugLen] = null;
    }
  }
  public void removeItem(int i){
    items[i] = null;
    len--;
    for (int j=i;j<len;j++){
      items[j] = items[j+1];
    }
    items[len] = null;
  }
  public void select(Item item){
    if (!item.locked){
      item.selected^=true;
      for (int i=0;i<groupLen;i++){
        if (groups[i].select(item)){
          break;
        }
      }
      ACESEquipmentBuilder.lib.evaluateSuggestions(new Container<Integer>(0));
    }
  }
  /**
   * Evaluates all If-Then statement suggestions
   */
  public boolean evaluateSuggestions(Container<Integer> c){
    try{
      if (!selected){
        return false;
      }
      boolean change = true;
      boolean ret = false;
      //Continues to loop so long as changes are occuring
      while (change){
        if (++c.x>32768){
          ACESEquipmentBuilder.error("Infinite loop detected in If-Then statement logic.\nPlease review configuration files.");
          return false;
        }
        change = false;
        for (int i=0;i<sugLen;i++){
          if (suggestGroups[i].act()){
            change = true;
            ret = true;
          }
        }
        for (int i=0;i<len;i++){
          if (items[i].evaluateSuggestions(c)){
            change = true;
            ret = true;
          }
          if (c.x>32768){
            return false;
          }
        }
      }
      return ret;
    }catch(StackOverflowError e){
      System.gc();
      ACESEquipmentBuilder.error("Infinite loop detected in If-Then statement logic.\nPlease review configuration files.");
      return false;
    }
  }
  public boolean valid(){
    for (Item ref=this;ref!=null;ref=ref.parent){
      if (!ref.selected){
        return false;
      }
    }
    return true;
  }
  public boolean onScreen(){
    Item ref = this;
    boolean b = ref.visible;
    if (b){
      for (ref=ref.parent;ref!=null;ref=ref.parent){
        if (!ref.visible || !ref.selected){
          b = false;
          break;
        }
      }
    }
    return b;
  }
  public boolean ready(){
    boolean ret = true;
    if (parent==null){
      failureMessage.setLength(0);
      failureMessage.append("Requirements have not been satisfied.");
    }
    for (int i=0;i<groupLen;i++){
      if (groups[i].num<groups[i].min){
        failureMessage.append("\nGroup minimum unsatisfied in "+getRelativeDisplayPath(null));
        ret = false;
        break;
      }
    }
    for (int i=0;i<conLen;i++){
      if (!Utilities.evaluateExpression(Utilities.expandTernary(Utilities.expandProperties(this, conditions[i])))){
        if (failMsg[i]!=null){
          failureMessage.append('\n').append(Utilities.escape(Utilities.expandTernary(Utilities.expandProperties(this, failMsg[i]))));
        }
        ret = false;
      }
    }
    for (int i=0;i<len;i++){
      if (items[i].selected && !items[i].ready()){
        ret = false;
      }
    }
    return ret;
  }
  public String getProperty(String name){
    switch (name.toUpperCase()){
      case "SELECTED": return valid()?"1":"0";
      case "@SELECTED": return selected?"1":"0";
      case "VISIBLE": return onScreen()?"1":"0";
      case "@VISIBLE": return visible?"1":"0";
      case "LOCKED": return locked?"1":"0";
      case "VALUECHANGING": return valueChanger&&lastClicked.ID==ID?"1":"0";
      case "EXISTS": return "1";
      case "VALUE": return String.valueOf(value);
      case "MIN": case "MINIMUM": return String.valueOf(min);
      case "MAX": case "MAXIMUM": return String.valueOf(max);
      case "INCREMENT": return String.valueOf(increment);
      case "DISPLAYNAME": return this.name;
      case "REFERENCENAME": return refName;
      case "EQUIPMENTNAME": return equipmentName;
      case "FILEPATH":{
        String tmp = file.getPath().replace('\\','/');
        return tmp.endsWith(".logicsymbol")?tmp.substring(0,tmp.length()-12):tmp;
      }
    }
    return null;
  }
  public boolean setProperty(String name, String value){
    try{
      int i = Integer.parseInt(value);
      switch (name.toUpperCase()){
        case "VALUE": {
          if (valueChanger){
            if (i<min){
              i = min;
            }else if (i>max){
              i = max;
            }
          }
          if (this.value!=i){
            this.value = i;
            return true;
          }
          break;
        }
        case "MIN": case"MINIMUM": {
          if (min!=i){
            min = i;
            if (this.value<min){
              this.value = min;
            }
            if (max<min){
              max = min;
            }
            return true;
          }
          break;
        }
        case "MAX": case "MAXIMUM": {
          if (max!=i){
            max = i;
            if (this.value>max){
              this.value = max;
            }
            if (min>max){
              min = max;
            }
            return true;
          }
          break;
        }
        case "INCREMENT": {
          if (increment!=i){
            increment = i;
            return true;
          }
          break;
        }
      }
    }catch(Exception e){}
    return false;
  }
  public int paint(Graphics2D g, int x, int y){
    if (visible){
      if (parent==null){
        g.setColor(ACESEquipmentBuilder.foreground);
      }else{
        drawIcon(g,x,y+entryHeight2-r);
        for (int i=0;i<parent.groupLen;i++){
          if (parent.groups[i].contains(this) && parent.groups[i].num<parent.groups[i].min){
            g.setColor(requiredColor);
            break;
          }
        }
        g.drawString(name, x+spacing, y+yStringOffset);
        y+=entryHeight;
        x+=tab;
      }
      if (selected){
        for (int i=0;i<len;i++){
          y = items[i].paint(g,x,y);
        }
      }
    }
    return y;
  }
  /** Updates name with originalName and computes the string width to be painted */
  public int computeWidth(FontMetrics metrics){
    int width = 0;
    if (visible){
      if (parent!=null){
        name = Utilities.escape(Utilities.expandTernary(Utilities.expandProperties(this, originalName))).toString();
        entryWidth = spacing+metrics.stringWidth(name);
        width = entryWidth;
      }
      if (selected){
        for (int i=0;i<len;i++){
          int w = items[i].computeWidth(metrics)+tab;
          if (w>width){
            width = w;
          }
        }
      }
    }
    return width;
  }
  /** Recursively computes the height to be painted */
  public int computeHeight(){
    int height = 0;
    if (visible){
      if (parent!=null){
        height+=entryHeight;
      }
      if (selected){
        for (int i=0;i<len;i++){
          height+=items[i].computeHeight();
        }
      }
    }
    return height;
  }
  public Item getItem(Container<Integer> y){
    if (y.x<0){
      return null;
    }
    if (visible){
      if (parent!=null){
        y.x-=entryHeight;
        if (y.x<0){
          return this;
        }
      }
      if (selected){
        Item x;
        for (int i=0;i<len;i++){
          x = items[i].getItem(y);
          if (x!=null){
            return x;
          }
        }
      }
    }
    return null;
  }
  public void doClick(){
    if (!selected || !valueChanger || lastClicked==this){
      parent.select(this);
    }
    lastClicked = this;
  }
  /**
   * Used for passing information from the program to Eikon scripts
   */
  private void format(StringBuilder sb, String str, boolean newLine){
    if (!str.isEmpty()){
      if (newLine){
        sb.append(ACESEquipmentBuilder.lineSeparator);
      }
      StringBuilder sub = new StringBuilder();
      boolean a = false;
      boolean b = false;
      boolean found = false;
      int len = str.length();
      char c;
      for (int i=0;i<len;i++){
        c = str.charAt(i);
        if (a){
          if (found){
            if (c==']'){
              sb.append(Utilities.escape(Utilities.expandProperties(this, '<'+sub.toString()+'>')));
            }else{
              sb.append("[<").append(sub).append('>').append(c);
            }
            a = false;
            found = false;
            sub.setLength(0);
          }else if (b){
            if (c=='>'){
              found = true;
              b = false;
            }else{
              sub.append(c);
            }
          }else if (c=='<'){
            b = true;
          }else{
            sb.append('[').append(c);
            a = false;
          }
        }else if (c=='['){
          a = true;
        }else{
          sb.append(c);
        }
      }
      if (sub.length()!=0){
        sb.append("[<").append(sub);
        if (found){
          sb.append('>');
        }
      }
    }
  }
  public void toScript(StringBuilder sb){
    if (selected){
      format(sb, preScript,parent!=null);
      for (int i=0;i<len;i++){
        items[i].toScript(sb);
      }
      format(sb,postScript,true);
    }
  }
  /**
   * drawIcon is trusted to leave g's color as ACESEquipmentColor.foreground
   */
  public abstract void drawIcon(Graphics2D g, int x, int y);
}