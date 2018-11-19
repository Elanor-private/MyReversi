package com.example.myreversi;

import java.util.List;

import com.example.myreversi.BoardCell.STONE_COLOR;

// ゲーム盤ビューの抽象クラス
// 「ボードの初期化」「石を置く」「ひっくり返す」を抽象メソッドとして持つ
public class ReversiViewLogic {

	// フィールド
	IReversiView reversiView = null;

	// コンストラクタ
	public ReversiViewLogic(IReversiView reversiView) {
		this.reversiView = reversiView;
	}

	// マス目指定で石を置く
	public void putStone(BoardCell boardCell, STONE_COLOR color) {
		// 座標系をx,yに変換して抽象メソッドを呼び出す
		reversiView.putStone(boardCell.getX(), boardCell.getY(), color);
	}
	
	// 「ひっくり返す」は実メソッド(「石を置く」を繰り返すため)
	// ひっくり返す対象はビジネスロジック(BoardCondition)で特定する前提とし、
	// ここではViewへの表示ロジックのみを実装
	public void reverseAll(List<BoardCell>boardCellList, STONE_COLOR color) {
		// 入力リストがnullの場合は何もしない
		if (boardCellList == null) {
			return;
		}
		
		// リスト内の対象すべてを描画
		for (BoardCell boardCell : boardCellList) {
			reversiView.reverse(boardCell, color);
		}
		
		// 後処理
		reversiView.doPost();
	}
	
	// 「全配置」は実メソッド(「石を置く」を繰り返すため)
	public void putAll(BoardCondition boardCondition) {
		// 盤面を初期化
		reversiView.doInit();

		// 石を配置
		for (BoardCell boardCell : boardCondition.getBoardState()) {
			STONE_COLOR color = boardCell.getColor();
			if (color != STONE_COLOR.NONE) {
				// 石が置かれている場合のみ配置
				putStone(boardCell, color);
			}
		}
		
		// 後処理
		reversiView.doPost();
	}
}
