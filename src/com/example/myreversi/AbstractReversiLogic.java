package com.example.myreversi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.example.myreversi.BoardCell.NEXT_DIRECTION;
import com.example.myreversi.BoardCell.STONE_COLOR;

public abstract class AbstractReversiLogic {

	// フィールド定義
	BoardCondition boardCondition = null;					// 盤面の状況
	STONE_COLOR prevStoneColor = STONE_COLOR.NONE;			// 直前の手番
	Map<NEXT_DIRECTION, List<BoardCell>> prevReverseMap;	// 直前にひっくり返した石のMap
	private Stack<BoardCondition> undoBuffer = null;		// undoバッファ
	private Stack<BoardCondition> redoBuffer = null;		// redoバッファ
	
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
		
		// undo,redoバッファを初期化
		this.undoBuffer = new Stack<BoardCondition>();
		this.redoBuffer = new Stack<BoardCondition>();
	}
	
	// 初期化処理
	public void doInit() {
		// 盤面を初期化
		this.boardCondition.doInit();

		// undo,redoバッファを初期化
		this.undoBuffer.clear();
		this.undoBuffer.push(this.boardCondition.clone());
		this.redoBuffer.clear();
		
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
		
		// 着手可能な場合、undo/redoバッファを設定
		if (prevReverseMap != null) {
			// 着手後の状態のコピーをundoバッファに入れる
			this.undoBuffer.push(this.boardCondition.clone());

			// redoバッファはクリア
			this.redoBuffer.clear();
			
			return true;
			
		} else {
			return false;
		}
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
	
	// undo可否を返す
	public boolean canUndo() {
		if ( this.undoBuffer == null || this.undoBuffer.size() <= 1 ) {
			return false;
		} else {
			return true;
		}
	}
	
	// undo処理
	public void doUndo() {
		// undoできない場合は何もしない
		if ( !this.canUndo() ) {
			return;
		}
		
		// undoバッファの末尾を取り出す
		BoardCondition undoCondition = this.undoBuffer.pop();
		
		// 取り出した要素をredoバッファに追加
		this.redoBuffer.push(undoCondition);
		
		// undoバッファの末尾から盤面の状態を取得
		undoCondition = this.undoBuffer.peek();
		
		// 盤面の状態をフィールドに再設定
		// バッファ内容が着手で書き換えられる（状態を保持できない）ためコピーを渡す
		this.boardCondition = undoCondition.clone();
	}
	
	// redo可否を返す
	public boolean canRedo() {
		if (this.redoBuffer == null || this.redoBuffer.size() == 0 ) {
			return false;
		} else {
			return true;
		}
	}
	
	// redo処理
	public void doRedo() {
		// redoできない場合は何もしない
		if ( !this.canRedo() ) {
			return;
		}
		
		// redoスタックを取り出す
		BoardCondition redoCondition = this.redoBuffer.pop();
		
		// 取り出した内容をundoスタックに設定
		this.undoBuffer.push(redoCondition);
		
		// 盤面の状態をフィールドに再設定
		// バッファ内容が着手で書き換えられる（状態を保持できない）ためコピーを渡す
		this.boardCondition = redoCondition.clone();
	}
}
