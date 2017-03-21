/*
 * TCSS 305 Autumn 2016
 * Assignment 6 - Tetris
 */

package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.MovableTetrisPiece;

/**
 * JPanel drawing the next piece of a Tetris game.
 * 
 * @author Phu-Lam Pham
 * @version 9 December 2016
 */
public class NextPiecePreviewPanel extends JPanel implements Observer, PropertyChangeListener {
    
    /**  A generated serial version UID for object Serialization. */
    private static final long serialVersionUID = 2329425972910077478L;

    /** Default panel size. */
    private static final Dimension DEFAULT_SIZE = new Dimension(150, 150);
    
    /** Default panel background color. */
    private static final Color DEFAULT_COLOR = new Color(210, 230, 190);
    
    /** Number of blocks per column and row in this panel. */
    private static final int NUM_OF_BLOCKS_PER_COL_AND_ROW = 5;
    
    /** Ratio related to size used to draw objects with shorter padding. */
    private static final int SHORTER_PADDING_RATIO = 10;
    
    /** List of different Christmas color. */
    private static final Color[] CHRISTMAS_COLORS = 
    {new Color(153, 33, 20, 200), 
        new Color(26 , 49, 18, 200), 
        new Color(197, 164, 54, 200), 
        new Color(42, 143, 189, 200)};
    
    /** Fill color of the blocks. */
    private static final Color DEFAULT_BLOCK_COLOR = CHRISTMAS_COLORS[1];
    
    /** Default font size of the panel. */
    private static final int DEFAULT_FONT_SIZE = 20;
    
    /** Border size of the panel. */
    private static final int BORDER_SIZE = 10;
    
    /** String representation of the next piece. */
    private String[] myNextPiece;
    
    /** The Rectangle object used to draw rectangle shapes in the panel. */
    private final RoundRectangle2D myRect;
    
    /** If Tetris block shape circle is selected. */
    private boolean myIsCircleSelected;

    /** If special color mode is selected. */
    private boolean myIsSpecialColor;
    
    /** The color of Tetris blocks. */
    private Color myBlockColor;
    
    /**
     * Constructor initializing default settings of the panel.
     */
    public NextPiecePreviewPanel() {
        super();
        this.setBackground(DEFAULT_COLOR);
        setMaximumSize(DEFAULT_SIZE);
        myRect = new RoundRectangle2D.Double();
        myBlockColor = DEFAULT_BLOCK_COLOR;
        
        setBorder(BorderFactory.createLineBorder(DEFAULT_BLOCK_COLOR, BORDER_SIZE));

        
        final JLabel label = new JLabel("Next Piece");
        label.setFont(new Font("Serif", Font.ITALIC, DEFAULT_FONT_SIZE));
        add(label);
    }
    
    /**
     * Paint the next piece panel.
     * 
     * @param theGraphics object used for drawing.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        if (myNextPiece != null) {
            final int blockSize = getWidth() / NUM_OF_BLOCKS_PER_COL_AND_ROW;
            final int longPadding = getWidth() / SHORTER_PADDING_RATIO;
            int roundness = 0;
            
            if (myIsCircleSelected) {
                roundness = blockSize;
            }
            
            drawNextPiece(g2d, blockSize, longPadding, roundness);
        }
    }
    
    /**
     * Draw the preview of next Tetris piece.
     * 
     * @param theGraphics the Graphics object used for drawing.
     * @param theBlockSize the size of each block.
     * @param theLongPadding the long padding for the piece.
     * @param theRoundness the roundness of the block shape.
     */
    private void drawNextPiece(final Graphics2D theGraphics, final int theBlockSize, 
                               final int theLongPadding, final int theRoundness) {
        theGraphics.setStroke(new BasicStroke(2));
        
        for (int i = 0; i < myNextPiece.length; i++) {
            for (int j = 0; j < myNextPiece[i].length(); j++) {
                if (myIsSpecialColor) {
                    theGraphics.setColor(CHRISTMAS_COLORS
                                    [new Random().nextInt(CHRISTMAS_COLORS.length)]);
                } else {
                    theGraphics.setColor(myBlockColor);
                }
                if (myNextPiece[i].charAt(j) != ' ' 
                                && myNextPiece[i].charAt(j) != '|' 
                                && myNextPiece[i].charAt(j) != '-') {
                    
                    // Block I and O have different starting locations than other blocks.
                    if (myNextPiece[i].charAt(j) == 'I' 
                                    || myNextPiece[i].charAt(j) == 'O') {
                        
                        myRect.setRoundRect(theLongPadding + j * theBlockSize, 
                                            theLongPadding + i * theBlockSize, 
                                     theBlockSize, theBlockSize, theRoundness, theRoundness);
                        
                        theGraphics.fill(myRect);
                        theGraphics.setColor(Color.WHITE);
                        theGraphics.draw(myRect);
                    } else {
                        
                        myRect.setRoundRect(theBlockSize + j * theBlockSize, 
                                            theLongPadding + i * theBlockSize, 
                                       theBlockSize, theBlockSize, theRoundness, theRoundness);
                        
                        theGraphics.fill(myRect);
                        theGraphics.setColor(Color.WHITE);
                        theGraphics.draw(myRect);
                    }
                }
            }
        }
    }
    
    /**
     * Receive property changes from the main GUI and change the preview panel's 
     * look accordingly.
     * 
     * @param theEvent the property change event received.
     */
    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {
        if ("color".equals(theEvent.getPropertyName())) {
            if (theEvent.getNewValue() instanceof Color) {
                myBlockColor = (Color) theEvent.getNewValue();
                myIsSpecialColor = false;
            } else {
                myIsSpecialColor = true;
            }
        }  else if ("circle".equals(theEvent.getPropertyName())) {
            myIsCircleSelected = (boolean) theEvent.getNewValue();
        }
        repaint();
    }

    /**
     * Update the next piece of the game when being notified by the observable.
     * 
     * @param theObservable the class/object that this panel is observing.
     * @param theObject the new data being sent from the observable. 
     */
    @Override
    public void update(final Observable theObservable, final Object theObject) {
        if (theObject instanceof MovableTetrisPiece) {
            myNextPiece = theObject.toString().split("\\r?\\n");
        }
        repaint();

    }
}
