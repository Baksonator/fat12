package rs.raf.os.gui;

import rs.raf.os.disk.Disk;
import rs.raf.os.disk.SimpleDisk;
import rs.raf.os.fat.FAT16;
import rs.raf.os.test.MockDirectory;
import rs.raf.os.test.MockFAT;

public class Main {

	public static void main(String[] args) {
		FAT16 fat = new MockFAT(2, 10);
		
		Disk disk = new SimpleDisk(100, 10);
		
		MockDirectory dir = new MockDirectory(fat, disk);
		
		byte[] data = new byte[150];
		for(int i = 0; i < 150; i++) {
			data[i] = (byte)(i*2);
		}
		
		byte[] data1 = new byte[300];
		for(int i = 0; i < 300; i++) {
			data1[i] = (byte)i;
		}
		
		byte[] data2 = new byte[230];
		for(int i = 0; i < 230; i++) {
			data2[i] = (byte)(i*2);
		}
		
		dir.writeFile("Even", data);
		dir.writeFile("Odd", data1);
		dir.deleteFile("Even");
		dir.writeFile("Third", data2);
		
		FAT16Frame frame = new FAT16Frame(fat, dir, disk);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
