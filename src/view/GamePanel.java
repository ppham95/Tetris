/*
 * TCSS 305 Autumn 2016
 * Assignment 6 - Tetris
 */

package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import model.Board;

/**
 * JPanel drawing the main game panel of a Tetris game.
 * 
 * @author Phu-Lam Pham
 * @version 9 December 2016
 */
public class GamePanel extends JPanel implements Observer, PropertyChangeListener {
    
    /**  A generated serial version UID for object Serialization. */
    private static final long serialVersionUID = 306985486783684717L;
    
    /** Number of lines not needed for the GUI display received from the model of the game. */
    private static final int UNUSED_BOARD_LINES = 5;
    
    /** Default background color of the game panel. */
    private static final Color BACKGROUND_COLOR = new Color(153, 33, 20);
    
    /** Message to display when game is over. */
    private static final String GAME_OVER_MESSAGE = "Game Over";
    
    /** The default size of a block. */
    private static final int BLOCK_SIZE = 30;

    /** Layout color of the Tetris board. */
    private static final Color LAYOUT_COLOR = new Color(153, 33, 20, 127);

    /** File path of background image. */
    private static final String BACKGROUND_IMAGE_PATH = "images/background.jpg";

    /** List of different Christmas colors used in the panel. */
    private static final Color[] CHRISTMAS_COLORS = 
    {new Color(153, 33, 20, 200), 
        new Color(26 , 49, 18, 200), 
        new Color(197, 164, 54, 200), 
        new Color(42, 143, 189, 200)};
    
    /** String representation of current game state. */
    private String[] myCurrentGame;
    
    /** If game is over. */
    private boolean myGameOver;
    
    /** If game is paused. */
    private boolean myIsPaused;
    
    /** The Board object managing the current state of the game. */
    private Board myBoard;
    
    /** The width of the game panel. */
    private final int myGameWidth;
    
    /** The height of the game panel. */
    private final int myGameHeight;
    
    /** The Rectangle object used to draw rectangle shapes in the panel. */
    private final RoundRectangle2D myRect;
    
    /** The Line object used to draw lines in the panel. */
    private final Line2D myLine;

    /** If special color mode is selected. */
    private boolean myIsSpecialColor;
    
    /** The color for Tetris blocks. */
    private Color myBlockColor = CHRISTMAS_COLORS[1];
    
    /** If Tetris block shape circle is selected. */
    private boolean myIsCircleSelected;

    /** If showing grid option is enabled. */
    private boolean myIsGridEnabled;
    
    /** The ratio of game height to game width .*/
    private int myHeightToWidthRatio;
    
    /**
     * Constructor initialize the game panel with default settings.
     * 
     * @param theBoard the Board object managing the current state of the game.
     */
    public GamePanel(final Board theBoard) {
        super();
        myBoard = theBoard;
        myGameWidth = theBoard.getWidth() * BLOCK_SIZE;
        myGameHeight = theBoard.getHeight() * BLOCK_SIZE;
        if (myBoard.getHeight() == myBoard.getWidth()) {
            myHeightToWidthRatio = 1;
        } else {
            myHeightToWidthRatio = 2;
        }

        myRect = new RoundRectangle2D.Double();
        myLine = new Line2D.Double();
       
        setupPanel();
       
    }
    
