package android;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class TestMain {

	JFrame frame = new JFrame("Log Frame");

	JButton startButton = new JButton("操作記録開始");

	JButton endButton = new JButton("操作記録完了");

	JButton createButton = new JButton("操作記録再生");

	JLabel label = new JLabel("eventの番号を入力してください。");

	JTextArea text = new JTextArea(1, 10);

	MonitorThread thread = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestMain tb = new TestMain();
		tb.show();
	}

	public TestMain() {
		
		frame.getContentPane().setLayout(
				new java.awt.FlowLayout(FlowLayout.LEFT, 25, 10));

		thread = new MonitorThread();

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if ("".equals(text.getText())) {
					JOptionPane.showMessageDialog(null, "eventの番号を入力してください。", "ERROR",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (thread.isAlive()) {
					JOptionPane.showMessageDialog(null, "操作記録中", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				} else {
					thread = new MonitorThread();
					thread.setTextValue(text.getText());
					thread.start();
				}
			}
		});

		endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (thread.isAlive()) {
					thread.closeCommand();
				}
			}
		});
		
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (thread.isAlive()) {
					JOptionPane.showMessageDialog(null, "操作記録中", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
				else {
					Endorphin e = new Endorphin("C:/kenshin/test.csv", "3");
					e.start();
				}
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				thread.closeCommand();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});

		frame.getContentPane().add(label);
		frame.getContentPane().add(text);
		frame.getContentPane().add(startButton);
		frame.getContentPane().add(endButton);
		frame.getContentPane().add(createButton);

		frame.setSize(250, 250);
		int w = (Toolkit.getDefaultToolkit().getScreenSize().width - 250) / 2;
		int h = (Toolkit.getDefaultToolkit().getScreenSize().height - 250) / 2;
		frame.setLocation(w, h);
	}

	public void show() {
		frame.setVisible(true);
	}
}
