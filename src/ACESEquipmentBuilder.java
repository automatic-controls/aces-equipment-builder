import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.util.regex.*;
public class ACESEquipmentBuilder {
  /** Used to determine whether updates are available */
  private final static double VERSION = 1.14;
  public final static String lineSeparator = System.lineSeparator();
  public final static String configName = "config.aceseb";
  private static ImageIcon icon;
  private static ImageIcon helpIcon;
  private static String scriptPlugin;
  private static Path installation = null;
  private static Path localLib = null;
  public static int libSubstringPos = 0;
  private static Path mainConfig = null;
  private static String WebCTRL = null;
  private static Path localFavorites = null;
  private static Path localFavoritesFile = null;
  private static Path localPlugins = null;
  private static Path localScripts = null;
  private static Path localScriptsFile = null;
  private static Path genScriptFile = null;
  private static Path aces = null;
  private static Path acesLib = null;
  private static Path acesFavorites = null;
  private static Path acesScripts = null;
  private static Path acesConfig = null;
  private static File logFile = null;
  /** This is the root item containing all other items */
  public static Folder lib = null;
  /** This is the primary GUI */
  public final static JFrame Main = new JFrame("ACES Equipment Builder");
  public final static JLabel MLabel = new JLabel();
  private static JMenuItem popupEikon;
  private static JFileChooser webctrlDirChooser;
  private static FontMetrics metrics = null;
  public static JTextField equipmentNameInput;
  private static JButton generateScriptButton;
  private static JLabel helpButton;
  private static JButton feedbackButton;
  public final static int fontSize = 20;
  private final static int boxHeight = fontSize+8;
  private final static int buttonWidth = 180;
  private final static int buttonPadding = 10;
  private final static int initialY = 13;
  private static int helpButtonSize;
  private static int helpButtonInitialY;
  private final static int rightPad = 15;
  public static Font font;
  public final static Color foreground = Color.BLACK;
  public final static Color background = Color.WHITE;
  /** The color to use to highlight the help button when the mouse hovers over it */
  private final static Color helpHighlightColor = new Color(220, 220, 255);
  private final static Color scrollBarColor = Color.GRAY;
  /** initial width of program window */
  public static int w = 1000;
  /** initial height of program window */
  private static int h = 800;
  /** Double buffering is used to prevent GUI flashes */
  private static BufferedImage internalImage = null;
  public static int imageW = 0;
  private static int imageH = 0;
  private static double xOffset = 0;
  private static double yOffset = 0;
  private static int wDif = 0;
  private static int hDif = 0;
  private final static int topPadding = initialY+37;
  private final static int bottomPadding = 45;
  private final static int leftPadding = 2;
  private final static int rightPadding = 18;
  private final static int stringStart = 7-leftPadding;
  /** Increase this to increase the default scroll speed */
  private final static int scrollFactor = 10;
  /** Increase this to increase the minimum scroll speed */
  private final static int scrollMinimum = 25;
  private final static int scrollWidth = 15;
  private final static int scrollLength = 80;
  private final static int vScrollX = 18+scrollWidth;
  private final static int hScrollY = 41+scrollWidth;
  private static int vScrollY = 0;
  private static int hScrollX = 0;
  private static int dragX = 0;
  private static int dragY = 0;
  private static boolean vScroll = false;
  private static boolean hScroll = false;
  private static boolean paint = true;
  private static boolean autoUpdate = true;
  private static boolean updateLibrary = true;
  private static boolean updateFavorites = true;
  private static boolean updateScripts = true;
  private static boolean suggestEntries = true;
  private static boolean groupMin = true;
  private static boolean groupMax = true;
  private static boolean hideEntries = true;
  private static boolean lockedEntries = true;
  private static boolean defaultSelections = true;
  private static String supportMessage = "";
  private static boolean devMode = false;
  private static File helpFile = null;
  private static File regexHelpFile = null;
  /** Helps to control functions which should be called when the program exits */
  private static volatile boolean onExitCalled = false;
  private static int mouseX = 0;
  private static int mouseY = 0;
  private static boolean mouseValid = false;
  /** Controls the shortcut: CTRL+D+E+V */
  private static byte devCTRL = 0;
  private static boolean allowUpdate = true;
  private static String emailTo = "";
  private static String systemDrive;
  public static void main(String[] args){
    {
      Exception error = null;
      try{
        //Makes the GUI look more like the native system format
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }catch(Exception e){
        error = e;
      }
      try{
        installation = Paths.get(ACESEquipmentBuilder.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        Path p = installation.resolve("log.txt");
        if (!Files.exists(p)){
          Files.createFile(p);
        }
        Logger.init(p);
        logFile = p.toFile();
      }catch(Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Critical startup error occurred - "+e.getClass().getSimpleName(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (error!=null){
        Logger.log(error);
      }
    }
    JFrame loader = new JFrame("Initializing...");
    {
      JLabel lbl = new JLabel();
      icon = Utilities.load("Icon.png");
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      loader.setIconImage(icon.getImage());
      loader.setBounds((screen.width>>1)-150, (screen.height>>1)-50, 300, 100);
      loader.setUndecorated(true);
      loader.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      lbl.setLocation(0,0);
      loader.setBackground(Color.WHITE);
      lbl.setBackground(Color.WHITE);
      lbl.setHorizontalAlignment(JLabel.LEFT);
      lbl.setVerticalAlignment(JLabel.TOP);
      {
        BufferedImage img = new BufferedImage(300, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,300,100);
        g.drawImage(icon.getImage(), 0, 0, null);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 40));
        g.drawString("Initializing", 110, 60);
        g.dispose();
        lbl.setIcon(new ImageIcon(img));
      }
      loader.getContentPane().add(lbl);
      loader.setVisible(true);
    }
    
