package levels;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import animation.*;
import invadersgame.Counter;
import invadersgame.EnemiesArmy;
import invadersgame.GameEnvironment;
import invadersgame.LivesIndicator;
import invadersgame.Paddle;
import invadersgame.ScoreIndicator;
import invadersgame.ScoreTrackingListener;
import biuoop.DrawSurface;
import biuoop.KeyboardSensor;
import collision.BallRemover;
import collision.BlockRemover;
import collision.Collidable;
import collision.HitListener;
import geometry.Line;
import geometry.Point;
import sprites.Ball;
import sprites.Block;
import sprites.BlockColorBackground;
import sprites.Colors;
import sprites.Sprite;
import sprites.SpriteCollection;

/**
 * The GameLevel class is responsible to hold the sprites & collidables, and in charge of the animation.
 */
public class GameLevel implements Animation {
    // Declare the members of the class.
    private SpriteCollection sprites;
    private GameEnvironment environment;
    private AnimationRunner runner;
    private boolean running;
    private LevelInformation levelInformation;
    private Counter blocksCounter;
    private Counter ballsCounter;
    private Counter gameScore;
    private Counter numberOfLives;
    private Paddle paddle;
    private KeyboardSensor keyboard;
    private List<Block> blocks;
    private List<Ball> shots;
    private EnemiesArmy enemiesArmy;
    private Counter paddleHits;

    // Set some sizes as constants.
    public static final int SURFACE_WIDTH = 800;
    public static final int SURFACE_HEIGHT = 600;
    public static final int SIDES_BORDERS_WIDTH = 25;
    public static final int UP_DOWN_BORDERS_HEIGHT = 30;
    public static final int PADDLE_HEIGHT = 20;

    /**
     * Constructor.
     * @param levelInformation the information required to fully describe a level.
     * @param keyboard a keyboard sensor.
     * @param runner takes an animation object and runs it.
     * @param gameScore the current score of the game.
     * @param lives the current number of the lives in the game.
     * @param enemiesSpeed the speed of the enemies.
     */
    public GameLevel(LevelInformation levelInformation, KeyboardSensor keyboard,
        AnimationRunner runner, Counter gameScore, Counter lives, int enemiesSpeed) {
        this.environment = new GameEnvironment();
        this.sprites = new SpriteCollection();
        this.runner = runner;
        // Get a draw surface to draw on.
        this.levelInformation = levelInformation;
        this.blocksCounter = new Counter(levelInformation.numberOfBlocksToRemove());
        this.ballsCounter = new Counter(0);
        this.gameScore = gameScore;
        this.numberOfLives = lives;
        this.keyboard = keyboard;
        this.blocks = new ArrayList<>();
        // Set the info block at the top of the screen (and draw on it the name of the level).
        setBordersBlocks();
        // Set the blocks of the enemies.
        this.enemiesArmy = new EnemiesArmy(this, enemiesSpeed);
        for (int i = 0; i < levelInformation.blocks().size(); i++) {
            Block block = levelInformation.blocks().get(i);
            block.setAsEnemy();
            this.blocks.add(block);
            this.enemiesArmy.addBlock(block);
        }
        this.gameScore = gameScore;
        this.running = true;
        this.shots = new ArrayList<>();
        this.sprites.addSprite(enemiesArmy);
        // Set the lives indicator and the score indicator.
        LivesIndicator livesIndicator = new LivesIndicator(this.numberOfLives);
        ScoreIndicator scoreIndicator = new ScoreIndicator(this.gameScore);
        this.sprites.addSprite(livesIndicator);
        this.sprites.addSprite(scoreIndicator);
        this.shots = new ArrayList<>();
        this.paddleHits = new Counter(1);
    }

    /**
     * @return the game environment.
     */
    public GameEnvironment getGameEnvironment() {
        return this.environment;
    }

    /**
     * Add the given collidable to the game environment.
     * @param c the collidable object that should be added to the game environment.
     */
    public void addCollidable(Collidable c) {
        this.environment.addCollidable(c);
    }

    /**
     * Add the given sprite to the game environment.
     * @param s the sprite that should be added to the Sprite collection of the game.
     */
    public void addSprite(Sprite s) {
        this.sprites.addSprite(s);
    }

    /**
     * @param c the collidable object that should be removed from the game environment.
     */
    public void removeCollidable(Collidable c) {
        this.getGameEnvironment().removeCollidable(c);
    }

