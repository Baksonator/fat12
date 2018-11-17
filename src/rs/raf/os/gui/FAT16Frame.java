package rs.raf.os.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import rs.raf.os.disk.Disk;
import rs.raf.os.fat.FAT16;
import rs.raf.os.test.MockDirectory;

@SuppressWarnings("serial")
public class FAT16Frame extends JFrame {

	public FAT16Frame(FAT16 fat, MockDirectory dir, Disk disk) {
		JPanel gornji = new JPanel();
		BorderLayout bl = new BorderLayout();
		gornji.setLayout(bl);
		JPanel gornjiP = new JPanel();
		gornji.add(gornjiP, BorderLayout.NORTH);
		JButton dugmence = new JButton("Oznaci");
		JTextField tf = new JTextField();
		gornji.add(tf, BorderLayout.CENTER);
		gornji.add(dugmence, BorderLayout.SOUTH);
		
		dugmence.addActionListener(new AkcijaOznaci(tf, gornjiP, dir, fat));
		
		JPanel donji = new JPanel();
		BorderLayout bl1 = new BorderLayout();
		donji.setLayout(bl1);
		JPanel donjiP = new JPanel();
		donji.add(donjiP, BorderLayout.NORTH);
		JTextArea ta = new JTextArea("Sector contents:");
		donji.add(ta, BorderLayout.CENTER);
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setSize(3 * screenWidth / 4, 3 * screenHeight / 4);
		
		for(int i = 2; i < fat.getClusterCount() + 2; i++) {
			JButton dugme = new JButton("" + fat.readCluster(i));
			gornjiP.add(dugme);
		}
		
		for(int i = 0; i < disk.getSectorCount(); i++) {
			JButton dugme = new JButton("" + i);
			byte[] readSector = disk.readSector(i);
			boolean flag = false;
			for(int j = 0; j < readSector.length; j++) {
				if(readSector[j] != 0) {
					dugme.setBackground(Color.BLACK);
					dugme.addActionListener(new AkcijaProcitaj(disk, dugme, ta));
					flag = true;
					break;
				}
			}
			if(!flag) {
				dugme.setBackground(Color.white);
			}
			donjiP.add(dugme);
		}
		
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gornji, donji);
		sp.setDividerLocation(3 * screenHeight / 8);
		add(sp);
	}
}
