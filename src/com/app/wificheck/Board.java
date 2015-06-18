package com.app.wificheck;

import java.util.StringTokenizer;

import android.util.Log;

public class Board {
	private final String TAG = getClass().getSimpleName();
	final int[][] board = new int[10][10];

	static final int COLOR_BLANK = 0;
	static final int COLOR_BLACK = 1;
	static final int COLOR_WHITE = 2;
	private final int color;
	private boolean myTurn;

	public Board(int color) {
		this.color = color;
		myTurn = color == COLOR_BLACK;
		board[4][4] = COLOR_WHITE;
		board[5][5] = COLOR_WHITE;
		board[4][5] = COLOR_BLACK;
		board[5][4] = COLOR_BLACK;
	}

	/**
	 * 指定した座標に駒を置く。駒が置ける場合ははさまれた駒は裏返る。
	 * @param x X 座標
	 * @param y Y 座標
	 * @param color 駒の色
	 * @return 駒が置けた場合はメッセージ、そうでない場合はnull
	 */
	private String put(int x, int y, int color) {
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return null;
		}
		Log.d(TAG, "put x:" + x + " y:" + y + " color:" + (color == COLOR_BLACK ? "B" : "W"));
		int xx = x + 1;
		int yy = y + 1;
		int opponent = color == COLOR_WHITE ? COLOR_BLACK : COLOR_WHITE;
		int mine = color == COLOR_WHITE ? COLOR_WHITE : COLOR_BLACK;
		boolean judge = false;

		System.out.println("xx:" + xx + " yy:" + yy + " color:" + (color == COLOR_BLACK ? "B" : "W"));

		if (board[xx][yy] != COLOR_BLANK) {
			System.out.println("this place is not blank.");
			return null;
		}

		// 状況をデバッグ出力
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				if (board[j][i] == COLOR_BLACK) {
					sb.append("●");
				} else if (board[j][i] == COLOR_WHITE) {
					sb.append("○");
				} else if (i == yy && j == xx) {
					if (color == COLOR_BLACK) {
						sb.append("◆");
					} else {
						sb.append("◇");
					}
				} else {
					sb.append("□");
				}
			}
			sb.append("\n");
		}
		Log.d(TAG, new String(sb));

		// 上方向に捜査
		if (board[xx][yy-1] == opponent) {
			for (int i = 2; board[xx][yy-i] != COLOR_BLANK; i++) {
				if (board[xx][yy-i] == mine) {
					judge = true;
					for (int j = 1; board[xx][yy-j] != mine; j++) {
						board[xx][yy-j] = mine;
					}
					break;
				}
			}
		}
		// 下方向に捜査
		if (board[xx][yy+1] == opponent) {
			for (int i = 2; board[xx][yy+i] != COLOR_BLANK; i++) {
				if (board[xx][yy+i] == mine) {
					judge = true;
					for (int j = 1; board[xx][yy+j] != mine; j++) {
						board[xx][yy+j] = mine;
					}
					break;
				}
			}
		}
		// 左方向に捜査
		if (board[xx-1][yy] == opponent) {
			for (int i = 2; board[xx-i][yy] != COLOR_BLANK; i++) {
				if (board[xx-i][yy] == mine) {
					judge = true;
					for (int j = 1; board[xx-j][yy] != mine; j++) {
						board[xx-j][yy] = mine;
					}
					break;
				}
			}
		}
		// 右方向に捜査
		if (board[xx+1][yy] == opponent) {
			for (int i = 2; board[xx+i][yy] != COLOR_BLANK; i++) {
				if (board[xx+i][yy] == mine) {
					judge = true;
					for (int j = 1; board[xx+j][yy] != mine; j++) {
						board[xx+j][yy] = mine;
					}
					break;
				}
			}
		}
		// 左上方向に捜査
		if (board[xx-1][yy-1] == opponent) {
			for (int i = 2; board[xx-i][yy-i] != COLOR_BLANK; i++) {
				if (board[xx-i][yy-i] == mine) {
					judge = true;
					for (int j = 1; board[xx-j][yy-j] != mine; j++) {
						board[xx-j][yy-j] = mine;
					}
					break;
				}
			}
		}
		// 右上方向に捜査
		if (board[xx+1][yy-1] == opponent) {
			for (int i = 2; board[xx+i][yy-i] != COLOR_BLANK; i++) {
				if (board[xx+i][yy-i] == mine) {
					judge = true;
					for (int j = 1; board[xx+j][yy-j] != mine; j++) {
						board[xx+j][yy-j] = mine;
					}
					break;
				}
			}
		}
		// 左下方向に捜査
		if (board[xx-1][yy+1] == opponent) {
			for (int i = 2; board[xx-i][yy+i] != COLOR_BLANK; i++) {
				if (board[xx-i][yy+i] == mine) {
					judge = true;
					for (int j = 1; board[xx-j][yy+j] != mine; j++) {
						board[xx-j][yy+j] = mine;
					}
					break;
				}
			}
		}
		// 右下方向に捜査
		if (board[xx+1][yy+1] == opponent) {
			for (int i = 2; board[xx+i][yy+i] != COLOR_BLANK; i++) {
				if (board[xx+i][yy+i] == mine) {
					judge = true;
					for (int j = 1; board[xx+j][yy+j] != mine; j++) {
						board[xx+j][yy+j] = mine;
					}
					break;
				}
			}
		}

		if (judge) {
			board[xx][yy] = mine;
			myTurn = !myTurn;
		}

		return x + "," + y;
	}

	public String put(int x, int y) {
		String message = null;
		if (myTurn) {
			message = put(x, y, color);
		}
		return message;
	}

	public void receiveMessage(String message) {
		Log.d(TAG, "receiveMessage:" + message);
		StringTokenizer st = new StringTokenizer(message, ",");
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		put(x, y, color == COLOR_WHITE ? COLOR_BLACK : COLOR_WHITE);
	}
}
