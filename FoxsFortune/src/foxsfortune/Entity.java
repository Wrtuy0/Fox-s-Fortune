package foxsfortune;

/**
 * Represents an entity in the game with a name, position, and movement state.
 *
 * @author reesesanders
 */
public class Entity {
    private String name;
    private int xPos;
    private int yPos;
    private boolean moving;

    public Entity() {
        this.name = "";
        this.xPos = 0;
        this.yPos = 0;
        this.moving = false;
    }

    public Entity(String name, int xPos, int yPos, boolean moving) {
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        this.moving = moving;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "name='" + name + '\'' +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", moving=" + moving +
                '}';
    }
}
