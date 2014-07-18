package voxel.landscape.map.debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Array2DViewer extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BufferedImage image;

	private static Array2DViewer instance = null;

	public static Array2DViewer getInstance() {
		return instance;
	}
	public static Array2DViewer getInstance(int width, int height) {
		  if(instance == null) {
		     instance = new Array2DViewer(width,height);
		  }
		  return instance;
	  }

	  protected Array2DViewer(int width, int height) {
	    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	  }
	  
	  public void setPixel(int x, int y, Color c) {
		  if (outOfBounds(x,y)) return;
		  image.setRGB(x, y, c.getRGB());
	  }
	  private boolean outOfBounds(int x, int y) {
		  return x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight();
	  }
	  
	  public BufferedImage getImage() { return image; }
	  
	  public void saveToPNG() {
		  saveToPNG("testimage.png");
	  }
	  
	  private void fillImage() {
		  for (int x = 0 ; x < image.getWidth(); ++x) {
			  for (int y = 0; y < image.getHeight() ; ++y) {
				  image.setRGB(x, y, Color.BLACK.getRGB());
			  }
		  }
	  }
	  
	  public void saveToPNG(String name) {
		  try {
			    File outputfile = new File(name);
			    ImageIO.write(image, "png", outputfile);
			} catch (IOException e) {
			   
			}

	  }

	  public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;
	    g2.drawImage(image, null, null);
	    g2.dispose();
	  }

}
