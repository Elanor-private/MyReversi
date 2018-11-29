package com.example.myreversi;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.example.myreversi.BoardCell.NEXT_DIRECTION;
import com.example.myreversi.BoardCell.STONE_COLOR;

public class BoardCondition {
	// フィールド定義
	private Map<Byte, BoardCell> blankField = null;		// 空いているマス
	private List<BoardCell> boardState = null;			// ボードの状態
	private STONE_COLOR stoneColor;						// 手番
	private STONE_COLOR passPlayer;						// 最後にパスしたプレーヤー
	private int blackCount = 0;							// 黒の個数
	private int whiteCount = 0;							// 白の個数
	private STONE_COLOR winner;							// 勝者
	
	// 以下、開放度算出用の定義
	private Map<Byte, Integer> openCountMap = null;		// 開放度
	
	// 以下、確定石算出用の定義
	private Map<Byte, BoardCell> fixedMap = null;		// 確定石Map
	private Map<STONE_COLOR, List<BoardCell>> fixedList = null;	// 黒白それぞれの確定石

	// 盤面圧縮結果(確定石算出に使用)
	private class CompressResult {
		private class CompressElement {
			private STONE_COLOR color = null;						// マスの色
			private List<BoardCell> cellList = null;				// 圧縮されたマス

			// コンストラクタ
			// セルを受け取って圧縮結果を初期化
			public CompressElement(BoardCell boardCell) {
				this.color = boardCell.getColor();
				this.cellList = new ArrayList<BoardCell>(8);
				this.cellList.add(boardCell.clone());
			}
			
			public STONE_COLOR getColor() {
				return this.color;
			}
			
			// 圧縮処理
			public void add(BoardCell boardCell) {
				// 色が同じ場合のみ圧縮されたマスのリストに追加
				STONE_COLOR boardColor = boardCell.getColor();
				if (!STONE_COLOR.NONE.equals(boardColor) && this.color.equals(boardColor)) {
					// 圧縮されたマスに追加
					cellList.add(boardCell);
				}
			}
			
			// 圧縮されたマスのリストを返す
			public List<BoardCell> getCells() {
				return this.cellList;
			}
		};
		
		// フィールド
		private List<CompressElement> compressResult = null;


		// コンストラクタ
		public CompressResult() {
			// 圧縮結果を初期化
			this.compressResult = new ArrayList<CompressElement>(8);
		}

		// 圧縮処理
		public void compress(List<BoardCell> cellList) {
			// 圧縮要素
			CompressElement compressElement = null;
			
			// 圧縮結果を初期化
			this.compressResult.clear();

			for (int i = 0; i < cellList.size(); i++) {
				BoardCell boardCell = cellList.get(i);
				
				if (this.compressResult.isEmpty() || STONE_COLOR.NONE.equals(boardCell.getColor())) {
					// リストが空、または空白マスの場合はクラスを生成して追加
					// 初回は必ずここを通る
					compressElement = new CompressElement(boardCell);
					this.compressResult.add(compressElement);

				} else if (boardCell.getColor().equals(compressElement.getColor())) {
					// 同一色の場合は既存の圧縮結果のリストに追加
					this.compressResult.get(this.compressResult.size()-1).add(boardCell);
					
				} else {
					// 色が異なる場合はクラスを生成して追加
					compressElement = new CompressElement(boardCell);
					this.compressResult.add(compressElement);
				}				
			}
		}
		
		// 圧縮結果のn番目を返す
		// 範囲外の場合はnullを返す
		public List<BoardCell> get(int index) {
			if (this.compressResult.size() <= index) {
				return null;
			} else {
				return this.compressResult.get(index).getCells();
			}
		}
		
		// 圧縮結果の色を返す
		// 範囲外の場合はnullを返す
		public STONE_COLOR getColor(int index) {
			if (this.compressResult.size() <= index) {
				return null;
			} else {
				return this.compressResult.get(index).getColor();
			}
		}
		
