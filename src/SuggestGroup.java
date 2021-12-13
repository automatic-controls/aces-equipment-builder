/*
  BSD 3-Clause License
  Copyright (c) 2021, Automatic Controls Equipment Systems, Inc.
  Author: Cameron Vogt (@cvogt729)
*/
public class SuggestGroup {
  //The following are modifier bitmask constants
  public final static byte IGNORE_PARENTS = 1;
  public final static byte SELECTED = 2;
  public final static byte NOT_SELECTED = 4;
  public final static byte VISIBLE = 8;
  public final static byte NOT_VISIBLE = 16;
  public final static byte LOCKED = 32;
  public final static byte NOT_LOCKED = 64;
  /** The item this belongs to */
  public Item parent = null;
  /** Preconditions */
  public Item[] pre = null;
  /** Precondition modifiers */
  public byte[] preMod = null;
  /** Postconditions */
  public Item[] post = null;
  /** Postcondition modifiers */
  public byte[] postMod = null;
  /** Additional precondition */
  public String preExpression = null;
  /** Additional postcondition */
  public String postExpression = null;
  /** Initialized length of pre and preMod */
  public int preLen = 0;
  /** Initialized length of post and postMod */
  public int postLen = 0;
  public void addPre(Item item, byte mod){
    if (preLen==0){
      pre = new Item[]{item};
      preMod = new byte[]{mod};
    }else if (preLen==pre.length){
      Item[] pre2 = new Item[preLen<<1];
      byte[] preMod2 = new byte[preLen<<1];
      for (int i=0;i<preLen;i++){
        pre2[i] = pre[i];
        preMod2[i] = preMod[i];
      }
      pre2[preLen] = item;
      preMod2[preLen] = mod;
      pre = pre2;
      preMod = preMod2;
    }else{
      pre[preLen] = item;
      preMod[preLen] = mod;
    }
    preLen++;
  }
  public void addPost(Item item, byte mod){
    if (postLen==0){
      post = new Item[]{item};
      postMod = new byte[]{mod};
    }else if (postLen==post.length){
      Item[] post2 = new Item[postLen<<1];
      byte[] postMod2 = new byte[postLen<<1];
      for (int i=0;i<postLen;i++){
        post2[i] = post[i];
        postMod2[i] = postMod[i];
      }
      post2[postLen] = item;
      postMod2[postLen] = mod;
      post = post2;
      postMod = postMod2;
    }else{
      post[postLen] = item;
      postMod[postLen] = mod;
    }
    postLen++;
  }
  public int indexOfPre(Item item){
    for (int i=0;i<preLen;i++){
      if (pre[i]==item){
        return i;
      }
    }
    return -1;
  }
  public boolean containsPre(Item item){
    return indexOfPre(item)!=-1;
  }
  public int indexOfPost(Item item){
    for (int i=0;i<postLen;i++){
      if (post[i]==item){
        return i;
      }
    }
    return -1;
  }
  public boolean containsPost(Item item){
    return indexOfPost(item)!=-1;
  }
  /**
   * Whether the preconditions are satisfied
   */
  public boolean satisfied(){
    Item ref;
    for (ref=parent;ref!=null;ref=ref.parent){
      if (!ref.selected){
        return false;
      }
    }
    Item par;
    byte mod;
    boolean b;
    for (int i=0;i<preLen;i++){
      mod = preMod[i];
      par = pre[i];
      if ((mod&LOCKED)==LOCKED && !par.locked || (mod&NOT_LOCKED)==NOT_LOCKED && par.locked){
        return false;
      }
      if ((mod&IGNORE_PARENTS)==IGNORE_PARENTS){
        if ((mod&SELECTED)==SELECTED && !par.selected || (mod&NOT_SELECTED)==NOT_SELECTED && par.selected || (mod&VISIBLE)==VISIBLE && !par.visible || (mod&NOT_VISIBLE)==NOT_VISIBLE && par.visible){
          return false;
        }
      }else{
        if ((mod&(SELECTED|NOT_SELECTED))!=0){
          b = par.valid();
          if ((mod&SELECTED)==SELECTED && !b || (mod&NOT_SELECTED)==NOT_SELECTED && b){
            return false;
          }
        }
        if ((mod&(VISIBLE|NOT_VISIBLE))!=0){
          b = par.onScreen();
          if ((mod&VISIBLE)==VISIBLE && !b || (mod&NOT_VISIBLE)==NOT_VISIBLE && b){
            return false;
          }
        }
      }
    }
    return preExpression==null?true:Utilities.evaluateExpression(Utilities.expandTernary(Utilities.expandProperties(parent,preExpression)));
  }
  /**
   * Returns whether any modifications were made
   */
  public boolean act(){
    if (!satisfied()){
      return false;
    }
    Item item;
    byte mod;
    boolean ret = false;
    boolean parents;
    for (int i=0;i<postLen;i++){
      item = post[i];
      mod = postMod[i];
      parents = (mod&IGNORE_PARENTS)==0;
      if ((mod&LOCKED)==LOCKED){
        if (!item.locked){
          ret = true;
          item.locked = true;
        }
      }else if ((mod&NOT_LOCKED)==NOT_LOCKED){
        if (item.locked){
          ret = true;
          item.locked = false;
        }
      }
      if ((mod&VISIBLE)==VISIBLE){
        if (parents){
          if (item.parent!=null){
            ret|=select(item.parent,true);
          }
        }
        ret|=makeVisible(item);
      }else if ((mod&NOT_VISIBLE)==NOT_VISIBLE){
        if (item.visible){
          ret = true;
          item.visible = false;
        }
      }
      if ((mod&SELECTED)==SELECTED){
        ret|=select(item,parents);
      }else if ((mod&NOT_SELECTED)==NOT_SELECTED){
        if (item.selected){
          ret = true;
          item.selected = false;
          //Updates ItemGroups
          for (int j=0;j<item.parent.groupLen;j++){
            if (item.parent.groups[j].select(item)){
              break;
            }
          }
        }
      }
    }
    if (postExpression!=null && Utilities.setProperties(parent, Utilities.expandProperties(parent, postExpression))){
      ret = true;
    }
    return ret;
  }
  private static boolean makeVisible(Item item){
    if (item.parent==null){
      return false;
    }
    boolean ret = false;
    if (!item.visible){
      //Check to see if this item is an unselected member of a maxed group, in which case this item cannot be made visible
      boolean modify = true;
      if (!item.selected){
        for (int j=0;j<item.parent.groupLen;j++){
          if (item.parent.groups[j].contains(item)){
            if (item.parent.groups[j].num==item.parent.groups[j].max){
              modify = false;
            }
            break;
          }
        }
      }
      if (modify && allowVisibility(ACESEquipmentBuilder.lib, item)){
        ret = true;
        item.visible = true;
      }
    }
    return ret||makeVisible(item.parent);
  }
  private static boolean select(Item item, boolean parents){
    int changes = 0;
    do {
      if (!item.selected){
        ++changes;
        item.selected = true;
        if (item.parent!=null){
          //Updates ItemGroups
          ItemGroup grp;
          for (int j=0;j<item.parent.groupLen;j++){
            grp = item.parent.groups[j];
            if (grp.contains(item)){
              if (grp.num==grp.max){
                //Deselects another item when a group is already at its maximum
                boolean fail = true;
                for (int k=0;k<grp.len;k++){
                  Item grpItem = grp.items[k];
                  if (grpItem.selected && item!=grpItem && allowDeselect(ACESEquipmentBuilder.lib, grpItem)){
                    grpItem.selected = false;
                    grpItem.visible = false;
                    if (allowVisibility(ACESEquipmentBuilder.lib, item)){
                      item.visible = true;
                    }
                    fail = false;
                    break;
                  }
                }
                if (fail){
                  item.selected = false;
                  --changes;
                }
              }else{
                grp.num++;
                grp.check();
              }
              break;
            }
          }
        }
      }
      item = item.parent;
    } while (parents && item!=null);
    return changes>0;
  }
  public static boolean allowDeselect(Item a, Item b){
    if (!a.selected){
      return true;
    }
    SuggestGroup grp;
    Item item;
    int i,j,k;
    for (i=0;i<a.sugLen;++i){
      grp = a.suggestGroups[i];
      if (grp.satisfied()){
        for (j=0;j<grp.postLen;++j){
          k = grp.postMod[j];
          if ((k&SELECTED)==SELECTED){
            item = grp.post[j];
            if ((k&IGNORE_PARENTS)==IGNORE_PARENTS){
              if (item==b){
                return false;
              }
            }else{
              for (;item!=null;item=item.parent){
                if (item==b){
                  return false;
                }
              }
            }
          }
        }
      }
    }
    for (i=0;i<a.len;++i){
      if (!allowDeselect(a.items[i],b)){
        return false;
      }
    }
    return true;
  }
  public static boolean allowVisibility(Item a, Item b){
    if (!a.selected){
      return true;
    }
    int i,j;
    SuggestGroup grp;
    for (i=0;i<a.sugLen;++i){
      grp = a.suggestGroups[i];
      if (grp.satisfied()){
        for (j=0;j<grp.postLen;++j){
          if (grp.post[j]==b && (grp.postMod[j]&NOT_VISIBLE)==NOT_VISIBLE){
            return false;
          }
        }
      }
    }
    for (i=0;i<a.len;++i){
      if (!allowVisibility(a.items[i],b)){
        return false;
      }
    }
    return true;
  }
}