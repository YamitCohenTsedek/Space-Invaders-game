package collision;

import invadersgame.Counter;
import invadersgame.Paddle;
import levels.GameLevel;
import sprites.Ball;
import sprites.Block;

/**
 * The BlockRemover class is in charge of removing blocks from the game,
 * as well as keeping count of the number of the remaining blocks.
 */
public class BlockRemover implements HitListener {
    // Declare the members of the class.
    private GameLevel game;
    private Counter remainingBlocks;

    /**
     * Constructor.
     * @param game the game that the block should be removed from.
     * @param removedBlocks the counter of the removed blocks.
     */
    public BlockRemover(GameLevel game, Counter removedBlocks) {
        this.game = game;
        this.remainingBlocks = removedBlocks;
    }

    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        if (hitter.getVelocity().getDy() < 0 || (!beingHit.isEnemy()) ) {
            if (beingHit.getHitPoints() == 0) {
                // When a block is removed from the game, the hit listener of it also should be removed.
                beingHit.removeHitListener(this);
                beingHit.removeFromGame(this.game);
                this.remainingBlocks.decrease(1);
            }
        }
    }
    
    // Do nothing when hit event occurs with the paddle.
    @Override
    public void hitEvent(Paddle paddle, Ball hitter) {
    }

} // class BlockRemover