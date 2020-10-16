package levels;
import java.util.List;

import animation.*;
import biuoop.DialogManager;
import biuoop.KeyboardSensor;
import highscores.HighScoresTable;
import highscores.ScoreInfo;
import invadersgame.Counter;
import invadersgame.LivesIndicator;
import invadersgame.ScoreIndicator;

/**
 * The GameFlow class is in charge of creating different levels and passing from one level to the next one.
 */
public class GameFlow {
    // Declare the members of the class.
    private DialogManager dialogManager;
    private AnimationRunner animationRunner;
    private KeyboardSensor keyboard;
    private Counter gameScore;
    private Counter lives;
    private HighScoresTable highScoresTable;

    /**
     * Constructor.
     * @param animationRunner an animation runner.
     * @param keyboard a keyboard sensor.
     * @param dialogManager is used to get the player's name.
     * @param highScoresTable the table of the highest scores.
     * @param numberOfLives the current number of lives.
     */
    public GameFlow(AnimationRunner animationRunner, KeyboardSensor keyboard, DialogManager dialogManager,
        HighScoresTable highScoresTable, int numberOfLives) {
            this.animationRunner = animationRunner;
            this.keyboard = keyboard;
            this.dialogManager = dialogManager;
            // Initialize the score and the number of lives.
            this.gameScore = new Counter(0);
            this.lives = new Counter(numberOfLives);
            this.highScoresTable = highScoresTable;
        }

    /**
     * @param levels a list of LevelInformation objects (the information of each level).
     */
    public void runLevels(List<LevelInformation> levels) {
        LevelInformation levelInfo = levels.get(0);
        int levelNumber = 1;
        int enemiesSpeed = 65;
        while (true) {
            GameLevel level = new GameLevel(levelInfo, this.keyboard, this.animationRunner,
                this.gameScore, this.lives, enemiesSpeed);
            // Initialize the game.
            level.initialize();
            LevelNameIndicator levelName = new LevelNameIndicator(levelInfo.levelName() + levelNumber);
            LivesIndicator livesIndicator = new LivesIndicator(this.lives);
            ScoreIndicator scoreIndicator = new ScoreIndicator(this.gameScore);
            level.addSprite(livesIndicator);
            level.addSprite(scoreIndicator);
            level.addSprite(levelName);

            while (this.lives.getValue() > 0) {
                level.playOneTurn();
                if (level.getBlocksNumber() == 0) {
                    levelNumber++;
                    enemiesSpeed = enemiesSpeed + 15;
                    break;
                } else {
                    this.lives.decrease(1);
                }
            }
            if (this.lives.getValue() == 0) {
                break;
            }
        }

        // Show the animation of the end of the game.
        this.animationRunner.run(new KeyPressStoppableAnimation(keyboard, "c",
                new EndScreenAnimation(this.gameScore)));

        if (this.highScoresTable.getRank(this.gameScore.getValue()) <= this.highScoresTable.size()
            && gameScore.getValue() > 0) {
            String playerName = this.dialogManager.showQuestionDialog("Name", "What is your name?", "");
            ScoreInfo scoreInfo = new ScoreInfo(playerName, this.gameScore.getValue());
            this.highScoresTable.add(scoreInfo);
        }
        Animation highScoresAnimation = new HighScoresAnimation(highScoresTable);
        this.animationRunner.run(new KeyPressStoppableAnimation(keyboard, keyboard.SPACE_KEY, highScoresAnimation));
    }

} // class GameFlow