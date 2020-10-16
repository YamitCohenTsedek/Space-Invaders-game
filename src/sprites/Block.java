package sprites;

import java.util.ArrayList;
import java.util.List;

import invadersgame.Velocity;
import biuoop.DrawSurface;
import collision.Collidable;
import collision.HitListener;
import collision.HitNotifier;
import geometry.Point;
import geometry.Rectangle;
import levels.GameLevel;

/**
 * The Block class represents a block that the ball can collide with.
 */
public class Block implements Collidable, Sprite, HitNotifier {
    // Declare the members of the class.
    private Rectangle rectangle;
    private BlockBackground blockBackground = null;
    private Stroke stroke = null;
    // The number of hits that the ball should hit the block. Each hit decreases this number by 1.
    private int numOfHits;
    // A map that maps between the current number of hits of the block and the fit background.
    private List<HitListener> hitListeners = new ArrayList<>();
    private boolean isEnemy = false;

    /**
     * Constructor (first type).
     * @param location the location of the start of the block.
     * @param width the width of the block.
     * @param height the height of the block
     * @param numOfHits the number of hits that the ball should hit the block, each hit decreases this number by 1.
     * @param background the background of the block.
     */
    public Block(Point location, double width, double height, int numOfHits, BlockBackground background) {
        this.rectangle = new Rectangle(location, width, height);
        this.numOfHits = numOfHits;
        this.blockBackground = background;
        this.stroke = null;
        this.blockBackground = background;
        this.numOfHits = numOfHits;
        this.hitListeners = new ArrayList<>();
    }

    /**
     * Constructor (second type).
     * @param location the location of the start of the block.
     */
    public Block(Point location) {
        int initializedValue = 0;
        this.rectangle = new Rectangle(location, initializedValue, initializedValue);
        this.numOfHits = 1;
    }

    /**
     * Set the current block as an enemy.
     */
    public void setAsEnemy() {
        this.isEnemy = true;
    }

    /**
     * @return true if the block is an enemy, false otherwise.
     */
    public boolean isEnemy() {
        return this.isEnemy;
    }

    /**
     * @param blockWidth the width to set to the block.
     */
    public void setWidth(int blockWidth) {
        this.rectangle.setWidth(blockWidth);
    }

    /**
     * @param blockHeight the height to set to the block.
     */
    public void setHeight(int blockHeight) {
        this.rectangle.setHeight(blockHeight);
    }

    /**
     * @param rec the rectangle shape to set to the block.
     */
    public void setRectangle(Rectangle rec) {
        this.rectangle = rec;
    }

    /**
     * @param blockStroke the stroke to set to the block.
     */
    public void setStroke(Stroke blockStroke) {
        this.stroke = blockStroke;
    }

    /**
     * @param hits the number of hits that the ball should hit the block.
     */
    public void setNumOfHits(int hits) {
        this.numOfHits = hits;
    }

    /**
     * @return the "collision shape" of the object.
     */
     public Rectangle getCollisionRectangle() {
        return this.rectangle;
    }

     /**
      * @return the number of the hits of the block.
      */
     public int getHitPoints() {
         return this.numOfHits;
     }

    /**
     * Decrease the number of the hits of the block by 1.
     */
     public void decreaseNumOfHits() {
         this.numOfHits = this.numOfHits - 1;
     }

    @Override
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity, double dt) {
        boolean hitX = false;
        boolean hitY = false;
        if (this.rectangle.getUpperEdge().isInXRange(collisionPoint.getX())) {
            hitX = true;
        }
        if (this.rectangle.getLeftEdge().isInYRange(collisionPoint.getY())) {
            hitY = true;
        }
        if (this.numOfHits > 0) {
            if ((hitY || hitX)) {
                this.numOfHits--;
            }
        }
        // The hitter is the Ball that's doing the hitting. Save it in notifyHit.
        this.notifyHit(hitter);
        return new Velocity(0, 0);
    }

    /**
     * Whenever a hit() occurs, notify all the registered HitListener objects by calling their hitEvent.
     * @param hitter the Ball that's doing the hitting.
     */
    private void notifyHit(Ball hitter) {
        /*
         * Calling the removeHitListener or the addHitListener methods from inside the notifyHit method,
         * may cause an exception. For this reason, perform the iteration on a copy of hitListeners list instead.
         */
        List<HitListener> listeners = new ArrayList<>(this.hitListeners);
        // Notify all listeners about a hit event.
        for (HitListener hl : listeners) {
           hl.hitEvent(this, hitter);
        }
     }

    /**
     * Draw the block on the screen.
     * @param d a surface you can draw on.
     */
    public void drawOn(DrawSurface d) {
        this.blockBackground.draw(d, this.rectangle);
        // If the block has an outline, draw it.
        if (this.stroke != null) {
            this.stroke.draw(d, this.rectangle);
        }
    } // drawOn

    /**
     * Notify the block that time has passed.
     * @param dt specifies the number of seconds passed since the last call.
     */
    public void timePassed(double dt) {
    }

    /**
     * Add the block to the game, calling the appropriate game methods.
     * @param game the game that the block should be added to.
     */
    public void addToGame(GameLevel game) {
        game.addSprite(this);
        game.addCollidable(this);
    }

    /**
     * Remove the block from the Sprite collection.
     * @param game the game that the block should be removed from.
     */
    public void removeFromGame(GameLevel game) {
        game.removeSprite(this);
        game.removeCollidable(this);
    }

    @Override
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    @Override
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * @return the list of the his listeners of the block.
     */
    public List<HitListener> getHitListenersList() {
        return this.hitListeners;
    }

    /**
     * @param dt specifies the amount of seconds passed since the last call.
     * @param velocity the velocity of the movement.
     */
    public void moveBlock(Velocity velocity, double dt) {
        double dxByDt = velocity.getDx() * dt;
        double dyByDt = velocity.getDy() * dt;
        // Find the new position of the blocks.
        int newX = (int) (this.rectangle.getUpperLeft().getX() + dxByDt);
        int newY = (int) (this.rectangle.getUpperLeft().getY() + dyByDt);
        double width = this.rectangle.getWidth();
        double height = this.rectangle.getHeight();
        this.setRectangle(new Rectangle(new Point(newX, newY), width, height));
    }

} // class Block