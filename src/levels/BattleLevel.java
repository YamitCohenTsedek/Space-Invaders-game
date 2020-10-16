package levels;

import java.awt.Image;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import javax.imageio.ImageIO;

import geometry.Point;
import invadersgame.Velocity;
import sprites.*;

/**
 * The BattleLevel class implements the LevelInformation interface and supplies the information of the level.
 */
public class BattleLevel implements LevelInformation {

    @Override
    public int numberOfBalls() {
        return 0;
    }

    @Override
    public List<Velocity> initialBallVelocities() {
        List<Velocity> ballsVelocityList = new ArrayList<>();
        ballsVelocityList.add(Velocity.fromAngleAndSpeed(0, 300));
        return ballsVelocityList;
    }

    @Override
    public int paddleSpeed() {
        return 400;
    }

    @Override
    public int paddleWidth() {
        return 100;
    }

    @Override
    public String levelName() {
        return "Battle no.";
    }

    @Override
    public Sprite getBackground() {
        Image backgroundImage = null;
        try {
            backgroundImage = ImageIO.read(
                    Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                            "background_images/space.jpg")));
        } catch (IOException ignored) {
        }
        ImageBackground imgBackground = new ImageBackground(backgroundImage);
        imgBackground.setStartPoint(new Point(0,0));
        return imgBackground;
    }

    @Override
    public int numberOfBlocksToRemove() {
        return 50;
    }

    @Override
    public List<Block> blocks() {
        List<Block> blocks = new ArrayList<>();
        Image image = null;
        try {
            image = ImageIO.read(ClassLoader.getSystemClassLoader().
                    getResourceAsStream("block_images/enemy.png"));
        } catch (IOException ignored) {
        }
        int y = 40;
        for (int i = 0; i < 5; i++) {
            int x = 150;
            for (int j = 0; j < 10; j++) {
                BlockImageBackground background = new BlockImageBackground(image);
                Block b = new Block(new Point(x, y), 40, 30, 1, background);
                blocks.add(b);
                x =  x + 50;
            }
            y = y + 40;
        }
        return blocks;
    }

} // class BattleLevel