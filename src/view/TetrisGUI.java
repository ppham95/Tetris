/*
 * TCSS 305 Autumn 2016
 * Assignment 6 - Tetris
 */

package view;

import audio.SoundPlayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.Timer;

import model.Board;


/**
 * Graphical user interface displaying a Tetris game.
 * 
 * @author Phu-Lam Pham
 * @version 9 December 2016
 */
public class TetrisGUI extends JFrame implements Observer, FocusListener {
    
    /** String used to split size menu text to get new dimension for the game. */
    private static final String SPLIT_SIZE_MENU_TEXT_KEY = " x ";

    /** Property change message when a new size is selected. */
    private static final String NEW_SIZE_PROPERTY_CHANGE_MESSAGE = "size";

    /** Property change message when the game is paused. */
    private static final String PAUSE_GAME_PROPERTY_CHANGE_MESSAGE = "paused";
    
    /** The sound file path of the lose sound. */
    private static final String LOSE_SOUND =  "sounds/lose.wav";

    /** The sound file path of the background track. */
    private static final String BACKGROUND_SOUND = "sounds/background_track.wav";
    
    /** The sound file path of the background track. */
    private static final String INTRO_SOUND = "sounds/intro.wav";

    /**  A generated serial version UID for object Serialization. */
    private static final long serialVersionUID = 225429081861081242L;
    
    /** Property change message when the game ends. */
    private static final String END_GAME_PROPERTY_CHANGE_MESSAGE = "endGame";
    
    /** Property change message when circle shape is selected. */
    private static final String CIRCLE_SELECTED_PROPERTY_CHANGE_MESSAGE = "circle";

    /** Default initial delay for timer. */
    private static final int DEFAULT_INITIAL_DELAY = 1000;
    
    /** Default size of the right panel. */
    private static final Dimension DEFAULT_RIGHT_PANEL_SIZE = new Dimension(300, 400);
    
    /** Default background color of the right panel. */
    private static final Color DEFAULT_RIGHT_BACKGROUND_COLOR = new Color(26, 49, 18, 250);
    
    /** Default size for struts in the GUI. */
    private static final int DEFAULT_STRUT_SIZE = 20;
    
    /** Christmas red color. */
    private static final Color CHRISTMAS_RED = new Color(153, 33, 20, 200);
    
    /** Christmas green color. */
    private static final Color CHRISTMAS_GREEN = new Color(26, 49, 18, 200);
    
    /** Christmas gold color. */
    private static final Color CHRISTMAS_GOLD = new Color(197, 164, 54, 200);
    
    /** Christmas blue color. */
    private static final Color CHRISTMAS_BLUE = new Color(42, 143, 189, 200);
    
    /** The Board object managing the current state of the game. */
    private Board myBoard;
    
    /** Timer to set difficulties level of the game. */
    private final Timer myTimer; 
    
    /** If game is over. */
    private boolean myIsGameOver = true;
    
    /** Menu item to end the current game. */
    private JMenuItem myEndGame;
    
    /** Menu item to start a new game. */
    private JMenuItem myNewGame;
    
    /** The panel displaying the preview of next Tetris piece. */
    private NextPiecePreviewPanel myPreviewPanel;
    
    /** The panel displaying the Tetris main game panel. */
    private GamePanel myGamePanel;
    
    /** The sound player to play game sounds. */
    private SoundPlayer mySoundPlayer;
    
    /** The menu selecting size of the Tetris game. */
    private JMenu mySizeMenu;

    /** The panel keeping track of Tetris score. */
    private GameStatsPanel myScorePanel;
    
    
    /**
     * Constructor initializing the default settings of the game.
     */
    public TetrisGUI() {
        super("Tetris!");
        setLayout(new BorderLayout());
        myBoard = new Board();
        
        myTimer = new Timer(DEFAULT_INITIAL_DELAY, new ActionListener() {
            /** 
             * Advance the Tetris Board every set amount of delay.
             * 
             * @param theEvent the timer action event.
             */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                myBoard.down();
            }
        });

        setupSoundAndPreload();
        