		// 圧縮結果の要素数を返す
		public int size() {
			return this.compressResult.size();
		}
	};

	// コンストラクタ
	public BoardCondition() {
		// フィールドのインスタンスを生成
		this.blankField = new HashMap<Byte, BoardCell>(64);
		this.boardState = new ArrayList<BoardCell>(64);
		this.fixedMap = new HashMap<Byte, BoardCell>(64);
		this.fixedList = new HashMap<STONE_COLOR, List<BoardCell>>(64);
		this.openCountMap = new HashMap<Byte, Integer>(64);
		
		// フィールドの初期化
		this.initFields();
		
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
	
		// フィールドを初期化
		this.initFields();

		// 石の配置
		BoardCell boardCell = null;

		// 黒石の配置
		boardCell = new BoardCell((byte)4, (byte)3);
		boardCell.put(STONE_COLOR.BLACK);
		this.setCell(boardCell);

		boardCell = new BoardCell((byte)3, (byte)4);
		boardCell.put(STONE_COLOR.BLACK);
		this.setCell(boardCell);
		
		// 白石の配置
		boardCell = new BoardCell((byte)3, (byte)3);
		boardCell.put(STONE_COLOR.WHITE);
		this.setCell(boardCell);

		boardCell = new BoardCell((byte)4, (byte)4);
		boardCell.put(STONE_COLOR.WHITE);
		this.setCell(boardCell);
		
		// 手番を黒に設定
		this.stoneColor = STONE_COLOR.BLACK;
	}
	
	// フィールド初期化
	private void initFields() {

		// ゲーム状態を初期化
		this.boardState.clear();
		this.blankField.clear();

		for (byte i = 0; i < 64; i++) {
			// 盤面をクリア
			// 64マスブランクで初期化
			this.boardState.add(new BoardCell(i));

			// 空きマスを初期化
			BoardCell boardCell = this.boardState.get(i);
			boardCell.put(STONE_COLOR.NONE);
			this.blankField.put(i, boardCell);

			// 開放度を初期化
			// 4隅=3  辺=5 その他=8
			if (i == 0 || i == 7 || i == 56 || i == 63) {
				// 4隅
				this.openCountMap.put(i, 3);
			} else if ((i & 0x07) == 0 || (i & 0x07) == 7) {
				// 左右の辺
				this.openCountMap.put(i, 5);
			} else if ((i & 0x38) == 0 || (i & 0x38) == 0x38) {
				// 上下の辺
				this.openCountMap.put(i, 5);
			} else {
				// 隅、辺以外
				this.openCountMap.put(i, 8);
			}
		}

		// 確定石の情報を初期化
		this.fixedMap.clear();
		this.fixedList.clear();
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
		if (!STONE_COLOR.NONE.equals(this.boardState.get(address).getColor())) {
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

		// 周囲の開放度を更新
		this.updateOpenCount(this.boardState.get(address));

		// 確定石を差分更新
		this.updateFixedCell(false);
		
		// 黒、白の個数を更新
		this.blackCount = this.countStone(STONE_COLOR.BLACK);
		this.whiteCount = this.countStone(STONE_COLOR.WHITE);

		// 終局判定と勝者の設定
		this.setWinner();
		
		return reversibleMap;
	}
	
	// 周囲の空きマス数をカウント
	// 開放度=sum(ひっくり返るマスの空きマス数)
	public int countBlankCell(byte address) {
		int			blankCount = 0;
		BoardCell	boardCell = this.boardState.get(address);
		
		// 周囲の空きマス数をカウント
		for (NEXT_DIRECTION nextDirection : NEXT_DIRECTION.values()) {
			byte nextAddress = boardCell.findNext(nextDirection);
			
			if (nextAddress != -1 && this.blankField.containsKey(nextAddress)) {
				// 空きマスの場合インクリメント
				blankCount++;
			}
		}
		
		// 計算結果を返す
		return blankCount;
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
		if ( STONE_COLOR.NONE.equals(boardCell.getColor()) ) {
			// 空きマスの追加
			this.blankField.putIfAbsent(boardCell.getAddress(), boardCell);
		} else {
			// 空きマスから削除
			this.blankField.remove(boardCell.getAddress());
		}

		// 周囲の開放度を更新
		this.updateOpenCount(boardCell);
		
		// 確定石を全更新
		this.updateFixedCell(true);
		
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
			if (boardCell.getColor().equals(color)) {
				countValue++;
			}
		}
		
		return countValue;
	}
	
