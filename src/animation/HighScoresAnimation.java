package animation;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

import biuoop.DrawSurface;
import highscores.HighScoresTable;
import highscores.ScoreInfo;

import javax.imageio.ImageIO;

/**
 * The HighScoresAnimation class represents the animation of the current highest scores of the players.
 */
public class HighScoresAnimation implements Animation {
    // Declare the members of the class.
    private HighScoresTable highScoresTable;

    /**
     * Constructor.
     * @param scores the table of the high scores.
     */
    public HighScoresAnimation(HighScoresTable scores) {
        this.highScoresTable = scores;
    }

    @Override
    public void doOneFrame(DrawSurface d, double dt) {
        // Draw the high scores table.
        Image backgroundImage = null;
        try {
            backgroundImage = ImageIO.read(
                    Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                            "background_images/space.jpg")));
        } catch (IOException ignored) {
        }
        d.drawImage(0, 0, backgroundImage);

        Image highScoresImage = null;
        try {
            highScoresImage = ImageIO.read(
                    Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                            "general_images/HighScores.png")));
        } catch (IOException ignored) {
        }
        d.drawImage(100, 20, highScoresImage);

        d.setColor(Color.darkGray);
        d.fillRectangle(130, 175, 550, 330);
        d.setColor(new Color(255, 30, 0));
        ScoreInfo currentScoreInfo;
        int width = 170;
        int height = 220;
        d.drawText(width, height, "Player Name", 32);
        d.drawText(width + 390, height, "Score", 32);
        width += 10;
        height += 10;
        for (int i = 0; i < this.highScoresTable.size(); i++) {
            height = height + 50;
            currentScoreInfo = this.highScoresTable.getHighScores().get(i);
            if (currentScoreInfo.getScore() < 0) {
                continue;
            }
            d.drawText(width, height, currentScoreInfo.getName(), 26);
            d.drawText(width + 390, height, Integer.toString(currentScoreInfo.getScore()), 26);
        }
        d.drawText(150, 560, "Press space to back to the main menu", 30);
    }

    @Override
    public boolean shouldStop() {
        return true;
    }

} // class HighScoresAnimation