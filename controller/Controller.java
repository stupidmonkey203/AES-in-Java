package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Model;
import view.MainView;

public class Controller {

	public String hashString(String input, String typeHash) {
		boolean bit192 = false;
		String res = "";
		if (typeHash == "128 bit") {
			typeHash = "MD5";
		}
		if (typeHash == "256 bit") {
			typeHash = "SHA-256";
		}
		if (typeHash == "192 bit") {
			typeHash = "SHA-256";
			bit192 = true;
		}
		try {
			MessageDigest digest = MessageDigest.getInstance(typeHash);
			byte[] hash = digest.digest(input.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			if (bit192) {
				for (int i = 0; i < 192 / 4; i++) {
					res += hexString.toString().charAt(i);
				}
				return res;
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return " ";
	}

	public String[][] matrix(String input, String[][] arr) {
		int rows = input.length() / 8;
		int count = 0;
		String[][] matrix = new String[rows][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < rows; j++) {
				if (count + 2 > input.length()) {
					break;
				}
				matrix[i][j] = input.substring(count, count + 2);
				arr[j][i] = input.substring(count, count + 2);
				count += 2;
			}
		}
		return matrix;
	}

	public int[][] subOrInvSubByte(int rows, int colums, String[][] matrix, int[][] S_BOX, int[][] arr) {
		int[][] res = new int[rows][colums];
		Model m = new Model();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < colums; j++) {
				String oneByte = matrix[i][j];
				res[i][j] = S_BOX[m.convertHexToInt(oneByte.charAt(0))][m.convertHexToInt(oneByte.charAt(1))];
				arr[i][j] = S_BOX[m.convertHexToInt(oneByte.charAt(0))][m.convertHexToInt(oneByte.charAt(1))];
			}
		}
		return res;
	}

