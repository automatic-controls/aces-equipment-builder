/*
  Copyright (c) 2021, Automatic Controls Equipment Systems, Inc.
  Author: Cameron Vogt (@cvogt729)
*/
import java.text.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
/**
 * Thread-safe namespace which controls logging operations.
 * <p>Appends a timestamp to most log entries.
 */
public class Logger {
  private final static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
  private final static String separator = " - ";
  private volatile static PrintWriter out = null;
  private volatile static File f = null;
  private volatile static File tmp = null;
  /**
   * Initialization method.
   * @param file is the log file to write to.
   */
  public synchronized static void init(Path logFile) throws IOException {
    if (!Files.exists(logFile)){
      logFile = Files.createFile(logFile);
    }
    f = logFile.toFile();
    tmp = logFile.resolveSibling("tmp_"+f.getName()).toFile();
    out = new PrintWriter(new FileWriter(f,true));
  }
  /**
   * Logs and timestamps a message.
   * @param str is the message to log
   */
  public static void log(String str){
    log(str, new Date());
  }
  /**
   * Logs and timestamps a message.
   * @param str is the message to log
   * @param d is the timestamp for the message
   */
  public synchronized static void log(String str, Date d){
    out.print(format.format(d));
    out.print(separator);
    out.print(str);
    out.println();
    out.flush();
  }
  /**
   * Logs and timestamps an error with the given description.
   * @param desc is the description of the error to log.
   * @param e is the error to log.
   */
  public static void log(String desc, Throwable e){
    log(desc,e,new Date());
  }
  /**
   * Logs and timestamps an error with the given description.
   * @param desc is the description of the error to log.
   * @param e is the error to log.
   * @param d is the timestamp for the message
   */
  public synchronized static void log(String desc, Throwable e, Date d){
    out.print(format.format(d));
    out.print(separator);
    out.print(desc);
    out.println();
    e.printStackTrace(out);
    out.flush();
  }
  /**
   * Logs an error.
   * If you want a timestamp, you should use {@link #log(String,Throwable)} instead.
   * @param e is the error to log.
   */
  public synchronized static void log(Throwable e){
    e.printStackTrace(out);
    out.flush();
  }
  /**
   * Indicates whether {@link #init(File)} has been called.
   */
  public static boolean isInitialized(){
    return out!=null;
  }
  /**
   * Closed the logger output stream.
   */
  public synchronized static void close(){
    out.close();
  }
  /**
   * Gets all bytes stored in the log file.
   */
  public synchronized static byte[] getBytes() throws Exception {
    try{
      out.close();
      return Files.readAllBytes(f.toPath());
    }finally{
      out = new PrintWriter(new FileWriter(f,true));
    }
  }
  /**
   * Deletes log entries which occurred more than {@code deleteLogAfter} milliseconds ago.
   */
  public synchronized static void trim(long deleteLogAfter){
    try{
      long time = System.currentTimeMillis()-deleteLogAfter;
      if (!tmp.exists() && !tmp.createNewFile()){
        throw new IOException("Unable to create temporary log file.");
      }
      out.close();
      try{
        try (
          BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
          BufferedOutputStream outTMP = new BufferedOutputStream(new FileOutputStream(tmp, false));
        ){
          StringBuilder line = new StringBuilder(20);
          String str;
          int i;
          char c;
          boolean b;
          while (true){
            i = in.read();
            if (i==-1){
              break;
            }
            c = (char)i;
            if (c=='-'){
              b = false;
              str = line.toString();
              line.setLength(0);
              try{
                b = time<format.parse(str).getTime();
              }catch(ParseException e){}
              if (b){
                outTMP.write(str.getBytes());
                outTMP.write(i);
                byte[] buf = new byte[2048];
                while (true){
                  i = in.read(buf);
                  if (i==-1){
                    break;
                  }
                  outTMP.write(buf,0,i);
                }
                break;
              }else{
                do {
                  i = in.read();
                  c = (char)i;
                } while(i!=-1 && c!='\n');
                if (i==-1){
                  break;
                }
              }
            }else if (c=='\n'){
              line.setLength(0);
            }else{
              line.append(c);
            }
          }
        }
        if (!f.delete()){
          throw new IOException("Unable to delete log file.");
        }
        if (!tmp.renameTo(f)){
          throw new IOException("Unable to rename temporary log file.");
        }
      } finally {
        out = new PrintWriter(new FileWriter(f,true));
      }
    }catch(Exception e){
      Logger.log("Error occurred while deleting expired log entries.", e);
    }
  }
}