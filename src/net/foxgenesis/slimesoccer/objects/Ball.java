package net.foxgenesis.slimesoccer.objects;

import net.foxgenesis.slimesoccer.SlimeSoccer;
import net.foxgenesis.slimesoccer.image.Textures;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Ball extends GameObject {

	private Image ball;
	private final float FRICTION_RESISTANCE_FACTOR = 6;
	private final boolean MOTION_BLUR = true;
	
	public Ball() {
		this(20,20);
	}

	public Ball(float width, float height) {
		super(width,height);
		ball = Textures.get("soccerball").getScaledCopy((int)width,(int)height);
		location.x = SlimeSoccer.getWidth()/2;
		location.y = SlimeSoccer.getHeight()/2;
		velocity.x = 11f;
		velocity.y = 11f;
	}

	@Override
	public void render(Graphics g) {
		if(MOTION_BLUR) {
			ball.draw(location.x-(velocity.x*3), location.y-(velocity.y*3), new Color(1f,1f,1f,0.4f));
			ball.draw(location.x-(velocity.x*2), location.y-(velocity.y*2), new Color(1f,1f,1f,0.5f));
		}
		ball.draw(location.x, location.y);
	}

	@Override
	public void update(int delta) {
		ball.rotate(velocity.x/2 * width);
		if(location.y + height >= SlimeSoccer.getHeight())
			if(velocity.x > 0)
				if(velocity.x - GameObject.GRAVITY_FACTOR/FRICTION_RESISTANCE_FACTOR < 0)
					velocity.x -= velocity.x;
				else
					velocity.x-=GameObject.GRAVITY_FACTOR/FRICTION_RESISTANCE_FACTOR;
			else if(velocity.x < 0)
				if(velocity.x + GameObject.GRAVITY_FACTOR/FRICTION_RESISTANCE_FACTOR > 0)
					velocity.x -= velocity.x;
				else
					velocity.x+=GameObject.GRAVITY_FACTOR/FRICTION_RESISTANCE_FACTOR;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean isEnviormentControlled() {
		return true;
	}

	@Override
	public void onCollide(GameObject a, int axis) {
		if(a instanceof Slime) {
			if(axis == GameObject.X_AXIS)
				velocity.x = a.getVelocity().x*2;
			else if(axis == GameObject.Y_AXIS)
				velocity.y = a.getVelocity().y*2;
		}
	}
	
	@Override
	public boolean contains(float x, float y) {
		return Math.pow(x - location.x+width/2,2) + Math.pow(y - location.y+height/2,2)
				<= Math.pow(getWidth()/2, 2); //check if the point is inside the circle
	}
}
