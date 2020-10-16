package invadersgame;

import collision.HitListener;
import sprites.Ball;
import sprites.Block;

/**
 * The ScoreTrackingListener class updates the counter of the blocks of the game
 * when blocks are being hit and removed.
 */
public class ScoreTrackingListener implements HitListener {
    // Declare the members of the class.
    private Counter currentScore;

    /**
     * Constructor.
     * @param scoreCounter the counter of the score.
     */
    public ScoreTrackingListener(Counter scoreCounter) {
        this.currentScore = scoreCounter;
    }

    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        // Hitting an enemy block is worth 5 points.
        if (beingHit.isEnemy() && hitter.getVelocity().getDy() < 0) {
            this.currentScore.increase(5);
        }
    }

    @Override
    public void hitEvent(Paddle beingHit, Ball hitter) {
    }

 } // class ScoreTrackingListener