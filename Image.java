import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class Image {
	//UNICODE DOESN'T WORK ON BASH '\u2591' == Light Shade '\u2592' == Medium Shade '\u2593' == Dark Shade '\u2588' == Solid
	private static final int blackPixel = 1;
	private static final int whitePixel = 0;

	public static void main(String[] args) throws java.io.IOException {
		Scanner input = new Scanner(new File("images.txt"));
		LinkedList<String> results = new LinkedList<String>();
		boolean visualize = false, animate = false;
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("--f")){
				try{
					input = new Scanner(new File(args[i+1]));
					i++;
				} catch(Exception e){
					i = i;
				}
			} else if(args[i].equals("--v")){
				visualize = true;
			} else if(args[i].equals("--a")){
				animate = true;
			}
		}
		animate = visualize && animate;

		int tCases = input.nextInt();
		int rows = 0, columns = 0, rows2 = 0, columns2 = 0;
		int hold = 0, dloh = 0, flips = 0;
		int nob = 0, nob2 = 0;
		String str = "";
		int[][] locations = null, locations2 = null;
		int[][] image1, image2;
		int similarity = 0, bestSimilarity = 0;
		char[][] plane = null;

		for(int cases = 0; cases < tCases; cases++){
			bestSimilarity = 0; similarity = 0;
			String s = "";

			rows = input.nextInt(); columns = input.nextInt();
			String[] arg = new String[rows];
			for(int i = 0; i < arg.length; i++)
				arg[i] = input.next();

			rows2 = input.nextInt(); columns2 = input.nextInt();
			String[] arg2 = new String[rows2];
			for(int i = 0; i < arg2.length; i++)
				arg2[i] = input.next();

			image1 = new int[((rows*columns)<=(rows2*columns2))?rows:rows2][((rows*columns)<=(rows2*columns2))?columns:columns2];
			image2 = new int[(image1.length*image1[0].length==rows*columns?rows2:rows)][(image1.length*image1[0].length==rows*columns?columns2:columns)];
			locations = initImage(image1, image1.length*image1[0].length == rows*columns ? arg : arg2, locations);
			locations2 = initImage(image2, image1.length*image1[0].length == rows*columns ? arg2 : arg, locations2);
			nob = locations.length;
			if(visualize){
				hold = (image1.length>image1[0].length?image1.length:image1[0].length);
				plane = new char[(hold*2+image2.length)-2][(hold*2+image2[0].length)-2];
				//populate viaulization array
				for(int i = 0; i < plane.length; i++)
					for(int j = 0; j < plane[0].length; j++)
						plane[i][j] = ' ';
				/*/print
				for(char[] i : plane)
					System.out.println(Arrays.toString(i));//*/
				//center image2
				System.out.println();
				for(int i = hold-1, y = 0; y < image2.length; i++, y++)
					for(int j = hold-1, x = 0; x < image2[0].length; j++, x++)
						plane[i][j] = image2[y][x] == blackPixel ? '\u2593' : '\u2591' ;//'H' : 'O';
				/*/print
				for(int i = 0; i < plane.length; i++){
					for(int j = 0; j < plane[0].length; j++)
						System.out.printf("%c", plane[i][j]);
					System.out.println();
				}//*/

			}

			int[][][] imageOrientation1 = new int[4][image1.length][image1[0].length];
			int[][][] locationOrientations = new int[8][nob][2];
			int[][][] imageOrientation2 = new int[4][image1[0].length][image1.length];

			
			for(int i = 0, j = 0; i < 4; i++, j++){
				imageOrientation1[i] = image1;
				locationOrientations[j] = optimizedLocations(image1, nob);
				image1 = rotateClock(image1, locations);
			}
			image1 = flipHoriz(image1, locations);
			for(int i = 0, j = 4; i < 4; i++, j++){
				imageOrientation2[i] = image1;
				locationOrientations[j] = optimizedLocations(image1, nob);
				image1 = rotateClock(image1, locations);
			}
			long startTime = 0L, stopTime = 0L, overhead = 0L;
			if(!visualize){
				startTime = System.nanoTime();
				stopTime = System.nanoTime();
				overhead = stopTime - startTime;
				startTime = System.nanoTime();
			}

			image1 = imageOrientation1[0];
			locations = locationOrientations[0];	
			for(int y = -1*(imageOrientation1[0].length-1); y < (image2.length); y++){
				for(int x = -1*(imageOrientation1[0][0].length-1); x < (image2[0].length); x++){
					//System.out.printf("%d, %d%n", x, y);
					for(int j = 0, i = 0; j < 8; j++, i++){
						locations = locationOrientations[j];
						if(j < 4)
							image1 = imageOrientation1[i];
						else
							image1 = imageOrientation2[i-4];
						similarity = 0;
						for(int iter = 0; iter < locations2.length; iter++){
							for(int reti = 0; reti < locations.length; reti++){
								try{
									similarity += ((locations[reti][0]+y)==locations2[iter][0]&&(locations[reti][1]+x)==locations2[iter][1])?1:0;
								} catch(IndexOutOfBoundsException e){
									similarity+=0;
								}
							}//iterate through image1 black pixel locations
						}//iterate through image2 black pixel locations
						bestSimilarity = (similarity > bestSimilarity) ? similarity : bestSimilarity;
						if(visualize){
							System.out.printf("\n%d HIT(S):", similarity);				
							vizualization(plane, image1, image2, hold, y, x);							
							//*/"Animation"
							if(animate){
								try{
									TimeUnit.MICROSECONDS.sleep(20000);//12000
								} catch(Exception e){
									System.err.printf("Exception %s%n",e);
								}
							}
							//"Animation"*/
						}
					}//Image State Loop
				}//Translation y coordinate loop
			}//Translation x coordinate loop
			if(!visualize){
				stopTime = System.nanoTime();
				startTime = stopTime - startTime - overhead;	
				//System.out.printf("\nTime(ns): %d\n", startTime);
			}

			results.add(String.format("Test Case %d:\n\tSimilarity Hit: %d out of %d\n\tImage1 is %.2f%c similar to Image2\n", (1+cases), bestSimilarity, locations2.length, (bestSimilarity/(float)locations2.length*100)*((image1.length*image1[0].length)/(float)(image2.length*image2[0].length)),'%'));

			if(!visualize)
				results.add(String.format("\tTime(ns): %d\n\n",startTime));
						

			//print(image1, locations); print(image2, locations2);

		}//tCases loop
		if(visualize)
			results.add(0,"\nTime(ns): IO performed during execution. Timing is deactivaed.\n\n");

		while(results.size() != 0)
			System.out.print(results.remove(0));
	}//Main

	private static void vizualization(char[][] plane, int[][] image1, int[][] image2, int hold, int y, int x){
		System.out.println();
		for(int i = 0; i < plane.length; i++)
			for(int j = 0; j < plane[0].length; j++)
				plane[i][j] = ' ';
		for(int i = hold-1, z = 0; z < image2.length; i++, z++)
			for(int j = hold-1, v = 0; v < image2[0].length; j++, v++)
				plane[i][j] = image2[z][v] == blackPixel ? '\u2593' : '\u2591';//'H' : 'O';
		for(int z = y+(hold-1), g = 0; g < image1.length; z++, g++)
			for(int v = x+(hold-1), h = 0; h < image1[0].length; v++, h++)
				plane[z][v] = image1[g][h]==blackPixel?(image1[g][h]==blackPixel&&plane[z][v]=='\u2593')? 'X':'\u2593' : plane[z][v]==' ' ? '\u2591' : '\u2592' ;//'%':'E':image1[g][h]==blackPixel?'E':'U';
		for(int i = 0; i < plane.length; i++){
			for(int j = 0; j < plane[0].length; j++)
				System.out.printf("%c", plane[i][j]);
			System.out.println();
		}

	}	//Visualization


	private static int[][] initImage(int[][] image, String[] arg, int[][] locations){
		int nob = 0;
		for(int i = 0; i < arg.length; i++){
			for(int j = 0; j < arg[i].length(); j++){
				image[i][j] = (((int)arg[i].charAt(j)) == 35) ? blackPixel : whitePixel;
				nob += (image[i][j] == blackPixel) ? blackPixel : whitePixel;
			}
		}
		locations = new int[nob][2];
		updateLocations(locations, image);
		return locations;
	}//Initialization of Images

	private static void print(int[][] image, int[][] locations){
		System.out.println("Locations:");
		System.out.println(Arrays.deepToString(locations));
		System.out.println("Array State:");
		for(int i = 0; i < image.length; i++)
			System.out.println(Arrays.toString(image[i]));
		System.out.println();
	}//print


	private static int[][] rotateClock(int[][] image, int[][] locations){
		int[][] copy = new int[image[0].length][image.length];
		for(int i = 0, c = copy[0].length-1; i < image.length; i++, c--)
			for(int j = 0, r = 0; j < image[0].length; j++, r++)
				copy[r][c] = image[i][j];
		updateLocations(locations, copy);
		return copy;
	}//rotateClock
	
	private static int[][] rotateCounter(int[][] image, int[][] locations){
		int[][] copy = new int[image[0].length][image.length];
		for(int i = 0, r = copy.length-1; i < image[0].length; i++, r--)
			for(int j = 0, c = 0; j < image.length; j++, c++)
				copy[r][c] = image[j][i];
		updateLocations(locations, copy);
		return copy;
	}//rotateCounter

	private static int[][] flipHoriz(int[][] image, int[][] locations){
		int[][] copy = new int[image.length][image[0].length];
		for(int i = 0; i < image.length; i++)
			for(int j = 0, c = image[0].length-1; j < image[0].length; j++, c--)
				copy[i][c] = image [i][j];
		updateLocations(locations, copy);
		return copy;
	}//flipHorizontal


	//This will re-evaluate the image and update the locations of the black pixels
	private static void updateLocations(int[][] locations, int[][] image){
		for(int i = 0; i < locations.length; i ++)
			for(int j = 0; j < 2; j++)
				locations[i][j] = whitePixel;
		for(int j = 0, iter = 0; j < image.length; j++){
			for(int k = 0; k < image[0].length; k++){
				if(image[j][k] == blackPixel){
					locations[iter][0] = j;
					locations[iter][1] = k;
					iter++;
				}
			} 
		}
	}//updateLocations

	private static int[][] optimizedLocations(int[][] image, int nob){
		int[][] locations = new int[nob][2];
		for(int i = 0; i < locations.length; i ++)
			for(int j = 0; j < 2; j++)
				locations[i][j] = whitePixel;
		for(int j = 0, iter = 0; j < image.length; j++){
			for(int k = 0; k < image[0].length; k++){
				if(image[j][k] == blackPixel){
					locations[iter][0] = j;
					locations[iter][1] = k;
					iter++;
				}
			} 
		}
		return locations;
	}//updateLocations



/*
 Left over testing code:
 	rotateClock test after copy:
		System.out.printf("Image length (rows): %d\n", image.length);
		System.out.printf("Image[x] length (columns) %d\n\n", image[0].length);
		print(image, image.length, image[0].length, locations);
		System.out.printf("Copy length (rows): %d\n", copy.length);
		System.out.printf("Copy[x] length (columns) %d\n\n", copy[0].length);
		//print(copy, copy.length, copy[0].length, locations); print(image, rows, columns, locations);



 */

}
