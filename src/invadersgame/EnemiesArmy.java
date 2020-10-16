package invadersgame;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import biuoop.DrawSurface;
import collision.HitListener;
import geometry.Point;
import levels.GameLevel;
import sprites.Ball;
import sprites.Block;
import sprites.Sprite;

/**
 * The EnemiesArmy class builds the "enemies army" - the blocks of the enemies that move in a formation.
 */
public class EnemiesArmy implements Sprite, HitListener {
    // Declare the members of the class.
    private GameLevel gameLevel;
    private List<Block> blocks;
    private Map<Double, Set<Block>> blockByX;
    private Map<Double, Set<Block>> blockByY;
    private double movementDirection;
    private double originalSpeed;
    private double speed;
    private boolean movedDown;
    private boolean stop;
    private long lastTime;
    private double maxX;
    private double minX;
    private double maxY;
    private double minY;

    /**
     * Constructor.
     * @param game the current level.
     * @param speed the current speed of the enemies.
     */
    public EnemiesArmy(GameLevel game, double speed) {
        this.gameLevel = game;
        this.blocks = new ArrayList<>();
        this.blockByX = new TreeMap<>();
        this.blockByY = new TreeMap<>();
        this.movementDirection = 90;
        this.originalSpeed = speed;
        this.speed = speed;
        this.movedDown = false;
        this.stop = false;
        this.lastTime = 0;
        this.maxX = 0;
        this.minX = 800;
        this.maxY = 0;
    }

    /**
     * Update the min/max values according to the state of the enemies.
     */
    public void updateMovement() {
        this.maxX = 0;
        this.minX = 800;
        this.maxY = 0;
        this.minY = 600;
        for (Double x : this.blockByX.keySet()) {
            if (this.maxX < x) {
                this.maxX = x;
            }
            if (this.minX > x) {
                this.minX = x;
            }
        }
        for (Double y : this.blockByY.keySet()) {
            if (this.maxY < y) {
                this.maxY = y;
            }
            if (this.minY > y) {
                this.minY = y;
            }
        }
        if (this.maxX >= 750) {
            this.movementDirection = 270;
        }
        if (this.minX <= 0) {
            this.movementDirection = 90;
        }
        if ((this.minX <= 0 || this.maxX >= 750) && !this.movedDown) {
            this.speed = 1.1 * this.speed;
            this.movementDirection = 180;
            this.movedDown = true;
        }
        if (this.movementDirection == 90 || this.movementDirection == 270) {
            this.movedDown = false;
        }
        if (maxY >= 450) {
            this.stop = true;
        }
    } // updateMovement

    /**
     * Move the enemies to the required direction according to the dt value.
     * @param dt the dt value.
     */
    public void move(double dt) {
        Velocity velocity = null;
        if (this.movementDirection == 180) {
            velocity = Velocity.fromAngleAndSpeed(this.movementDirection, 1000);
        } else {
            velocity = Velocity.fromAngleAndSpeed(this.movementDirection, this.speed);
        }
        for (Block block : this.blocks) {
            block.moveBlock(velocity, dt);
        }
    }

    /**
     * Shoot a random ball from the paddle.
     * @param game the game level we play on.
     */
    public void shoot(GameLevel game) {
        List<Block> lowest = new ArrayList<>();
        if (System.currentTimeMillis() - this.lastTime > 500) {
            for (Double x : this.blockByX.keySet()) {
                Set<Block> column = blockByX.get(x);
                if (column.size() != 0) {
                    Block lowestBlock = new Block(new Point(0, -1));
                    for (Block block : column) {
                        if (block.getCollisionRectangle().getUpperLeft().getY()
                                > lowestBlock.getCollisionRectangle().getUpperLeft().getY()) {
                            lowestBlock = block;
                        }
                    }
                    lowest.add(lowestBlock);
                 }
            }
            Random random = new Random();
            int shooting = random.nextInt(lowest.size());
            Ball shot = game.shotOfEnemy(lowest.get(shooting).getCollisionRectangle().getLowerEdge().middle());
            shot.setVelocity(Velocity.fromAngleAndSpeed(170 + random.nextInt(20), 300));
            shot.addToGame(game);
            this.lastTime = System.currentTimeMillis();
        }
    } // shoot

