package animation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import biuoop.DialogManager;
import biuoop.KeyboardSensor;
import highscores.HighScoresTable;
import levels.GameFlow;
import levels.LevelInformation;
import menu.Task;

/**
 * The StartGameTask class represents a task of starting the game.
 */
public class StartGameTask implements Task<Void> {
    // Declare the members of the class.
    private KeyboardSensor keyboard;
    private AnimationRunner animationRunner;
    private DialogManager dialogManager;
    private HighScoresTable highScoresTable;
    private List<LevelInformation> levels;
    private int numberOfLives;

    /**
     * Constructor.
     * @param keyboard a keyboard to detect the player's presses.
     * @param dialogManager to read the player's name
     * @param animationRunner to run animations.
     * @param highScoresTable the table of the high scores.
     * @param levels the levelInformation list.
     * @param lives the current number of lives in the game.
     */
    public StartGameTask(KeyboardSensor keyboard, DialogManager dialogManager,
        AnimationRunner animationRunner, HighScoresTable highScoresTable, List<LevelInformation> levels, int lives) {
        this.keyboard = keyboard;
        this.dialogManager = dialogManager;
        this.animationRunner = animationRunner;
        this.highScoresTable = highScoresTable;
        this.levels = levels;
        this.numberOfLives = lives;
    }

    /**
     * Run the task of starting the game.
     */
    public Void run() {
        GameFlow game = new GameFlow(this.animationRunner, this.keyboard, this.dialogManager,
            this.highScoresTable, this.numberOfLives);
        game.runLevels(this.levels);
        File highScoresFile = new File("highscores.txt");
        try {
            highScoresTable.save(highScoresFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}