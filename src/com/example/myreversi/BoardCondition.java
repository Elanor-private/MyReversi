package com.example.myreversi;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.example.myreversi.BoardCell.NEXT_DIRECTION;
import com.example.myreversi.BoardCell.STONE_COLOR;

public class BoardCondition {
	// フィールド定義
	private Map<Byte, BoardCell> blankField = null;	// 空いているマス
	private List<BoardCell> boardState = null;		// ボードの状態
	private STONE_COLOR stoneColor;					// 手番
	private STONE_COLOR passPlayer;					// 最後にパスしたプレーヤー
	private int blackCount = 0;						// 黒の個数
	private int whiteCount = 0;						// 白の個数
	private STONE_COLOR winner;						// 勝者


	// コンストラクタ
	public BoardCondition() {
		// 空きマスの初期化
		this.blankField = new HashMap<Byte, BoardCell>(64);

		// ボード状態の初期化
		this.boardState = new ArrayList<BoardCell>(64);
		for (byte i = 0; i < 64; i++) {
			// 64マスブランクで初期化
			this.boardState.add(new BoardCell(i));
		}
		
		this.blackCount = 0;
		this.whiteCount = 0;
		this.passPlayer = STONE_COLOR.NONE;
	}
	
	// ボードの状態を返す
	public List<BoardCell> getBoardState() {
		return boardState;
	}

	// 黒の個数を返す
	public int getBlackCount() {
		return blackCount;
	}

	// 白の個数を返す
	public int getWhiteCount() {
		return whiteCount;
	}

	// 手番を返す
	public STONE_COLOR getStoneColor() {
		return this.stoneColor;
	}

	// パスした手番を返す
	public STONE_COLOR getPassPlayer() {
		return passPlayer;
	}

	// 手番を設定
	public void setStoneColor(STONE_COLOR stoneColor) {
		this.stoneColor = stoneColor;
	}

	// 勝者を取得
	public STONE_COLOR getWinner() {
		return winner;
	}

	// ゲームを初期状態にする
	public void doInit() {
		// 盤面をクリア
		this.blankField.clear();
		for (byte i = 0; i < 64; i++) {
			BoardCell boardCell = this.boardState.get(i);
			boardCell.put(STONE_COLOR.NONE);
			this.blankField.put(i, boardCell);
		}
		
		// 黒石の配置
		this.boardState.get(4 + 3*8).put(STONE_COLOR.BLACK);
		this.boardState.get(3 + 4*8).put(STONE_COLOR.BLACK);

		// 白石の配置
		this.boardState.get(3 + 3*8).put(STONE_COLOR.WHITE);
		this.boardState.get(4 + 4*8).put(STONE_COLOR.WHITE);

		// 空きマスを調整
		this.blankField.remove((byte)(4 + 3*8));
		this.blankField.remove((byte)(3 + 4*8));
		this.blankField.remove((byte)(3 + 3*8));
		this.blankField.remove((byte)(4 + 4*8));
		
		// 手番を黒に設定
		this.stoneColor = STONE_COLOR.BLACK;

		// 黒、白の個数を初期化(4個と分かり切っているが…)
		this.blackCount = this.countStone(STONE_COLOR.BLACK);
		this.whiteCount = this.countStone(STONE_COLOR.WHITE);
	}
	
