package com.example.myreversi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.myreversi.BoardCell.NEXT_DIRECTION;
import com.example.myreversi.BoardCell.STONE_COLOR;

public abstract class AbstractReversiLogic {

	// フィールド定義
	BoardCondition boardCondition = null;					// 盤面の状況
	STONE_COLOR prevStoneColor = STONE_COLOR.NONE;			// 直前の手番
	Map<NEXT_DIRECTION, List<BoardCell>> prevReverseMap;	// 直前にひっくり返した石のMap
	
	// 初期化用の抽象メソッド
	// (AIの初期化などを行う）
	protected abstract void doInitExtend();

	// 次の着手箇所を取得
	// AIロジックでない場合はnullを返すこと
	public abstract BoardCell doThink();
	
	// コンストラクタ
	// リバーシ共通のインスタンス生成を行う
	public AbstractReversiLogic() {
		// 盤面のインスタンスを生成
		this.boardCondition = new BoardCondition();
	}
	
	// 初期化処理
	public void doInit() {
		// 盤面を初期化
		boardCondition.doInit();

		// ゲーム個別の初期化処理を実行
		doInitExtend();
	}
	
	// 終局判定
	public boolean isFinished() {
		return this.boardCondition.isFinished();
	}
	
	// 石を置いてひっくり返す
	public boolean doPut(BoardCell boardCell) {
		// 直前の手番を退避
		this.prevStoneColor = this.boardCondition.getStoneColor();
		
		// 石を置いてひっくり返す
		prevReverseMap = this.boardCondition.put(boardCell.getAddress());
		return (prevReverseMap != null);
	}

	// 最新のゲーム状態を返す
	public BoardCondition getBoardCondition() {
		return boardCondition;
	}

	// 直前にひっくり返した石のリストを返す
	public List<BoardCell> getPrevReverseList() {
		if (prevReverseMap == null) {
			return null;
		}
		
		List<BoardCell> boardCellList = new ArrayList<BoardCell>(48);
		for (List<BoardCell> reverseList : prevReverseMap.values()) {
			boardCellList.addAll(reverseList);
		}
		return boardCellList;
	}

	// 直前の手番を返す
	public STONE_COLOR getPrevStoneColor() {
		return prevStoneColor;
	}
}
