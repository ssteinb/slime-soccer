package net.foxgenesis.slimesoccer.ui;

import java.util.HashMap;

import net.foxgenesis.slimesoccer.Settings;
import net.foxgenesis.slimesoccer.SlimeSoccer;
import net.foxgenesis.slimesoccer.font.Fonts;
import net.foxgenesis.slimesoccer.image.Textures;
import net.foxgenesis.slimesoccer.io.KeyboardInput;
import net.foxgenesis.slimesoccer.sound.Sounds;
import net.foxgenesis.slimesoccer.ui.component.ProgressBar;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import static net.foxgenesis.slimesoccer.Settings.CYAN;
import static net.foxgenesis.slimesoccer.Settings.RED;
import static net.foxgenesis.slimesoccer.Settings.GUI_DISTANCE;

/**
 * Loading is the loading screen at the start of the game
 * @author Seth
 */
public class Loading extends Scene {

	private Image background,ball,image;
	private AngelCodeFont hiero;
	private ProgressBar bar;
	private int update = 0,update2 = 0,speed=1;
	private boolean getImage = false;
	private int step = -1;
	private String updateString = "Loading textures...";

	/**
	 * Create a new loading screen instance
	 * @throws SlickException 
	 */
	public Loading() throws SlickException {
		super();
		background = new Image("textures/background/mainBackground.jpg");
		ball = new Image("textures/soccerball.png").getScaledCopy(50,50);
		hiero = new AngelCodeFont("fonts/hiero.fnt", new Image("textures/hiero.png"));
		bar = new ProgressBar();
		bar.setVisible(true);
		bar.setMaximumValue(6);
		bar.setText("loading textures...");
		bar.setSmoothValues(false);
		bar.setAction(new Runnable() {
			@Override
			public void run() {
				bar.setVisible(false);
			}
		});
	}

	@Override
	public void draw(GameContainer container, Graphics g) {
		String title = "Slime Soccer", input = "PRESS ENTER";
		g.drawImage(background, 0, 0, container.getWidth(), 
				container.getHeight(), 0, 0, background.getWidth(), background.getHeight());
		ball.rotate((float) (Math.cos(0.05 * update2)/2 * ball.getWidth()/2));
		if(Settings.ANAGLYPH)
			g.drawImage(ball,container.getWidth()/2 - ball.getWidth()/2
					- (float)(hiero.getHeight(title)*2 * Math.sin(0.05 * update2))-GUI_DISTANCE,50, RED);
		g.drawImage(ball,container.getWidth()/2 - ball.getWidth()/2
				- (float)(hiero.getHeight(title)*2 * Math.sin(0.05 * update2)),50);
		if(Settings.ANAGLYPH)
			g.drawImage(ball,container.getWidth()/2 - ball.getWidth()/2
					- (float)(hiero.getHeight(title)*2 * Math.sin(0.05 * update2))+GUI_DISTANCE,50, CYAN);
		g.pushTransform();
		g.translate(0, -80);
		if(Settings.ANAGLYPH)
			hiero.drawString(container.getWidth()/2-hiero.getWidth(title)/2-GUI_DISTANCE,
					container.getHeight()/2-hiero.getHeight(title)/2, title, RED);
		hiero.drawString(container.getWidth()/2-hiero.getWidth(title)/2,
				container.getHeight()/2-hiero.getHeight(title)/2, title);
		if(Settings.ANAGLYPH)
			hiero.drawString(container.getWidth()/2-hiero.getWidth(title)/2+GUI_DISTANCE,
					container.getHeight()/2-hiero.getHeight(title)/2, title, CYAN);
		if(bar.isVisible())
			if(Settings.ANAGLYPH)
				bar.draw3D(g);
			else
				bar.draw(g, null);
		else {
			if(Settings.ANAGLYPH)
				hiero.drawString(container.getWidth()/2-hiero.getWidth(input)/2+10-GUI_DISTANCE,container.getHeight()-100, input, update > 15/2?Color.red.multiply(RED):Color.orange.multiply(RED));
			hiero.drawString(container.getWidth()/2-hiero.getWidth(input)/2+10,container.getHeight()-100, input, update > 15/2?Color.red:Color.orange);
			if(Settings.ANAGLYPH)
				hiero.drawString(container.getWidth()/2-hiero.getWidth(input)/2+10+GUI_DISTANCE,container.getHeight()-100, input, update > 15/2?Color.red.multiply(CYAN):Color.orange.multiply(CYAN));

		}
		g.popTransform();
		if(getImage) {
			try {
				image = new Image(container.getWidth(),container.getHeight());
				g.copyArea(image, 0, 0);
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update(GameContainer gc, int i) {
		if(++update >= 15)
			update = 0;
		if((update2 += speed) >= 360)
			speed = -speed;
		switch(step) {
		case 0:
			updateString = "loading animations...";
			Textures.init();
			break;
		case 1:
			updateString = "loading fonts...";
			try {
				Textures.confetti = Textures.createGif("falling confetti.gif", 30);
			} catch (SlickException e) {
				e.printStackTrace();
			}
			break;
		case 2:
			updateString = "loading music...";
			Fonts.init();
			break;
		case 3:
			updateString = "loading sounds...";
			try {
				SlimeSoccer.music = new Music("music/cactus.ogg");
			} catch (SlickException e) {
				e.printStackTrace();
			}
			break;
		case 4:
			updateString = "loading main menu...";
			Sounds.init();
			break;
		case 5:
			SlimeSoccer.music.loop();
			Scene.store("mainMenu", new MainMenu());
			break;
		}
		bar.setValue(step+=1);
		bar.setSize(gc.getWidth()/3*2,20);
		bar.setText(updateString);
		bar.setLocation(gc.getWidth()/2-bar.getWidth()/2,gc.getHeight()-100);
		bar.update(i);
		ball.rotate((float)(hiero.getHeight("Slime Soccer")/8 * Math.sin(0.05 * update2)));
		if(!bar.isVisible() && KeyboardInput.keys[Keyboard.KEY_RETURN])
			getImage = true;
		if(image != null) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("image",image);
			Scene.setCurrentScene(Scene.getScene("mainMenu"), params);
		}
	}

	@Override
	void load(HashMap<String, Object> params) {
	}

	@Override
	void unload(Scene scene) {

	}

}
