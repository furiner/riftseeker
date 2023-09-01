package net.arcadiasedge.riftseeker.world.locations;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import org.bukkit.Location;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This is a representation of a certain location in the game world, such as a spawn point, a capture point, etc.
 *
 * It's boundaries are set by a list of points, whereas players inside the boundaries are considered to be inside
 * the location.
 */
public class GameLocation {
    /**
     * The name of the location.
     */
    public String name;

    /**
     * A list of vertices that define the boundaries of the location.
     */
    public List<Location> points;

    /**
     * A component that represents the name of the location.
     */
    public Component displayName;

    public int priority;

    /**
     * Whether the location's name should be shown when a player newly enters the location.
     */
    public boolean showTitle;

    public GameLocation(String name) {
        this(name, new ArrayList<>());
    }

    public GameLocation(String name, List<Location> points) {
        this.name = name;
        this.points = points;
        this.priority = 0;
        this.showTitle = false;
    }

    /**
     * Gets the name of the location.
     * @return The name of the location.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of vertices that define the boundaries of the location.
     * @return The list of vertices that define the boundaries of the location.
     */
    public List<Location> getPoints() {
        return points;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Gets the component that represents the name of the location.
     * @return The component that represents the name of the location.
     */
    public Component getDisplayName() {
        return displayName;
    }

    /**
     * Whether the location's name should be shown when a player newly enters the location.
     * @return Whether the location's name should be shown when a player newly enters the location.
     */
    public boolean getShowTitle() {
        return showTitle;
    }

    /**
     * Sets whether the location's name should be shown when a player newly enters the location.
     * @param showTitle Whether the location's name should be shown when a player newly enters the location.
     */
    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Sets the display name of the location.
     * @param component The component that represents the name of the location.
     */
    public void setDisplayName(Component component) {
        this.displayName = component;
    }

    /**
     * Given a point, returns whether the point is inside the list of vertices that define the boundaries of the
     * location. This is done by raycasting from the point to the right, and counting the number of intersections
     * the ray has with the lines that make up the boundaries of the location. If the number of intersections is
     * odd, the point is inside the location.
     * @param location The point in space to check.
     * @return True if the point is inside the location, otherwise false.
     */
    public boolean contains(Location location) {
        // TODO: This entire function does not seem to work.

        // Easy check: if the location is in a different world, it's definitely not in the location.
        if (location.getWorld() != points.get(0).getWorld()) {
            return false;
        }

        // Introducing, horrible math!
        var isInside = false;

        // First, we need to get the list of points that are above the given y value.
        var points = new ArrayList<Location>();
        for (var point : this.points) {
            if (point.getY() >= location.getY()) {
                points.add(point);
            }
        }

        // Now, we need to check the number of intersections.
        var intersections = 0;
        for (var point : points) {
            var previousPoint = points.get(points.indexOf(point) - 1);

            // If the line is horizontal, we can't do anything with it.
            if (point.getY() == previousPoint.getY()) {
                continue;
            }

            // If the line is vertical, we can't do anything with it.
            if (point.getX() == previousPoint.getX()) {
                continue;
            }

            // Now, we need to check if the line intersects the given location's y value.
            var m = (point.getY() - previousPoint.getY()) / (point.getX() - previousPoint.getX());
            var b = point.getY() - (m * point.getX());
            var x = (location.getY() - b) / m;

            if (x > location.getX()) {
                intersections++;
            }
        }

        // If there are an odd number of intersections, the location is inside the location.
        if (intersections % 2 == 1) {
            isInside = true;
        }

        System.out.println("Location " + name + " contains " + location + ": " + isInside);
        System.out.println("Intersections: " + intersections);

        return isInside;
    }
}
