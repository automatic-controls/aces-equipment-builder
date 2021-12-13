/*
  BSD 3-Clause License
  Copyright (c) 2021, Automatic Controls Equipment Systems, Inc.
  Author: Cameron Vogt (@cvogt729)
*/
public class Container<T> {
  public volatile T x = null;
  public Container(){}
  public Container(T x){
    this.x = x;
  }
}