    /**
     * @param s the sprite object that should be removed from Sprite collection of the game.
     */
    public void removeSprite(Sprite s) {
        this.sprites.getSprites().remove(s);
    }

    /**
     * Create a block and add it to the game.
     * @param block the block that should be added to the game
     */
    public void addBlock(Block block) {
        // BlockRemover is in charge of removing blocks from the game, as well as counting the remaining blocks.
        BlockRemover blockRemover = new BlockRemover(this, this.blocksCounter);
        // stk is in charge of updating the score counter when blocks are being hit and removed.
        ScoreTrackingListener stk = new ScoreTrackingListener(this.gameScore);
        // Create a new block and add it to the game.
        block.addHitListener(blockRemover);
        block.addHitListener(stk);
        block.addToGame(this);
    }

    /**
     * Create a border block and add it to the game.
     * @param startPoint the start point of the block.
     * @param blockWidth the width of the block.
     * @param blockHeight the height of the block.
     * @param blockHits the number of hits of the block.
     * @param background the background of the block.
     * @return the block added to the game.
     */
    private Block addBorderBlock(Point startPoint, int blockWidth,
        int blockHeight, int blockHits, BlockColorBackground background) {
        Block borderBlock = new Block(startPoint, blockWidth, blockHeight, blockHits, background);
        borderBlock.addToGame(this);
        return borderBlock;
    }

    /**
     * Set the blocks at the borders of the surface of the game.
     */
    private void setBordersBlocks() {
        // The number of hits that the ball should hit the borders blocks is 0.
        int blockHits = 0;
        BlockColorBackground bordersBackground = new BlockColorBackground(new Color(255, 255, 204));
        // Add a ball remover to the borders.
        BallRemover ballRemover = new BallRemover(this, this.ballsCounter);
        Block upBlock = addBorderBlock(new Point(0, 0), SURFACE_WIDTH, UP_DOWN_BORDERS_HEIGHT,
                blockHits, bordersBackground);
        upBlock.addHitListener(ballRemover);
        upBlock.addToGame(this);
        // Set the down block border (the killer block) and add it to the game environment.
        Block killerBlock = addBorderBlock(new Point(0, SURFACE_HEIGHT), SURFACE_WIDTH, UP_DOWN_BORDERS_HEIGHT,
            blockHits, bordersBackground);
        killerBlock.addHitListener(ballRemover);
        killerBlock.addToGame(this);
    } // setBordersBlocks

    /**
     * Set the paddle of the current turn.
     */
    private void setPaddle() {
        // Create the paddle.
        double paddleWidth = this.levelInformation.paddleWidth();
        Point paddleStartPoint = new Point((float) (SURFACE_WIDTH / 2 - paddleWidth / 2),
            (float) (SURFACE_HEIGHT - PADDLE_HEIGHT));
        Colors colors = new Colors();
        this.paddle = new Paddle(paddleStartPoint, paddleWidth, PADDLE_HEIGHT,
            colors.setRainbowColor(-1), this.levelInformation.paddleSpeed(), this.keyboard);
        paddle.setKeyboardSensor(this.keyboard);
        // The line of the range that the paddle can move in it.
        Line paddleRange = new Line(new Point(SIDES_BORDERS_WIDTH, 0),
            new Point(SURFACE_WIDTH - SIDES_BORDERS_WIDTH, 0));
        paddle.setMovementRange(paddleRange);
        // Set the speed of the paddle.
        paddle.setSpeed(this.levelInformation.paddleSpeed());
        this.paddleHits = new Counter(1);
        HitListener removingBallsListener = new BallRemover(this, this.paddleHits);
        this.paddle.addHitListener(removingBallsListener);
        // Add the paddle to the game.
        paddle.addToGame(this);
    }

    /**
     * Build a Shield on the screen.
     * @param startX the start x position of the shield.
     * @param startY the start y position of the shield.
     * @param numOfRows the number of rows of the shield.
     * @param numOfColumns the number of columns of the shield.
     */
    public void buildShield(double startX, double startY, int numOfRows, int numOfColumns) {
        BlockRemover blockRemover = new BlockRemover(this, new Counter(0));
        BallRemover ballRemover = new BallRemover(this, this.ballsCounter);
        Block[][] shield = new Block[numOfRows][numOfColumns];
        double width = 5;
        double height = 5;
        double currentX;
        double currentY = startY;
        BlockColorBackground blockBackground = new BlockColorBackground(new Color(255, 30, 0));
        for (int i = 0; i < numOfRows; i++) {
            currentX = startX;
             for (int j = 0; j < numOfColumns; j++) {
                 shield[i][j] = new Block(new Point(currentX, currentY), width, height, 1, blockBackground);
                 currentX = currentX + width;
                 shield[i][j].addHitListener(blockRemover);
                 shield[i][j].addHitListener(ballRemover);
                 shield[i][j].addHitListener(new ScoreTrackingListener(this.gameScore));
                 shield[i][j].addToGame(this);
             }
             currentY = currentY + height;
        }
     }

