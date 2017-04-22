package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class tryCloneFile {

	private static File file;
	private static FileInputStream fis;
	private static FileOutputStream fos;
	private static byte[] buffer;
	
	public static void main(String[] args) {
		
		System.out.println(new File(".").getAbsolutePath());
		try {
			file = new File("Screenshot_2.png");
			fis = new FileInputStream(file);
			fos = new FileOutputStream(new File("copy of file" + file.getName()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		buffer = new byte[1024];
		int length;
		try {
			while((length = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
				fos.flush();
			}
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
