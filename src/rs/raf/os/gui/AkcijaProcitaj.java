package rs.raf.os.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

import rs.raf.os.disk.Disk;

public class AkcijaProcitaj implements ActionListener {

	private Disk disk;
	private JButton dugme;
	private JTextArea ta;
	
	public AkcijaProcitaj(Disk disk, JButton dugme, JTextArea labela) {
		this.disk = disk;
		this.dugme = dugme;
		this.ta = labela;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		byte[] readSector = disk.readSector(Integer.parseInt(dugme.getText()));
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < readSector.length; i++) {
			sb.append(readSector[i] + " ");
			if(i != 0 && i % 25 == 0) {
				sb.append("\n");
			}
		}
		ta.setText("Sector contents: " + sb.toString());
	}

}