    /**
     * Initialize the positions of the enemies to their original positions.
     * @param dt specifies the number of seconds passed since the last call.
     */
    public void initialize(double dt) {
        while (this.minX > 30) {
            this.updateMovement();
            this.movementDirection = 270;
            this.move(dt);
            this.updateMaps();
        }
        while (this.minX + this.maxX < 750) {
            this.updateMovement();
            this.movementDirection = 90;
            this.move(dt);
            this.updateMaps();
        }
        while (this.minY > 60) {
            this.updateMovement();
            this.movementDirection = 0;
            this.move(dt);
            this.updateMaps();
        }
        this.movementDirection = 90;
        this.speed = this.originalSpeed;
        this.movedDown = false;
        this.stop = false;
    } // initialize

    /**
     * @return true if the enemies reached to the shields, false otherwise.
     */
    public boolean arrivedToShields() {
        return this.stop;
    }

    /**
     * @param block a block to add to the block list.
     */
    public void addBlock(Block block) {
        this.blocks.add(block);
        this.mapping(block);
    }

    /**
     * @param block a block to remove from the block list.
     */
    public void removeBlock(Block block) {
        this.blocks.remove(block);
        this.removeBlockMapping(block);
    }

    /**
     * @param d a draw surface you can draw on.
     */
    public void drawOn(DrawSurface d) {
        for (int i = 0; i < this.blocks.size(); i++) {
            this.blocks.get(i).drawOn(d);
        }
    }

    /**
     * Map the given block according to its X and Y coordinates.
     * @param block the block to map.
     */
    public void mapping(Block block) {
        if (!this.blockByX.containsKey(block.getCollisionRectangle().getUpperLeft().getX())) {
            Set<Block> set = new HashSet<>();
            this.blockByX.put(block.getCollisionRectangle().getUpperLeft().getX(), set);
        }
        if (!this.blockByY.containsKey(block.getCollisionRectangle().getUpperLeft().getY())) {
            Set<Block> set = new HashSet<>();
            this.blockByY.put(block.getCollisionRectangle().getUpperLeft().getY(), set);
        }
        this.blockByX.get(block.getCollisionRectangle().getUpperLeft().getX()).add(block);
        this.blockByY.get(block.getCollisionRectangle().getUpperLeft().getY()).add(block);
    }

    /**
     * Removes the mapping of the given block
     * @param block the block that its mapping should be removed.
     */
    public void removeBlockMapping(Block block) {
        if (block.getCollisionRectangle().getUpperLeft().getY() < 500) {
            this.blockByX.get(block.getCollisionRectangle().getUpperLeft().getX()).remove(block);
                this.blockByY.get(block.getCollisionRectangle().getUpperLeft().getY()).remove(block);
        }
    }

    /**
     * Update the mapping of the given block.
     */
    public void updateMaps() {
        this.blockByX.clear();
        this.blockByY.clear();
        for (Block block : this.blocks) {
            this.mapping(block);
        }
    }

    /**
     * Notify the sprites that time has passed.
     * @param dt the dt value.
     */
    public void timePassed(double dt) {
        this.updateMovement();
        this.move(dt);
        this.updateMaps();
        this.shoot(this.gameLevel);
    }

    /**
     * This method is called whenever the beingHit block is being hit.
     * Remove the block that the ball hit.
     * @param beingHit the block that is being hit.
     * @param hitter the hitting ball.
     */
    public void hitEvent(Block beingHit, Ball hitter) {
        if (hitter.getVelocity().getDy() < 0) {
        this.removeBlock(beingHit);
        }
    }

    @Override
    public void hitEvent(Paddle beingHit, Ball hitter) {
    }

    @Override
    public void addToGame(GameLevel g) {
    }
}