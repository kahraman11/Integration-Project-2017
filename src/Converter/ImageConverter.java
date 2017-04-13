package Converter;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageConverter {


  public void receive(byte[] packet) {
		InputStream in = new ByteArrayInputStream(packet);
		BufferedImage imageInByte;
		try {
			imageInByte = ImageIO.read(in);
			ImageIO.write(imageInByte, "png", new File(Integer.toString((int) System.currentTimeMillis()) + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

  }

  public byte[] send(File file) {
    byte[] imageInByte = {};
    try {
	  	BufferedImage image = ImageIO.read(file);
	  	ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
	  	ImageIO.write(image, "png", byteArrayOS);
	  	byteArrayOS.flush();
	  	imageInByte = byteArrayOS.toByteArray();
	  	byteArrayOS.close();
	  	return imageInByte;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return imageInByte;
  }
}
