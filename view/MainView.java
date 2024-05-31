package view;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import model.Model;

public class MainView extends JFrame {
	public JRadioButton checkBox0 = new JRadioButton("encode 🔒", true);
	public JRadioButton checkBox1 = new JRadioButton("decode 🔑", false);
	public ButtonGroup btnGr = new ButtonGroup();
	public JLabel typeLb = new JLabel("Encode 🔒");
	public JTextField input = new JTextField();
	public JTextField res = new JTextField();
	public JLabel inputLb = new JLabel("input 📖");
	public JLabel resLb = new JLabel("result ⚙️");
	public JLabel validInput = new JLabel("valid input: ");
	public JLabel validKey = new JLabel("valid key: ");
	public JLabel keyLb = new JLabel("key 🗝️");
	public JLabel spLb = new JLabel("support all AES-128 , AES-192 , AES-256 : ");
	public JTextField key = new JTextField();
	public JButton btn = new JButton("Submit");

	public Image createImg(String fileName) {
		URL url = MainView.class.getResource(fileName);
		return Toolkit.getDefaultToolkit().createImage(url);
	}

	public ImageIcon createImgIcon(String fileName) {
		URL url = MainView.class.getResource(fileName);
		return new ImageIcon(url);
	}

	public MainView() {
		super("Encode and decode 🔑 AES");
		this.setContentPane(new JLabel(this.createImgIcon("hacker4.jpg")));

		this.checkBox0.setBounds(20, 20, 100, 30);
		this.checkBox0.setBackground(Color.YELLOW);
		this.checkBox0.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Model.state = true;
					typeLb.setText("Encode 🔒");
					typeLb.setForeground(Color.RED);
					typeLb.setBackground(Color.GREEN);
				}

			}
		});
		this.checkBox1.setBounds(120, 20, 100, 30);
		this.checkBox1.setBackground(Color.YELLOW);
		this.checkBox1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Model.state = false;
					typeLb.setText("decode 🔑 ");
					typeLb.setForeground(Color.YELLOW);
					typeLb.setBackground(Color.RED);
				}
			}
		});

		this.btnGr.add(checkBox0);
		this.btnGr.add(checkBox1);

		this.typeLb.setFont(new Font("Serif", Font.PLAIN, 40));
		this.typeLb.setBounds(930, -50, 1000, 150);
		this.typeLb.setForeground(Color.RED);
		this.typeLb.setBackground(Color.GREEN);

		this.input.setBounds(20, 100, 1000, 100);
		this.res.setBounds(20, 550, 1000, 100);

		this.inputLb.setFont(new Font("Serif", Font.PLAIN, 30));
		this.inputLb.setBounds(1050, 80, 100, 100);
		this.inputLb.setForeground(Color.YELLOW);

		this.resLb.setBounds(1050, 550, 200, 100);
		this.resLb.setFont(new Font("Serif", Font.PLAIN, 30));
		this.resLb.setForeground(Color.YELLOW);

		this.validInput.setFont(new Font("Serif", Font.PLAIN, 30));
		this.validInput.setBounds(1050, 130, 500, 100);
		this.validInput.setForeground(Color.YELLOW);

		this.validKey.setFont(new Font("Serif", Font.PLAIN, 30));
		this.validKey.setBounds(1050, 330, 500, 100);
		this.validKey.setForeground(Color.YELLOW);

		this.keyLb.setFont(new Font("Serif", Font.PLAIN, 30));
		this.keyLb.setBounds(1050, 280, 100, 100);
		this.keyLb.setForeground(Color.YELLOW);

		this.spLb.setFont(new Font("Serif", Font.PLAIN, 30));
		this.spLb.setBounds(370, -50, 1000, 150);
		this.spLb.setForeground(Color.YELLOW);

		this.key.setBounds(20, 300, 1000, 100);

		this.btn.setBounds(20, 700, 100, 40);
		this.btn.setBackground(Color.ORANGE);

		this.setIconImage(this.createImg("iconLock.png"));

		this.setLayout(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1350, 830);
		this.getContentPane().setBackground(Color.PINK);
		this.setLocationRelativeTo(null);

		this.add(this.checkBox0);
		this.add(this.checkBox1);
		this.add(this.typeLb);
		this.add(this.input);
		this.add(this.res);
		this.add(this.inputLb);
		this.add(this.resLb);
		this.add(this.validInput);
		this.add(this.validKey);
		this.add(this.keyLb);
		this.add(this.spLb);
		this.add(this.key);
		this.add(this.btn);

	}

}
