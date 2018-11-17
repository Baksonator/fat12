package rs.raf.os.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rs.raf.os.fat.FAT16;
import rs.raf.os.test.MockDirectory;

public class AkcijaOznaci implements ActionListener {

	private JTextField tf;
	private JPanel panel;
	private MockDirectory dir;
	private FAT16 fat;
	
	public AkcijaOznaci(JTextField tf, JPanel panel, MockDirectory dir, FAT16 fat) {
		this.tf = tf;
		this.panel = panel;
		this.dir = dir;
		this.fat = fat;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = tf.getText();
		for(int i = 0; i < panel.getComponentCount(); i++) {
			JButton button = (JButton)panel.getComponent(i);
			button.setBackground(new JButton().getBackground());
		}
		for(int i = 0; i < dir.getFiles().length; i++) {
			if(dir.getFiles()[i] != null && dir.getFiles()[i].getName().equals(name)) {
				int first = dir.getFiles()[i].getFirstCluster();
				JButton button1 = (JButton)panel.getComponent(first - 2);
				button1.setBackground(Color.YELLOW);
				int next = fat.readCluster(first);
				while(next != 0xFFF8) {
					JButton button = (JButton)panel.getComponent(next - 2);
					button.setBackground(Color.YELLOW);
					int tmp = fat.readCluster(next);
					next = tmp;
				}
				break;
			}
		}
	}

}
