package Converter;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageConverter {

	public static void main(String[] args) {
        byte[] bytes = send(new File(new File("").getAbsolutePath()+"/src/emoticons/weird.png"));
        receive(bytes);
	}


  public static void receive(byte[] packet) {
		InputStream in = new ByteArrayInputStream(packet);
		BufferedImage imageInByte;
		try {
			imageInByte = ImageIO.read(in);
			ImageIO.write(imageInByte, "png", new File(Integer.toString((int) System.currentTimeMillis()) + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

  }

  public static byte[] send(File file) {
    byte[] imageInByte = {};
    try {
	  	BufferedImage image = ImageIO.read(file);
	  	ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
	  	ImageIO.write(image, "png", byteArrayOS);
	  	byteArrayOS.flush();
	  	imageInByte = byteArrayOS.toByteArray();
	  	byteArrayOS.close();
        System.out.println("sending image bytes: " + imageInByte.length);
	  	return imageInByte;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return imageInByte;
  }
}
