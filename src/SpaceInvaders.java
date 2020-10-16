
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import animation.AnimationRunner;
import animation.HighScoresAnimation;
import animation.KeyPressStoppableAnimation;
import animation.StartGameTask;
import biuoop.DialogManager;
import biuoop.GUI;
import biuoop.KeyboardSensor;
import highscores.HighScoresTable;
import levels.LevelInformation;
import levels.BattleLevel;
import animation.MenuAnimation;
import menu.ShowHighScoresTask;
import menu.Task;

/**
 * The SpaceInvaders class contains the main function that runs the Space Invaders game.
 */
public class SpaceInvaders {
    // Set the sizes of some static final variables for our Space Invaders game.
    public static final int SURFACE_WIDTH = 800;
    public static final int SURFACE_HEIGHT = 600;
    public static final int FRAMES_PER_SECOND = 60;

    /**
     * The main function creates a game object, initializes and runs it.
     */
    public static void main(String[] args) {
        // Initialize some elementary objects that will be used for building the game.
        GUI gui = new GUI("Space Invaders", SURFACE_WIDTH, SURFACE_HEIGHT);
        DialogManager dialogManager = gui.getDialogManager();
        AnimationRunner animationRunner = new AnimationRunner(gui, FRAMES_PER_SECOND);
        KeyboardSensor keyboard = gui.getKeyboardSensor();
        List<LevelInformation> levels = new ArrayList<>();
        levels.add(new BattleLevel());
        HighScoresTable highScoresTable = new HighScoresTable(5);
        File highScores = new File("highscores.txt");
        try {
            highScoresTable.load(highScores);
        } catch (IOException e) {
            try {
                highScoresTable.save(highScores);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        MenuAnimation<Task<Void>> menu = new MenuAnimation<>(animationRunner, keyboard);
        menu.addSelection("s", "to start a new game", new StartGameTask(keyboard, dialogManager, animationRunner,
            highScoresTable, levels, 3));
        menu.addSelection("h", "for high scores table",  new ShowHighScoresTask(animationRunner, new KeyPressStoppableAnimation(keyboard,
                KeyboardSensor.SPACE_KEY, new HighScoresAnimation(highScoresTable))));
        menu.addSelection("q", "to quit",
                () -> {
                    System.exit(0);
                    return null;
                });
        while (true) {
            animationRunner.run(menu);
            Task<Void> task = menu.getStatus();
            task.run();
            menu.reset();
        }
    }

} // SpaceInvaders class