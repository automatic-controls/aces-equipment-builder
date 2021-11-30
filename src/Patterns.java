import java.util.regex.*;
public class Patterns {
  public volatile static Pattern webctrl;
  public volatile static Pattern group;
  public volatile static Pattern condition;
  public volatile static Pattern getProperty;
  public volatile static Pattern setProperty;
  public static void init(){
    webctrl = Pattern.compile("WebCTRL(\\d++\\.\\d++)");
    group = Pattern.compile("\\s*+Group\\(\\s*+(\\d++)\\s*+,\\s*+(\\d++)\\s*+\\)\\s*+", Pattern.CASE_INSENSITIVE);
    condition = Pattern.compile("Condition\\(\\s*+([^,\"]++),\\s*+(?>([^,\"]++),\\s*+)?\"((?>\\\\.|[^\"])*+)\"\\s*+\\)");
    getProperty = Pattern.compile("(?<![^\\\\]\\\\(?>\\\\\\\\){0,10}+)<\\s*+([^<>\\|]*+)\\|\\s*+([a-zA-Z@]++)\\s*+>");
    setProperty = Pattern.compile("(?<![^\\\\]\\\\(?>\\\\\\\\){0,10}+)<\\s*+([^<>\\|]*+)\\|\\s*+([a-zA-Z]++)\\s*+=\\s*+(\\d++)\\s*+>");
  }
}