import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
public class PanelSwitch {
	JFrame frame;
	JPanel panel1;
	JPanel panel2;
	public static void main(String[] args) {
		PanelSwitch p = new PanelSwitch();
		p.run();
	}
	public void run() {
		frame = new JFrame("Switching Panels");
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        panel1 = new JPanel(); //new BorderLayout
		panel2 = new JPanel(); //new BorderLayout
		Box buttonBox1 = new Box(BoxLayout.Y_AXIS);
		Box buttonBox2 = new Box(BoxLayout.Y_AXIS);
		
		JButton pushButton = new JButton("push me");
        pushButton.addActionListener(new MyPushListener());
        buttonBox1.add(pushButton);
		
		JButton otherButton = new JButton("and then just...");
        otherButton.addActionListener(new MyOtherListener());
        buttonBox2.add(otherButton);
		
        panel1.add(BorderLayout.WEST, buttonBox1);
        panel2.add(BorderLayout.WEST, buttonBox2);
        frame.getContentPane().add(BorderLayout.WEST, panel1);
		frame.pack();
		frame.setVisible(true);
	}
	
	public class MyPushListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			frame.getContentPane().remove(panel1);
			frame.getContentPane().add(BorderLayout.WEST, panel2);
			frame.pack();
			frame.repaint();
		}
	}
	public class MyOtherListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			frame.getContentPane().remove(panel2);
			frame.getContentPane().add(BorderLayout.WEST, panel1);
			frame.pack();
			frame.repaint();
		}
	}
}