    /**
     * Setup the game panel.
     */
    private void setupPanel() {
        setName("Game Panel");
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(myGameHeight, myGameHeight));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    }
    
    /**
     * Paint the current game state.
     * 
     * @param theGraphics the graphics object used for drawing.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        final double scale = getScale();
        double paddingX = 0;
        double paddingY = 0;
        
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Different conditions to obtain the reasonable
        // padding of the game relative to window size.
        if (myGameWidth < myGameHeight) {
            if (getWidth() > (getHeight() / myHeightToWidthRatio)) {
                paddingX = getWidth() / 2.0 - scale * myBoard.getWidth() / 2.0;
            } else {
                paddingY = getHeight() / 2.0 - scale * myBoard.getHeight() / 2.0;
            }
        } else {
            if (getHeight() > (getWidth() / myHeightToWidthRatio)) {
                paddingY = getHeight() / 2.0 - scale * myBoard.getHeight() / 2.0;
            } else {
                paddingX = getWidth() / 2.0 - scale * myBoard.getWidth() / 2.0;
            }
        }
        

        drawBackGroundAndLayout(g2d, scale, paddingX, paddingY);
        
        // To prevent showing the game while paused
        if (myIsPaused) {
            drawMessage(g2d, "Paused", scale);
        } else {
            if (myCurrentGame != null) {
                drawGame(g2d, scale, paddingX, paddingY);
            }
        }
        
        // Will allow user to see their game after the game is over.
        if (myGameOver) {
            drawMessage(g2d, GAME_OVER_MESSAGE, scale);
        }
    }
    
    /**
     * Get the scale to draw objects in this panel based on current panel size.
     * 
     * @return the scale to draw objects in this panel based on current panel size.
     */
    private double getScale() {
        double scale = 0;
        
        // Different conditions to obtain the reasonable
        // scale of everything in the panel based on the board's dimension and 
        // the panel's current size.
        if (myGameWidth < myGameHeight) {
            if (getWidth() <= getHeight() / myHeightToWidthRatio) {
                scale = 1.0 * getWidth() / myBoard.getWidth();
            } else {
                scale = 1.0 * getHeight() / myBoard.getHeight();
            }
        } else {
            if (getHeight() < getWidth() / myHeightToWidthRatio) {
                scale = 1.0 * getHeight() / myBoard.getHeight();
            } else  {
                scale = 1.0 * getWidth() / myBoard.getWidth();
            }
        }
        
        return scale;
    }
    
    /** Draw the background and game panel layout. 
     * 
     * @param theGraphics the Graphics object used for drawing.
     * @param theScale the scale to draw objects in this panel based on current panel size.
     * @param thePaddingX the horizontal padding.
     * @param thePaddingY the vertical padding.
     */
    private void drawBackGroundAndLayout(final Graphics2D theGraphics, final double theScale, 
                          final double thePaddingX, final double thePaddingY) {
        
        // Always paint background image first.
        Image img = null;
        try {
            img = ImageIO.read(new File(BACKGROUND_IMAGE_PATH));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        
        if (img != null) {
            theGraphics.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
        
        theGraphics.setColor(Color.WHITE);
        
        // Show grids option.
        if (myIsGridEnabled) {
            theGraphics.setStroke(new BasicStroke(1));
            for (int i = 0; i <= myBoard.getHeight(); i++) {
                myLine.setLine(thePaddingX, i * theScale + thePaddingY, 
                               getWidth() - thePaddingX, i * theScale + thePaddingY);
                theGraphics.draw(myLine);
            }
            
            for (int i = 1; i <= myBoard.getWidth() - 1; i++) {
                myLine.setLine(i * theScale + thePaddingX, thePaddingY,  
                               i * theScale + thePaddingX,  getHeight() + thePaddingY);
                theGraphics.draw(myLine);
            }
        }
        
        
        theGraphics.setStroke(new BasicStroke(2));
        
        // The half transparent layout under the area that Tetris game is using.
        myRect.setRoundRect(thePaddingX, thePaddingY, myBoard.getWidth() * theScale, 
                                                     myBoard.getHeight() * theScale, 0, 0);
        
        theGraphics.setColor(LAYOUT_COLOR);
        theGraphics.fill(myRect);
    }
    
    /**
     * Draw the playing game.
     * 
     * @param theGraphics the Graphics object used for drawing.
     * @param theScale the scale to draw objects in this panel based on current panel size.
     * @param thePaddingX the horizontal padding.
     * @param thePaddingY the vertical padding.
     */
    private void drawGame(final Graphics2D theGraphics, final double theScale, 
                          final double thePaddingX, final double thePaddingY) {
        theGraphics.setStroke(new BasicStroke(2));
        
        double roundness = 0;
        if (myIsCircleSelected) {
            
            // Using Rounded Rectangle to draw circles.
            // theScale gives a perfect roundness value to make the rectangle a circle.
            roundness = theScale;
        }
        
        for (int i = 0; i < myCurrentGame.length - 1; i++) {
            for (int j = 1; j < myCurrentGame[i].length() - 1; j++) {
                if (myCurrentGame[i].charAt(j) != ' ') {
                    myRect.setRoundRect((j - 1) * theScale + thePaddingX, 
                                        i * theScale + thePaddingY, theScale, theScale, 
                                        roundness, roundness);
                    
                    // Set block/fill color based on color selected or special color mode.
                    if (myIsSpecialColor) {
                        theGraphics.setColor(CHRISTMAS_COLORS
                                             [new Random().
                                              nextInt(CHRISTMAS_COLORS.length)]);
                    } else {
                        theGraphics.setColor(myBlockColor);
                    }

                    theGraphics.fill(myRect);
                    theGraphics.setPaint(Color.WHITE);
                    theGraphics.draw(myRect);
                }
            }
        }    
    }

    /**
     * Draw a message based on current game's state.
     * 
     * @param theGraphics the Graphics object used for drawing.
     * @param theMessage the message to be painted.
     * @param theScale the scale to draw objects in this panel based on current panel size.
     */
    private void drawMessage(final Graphics2D theGraphics, final String theMessage, 
                             final double theScale) {
        
        theGraphics.setFont(new Font("TimesRoman", Font.BOLD, (int) theScale));
        
        // Using FontMetrics to get the precise center of the string.
        final FontMetrics fm = theGraphics.getFontMetrics();
        final int stringWidth = fm.stringWidth(theMessage);
        
        theGraphics.setColor(Color.WHITE);
        theGraphics.drawString(theMessage, (getWidth() - stringWidth) / 2, getHeight() / 2);
    }
    
    /**
     * Receive and update state of the game based on property changes received.
     * 
     * @param theEvent the property change event received.
     */
    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {
        if (theEvent.getPropertyName().equals("paused")) {
            myIsPaused = (boolean) theEvent.getNewValue();
        } else if (theEvent.getPropertyName().equals("endGame")) {
            myGameOver = (boolean) theEvent.getNewValue();
            myIsPaused = false;
            
        // For extra features of colors and sizes and grids
            
        } else if ("color".equals(theEvent.getPropertyName())) {
            if (theEvent.getNewValue() instanceof Color) {
                myBlockColor = (Color) theEvent.getNewValue();
                myIsSpecialColor = false;
            } else {
                myIsSpecialColor = true;
            }
        } else if ("grid".equals(theEvent.getPropertyName())) {
            myIsGridEnabled = (boolean) theEvent.getNewValue();
        } else if ("circle".equals(theEvent.getPropertyName())) {
            myIsCircleSelected = (boolean) theEvent.getNewValue();
        } else if ("size".equals(theEvent.getPropertyName())) {
            myBoard = (Board) theEvent.getNewValue();
        }
        
        repaint();
    }

    /**
     * Update the game state when being notified from the observable.
     * 
     * @param theObservable the observable class/object that the panel is observing.
     * @param theObject the object data received from the observable.
     */
    @Override
    public void update(final Observable theObservable, final Object theObject) {
        if (theObject instanceof String) {
            myCurrentGame = theObject.toString().split("\n");
            
            // The first 5 lines of the notified String object isn't needed to draw the game.
            myCurrentGame = Arrays.copyOfRange(myCurrentGame, UNUSED_BOARD_LINES, 
                                               myCurrentGame.length);
        } else if (theObject instanceof Boolean) {
            myGameOver = (boolean) theObject;
        }
        repaint();
    }
}
