package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Model;

public class Controller {

	public int validInput(String input) {
		Pattern pt = Pattern.compile("[^0-9A-Fa-f]");
		Matcher mt = pt.matcher(input);
		if (!mt.find()) {
			if (input.length() == 32) {
				return 1;// valid input
			} else {
				return -1;// invalid input length
			}
		} else {
			return -2;// invalid input character
		}
	}

	public int validKey(String key) {
		Pattern pt = Pattern.compile("[^0-9A-Fa-f]");
		Matcher mt = pt.matcher(key);
		if (!mt.find()) {
			switch (key.length()) {
				case 32:
					return 1; // 128 bit
				case 48:
					return 2; // 196 bit
				case 64:
					return 3; // 256 bit
				default:
					return -1;// invalid key length
			}
		} else {
			return -2; // invalid key character
		}
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

	public int[][] subOrInvSubByte(int rows, int colums, String[][] matrix, int[][] S_BOX, int[][] arr) { // so hang cua
																											// ma tran,
																											// so cot
																											// cua ma
																											// tran ,
																											// mang
																											// String tu
																											// controller.matrix,
																											// S_BOX
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

	public int[][] subOrInvSubByte(String[][] matrix, String[][] S_BOX, String[][] arr) { // so hang cua ma tran, so cot
																							// cua ma tran , mang String
																							// tu controller.matrix,
																							// S_BOX
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

}