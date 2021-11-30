import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
/**
 * Contains various utility functions used throughout the program
 */
public class Utilities {
  /**
   * Loads an image resource
   */
  public static ImageIcon load(String str){
    try{
      return new ImageIcon(Utilities.class.getClassLoader().getResource(str));
    }catch(Exception e){
      ACESEquipmentBuilder.error("Failed to load image resource "+str, false, e);
      return null;
    }
  }
  public static StringBuilder escape(CharSequence seq){
    return escape(seq, 0, seq.length());
  }
  public static StringBuilder escape(CharSequence seq, int start, int end){
    final StringBuilder sb = (seq instanceof StringBuilder)?(StringBuilder)seq:new StringBuilder(seq);
    for (int i=start;i<end;++i){
      if (sb.charAt(i)=='\\'){
        sb.deleteCharAt(i);
        --end;
      }
    }
    return sb;
  }
  public static StringBuilder expandUnicode(CharSequence seq){
    return expandUnicode(seq, 0, seq.length());
  }
  public static StringBuilder expandUnicode(CharSequence seq, int start, int end){
    final StringBuilder sb = (seq instanceof StringBuilder)?(StringBuilder)seq:new StringBuilder(seq);
    int j;
    char c;
    boolean found;
    for (int i=start;i<end;++i){
      switch (sb.charAt(i)){
        case '\\':
          ++i;
          break;
        case '{':
          j = i;
          found = false;
          for (++i;i<end;++i){
            c = sb.charAt(i);
            if (c=='}'){
              found = true;
              break;
            }else if (Character.digit(c,16)==-1){
              break;
            }
          }
          if (found){
            try{
              sb.replace(j,i+1,String.valueOf((char)Integer.parseInt(sb.substring(j+1,i),16)));
              end+=j-i;
              i = j;
            }catch(Exception e){}
          }
          break;
      }
    }
    return sb;
  }
  public static boolean setProperties(Item base, CharSequence seq){
    return setProperties(base, seq, 0, seq.length());
  }
  public static boolean setProperties(Item base, CharSequence seq, int start, int end){
    if (base==null){
      base = ACESEquipmentBuilder.lib;
    }
    boolean ret = false;
    Matcher m = Patterns.setProperty.matcher(seq);
    m.region(start,end);
    Item item;
    while (m.find()){
      item = base.find(m.group(1).trim());
      if (item!=null){
        ret|=item.setProperty(m.group(2), m.group(3));
      }
    }
    return ret;
  }
  public static String expandProperties(Item base, CharSequence seq){
    return expandProperties(base, seq, 0, seq.length());
  }
  public static String expandProperties(Item base, CharSequence seq, int start, int end){
    if (base==null){
      base = ACESEquipmentBuilder.lib;
    }
    final Item ref = base;
    Matcher m = Patterns.getProperty.matcher(seq);
    m.region(start,end);
    return m.replaceAll(new java.util.function.Function<MatchResult,String>(){
      @Override public String apply(MatchResult r){
        String repl = null;
        Item item = ref.find(r.group(1).trim());
        if (item!=null){
          repl = item.getProperty(r.group(2));
        }
        if (repl==null){
          repl = "0";
        }else if (repl.indexOf('[')!=-1 || repl.indexOf(']')!=-1 || repl.indexOf('?')!=-1 || repl.indexOf(':')!=-1){
          StringBuilder sb = new StringBuilder(repl.length()+8);
          int len = repl.length();
          char c;
          for (int i=0;i<len;++i){
            c = repl.charAt(i);
            if (c=='[' || c==']' || c=='?' || c==':'){
              sb.append('\\');
            }
            sb.append(c);
          }
          repl = sb.toString();
        }
        return Matcher.quoteReplacement(repl);
      }
    });
  }
  public static StringBuilder expandTernary(CharSequence seq){
    return expandTernary(seq, 0, seq.length());
  }
  public static StringBuilder expandTernary(CharSequence seq, int start, int end){
    final StringBuilder sb = (seq instanceof StringBuilder)?(StringBuilder)seq:new StringBuilder(seq);
    char c;
    String repl;
    int i,j,k,l;
    boolean loop = true;
    while (loop){
      loop = false;
      outer:{
        for (i=start;i<end;++i){
          c = sb.charAt(i);
          if (c=='\\'){
            ++i;
          }else if (c=='['){
            for (j=i+1;j<end;++j){
              inner:{
                c = sb.charAt(j);
                if (c=='\\'){
                  ++j;
                }else if (c=='?'){
                  for (k=j+1;k<end;++k){
                    c = sb.charAt(k);
                    if (c=='\\'){
                      ++k;
                    }else if (c==':'){
                      for (l=k+1;l<end;++l){
                        c = sb.charAt(l);
                        if (c=='\\'){
                          ++l;
                        }else if (c==']'){
                          //   i j k l
                          //   [ ? : ]
                          repl = evaluateExpression(sb,i+1,j)?sb.substring(j+1,k):sb.substring(k+1,l);
                          sb.replace(i, l+1, repl);
                          end+=repl.length()+i-l-1;
                          loop = true;
                          break outer;
                        }else if (c=='['){
                          i = l;
                          j = l;
                          break inner;
                        }
                      }
                      break;
                    }else if (c=='['){
                      i = k;
                      j = k;
                      break inner;
                    }
                  }
                  break;
                }else if (c=='['){
                  i = j;
                }
              }
            }
            break;
          }
        }
        break;
      }
    }
    return sb;
  }
  private static class Node {
    public Node next = null;
    public boolean val;
    public boolean AND;
    public Node(Node next, boolean AND, boolean val){
      this.next = next;
      this.AND = AND;
      this.val = val;
    }
    public void evaluate(){
      for (Node n=this;n.next!=null;){
        if (n.AND){
          n.exec();
        }else{
          n = n.next;
        }
      }
      while (next!=null){
        exec();
      }
    }
    private void exec(){
      if (next!=null){
        val = AND?val&&next.val:val||next.val;
        AND = next.AND;
        next = next.next;
      }
    }
  }
  public static boolean evaluateExpression(CharSequence seq){
    return evaluateExpression(seq,0,seq.length());
  }
  public static boolean evaluateExpression(CharSequence seq, int start, int end){
    Node root = null;
    char c;
    int i,j,k;
    boolean AND = true;
    boolean NOT = false;
    for (i=start;i<end;++i){
      c = seq.charAt(i);
      if (c=='&'){
        AND = true;
      }else if (c=='|'){
        AND = false;
      }else if (c=='!'){
        NOT = true;
      }else if (c=='('){
        k = 1;
        j = i;
        for (++i;i<end;++i){
          c = seq.charAt(i);
          if (c=='('){
            ++k;
          }else if (c==')'){
            --k;
            if (k==0){
              root = new Node(root, AND, NOT^evaluateExpression(seq,j+1,i));
              NOT = false;
              break;
            }
          }
        }
      }else if (c!=' '){
        j = i;
        for (++i;i<end;++i){
          c = seq.charAt(i);
          if (c=='&' || c=='|'){
            break;
          }
        }
        root = new Node(root, AND, evaluateSubexpression(seq,j,i));
      }
    }
    if (root!=null){
      root.evaluate();
      return root.val;
    }
    return false;
  }
  public static boolean evaluateSubexpression(CharSequence seq){
    return evaluateSubexpression(seq,0,seq.length());
  }
  public static boolean evaluateSubexpression(CharSequence seq, int start, int end){
    int x;
    char c;
    int i = -1;
    for (x=start;x<end;++x){
      c = seq.charAt(x);
      if (c>='0' && c<='9' || c=='-'){
        i = x;
        break;
      }
    }
    if (i==-1){
      return false;
    }
    for (++x;x<end;++x){
      c = seq.charAt(x);
      if (c<'0' || c>'9'){
        break;
      }
    }
    try{
      i = Integer.parseInt(seq.subSequence(i,x).toString());
    }catch(Exception e){
      return false;
    }
    int op = 0;
    for (;x<end;++x){
      c = seq.charAt(x);
      if (c=='='){
        op|=1;
      }else if (c=='<'){
        op = 2;
      }else if (c=='>'){
        op = 4;
      }else if (c=='!'){
        op = 6;
      }else if (c!=' '){
        break;
      }
    }
    if (op==0){
      return i!=0;
    }
    int j = -1;
    for (;x<end;++x){
      c = seq.charAt(x);
      if (c>='0' && c<='9' || c=='-'){
        j = x;
        break;
      }
    }
    if (j==-1){
      return false;
    }
    for (++x;x<end;++x){
      c = seq.charAt(x);
      if (c<'0' || c>'9'){
        break;
      }
    }
    try{
      j = Integer.parseInt(seq.subSequence(j,x).toString());
    }catch(Exception e){
      return false;
    }
    switch (op){
      case 1: return i==j;
      case 2: return i<j;
      case 3: return i<=j;
      case 4: return i>j;
      case 5: return i>=j;
      case 7: return i!=j;
    }
    return false;
  }
  /**
   * Replaces matches of the regular expression {@code %.*?%} with the corresponding environment variable.
   */
  public static String expandEnvironmentVariables(String str){
    int len = str.length();
    StringBuilder out = new StringBuilder(len);
    StringBuilder var = new StringBuilder();
    String tmp;
    boolean env = false;
    char c;
    for (int i=0;i<len;++i){
      c = str.charAt(i);
      if (c=='%'){
        if (env){
          tmp = System.getenv(var.toString());
          if (tmp!=null){
            out.append(tmp);
            tmp = null;
          }
          var.setLength(0);
        }
        env^=true;
      }else if (env){
        var.append(c);
      }else{
        out.append(c);
      }
    }
    return out.toString();
  }
  /**
   * Assumes {@code src} and the parent of {@code dst} both exist.
   * Ensures the contenst of {@code dst} exactly match the contents of {@code src}.
   * The last modified timestamp is used to determine whether two files of the same relative path are the same.
   */
  public static void copy(final Path src, final Path dst) throws IOException {
    if (Files.isDirectory(src)){
      boolean copyNew = false;
      if (Files.exists(dst)){
        if (Files.isDirectory(dst)){
          LinkedList<Path> arr = new LinkedList<Path>();
          try(
            DirectoryStream<Path> stream = Files.newDirectoryStream(dst);
          ){
            for (Path p:stream){
              arr.add(p);
            }
          }
          LinkedList<CopyEvent> copies = new LinkedList<CopyEvent>();
          try(
            DirectoryStream<Path> stream = Files.newDirectoryStream(src);
          ){
            Path p;
            for (Path a:stream){
              p = dst.resolve(a.getFileName());
              arr.remove(p);
              copies.add(new CopyEvent(a,p));
            }
          }
          for (Path p:arr){
            deleteTree(p);
          }
          arr = null;
          for (CopyEvent e:copies){
            e.exec();
          }
        }else{
          Files.delete(dst);
          copyNew = true;
        }
      }else{
        copyNew = true;
      }
      if (copyNew){
        Logger.log("Copied \""+src.toString()+'"');
        Files.walkFileTree(src, new SimpleFileVisitor<Path>(){
          @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Files.createDirectory(dst.resolve(src.relativize(dir)));
            return FileVisitResult.CONTINUE;
          }
          @Override public FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
            Path p = dst.resolve(src.relativize(file));
            Files.copy(file, p);
            Files.setLastModifiedTime(p, Files.getLastModifiedTime(file));
            return FileVisitResult.CONTINUE;
          }
        });
      }
    }else{
      FileTime srcTime = Files.getLastModifiedTime(src);
      if (Files.exists(dst)){
        if (Files.isDirectory(dst)){
          deleteTree(dst);
          Files.copy(src, dst);
          Files.setLastModifiedTime(dst, srcTime);
          Logger.log("Copied \""+src.toString()+'"');
        }else if (Files.getLastModifiedTime(dst).compareTo(srcTime)!=0){
          Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
          Files.setLastModifiedTime(dst, srcTime);
          Logger.log("Copied \""+src.toString()+'"');
        }
      }else{
        Files.copy(src, dst);
        Files.setLastModifiedTime(dst, srcTime);
        Logger.log("Copied \""+src.toString()+'"');
      }
    }
  }
  /**
   * Recursively deletes a file tree.
   * @param root is the root of the tree to be deleted.
   * @return {@code true} if successful; {@code false} otherwise.
   */
  public static void deleteTree(Path root) throws IOException {
    Files.walkFileTree(root, new SimpleFileVisitor<Path>(){
      @Override public FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }
      @Override public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
        if (e==null){
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }else{
          throw e;
        }
      }
    });
  }
  private static class CopyEvent {
    public Path src;
    public Path dst;
    public CopyEvent(Path src, Path dst){
      this.src = src;
      this.dst = dst;
    }
    public void exec() throws IOException {
      Utilities.copy(src,dst);
    }
  }
}