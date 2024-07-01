package view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
	public JTextArea plainTextArea = new JTextArea(5, 20); // đầu vào bản rõ
	public JButton fileButton1 = new JButton("File"); // mở file mã hóa
	public JTextField keyField1 = new JTextField(20); // text key mã hóa
	String[] hashAlgorithms = { "128 bit", "192 bit", "256 bit" };
	public JComboBox<String> hashComboBox1 = new JComboBox<>(hashAlgorithms); // selection kiểu băm key mã hóa
	public JButton encryptButton = new JButton("Mã hóa"); // nút mã hóa
	public JTextArea encryptedTextArea = new JTextArea(5, 20); // kết quả sau mã hóa
	public JButton saveButton1 = new JButton("lưu"); // lưu kết quả sau mã hóa
	public JButton transferButton = new JButton("chuyển tiếp"); // nút chuyển tiếp
	public JTextArea encryptedDataTextArea = new JTextArea(5, 20); // đầu vào giải mã
	public JButton fileButton2 = new JButton("File");// chọn file để giải mã
	public JTextField keyField2 = new JTextField(20); // khóa cho giải mã
	public JComboBox<String> hashComboBox2 = new JComboBox<>(hashAlgorithms); // selection kiểu băm key mã hóa
	public JButton decryptButton = new JButton("Giải mã"); // nút giải mã
	public JTextArea decryptedDataTextArea = new JTextArea(5, 20); // kết quả sau giải mã
	public JButton saveButton2 = new JButton("lưu"); // lưu file sau giải mã

	public MainView() {
		// Tạo frame chính
		super("Mã Hóa và giải mã AES");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 600);

		// Tạo panel chính với layout GridBagLayout
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// Tạo font lớn hơn
		Font font = new Font("Arial", Font.PLAIN, 16);

		// Panel mã hóa
		JPanel encryptPanel = new JPanel(new GridBagLayout());
		encryptPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Mã Hóa"));
		((javax.swing.border.TitledBorder) encryptPanel.getBorder()).setTitleColor(Color.RED);

		// Panel giải mã
		JPanel decryptPanel = new JPanel(new GridBagLayout());
		decryptPanel
				.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Giải mã"));
		((javax.swing.border.TitledBorder) decryptPanel.getBorder()).setTitleColor(Color.BLUE);

		// Các thành phần cho panel mã hóa
		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		JLabel plainTextLabel = new JLabel("bản rõ:");
		plainTextLabel.setFont(font);
		encryptPanel.add(plainTextLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		plainTextArea.setFont(font);
		encryptPanel.add(new JScrollPane(plainTextArea), c);

		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		fileButton1.setFont(font);
		encryptPanel.add(fileButton1, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		JLabel keyLabel1 = new JLabel("khóa:");
		keyLabel1.setFont(font);
		encryptPanel.add(keyLabel1, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		keyField1.setFont(font);
		encryptPanel.add(keyField1, c);

		c.gridx = 3;
		c.gridy = 1;

		encryptPanel.add(hashComboBox1, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		encryptButton.setFont(font);
		encryptPanel.add(encryptButton, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		JLabel encryptedResultLabel = new JLabel("kết quả:");
		encryptedResultLabel.setFont(font);
		encryptPanel.add(encryptedResultLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		encryptedTextArea.setFont(font);
		encryptPanel.add(new JScrollPane(encryptedTextArea), c);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		saveButton1.setFont(font);
		encryptPanel.add(saveButton1, c);

		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 2;
		transferButton.setFont(font);
		encryptPanel.add(transferButton, c);

		// Các thành phần cho panel giải mã
		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		JLabel encryptedTextLabel = new JLabel("bản mã:");
		encryptedTextLabel.setFont(font);
		decryptPanel.add(encryptedTextLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		encryptedDataTextArea.setFont(font);
		decryptPanel.add(new JScrollPane(encryptedDataTextArea), c);

		c.gridx = 3;
		c.gridy = 0;
		fileButton2.setFont(font);
		decryptPanel.add(fileButton2, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		JLabel keyLabel2 = new JLabel("khóa:");
		keyLabel2.setFont(font);
		decryptPanel.add(keyLabel2, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		keyField2.setFont(font);
		decryptPanel.add(keyField2, c);

		c.gridx = 3;
		c.gridy = 1;
		decryptPanel.add(hashComboBox2, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		decryptButton.setFont(font);
		decryptPanel.add(decryptButton, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		JLabel decryptedResultLabel = new JLabel("kết quả:");
		decryptedResultLabel.setFont(font);
		decryptPanel.add(decryptedResultLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		decryptedDataTextArea.setFont(font);
		decryptPanel.add(new JScrollPane(decryptedDataTextArea), c);

		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1;
		saveButton2.setFont(font);
		decryptPanel.add(saveButton2, c);

		// Thêm panel mã hóa và giải mã vào frame chính
		c.gridx = 0;
		c.gridy = 0;
		panel.add(encryptPanel, c);

		c.gridx = 1;
		c.gridy = 0;
		panel.add(decryptPanel, c);

		this.getContentPane().add(panel);
		this.setVisible(true);

	}

}
