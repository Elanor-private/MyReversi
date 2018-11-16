package com.example.myreversi;

import com.example.myreversi.BoardCell.STONE_COLOR;

public abstract class AbstractReversiGameApp {

	// フィールド定義
	protected AbstractReversiLogic	reversiLogic = null;	// ゲームロジック
	protected AbstractReversiView 	reversiView = null;		// ビュー
	
	// コンストラクタ
	public AbstractReversiGameApp(AbstractReversiLogic reversiLogic,
			AbstractReversiView reversiView) {
		this.reversiLogic = reversiLogic;
		this.reversiView = reversiView;
	}

	protected abstract void gameStart();
	
	// アプリケーション実行
	public void run() {
		// ロジッククラスの初期化
		reversiLogic.doInit();
		
		// ビューの初期化と初期盤面表示
		reversiView.putAll(reversiLogic.getBoardCondition());
		
		// ビューに初期のゲーム状態を表示
		reversiView.showCondition(reversiLogic.getBoardCondition());

		// ゲーム開始
		gameStart();
	}
	
	// 入力待ち
	protected void doPlay() {
		// Logicクラスから盤面入力（AIなど）
		BoardCell boardCell = reversiLogic.doThink();

		if (boardCell == null) {
			// 次の着手箇所をロジックから取得できない場合は入力待ちに移行
			boardCell = reversiView.waitForInput();
		}

		if (boardCell != null) {
			// 「石を置く」ロジックを実行
			if (reversiLogic.doPut(boardCell)) {
				// 石を置いた後の画面表示
				// まずは石を置く
				STONE_COLOR stoneColor = reversiLogic.getPrevStoneColor();
				reversiView.putStone(boardCell, stoneColor);
				// ひっくり返す
				reversiView.reverseAll(reversiLogic.getPrevReverseList(), stoneColor);
				// ゲーム状況を表示
				reversiView.showCondition(reversiLogic.getBoardCondition());
			}
		}
	}
};
