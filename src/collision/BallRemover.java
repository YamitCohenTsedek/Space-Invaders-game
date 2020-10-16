package collision;

import invadersgame.Counter;
import invadersgame.Paddle;
import levels.GameLevel;
import sprites.Ball;
import sprites.Block;

/**
 * The BallRemover class is in charge of removing balls and updating a counter of the available balls.
 */
public class BallRemover implements HitListener {
    // Declare the members of the class.
    private GameLevel gameLevel;
    private Counter ballsToRemove;

    /**
     *  Constructor.
     *  @param gameLevel the level that the ball should be removed from.
     *  @param ballsToRemove the number of balls that should be removed.
     */
    public BallRemover(GameLevel gameLevel, Counter ballsToRemove) {
        this.gameLevel = gameLevel;
        this.ballsToRemove = ballsToRemove;
    }

    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        hitter.removeFromGame(this.gameLevel);
        this.ballsToRemove.decrease(1);
    }

    @Override
    public void hitEvent(Paddle paddle, Ball hitter) {
        hitter.removeFromGame(this.gameLevel);
        this.ballsToRemove.decrease(1);
    }

} // class BallRemover