	// ひっくり返せる石のリストを返す
	//   着手不可の場合nullを返す
	public Map<NEXT_DIRECTION, List<BoardCell>> getReversible(byte address, STONE_COLOR color) {
		// 戻り値の宣言
		// マップサイズは、6石/方向×8方向=48
		Map<NEXT_DIRECTION, List<BoardCell>> retMap = new HashMap<NEXT_DIRECTION, List<BoardCell>>(48);

		// 指定位置を起点として全方向を走査
		for (NEXT_DIRECTION nextDirection : NEXT_DIRECTION.values()) {
			// ひっくり返せる石のリストを初期化
			//  1方向には最大6石しかひっくり返らない
			List<BoardCell> reversibleStone = new ArrayList<BoardCell>(6);

			// 現在位置のセルを取り出す
			BoardCell boardCell = this.boardState.get((int)address);

			// findNextしながら相手の色かを確認
			byte nextAddress;
			for (nextAddress = boardCell.findNext(nextDirection); 
					nextAddress != -1;
					nextAddress = boardCell.findNext(nextDirection)) {
				
				// セルの位置を更新
				// -1の場合はループを抜けるのでここには入らない
				boardCell = this.boardState.get((int)nextAddress);
				
				// セルの色を判定
				if (boardCell.isBlank()) {
					// 置かれていない場合は着手不可
					reversibleStone.clear();
					break;
				} else if (boardCell.getColor().getReverse() == color.getInt()) {
					// 相手の色の場合は置ける
					reversibleStone.add(boardCell);
				} else {
					// 自分の色がある場合はループを抜ける(その先は走査しない)
					break;
				}
			}
			
			// 置ける場合かつひっくり返せる場合はMapに追加
			if (nextAddress != -1 && !reversibleStone.isEmpty()) {
				retMap.put(nextDirection, reversibleStone);
			}
		}
		
		// Mapが空の場合はNullを返す
		if (retMap.isEmpty()) {
			retMap = null;
		} 
		
		return retMap;
	}

	// 現在の手番の着手可能箇所を求める
	// 終局時はnullを返す
	public List<BoardCell> getPuttableCell() {
		return this.getPuttableCell(this.stoneColor);
	}
	
	// 指定された色の着手可能箇所を求める
	public List<BoardCell> getPuttableCell(STONE_COLOR stoneColor) {
		List<BoardCell> boardCellList = new ArrayList<BoardCell>(64);
		
		// 空きマスがない場合は終局
		if (this.blankField.isEmpty()) {
			return null;
		}
		
		// すべての空きマスに対して着手可否を判定
		for (BoardCell boardCell : this.blankField.values()) {
			if (getReversible(boardCell.getAddress(), stoneColor) != null) {
				// 着手可能箇所が見つかったのでマスを追加
				boardCellList.add(boardCell);
			}
		}
		
		// 取得結果を返す
		return boardCellList;
	}
		
	// 石を置く
	public Map<NEXT_DIRECTION, List<BoardCell>> put(byte address) {
		// 既に石が置かれている場合は置けない
		if (this.boardState.get(address).getColor() != STONE_COLOR.NONE) {
			return null;
		}
		
		// ひっくり返せる石のリストを取得
		Map<NEXT_DIRECTION, List<BoardCell>> reversibleMap = getReversible(address, this.stoneColor);
		
		if (reversibleMap == null) {
			return null;
		}
		
		// その場所に石を置く
		this.boardState.get(address).put(this.stoneColor);
		
		// 各方向をひっくり返す
		for (List<BoardCell> reversibleList : reversibleMap.values()) {
			this.reverse(reversibleList);
		}

		// 石を置いたマスを空きマスから削除
		this.blankField.remove(address);

		// 手番を変える
		this.switchPlayer();

		// パス判定
		List<BoardCell> puttableList = this.getPuttableCell(this.stoneColor);
		if (puttableList == null || puttableList.size() == 0) {
			// 相手の着手可能箇所がない場合
			// パスした手番を設定
			this.passPlayer = this.stoneColor;
			// 手番を変える
			this.switchPlayer();
		} else {
			// パスした手番をリセット
			this.passPlayer = STONE_COLOR.NONE;
		}

		// 黒、白の個数を更新
		this.blackCount = this.countStone(STONE_COLOR.BLACK);
		this.whiteCount = this.countStone(STONE_COLOR.WHITE);

		// 終局判定と勝者の設定
		this.setWinner();
		
		return reversibleMap;
	}

