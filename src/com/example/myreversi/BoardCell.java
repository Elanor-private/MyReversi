// 盤上のマス目
package com.example.myreversi;


public class BoardCell {
	// 盤上の移動先
	public enum NEXT_DIRECTION {
		UPPER_LEFT(-9),				// 左上
		UPPER_VERTICAL(-8),			// 上
		UPPER_RIGHT(-7),			// 右上
		HORIZONTAL_RIGHT(1),		// 右
		LOWER_RIGHT(9),				// 右下
		LOWER_VERTICAL(8),			// 下
		LOWER_LEFT(7),				// 左下
		HORIZONTAL_LEFT(-1)			// 左
		;
		
		private final int direction;

		private NEXT_DIRECTION(int direction) {
			this.direction = direction;
		}
		
		// 次の移動位置を返す
		public int getOffset() {
			return this.direction;
		}
	};
	
	
	// 盤上の石の色
	public enum STONE_COLOR {
		NONE(0),				// 置かれていない
		BLACK(1),				// 黒
		WHITE(2)				// 白
		;
		
		private final int color;

		private STONE_COLOR(int color) {
			this.color = color;
		}

		// 石の色を返す
		public int getInt() {
			return this.color;
		}

		// 反転色を返す
		public int getReverse() {
			return (this.color ^ 0x03);
		}
	};

	// 盤面の操作内容
	public enum OPERATION {
		NONE,					// 何もしない(石の単純配置など)
		PUT,					// 石を置いてひっくり返す
		UNDO,					// undo
		REDO					// redo
	};
	
	private	byte			x;			// 横座標
	private byte			y;			// 縦座標 
	private byte			address;	// 0-63の連番
	private STONE_COLOR		color;		// 置かれている石		 	
	private OPERATION		operation;	// 盤面の操作内容
	
	
	public OPERATION getOperation() {
		return operation;
	}

	public void setOperation(OPERATION operation) {
		this.operation = operation;
	}

	// コンストラクタ
	// 盤面の初期化時のみ呼び出す前提
	public BoardCell(byte address) {
		this.address = address;
		
		// 横、縦座標を算出
		byte[] location = getLocation(this.address); 
		x = location[0];
		y = location[1];
		
		// 石の状態を初期化
		this.color = STONE_COLOR.NONE;
		
		// 操作内容を初期化
		this.operation = OPERATION.PUT;
	}

	// コンストラクタ
	// x,y座標より初期化する
	public BoardCell(byte x, byte y) {
		this.x = x;
		this.y = y;
		
		// 絶対座標を算出
		this.address = (byte)(x + (y << 3));
		
		// 石の状態を初期化
		this.color = STONE_COLOR.NONE;
		
		// 操作内容を初期化
		this.operation = OPERATION.PUT;
	}

	// x座標を返す
	public byte getX() {
		return x;
	}
	
	// y座標を返す
	public byte getY() {
		return y;
	}
	
	// マスの座標を返す
	public byte getAddress() {
		return address;
	}

	// 横、縦座標を算出する
	private byte[] getLocation(byte address) {
		// 横、縦座標を算出
		byte x = (byte) (address & 0x07);	// 8で割った剰余が横座標
		byte y = (byte) (address >> 3);		// 8で割った商が縦座標

		// 戻り値を返す
		byte[] location = {x, y};
		return location;
	}
	
	// 移動先のアドレスを取得
	// 盤外にはみ出す場合は-1を返す
	public byte findNext(NEXT_DIRECTION direction) {
		// 移動先のアドレスを算出
		byte newAddress = (byte)(this.address + direction.getOffset());

		// 移動先のアドレスは0-63の範囲内
		if (newAddress < 0 || newAddress > 63) {
			return -1;
		}

		// 移動先のx,y値を取得
		byte newLocation[] = this.getLocation(newAddress);
		
		// 移動先のx,yが±1の範囲から外れる場合は盤面からはみ出している
		if (newLocation[0] < x-1 || x+1 < newLocation[0]) {
			// x方向のはみ出し
			return -1;
		} else if(newLocation[1] < y-1 || y+1 < newLocation[1]) {
			// y方向のはみ出し
			return -1;
		}
		
		// 移動先のアドレスを返す
		return newAddress;
	}
	
	// 自分自身のマス目が空いているかのチェック
	public boolean isBlank() {
		return (this.color == STONE_COLOR.NONE);
	}
	
	// 石を置く
	public boolean put(STONE_COLOR color) {
		// 石が置かれている場合はスキップ
		if (!this.isBlank()) {
			return false;
		}
		
		// 石を置く
		this.color = color;
		return true;
	}
	
	// 石をひっくり返す
	public boolean reverse(STONE_COLOR color) {
		if (this.color.getReverse() != color.getInt()) {
			// 反転した結果が自分の色とならない場合はひっくり返せない
			return false;
		}
		
		// 石をひっくり返す
		this.color = color;
		
		// 操作内容を設定
		this.operation = OPERATION.PUT;
		
		return true;
	}
	
	// 置かれている石の色を返す
	public STONE_COLOR getColor() {
		return this.color;
	}
	
	// 状態のコピーを返す
	@Override
	public BoardCell clone() {

		// インスタンスを生成
		BoardCell cloneCell = new BoardCell((byte) 0);

		// オブジェクトの内容をコピー
		cloneCell.x = this.x;
		cloneCell.y = this.y;
		cloneCell.address = this.address;
		cloneCell.color = this.color;

		return cloneCell;
	}
}
