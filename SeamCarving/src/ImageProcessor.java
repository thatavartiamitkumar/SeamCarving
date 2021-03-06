import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * This is the image processing class which has core logic to remove vertical
 * and horizontal seams
 */
public class ImageProcessor {
	private int rowN, columnN; // the image has rowN rows and columnN columns
	private int maxG;
	private int[][] img; // pixel values of the image
	private int[][] originalImg; // original image
	private int[][] energy;
	private int[][] cumMinEnergy;
	private String fileName;
	private String header;

	/**
	 * read the image file
	 * 
	 * @param file
	 *            : the image file
	 */
	public void readImage(File file) {
		this.fileName = file.getPath();
		header = "";
		try {
			Scanner input = new Scanner(file);

			if (input.hasNext("P2"))
				header += input.nextLine() + "\n";
			else
				throw new IOException();

			if (input.hasNext("#.*"))
				header += input.nextLine() + "\n";

			columnN = input.nextInt();
			rowN = input.nextInt();
			maxG = input.nextInt();

			img = new int[rowN][columnN];
			originalImg = new int[rowN][columnN];
			for (int i = 0; i < rowN; i++) {
				for (int j = 0; j < columnN; j++) {
					originalImg[i][j] = img[i][j] = input.nextInt();
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.out.println("warning: file not found");
		} catch (IOException e) {
			System.out.println("warning: file format error");
		}
	}

	/**
	 * This method is called when we click on Process
	 * 
	 * @param processingMethod
	 *            : ...
	 * @param viewer
	 *            : ...
	 */
	public void processImg(String processingMethod, PgmImageViewer viewer) {
		System.out.println("processing method = " + processingMethod);
		resetImgToOriginal();
		if (processingMethod.equals("stretching"))
			viewer.resizeImg(true);
		else if (processingMethod.equals("fixedRatio"))
			viewer.resizeImg(false);
		else if (processingMethod.equals("cropping")) {
			viewer.resizeImg(false);
			int viewerH = viewer.getHeight();
			int viewerW = viewer.getWidth();
			// remove one row/column at a time
			if ((double) rowN / columnN / ((double) viewerH / viewerW) > 1) {
				while ((double) rowN / columnN / ((double) viewerH / viewerW) > 1)
					rowN--;
			} else {
				while ((double) rowN / columnN / ((double) viewerH / viewerW) < 1)
					columnN--;
			}
			img = updateImg();
		} else if (processingMethod.equals("seamCarving")) {
			viewer.resizeImg(false);
			int numberOfHorizontalSeam = 0;
			int numberOfVerticalSeam = 0;

			Scanner userInput = new Scanner(System.in);

			System.out.println("Please enter number of vertical seams: ");

			numberOfVerticalSeam = userInput.nextInt();

			while (numberOfVerticalSeam > columnN) {
				System.out
						.println("Vertical seams entered are greater than number of pixels please enter a lesser number");
				numberOfVerticalSeam = userInput.nextInt();
			}

			System.out.println("Please enter number of horizontal seams: ");

			numberOfHorizontalSeam = userInput.nextInt();

			while (numberOfHorizontalSeam > rowN) {
				System.out
						.println("horizntal seams entered are greater than number of pixels please enter a lesser number");
				numberOfHorizontalSeam = userInput.nextInt();
			}

			Calendar instance = Calendar.getInstance();
			instance.setTime(new Date());
			int startMinute = instance.get(Calendar.MINUTE);
			int startSecond = instance.get(Calendar.SECOND);
			int startMilliSecond = instance.get(Calendar.MILLISECOND);

			int totalTime = startMilliSecond + startSecond * 1000 + startMinute
					* 60 * 1000;

			System.out.println("start time = " + totalTime);

			for (int i = 0; i < numberOfVerticalSeam; i++) {
				img = verticalSeamCarving();
				columnN--;
			}
			for (int i = 0; i < numberOfHorizontalSeam; i++) {
				img = horizontalSeamCarving();
				rowN--;
			}

			instance.setTime(new Date());
			int endMinute = instance.get(Calendar.MINUTE);
			int endSecond = instance.get(Calendar.SECOND);
			int endMilliSecond = instance.get(Calendar.MILLISECOND);

			int endTotalTime = endMilliSecond + endSecond * 1000 + endMinute
					* 60 * 1000;

			System.out.println("end time = " + endTotalTime);
			System.out.println("time taken =" + (endTotalTime - totalTime));

			System.out.println("Process Ends");
		}
	}

	/**
	 * Energy matrix of the image is calculated in this method
	 */
	private void calculatePixelEnergy() {

		energy = new int[rowN][columnN];

		for (int i = 0; i < rowN; i++) {
			for (int j = 0; j < columnN; j++) {

				if (i - 1 < 0 && j - 1 < 0) {
					energy[i][j] = Math.abs(img[i][j] - img[i + 1][j])
							+ Math.abs(img[i][j] - img[i][j + 1]);
				} else if (i + 1 >= rowN && j - 1 < 0) {
					energy[i][j] = Math.abs(img[i][j] - img[i - 1][j])
							+ Math.abs(img[i][j] - img[i][j + 1]);
				} else if (i + 1 >= rowN && j + 1 >= columnN) {
					energy[i][j] = Math.abs(img[i][j] - img[i - 1][j])
							+ Math.abs(img[i][j] - img[i][j - 1]);
				} else if (i - 1 < 0 && j + 1 >= columnN) {
					energy[i][j] = Math.abs(img[i][j] - img[i + 1][j])
							+ Math.abs(img[i][j] - img[i][j - 1]);
				} else if (j - 1 < 0) {
					energy[i][j] = Math.abs(img[i][j] - img[i - 1][j])
							+ Math.abs(img[i][j] - img[i + 1][j])
							+ Math.abs(img[i][j] - img[i][j + 1]);
				} else if (i - 1 < 0) {
					energy[i][j] = Math.abs(img[i][j] - img[i + 1][j])
							+ Math.abs(img[i][j] - img[i][j - 1])
							+ Math.abs(img[i][j] - img[i][j + 1]);
				} else if (i + 1 >= rowN) {
					energy[i][j] = Math.abs(img[i][j] - img[i - 1][j])
							+ Math.abs(img[i][j] - img[i][j - 1])
							+ Math.abs(img[i][j] - img[i][j + 1]);
				} else if (j + 1 >= columnN) {
					energy[i][j] = Math.abs(img[i][j] - img[i - 1][j])
							+ Math.abs(img[i][j] - img[i + 1][j])
							+ Math.abs(img[i][j] - img[i][j - 1]);
				} else {
					energy[i][j] = Math.abs(img[i][j] - img[i - 1][j])
							+ Math.abs(img[i][j] - img[i + 1][j])
							+ Math.abs(img[i][j] - img[i][j - 1])
							+ Math.abs(img[i][j] - img[i][j + 1]);
				}

			}
		}

	}

	/**
	 * cumulative minimum energy matrix for a vertical seam is calculated using
	 * enrgy matrix
	 */
	private void calculateVerticalCumulativeMinEnergy() {

		cumMinEnergy = new int[rowN][columnN];

		for (int i = 0; i < rowN; i++) {
			for (int j = 0; j < columnN; j++) {
				if (i == 0) {
					cumMinEnergy[i][j] = energy[i][j];
				} else {
					if (j - 1 < 0) {
						cumMinEnergy[i][j] = energy[i][j]
								+ Math.min(cumMinEnergy[i - 1][j],
										cumMinEnergy[i - 1][j + 1]);

					} else if (j + 1 >= columnN) {
						cumMinEnergy[i][j] = energy[i][j]
								+ Math.min(cumMinEnergy[i - 1][j - 1],
										cumMinEnergy[i - 1][j]);
					} else {
						cumMinEnergy[i][j] = energy[i][j]
								+ Math.min(cumMinEnergy[i - 1][j - 1], Math
										.min(cumMinEnergy[i - 1][j],
												cumMinEnergy[i - 1][j + 1]));

					}

				}
			}
		}

	}

	/**
	 * Cumulative energy matrix of horizontal seam is calculated using enrgy
	 * matrix
	 */
	private void calculateHorizontalCumulativeMinEnergy() {

		cumMinEnergy = new int[rowN][columnN];

		for (int j = 0; j < columnN; j++) {
			for (int i = 0; i < rowN; i++) {

				if (j == 0) {
					cumMinEnergy[i][j] = energy[i][j];
				} else {
					if (i - 1 < 0) {
						cumMinEnergy[i][j] = energy[i][j]
								+ Math.min(cumMinEnergy[i][j - 1],
										cumMinEnergy[i + 1][j - 1]);

					} else if (i + 1 >= rowN) {
						cumMinEnergy[i][j] = energy[i][j]
								+ Math.min(cumMinEnergy[i - 1][j - 1],
										cumMinEnergy[i][j - 1]);

					} else {
						cumMinEnergy[i][j] = energy[i][j]
								+ Math.min(cumMinEnergy[i - 1][j - 1], Math
										.min(cumMinEnergy[i][j - 1],
												cumMinEnergy[i + 1][j - 1]));

					}

				}
			}
		}

	}

	/**
	 * This method contains the core logic of how the minimum energy pixel is
	 * back traced and removed from the image pixel for a vertical seaming
	 */
	private int[][] verticalSeamCarving() {

		// calculate Energy pixel matrix
		calculatePixelEnergy();
		// calculate cumulative energy matrix for vertical seam
		calculateVerticalCumulativeMinEnergy();

		// seamed new img is saved into this array
		int[][] vSeamImg = new int[rowN][columnN - 1];

		// find the min J value in cumulative minimum energy matrix
		int minJValue = 0;
		for (int j = 0; j < columnN; j++) {

			if (cumMinEnergy[rowN - 1][j] < cumMinEnergy[rowN - 1][minJValue]) {
				minJValue = j;
			}

		}

		// the min value in the last row is @ [rowN-1][minJValue]

		// copy the img array into the seam array after removing the min seam
		for (int i = rowN - 1; i >= 0; i--) {

			// update the min value of J, back tracing the path
			if (i != rowN - 1) {
				int previousJ = minJValue;

				if (previousJ - 1 < 0) {
					if (cumMinEnergy[i][previousJ + 1] < cumMinEnergy[i][previousJ]) {
						minJValue = previousJ + 1;
					} else {
						minJValue = previousJ;
					}
				} else if (previousJ + 1 >= columnN) {
					if (cumMinEnergy[i][previousJ - 1] <= cumMinEnergy[i][previousJ]) {
						minJValue = previousJ - 1;
					} else {
						minJValue = previousJ;
					}
				} else {
					if (cumMinEnergy[i][previousJ - 1] <= cumMinEnergy[i][previousJ]) {
						minJValue = previousJ - 1;
					} else {
						minJValue = previousJ;
					}

					if (cumMinEnergy[i][previousJ + 1] < cumMinEnergy[i][minJValue]) {
						minJValue = previousJ + 1;
					}
				}

			}

			// j is index in the img matrix and k is index in the cropped matrix
			for (int j = 0, k = 0; j < columnN && k < columnN - 1; j++, k++) {

				if (j != minJValue) {
					vSeamImg[i][k] = img[i][j];
				} else {
					k--;
				}
			}
		}
		return vSeamImg;

	}

	private int[][] horizontalSeamCarving() {

		// calculate energy matrix
		calculatePixelEnergy();

		// This method contains the core logic of how the minimum energy pixel
		// is back traced and removed from the image pixel for horizontal seam
		calculateHorizontalCumulativeMinEnergy();

		// seamed new img is saved into this array
		int[][] hSeamImg = new int[rowN - 1][columnN];

		// find the min val in cum matrix

		int minIValue = 0;
		for (int i = 0; i < rowN; i++) {

			if (cumMinEnergy[i][columnN - 1] < cumMinEnergy[minIValue][columnN - 1]) {
				minIValue = i;
			}

		}

		// the min value in the last column is @ [minIValue][columnN-1]

		// copy the img array into the seam array after removing the min seam
		for (int j = columnN - 1; j >= 0; j--) {

			// update the min value of I, back tracing the path
			if (j != columnN - 1) {
				int previousI = minIValue;

				if (previousI - 1 < 0) {
					if (cumMinEnergy[previousI][j] <= cumMinEnergy[previousI + 1][j]) {
						minIValue = previousI;
					} else {
						minIValue = previousI + 1;
					}

				} else if (previousI + 1 >= rowN) {
					if (cumMinEnergy[previousI - 1][j] <= cumMinEnergy[previousI][j]) {
						minIValue = previousI - 1;
					} else {
						minIValue = previousI;
					}

				} else {
					if (cumMinEnergy[previousI - 1][j] <= cumMinEnergy[previousI][j]) {
						minIValue = previousI - 1;
					} else {
						minIValue = previousI;
					}

					if (cumMinEnergy[previousI + 1][j] < cumMinEnergy[minIValue][j]) {
						minIValue = previousI + 1;
					}
				}

			}

			// i is index in the img matrix and k is index in the cropped matrix
			for (int i = 0, k = 0; i < rowN && k < rowN - 1; i++, k++) {

				if (i != minIValue) {
					hSeamImg[k][j] = img[i][j];
				} else {
					k--;
				}
			}
		}
		return hSeamImg;

	}

	/**
	 * add description ...
	 * 
	 * @return : new image
	 */
	private int[][] updateImg() {
		int[][] processedImg = new int[rowN][columnN];

		for (int i = 0; i < rowN; i++) {
			for (int j = 0; j < columnN; j++) {
				processedImg[i][j] = img[i][j];
			}
		}
		return processedImg;
	}

	/**
	 * add description ...
	 */
	public void saveImage() {
		if (fileName != null) {
			String outputFileName = fileName
					.substring(0, fileName.indexOf('.'))
					+ "_processed"
					+ fileName.substring(fileName.indexOf('.'));

			try {
				FileWriter fwriter = new FileWriter(outputFileName);
				PrintWriter outputFile = new PrintWriter(fwriter);

				outputFile.print(header);
				outputFile
						.print("" + columnN + " " + rowN + "\n" + maxG + "\n");
				for (int i = 0; i < rowN; i++) {
					for (int j = 0; j < columnN; j++) {
						outputFile.print(img[i][j] + " ");
					}
					outputFile.print("\n");
				}
				fwriter.close();
			} catch (IOException e) {
				System.out.println("warning: file output error");
			}
		}
	}

	/**
	 * description
	 */
	private void resetImgToOriginal() {
		rowN = originalImg.length;
		columnN = originalImg[0].length;
		img = new int[rowN][columnN];
		for (int i = 0; i < rowN; i++) {
			for (int j = 0; j < columnN; j++) {
				img[i][j] = originalImg[i][j];
			}
		}
	}

	/**
	 * add description ...
	 * 
	 * @return : ...
	 */
	public int[][] getImg() {
		return img;
	}

	/**
	 * description
	 * 
	 * @return : ...
	 */
	public int[][] getOriginalImg() {
		return originalImg;
	}
}