	// 終局判定と勝者の設定
	private void setWinner() {

		// 終局判定
		if (this.isFinished()) {
			// 終局の場合、勝者を設定
			if (this.blackCount > this.whiteCount) {
				// 黒の勝ち
				this.winner = STONE_COLOR.BLACK;
			} else if (this.blackCount < this.whiteCount) {
				// 白の勝ち
				this.winner = STONE_COLOR.WHITE;
			} else {
				// 引き分け
				this.winner = STONE_COLOR.NONE;
			}
		} else {
			// 対局中は勝者未設定
			this.winner = null;
		}
	}
	
	// マス目を単純配置(ひっくり返さない)
	public void setCell(BoardCell boardCell) {
		// マップ内のマス目を置き換え
		this.boardState.set(boardCell.getAddress(), boardCell);

		// 空きマスの更新
		if ( boardCell.getColor() == STONE_COLOR.NONE ) {
			// 空きマスの追加
			this.blankField.putIfAbsent(boardCell.getAddress(), boardCell);
		} else {
			// 空きマスから削除
			this.blankField.remove(boardCell.getAddress());
		}
		
		// 黒、白の個数を更新
		this.blackCount = this.countStone(STONE_COLOR.BLACK);
		this.whiteCount = this.countStone(STONE_COLOR.WHITE);

		// 終局判定と勝者の設定
		this.setWinner();
	}
	
	// 一方向をひっくり返す
	private void reverse(List<BoardCell> reversibleList) {
		for (BoardCell boardCell : reversibleList) {
			// ひっくり返す
			this.boardState.get(boardCell.getAddress()).reverse(this.stoneColor);
		}
	}
	
	// 手番を変える
	public void switchPlayer() {
		this.stoneColor = (this.stoneColor == STONE_COLOR.BLACK) ? STONE_COLOR.WHITE : STONE_COLOR.BLACK;
	}
	
	// 終局判定
	public boolean isFinished() {
		// 黒、白それぞれの着手可能箇所数を取得
		List<BoardCell> black = this.getPuttableCell(STONE_COLOR.BLACK);
		List<BoardCell> white = this.getPuttableCell(STONE_COLOR.WHITE);

		// 終局判定
		if (black == null || white == null) {
			// いずれかがnullの場合は終局
			return true;
		} else if (black.size() == 0 && white.size() == 0) {
			// 黒、白とも着手可能箇所が無ければ終局
			return true;
		}
		
		// 黒、白いずれかは着手可能なので終局ではない
		return false;
	}
	
	// 石の個数を返す
	public int countStone(STONE_COLOR color) {
		int countValue = 0;
		
		for (BoardCell boardCell : this.boardState) {
			if (boardCell.getColor() == color) {
				countValue++;
			}
		}
		
		return countValue;
	}
	
	// ゲーム盤のコピーを生成
	@Override
	public BoardCondition clone() {
		// インスタンスを生成
		BoardCondition cloneBoardCondition = new BoardCondition();
		
		// 空きマス情報をコピー
		cloneBoardCondition.blankField.clear();
		for (Map.Entry<Byte, BoardCell> entry : this.blankField.entrySet()) {
			cloneBoardCondition.blankField.put(entry.getKey(), entry.getValue().clone());
		}
		
		// ボードの状態をコピー
		cloneBoardCondition.boardState.clear();
		for (BoardCell boardCell : this.boardState) {
			cloneBoardCondition.boardState.add(boardCell.clone());
		}
		
		// 手番を設定
		cloneBoardCondition.stoneColor = this.stoneColor;

		// パスした手番を設定
		cloneBoardCondition.passPlayer = this.passPlayer;
		
		// 黒、白の個数を設定
		cloneBoardCondition.blackCount = this.blackCount;
		cloneBoardCondition.whiteCount = this.whiteCount;

		// 勝者を設定
		cloneBoardCondition.winner = this.winner;
		
		return cloneBoardCondition;
	}
}
