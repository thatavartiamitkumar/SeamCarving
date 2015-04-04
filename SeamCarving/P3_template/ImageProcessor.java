import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.io.File;

/**
	general description of the class
	...
*/
public class ImageProcessor 
{
	private int rowN,columnN;     //the image has rowN rows and columnN columns
   private int maxG;
	private int[][] img;		      //pixel values of the image 
	private int[][] originalImg;  //original image 
	private int[][] energy;
	private int[][] cumMinEnergy; 
	private boolean verticalSeam = false;
	private String fileName;
	private String header;
	
	/** 
		read the image file
		@param file : the image file
	*/	
	public void readImage(File file)
	{			
		this.fileName = file.getPath(); 
		header = "";
		try {
			Scanner input = new Scanner(file);
			
			if (input.hasNext("P2")) 
				header += input.nextLine()+"\n";
			else
				throw new IOException();
					
			if (input.hasNext("#.*")) 
				header += input.nextLine()+"\n";
			
			columnN = input.nextInt();
			rowN = input.nextInt();
			maxG = input.nextInt();
			
			img = new int[rowN][columnN];
			originalImg = new int[rowN][columnN];
			for (int i=0; i<rowN; i++) {
				for (int j=0; j< columnN; j++) {
					originalImg[i][j] = img[i][j] = input.nextInt();
				}
			}
			input.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("warning: file not found");
		}
		catch (IOException e) {
			System.out.println("warning: file format error");
		}
	}	
	/**
		add description ...
		@param processingMethod : ...
		@param viewer : ...
	*/
	public void processImg(String processingMethod, PgmImageViewer viewer)
	{
		System.out.println("processing method = "+processingMethod);
		resetImgToOriginal();
		if (processingMethod.equals("stretching"))
			viewer.resizeImg(true);
		else if (processingMethod.equals("fixedRatio")) 
			viewer.resizeImg(false);
		else if (processingMethod.equals("cropping")) {
			viewer.resizeImg(false);
			int viewerH = viewer.getHeight();
			int viewerW = viewer.getWidth();
			//remove one row/column at a time
			if ((double)rowN/columnN/((double)viewerH/viewerW)>1) {
				while ((double)rowN/columnN/((double)viewerH/viewerW)>1)  
					rowN--;				
			}
			else {
				while ((double)rowN/columnN/((double)viewerH/viewerW)<1)
					columnN--;
			}
			img = updateImg();
		} 
		else if(processingMethod.equals("seamCarving")) {		
			viewer.resizeImg(false);
			
			// your code
			
			img = updateImg();		
		}
	}	
	/**
		add description ...
	*/
	private void calculatePixelEnergy() {
		
		// your code

	}	
	/**
		add description ...
	*/
	private void calculateCumulativeMinEnergy() {
		
		// your code

	}
	/**
		add description ...
	*/
	private void seamCarving() {
		
		// your code

	}
	/**
		add description ...
		@return : new image
	*/
	private int[][] updateImg() {
		int[][] processedImg = new int[rowN][columnN];

		for (int i=0; i<rowN; i++) {
			for (int j=0; j< columnN; j++) {
				processedImg[i][j] = img[i][j];
			}
		}
		return processedImg;
	}
	
	/**
		add description ...
	*/
	public void saveImage()
	{
		if (fileName!=null) {
			String outputFileName = fileName.substring(0, fileName.indexOf('.'))
				+"_processed"+fileName.substring(fileName.indexOf('.'));
				
			try {
				FileWriter fwriter = new FileWriter(outputFileName);
				PrintWriter outputFile = new PrintWriter(fwriter);
		
				outputFile.print(header);
            outputFile.print(""+columnN+" "+rowN+"\n"+maxG+"\n");
				for (int i=0; i<rowN; i++) {
					for (int j=0; j< columnN; j++) {
						outputFile.print(img[i][j] + " ");
					}
					outputFile.print("\n");
				}
				fwriter.close();
			}
			catch (IOException e) {
				System.out.println("warning: file output error");
			}
		}
	}
	/**
		description
	*/
	private void resetImgToOriginal()
	{
		rowN = originalImg.length;
		columnN = originalImg[0].length;
		img = new int[rowN][columnN];
		for (int i=0; i<rowN; i++) {
			for (int j=0; j< columnN; j++) {
				img[i][j] = originalImg[i][j];
			}
		}
	}
	/**
		add description ...
		@return : ...
	*/
	public int[][] getImg()
	{
		return img;
	}
	
	/**
		description
		@return : ...
	*/
	public int[][] getOriginalImg()
	{
		return originalImg;
	}
}