	// 確定石の情報を更新
	//   引数にtrueが渡された場合はフィールドの確定石Mapを初期化して再計算する
	private void updateFixedCell(boolean initFixedMap) {

		//*************************************************
		// 引数判定
		//*************************************************
		if (initFixedMap) {
			// 確定石Map、黒白の確定石リストを初期化
			this.fixedMap.clear();
			this.fixedList.clear();
		}

		if (this.fixedList.size() < 2) {
			// 黒白の確定石リストが未設定の場合は黒白のリストを再設定
			this.fixedList.put(STONE_COLOR.BLACK, new ArrayList<BoardCell>(64));
			this.fixedList.put(STONE_COLOR.WHITE, new ArrayList<BoardCell>(64));
		}
		
		
		//*************************************************
		// 初期化
		//*************************************************
		// 確定石チェック用Map
		Map<Byte, BoardCell> checkMap = new HashMap<Byte, BoardCell>(64);

		// チェック用Mapにチェック対象のセルを詰める
		for (BoardCell boardCell : this.boardState) {
			// 空白セルでない、かつ確定石Mapに含まれていない場合のみチェック対象
			if (!STONE_COLOR.NONE.equals(boardCell.getColor()) && 
					!this.fixedMap.containsKey(boardCell.getAddress())) {
				checkMap.put(boardCell.getAddress(), boardCell);
			}
		}
		
		// チェック用Mapが空になるまでループ
		while (!checkMap.isEmpty()) {
			List<BoardCell> line = null;
			List<BoardCell> noFixedCellList = null;
			
			// Mapの先頭要素を取得
			BoardCell boardCell = (BoardCell)checkMap.values().toArray()[0];
			
			// 上下方向のチェック
			line = getVerticalLine(boardCell);
			noFixedCellList = this.getNoFixedCell(line);
			if (noFixedCellList != null && !noFixedCellList.isEmpty()) {
				// 確定石でない要素をMapから取り除く
				for (BoardCell noFixedCell : noFixedCellList) {
					checkMap.remove(noFixedCell.getAddress());
				}
			}
			
			// 左右方向のチェック
			line = getHorizontalLine(boardCell);
			noFixedCellList = this.getNoFixedCell(line);
			if (noFixedCellList != null && !noFixedCellList.isEmpty()) {
				// 確定石でない要素をMapから取り除く
				for (BoardCell noFixedCell : noFixedCellList) {
					checkMap.remove(noFixedCell.getAddress());
				}
			}

			// ブラックライン(右上→左下)方向のチェック
			line = getBlackLine(boardCell);
			noFixedCellList = this.getNoFixedCell(line);
			if (noFixedCellList != null && !noFixedCellList.isEmpty()) {
				// 確定石でない要素をMapから取り除く
				for (BoardCell noFixedCell : noFixedCellList) {
					checkMap.remove(noFixedCell.getAddress());
				}
			}
			
			// ホワイトライン(左上→右下)方向のチェック
			line = getWhiteLine(boardCell);
			noFixedCellList = this.getNoFixedCell(line);
			if (noFixedCellList != null && !noFixedCellList.isEmpty()) {
				// 確定石でない要素をMapから取り除く
				for (BoardCell noFixedCell : noFixedCellList) {
					checkMap.remove(noFixedCell.getAddress());
				}
			}
			
			// 4方向とも消し込めない石であれば確定石
			if (checkMap.containsKey(boardCell.getAddress())) {
				
				// 確定石Mapへ追加
				this.fixedMap.put(boardCell.getAddress(), boardCell);
				
				// 黒または白の確定石リストへ追加
				this.fixedList.get(boardCell.getColor()).add(boardCell);
			}
			
			// チェック済みの石をチェック用Mapから取り除く
			// (じゃないと無限ループするので)
			checkMap.remove(boardCell.getAddress());
		}
	}