	public int[][] subOrInvSubByte(String[][] matrix, String[][] S_BOX, String[][] arr) {
		int[][] res = new int[4][4];
		Model m = new Model();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				String oneByte = matrix[i][j];
				arr[i][j] = S_BOX[m.convertHexToInt(oneByte.charAt(0))][m.convertHexToInt(oneByte.charAt(1))];
			}
		}
		return res;
	}

	public void shiftRow(int[][] subByted, int[][] arr) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (j + i > 3) {
					arr[i][j] = subByted[i][j + i - 4];
				} else {
					arr[i][j] = subByted[i][j + i];
				}
			}
		}
	}

	public void invShiftRows(String[][] addedText, String[][] arr) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (j + i > 3) {
					arr[i][j + i - 4] = addedText[i][j];
				} else {
					arr[i][j + i] = addedText[i][j];
				}
			}
		}
	}

	public String intToBinary(int b) {
		Model m = new Model();
		if (b >= 16) {
			return m.HexToBinary(Integer.toHexString(b).charAt(0)) + m.HexToBinary(Integer.toHexString(b).charAt(1));
		} else {
			return "0000" + m.HexToBinary(Integer.toHexString(b).charAt(0));
		}
	}

	public int golois1(int b) {
		return b;
	}

	public int golois2(int b) {
		int temp;
		String binary = this.intToBinary(b);
		String binary2 = binary.substring(1) + "0";
		if (binary.charAt(0) == '1') {
			temp = this.bitToInt(this.XOR(binary2, "00011011"));
			return temp;
		} else {
			temp = this.bitToInt(binary2);
			return temp;
		}
	}

	public int golois3(int b) {
		String t;
		String binary = this.intToBinary(b);
		String binary2 = binary.substring(1) + "0";
		if (binary.charAt(0) == '1') {
			t = this.XOR(binary2, "00011011");
		} else {
			t = binary2;
		}
		return this.bitToInt(this.XOR(t, binary));
	}

	public int golois(int a, int b) { // a se nam trong matrixMix , b nam trong shifted
		switch (a) {
			case 0x01:
				return this.golois1(b);
			case 0x02:
				return this.golois2(b);
			case 0x03:
				return this.golois3(b);
			case 0x09:// = A.H02.H02.H02 + A.H01
				return this.bitToInt(this.XOR(this.golois2(this.golois2(this.golois2(b))), b));
			case 0x0B:// = A.H02.H02.H02 + A.H02 + A.H01
				return this.bitToInt(this
						.XOR(this.bitToInt(this.XOR(this.golois2(this.golois2(this.golois2(b))), this.golois2(b))), b));
			case 0x0D: // A.H02.H02.H02 + A.H02.H02 + A.H01
				return this.bitToInt(this.XOR(
						this.bitToInt(
								this.XOR(this.golois2(this.golois2(this.golois2(b))), this.golois2(this.golois2(b)))),
						b));
			case 0x0E: // A.H02.H02.H02 + A.H02.H02 + A.H02
				return this.bitToInt(this.XOR(
						this.bitToInt(
								this.XOR(this.golois2(this.golois2(this.golois2(b))), this.golois2(this.golois2(b)))),
						this.golois2(b)));
			default:
				return 0;
		}
	}

	public int bitToInt(String a) {
		Model m = new Model();
		return m.convertHexToInt(m.binaryToHex(a.substring(0, 4))) * 16
				+ m.convertHexToInt(m.binaryToHex(a.substring(4, 8)));
	}

	public String XOR(int a, int b) {
		String res = "";
		String binary1 = this.intToBinary(a);
		String binary2 = this.intToBinary(b);
		for (int i = 0; i < 8; i++) {
			if (binary1.charAt(i) == binary2.charAt(i)) {
				res += "0";
			} else {
				res += "1";
			}
		}
		return res;
	}

	public String XOR(String a, String b) {
		String res = "";
		for (int i = 0; i < 8; i++) {
			if (a.charAt(i) == b.charAt(i)) {
				res += "0";
			} else {
				res += "1";
			}
		}
		return res;
	}

	public void mixCl(int[][] MIX_MATRIX, int[][] shiftRowed, String[][] mixedMatrix) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int res = 0;
				for (int k = 0; k < 4; k++) {
					res = this.bitToInt(this.XOR(this.golois(MIX_MATRIX[i][k], shiftRowed[k][j]), res));
				}
				if (res > 15) {
					mixedMatrix[i][j] = Integer.toHexString(res);
				} else {
					mixedMatrix[i][j] = "0" + Integer.toHexString(res);
				}
			}
		}
	}

	public int[][] convertStringToInt(String arr[][]) {
		int[][] res = new int[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				res[i][j] = Integer.parseInt(arr[i][j], 16);
			}
		}
		return res;
	}

	public void addRoundKey(String[][] mixedMatrix, String[][] matrixKey, String[][] addedMatrix) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (this.bitToInt(this.XOR(Integer.parseInt(mixedMatrix[i][j], 16),
						Integer.parseInt(matrixKey[i][j], 16))) > 15) {
					addedMatrix[i][j] = Integer.toHexString(this.bitToInt(
							this.XOR(Integer.parseInt(mixedMatrix[i][j], 16), Integer.parseInt(matrixKey[i][j], 16))));
				} else {
					addedMatrix[i][j] = "0" + Integer.toHexString(this.bitToInt(
							this.XOR(Integer.parseInt(mixedMatrix[i][j], 16), Integer.parseInt(matrixKey[i][j], 16))));
				}
			}
		}
	}

	public String subByteKey(String s, int S_BOX[][]) {
		Model m = new Model();
		s = s.toLowerCase();
		String res = "";
		for (int i = 0; i < s.length(); i += 2) {
			String oneByte = s.substring(i, i + 2);
			if (Integer.toHexString(S_BOX[m.convertHexToInt(oneByte.charAt(0))][m.convertHexToInt(oneByte.charAt(1))])
					.length() == 0) {
				res += "00";
			} else {
				if (Integer
						.toHexString(S_BOX[m.convertHexToInt(oneByte.charAt(0))][m.convertHexToInt(oneByte.charAt(1))])
						.length() == 1) {
					res += "0";
					res += Integer.toHexString(
							S_BOX[m.convertHexToInt(oneByte.charAt(0))][m.convertHexToInt(oneByte.charAt(1))]);
				} else {
					res += Integer.toHexString(
							S_BOX[m.convertHexToInt(oneByte.charAt(0))][m.convertHexToInt(oneByte.charAt(1))]);
				}
			}
		}
		return res;
	}

	public String rootWord(String s) {
		return s.substring(2) + s.substring(0, 2);
	}

	public String XORkey(String A, String B) {
		Model m = new Model();
		A = A.toLowerCase();
		B = B.toLowerCase();
		String res = "";
		String res1 = "";
		String a = "";
		String b = "";
		for (int i = 0; i < 8; i++) {
			a += m.HexToBinary(A.charAt(i));
			b += m.HexToBinary(B.charAt(i));
		}
		for (int i = 0; i < 32; i++) {
			if (a.charAt(i) == b.charAt(i)) {
				res += "0";
			} else {
				res += "1";
			}
		}
		for (int i = 0; i < res.length(); i += 4) {
			String oneByte = res.substring(i, i + 4);
			res1 += m.binaryToHexKey(oneByte);
		}
		return res1;
	}

	public String[][] convertIntToString(int[][] intArr) {
		String[][] res = new String[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (Integer.toHexString(intArr[j][i]).length() == 1) {
					res[j][i] = "0" + Integer.toHexString(intArr[j][i]);
				} else {
					res[j][i] = Integer.toHexString(intArr[j][i]);
				}
			}
		}
		return res;
	}

	public String textToHex(String text) {
		String res = "";
		StringBuilder hexBuilder = new StringBuilder();
		for (char character : text.toCharArray()) {
			hexBuilder.append(String.format("%02X", (int) character)); // trả về byte <-> 1 ký tự tương ứng với 1 byte
		}
		res += hexBuilder;
		int lessLength = 16 - ((res.length() / 2) % 16);
		if (text.length() % 16 == 0) {
			lessLength = 0;
		}
		if (lessLength > 0) {
			for (int i = 0; i < lessLength; i++) {
				res += "0";
				res += Integer.toHexString(lessLength);
			}
		}
		Model.paddingInput = lessLength;
		Model.loops = res.length() / 32;
		Model.inputHex = res;
		System.out.println("text to hex : " + res);
		return res;
	}

	public String hexToText(String hexStr) {
		StringBuilder output = new StringBuilder("");
		for (int i = 0; i < hexStr.length(); i += 2) {
			String str = hexStr.substring(i, i + 2);
			output.append((char) Integer.parseInt(str, 16));
		}
		return output.toString();
	}

	public String hexToText2(String hexString) {
		String[] hexArray = hexString.split(" ");
		StringBuilder unicodeBuilder = new StringBuilder();

		for (String hex : hexArray) {
			if (!hex.isEmpty()) {
				try {
					int hexValue = Integer.parseInt(hex, 16);
					unicodeBuilder.append((char) hexValue);
				} catch (NumberFormatException e) {
					// Handle invalid hex values if needed
					System.err.println("Invalid hex value: " + hex);
				}
			}
		}

		return unicodeBuilder.toString();
	}

	public String base64ToText(String base64String) {
		byte[] bytes = Base64.getDecoder().decode(base64String);
		return new String(bytes);
	}

	public String textToBase64(String textString) {
		byte[] bytes = textString.getBytes();
		return Base64.getEncoder().encodeToString(bytes);
	}

	public String readFileByPath(String pathFile) {
		Path filePath = Paths.get(pathFile);
		String fileContent = "";

		try {
			byte[] bytes = Files.readAllBytes(filePath);
			fileContent = new String(bytes, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}

	public void saveToFile(String content, MainView view) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save File");

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
		fileChooser.setFileFilter(filter);

		int userSelection = fileChooser.showSaveDialog(view);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
				writer.write(content);
				JOptionPane.showMessageDialog(view, "File saved successfully!");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(view, "Error saving file: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}