    /**
     * Build the shields and add them to the game.
     */
    public void setShields() {
        int numOfRows = 3;
        int numOfColumns = 30;
        // Build the left shield.
        buildShield(83, 500, numOfRows, numOfColumns);
        // Build the middle shield.
        buildShield(320, 500, numOfRows, numOfColumns);
        // Build the right shield.
        buildShield(560, 500, numOfRows, numOfColumns);
    }

    /**
     * Initialize a new game: set the game blocks and add them to the game, and also initialize the balls counter.
     */
    public void initialize() {
        setShields();
        HitListener br = new BlockRemover(this, this.blocksCounter);
        HitListener blr = new BallRemover(this, this.ballsCounter);
        HitListener stl = new ScoreTrackingListener(this.gameScore);
        for (Block i: this.blocks) {
            i.addToGame(this);
            i.addHitListener(br);
            i.addHitListener(blr);
            i.addHitListener(stl);
            i.addHitListener(this.enemiesArmy);
        }
        this.sprites.getSprites().add(0, levelInformation.getBackground());
        this.ballsCounter = new Counter(this.levelInformation.numberOfBalls());
      } // initialize

    @Override
    public boolean shouldStop() {
        return !this.running;
    }

    @Override
    public void doOneFrame(DrawSurface d, double dt) {
        this.sprites.drawAllOn(d);
        // Notify all the sprites that time has passed (change the state of the sprites in each frame).
        this.sprites.notifyAllTimePassed(dt);
        // Shoot with the paddle.
        if ((this.keyboard.isPressed(KeyboardSensor.SPACE_KEY))) {
            this.paddle.paddleShot(this);
          }
        // Pause screen animation.
        if (this.keyboard.isPressed("p")) {
            this.runner.run(new KeyPressStoppableAnimation(
                this.keyboard, "c", new PauseScreenAnimation()));
        }
        // Check whether the player won.
        if (this.blocksCounter.getValue() == 0) {
            this.running = false;
        }
        if (this.paddleHits.getValue() == 0) {
            this.running = false;
            this.enemiesArmy.initialize(dt);
            this.removeShots();
        }
        if (this.enemiesArmy.arrivedToShields()) {
            this.running = false;
            this.enemiesArmy.initialize(dt);
            this.removeShots();
        }
    }

    /**
     *  Run the game - start the animation loop that goes over all the sprites and calls drawOn and timePassed methods
     *  on each Sprite.
     */
    public void playOneTurn() {
        setPaddle();
        this.ballsCounter = new Counter(this.levelInformation.numberOfBalls());
        CountdownAnimation countdownAnimation = new CountdownAnimation(2.0, 3, this.sprites);
        this.runner.run(countdownAnimation);
        // Use the runner to run the current animation, which is one turn of the game.
        this.running = true;
        this.runner.run(this);
        // Remove the paddle since the turn is over.
        this.removeSprite(paddle);
        this.removeCollidable(paddle);
    } // playOneTurn

    /**
     * Remove the shots from the screen.
     */
    public void removeShots() {
        for (int i = 0; i < this.shots.size(); i++) {
            this.shots.get(i).removeFromGame(this);
        }
    }

    /**
     *  Create a shot of an enemy.
     *  @param point the start point of the shot.
     *  @return a new ball.
     */
    public Ball shotOfEnemy(Point point) {
        Ball ball = new Ball(point, 5, Color.RED, this.environment);
        this.shots.add(ball);
        return ball;
    }

    /**
     *  Perform a shot of the paddle.
     *  @param point the start point of the shot.
     *  @return a new ball.
     */
    public Ball shotOfPaddle(Point point) {
        Ball ball = new Ball(point, 4, Color.WHITE, this.environment);
        this.shots.add(ball);
        return ball;
    }

    /**
     * @return the current number of the blocks.
     */
    public int getBlocksNumber() {
        return this.blocksCounter.getValue();
    }

} // class GameLevel