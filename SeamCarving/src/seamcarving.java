import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class seamcarving {
	private int[][] originalimage;
	private int rowN;
	private int columnN;
	private int[][] intensity;
	private int[][] cum_intensity;
	private String fileName;
	private String header;
	private int maxG;
	private int[][] img;
	private int[] x, y;

	public void readImage(String string2) {
		String string = "C:\\Users\\Shashank\\Desktop\\testcases\\testcase1.pgm";
		fileName = string.substring(string.lastIndexOf("\\") + 1);
		header = "";
		try {
			Scanner input = new Scanner(new File(string));
			System.out.println("01" + input);
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
			originalimage = new int[rowN][columnN];
			System.out.println("original matirx");
			for (int i = 0; i < rowN; i++) {
				for (int j = 0; j < columnN; j++) {
					originalimage[i][j] = img[i][j] = input.nextInt();
					System.out.print("\t" + originalimage[i][j]);
				}
				System.out.println("\n");
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.out.println("warning: file not found");
		} catch (IOException e) {
			System.out.println("warning: file format error");
		}
	}

	public void energymatrix() {

		intensity = new int[rowN][columnN];

		for (int i = 0; i < rowN; i++) {
			for (int j = 0; j < columnN; j++) {
				if (i == 0 && j == 0) {
					intensity[i][j] = Math.abs(originalimage[i][j]
							- originalimage[i][j + 1])
							+ Math.abs(originalimage[i][j]
									- originalimage[i + 1][j]);
				} else if (i == 0 && j != 0) {
					if (j == columnN - 1) {
						intensity[i][j] = Math.abs(originalimage[i][j]
								- originalimage[i + 1][j])
								+ Math.abs(originalimage[i][j]
										- originalimage[i][j - 1]);
					} else {
						intensity[i][j] = Math.abs(originalimage[i][j]
								- originalimage[i][j + 1])
								+ Math.abs(originalimage[i][j]
										- originalimage[i][j - 1])
								+ Math.abs(originalimage[i][j]
										- originalimage[i + 1][j]);

					}
				} else if (i != 0 && j == 0) {
					if (i == rowN - 1) {
						intensity[i][j] = Math.abs(originalimage[i][j]
								- originalimage[i][j + 1])
								+ Math.abs(originalimage[i][j]
										- originalimage[i - 1][j]);
					} else {
						intensity[i][j] = Math.abs(originalimage[i][j]
								- originalimage[i + 1][j])
								+ Math.abs(originalimage[i][j]
										- originalimage[i - 1][j])
								+ Math.abs(originalimage[i][j]
										- originalimage[i][j + 1]);
					}
				} else if (i != 0 && i != rowN - 1 && j == columnN - 1) {
					intensity[i][j] = Math.abs(originalimage[i][j - 1]
							- originalimage[i][j])
							+ Math.abs(originalimage[i + 1][j]
									- originalimage[i][j])
							+ Math.abs(originalimage[i - 1][j]
									- originalimage[i][j]);
				} else if (i == rowN - 1 && (j != 0 && j != columnN - 1)) {
					intensity[i][j] = Math.abs(originalimage[i][j]
							- originalimage[i - 1][j])
							+ Math.abs(originalimage[i][j]
									- originalimage[i][j - 1])
							+ Math.abs(originalimage[i][j]
									- originalimage[i][j + 1]);

				} else if (i == rowN - 1 && j == columnN - 1) {
					intensity[i][j] = Math.abs(originalimage[i][j]
							- originalimage[i - 1][j])
							+ Math.abs(originalimage[i][j]
									- originalimage[i][j - 1]);

				} else {
					if (i != 0 && j != 0 && i != rowN - 1 && j != columnN - 1) {
						intensity[i][j] = Math.abs(originalimage[i][j]
								- originalimage[i + 1][j])
								+ Math.abs(originalimage[i][j]
										- originalimage[i - 1][j])
								+ Math.abs(originalimage[i][j]
										- originalimage[i][j + 1])
								+ Math.abs(originalimage[i][j]
										- originalimage[i][j - 1]);
					}
				}
			}
		}

		System.out.println("Energy Matrix");
		for (int a = 0; a < rowN; a++) {
			for (int b = 0; b < columnN; b++) {
				System.out.print("\t" + intensity[a][b]);
			}
			System.out.println("\n");
		}
	}

	private void cumulativeenergy_verticalseam() {
		cum_intensity = new int[rowN][columnN];
		for (int i = 0; i < rowN; i++) {
			for (int j = 0; j < columnN; j++) {
				if (i == 0) {
					cum_intensity[i][j] = intensity[i][j];
				}
			}
		}
		for (int i = 1; i < rowN; i++) {
			for (int j = 0; j < columnN; j++) {
				if (i != 0 && j == 0) {
					cum_intensity[i][j] = intensity[i][j]
							+ Math.min(cum_intensity[i - 1][j],
									cum_intensity[i - 1][j + 1]);

				} else if ((i != 0) && (j == columnN - 1)) {
					cum_intensity[i][j] = intensity[i][j]
							+ Math.min(cum_intensity[i - 1][j],
									cum_intensity[i - 1][j - 1]);
				}

				else {

					cum_intensity[i][j] = intensity[i][j]
							+ Math.min(cum_intensity[i - 1][j - 1], Math.min(
									cum_intensity[i - 1][j],
									cum_intensity[i - 1][j + 1]));

				}
			}
		}
		System.out.println("Cumulative_Energy Matrix");
		for (int a = 0; a < rowN; a++) {
			for (int b = 0; b < columnN; b++) {
				System.out.print("\t" + cum_intensity[a][b]);
			}
			System.out.println("\n");
		}
	}

	public void backtracking_verticalseam() {
		int ti = 0, tj = 0, min = 99999999;
		int a = 0;
		int a1 = 0;
		int[] maxlist;
		int[] store = new int[rowN];
		int trackx, tracky;
		trackx = rowN - 1;
		for (tracky = 0; tracky < columnN - 1; tracky++) {
			min = Math.min(min, cum_intensity[trackx][tracky]);
		}
		int x1, y1, stop = 0;
		trackx = rowN - 1;
		for (tracky = 0; tracky < columnN - 1; tracky++) {
			if (min == Math.min(min, cum_intensity[trackx][tracky])) {
				x1 = trackx;
				y1 = tracky;
			}
			for (tracky = 0; tracky < columnN; tracky++) {
				if (intensity[trackx][tracky] == min) {
					if (stop < 0) {
						System.out.println("stop");
						break;
					}
					ti = trackx;
					tj = tracky;
					x[a1] = trackx;
					y[a1] = tracky;
					a1++;
					stop++;
				}
			}
			for (int i = 0; i < columnN; i++) {
				int list = 0;
				maxlist = new int[rowN];
				for (trackx = rowN - 1; trackx >= 0; trackx--) {
					for (tracky = 0; tracky < columnN - 1; tracky++) {
						min = Math.min(min, cum_intensity[trackx][tracky]);
					}
					System.out.println("the minimum value in each row" + min);
				}
				for (int i1 = 0; i1 < rowN; i1++) {
					System.out.println("the stored array list" + maxlist[i1]);
				}

				while (ti != 0) {
					if (ti != 0 && tj == 0) {
						store[a] = Math.min(intensity[ti - 1][tj],
								intensity[ti - 1][tj + 1]);
						{
							if (store[a] == intensity[ti - 1][tj]) {
								ti = ti - 1;
								tj = tj;
								x[a1] = ti - 1;
								y[a1] = tj;
								a1++;
							} else {
								ti = ti - 1;
								tj = tj + 1;
								x[a1] = ti - 1;
								y[a1] = tj + 1;
								a1++;
							}
							a++;
						}
						if (ti != 0 && tj != 0) {
							if (ti == rowN - 1 && tj == columnN - 1) {
								store[a] = Math.min(intensity[ti - 1][tj - 1],
										intensity[ti - 1][tj]);

								if (store[a] == intensity[ti - 1][tj - 1]) {
									ti = ti - 1;
									tj = tj - 1;
									x[a1] = ti - 1;
									y[a1] = tj - 1;
									a1++;
								} else {
									ti = ti - 1;
									tj = tj;
									x[a1] = ti - 1;
									y[a1] = tj;
									a1++;
								}
								a++;
							} else if (ti != 0 && ti != rowN - 1
									&& tj == columnN - 1) {
								store[a] = Math.min(Math.min(
										intensity[ti - 1][tj + 1],
										intensity[ti - 1][tj - 1]),
										intensity[ti - 1][tj]);
								{
									if (store[a] == intensity[ti - 1][tj + 1]) {
										ti = ti - 1;
										tj = tj + 1;
										x[a1] = ti - 1;
										y[a1] = tj + 1;
										a1++;
									} else if (store[a] == intensity[ti - 1][tj - 1]) {
										ti = ti - 1;
										tj = tj - 1;
										x[a1] = ti - 1;
										y[a1] = tj - 1;
										a1++;
									} else {
										ti = ti - 1;
										tj = tj;
										x[a1] = ti - 1;
										y[a1] = tj;
										a1++;
									}
									a++;
								}
								for (int i2 = 0; i2 < rowN; i2++) {
									System.out
											.println("the stored values are as follows"
													+ x[i2]);
								}
							}
						}
					}
				}
			}
		}
	}

	public static void main(String args[]) {
		seamcarving sc = new seamcarving();
		sc.readImage("C:\\java projects1\\seams_carving\\src\\twoBalls.pgm");
		sc.energymatrix();
		sc.cumulativeenergy_verticalseam();
		sc.backtracking_verticalseam();
	}
}