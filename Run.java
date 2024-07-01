package run;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import controller.Controller;
import model.Model;
import view.MainView;

public class Run {

	public void setPropertyEncode(Model m, MainView view, Controller controller) {
		m.input = view.plainTextArea.getText();
		m.key = view.keyField1.getText();
		if (m.input.isEmpty()) {
			JOptionPane.showMessageDialog(view, "bản rõ không được bỏ trống");
			return;
		}
		if (m.key.isEmpty()) {
			JOptionPane.showMessageDialog(view, "key không được bỏ trống");
			return;
		}
		m.key = controller.hashString(m.key, view.hashComboBox1.getSelectedItem().toString());
		m.lastKey = controller.hashString(m.key, view.hashComboBox1.getSelectedItem().toString());
		m.matrixKey = new String[m.rowsKey][m.columsKey];
		m.plainText = m.input;
		m.input = controller.textToHex(m.input);
		m.lastPlainText = m.input;
	}

	public void setPropertyDecode(Model m, MainView view, Controller controller) {
		m.input = view.encryptedDataTextArea.getText();
		m.key = view.keyField2.getText();
		if (m.input.isEmpty()) {
			JOptionPane.showMessageDialog(view, "bản mã không được bỏ trống");
			return;
		}
		if (m.key.isEmpty()) {
			JOptionPane.showMessageDialog(view, "key không được bỏ trống");
			return;
		}
		m.key = controller.hashString(m.key, view.hashComboBox2.getSelectedItem().toString());
		m.matrixKey = new String[m.rowsKey][m.columsKey];

	}

	public void explandKey(Model m, Controller controller, String typeHash) {
		m.keyAddround += m.key;
		switch (typeHash) {
			case "128 bit":
				Model.smallLoops = 10;
				break;
			case "192 bit":
				Model.smallLoops = 12;
				break;
			case "256 bit":
				Model.smallLoops = 14;
				break;
			default:
				break;
		}
		if (Model.smallLoops == 10) { // 128 bit
			for (int i = 0; i < Model.smallLoops; i++) {
				String[] arrWords = new String[4];

				int count = 0;

				for (int j = 0; j < m.key.length(); j += 8) {
					arrWords[count] = m.key.substring(j, j + 8);
					count++;
				}

				String lastWord = arrWords[m.key.length() / 8 - 1];

				String rootedWord = controller.rootWord(lastWord);

				String subedWord = controller.subByteKey(rootedWord, m.S_BOX);

				String doneWord = controller.XORkey(subedWord, m.RCON_ARR[i]);

				for (int j = 0; j < 4; j++) {
					String newWord = controller.XORkey(doneWord, arrWords[j]);
					if (j > -1 && j < 4) {
						m.keyAddround += newWord;
					}
					doneWord = newWord;
				}

			}

		}

		if (Model.smallLoops == 12) { // 192 bit
			String[] arrWords = new String[52];
			int count = 0;
			for (int j = 0; j < m.key.length(); j += 8) {
				arrWords[count] = m.key.substring(j, j + 8);
				count++;
			}
			for (int i = 6; i < 52; i++) {
				if (i % 6 == 0) {
					String rootedWord = controller.rootWord(arrWords[i - 1]);
					String subedWord = controller.subByteKey(rootedWord, m.S_BOX);
					String doneWord = controller.XORkey(subedWord, m.RCON_ARR[(i / 6) - 1]);
					arrWords[i] = controller.XORkey(doneWord, arrWords[i - 6]);
				} else {
					String doneWord = arrWords[i - 1];
					arrWords[i] = controller.XORkey(doneWord, arrWords[i - 6]);
				}
				m.keyAddround += arrWords[i];
			}
		}

		if (Model.smallLoops == 14) { // 256 bit
			String[] arrWords = new String[60];
			int count = 0;
			for (int j = 0; j < m.key.length(); j += 8) {
				arrWords[count] = m.key.substring(j, j + 8);
				count++;
			}
			for (int i = 8; i < 60; i++) {
				if (i % 8 == 0) {
					String rootedWord = controller.rootWord(arrWords[i - 1]);
					String subedWord = controller.subByteKey(rootedWord, m.S_BOX);
					String doneWord = controller.XORkey(subedWord, m.RCON_ARR[(i / 8) - 1]);
					arrWords[i] = controller.XORkey(doneWord, arrWords[i - 8]);
				} else {
					if (i % 8 == 4) {
						String doneWord = controller.subByteKey(arrWords[i - 1], m.S_BOX);
						arrWords[i] = controller.XORkey(doneWord, arrWords[i - 8]);
					} else {
						String doneWord = arrWords[i - 1];
						arrWords[i] = controller.XORkey(doneWord, arrWords[i - 8]);
					}
				}
				m.keyAddround += arrWords[i];
			}
		}

	}

