package net.foxgenesis.slimesoccer.objects;

import java.util.Timer;
import java.util.TimerTask;

import net.foxgenesis.slimesoccer.SlimeSoccer;
import net.foxgenesis.slimesoccer.font.Fonts;
import net.foxgenesis.slimesoccer.image.Textures;
import net.foxgenesis.slimesoccer.io.KeyboardInput;
import net.foxgenesis.slimesoccer.ui.component.ProgressBar;
import net.foxgenesis.slimesoccer.util.SlimeAbilityUtil;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * Slime is the player object
 * @author Seth
 */
public class Slime extends GameObject {
	private final Image img;
	private Image flipped;
	private final Type type;
	private static final float MAX_SPEED = 5f, SPEED = 0.3f, JUMP_VELOCITY = -5f;
	protected final boolean controlled, secondary;
	private boolean canJump = true,abilityCheck = true, indianJump = true;
	private int direction = 0, goals = 0;
	private Font font;
	private double cooldown = 0.5;
	private ProgressBar bar;
	private Timer timer;
	protected float opacity = 1f;
	protected static boolean paused;

	/**
	 * Create a new Slime with a type and controlled parameters
	 * @param type -  type of slime to make
	 * @param controlled - is this player controlled
	 */
	public Slime(Type type, boolean controlled) {
		this(type,controlled,false);
	}

	/**
	 * Create a new Slime with a type and controlled parameters
	 * @param type -  type of slime to make
	 * @param controlled - is this player controlled
	 * @param secondaryInput - if to use secondary input controls
	 */
	public Slime(Type type, boolean controlled, boolean secondaryInput) {
		super(100, 100);
		img = Textures.get(type.img).getScaledCopy((int)width,(int)height);
		flipped = img.getFlippedCopy(true,false);
		img.rotate(90);
		this.type = type;
		font = Fonts.get("hiero");
		this.controlled = controlled;
		this.secondary = secondaryInput;
		timer = new Timer();
		bar = new ProgressBar();
		bar.setPrintText(false);
		bar.setSize(SlimeSoccer.getWidth()/2-60, 20);
		bar.setLocation(secondary?SlimeSoccer.getWidth()-bar.getWidth()-10f:10f, 50f);
		bar.setBackground(secondary?Color.black:Color.green);
		bar.setForeground(secondary?Color.green:Color.black);
		bar.setValue(50);
		if(!secondary)
			bar.setInvertedPercentage(true);
	}

	/**
	 * Get the current images for the slime
	 * @return slime images
	 */
	public Image[] getImages() {
		return new Image[]{img,flipped};
	}

	/**
	 * Get the amount of goals the slime has made
	 * @return goal count
	 */
	public int getGoalCount() {
		return goals;
	}

	/**
	 * Set the amount of goals the slime has made
	 * @param count - goal count
	 */
	public void setGoalCount(int count) {
		goals = count;
	}

	/**
	 * Toggle indian jump
	 */
	public void changeJump() {
		indianJump = !indianJump;
	}
	
	/**
	 * Set the display opacity for the slime
	 * @param opacity - display opacity
	 */
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	/**
	 * Get the current display opacity of the slime
	 * @return display opacity
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Gets the type of slime
	 * @return type of slime
	 */
	public Type getType() {
		return type;
	}

	@Override
	public void render(Graphics g) {
		bar.draw(g);
		switch(direction) {
		case 0:
			if(img != null)
				img.draw(location.x,location.y, width, height, new Color(1f,1f,1f,opacity));
			break;
		case 1:
			if(flipped != null)
				flipped.draw(location.x,location.y, width, height, new Color(1f,1f,1f,opacity));
			break;
		}
		font.drawString(secondary?SlimeSoccer.getWidth()-font.getWidth(type.name())-5:15, 55, type.name(), Color.black);
		font.drawString(secondary?SlimeSoccer.getWidth()-font.getWidth(type.name())-10:10, 50, type.name());

		font.drawString(SlimeSoccer.getWidth()/2 - font.getWidth(""+goals)/2 - (secondary?-40:35), 55, ""+goals, Color.black);
		font.drawString(SlimeSoccer.getWidth()/2 - font.getWidth(""+goals)/2 - (secondary?-35:40), 50, ""+goals);
	}

