/*
  Copyright (c) 2021, Automatic Controls Equipment Systems, Inc.
  Author: Cameron Vogt (@cvogt729)
*/
import java.util.regex.*;
public class Patterns {
  public volatile static Pattern webctrl;
  public volatile static Pattern group;
  public volatile static Pattern condition;
  public volatile static Pattern getProperty;
  public volatile static Pattern setProperty;
  public volatile static Pattern refname;
  public static void init(){
    refname = Pattern.compile("^[a-zA-Z0-9_ ]++$");
    webctrl = Pattern.compile("WebCTRL(\\d++\\.\\d++)");
    group = Pattern.compile("\\s*+Group\\(\\s*+(\\d++)\\s*+,\\s*+(\\d++)\\s*+\\)\\s*+", Pattern.CASE_INSENSITIVE);
    condition = Pattern.compile("Condition\\(\\s*+([^,\"]++),\\s*+(?>([^,\"]++),\\s*+)?\"((?>\\\\.|[^\"])*+)\"\\s*+\\)");
    getProperty = Pattern.compile("(?<![^\\\\]\\\\(?>\\\\\\\\){0,10}+)<\\s*+([^<>\\|]*+)\\|\\s*+([a-zA-Z@]++)\\s*+>");
    setProperty = Pattern.compile("(?<![^\\\\]\\\\(?>\\\\\\\\){0,10}+)<\\s*+([^<>\\|]*+)\\|\\s*+([a-zA-Z]++)\\s*+=\\s*+(\\d++)\\s*+>");
  }
}