	public static void main(String args[]) {
		MainView view = new MainView();
		Controller controller = new Controller();
		Model m = new Model();
		Run r = new Run();
		view.encryptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					m.endText = "";
					m.key = "";
					m.keyAddround = "";
					m.input = "";

					r.setPropertyEncode(m, view, controller);
					r.explandKey(m, controller, view.hashComboBox1.getSelectedItem().toString());
					JOptionPane.showMessageDialog(view, "đang tiến hành mã hóa vui lòng đợi");
					for (int z = 0; z < Model.loops; z++) {
						m.input = Model.inputHex.substring(32 * z, 32 + (32 * z));
						controller.matrix(m.input, m.matrixText);
						controller.matrix(m.keyAddround.substring(0, 32), m.matrixKey);
						controller.addRoundKey(m.matrixText, m.matrixKey, m.addedText);
						//
						//
						for (int i = 1; i < Model.smallLoops; i++) {

							controller.subOrInvSubByte(m.rowsText, m.columsText, m.addedText, m.S_BOX,
									m.subBytedText);

							controller.shiftRow(m.subBytedText, m.shiftRowText);

							controller.matrix(m.keyAddround.substring(32 * i, 32 * i + 32), m.matrixKey);

							controller.mixCl(m.MIX_COLUMNS_MATRIX, m.shiftRowText, m.MixedText);

							controller.addRoundKey(m.MixedText, m.matrixKey, m.addedText);

						}
						// ngoai vong lap encode
						controller.matrix(m.keyAddround.substring(32 * Model.smallLoops, 32 * Model.smallLoops + 32),
								m.matrixKey);

						controller.subOrInvSubByte(m.rowsText, m.columsText, m.addedText, m.S_BOX, m.subBytedText);

						controller.shiftRow(m.subBytedText, m.shiftRowText);

						controller.addRoundKey(controller.convertIntToString(m.shiftRowText), m.matrixKey,
								m.addedText);

						for (int k = 0; k < 4; k++) {
							for (int j = 0; j < 4; j++) {
								m.endText += m.addedText[j][k];
							}
						}
					}
					JOptionPane.showMessageDialog(view, "mã hóa thành công");

					view.encryptedTextArea.setText(controller.textToBase64(controller.hexToText(m.endText)));

				} catch (Exception er) {
					System.out.println("");
				}
			}

		});

		view.decryptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				m.endText = "";
				m.key = "";
				m.keyAddround = "";
				m.input = "";

				r.setPropertyDecode(m, view, controller);
				try {
					m.input = controller.base64ToText(m.input);
				} catch (Exception err) {
					JOptionPane.showMessageDialog(view, "đầu vào bản mã phải đúng định dạng base64");
					return;
				}
				m.input = controller.textToHex(m.input);
				r.explandKey(m, controller, view.hashComboBox2.getSelectedItem().toString());
				JOptionPane.showMessageDialog(view, "đang tiến hành giải mã vui lòng đợi");
				for (int z = 0; z < Model.loops; z++) {
					m.input = Model.inputHex.substring(32 * z, 32 + (32 * z));
					controller.matrix(m.input, m.matrixText);
					controller.matrix(
							m.keyAddround.substring(m.keyAddround.length() - 32, m.keyAddround.length()),
							m.matrixKey);
					controller.addRoundKey(m.matrixText, m.matrixKey, m.addedText);

					for (int i = Model.smallLoops; i >= 2; i--) {

						controller.invShiftRows(m.addedText, m.invShiftText);

						controller.subOrInvSubByte(m.invShiftText, m.INV_S_BOX, m.inSubBytedText);

						controller.matrix(m.keyAddround.substring(32 * i - 32, 32 * i), m.matrixKey);

						controller.addRoundKey(m.inSubBytedText, m.matrixKey, m.addedText);

						controller.mixCl(m.INV_MIX_COLUMNS_MATRIX,
								controller.convertStringToInt(m.addedText),
								m.addedText);

					}
					controller.invShiftRows(m.addedText, m.invShiftText);
					controller.subOrInvSubByte(m.invShiftText, m.INV_S_BOX, m.inSubBytedText);
					controller.matrix(m.keyAddround.substring(0, 32), m.matrixKey);
					controller.addRoundKey(m.inSubBytedText, m.matrixKey, m.addedText);
					for (int k = 0; k < 4; k++) {
						for (int j = 0; j < 4; j++) {
							m.endText += m.addedText[j][k];
						}
					}
				}
				String r = "";
				System.out.println("endText : " + m.endText);
				if (Integer.parseInt(m.endText.substring(m.endText.length() - 2,
						m.endText.length()), 16) >= 16) {

					r = controller.hexToText(m.endText);
				} else {
					r = controller.hexToText(m.endText.substring(0, m.endText.length()
							- Integer.parseInt(m.endText.substring(m.endText.length() - 2,
									m.endText.length()),
									16)
									* 2));
				}
				JOptionPane.showMessageDialog(view, "giải mã xong");

				view.decryptedDataTextArea.setText(r);

				if (m.endText.toLowerCase().equals(m.lastPlainText.toLowerCase())) {
					view.decryptedDataTextArea.setText(m.plainText);
					JOptionPane.showMessageDialog(view, "không bị chỉnh sửa");
					return;
				} else {
					try {
						if (m.endText.toLowerCase().substring(0, m.endText.length() - 2)
								.equals(m.lastPlainText.toLowerCase().substring(0, m.lastPlainText.length() - 3))) {
							view.decryptedDataTextArea.setText(m.plainText);
							JOptionPane.showMessageDialog(view, "không bị chỉnh sửa");
						} else {
							JOptionPane.showMessageDialog(view, "đã bị chỉnh sửa");
						}
					} catch (Exception er) {
						System.out.println("");
					}
				}
			}

		});

		view.fileButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(view.fileButton1);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					Model.filePathToSig = selectedFile.toPath().toString();
					view.plainTextArea.setText(controller.readFileByPath(Model.filePathToSig));
				}
			}

		});

		view.fileButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(view.fileButton2);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					Model.filePathToSig = selectedFile.toPath().toString();
					view.encryptedDataTextArea.setText(controller.readFileByPath(Model.filePathToSig));
				}
			}

		});

		view.saveButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.saveToFile(view.encryptedTextArea.getText(), view);
			}

		});

		view.saveButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.saveToFile(view.decryptedDataTextArea.getText(), view);
			}

		});

		view.transferButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				view.keyField2.setText(view.keyField1.getText());
				view.encryptedDataTextArea.setText(view.encryptedTextArea.getText());
			}

		});

	}

}