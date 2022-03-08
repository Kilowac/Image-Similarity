import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/*
 * Automate.java will auto-generate the 'image' files in their proper format for testing
 */

public class Automate {
	public static void main(String[] args) throws IOException {
		String file_path = "test2.txt";
		File f = new File(file_path);
		int iter = 2;
		do {
			f = new File(file_path);
			if(f.exists()){
				file_path = String.format("test%d.txt",++iter);
			} else {
				break;
			}
		} while(true);
		PrintWriter output = new PrintWriter(f);
		Random rand = new Random();
		int t = rand.nextInt(10)+5;
		int x = rand.nextInt(10)+1;
		int y = rand.nextInt(10)+1;
		String str = "";
		output.printf("%d\n", t);
		for(int a = 0; a < t; a++){
			x = rand.nextInt(10)+1;
			y = rand.nextInt(10)+1;
			output.printf("%d %d\n", y, x);
			for(int i = 0; i < y; i++){
				for(int j = 0; j < x; j++){
					if(rand.nextInt(2) == 0)
						str += ".";
					else
						str += "#";
				}
				//System.out.print(str + "\n");
				output.print(str + "\n");
				str = "";
			}
			x = rand.nextInt(10)+1;
			y = rand.nextInt(10)+1;
			output.printf("%d %d\n", y, x);
			for(int i = 0; i < y; i++){
				for(int j = 0; j < x; j++){
					if(rand.nextInt(2) == 0)
						str+=".";
					else
						str+="#";
				}
				output.print(str + "\n");
				str = "";	
			}
		}
		output.close();

	}
}
