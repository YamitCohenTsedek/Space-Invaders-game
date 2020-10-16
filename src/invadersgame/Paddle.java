package invadersgame;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import collision.Collidable;
import collision.HitListener;
import collision.HitNotifier;
import geometry.Line;
import geometry.Point;
import geometry.Rectangle;
import levels.GameLevel;
import sprites.Ball;
import sprites.Colors;
import sprites.Sprite;

/**
 * The Paddle class represents the paddle that is used by the player. It's a rectangle that is controlled by the arrow
 * keys, and moves according to the player key presses. Class Paddle implements the Sprite and Collidable interfaces.
 */
public class Paddle implements Sprite, Collidable, HitNotifier {
    // Declare the members of the class.
    private Line movementRange; // The range that the paddle can move in.
    private KeyboardSensor keyboard;
    private Rectangle rectangle;
    private Color color;
    // The change that the paddle makes in a case of a key pressing to the left or right.
    private int paddleSpeed;
    private List<HitListener> hitListeners;
    private long lastTimeShot;

    /**
     * Constructor.
     * @param location the location where the paddle starts.
     * @param width the width of the paddle.
     * @param height the height of the paddle.
     * @param color the color of the paddle.
     * @param paddleSpeed the speed of the paddle.
     */
    public Paddle(Point location, double width, double height, Color color, int paddleSpeed, KeyboardSensor keyboard) {
        this.rectangle = new Rectangle(location, width, height);
        this.color = color;
        this.paddleSpeed = paddleSpeed;
        this.keyboard = keyboard;
        this.hitListeners = new ArrayList<>();
        this.lastTimeShot = 0;
    }

    /**
     * @param speed the speed that we set to the paddle.
     */
    public void setSpeed(int speed) {
        this.paddleSpeed = speed;
    }

    /**
     * @param paddleColor the color that we set to the paddle.
     */
    public void setColor(Color paddleColor) {
        this.color = paddleColor;
    }

    /**
     * @param keyboardS a KeyboardSensor that is used to detect the key presses.
     */
    public void setKeyboardSensor(KeyboardSensor keyboardS) {
        this.keyboard = keyboardS;
    }

    /**
     * @param range the range that the paddle can move in.
     */
    public void setMovementRange(Line range) {
        this.movementRange = range;
    }

    /**
     * @param d the movement that the paddle should make in the x-axis.
     */
    private void changePaddlePosition(double d) {
        Point currentPosition = this.rectangle.getUpperLeft();
        // Update the x value of the start point of the rectangle.
        this.rectangle.setUpperLeft(new Point(currentPosition.getX() + d, currentPosition.getY()));
    }

    /**
     * @return true if the paddle is going to exceed from the right side, false otherwise.
     * @param dt specifies the number of seconds passed since the last call.
     */
    private boolean rightExceeding(double dt) {
        return this.getCollisionRectangle().getUpperEdge().end().getX() + this.paddleSpeed * dt
            > this.movementRange.end().getX();
    }

    /**
     * @return true if the paddle is going to exceed from the left side, false otherwise.
     * @param dt specifies the number of seconds passed since the last call.
     */
    private boolean leftExceeding(double dt) {
        return this.getCollisionRectangle().getUpperEdge().start().getX() - paddleSpeed * dt
            < this.movementRange.start().getX();
    }

    /**
     * Perform the movement of the paddle to the left side.
     * @param dt specifies the number of seconds passed since the last call.
     */
    public void moveLeft(double dt) {
        this.changePaddlePosition((-paddleSpeed * dt));
        Colors colors = new Colors();
        this.setColor(colors.setRainbowColor(-1));
    }

    /**
     * Perform the movement of the paddle to the right side.
     * @param dt specifies the number of seconds passed since the last call.
     */
    public void moveRight(double dt) {
        this.changePaddlePosition((this.paddleSpeed * dt));
        Colors colors = new Colors();
        this.setColor(colors.setRainbowColor(-1));
    }

    /**
     * Notify the paddle that time has passed.
     * If the left or the right key are pressed, the paddle moves respectively.
     * @param dt specifies the amount of seconds passed since the last call.
     */
    public void timePassed(double dt) {
        if (this.keyboard.isPressed(KeyboardSensor.LEFT_KEY) && !leftExceeding(dt)) {
            moveLeft(dt);
        } else if (this.keyboard.isPressed(KeyboardSensor.RIGHT_KEY) && !rightExceeding(dt)) {
            moveRight(dt);
        }
    }

    /**
     * Draw the paddle on the screen.
     * @param d a surface you can draw on.
     */
    public void drawOn(DrawSurface d) {
        // Find the x and y values of the upper left, the width and the height of the rectangle.
        int upperLeftX = (int) this.rectangle.getUpperLeft().getX();
        int upperLeftY = (int) this.rectangle.getUpperLeft().getY();
        int width = (int) this.rectangle.getWidth();
        int height = (int) this.rectangle.getHeight();
        // Draw the rectangle shape of the block.
        d.setColor(this.color);
        d.fillRectangle(upperLeftX, upperLeftY, width, height);
    }

    /**
     * @return the "collision shape" of the object.
     */
    public Rectangle getCollisionRectangle() {
        return (this.rectangle);
    }

    /**
     * Perform the shot of the paddle.
     * @param gameLevel the gameLevel.
     */
    public void paddleShot(GameLevel gameLevel) {
        long shootingTime = 350;
        if (System.currentTimeMillis() - lastTimeShot > shootingTime) {
            this.lastTimeShot = System.currentTimeMillis();
            double xPosition = this.getCollisionRectangle().getUpperEdge().middle().getX();
            double yPosition = this.getCollisionRectangle().getUpperEdge().middle().getY();
            Ball shotOfPaddle = gameLevel.shotOfPaddle(new Point(xPosition, yPosition - 5));
            shotOfPaddle.setVelocity(0, -350);
            shotOfPaddle.addToGame(gameLevel);
            }
        }

    /**
     * Perform the hit with the paddle.
     * @param hitter the Ball that's doing the hitting, and we save it in notifyHit.
     * @param collisionPoint the collision point with the paddle.
     * @param currentVelocity the current velocity of the ball.
     * @return the new velocity after the hitting, according to the region of the collision.
     * @param dt specifies the amount of seconds passed since the last call.
     */
    public Velocity hit(Ball hitter, Point collisionPoint, Velocity currentVelocity, double dt) {
        notifyHit(hitter);
        return new Velocity(0, 0);
        }

    /**
     * @param hl the hit listener to add to the list of the hit listeners of the paddle.
     */
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    /**
     * @param hl the hit listener to remove from list of the hit listeners of the paddle.
     */
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * Notify all the hit listeners about the hit that occurred.
     * @param hitter the ball that hits the paddle.
     */
    private void notifyHit(Ball hitter) {
        List<HitListener> listeners =
            new ArrayList<>(this.hitListeners);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).hitEvent(this, hitter);
        }
    }

    /**
     * Add the paddle to the game by calling the appropriate game methods.
     * @param g the game that the paddle should be added to.
     */
    public void addToGame(GameLevel g) {
        g.addSprite(this);
        g.addCollidable(this);
    }

} // class paddle