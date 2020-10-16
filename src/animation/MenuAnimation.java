package animation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import animation.AnimationRunner;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import menu.Task;

import javax.imageio.ImageIO;

/**
 * The MenuAnimation class represents a menu animation. The menu is a screen stating a list of several options that the
 * player can choose to do.
 * @param <T> the specific type of MenuAnimation .
 */
public class MenuAnimation<T> implements menu.Menu<T> {
    // Declare the members of the class.
    private static final int UPPER_TITLE_SIZE = 60;
    private static final int LOWER_TITLE_SIZE = 36;
    private AnimationRunner animationRunner;
    private KeyboardSensor keyboard;
    private T status;
    private boolean stop;
    private List<String> keys;
    private List<String> messages;
    private List<Task<T>> returnValues;
    
    /**
     * Constructor.
     * @param animationRunner a runner that runs animations.
     * @param keyboard detects the player's presses.
     */
    public MenuAnimation(AnimationRunner animationRunner, KeyboardSensor keyboard) {
        this.animationRunner = animationRunner;
        this.keyboard = keyboard;
        this.status = null;
        this.stop = false;
        this.keys = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.returnValues =  new ArrayList<>();
    }

    @Override
    public void doOneFrame(DrawSurface d, double dt) {
        // Draw the primary screen of the menu.
        Image backgroundImage = null;
        try {
            backgroundImage = ImageIO.read(
                    Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                            "background_images/space.jpg")));
        } catch (IOException ignored) {
        }
        d.drawImage(0, 0, backgroundImage);

        Image spaceInvadersImage = null;
        try {
            spaceInvadersImage = ImageIO.read(
                    Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                            "general_images/SpaceInvaders.png")));
        } catch (IOException ignored) {
        }
        d.drawImage(40, 80, spaceInvadersImage);

        d.setColor(new Color(255, 30, 0));
        for (int i = 0; i < this.keys.size(); i++) {
            d.drawText(150, 280 + (i * 100), "▶ Press " + "\"" +this.keys.get(i) + "\" " +
                    this.messages.get(i), LOWER_TITLE_SIZE);
        }

        // Check whether the player pressed on a key.
        for (int i = 0; i < this.keys.size(); i++) {
            if (this.keyboard.isPressed(this.keys.get(i))) {
                this.status = this.returnValues.get(i).run();
                this.stop = true;
                break;
            }
        }
    }

    @Override
    public boolean shouldStop() {
        boolean tempStop = this.stop;
        this.stop = false;
        return tempStop;
    }

    /**
     * Initialize the stop member with false to enable to run the animation again.
     */
    public void reset() {
        this.status = null;
        this.stop = false;
    }

    @Override
    public void addSelection(String stopKey, String stopMessage, T returnValue) {
        this.keys.add(stopKey);
        this.messages.add(stopMessage);
        this.returnValues.add(() -> returnValue);
    }

    @Override
    public T getStatus() {
        return status;
    }

} // class MenuAnimation<T>