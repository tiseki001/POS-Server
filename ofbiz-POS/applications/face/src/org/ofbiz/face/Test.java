package org.ofbiz.face;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Test {

	public static void main(String[] args) {
		System.out.println();
	}

	public static void fileWriter(String str) {
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try {
			File file = new File("D:\\write.txt");
			fos = new FileOutputStream(file);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(str);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fos != null)
					fos.close();
			} catch (IOException ie) {

			}
		}
	}

}
