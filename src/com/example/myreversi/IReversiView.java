/**
 * IReversiViewインターフェース
 * ビューに依存する表示処理をインターフェースとして宣言する
 */
package com.example.myreversi;

import com.example.myreversi.BoardCell.STONE_COLOR;

/**
 * @author IT-career
 *
 */
public interface IReversiView {
	// 「ボードの初期化」メソッド(描画方法により初期化内容が異なるため個別実装が必要)
	public void doInit();

	// 「石を置く」メソッド(描画方法が異なるため個別実装が必要)
	public void putStone(byte x, byte y, STONE_COLOR color);

	// 「ひっくり返す」メソッド(描画方法により内容が異なる)
	public void reverse(BoardCell boardCell, STONE_COLOR color);
	
	// 「後処理」メソッド(描画方法により必要な後処理が異なる)
	public void doPost();

	// 「入力待ち状態へ移行」メソッド(描画方法により待ち方が異なる)
	public BoardCell waitForInput();
	
	// 「ゲーム状況の表示」メソッド(描画方法により表示処理が異なる)
	public void showCondition(BoardCondition boardCondition);

}