    Logger.trim(604800000);
    Patterns.init();
    font = new Font("Times New Roman", Font.PLAIN, fontSize);
    localLib = installation.resolve("lib");
    mainConfig = installation.resolve("config.txt");
    helpFile = installation.resolve("docs\\ACES Equipment Builder.pdf").toFile();
    regexHelpFile = installation.resolve("docs\\Regular Expressions.pdf").toFile();
    libSubstringPos = localLib.toString().length()+1;
    scriptPlugin = "<plugin path=\"./extras/add-ons/script.logic-plugin\" class-name=\"com.automatedlogic.green.plugin.logicbuilder.script.LogicBuilderMacroPlugin\" />";
    helpIcon = Utilities.load("Help Icon.png");
    helpButtonSize = helpIcon.getIconWidth();
    helpButtonInitialY = initialY+(boxHeight>>1)-(helpButtonSize>>1);
    equipmentNameInput = new JTextField();
    generateScriptButton = new JButton("Generate Script");
    helpButton = new JLabel(helpIcon);
    feedbackButton = new JButton("Contact Engineering");
    lib = new Folder(localLib.toFile());
    systemDrive = System.getenv("SystemDrive");
    if (systemDrive==null){
      webctrlDirChooser = new JFileChooser();
    }else{
      systemDrive+='\\';
      webctrlDirChooser = new JFileChooser(systemDrive);
    }
    webctrlDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    webctrlDirChooser.setMultiSelectionEnabled(false);
    webctrlDirChooser.setDialogTitle("Choose Your WebCTRL Installation Directory");
    final Container<Item> popupItem = new Container<Item>();
    final JPopupMenu popup = new JPopupMenu();
    final JMenuItem popupSync = new JMenuItem("Synchronize Library");
    final JMenuItem popupLoad = new JMenuItem("Reload Library");
    final JMenuItem popupEditConfig = new JMenuItem("Edit Configuration Options");
    final JMenuItem popupLog = new JMenuItem("Open Log File");
    final JMenuItem popupRebind = new JMenuItem("Rebind WebCTRL");
    final JMenuItem popupRegex = new JMenuItem();
    final JMenuItem popupOpen = new JMenuItem("Open");
    final JMenuItem popupConfigure = new JMenuItem("Configure");
    final JMenuItem popupDelete = new JMenuItem("Delete");
    final JMenuItem popupReferAll = new JMenuItem("Find All References");
    final JMenuItem popupReferDirect = new JMenuItem("Find Direct References");
    popupEikon = new JMenuItem("Launch EIKON");
    popupSync.setIcon(Utilities.load("sync.png"));
    popupLoad.setIcon(Utilities.load("refresh.png"));
    popupEditConfig.setIcon(Utilities.load("edit.png"));
    popupConfigure.setIcon(popupEditConfig.getIcon());
    popupLog.setIcon(Utilities.load("open.png"));
    popupOpen.setIcon(popupLog.getIcon());
    popupDelete.setIcon(Utilities.load("delete.png"));
    popupRebind.setIcon(Utilities.load("bind.png"));
    popupRegex.setIcon(Utilities.load("findall.png"));
    popupReferDirect.setIcon(Utilities.load("search.png"));
    popupReferAll.setIcon(popupReferDirect.getIcon());
    popupEikon.setIcon(Utilities.load("eikon.png"));
    popupEikon.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        openEikon();
      }
    });
    popupRegex.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        if (popupItem.x==null){
          regex(acesLib);
        }else if (popupItem.x instanceof Symbol){
          regex(popupItem.x.origin.toPath().getParent());
        }else{
          regex(popupItem.x.origin.toPath());
        }
      }
    });
    popupReferAll.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        findReferences(popupItem.x, true);
      }
    });
    popupReferDirect.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        findReferences(popupItem.x, false);
      }
    });
    popupDelete.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        delete(popupItem.x);
      }
    });
    popupConfigure.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        openConfig(popupItem.x);
      }
    });
    popupOpen.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        open(popupItem.x);
      }
    });
    popupRebind.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        rebindWebCTRL(false);
      }
    });
    popupLog.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        openLog();
      }
    });
    popupEditConfig.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        editConfigOptions();
      }
    });
    popupSync.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        F5();
      }
    });
    popupLoad.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        F3();
      }
    });

    //Loads the configuration file and updates if necessary.
    if (loadConfig(false)){
      if (autoUpdate){
        update();
      }else if (aces!=null){
        loadACESConfig(aces);
      }
    }else{
      saveConfig(true);
    }
    Item.lastClicked = lib;
    lib.selected = true;
    try{
      doLoad();
    }catch(Exception e){
      error("Unable to load library.", e);
      lib.reset();
    }
    Logger.log("Application initialized.");
    loader.dispose();
    loader = null;

    Main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Main.setBounds(200, 100, w, h);
    Main.setIconImage(icon.getImage());
    MLabel.setLocation(0,0);
    Main.setBackground(Color.WHITE);
    MLabel.setBackground(Color.WHITE);
    MLabel.setHorizontalAlignment(JLabel.LEFT);
    MLabel.setVerticalAlignment(JLabel.TOP);
    equipmentNameInput.setFont(font);
    equipmentNameInput.setBackground(background);
    equipmentNameInput.setForeground(foreground);
    equipmentNameInput.setLocation(157, initialY);
    feedbackButton.setFont(new Font("Times New Roman", Font.ITALIC, 14));
    feedbackButton.setBackground(background);
    feedbackButton.setForeground(foreground);
    generateScriptButton.setFont(font);
    generateScriptButton.setBackground(background);
    generateScriptButton.setForeground(foreground);
    generateScriptButton.setSize(buttonWidth, boxHeight);
    helpButton.setBackground(background);
    helpButton.setSize(helpButtonSize, helpButtonSize);
    helpButton.setOpaque(true);
    Main.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        onExit();
      }
    });
    feedbackButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        if (!sendEmail(null)){
          if (emailTo.isEmpty()){
            error("Failed to generate email.");
          }else{
            error("Failed to generate email.\n"+emailTo);
          }
        }
      }
    });
    helpButton.addMouseListener(new MouseAdapter(){
      public void mouseEntered(MouseEvent e){
        helpButton.setBackground(helpHighlightColor);
      }
      public void mouseExited(MouseEvent e){
        helpButton.setBackground(background);
      }
      public void mousePressed(MouseEvent e){
        if (e.getButton()==MouseEvent.BUTTON1){
          if (helpFile==null || !helpFile.exists()){
            error("Cannot locate documentation!");
          }else{
            try{
              Desktop.getDesktop().open(helpFile);
            }catch(Exception err){
              error("Unable to open "+helpFile.getPath(), err);
            }
          }
        }
      }
    });
    generateScriptButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        if (generateScript()){
          info("Script generated!");
        }
      }
    });
    KeyAdapter keyAdapter = new KeyAdapter(){
      public void keyReleased(KeyEvent e){
        if (e.getKeyCode()==17){//CTRL
          devCTRL = 0;
        }
      }
      public void keyPressed(KeyEvent e){
        vScroll = false;
        hScroll = false;
        int key = e.getKeyCode();
        if (key==17){//CTRL
          devCTRL = 1;
        }else if (key==68 && devCTRL==1){//D
          devCTRL = 2;
          e.consume();
        }else if (key==69 && devCTRL==2){//E
          devCTRL = 3;
          e.consume();
        }else if (key==86 && devCTRL==3){//V
          devCTRL = 0;
          devMode^=true;
          e.consume();//Prevents CTRL+V from doing the usual paste command
          lib.reset();
          try{
            doLoad();
          }catch(Exception err){
            lib.reset();
            error("Unable to reload library.", err);
          }
          repaint();
          if (devMode){
            Main.setTitle("ACES Equipment Builder (Development Mode)");
            info("Developer mode activated!");
          }else{
            Main.setTitle("ACES Equipment Builder");
            info("Developer mode deactivated!");
          }
        }else{
          if (devCTRL!=0){
            devCTRL = 0;
          }
          if (key==116){//F5
            F5();
            e.consume();
          }else if (key==114){//F3
            F3();
            e.consume();
          }else if (key==79){//CTRL+O
            if (e.isControlDown()){
              editConfigOptions();
              e.consume();
            }
          }else if (key==75){//CTRL+K
            if (e.isControlDown()){
              rebindWebCTRL(e.isAltDown());
              e.consume();
            }
          }else if (key==76 && e.isControlDown()){//CTRL+L
            if (e.isControlDown()){
              openLog();
              e.consume();
            }
          }else if (key==70){//CTRL+F
            if (e.isControlDown() && devMode){
              regex(acesLib);
              e.consume();
            }
          }else if (key==69){//CTRL+E
            if (e.isControlDown()){
              openEikon();
              e.consume();
            }
          }else if (key==127){//Delete
            if (devMode && mouseValid && delete(lib.getItem(new Int(mouseY-topPadding-(int)(yOffset+0.5))))){
              repaint();
              e.consume();
            }
          }else if (key==KeyEvent.VK_UP || key==KeyEvent.VK_RIGHT){//Up or right arrow keys
            if (Item.lastClicked.valueChanger){
              int tmp = Item.lastClicked.value;
              Item.lastClicked.value+=Item.lastClicked.increment;
              if (Item.lastClicked.value>Item.lastClicked.max){
                Item.lastClicked.value = Item.lastClicked.max;
              }
              if (tmp!=Item.lastClicked.value){
                lib.evaluateSuggestions(new Int(0));
                repaint();
              }
              e.consume();
            }
          }else if (key==KeyEvent.VK_DOWN || key==KeyEvent.VK_LEFT){//Down or left arrow keys
            if (Item.lastClicked.valueChanger){
              int tmp = Item.lastClicked.value;
              Item.lastClicked.value-=Item.lastClicked.increment;
              if (Item.lastClicked.value<Item.lastClicked.min){
                Item.lastClicked.value = Item.lastClicked.min;
              }
              if (tmp!=Item.lastClicked.value){
                lib.evaluateSuggestions(new Int(0));
                repaint();
              }
              e.consume();
            }
          }/* else{
            info("KeyCode: "+key)
          }*/
        }
      }
    };
    Main.addKeyListener(keyAdapter);
    MLabel.addKeyListener(keyAdapter);
    equipmentNameInput.addKeyListener(keyAdapter);
    equipmentNameInput.addKeyListener(new KeyAdapter(){
      @Override public void keyPressed(KeyEvent e){
        Item.equipmentName = equipmentNameInput.getText();
      }
      @Override public void keyReleased(KeyEvent e){
        Item.equipmentName = equipmentNameInput.getText();
      }
    });
    Main.addComponentListener(new ComponentAdapter(){
      public void componentResized(ComponentEvent e){
        //Controls the location and sizes of various components on the GUI
        boolean update = w!=Main.getWidth();
        w = Main.getWidth();
        h = Main.getHeight();
        MLabel.setSize(w, h);
        if (update){
          int tmp = w-helpButtonSize-buttonPadding-rightPad;
          helpButton.setLocation(tmp, helpButtonInitialY);
          tmp-=buttonWidth+buttonPadding;
          generateScriptButton.setLocation(tmp,initialY);
          equipmentNameInput.setSize(tmp-157-buttonPadding, boxHeight);
          repaintInternal();
        }else{
          hDif = h-imageH-topPadding-bottomPadding;
        }
        repaintExternal();
      }
    });
    MLabel.addMouseWheelListener(new MouseWheelListener(){
      public void mouseWheelMoved(MouseWheelEvent e){
        if (hDif<0){
          double v = scrollFactor*e.getPreciseWheelRotation();
          if (v>0 && -v*hDif<scrollMinimum*(hDif+imageH-scrollLength)){
            v = -scrollMinimum*(hDif+imageH-scrollLength)/hDif;
          }else if (v<0 && v*hDif<scrollMinimum*(hDif+imageH-scrollLength)){
            v = scrollMinimum*(hDif+imageH-scrollLength)/hDif;
          }
          vScrollY+=(int)(v+0.5);
          if (vScrollY<topPadding){
            vScrollY = topPadding;
          }else{
            int tmp = h-scrollLength-bottomPadding;
            if (vScrollY>tmp){
              vScrollY = tmp;
            }
          }
          repaintExternal();
        }
      }
    });
    MLabel.addMouseListener(new MouseAdapter(){
      public void mousePressed(MouseEvent e){
        equipmentNameInput.requestFocus();
        mouseX = e.getX();
        mouseY = e.getY();
        if (hDif<0 && mouseX+vScrollX>=w && mouseX+vScrollX<=w+scrollWidth && mouseY>=vScrollY && mouseY<=vScrollY+scrollLength){
          vScroll = true;
          dragY = mouseY;
        }else if (wDif<0 && mouseX>=hScrollX && mouseX<=hScrollX+scrollLength && mouseY+hScrollY>=h && mouseY+hScrollY<=h+scrollWidth){
          hScroll = true;
          dragX = mouseX;
        }else{
          Item x = lib.getItem(new Int(mouseY-topPadding-(int)(yOffset+0.5)));
          if (e.getButton()==MouseEvent.BUTTON1){
            if (x!=null){
              x.doClick();
              repaintInternal();
              vScrollY = (int)(yOffset*(hDif+imageH-scrollLength)/hDif+0.5)+topPadding;
              repaintExternal();
            }
          }else{
            popupItem.x = x;
            popup.removeAll();
            if (x==null || !devMode){
              popup.add(popupSync);
              popup.add(popupLoad);
              popup.add(popupEikon);
              popup.add(popupEditConfig);
              popup.add(popupRebind);
              popup.add(popupLog);
              if (devMode){
                popupRegex.setText("Global Find/Replace");
                popup.add(popupRegex);
              }
            }else{
              popup.add(popupOpen);
              popup.add(popupConfigure);
              popup.add(popupDelete);
              popupRegex.setText("Find/Replace Within");
              popup.add(popupRegex);
              popup.add(popupReferAll);
              popup.add(popupReferDirect);
            }
            popup.show(e.getComponent(), e.getX(), e.getY());
          }
        }
      }
      public void mouseReleased(MouseEvent e){
        vScroll = false;
        hScroll = false;
        mouseX = e.getX();
        mouseY = e.getY();
      }
      public void mouseEntered(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();
        mouseValid = true;
      }
      public void mouseExited(MouseEvent e){
        mouseValid = false;
      }
    });
    MLabel.addMouseMotionListener(new MouseMotionAdapter(){
      public void mouseDragged(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();
        if (vScroll){
          vScrollY+=mouseY-dragY;
          dragY = mouseY;
          if (vScrollY<topPadding){
            vScrollY = topPadding;
          }else{
            int tmp = h-scrollLength-bottomPadding;
            if (vScrollY>tmp){
              vScrollY = tmp;
            }
          }
          repaintExternal();
        }else if (hScroll){
          hScrollX+=mouseX-dragX;
          dragX = mouseX;
          if (hScrollX<leftPadding){
            hScrollX = leftPadding;
          }else{
            int tmp = w-scrollLength-rightPadding;
            if (hScrollX>tmp){
              hScrollX = tmp;
            }
          }
          repaintExternal();
        }
      }
      public void mouseMoved(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();
      }
    });
    Main.getContentPane().add(MLabel);
    MLabel.add(equipmentNameInput);
    MLabel.add(generateScriptButton);
    MLabel.add(helpButton);
    w = 0;//Forces update on the fist componentResized event
    metrics = MLabel.getFontMetrics(font);
    Main.setVisible(true);
    focus();
  }
  /**
   * Attempts to focus equipmentNameInput
   */
  private static void focus(){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        equipmentNameInput.requestFocus();
      }
    });
  }
  /**
   * {@code false} adds the modifier {@code Pattern.LITERAL}
   */
  private static boolean regexUse = false;
  /**
   * {@code true} adds modifiers {@code Pattern.DOTALL} and {@code Pattern.MULTILINE}
   */
  private static boolean regexMultiLine = false;
  /**
   * {@code false} adds the modifier {@code Pattern.CASE_INSENSITIVE}
   */
  private static boolean regexMatchCase = true;
  /**
   * Whether to save changes.
   */
  private static boolean regexSave = true;
  /**
   * Whether to use the replace function.
   */
  private static boolean regexReplace = false;
  /**
   * The expression to be found.
   */
  private static String regexFindText = null;
  /**
   * The expression which replaces any matches.
   */
  private static String regexReplaceText = null;
  private static void regex(Path root){
    int i;
    {
      JButton openHelp = new JButton("Open Regex Documentation");
      JCheckBox regex = new JCheckBox("Use Regex", regexUse);
      JCheckBox multiLine = new JCheckBox("Multiline/Dotall Mode", regexMultiLine);
      JCheckBox matchCase = new JCheckBox("Case Sensitive", regexMatchCase);
      JCheckBox replace = new JCheckBox("Replace", regexReplace);
      JCheckBox save = new JCheckBox("Save Changes", regexSave);
      JTextField findText = new JTextField(regexFindText, 18);
      JTextField replaceText = new JTextField(regexReplaceText, 18);
      multiLine.setEnabled(regexUse);
      replaceText.setEditable(regexReplace);
      save.setEnabled(regexUse && regexReplace);
      regex.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          regexUse = regex.isSelected();
          multiLine.setEnabled(regexUse);
          if (!save.isEnabled() && regexUse && regexReplace){
            regexSave = false;
          }else{
            regexSave|=!regexUse||!regexReplace;
          }
          save.setEnabled(regexUse && regexReplace);
          save.setSelected(regexSave);
        }
      });
      replace.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          regexReplace = replace.isSelected();
          replaceText.setEditable(regexReplace);
          if (!save.isEnabled() && regexUse && regexReplace){
            regexSave = false;
          }else{
            regexSave|=!regexUse||!regexReplace;
          }
          save.setEnabled(regexUse && regexReplace);
          save.setSelected(regexSave);
        }
      });
      openHelp.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          if (regexHelpFile==null || !regexHelpFile.exists()){
            error("Cannot locate documentation!\nTry browsing to:\nhttps://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/regex/Pattern.html");
          }else{
            try{
              Desktop.getDesktop().open(regexHelpFile);
            }catch(Exception err){
              error("Unable to open "+regexHelpFile.getPath(), err);
            }
          }
        }
      });
      Object[] params = {openHelp,"Scope: \""+acesLib.relativize(root).toString()+"\\**\\"+configName+'"',regex, multiLine, matchCase, replace, save, "Find:", findText, "Replace:", replaceText};
      i = JOptionPane.showConfirmDialog(Main, params, "Configuration File Search Utility", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      regexUse = regex.isSelected();
      regexMultiLine = multiLine.isSelected();
      regexMatchCase = matchCase.isSelected();
      regexReplace = replace.isSelected();
      regexSave = save.isSelected();
      regexFindText = findText.getText();
      regexReplaceText = replaceText.getText();
    }
    if (i==JOptionPane.OK_OPTION){
      if (regexFindText==null || regexFindText.isEmpty()){
        error("Please specify the search expression.");
        return;
      }
      if (regexReplace && regexReplaceText==null){
        regexReplaceText = "";
      }
      final String replText = regexUse?regexReplaceText:Matcher.quoteReplacement(regexReplaceText);
      int flags = 0;
      if (!regexUse){
        flags|=Pattern.LITERAL;
      }
      if (regexMultiLine){
        flags|=Pattern.MULTILINE;
        flags|=Pattern.DOTALL;
      }
      if (!regexMatchCase){
        flags|=Pattern.CASE_INSENSITIVE;
      }
      final Container<Pattern> p = new Container<Pattern>();
      try{
        p.x = Pattern.compile(regexFindText, flags);
      }catch(Exception e){
        error("Error occurred while compiling pattern.\n"+e.getMessage(), e);
        return;
      }
      final StringBuilder sb = new StringBuilder(16);
      final Container<Integer> count = new Container<Integer>(0);
      try{
        if (Files.exists(root)){
          Files.walkFileTree(root, new SimpleFileVisitor<Path>(){
            @Override public FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
              if (file.getFileName().toString().equals(configName)){
                String data = new String(Files.readAllBytes(file));
                Matcher m = p.x.matcher(data);
                if (m.find()){
                  java.util.ArrayList<Integer> linePositions = new java.util.ArrayList<Integer>();
                  int i = -1;
                  while ((i = data.indexOf('\n', i+1))!=-1){
                    linePositions.add(i);
                  }
                  int c = count.x;
                  int line, len;
                  int last = 0;
                  StringBuilder sbb = new StringBuilder();
                  StringBuilder output = regexReplace?new StringBuilder():null;
                  do {
                    ++count.x;
                    line = java.util.Collections.binarySearch(linePositions, m.start());
                    if (line<0){
                      line = -line;
                    }else{
                      ++line;
                    }
                    if (regexUse){
                      if (regexReplace){
                        sbb.append("\n    Line ").append(line).append("\n        Original: ").append(m.group()).append("\n        Replacement: ");
                        len = output.length();
                        m.appendReplacement(output, replText);
                        sbb.append(output, len+m.start()-last, output.length());
                        last = m.end();
                        if (!regexSave){
                          output.setLength(0);
                        }
                      }else{
                        sbb.append("\n    Line ").append(line).append(": ").append(m.group());
                      }
                    }else{
                      sbb.append("\n    Line ").append(line);
                      if (regexReplace && regexSave){
                        m.appendReplacement(output, replText);
                      }
                    }
                  } while (m.find());
                  c = count.x-c;
                  sb.append(regexReplace && regexSave?"\n\nReplaced ":"\n\nFound ").append(c).append(c==1?" match in \"":" matches in \"").append(root.relativize(file).toString()).append('"').append(sbb);
                  if (regexReplace && regexSave){
                    m.appendTail(output);
                    ByteBuffer buf = ByteBuffer.wrap(output.toString().getBytes());
                    try(
                      FileChannel ch = FileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                    ){
                      while (buf.hasRemaining()){
                        ch.write(buf);
                      }
                    }
                  }
                }
              }
              return FileVisitResult.CONTINUE;
            }
          });
        }else{
          error("Cannot locate \""+root.toString()+'"');
          return;
        }
      }catch(Exception e){
        error("Error occurred while traversing remote library.", e);
      }
      info(count.x==0?("No matches in \""+root.toString()+'"'):((regexReplace && regexSave?"Replaced ":"Found ")+count.x+(count.x==1?" match in \"":" matches in \"")+root.toString()+'"'+sb.toString()));
    }
  }
  private static void findReferences(Item x, boolean indirect){
    if (x==null){
      return;
    }
    java.util.TreeSet<Item> set = x.findReferences(indirect);
    if (set.isEmpty()){
      info("No references found!");
    }else{
      StringBuilder sb = new StringBuilder();
      sb.append("Found ").append(set.size()).append(" reference(s) to ").append(x.getRelativeReferencePath(null)).append(":");
      for (Item y:set){
        sb.append('\n').append(y.getRelativeReferencePath(null));
      }
      info(sb.toString());
    }
  }
  private static void rebindWebCTRL(boolean manual){
    if (findWebCTRL(true,true,manual)){
      info("WebCTRL version changed!");
    }
  }
  private static void openEikon(){
    String web = WebCTRL;
    if (web==null){
      info("Please bind to a WebCTRL instance before attempting to open EIKON.");
    }else{
      File f = new File(web+"\\EIKON.exe");
      if (f.exists()){
        try{
          Desktop.getDesktop().open(f);
        }catch(Exception err){
          error("Unable to open EIKON.exe.", err);
        }
      }else{
        info("Cannot locate EIKON.exe!");
      }
    }
  }
  private static void openLog(){
    if (logFile.exists()){
      try{
        Desktop.getDesktop().open(logFile);
      }catch(Exception err){
        error("Unable to open log file.", err);
      }
    }else{
      info("Cannot locate log file!");
    }
  }
  private static void F3(){
    loadConfig(false);
    lib.reset();
    try{
      doLoad();
      info("Library reloaded!");
    }catch(Exception err){
      lib.reset();
      error("Unable to reload library.", err);
    }
    repaint();
  }
  private static void F5(){
    loadConfig(true);
    lib.reset();
    try{
      doLoad();
      info("Library "+(aces!=null && Files.exists(aces) && (devMode || allowUpdate)?"updated and ":"")+"reloaded!");
    }catch(Exception err){
      lib.reset();
      error("Unable to reload library.", err);
    }
    repaint();
  }
  private static boolean delete(Item x){
    if (x!=null){
      if (x.origin.exists()){
        if (JOptionPane.showConfirmDialog(Main, "Are you sure you want to delete "+x.origin.getName()+"?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION){
          try{
            Utilities.deleteTree(x.origin.toPath());
            if (x.file.exists()){
              Utilities.deleteTree(x.file.toPath());
            }
            lib.reset();
            try{
              doLoad();
            }catch(Exception err){
              lib.reset();
              error("Unable to reload library.", err);
            }
            repaint();
          }catch(Exception e){
            error("Unable to delete file(s).", e);
          }
          return true;
        }
      }else{
        info("Cannot locate item!");
      }
    }
    return false;
  }
  private static void openConfig(Item x){
    if (x!=null){
      if (x.origin.exists()){
        try{
          File con;
          if (x.origin.isDirectory()){
            con = new File(x.origin.getPath()+'\\'+configName);
          }else{
            con = new File(x.origin.getParentFile().getPath()+'\\'+configName);
          }
          if (con.exists()){
            Desktop.getDesktop().open(con);
          }else if (JOptionPane.showConfirmDialog(Main, "Create a new configuration file?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION){
            con.createNewFile();
            Desktop.getDesktop().open(con);
          }
        }catch(Exception e){
          ACESEquipmentBuilder.error("Unable to open configuration file", e);
        }
      }else{
        ACESEquipmentBuilder.info("Cannot locate item!");
      }
    }
  }
  private static void open(Item x){
    if (x!=null){
      if (x.origin.exists()){
        try{
          Desktop.getDesktop().open(x.origin);
        }catch(Exception e){
          ACESEquipmentBuilder.error("Unable to open "+x.origin.getName(), e);
        }
      }else{
        ACESEquipmentBuilder.info("Cannot locate item!");
      }
    }
  }
  private static boolean generateScript(){
    if (lib.ready()){
      try{
        if (!Files.exists(genScriptFile)){
          Files.createFile(genScriptFile);
        }
        StringBuilder sb = new StringBuilder();
        lib.toScript(sb);
        BufferedWriter out = new BufferedWriter(new FileWriter(genScriptFile.toFile(), false));
        out.write(sb.toString());
        out.close();
      }catch(Exception e){
        error("Unable to generate script.", e);
        return false;
      }
      generateScriptsFile();
      return true;
    }else{
      error(Item.failureMessage.toString());
      return false;
    }
  }
  private static void repaint(){
    repaintInternal();
    repaintExternal();
  }
  private static void repaintInternal(){
    imageH = lib.computeHeight()+1;
    imageW = stringStart+lib.computeWidth(metrics);
    paint = imageW!=stringStart;
    if (imageW==stringStart){
      return;
    }
    hDif = h-imageH-topPadding-bottomPadding;
    wDif = w-imageW-leftPadding-rightPadding;
    internalImage = new BufferedImage(imageW,imageH,BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = internalImage.createGraphics();
    g.setFont(font);
    g.setColor(background);
    g.fillRect(0,0,imageW,imageH);
    lib.paint(g,stringStart,0);
    g.dispose();
  }
  private static void repaintExternal(){
    if (internalImage==null){
      return;
    }
    BufferedImage snip = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = snip.createGraphics();
    g.setFont(font);
    g.setColor(background);
    g.fillRect(0,0,w,h);
    g.setColor(foreground);
    g.drawString("Equipment Name:", 10, initialY+20);
    if (paint){
      //The scroll bar position controls where the internal image is drawn to the screen.
      if (hDif<0){
        yOffset = (double)hDif*(vScrollY-topPadding)/(hDif+imageH-scrollLength);
        if (yOffset>0){ yOffset=0; }
        if (yOffset<hDif){ yOffset=hDif; }
      }else{
        vScrollY = topPadding;
        yOffset = 0;
      }
      int yoff = (int)(yOffset+0.5);
      if (wDif<0){
        xOffset = (double)wDif*(hScrollX-leftPadding)/(wDif+imageW-scrollLength);
        if (xOffset>0){ xOffset=0; }
        if (xOffset<wDif){ xOffset=wDif; }
      }else{
        hScrollX = leftPadding;
        xOffset = 0;
      }
      int xoff = (int)(xOffset+0.5);
      int[] source = ((DataBufferInt)internalImage.getRaster().getDataBuffer()).getData();
      int[] target = ((DataBufferInt)snip.getRaster().getDataBuffer()).getData();
      int y = -yoff;
      int yyLim = Math.min(imageH+yoff+topPadding,h);
      int x = -xoff;
      int xxLim = Math.min(imageW+xoff+leftPadding,w);
      int wyy = w*topPadding;
      int wy = imageW*y;
      //The loop which actually copies the pixels from the internal image to the image drawn to the screen
      for (int yy=topPadding;yy<yyLim;yy++){
        for (int xx=leftPadding;xx<xxLim;xx++,x++){
          target[wyy+xx] = source[wy+x];
        }
        wyy+=w;
        wy+=imageW;
        x = -xoff;
      }
      yyLim = topPadding+imageH+yoff;
      y=topPadding+yoff;
      while (y<topPadding){
        y+=Item.entryHeight;
      }
      for (;y<yyLim;y+=Item.entryHeight){
        g.drawLine(0,y,w,y);
      }
      //Renders the scroll bars
      g.setColor(scrollBarColor);
      if (hDif<0){
        g.fillRect(w-vScrollX, vScrollY, scrollWidth, scrollLength);
      }
      if (wDif<0){
        g.fillRect(hScrollX, h-hScrollY, scrollLength, scrollWidth);
      }
    }
    g.dispose();
    MLabel.setIcon(new ImageIcon(snip));
    MLabel.repaint();
  }
  public static boolean doLoad() throws Exception {
    StringBuilder sb = new StringBuilder();
    boolean ret = load(lib, sb);
    if (sb.length()!=0){
      error("The following error(s) occurred while loading the library:"+sb.toString());
    }
    lib.evaluateSuggestions(new Int(0));
    return ret;
  }
  private static class Line {
    public String str;
    public int num;
    public Line(String str, int num){
      this.str = str;
      this.num = num;
    }
  }
  /**
   * Loads the local library files into the program.
   * Return value indicates whether item initialization was successful.
   */
  public static boolean load(Item item, StringBuilder sb) throws Exception {
    char c;
    String ppath = item.file.getPath();
    String relPath = item==lib?"":ppath.substring(libSubstringPos);
    if (acesLib!=null){
      item.origin = acesLib.resolve(relPath).toFile();
    }
    if (item instanceof Folder){
      String path = ppath+"\\";
      String cpath = path+configName;
      Path config = Paths.get(cpath);
      String configRelPath = cpath.substring(libSubstringPos);
      String str;
      if (Files.exists(config)){
        String trim;
        int begin;
        Item tmpItem = null;
        Line line;
        java.util.List<String> data = Files.readAllLines(config);
        int LEN = data.size();
        //Strip comments and empty lines
        java.util.ArrayList<Line> lines = new java.util.ArrayList<Line>(LEN);
        for (int i=0;i<LEN;++i){
          str = data.get(i);
          trim = str.trim();
          if (trim.startsWith("/*")){
            if (devMode && trim.startsWith("/**")){
              str = trim.substring(3);
              trim = str.trim();
            }else{
              begin = i;
              while (!trim.endsWith("*/")){
                ++i;
                if (i==LEN){
                  sb.append("\nFailed to close multiline comment on line "+begin+" of "+configRelPath);
                  return false;
                }
                trim = data.get(i).trim();
              }
              continue;
            }
          }
          if (devMode && trim.endsWith("*/")){
            str = trim.substring(0,trim.length()-3);
            trim = str.trim();
          }
          if (trim.startsWith("//")){
            if (devMode && trim.startsWith("///")){
              str = trim.substring(3);
              trim = str.trim();
            }else{
              continue;
            }
          }
          if (trim.isEmpty()){
            continue;
          }
          lines.add(new Line(str, i+1));
        }
        data = null;
        LEN = lines.size();
        //Evaluates item initialization statements and performs syntax validation.
        for (int j=0;j<LEN;++j){
          line = lines.get(j);
          str = line.str.trim();
          if ((str.startsWith("Value(") && str.endsWith(")")) || (str.startsWith("Condition(") && str.endsWith(")"))){
            continue;
          }
          if (str.equalsIgnoreCase("If [") || str.equalsIgnoreCase("if[")){
            begin = line.num;
            do {
              ++j;
              if (j==LEN){
                sb.append("\nCannot find 'Then' corresponding to 'If' on line "+begin+" of "+configRelPath);
                return false;
              }
              str = lines.get(j).str.trim();
            } while (!str.equalsIgnoreCase("] Then [") && !str.equalsIgnoreCase("]Then["));
            begin = line.num;
            do {
              ++j;
              if (j==LEN){
                sb.append("\nFailed to close If-Then statement on line "+begin+" of "+configRelPath);
                return false;
              }
              str = lines.get(j).str.trim();
            } while (!str.equals("]"));
          }else if (str.startsWith("PreScript(") && str.endsWith(")[")){
            begin = line.num;
            do {
              ++j;
              if (j==LEN){
                sb.append("\nFailed to close PreScript on line "+begin+" of "+configRelPath);
                return false;
              }
              str = lines.get(j).str.trim();
            } while (!str.equals("]"));
          }else if (str.startsWith("PostScript(") && str.endsWith(")[")){
            begin = line.num;
            do {
              ++j;
              if (j==LEN){
                sb.append("\nFailed to close PostScript on line "+begin+" of "+configRelPath);
                return false;
              }
              str = lines.get(j).str.trim();
            } while (!str.equals("]"));
          }else if (str.startsWith("Group(") && str.endsWith(")[")){
            begin = line.num;
            do {
              ++j;
              if (j==LEN){
                sb.append("\nFailed to close Group on line "+begin+" of "+configRelPath);
                return false;
              }
              str = lines.get(j).str.trim();
            } while (!str.equals("]"));
          }else{
            boolean selected = false;
            boolean visible = true;
            boolean locked = false;
            boolean addScript = true;
            int len = str.length();
            for (int i=0;i<len;i++){
              c = str.charAt(i);
              if (c=='+'){
                selected = defaultSelections;
              }else if (c=='-'){
                visible = !hideEntries;
              }else if (c=='*'){
                locked = lockedEntries;
              }else if (c=='@'){
                addScript = false;
              }else if (c!=' '){
                str = str.substring(i).trim();
                break;
              }
            }
            String name = null;
            int i = str.indexOf('"');
            int k = str.lastIndexOf('"');
            if (i!=-1 && i<k){
              name = Utilities.expandUnicode(str.substring(i+1,k)).toString();
              str = str.substring(0,i).trim();
            }
            if (str.indexOf('\\')!=-1 || str.indexOf('/')!=-1){
              Item ret = item.find(str, 0, str.length());
              if (ret==null){
                sb.append("\nUnable to locate item reference on line "+line.num+" of "+configRelPath);
                continue;
              }
              str = ret.refName;
              if (ret instanceof Symbol){
                tmpItem = new Symbol(ret.file);
                tmpItem.originalName = name==null?toTitleCase(str):name;
              }else{
                tmpItem = new Folder(ret.file);
                tmpItem.originalName = name==null?str:name;
              }
              tmpItem.original = ret;
              Item check;
              do {
                for (check=item;check!=null;check=check.parent){
                  if (check.file==ret.file){
                    error("\nReference loop detected on line "+line.num+" of "+configRelPath);
                    throw new Exception("Critical error occurred while loading library.");
                  }
                }
                ret = ret.original;
              } while (ret!=null);
            }else{
              String p = path+str;
              File file = new File(p);
              if (file.isDirectory()){
                tmpItem = new Folder(file);
                tmpItem.originalName = name==null?str:name;
              }else{
                file = new File(p+".logicsymbol");
                if (file.isFile()){
                  tmpItem = new Symbol(file);
                  tmpItem.originalName = name==null?toTitleCase(str):name;
                  if (addScript){
                    tmpItem.preScript = "  symbol('"+file.getPath().replace('\\','/')+"')";
                  }
                }else if (!addScript){
                  tmpItem = new Symbol(new File(p));
                  tmpItem.originalName = name==null?(str.indexOf('_')==-1?str:toTitleCase(str)):name;
                }else{
                  sb.append("\nUnable to locate item on line "+line.num+" of "+configRelPath);
                  continue;
                }
              }
            }
            tmpItem.refName = str;
            tmpItem.selected = selected;
            tmpItem.visible = visible;
            tmpItem.locked = locked;
            item.add(tmpItem);
          }
        }
        try{
          for (int i=0;i<item.len;i++){
            if (!load(item.items[i], sb)){
              item.removeItem(i--);
            }
          }
        }catch(StackOverflowError e){
          System.gc();
          error("\nReference loop detected. Please review configuration files.");
          throw new Exception("Critical error occurred while loading library.");
        }
        //type: 0 - default, 1 - group, 2 - prescript, 3 - postscript, 4 - If, 5 - Then
        int type = 0;
        StringBuilder block = new StringBuilder();
        ItemGroup group = null;
        tmpItem = null;
        SuggestGroup sugGroup = null;
        //This loop does most of the work parsing the configuration file
        for (int l=0;l<LEN;++l){
          line = lines.get(l);
          str = line.str;
          trim = str.trim();
          if (type==0){
            str = trim;
            if (str.startsWith("Value(") && str.endsWith(")")){
              int i = str.indexOf(',');
              if (i==-1){
                sb.append("\nInvalid Value statement formatting on line "+line.num+" of "+configRelPath);
                continue;
              }
              String name = str.substring(6,i).trim();
              tmpItem = item.findSimple(name);
              int def,min,increment,max;
              boolean passed = false;
              try{
                int j = str.indexOf(',',++i);
                if (j==-1){
                  sb.append("\nInvalid Value statement formatting on line "+line.num+" of "+configRelPath);
                  continue;
                }
                if (tmpItem==null){
                  tmpItem = item;
                  def = Integer.parseInt(name);
                  passed = true;
                  int tmp = i;
                  i = j;
                  j = tmp;
                }else{
                  def = Integer.parseInt(str.substring(i,j).trim());
                  i = str.indexOf(',',++j);
                  if (i==-1){
                    sb.append("\nInvalid Value statement formatting on line "+line.num+" of "+configRelPath);
                    continue;
                  }
                }
                String tmp = str.substring(j,i).trim();
                if (tmp.equalsIgnoreCase("-Inf")){
                  min = Integer.MIN_VALUE;
                }else{
                  min = Integer.parseInt(tmp);
                }
                j = str.indexOf(',',++i);
                if (j==-1){
                  sb.append("\nInvalid Value statement formatting on line "+line.num+" of "+configRelPath);
                  continue;
                }
                increment = Integer.parseInt(str.substring(i,j).trim());
                tmp = str.substring(++j,str.length()-1).trim();
                if (tmp.equalsIgnoreCase("Inf")){
                  max = Integer.MAX_VALUE;
                }else{
                  max = Integer.parseInt(tmp);
                }
              }catch(Exception e){
                if (passed){
                  sb.append("\nInvalid Value statement formatting on line "+line.num+" of "+configRelPath);
                }else{
                  sb.append("\nUnable to find item on line "+line.num+" of "+configRelPath);
                }
                continue;
              }
              if (max<min){
                max = min;
              }
              if (def<min){
                def = min;
              }else if (def>max){
                def = max;
              }
              tmpItem.valueChanger = true;
              tmpItem.value = def;
              tmpItem.min = min;
              tmpItem.increment = increment;
              tmpItem.max = max;
            }else if (str.startsWith("Condition(") && str.endsWith(")")){
              Matcher m = Patterns.condition.matcher(str);
              if (!m.matches()){
                sb.append("\nInvalid Condition statement formatting on line "+line.num+" of "+configRelPath);
                continue;
              }
              String msg;
              String expr = m.group(2);
              if (expr==null){
                tmpItem = item;
                expr = m.group(1).trim();
              }else{
                tmpItem = item.findSimple(m.group(1).trim());
                expr = expr.trim();
              }
              msg = m.group(3).trim();
              if (tmpItem==null){
                sb.append("\nUnable to find item on line "+line.num+" of "+configRelPath);
                continue;
              }
              msg = Utilities.expandUnicode(msg).toString();
              tmpItem.add(expr,msg);
            }else if (str.equalsIgnoreCase("If [") || str.equalsIgnoreCase("if[")){
              type = 4;
              sugGroup = new SuggestGroup();
            }else if (str.equals("PreScript()[")){
              type = 2;
              tmpItem = item;
            }else if (str.equals("PostScript()[")){
              type = 3;
              tmpItem = item;
            }else if (str.startsWith("PreScript(") && str.endsWith(")[")){
              type = 2;
              str = str.substring(10,str.length()-2).trim();
              tmpItem = item.findSimple(str);
              if (tmpItem==null){
                sb.append("\nUnable to find item on line "+line.num+" of "+configRelPath);
                continue;
              }
            }else if (str.startsWith("PostScript(") && str.endsWith(")[")){
              type = 3;
              str = str.substring(11,str.length()-2).trim();
              tmpItem = item.findSimple(str);
              if (tmpItem==null){
                sb.append("\nUnable to find item on line "+line.num+" of "+configRelPath);
                continue;
              }
            }else if (str.startsWith("Group(") && str.endsWith(")[")){//Group([Minimum, ]Maximum)
              type = 1;
              try{
                str = str.substring(6,str.length()-2);
                int index = str.indexOf(',');
                if (index==-1){
                  group = new ItemGroup(0,Integer.valueOf(str.trim()));
                }else{
                  String tmp = str.substring(index+1).trim();
                  group = new ItemGroup(Integer.valueOf(str.substring(0,index).trim()),tmp.equalsIgnoreCase("Inf")?-1:Integer.valueOf(tmp));
                }
              }catch(Exception e){
                sb.append("\nUnable to parse integers from Group(?,?) on line "+line.num+" of "+configRelPath);
                group = null;
                continue;
              }
              if (group.min>group.max && (group.max!=-1 || (group.min==0 && group.max==-1))){
                sb.append("\nGroup minimum is greater than the maximum on line "+line.num+" of "+configRelPath);
                group = null;
                continue;
              }
            }
          }else if (type==1){
            str = trim;
            if (str.equals("]")){
              if (group!=null && (groupMin || groupMax)){
                if (!groupMin){
                  group.min = 0;
                }
                if (!groupMax){
                  group.max = -1;
                }
                boolean add = true;
                if (group.len==0){
                  sb.append("\nAttempted to initialize empty group on line "+line.num+" of "+configRelPath);
                  add = false;
                }
                if (groupMax && group.len<group.max){
                  sb.append("\nGroup has more maximum options than actual options on line "+line.num+" of "+configRelPath);
                  add = false;
                }
                if (group.len<group.min){
                  sb.append("\nGroup has more minimum options than actual options on line "+line.num+" of "+configRelPath);
                  add = false;
                }
                if (add){
                  item.add(group);
                }
              }
              group = null;
              type = 0;
            }else if (group!=null){
              if (str.indexOf('/')!=-1 || str.indexOf('\\')!=-1){
                sb.append("\nAttempted to group items using relative paths on line "+line.num+" of "+configRelPath);
                continue;
              }
              tmpItem = item.findSimple(str);
              if (tmpItem==null){
                sb.append("\nUnable to find item on line "+line.num+" of "+configRelPath);
                continue;
              }
              if (group.contains(tmpItem)){
                sb.append("\nDuplicate group entry on line "+line.num+" of "+configRelPath);
                continue;
              }
              boolean overlap = false;
              for (int i=0;i<item.groupLen;++i){
                if (item.groups[i].contains(tmpItem)){
                  sb.append("\nOverlapping group on line "+line.num+" of "+configRelPath);
                  overlap = true;
                  break;
                }
              }
              if (overlap){
                continue;
              }
              group.add(tmpItem);
              if (group.num>group.max && group.max!=-1){
                sb.append("\nDefault group selections exceed maximum allowed selections on line "+line.num+" of "+configRelPath);
                continue;
              }
            }
          }else if (type==4){
            str = trim;
            if (str.equalsIgnoreCase("] Then [") || str.equalsIgnoreCase("]Then[")){
              type = 5;
              if (sugGroup.preLen==0 && sugGroup.preExpression==null){
                sugGroup = null;
              }
            }else{
              int len = str.length();
              if (str.indexOf('&')!=-1 || str.indexOf('|')!=-1 || str.indexOf('<')!=-1 || str.indexOf('>')!=-1 || str.indexOf('=')!=-1){
                if (sugGroup.preExpression==null){
                  sugGroup.preExpression = '('+str+')';
                }else{
                  sugGroup.preExpression+="&&("+str+')';
                }
                continue;
              }
              byte mod = 0;
              boolean not = false;
              for (int i=0;i<len;i++){
                c = str.charAt(i);
                if (c=='!'){
                  not = true;
                }else if (c=='@'){
                  if (not){
                    not = false;
                  }else{
                    mod|=SuggestGroup.IGNORE_PARENTS;
                  }
                }else if (c=='+'){
                  mod|=not?SuggestGroup.NOT_SELECTED:SuggestGroup.SELECTED;
                  not = false;
                }else if (c=='-'){
                  mod|=not?SuggestGroup.VISIBLE:SuggestGroup.NOT_VISIBLE;
                  not = false;
                }else if (c=='*'){
                  mod|=not?SuggestGroup.NOT_LOCKED:SuggestGroup.LOCKED;
                  not = false;
                }else if (c!=' '){
                  str = str.substring(i).trim();
                  break;
                }
              }
              if (mod==0){
                mod = not?SuggestGroup.NOT_SELECTED:SuggestGroup.SELECTED;
              }
              tmpItem = item.find(str, 0, str.length());
              if (tmpItem==null){
                sb.append("\nUnable to find item on line "+line.num+" of "+configRelPath);
                continue;
              }
              sugGroup.addPre(tmpItem,mod);
            }
          }else if (type==5){
            str = trim;
            if (str.equals("]")){
              type = 0;
              if (sugGroup!=null){
                if (sugGroup.postLen==0 && sugGroup.postExpression==null){
                  continue;
                }
                if (suggestEntries){
                  item.add(sugGroup);
                }
              }
            }else if (sugGroup!=null){
              int len = str.length();
              if (str.indexOf('&')!=-1 || str.indexOf('|')!=-1 || str.indexOf('<')!=-1 || str.indexOf('>')!=-1 || str.indexOf('=')!=-1){
                int i = str.indexOf('>');
                if (i==-1){
                  continue;
                }
                str = str.substring(0,i)+str.substring(i+1)+'>';
                if (sugGroup.postExpression==null){
                  sugGroup.postExpression = str;
                }else{
                  sugGroup.postExpression+=str;
                }
                continue;
              }
              byte mod = 0;
              boolean not = false;
              for (int i=0;i<len;i++){
                c = str.charAt(i);
                if (c=='!'){
                  not = true;
                }else if (c=='@'){
                  if (not){
                    not = false;
                  }else{
                    mod|=SuggestGroup.IGNORE_PARENTS;
                  }
                }else if (c=='+'){
                  mod|=not?SuggestGroup.NOT_SELECTED:SuggestGroup.SELECTED;
                  not = false;
                }else if (c=='-'){
                  mod|=not?SuggestGroup.VISIBLE:SuggestGroup.NOT_VISIBLE;
                  not = false;
                }else if (c=='*'){
                  mod|=not?SuggestGroup.NOT_LOCKED:SuggestGroup.LOCKED;
                  not = false;
                }else if (c!=' '){
                  str = str.substring(i).trim();
                  break;
                }
              }
              if (mod==0){
                mod = not?SuggestGroup.NOT_SELECTED:SuggestGroup.SELECTED;
              }
              mod&=0xFF^(lockedEntries?(hideEntries?0:SuggestGroup.NOT_VISIBLE):(hideEntries?SuggestGroup.LOCKED:SuggestGroup.NOT_VISIBLE|SuggestGroup.LOCKED));
              if (mod==0){
                continue;
              }
              tmpItem = item.find(str, 0, str.length());
              if (tmpItem==null){
                sb.append("\nUnable to find item on line "+line.num+" of "+configRelPath);
                continue;
              }
              sugGroup.addPost(tmpItem,mod);
            }
          }else{
            if (trim.equals("]")){
              if (tmpItem!=null){
                if (type==2){
                  if (tmpItem.preScript.isEmpty()){
                    tmpItem.preScript = block.toString();
                  }else{
                    tmpItem.preScript = block.append(lineSeparator).append(tmpItem.preScript).toString();
                  }
                }else if (type==3){
                  if (tmpItem.postScript.isEmpty()){
                    tmpItem.postScript = block.toString();
                  }else{
                    tmpItem.postScript+=lineSeparator+block.toString();
                  }
                }
              }
              type = 0;
              block.setLength(0);
            }else{
              if (block.length()!=0){
                block.append(lineSeparator);
              }
              block.append(str);
            }
          }
        }
      }else{
        //This specifies how to load in folders which lack a configuration file
        File[] arr = item.file.listFiles();
        for (int i=0;i<arr.length;i++){
          if (arr[i].isFile() && arr[i].getName().endsWith(".logicsymbol")){
            str = arr[i].getName().substring(0, arr[i].getName().length()-12);
            Symbol sym = new Symbol(arr[i]);
            sym.refName = str;
            sym.originalName = toTitleCase(str);
            sym.preScript = "  symbol('"+arr[i].getPath().replace('\\','/')+"')";
            item.add(sym);
          }
        }
        for (int i=0;i<arr.length;i++){
          if (arr[i].isDirectory()){
            Folder dir = new Folder(arr[i]);
            dir.originalName = arr[i].getName();
            dir.refName = dir.originalName;
            item.add(dir);
          }
        }
        try{
          for (int i=0;i<item.len;i++){
            if (!load(item.items[i], sb)){
              item.removeItem(i--);
            }
          }
        }catch(StackOverflowError e){
          System.gc();
          error("\nReference loop detected. Please review configuration files.");
          throw new Exception("Critical error occurred while loading library.");
        }
      }
    }
    return true;
  }
  private static void update(){
    if (aces!=null){
      loadACESConfig(aces);
      if (updateLibrary && Files.exists(aces)){
        if (allowUpdate || devMode){
          try{
            if (!Files.exists(acesLib)){
              Files.createDirectories(acesLib);
            }
            Utilities.copy(acesLib, localLib);
          }catch(Exception e){
            error("Failed to update library.", e);
          }
        }else{
          info("Updates have been temporarily disabled.");
        }
      }
    }
    if (WebCTRL!=null){
      setupWebCTRL(true);
    }
  }
  private static void setupWebCTRL(boolean updates){
    try{
      if (!Files.exists(localScripts)){
        Files.createDirectories(localScripts);
        StringBuilder sb = new StringBuilder(128);
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+lineSeparator+"<root>");
        boolean present = false;
        if (Files.exists(localPlugins)){
          BufferedReader in = new BufferedReader(new FileReader(localPlugins.toFile()));
          while (true){
            String line = in.readLine();
            if (line==null){
              break;
            }else if (line.contains("<plugin")){
              sb.append(lineSeparator);
              sb.append(line);
              if (line.contains(scriptPlugin)){
                present = true;
                break;
              }
            }
          }
          in.close();
        }
        if (!present){
          sb.append(lineSeparator);
          sb.append("  ");
          sb.append(scriptPlugin);
          sb.append(lineSeparator);
          sb.append("</root>");
          ByteBuffer buf = ByteBuffer.wrap(sb.toString().getBytes());
          try(
            FileChannel ch = FileChannel.open(localPlugins, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
          ){
            while (buf.hasRemaining()){
              ch.write(buf);
            }
          }
        }
      }
      if ((allowUpdate || devMode) && updates && aces!=null && Files.exists(aces)){
        if (updateScripts){
          if (!Files.exists(acesScripts)){
            Files.createDirectories(acesScripts);
          }
          Utilities.copy(acesScripts,localScripts);
          generateScriptsFile();
        }
        if (updateFavorites){
          if (!Files.exists(acesFavorites)){
            Files.createDirectories(acesFavorites);
          }
          Utilities.copy(acesFavorites, localFavorites);
          generateFavoritesFile();
        }
      }
    }catch(Exception e){
      Logger.log("Error occurred while configuring WebCTRL files.", e);
    }
  }
  private static void generateFavoritesFile(){
    if (!Files.exists(localFavoritesFile)){
      try{
        Files.createFile(localFavoritesFile);
      }catch(Exception e){
        error("Unable to create favorites file.", e);
        return;
      }
    }
    File[] fileList = localFavorites.toFile().listFiles(new FilenameFilter(){
      public boolean accept(File dir, String name){
        return name.endsWith(".logicsymbol");
      }
    });
    try{
      BufferedWriter out = new BufferedWriter(new FileWriter(localFavoritesFile.toFile(),false));
      out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+lineSeparator+"<favorites>");
      for (int i=0;i<fileList.length;i++){
        out.write(lineSeparator+"  <favorite name=\""+toTitleCase(fileList[i].getName().substring(0,fileList[i].getName().length()-12))+"\" description=\"\" file=\""+fileList[i].getAbsolutePath()+"\" />");
      }
      out.write(lineSeparator+"</favorites>");
      out.close();
    }catch(Exception e){
      error("Unable to write to favorites file.", e);
    }
  }
  private static void generateScriptsFile(){
    if (!Files.exists(localScriptsFile)){
      try{
        Files.createFile(localScriptsFile);
      }catch(Exception e){
        error("Unable to create scripts file.", e);
        return;
      }
    }
    File[] fileList = localScripts.toFile().listFiles(new FilenameFilter(){
      public boolean accept(File dir, String name){
        return name.endsWith(".logic-script");
      }
    });
    try{
      BufferedWriter out = new BufferedWriter(new FileWriter(localScriptsFile.toFile(),false));
      out.write("<scripts>");
      for (int i=0;i<fileList.length;i++){
        out.write(lineSeparator+"  <script name=\""+toTitleCase(fileList[i].getName().substring(0,fileList[i].getName().length()-13))+"\" file=\""+fileList[i].getAbsolutePath()+"\" />");
      }
      out.write(lineSeparator+"</scripts>");
      out.close();
    }catch(Exception e){
      error("Unable to write to scripts file.", e);
    }
  }
  /**
   * Converts underscores to spaces and capitalizes the first letter of every word
   */
  public static String toTitleCase(String str){
    char[] arr = str.toCharArray();
    boolean toUpper = true;
    for (int i=0;i<arr.length;i++){
      if (toUpper){
        arr[i] = Character.toUpperCase(arr[i]);
        toUpper = false;
      }else if (arr[i]=='_'){
        arr[i] = ' ';
        toUpper = true;
      }else if (arr[i]==' '){
        toUpper = true;
      }
    }
    return new String(arr);
  }
  public static void error(String message){
    error(message, false);
  }
  public static void error(String message, boolean exit){
    if (!supportMessage.isEmpty()){
      message+="\n"+supportMessage;
    }
    int lineCount = 0;
    int i = -1;
    do {
      ++lineCount;
      i = message.indexOf('\n', i+1);
    } while (i!=-1);
    if (lineCount<=5){
      JOptionPane.showMessageDialog(Main, message, "Error", JOptionPane.ERROR_MESSAGE);
    }else{
      JTextPane txt = new JTextPane();
      txt.setEditable(false);
      txt.setText(message);
      JScrollPane jsp = new JScrollPane(txt);
      jsp.setPreferredSize(new Dimension(800, 500));
      JOptionPane.showMessageDialog(Main, jsp, "Error", JOptionPane.ERROR_MESSAGE);
    }
    focus();
    Logger.log(message);
    if (exit){
      onExit();
      System.exit(1);
    }
  }
  public static void error(String message, Exception e){
    error(message, false, e);
  }
  public static void error(String message, boolean exit, Exception e){
    if (!supportMessage.isEmpty()){
      message+="\n"+supportMessage;
    }
    int lineCount = 0;
    int i = -1;
    do {
      ++lineCount;
      i = message.indexOf('\n', i+1);
    } while (i!=-1);
    if (lineCount<=5){
      JOptionPane.showMessageDialog(Main, message, "Error", JOptionPane.ERROR_MESSAGE);
    }else{
      JTextPane txt = new JTextPane();
      txt.setEditable(false);
      txt.setText(message);
      JScrollPane jsp = new JScrollPane(txt);
      jsp.setPreferredSize(new Dimension(800, 500));
      JOptionPane.showMessageDialog(Main, jsp, "Error", JOptionPane.ERROR_MESSAGE);
    }
    focus();
    Logger.log(message, e);
    if (exit){
      onExit();
      System.exit(1);
    }
  }
  public static void info(String message){
    int lineCount = 0;
    int i = -1;
    do {
      ++lineCount;
      i = message.indexOf('\n', i+1);
    } while (i!=-1);
    if (lineCount<=5){
      JOptionPane.showMessageDialog(Main, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
    }else{
      JTextPane txt = new JTextPane();
      txt.setEditable(false);
      txt.setText(message);
      JScrollPane jsp = new JScrollPane(txt);
      jsp.setPreferredSize(new Dimension(800, 500));
      JOptionPane.showMessageDialog(Main, jsp, "Notification", JOptionPane.INFORMATION_MESSAGE);
    }
    focus();
    Logger.log(message);
  }
  private static void loadACESConfig(Path loc){
    try{
      acesLib = null;
      acesFavorites = null;
      acesScripts = null;
      if (acesConfig==null || !Files.exists(aces)){
        return;
      }
      if (Files.exists(acesConfig) && Files.isReadable(acesConfig)){
        BufferedReader in = new BufferedReader(new FileReader(acesConfig.toFile()));
        double ver = VERSION;
        String script = null;
        String str,key,val;
        while (true){
          str = in.readLine();
          if (str==null){
            break;
          }
          if (str.startsWith("//")){
            continue;
          }else if (str.startsWith("/*")){
            while (str!=null && !str.endsWith("*/")){
              str = in.readLine();
            }
            if (str==null){
              break;
            }
          }
          str = Utilities.expandEnvironmentVariables(str);
          int i = str.indexOf('=');
          if (i==-1){
            continue;
          }
          key = str.substring(0,i).trim();
          val = str.substring(i+1).trim();
          if (key.equals("Library")){
            acesLib = loc.resolve(val);
          }else if (key.equals("Favorites")){
            acesFavorites = loc.resolve(val);
          }else if (key.equals("Scripts")){
            acesScripts = loc.resolve(val);
          }else if (key.equals("AllowUpdate")){
            allowUpdate = Boolean.valueOf(val);
          }else if (key.equals("Version")){
            ver = Double.parseDouble(val);
          }else if (key.equals("UpdateScript")){
            String tmp = loc.toString();
            script = val.isEmpty()?"explorer.exe \""+tmp+'"':"cmd /c start \"\" \""+tmp+'\\'+val+"\" "+installation.toString();
          }else{
            info("Unrecognized entry in remote configuration file.\n"+acesConfig.toString()+'\n'+str);
          }
        }
        if (ver>VERSION){
          if (script==null){
            info("Please install version "+ver+" of ACES Equipment Builder.");
          }else if (JOptionPane.YES_OPTION==JOptionPane.showConfirmDialog(Main, "Do you want to update to version "+ver+" of ACES Equipment Builder?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)){
            try{
              Runtime.getRuntime().exec(script);
              onExit();
              System.exit(0);
            }catch(Exception e){
              error("Failed to update ACES Equipment Builder.", e);
            }
          }
        }
        in.close();
      }else{
        File f = acesConfig.toFile();
        if (f.createNewFile()){
          BufferedWriter out = new BufferedWriter(new FileWriter(f,false));
          out.write("Version="+VERSION+lineSeparator);
          out.write("AllowUpdate="+allowUpdate+lineSeparator);
          out.write("UpdateScript="+lineSeparator);
          out.write("Library=Library"+lineSeparator);
          out.write("Favorites=Favorites"+lineSeparator);
          out.write("Scripts=Scripts"+lineSeparator);
          out.close();
        }
      }
    }catch (FileNotFoundException e){
    }catch(Exception e){
      error("Unable to load ACES configuration file.", e);
    }
    if (acesLib==null){
      acesLib = loc.resolve("Library");
    }
    if (acesFavorites==null){
      acesFavorites = loc.resolve("Favorites");
    }
    if (acesScripts==null){
      acesScripts = loc.resolve("Scripts");
    }
  }
  private static boolean sendEmail(String str){
    try{
      if (str==null){
        Desktop.getDesktop().mail(new java.net.URI("mailto:"+emailTo+"?subject=ACES%20Equipment%20Builder%20Feedback"));
      }else{
        Desktop.getDesktop().mail(new java.net.URI("mailto:"+emailTo+"?subject=ACES%20Equipment%20Builder%20Feedback&body="+java.net.URLEncoder.encode(str, "UTF-8").replace("+","%20")));
      }
    }catch(Exception e){
      error("Failed to send email.", e);
      return false;
    }
    return true;
  }
  private static boolean loadConfig(boolean updates){
    try {
      if (Files.exists(mainConfig)){
        BufferedReader in = new BufferedReader(new FileReader(mainConfig.toFile()));
        String str,key,val,web=null,lib=null;
        while (true){
          str = in.readLine();
          if (str==null){
            break;
          }
          if (str.startsWith("//")){
            continue;
          }else if (str.startsWith("/*")){
            while (str!=null && !str.endsWith("*/")){
              str = in.readLine();
            }
            if (str==null){
              break;
            }
          }
          str = Utilities.expandEnvironmentVariables(str);
          int i = str.indexOf('=');
          if (i==-1){
            continue;
          }
          key = str.substring(0,i).trim();
          val = str.substring(i+1).trim();
          if (key.equals("WebCTRLPath")){
            web = val;
          }else if (key.equals("EmailTo")){
            emailTo = val;
          }else if (key.equals("RemoteDirectoryPath")){
            lib = val;
          }else if (key.equals("AutoUpdate")){
            autoUpdate = Boolean.valueOf(val);
          }else if (key.equals("UpdateLibrary")){
            updateLibrary = Boolean.valueOf(val);
          }else if (key.equals("UpdateFavorites")){
            updateFavorites = Boolean.valueOf(val);
          }else if (key.equals("UpdateScripts")){
            updateScripts = Boolean.valueOf(val);
          }else if (key.equals("AllowHiddenEntries")){
            hideEntries = Boolean.valueOf(val);
          }else if (key.equals("AllowLockedEntries")){
            lockedEntries = Boolean.valueOf(val);
          }else if (key.equals("AllowDefaultSelections")){
            defaultSelections = Boolean.valueOf(val);
          }else if (key.equals("AllowGroupMinimums")){
            groupMin = Boolean.valueOf(val);
          }else if (key.equals("AllowGroupMaximums")){
            groupMax = Boolean.valueOf(val);
          }else if (key.equals("AllowIfThenStatements")){
            suggestEntries = Boolean.valueOf(val);
          }else if (key.equals("SupportMessage")){
            supportMessage = val;
          }else{
            info("Unrecognized entry in primary configuration file:\n"+mainConfig.toString()+'\n'+str);
          }
        }
        in.close();
        if (lib==null){
          aces = null;
        }else{
          initRemotePaths(lib,updates);
        }
        if (web==null){
          WebCTRL = null;
        }else{
          initLocalPaths(web,updates);
        }
        return aces!=null && WebCTRL!=null;
      }
    }catch(Exception e){
      error("Unable to load primary configuration file.", true);
    }
    return false;
  }
  private static boolean saveConfig(boolean exitOnCancel){
    boolean exit = false;
    try {
      if (!Files.exists(installation)){
        Files.createDirectories(installation);
      }
      if (!Files.exists(mainConfig)){
        Files.createFile(mainConfig);
      }
      if (aces==null){
        String remlib = JOptionPane.showInputDialog(Main,"Remote Directory Path:","Configuration",JOptionPane.QUESTION_MESSAGE);
        focus();
        if (remlib==null){
          if (exitOnCancel){
            exit = true;
            Files.delete(mainConfig);
            System.exit(0);
          }else{
            remlib = "";
          }
        }
        initRemotePaths(remlib, true);
      }
      if (WebCTRL==null){
        findWebCTRL(true, false, false);
      }
      writeToConfig();
      return true;
    }catch(Exception e){
      if (exit){
        error("Unable to delete primary configuration file.", true, e);
      }else{
        error("Unable to save primary configuration file.", e);
      }
      return false;
    }
  }
  private static void writeToConfig() throws Exception {
    BufferedWriter out = new BufferedWriter(new FileWriter(mainConfig.toFile(),false));
    out.write("WebCTRLPath="+WebCTRL+lineSeparator);
    out.write("RemoteDirectoryPath="+(aces==null?lineSeparator:aces.toString()+lineSeparator));
    out.write("AutoUpdate="+autoUpdate+lineSeparator);
    out.write("UpdateLibrary="+updateLibrary+lineSeparator);
    out.write("UpdateFavorites="+updateFavorites+lineSeparator);
    out.write("UpdateScripts="+updateScripts+lineSeparator);
    out.write("AllowHiddenEntries="+hideEntries+lineSeparator);
    out.write("AllowLockedEntries="+lockedEntries+lineSeparator);
    out.write("AllowDefaultSelections="+defaultSelections+lineSeparator);
    out.write("AllowGroupMinimums="+groupMin+lineSeparator);
    out.write("AllowGroupMaximums="+groupMax+lineSeparator);
    out.write("AllowIfThenStatements="+suggestEntries+lineSeparator);
    out.write("SupportMessage="+supportMessage+lineSeparator);
    out.write("EmailTo="+emailTo);
    out.close();
  }
  private static boolean findWebCTRL(boolean updates, boolean save, boolean manual){
    String[] fileList = null;
    if (!manual && systemDrive!=null){
      fileList = new File(systemDrive).list(new FilenameFilter(){
        public boolean accept(File dir, String name){
          return Patterns.webctrl.matcher(name).matches();
        }
      });
    }
    String str = null;
    if (manual || systemDrive==null || fileList.length==0){
      if (webctrlDirChooser.showDialog(Main, "Select")!=JFileChooser.APPROVE_OPTION){
        return false;
      }
      File f = webctrlDirChooser.getSelectedFile();
      if (!f.exists()){
        return false;
      }
      str = f.getPath();
    }else if (fileList.length==1){
      str = systemDrive+fileList[0];
    }else{
      if (WebCTRL==null){
        double version = 0;
        for (int i=0;i<fileList.length;i++){
          try {
            double ver = Double.valueOf(fileList[i].substring(7));
            if (ver>version){
              version = ver;
              str = fileList[i];
            }
          }catch(Exception e){}
        }
      }else{
        str = WebCTRL.substring(systemDrive.length());
      }
      int i = JOptionPane.showOptionDialog(Main,"WebCTRL Version:","Configuration",JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,fileList,str);
      if (i==JOptionPane.CLOSED_OPTION){
        str = systemDrive+(str==null?fileList[0]:str);
      }else{
        str = systemDrive+fileList[i];
      }
    }
    if (WebCTRL==null || !WebCTRL.equals(str)){
      initLocalPaths(str, updates);
      if (save){
        saveConfig(false);
      }
      return true;
    }
    return false;
  }
  private static void initRemotePaths(String str, boolean updates){
    aces = Paths.get(str);
    acesConfig = aces.resolve("config.txt");
    if (updates){
      update();
    }
  }
  private static void initLocalPaths(String str, boolean updates){
    Matcher m = Patterns.webctrl.matcher(str);
    if (m.find()){
      popupEikon.setText("Launch EIKON "+m.group(1));
    }else{
      popupEikon.setText("Launch EIKON");
    }
    WebCTRL = str;
    localFavorites = Paths.get(WebCTRL+"\\resources\\logicbuilder\\favorites");
    localFavoritesFile = localFavorites.resolve("favorites.xml");
    localPlugins = Paths.get(WebCTRL+"\\resources\\logicbuilder\\logicbuilder.plugins");
    localScripts = Paths.get(WebCTRL+"\\resources\\logicbuilder\\plugins\\script\\scripts");
    localScriptsFile = Paths.get(WebCTRL+"\\resources\\logicbuilder\\plugins\\script\\logic.scripts");
    genScriptFile = localScripts.resolve("Generated Script.logic-script");
    setupWebCTRL(updates);
  }
  private static void editConfigOptions(){
    JCheckBox b3 = new JCheckBox("Auto Update", autoUpdate);
    JCheckBox b4 = new JCheckBox("Update Library", updateLibrary);
    JCheckBox b5 = new JCheckBox("Update Favorites", updateFavorites);
    JCheckBox b6 = new JCheckBox("Update Scripts", updateScripts);
    if (devMode){
      JCheckBox b7 = new JCheckBox("Allow Hidden Entries", hideEntries);
      JCheckBox b8 = new JCheckBox("Allow Locked Entries", lockedEntries);
      JCheckBox b9 = new JCheckBox("Allow Default Selections", defaultSelections);
      JCheckBox b10 = new JCheckBox("Allow Group Minimums", groupMin);
      JCheckBox b11 = new JCheckBox("Allow Group Maximums", groupMax);
      JCheckBox b12 = new JCheckBox("Allow If-Then Statements", suggestEntries);
      Object[] params = {b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,"Remote Directory Path:"};
      Object ret = JOptionPane.showInputDialog(Main,params,"Configuration Options v"+VERSION,JOptionPane.OK_CANCEL_OPTION,icon,null,aces==null?"":aces.toString());
      focus();
      if (ret!=null){
        autoUpdate = b3.isSelected();
        updateLibrary = b4.isSelected();
        updateFavorites = b5.isSelected();
        updateScripts = b6.isSelected();
        hideEntries = b7.isSelected();
        lockedEntries = b8.isSelected();
        defaultSelections = b9.isSelected();
        groupMin = b10.isSelected();
        groupMax = b11.isSelected();
        suggestEntries = b12.isSelected();
        String remlib = (String)ret;
        if (aces==null || (!remlib.isEmpty() && !aces.toString().equals(remlib))){
          initRemotePaths(remlib, true);
        }
        saveConfig(false);
        lib.reset();
        try{
          doLoad();
          info("Library reloaded!");
        }catch(Exception err){
          lib.reset();
          error("Unable to reload library.", err);
        }
        repaint();
      }
    }else{
      Object[] params = {feedbackButton,b3,b4,b5,b6};
      int ret = JOptionPane.showOptionDialog(Main,params,"Configuration Options v"+VERSION,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,icon,null,null);
      focus();
      if (ret==JOptionPane.OK_OPTION){
        autoUpdate = b3.isSelected();
        updateLibrary = b4.isSelected();
        updateFavorites = b5.isSelected();
        updateScripts = b6.isSelected();
        saveConfig(false);
        lib.reset();
        try{
          doLoad();
          info("Library reloaded!");
        }catch(Exception err){
          lib.reset();
          error("Unable to reload library.", err);
        }
        repaint();
      }
    }
  }
  /**
   * Function called before the program terminates
   */
  private static void onExit(){
    if (!onExitCalled){
      onExitCalled = true;
      Logger.log("Application terminated.");
      Logger.log("------------------------------------------------------------------------");
      Logger.close();
    }
  }
}