	// 上下方向の盤面リストを取得
	private List<BoardCell> getVerticalLine(BoardCell targetCell) {
		return getLine(
				targetCell, 
				NEXT_DIRECTION.UPPER_VERTICAL, 
				NEXT_DIRECTION.LOWER_VERTICAL);
	}

	// 左右方向の盤面リストを取得
	private List<BoardCell> getHorizontalLine(BoardCell targetCell) {
		return getLine(
				targetCell, 
				NEXT_DIRECTION.HORIZONTAL_LEFT, 
				NEXT_DIRECTION.HORIZONTAL_RIGHT);
	}
	
	// ブラックライン方向の盤面リストを取得
	private List<BoardCell> getBlackLine(BoardCell targetCell) {
		return getLine(
				targetCell, 
				NEXT_DIRECTION.UPPER_RIGHT, 
				NEXT_DIRECTION.LOWER_LEFT);
	}
	
	// ホワイトライン方向の盤面リストを取得
	private List<BoardCell> getWhiteLine(BoardCell targetCell) {
		return getLine(
				targetCell, 
				NEXT_DIRECTION.UPPER_LEFT, 
				NEXT_DIRECTION.LOWER_RIGHT);
	}

	// 端からのリストを作成する共通メソッド
	private List<BoardCell> getLine(
			BoardCell targetCell, 
			NEXT_DIRECTION backward, 
			NEXT_DIRECTION forward
			) {
		
		byte tmpAddress = targetCell.getAddress();
		BoardCell boardCell = targetCell;
		List<BoardCell> resultLine = new ArrayList<BoardCell>(8);

		
		// 前方向に端まで走査
		while (tmpAddress != -1) {
			// 次のアドレスを取得
			tmpAddress = boardCell.findNext(backward);

			if (tmpAddress != -1) {
				// 隣のマスがある場合は次のマスを取得
				boardCell = this.boardState.get(tmpAddress);
			}
		}

		// 下方向に端まで走査
		tmpAddress = boardCell.getAddress();
		resultLine.add(boardCell);

		while (tmpAddress != -1) {
			// 次のアドレスを取得
			tmpAddress = boardCell.findNext(forward);

			if (tmpAddress != -1) {
				// 隣のマスがある場合は次のマスを取得して追加
				boardCell = this.boardState.get(tmpAddress);
				resultLine.add(boardCell);
			}
		}
		
		return resultLine;
	}
	
	
	// 1方向に対する盤面圧縮とチェック処理
	// 確定石ではないマスのリストを返す
	public List<BoardCell> getNoFixedCell(List<BoardCell> line) {
		// ワーク変数
		List <BoardCell> nonFixedCell = new ArrayList<BoardCell>(16);		// 確定石以外のリスト
		CompressResult compressResult = new CompressResult();	// 圧縮結果
		int 			blankCount = 0;							// 空白マスの数
		int 			compressEndPos = 0;						// 圧縮結果の終端

		
		if (line == null || line.size() <= 2) {
			// 入力リストが2マス以下の場合はひっくり返せないのでチェックしない
			return nonFixedCell;
		}
		
		// 盤面を圧縮
		compressResult.compress(line);
		compressEndPos = compressResult.size() - 1;

		// チェック処理(正方向)
		if (STONE_COLOR.NONE.equals(compressResult.getColor(0))) {
			// 先頭が空白セルの場合はカウントを1から始める
			blankCount = 1;
		} 
		
		// 両端は確定石または空白マスなのでループから外す
		for (int i = 1; i < compressEndPos; i++) {
			if (STONE_COLOR.NONE.equals(compressResult.getColor(i))) {
				// 空白マスはカウントのみ
				blankCount++;
				continue;

			} else {
				// 石の置かれているセルの場合、その位置から後方が確定石ではなくなる
				for (int j = 0; j < blankCount; j++) {
					// 後端に達した場合はループを打ち切る(後端は常に確定石か空白)
					if ( (i+j) == (compressEndPos) ) {
						break;
					}
					
					if (!STONE_COLOR.NONE.equals(compressResult.getColor(i+j))) {
						// 対象セルが空白でない場合は戻り値に追加
						nonFixedCell.addAll(compressResult.get(i+j));
					} else {
						// 対象セルが空白の場合はループを打ち切る
						break;
					}
				}
				
				// 空白マス以外を検出したので、空白カウントをリセット
				blankCount = 0;
			}
		}


		// チェック処理(負方向)
		if (STONE_COLOR.NONE.equals(compressResult.getColor(compressEndPos))) {
			// 末尾が空白セルの場合はカウントを1から始める
			blankCount = 1;
		} else {
			// 末尾が空白でない場合はカウントを0から始める
			blankCount = 0;
		}
		
		// 両端は確定石または空白マスなのでループから外す
		for (int i = compressEndPos - 1; i > 0; i--) {
			if (STONE_COLOR.NONE.equals(compressResult.getColor(i))) {
				// 空白マスはカウントのみ
				blankCount++;
				continue;

			} else {
				// 石の置かれているセルの場合、その位置から前方が確定石ではなくなる
				for (int j = 0; j < blankCount; j++) {
					// 前端に達した場合はループを打ち切る(前端は常に確定石か空白)
					if ( i <= j ) {
						break;
					}

					if (!STONE_COLOR.NONE.equals(compressResult.getColor(i-j))) {
						// 対象セルが空白でない場合は戻り値に追加
						nonFixedCell.addAll(compressResult.get(i-j));
					} else {
						// 対象セルが空白の場合はループを打ち切る
						break;
					}
				}
				
				// 空白マス以外を検出したので、空白カウントをリセット
				blankCount = 0;
			}
		}
		
		// 戻り値を返す
		return nonFixedCell;
	}
	