	/**
	 * Gets the direction that the slime is facing. 0 = RIGHT, 1 = LEFT
	 * @return slime direction
	 */
	public int getDirection() {
		return direction;
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		bar.update(delta);
		bar.setValue(bar.getValue()+(secondary?cooldown:-cooldown));
		img.setRotation(this.getRotation());
		flipped = img.getFlippedCopy(true, false);
		flipped.rotate(-img.getRotation());

		if(controlled) {
			if(KeyboardInput.keys[secondary?Keyboard.KEY_RIGHT:Keyboard.KEY_D]) {
				velocity.x = velocity.x + SPEED < MAX_SPEED?velocity.x+=SPEED:MAX_SPEED;
				direction = 1;
			}
			else if(KeyboardInput.keys[secondary?Keyboard.KEY_LEFT:Keyboard.KEY_A]) {
				velocity.x = velocity.x - SPEED > -MAX_SPEED?velocity.x-=SPEED:-MAX_SPEED;
				direction = 0;
			}
			else
				// if(indianJump)
				//{
				if(canJump)
					if(velocity.x > 0)
						if(velocity.x - SPEED/2 < 0)
							velocity.x -= velocity.x;
						else
							velocity.x-=SPEED/2;
					else if(velocity.x < 0)
						if(velocity.x + SPEED/2 > 0)
							velocity.x -= velocity.x;
						else
							velocity.x+=SPEED/2;
			if(indianJump && KeyboardInput.keys[secondary?Keyboard.KEY_UP:Keyboard.KEY_W] && canJump) {
				velocity.y = JUMP_VELOCITY;
				canJump = false;
			}

			if(abilityCheck && canUseAbility() && KeyboardInput.keys[secondary?Keyboard.KEY_SPACE:Keyboard.KEY_1]) {
				abilityCheck = false;
				bar.setValue(bar.getValue()+(secondary?-50:50));
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						abilityCheck = true;
					}
				}, 1000);
				SlimeAbilityUtil.handleFirstAbility(this);
			}
			if(outOfBounds(location.x,location.y + velocity.y,false,true))
				canJump = true;
		}
	}

	@Override
	public boolean isSolid() {
		return paused;
	}

	/**
	 * Checks if the slime is using secondary controls
	 * @return is slime player two
	 */
	public boolean isSecondaryControls() {
		return secondary;
	}

	/**
	 * Checks if the player can use their ability
	 * @return if the player can use their ability
	 */
	public boolean canUseAbility() {
		return secondary?bar.getValue()>bar.getMaximumValue()/2:bar.getValue()<bar.getMaximumValue()/2;
	}

	/**
	 * Type contains the list of Slime types
	 * @author Seth
	 */
	public static enum Type {
		DEFAULT("defaultslime",1000),
		GOAL("goal",1000),
		SPONGE("spongeslime",1000),
		DISCO("discoslime",1000),
		INDIAN("indianslime",1000),
		NATURE("natureslime", 1000),
		RUNNER("runnerslime", 1000),
		GHOST("ghostslime", 1000),
		FIRE("fireslime", 1000),
		TRAFFIC("trafficslime", 1000),
                ICE("iceeslime", 1000),
                MONK("monkslime", 1000),
                WATER("waterslime", 1000),
                BOXER("boxerslime", 1000),
		TEST5("svetty",1000);

		private String img;
		private long duration;
		/**
		 * Create a new Type with given Image path
		 * @param img - Image path
		 */
		Type(String img, long duration) {
			this.img = img;
			this.duration = duration;
		}

		/**
		 * Get the texture name used for the slime
		 * @return slime texture name
		 */
		public String getTextureName() {
			return img;
		}

		/**
		 * Get the duration for the ability
		 * @return ability duration
		 */
		public long getDuration() {
			return duration;
		}
	}

}
