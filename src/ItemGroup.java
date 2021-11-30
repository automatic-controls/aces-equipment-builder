public class ItemGroup {
  public Item[] items = null;
  public int len = 0;
  /** Maintains the number of selected items out of the group */
  public int num = 0;
  /** Maintains num>=min */
  public int min = 0;
  /** Maintins num<=max */
  public int max = 0;
  public ItemGroup(int min, int max){
    this.min = min;
    this.max = max;
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
    len++;
    if (item.selected){
      num++;
    }
    check();
  }
  public boolean contains(Item item){
    for (int i=0;i<len;i++){
      if (items[i]==item){
        return true;
      }
    }
    return false;
  }
  /**
   * Maintains the value of num and partially controls item visibility
   */
  public boolean select(Item item){
    boolean ret = contains(item);
    if (ret){
      if (item.selected){
        num++;
        if (num==max){
          for (int k=0;k<len;k++){
            setVisible(items[k], items[k].selected);
          }
        }
      }else{
        //Makes items visible when going from num=max to num=max-1
        if (num==max){
          for (int i=0;i<len;i++){
            setVisible(items[i], true);
          }
        }
        num--;
      }
    }
    return ret;
  }
  /**
   * Controls item visibility
   */
  public void check(){
    if (num==max){
      for (int k=0;k<len;k++){
        items[k].visible = items[k].selected;
      }
    }else{
      for (int k=0;k<len;k++){
        setVisible(items[k], true);
      }
    }
  }
  private static void setVisible(Item item, boolean visible){
    if (visible){
      if (SuggestGroup.allowVisibility(ACESEquipmentBuilder.lib, item)){
        item.visible = true;
      }
    }else{
      item.visible = false;
    }
  }
}