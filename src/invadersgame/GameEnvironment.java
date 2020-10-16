package invadersgame;
import java.util.ArrayList;
import java.util.List;

import collision.Collidable;
import collision.CollisionInfo;
import geometry.Line;
import geometry.Point;
import geometry.Rectangle;

/**
 * The GameEnvironment class represents a collection of objects that a ball can collide with them.
 * The ball should know the game environment, and use it to check for collisions and direct its movement.
 */
public class GameEnvironment {
    // Declare the members of the class.
    private List<Collidable> collidablesList = new ArrayList<>();

    /**
     * Add the given collidable to the game environment.
     * @param c the collidable object that should be added to the game environment.
     */
    public void addCollidable(Collidable c) {
        this.collidablesList.add(c);
    }

    /**
     * @param collidable the collidable object that we want to remove from the game environment.
     */
    public void removeCollidable(Collidable collidable) {
        this.collidablesList.remove(collidable);
    }

    /**
     * @param trajectory the movement line of the object.
     * @return the information about the closest collision that is going to occur. If the object isn't going to collide
     * with any of the collidables of the collection, return null.
     */
    public CollisionInfo getClosestCollision(Line trajectory) {
        Collidable pointerToList = null;
        Collidable saver = null;
        Point closestInter = null; // The closest point.
        Point currentInter = null; // The current point.
        Rectangle recCollidable = null;
        // Run over all the collidiables and find the closest colliding point with the line.
        for (int i = 0; i < this.collidablesList.size(); i++) {
            // Set a pointer to the current position in the list.
            pointerToList = this.collidablesList.get(i);
            // Get the rectangle shape of the collidable.
            recCollidable = pointerToList.getCollisionRectangle();
            // Find the closest intersection with the line.
            currentInter = trajectory.closestIntersectionToStartOfLine(recCollidable);
            if (currentInter !=  null && closestInter == null) {
                // Update the closest to be the current intersection.
                closestInter = currentInter;
                saver = pointerToList;
            } else if (currentInter != null && closestInter != null && trajectory.start().distance(currentInter)
                < trajectory.start().distance(closestInter)) {
                closestInter = currentInter;
                saver = pointerToList;
            }
        }
        return new CollisionInfo(closestInter, saver);
    }

} // class GameEnvironment