	// 開放度の更新
	private void updateOpenCount(BoardCell boardCell) {
		for (NEXT_DIRECTION nextDirection : NEXT_DIRECTION.values()) {
			// 隣接するマスを取得
			byte neighborAddress = boardCell.findNext(nextDirection);
			
			if (neighborAddress != -1) {
				// 隣接するマスが存在する場合は開放度を更新
				Integer openCount = this.openCountMap.get(neighborAddress);
				openCount--;
				this.openCountMap.put(neighborAddress, openCount);
			}
		}
	}
	
	// 局面を評価
	// 黒から見た評価値を返す
	public int validate() {
		
		// 評価値
		int blackValue = this.validate(STONE_COLOR.BLACK);	// 黒の評価値
		int whiteValue = this.validate(STONE_COLOR.WHITE);	// 白の評価値
		
		return blackValue - whiteValue;
	}
	
	// 評価値算出のコアロジック
	private int validate(STONE_COLOR color) {
		
		int validateValue = 0;				// 評価値
		List <BoardCell> cellList = null;	// Map取得用ワーク変数
		
		final int fixedWeight = 5;			// 確定石に対する重み
		
		
		cellList = this.getPuttableCell(color);
		if (cellList != null) {
			validateValue = cellList.size();
		}
		if (this.fixedList != null) {
			cellList = this.fixedList.get(color);
			if (cellList != null) {
				validateValue += cellList.size() * fixedWeight;
			}
		}
		
		// 算出結果を返す
		return validateValue;
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
