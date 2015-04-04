import javax.swing.*;
import java.awt.*;
import java.io.*;

//DO NOT MODIFY THIS PROGRAM!!!

/**
	This is the driver program for the image processing project
*/
public class ImageProcessorDriver 
{	
	public static void main(String[] args) throws IOException {		
		ImageProcessorGUI p1 = new ImageProcessorGUI();
		p1.setSize(new Dimension(500,600));
		p1.setVisible(true);
		p1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}