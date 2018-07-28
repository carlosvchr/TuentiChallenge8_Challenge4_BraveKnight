package tc.braveknightchallenge.mainpk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class IOManager {

	/** Create a new reader to read the input */
	public static BufferedReader getReader(String path) {
		// Create the file reader
		FileReader fr;
		try {
			fr = new FileReader(new File(path));
		} catch (FileNotFoundException e1) {
			// Check if the file exists, if not, finish the program safely
			System.out.println("File does not exist.");
			e1.printStackTrace();
			return null;
		}	

		return new BufferedReader(fr);	
	}
	
	
	/** Create a writer to write the output */
	public static PrintWriter getWriter(String path) {
		// Create the file writer
		FileWriter fw;
		try {
			fw = new FileWriter(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return new PrintWriter(fw);
	}
	
	
	/** Close opened streams */
	public static void closeStreams(BufferedReader br, PrintWriter pw) {
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.close();
	}
	
}
