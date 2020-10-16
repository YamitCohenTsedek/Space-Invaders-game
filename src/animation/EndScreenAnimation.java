package animation;

import java.awt.*;

import biuoop.DrawSurface;
import invadersgame.Counter;

import static levels.GameLevel.SURFACE_HEIGHT;
import static levels.GameLevel.SURFACE_WIDTH;

/**
 * The EndScreenAnimation class represents the animation of the end of the game.
 */
public class EndScreenAnimation implements Animation {
    // Declare the members of the class.
    private boolean stop;
    private Counter score;

    /**
     * Constructor.
     * @param score the score of the game.
     */
    public EndScreenAnimation(Counter score) {
        this.stop = false;
        this.score = score;
    }

    @Override
    public void doOneFrame(DrawSurface d, double dt) {
        d.setColor(Color.black);
        d.fillRectangle(0, 0, SURFACE_WIDTH, SURFACE_HEIGHT);
        d.setColor(Color.white);
        d.drawText(170, 200, "Game Over", 90);
        d.drawText(240, 330, "Your score is: " + score.getValue(), 40);
        // If "c" key is pressed, the animation ends.
        d.drawText(260, 550, "Press \"c\" to continue", 30);
    }

    @Override
    public boolean shouldStop() {
        return this.stop;
    }

} // class EndScreenAnimation