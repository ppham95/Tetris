/*
 * TCSS 305 Autumn 2016
 * Assignment 6 - Tetris
 */

package view;

import audio.SoundPlayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * JPanel displaying the current score and stats of Tetris game.
 * 
 * @author Phu-Lam Pham
 * @version 9 December 2016
 */
public class GameStatsPanel extends JPanel implements Observer, PropertyChangeListener {
    
    /** The sound file path one line cleared sound. */
    private static final String ONE_LINE_CLEARED_SOUND = "sounds/snowball.wav";

    /** The sound file path of the bonus points sound. */
    private static final String BONUS_POINTS_SOUND = "sounds/hohoho.wav";

    /** The sound file path of the lose sound. */
    private static final String LEVEL_UP_SOUND = "sounds/levelup.wav";

    /**  A generated serial version UID for object Serialization. */
    private static final long serialVersionUID = 821718255933296479L;

    /** Points granted for 4 lines cleared in a turn. */
    private static final int FOUR_LINES_CLEARED_SCORE = 800;
    
    /** Points granted for each line cleared. */
    private static final int ONE_LINE_CLEARED_SCORE = 100;
    
    /** Lines needed to clear in a turn for bonus points. */
    private static final int NUM_LINES_CLEARED_FOR_BONUS = 4;
    
    /** Number of lines needed to cleared for one level. */
    private static final int LINES_CLEARED_PER_NEXT_LEVEL = 5;
    
    /** Default initial delay for timer. */
    private static final int DEFAULT_INITIAL_DELAY = 1000;
    
    /** The amount of delay being subtracted per difficulty level. */
    private static final int LESS_DELAY_TIME_PER_LEVEL = 75;
    
    /** Default size of the panel. */
    private static final Dimension DEFAULT_PANEL_SIZE = new Dimension(250, 270);
    
    /** Default color of the panel. */
    private static final Color DEFAULT_PANEL_COLOR = new Color(197, 164, 54);

    /** The border size of the panel. */
    private static final int BORDER_SIZE = 7;

    /** The default font size of the label of the panel. */
    private static final int DEFAULT_LABEL_FONT_SIZE = 30;

    /** The padding between message lines. */
    private static final int LINE_PADDING = 70;

    /** The left padding of each message lines. */
    private static final int LEFT_PADDING = 20;

    /** Default font for text messages of the panel. */
    private static final Font DEFAULT_FONT = new Font("Verdana", Font.ITALIC, 12);
    
    /** Timer to set difficulties level of the game. */
    private final Timer myTimer;
    
    /** The score of the game. */
    private int myScore;
    
    /** Number of lines cleared in the Tetris game. */
    private int myLinesCleared;
    
    /** The list of different statistic messages. */
    private final List<String> myStatMessageList;
    
    /** The sound player to play game sounds. */
    private final SoundPlayer mySoundPlayer;
    
    /**
     * Constructor initializing the statistics panel of Tetris game.
     * 
     * @param theTimer timer to control difficulties level of the game.
     * @param theSoundPlayer the sound player to play game sounds.
     */
    public GameStatsPanel(final Timer theTimer, final SoundPlayer theSoundPlayer) {
        super();
        
        setupPanel();
        
        myStatMessageList = new ArrayList<String>();
        myTimer = theTimer;
        myScore = 0;
        myLinesCleared = 0;
        
        mySoundPlayer = theSoundPlayer;
        loadSoundFiles();
        
        updateStatMessageList();
    }
    
    /**
     * Setup game statistic panel.
     */
    private void setupPanel() {
        setMaximumSize(DEFAULT_PANEL_SIZE);
        this.setBackground(DEFAULT_PANEL_COLOR);
        
        setBorder(BorderFactory.createLineBorder(Color.WHITE, BORDER_SIZE));
        final JLabel label = new JLabel("Game Stats:");
        label.setFont(new Font("Garamond", Font.BOLD, DEFAULT_LABEL_FONT_SIZE));
        add(label);
        
    }
    
    /**
     * Load sound files that will be played from this frame.
     */
    private void loadSoundFiles() {
        mySoundPlayer.preLoad(BONUS_POINTS_SOUND);
        mySoundPlayer.preLoad(LEVEL_UP_SOUND);
        mySoundPlayer.preLoad(ONE_LINE_CLEARED_SOUND);
    }
    
    /**
     * Paint the Tetris game statistic messages.
     * 
     * @param theGraphics the graphics object used for drawing.
     */
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setFont(DEFAULT_FONT);
        
        for (int i = 0; i < myStatMessageList.size(); i++) {
            g2d.drawString(myStatMessageList.get(i), LEFT_PADDING, 
                           LINE_PADDING + LEFT_PADDING * i * 2);
        }

        
    }
    
    /**
     * Get the current difficulty level of the game.
     * 
     * @return the current difficulty level of the game.
     */
    private int getLevel() {
        return 1 + myLinesCleared / LINES_CLEARED_PER_NEXT_LEVEL;
    }
    
    /**
     * Update the game stat messages.
     */
    private void updateStatMessageList() {
        myStatMessageList.clear();
        myStatMessageList.add("Current Score:                 " + myScore);
        myStatMessageList.add("Lines Cleared:                 " + myLinesCleared);
        myStatMessageList.add("Lines until next level:     " 
                        + (LINES_CLEARED_PER_NEXT_LEVEL 
                                        - myLinesCleared % LINES_CLEARED_PER_NEXT_LEVEL));
        myStatMessageList.add("Current Level:                " + getLevel());
        myStatMessageList.add("Current timer delay:       " + myTimer.getDelay());
    }
    
    /**
     * Receive property changes from the main GUI and change the game's statistics accordingly.
     * 
     * @param theEvent the property change event.
     */
    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {
        if (theEvent.getPropertyName().equals("endGame")) {
            if (theEvent.getNewValue().equals(true)) {
                // No need to repaint or update messages yet since player 
                // would still want to see their score when game is over.
                myLinesCleared = 0;
                myScore = 0;
            } else {
                updateStatMessageList();
            }
        } else {
            updateStatMessageList();
        }
    }

    /**
     * Update the statistic panel when being notified by the observable.
     * 
     * @param theObservable the observable class/object that the panel is observing.
     * @param theObject the object data received from the observable.
     */
    @Override
    public void update(final Observable theObservable, final Object theObject) {
        if (theObject instanceof Integer[]) {

            final int linesCleared = ((Integer[]) theObject).length;
            final int oldLevel = getLevel();
            
            // getLevel() method interprets myLinesCleared to get current level.
            myLinesCleared += linesCleared;
            if (getLevel() > oldLevel) {
                mySoundPlayer.play(LEVEL_UP_SOUND);
            }
            
            // Tetris game can only clear at max 4 rows per turn.
            if (linesCleared == NUM_LINES_CLEARED_FOR_BONUS) {
                myScore += FOUR_LINES_CLEARED_SCORE;
                mySoundPlayer.play(BONUS_POINTS_SOUND);
            } else {
                myScore += ONE_LINE_CLEARED_SCORE * linesCleared;
                mySoundPlayer.play(ONE_LINE_CLEARED_SOUND);
            }
            
            myTimer.setDelay(DEFAULT_INITIAL_DELAY 
                             - myLinesCleared / LINES_CLEARED_PER_NEXT_LEVEL 
                             * LESS_DELAY_TIME_PER_LEVEL);
            updateStatMessageList();
            repaint();
        }
    }
}