        createPanels();
        createMenuBar();
        addKeyListener(new GameKeyListener());
        addFocusListener(this);
    }
    
    /**
     * Private method to create different panels of the GUI.
     */
    private void createPanels() {
        myGamePanel = new GamePanel(myBoard);

        final JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.setBackground(DEFAULT_RIGHT_BACKGROUND_COLOR);
        rightPanel.setPreferredSize(DEFAULT_RIGHT_PANEL_SIZE);
        
        myPreviewPanel = new NextPiecePreviewPanel();
        
        myScorePanel = new GameStatsPanel(myTimer, mySoundPlayer);
        
        rightPanel.add(Box.createVerticalStrut(DEFAULT_STRUT_SIZE));
        rightPanel.add(myPreviewPanel);
        rightPanel.add(Box.createVerticalStrut(DEFAULT_STRUT_SIZE));
        rightPanel.add(myScorePanel);
        
        // These panels listen for changes from the GUI.
        addPropertyChangeListener(myGamePanel);
        addPropertyChangeListener(myPreviewPanel);
        addPropertyChangeListener(myScorePanel);
        
        add(myGamePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    /**
     * Private helper method to create the menu bar.
     */
    private void createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        
        menuBar.add(createFileMenu());
        menuBar.add(createOptionsMenu());
        menuBar.add(createHelpMenu());
          
        setJMenuBar(menuBar);
    }
    
    /**
     * Create the File menu.
     * 
     * @return the File menu.
     */
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);    
        
        myEndGame = new JMenuItem("End Game");
        myEndGame.setMnemonic(KeyEvent.VK_E);
        myEndGame.addActionListener(new ActionListener() {
            /** 
             * End the game on action performed.
             * 
             * @param theEvent the end game option event.
             */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                endGame();
                
            }
        });
        
        myNewGame = new JMenuItem("New Game");
        myNewGame.setMnemonic(KeyEvent.VK_N); 
        myNewGame.addActionListener(new ActionListener() {
            /** 
             * Start a new game on action performed.
             * 
             * @param theEvent the new game option action event.
             */ 
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                startGame();
            }
        });
        
  
        final JMenuItem quit = new JMenuItem("Quit", KeyEvent.VK_Q);
        quit.addActionListener(new ActionListener() {
            /** 
             * Close the game on action event of the Quit button.
             * 
             * @param theEvent the quit option action event.
             */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                dispose();
            }
        });
        
        createSizeMenu(); // Initialize mySizeMenu before adding it to File Menu.
        
        fileMenu.add(myEndGame);
        fileMenu.add(myNewGame);
        fileMenu.add(mySizeMenu);
        fileMenu.addSeparator();
        fileMenu.add(quit);
        
        return fileMenu;
    }
    
    /**
     * Create a size menu inside the File menu as feature to change game size.
     */
    private void createSizeMenu() {
        mySizeMenu = new JMenu("New Game & Size (Current game must end)");
        
        final ButtonGroup sizeGroup = new ButtonGroup();
        
        // Not the most ideal way to add ActionListeners for these buttons,
        // but if we make an action class for these buttons, we would need reference
        // to the board, end game and new me buttons, and the startGame() method 
        // of this GUI. That could cause a lot of tight couplings.
        final JRadioButtonMenuItem defaultSize = new JRadioButtonMenuItem("10 x 20");
        defaultSize.setSelected(true);
        defaultSize.addActionListener(new ActionListener() {
            /** 
             * Set the new game to 10 x 20.
             * 
             * @param the 10x20 game option action event.
             */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                final String[] dimension = defaultSize.getText().
                                split(SPLIT_SIZE_MENU_TEXT_KEY);
                myBoard = new Board(Integer.parseInt(dimension[0]), 
                                    Integer.parseInt(dimension[1]));
                firePropertyChange(NEW_SIZE_PROPERTY_CHANGE_MESSAGE, null, myBoard);
                startGame();
            }
        });
        
        final JRadioButtonMenuItem fifteenByFifteen = new JRadioButtonMenuItem("15 x 15");
        fifteenByFifteen.addActionListener(new ActionListener() {
            /** 
             * Set the new game to 15 x 15.
             * 
             * @param the 15x15 game option action event.
             */ 
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                final String[] dimension = fifteenByFifteen.getText().
                                split(SPLIT_SIZE_MENU_TEXT_KEY);
                myBoard = new Board(Integer.parseInt(dimension[0]), 
                                    Integer.parseInt(dimension[1]));
                firePropertyChange(NEW_SIZE_PROPERTY_CHANGE_MESSAGE, null, myBoard);
                startGame();
            }
        });
        
        final JRadioButtonMenuItem fifteenByThirty = new JRadioButtonMenuItem("15 x 30");
        fifteenByThirty.addActionListener(new ActionListener() {
            /** 
             * Set the new game to 15 x 30.
             * 
             * @param the 15x30 game option action event.
             */
            
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                final String[] dimension = fifteenByThirty.getText().
                                split(SPLIT_SIZE_MENU_TEXT_KEY);
                myBoard = new Board(Integer.parseInt(dimension[0]), 
                                    Integer.parseInt(dimension[1]));
                firePropertyChange(NEW_SIZE_PROPERTY_CHANGE_MESSAGE, null, myBoard);
                startGame();
            }
        });
        
        sizeGroup.add(defaultSize);
        sizeGroup.add(fifteenByFifteen);
        sizeGroup.add(fifteenByThirty);
        
        mySizeMenu.add(defaultSize);
        mySizeMenu.add(fifteenByFifteen);
        mySizeMenu.add(fifteenByThirty);
    }
    
    /**
     * Create the options menu.
     * 
     * @return the created options menu.
     */
    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_O);
        
        addCheckBoxOptions(optionsMenu);
        addColorOptions(optionsMenu);
        addShapeOptions(optionsMenu);
        
        return optionsMenu;
    }
    
    /**
     * Create the help menu.
     * 
     * @return the help menu.
     */
    private JMenu createHelpMenu() {
        final JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H); 
        
        final JMenuItem howToPlay = new JMenuItem("How To Play...");
        howToPlay.setMnemonic(KeyEvent.VK_H); 
        howToPlay.addActionListener(new ActionListener() {
             /** Pop up a message dialog explaining the game's controls.
              * @param theEvent the help dialog action event.
              */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                // Extra spaces to align the words nicely after :
                JOptionPane.showMessageDialog(null, "Control Buttons:"
                                + "\nMove Left:                  Left Arrow" 
                                + "\nMove Right:                Right Arrow" 
                                + "\nMoveDown:                Down Arrow" 
                                + "\nRotate Clockwise:   Space"
                                + "\nDrop the piece:         Enter"
                                + "\n\nScoring Rules:"
                                + "\n 1-3 Lines cleared:    100 points per line"
                                + "\n 4 Lines cleared:        800 points"
                                + "\n\n Difficulty:"
                                + "\n Increase difficulty level by one every 5 lines cleared");
            } 
        });
        
        final JMenuItem about = new JMenuItem("About...");
        about.setMnemonic(KeyEvent.VK_A); 
        about.addActionListener(new ActionListener() {
            /** 
             * Pop up a message dialog description about the game.
             * 
             * @param theEvent the about dialog action event.
             */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                JOptionPane.showMessageDialog(null, "TCSS 305 Autumn 2016"
                                + "\nPhu-Lam Pham"
                                + "\n\nAll sound effects and music used in this program can be"
                                + "found at:"
                                + "\nhttp://soundbible.com/tags-christmas.html"
                                + "\nhttps://www.westnet.com/Holiday/midi/"
                                + "\n\nThe background used in this program can be found at"
                                + "\nhttp://gallery.yopriceville.com/"
                                + "Backgrounds/Wooden_Square_Christmas_Background_"
                                + "with_Pine_Branches_and_Ornaments");
            } 
        });
        
        helpMenu.add(howToPlay);
        helpMenu.add(about);
        
        return helpMenu;
    }

    /**
     * Add the Tetris block shape options into options menu.
     * 
     * @param theOptionsMenu the options menu item.
     */
    private void addShapeOptions(final JMenu theOptionsMenu) {
        final ButtonGroup shapeGroup = new ButtonGroup();
        final JRadioButtonMenuItem rect = new JRadioButtonMenuItem("Rectangle");
        rect.setSelected(true);
        rect.addActionListener(new ActionListener() {
            /** Select the rectangle shape block on action performed.
             * 
             * @param theEvent the rectangle shape action event.
             */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                firePropertyChange(CIRCLE_SELECTED_PROPERTY_CHANGE_MESSAGE, 
                                   null, !rect.isSelected());
            }
        });
        
        final JRadioButtonMenuItem circle = new JRadioButtonMenuItem("Circle");
        circle.setSelected(true);
        circle.addActionListener(new ActionListener() {
            /** Select the circle shape block on action performed. 
             * 
             * @param theEvent the circle shape action event.
             */
            
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                firePropertyChange(CIRCLE_SELECTED_PROPERTY_CHANGE_MESSAGE, 
                                   null, circle.isSelected());
            }
        });
        
        shapeGroup.add(rect);
        shapeGroup.add(circle);
        
        theOptionsMenu.addSeparator();
        theOptionsMenu.add(rect);
        theOptionsMenu.add(circle);
    }

    /**
     * Add Tetris blocks coloring option into options menu.
     * 
     * @param theOptionsMenu the options menu item.
     */
    private void addColorOptions(final JMenu theOptionsMenu) {
        final ButtonGroup colorGroup = new ButtonGroup();
        
        final JRadioButtonMenuItem green = new JRadioButtonMenuItem("Green");
        colorGroup.add(green);
        green.setSelected(true);
        green.addActionListener(new ColorAction(CHRISTMAS_GREEN, myGamePanel, myPreviewPanel));
        
        final JRadioButtonMenuItem red = new JRadioButtonMenuItem("Red");
        colorGroup.add(red);
        red.addActionListener(new ColorAction(CHRISTMAS_RED, myGamePanel, myPreviewPanel));
        
        final JRadioButtonMenuItem gold = new JRadioButtonMenuItem("Gold");
        colorGroup.add(gold);
        gold.addActionListener(new ColorAction(CHRISTMAS_GOLD, myGamePanel, myPreviewPanel));
        
        final JRadioButtonMenuItem blue = new JRadioButtonMenuItem("Blue");
        colorGroup.add(blue);
        blue.addActionListener(new ColorAction(CHRISTMAS_BLUE, myGamePanel, myPreviewPanel));
        
        final JRadioButtonMenuItem light = new JRadioButtonMenuItem("Light them up!");
        colorGroup.add(light);
        light.addActionListener(new ColorAction(null, myGamePanel, myPreviewPanel));
        
        theOptionsMenu.addSeparator();
        
        theOptionsMenu.add(green);
        theOptionsMenu.add(red);
        theOptionsMenu.add(gold);
        theOptionsMenu.add(blue);
        theOptionsMenu.add(light);  
    }

    /**
     * Add options with check boxes to the options menu.
     * 
     * @param theOptionsMenu the options menu.
     */
    private void addCheckBoxOptions(final JMenu theOptionsMenu) {
        final JCheckBoxMenuItem muteSounds = new JCheckBoxMenuItem("Mute Sounds");
        muteSounds.setMnemonic(KeyEvent.VK_M);
        muteSounds.addActionListener(new ActionListener() {
            /** 
             * Change the mute option.
             * 
             * @param theEvent the mute option action event.
             */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                mySoundPlayer.setMuteOption(muteSounds.isSelected());

                if (!muteSounds.isSelected() && !myIsGameOver) {
                    mySoundPlayer.loop(BACKGROUND_SOUND);
                }
            }
            
        });
        
        final JCheckBoxMenuItem showGrids = new JCheckBoxMenuItem("Show Grids");
        showGrids.setMnemonic(KeyEvent.VK_G);
        showGrids.addActionListener(new ActionListener() {
            /** 
             * Show/Hide grids.. 
             * 
             * @param theEvent the grid option action event.
             */
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                firePropertyChange("grid", null, showGrids.isSelected());
            }
            
        });
        
        theOptionsMenu.add(muteSounds);
        theOptionsMenu.addSeparator();
        theOptionsMenu.add(showGrids);
        
    }
    
    /**
     * Add/or update observers of a new game.
     */
    private void addObservers() {
        myBoard.addObserver(this);
        myBoard.addObserver(myGamePanel);
        myBoard.addObserver(myScorePanel);
        myBoard.addObserver(myPreviewPanel);
    }


    /**
     * Initialize the sound player and pre-load sounds files that will
     * be played from this GUI.
     */
    private void setupSoundAndPreload() {
        mySoundPlayer = new SoundPlayer();
        mySoundPlayer.preLoad(INTRO_SOUND);
        mySoundPlayer.preLoad(BACKGROUND_SOUND);
        mySoundPlayer.preLoad(LOSE_SOUND);
    }
    
    /** Switch the enable states of new and end game buttons .*/
    private void switchFileMenuButtonsState() {
        // Cannot start a new game without ending the current game.
        myEndGame.setEnabled(!myIsGameOver);
        
        myNewGame.setEnabled(myIsGameOver);
        mySizeMenu.setEnabled(myNewGame.isEnabled());
    }
    
    /**
     * Start/or Restart the game and change the GUI acoordingly.
     */
    private void startGame() {
        
        // If new size is selected, the program needs to create a new Board,
        // hence the new board needs to add the observers that the game is using.
        addObservers();
        
        myBoard.newGame();

        myTimer.setDelay(DEFAULT_INITIAL_DELAY);
        myTimer.start();
        
        mySoundPlayer.stopAll();
        mySoundPlayer.loop(BACKGROUND_SOUND);
        myIsGameOver = false;
        switchFileMenuButtonsState();
        firePropertyChange(END_GAME_PROPERTY_CHANGE_MESSAGE, null, false);
    }
    
    /**
     * End the game and change the GUI accordingly.
     */
    private void endGame() {
        mySoundPlayer.stopAll();
        myIsGameOver = true;
        myTimer.stop();
        mySoundPlayer.play(LOSE_SOUND);
        switchFileMenuButtonsState();
        firePropertyChange(END_GAME_PROPERTY_CHANGE_MESSAGE, null, true);
    }
    
    /**
     * Pause/Or Resume the game.
     */
    private void pauseOrResumeGame() {
        if (myTimer.isRunning() && !myIsGameOver) {
            myTimer.stop();
        } else {
            myTimer.start();
        }
        firePropertyChange(PAUSE_GAME_PROPERTY_CHANGE_MESSAGE, null, !myTimer.isRunning());
    }
    
    /**
     * Update the GUI based on information received from the Observable model.
     * 
     * @param theObservable the observable class/object that the panel is observing.
     * @param theObject the object data received from the observable.
     */
    @Override
    public void update(final Observable theObservable, final Object theObject) {
        // End the game if notified by observable that the game is over.
        if (theObject instanceof Boolean) {
            endGame();
        }
    }
    
    /** 
     * Resume the game if the component gains focus.
     * 
     * @param theEvent the focus gained event.
     */
    @Override
    public void focusGained(final FocusEvent theEvent) {
        if (!myTimer.isRunning() && !myIsGameOver) {
            myTimer.start();
            firePropertyChange(PAUSE_GAME_PROPERTY_CHANGE_MESSAGE, null, false);
        }
    }
    
    /**
     * Pause the game if the component loses focus.
     * 
     * @param theEvent the focus lost event.
     */
    @Override
    public void focusLost(final FocusEvent theEvent) {
        if (myTimer.isRunning() && !myIsGameOver) {
            myTimer.stop();
            firePropertyChange(PAUSE_GAME_PROPERTY_CHANGE_MESSAGE, null, true);
        }
    }
    
    /**
     * Start and display the game.
     */
    public void start() {
        mySoundPlayer.loop(INTRO_SOUND);
        switchFileMenuButtonsState();
        pack();
        setMinimumSize(new Dimension(getWidth(), getHeight()));
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Private inner KeyListener class for control functions of the game.
     * 
     * @author Phu-Lam Pham
     * @version 9 December 2016
     */
    private class GameKeyListener extends KeyAdapter {
        
        /**
         * Default constructor of KeyListener.
         */
        GameKeyListener() {
            super();
        }
        
        /**
         * Listens for keys pressed that are controlling the game.
         * 
         * @param theEvent the key pressed event.
         */
        @Override
        public void keyPressed(final KeyEvent theEvent) {
            final int key = theEvent.getKeyCode();
            
            if (!myIsGameOver) {
                if (key == KeyEvent.VK_P) {
                    pauseOrResumeGame(); // This method can stop or restart the timer.
                }
                
                if (myTimer.isRunning()) {
                    switch (key) {
                        case KeyEvent.VK_LEFT:
                            myBoard.left();
                            break;

                        case KeyEvent.VK_RIGHT:
                            myBoard.right();
                            break;
                            
                        case KeyEvent.VK_DOWN:
                            myBoard.down();
                            break;

                        case KeyEvent.VK_SPACE:
                            myBoard.rotate();
                            break;

                        case KeyEvent.VK_ENTER:
                            myBoard.drop();
                            break;

                        default:
                            break;
                    }
                }
            }
        }
    }
}
