package net.foxgenesis.slimesoccer.ui.component;


import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * ProgressBar is a UI component that displays progress
 * @author Seth
 */
public class ProgressBar extends Component {
	private double value,max,min;
	private Color foreground,background;
	private Runnable run;
	private boolean pText=true, invert = false;
	private String text;
	
	/**
	 * Create a new progress bar
	 */
	public ProgressBar() {
		super();
		foreground = Color.green;
		background = Color.black;
		max = 100;
		min = 0;
		text="Loading...";
	}
	
	/**
	 * Gets the maximum value of the progress bar
	 * @return maximum value
	 */
	public double getMaximumValue() {
		return max;
	}
	
	/**
	 * Sets whether to display text under the loading bar
	 * @param state - display state
	 */
	public void setPrintText(boolean state) {
		this.pText = state;
	}
	
	/**
	 * Gets the text under the loading bar
	 * @return loading text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets whether to use inverted percentage
	 * @param state - inverted state
	 */
	public void setInvertedPercentage(boolean state) {
		invert = state;
	}
	
	/**
	 * Sets the text to display under the loading bar
	 * @param text - text to display
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Gets the minimum value
	 * @return minimum value
	 */
	public double getMinimumValue() {
		return min;
	}
	
	/**
	 * Sets the maximum value
	 * @param value - maximum value
	 */
	public void setMaximumValue(double value) {
		this.max = value;
	}
	
	/**
	 * Sets the minimum value
	 * @param value
	 */
	public void setMinimumValue(double value) {
		this.min = value;
	}
	
	/**
	 * Sets the current value of the progress bar
	 * @param value
	 */
	public void setValue(double value) {
		if(value>max)
			value = max;
		else if(value < min)
			value = min;
		this.value = value;
	}
	
	/**
	 * Sets the action to run when value is equal to maximum value
	 * @param run
	 */
	public void setAction(Runnable run) {
		this.run = run;
	}
	
	/**
	 * Gets the current value of the progress bar
	 * @return
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * Sets the foreground of the loading bar
	 * @param color
	 */
	public void setForeground(Color color) {
		this.foreground = color;
	}
	
	/**
	 * Sets the background of the loading bar
	 * @param color
	 */
	public void setBackground(Color color) {
		this.background = color;
	}
	
	/**
	 * Gets the background of the loading bar
	 * @return bar background
	 */
	public Color getBackground() {
		return background;
	}
	
	/**
	 * Gets the foreground of the loading bar
	 * @return bar foreground
	 */
	public Color getForeground() {
		return foreground;
	}
	
	@Override
	public void update(int delta) {
		if((invert && value <= min) || (!invert && value >= max))
			if(run != null) {
				run.run();
				run = null;
			}
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		g.setColor(background);
		g.fillRect(getX(), getY(), getWidth(), getHeight());
		g.setColor(foreground);
		g.fillRect(getX(), getY(), (int)(getWidth()/max*value), getHeight());
		g.setColor(Color.black);
		g.drawRect(getX(), getY(), getWidth(), getHeight());
		if(pText) {
			g.setColor(Color.white);
			g.drawString(text, (int) (getX()+getWidth()/2-g.getFont().getWidth(text)/2), getY()+getHeight()+5);
		}
	}

}
