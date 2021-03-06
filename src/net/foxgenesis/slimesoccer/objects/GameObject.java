package net.foxgenesis.slimesoccer.objects;

import java.util.ArrayList;

import net.foxgenesis.slimesoccer.SlimeSoccer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import static net.foxgenesis.slimesoccer.Settings.CYAN;
import static net.foxgenesis.slimesoccer.Settings.RED;
import static net.foxgenesis.slimesoccer.Settings.GUI_DISTANCE;

/**
 * GameObject is the base class for all game objects
 * @author Seth
 */
public abstract class GameObject 
{
	/**
	 * Gravity factor for all GameObjects
	 */
	public static final float GRAVITY_FACTOR = 0.098f;

	/**
	 * Finals for X_Axis and Y_Axis
	 */
	public static final int X_AXIS = 0, Y_AXIS = 1;

	protected Vector2f location = new Vector2f(), velocity = new Vector2f();
	protected float rotation = 0f;
	protected float width,height;
	protected Shape bounds;
	protected float distanceX = 10f, distanceY = 0f;

	/**
	 * Create a new GameObject with given width and height
	 * @param width - width of object
	 * @param height - height of object
	 */
	public GameObject(float width, float height) {
		this.width = width;
		this.height = height;
		Polygon b = new Polygon();
		b.setClosed(true);
		b.addPoint(location.x,location.y);
		b.addPoint(location.x,location.y+height);
		b.addPoint(location.x+width,location.y+height);
		b.addPoint(location.x+width,location.y);
		bounds = b;
	}

	/**
	 * Sets the rotation of the object
	 * @param rotation - object rotation
	 */
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	/**
	 * Gets the rotation of the object
	 * @return object rotation
	 */
	public float getRotation() {
		return rotation;
	}

	/**
	 * Set the size of the game object
	 * @param width - width of the object
	 * @param height - height of the object
	 */
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	public void draw3D(Graphics g) {
		g.pushTransform();
		g.translate(-distanceX, distanceY);
		render(g, RED);
		g.popTransform();
		render(g, null);
		g.pushTransform();
		g.translate(distanceX, -distanceY);
		render(g, CYAN);
		g.popTransform();
		g.pushTransform();
		g.translate(-GUI_DISTANCE, 0);
		renderGUI(g, RED);
		g.popTransform();
		renderGUI(g, null);
		g.pushTransform();
		g.translate(GUI_DISTANCE, 0);
		renderGUI(g, CYAN);
		g.popTransform();
	}

	/**
	 * Render the object with given graphics
	 * @param g - graphics to draw with
	 */
	public abstract void render(Graphics g, Color imageFilter);
	
	public void renderGUI(Graphics g, Color filter){}

	/**
	 * Called on frame udpate
	 * @param delta - delay from last frame
	 */
	public void update(int delta) {
		bounds.setLocation(location);
	}

	/**
	 * Get the polygon bounds of the game object
	 * @return object bounds
	 */
	public Shape getBounds() {
		return bounds;
	}

	/**
	 * Called on collide with another GameObject
	 * @param x - GameObject collided with
	 * @param axis - axis on which it collided (call twice for both axis)
	 */
	public void onCollide(GameObject[] x, int axis){}

	/**
	 * Gets the location of the object
	 * @return object location
	 */
	public Vector2f getLocation() {
		return location;
	}

	/**
	 * Gets whether the object can move
	 * @return can move
	 */
	public boolean isSolid() {
		return true;
	}

	/**
	 * Gets whether the object should move when pushed
	 * @return
	 */
	public boolean isEnviormentControlled() {
		return false;
	}

	/**
	 * Gets the velocity of the object
	 * @return object velocity
	 */
	public Vector2f getVelocity() {
		return velocity;
	}

	/**
	 * Gets the width of the object
	 * @return width of the object
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * Gets the height of the object
	 * @return object height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * Update the position of the object
	 * @param objects - objects that CAN collide with this object
	 */
	public void updatePosition(GameObject[] objects) {
		if(!isSolid()) {
			GameObject[] x = updateX(objects), y = updateY(objects);
			if(x != null)
				onCollide(x,0);
			if(y != null)
				onCollide(y,1);
		}
		bounds.setLocation(location);
	}

	private GameObject[] updateX(GameObject[] objects) {
		ArrayList<GameObject> output = new ArrayList<GameObject>();
		for(GameObject a: objects)
			if(a != null && a != this)
				if(a.contains(location.x + velocity.x, location.y))
					output.add(a);
		if(output.isEmpty())
			if(!outOfBounds(location.x + velocity.x,location.y,true,false))
				location.x += velocity.x;
			else
				velocity.x = isEnviormentControlled()?-velocity.x/2:0f;
		return output.toArray(new GameObject[]{});
	}

	private GameObject[] updateY(GameObject[] objects) {
		ArrayList<GameObject> output = new ArrayList<GameObject>();
		for(GameObject a: objects)
			if(a != null && a != this)
				if(a.contains(location.x, location.y + velocity.y))
					output.add(a);
		if(output.isEmpty())
			if(!outOfBounds(location.x,location.y + velocity.y)) {
				velocity.y += GRAVITY_FACTOR;
				location.y += velocity.y;
			}
			else
				velocity.y = isEnviormentControlled()?-velocity.y/2:0f;
		return output.toArray(new GameObject[]{});
	}

	/**
	 * Checks if a point is within the bounds of the object
	 * \nNOTE: Override this if object does NOT have a rectangular bounds
	 * @param x - x point
	 * @param y - y point
	 * @return if point is within bounds
	 */
	public boolean contains(float x, float y) {
		if(x >= location.x && x <= location.x + width)
			if(y >= location.y && y <= location.y + height) 
				return true;
		return false;
	}

	/**
	 * Checks if a point is outside the screen
	 * @param x - x point
	 * @param y - y point
	 * @return point is outside the screen
	 */
	protected boolean outOfBounds(float x, float y) {
		return outOfBounds(x,y,true,true);
	}

	/**
	 * Checks if a point is outside the screen. allows choice of axis
	 * @param x - x point
	 * @param y - y point
	 * @param testX - test x axis
	 * @param testY - test y axis
	 * @return point is outside of screen
	 */
	protected boolean outOfBounds(float x, float y, boolean testX, boolean testY) {
		if(testX)
			if(x < 0 || x+width > SlimeSoccer.getWidth())
				return true;
		if(testY)
			if(y-height/2 < 0 || y+height > SlimeSoccer.getHeight())
				return true;
		return false;
	}
}
