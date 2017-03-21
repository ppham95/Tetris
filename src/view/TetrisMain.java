/*
 * TCSS 305 Autumn 2016
 * Assignment 6 - Tetris
 */

package view;

import java.awt.EventQueue;

/**
 * Runs Tetris game, instantiating and starting the TetrisGUI.
 * 
 * @author Phu-Lam Pham
 * @version 9 December 2016
 */
public final class TetrisMain {

    /**
     * Private constructor, to prevent instantiation of this class.
     */
    private TetrisMain() {
        throw new IllegalStateException();
    }

    /**
     * The main method, invokes the TetrisGUI. Command line arguments are
     * ignored.
     * 
     * @param theArgs Command line arguments.
     */
    public static void main(final String[] theArgs) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TetrisGUI().start();
            }
        });
    }
}
