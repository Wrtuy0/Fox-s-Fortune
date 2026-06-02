package foxsfortune;

/**
 * Simple data holder for a checkpoint area in the level.
 *
 * A checkpoint stores a rectangle (x,y,width,height) used by the
 * `GamePanel` to set player respawn locations when the player touches it.
 */
public class Checkpoint {
    /** Top-left x coordinate of the checkpoint rectangle. */
    public int x;
    /** Top-left y coordinate of the checkpoint rectangle. */
    public int y;
    /** Width of the checkpoint rectangle in pixels. */
    public int width;
    /** Height of the checkpoint rectangle in pixels. */
    public int height;

    /**
     * Create a checkpoint rectangle at the given position and size.
     *
     * @param x top-left x coordinate
     * @param y top-left y coordinate
     * @param width rectangle width
     * @param height rectangle height
     */
    public Checkpoint(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
