package run;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.Controller;
import model.Model;
import view.MainView;

public class Run {

	public static void main(String args[]) {
		MainView view = new MainView();
		Controller controller = new Controller();

		view.btn.addActionListener(new ActionListener() {
			/**
			 *
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				String typeSp;
				Model m = new Model();
				int loops = 0;
				int nk = 0;
				int index = -1;
				boolean run1 = false;
				boolean run2 = false;
				m.input = view.input.getText();
				m.input = m.input.replaceAll(" ", "").toLowerCase();
				view.res.setText("processing.....");
				m.key = view.key.getText();
				m.key = m.key.replaceAll(" ", "").toLowerCase();
				m.matrixKey = new String[m.rowsKey][m.columsKey];
				switch (controller.validInput(m.input)) {
					case 1:
						run1 = true;
						view.validInput.setText("input valid ❤️");
						view.validInput.setForeground(Color.GREEN);
						break;
					case -1:
						view.validInput.setText("invalid input length 💔");
						view.validInput.setForeground(Color.ORANGE);
						break;
					case -2:
						view.validInput.setText("invalid input character 💔");
						view.validInput.setForeground(Color.ORANGE);
						break;
					default:
						view.validInput.setText("sr have wrong 💔");
						view.validInput.setForeground(Color.ORANGE);
						break;
				}

				switch (controller.validKey(m.key)) {
					case 1:
						run2 = true;
						loops = 10;
						nk = 4;
						view.validKey.setText("key valid : 128 bits ❤️");
						view.validKey.setForeground(Color.GREEN);
						typeSp = "AES-128 bit";
						view.spLb.setText(view.typeLb.getText() + typeSp);
						break;
					case 2:
						run2 = true;
						loops = 12;
						nk = 6;
						typeSp = "AES-192 bit";
						view.spLb.setText(view.typeLb.getText() + typeSp);
						view.validKey.setText("key valid : 192 bits ❤️");
						view.validKey.setForeground(Color.GREEN);
						break;
					case 3:
						run2 = true;
						loops = 14;
						nk = 8;
						typeSp = "AES-256 bit";
						view.spLb.setText(view.typeLb.getText() + typeSp);
						view.validKey.setText("key valid : 256 bits ❤️");
						view.validKey.setForeground(Color.GREEN);
						break;
					case -1:
						view.validKey.setText("invalid key length 💔");
						view.validKey.setForeground(Color.ORANGE);
						break;
					case -2:
						view.validKey.setText("invalid key character 💔");
						view.validKey.setForeground(Color.ORANGE);
						break;
					default:
						view.validKey.setText("sr have wrong 💔");
						view.validKey.setForeground(Color.ORANGE);
						break;
				}

				if (run1 == true && run2 == true) {
					// expland key
					m.keyAddround += m.key;
					if (loops == 10) { // 128 bit
						for (int i = 0; i < loops; i++) {
							String[] arrWords = new String[nk];

							int count = 0;

							for (int j = 0; j < m.key.length(); j += 8) {
								arrWords[count] = m.key.substring(j, j + 8);
								count++;
							}

							String lastWord = arrWords[m.key.length() / 8 - 1];

							String rootedWord = controller.rootWord(lastWord);

							String subedWord = controller.subByteKey(rootedWord, m.S_BOX);

							String doneWord = controller.XORkey(subedWord, m.RCON_ARR[i]);

							m.key = "";
							for (int j = 0; j < nk; j++) {
								String newWord = controller.XORkey(doneWord, arrWords[j]);
								if (j > index && j < index + 5) {
									m.keyAddround += newWord;
								}
								m.key += newWord;
								doneWord = newWord;
							}

						}

					}

					if (loops == 12) { // 192 bit
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

					if (loops == 14) { // 256 bit
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

					System.out.println("expand key : " + m.keyAddround);
					System.out.println();

					// valid input and key
					if (Model.state) {
						// addroundkey
						controller.matrix(m.input, m.matrixText);
						controller.matrix(m.keyAddround.substring(0, 32), m.matrixKey);
						controller.addRoundKey(m.matrixText, m.matrixKey, m.addedText);
						//
						//
						for (int i = 1; i < loops; i++) {

							controller.subOrInvSubByte(m.rowsText, m.columsText, m.addedText, m.S_BOX, m.subBytedText);

							controller.shiftRow(m.subBytedText, m.shiftRowText);

							controller.matrix(m.keyAddround.substring(32 * i, 32 * i + 32), m.matrixKey);

							controller.mixCl(m.MIX_COLUMNS_MATRIX, m.shiftRowText, m.MixedText);

							controller.addRoundKey(m.MixedText, m.matrixKey, m.addedText);

						}
						// ngoai vong lap encode
						controller.matrix(m.keyAddround.substring(32 * loops, 32 * loops + 32), m.matrixKey);

						controller.subOrInvSubByte(m.rowsText, m.columsText, m.addedText, m.S_BOX, m.subBytedText);

						controller.shiftRow(m.subBytedText, m.shiftRowText);

						controller.addRoundKey(controller.convertIntToString(m.shiftRowText), m.matrixKey, m.addedText);

						System.out.println("result : ");
						for (int k = 0; k < 4; k++) {
							for (int j = 0; j < 4; j++) {
								System.out.print(m.addedText[k][j] + "\t");
							}
							System.out.println();

						}
						System.out.println("---------------------------------------------");
						System.out.println();

						String result = "";
						for (int k = 0; k < 4; k++) {
							for (int j = 0; j < 4; j++) {
								result += m.addedText[j][k];
							}
						}

						view.res.setText(result);
						//
					} else {
						// //decode
						controller.matrix(m.input, m.matrixText);
						controller.matrix(m.keyAddround.substring(m.keyAddround.length() - 32, m.keyAddround.length()),
								m.matrixKey);
						controller.addRoundKey(m.matrixText, m.matrixKey, m.addedText);

						for (int i = loops; i >= 2; i--) {

							controller.invShiftRows(m.addedText, m.invShiftText); // chuan

							controller.subOrInvSubByte(m.invShiftText, m.INV_S_BOX, m.inSubBytedText); // chuan

							controller.matrix(m.keyAddround.substring(32 * i - 32, 32 * i), m.matrixKey);

							controller.addRoundKey(m.inSubBytedText, m.matrixKey, m.addedText);

							controller.mixCl(m.INV_MIX_COLUMNS_MATRIX, controller.convertStringToInt(m.addedText),
									m.addedText);

						}
						controller.invShiftRows(m.addedText, m.invShiftText);
						controller.subOrInvSubByte(m.invShiftText, m.INV_S_BOX, m.inSubBytedText);
						controller.matrix(m.keyAddround.substring(0, 32), m.matrixKey);
						controller.addRoundKey(m.inSubBytedText, m.matrixKey, m.addedText);

						System.out.println("result : ");
						for (int k = 0; k < 4; k++) {
							for (int j = 0; j < 4; j++) {
								System.out.print(m.addedText[k][j] + "\t");
							}
							System.out.println();

						}
						System.out.println("---------------------------------------------");
						System.out.println();

						String result = "";
						for (int k = 0; k < 4; k++) {
							for (int j = 0; j < 4; j++) {
								result += m.addedText[j][k];
							}
						}

						view.res.setText(result);
					}

				} else {
					view.res.setText("input and key must be valid!!");
				}

			}
		});

	}
}