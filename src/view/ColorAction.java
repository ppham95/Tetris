/*
 * TCSS 305 Autumn 2016
 * Assignment 6 - Tetris
 */

package view;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Action representing a color option in Tetris game.
 * 
 * @author Phu-Lam Pham
 * @version 9 December 2016
 */
public class ColorAction extends AbstractAction {
    
    /**  A generated serial version UID for object Serialization. */
    private static final long serialVersionUID = 646554984533184530L;
    
    /** The color of the action .*/
    private final Color myColor;
    
    /**
     * Constructor initializing the color action.
     * 
     * @param theColor the color of the action.
     * @param theGamePanel the game panel associated with Tetris game.
     * @param thePreviewPanel the preview panel of the next Tetris piece.
     */
    public ColorAction(final Color theColor, 
                       final GamePanel theGamePanel, 
                       final NextPiecePreviewPanel thePreviewPanel) {
        super("Color");
        myColor = theColor;
        addPropertyChangeListener(theGamePanel);
        addPropertyChangeListener(thePreviewPanel);
    }
    
    /** 
     * Select the color of this action.
     * 
     * @param theEvent the color selected action event.
     */
    @Override
    public void actionPerformed(final ActionEvent theEvent) {
        putValue(Action.SELECTED_KEY, true);
        firePropertyChange("color", null, myColor);
    }
}
