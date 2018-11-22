// �Տ�̃}�X��
package com.example.myreversi;


public class BoardCell {
	// �Տ�̈ړ���
	public enum NEXT_DIRECTION {
		UPPER_LEFT(-9),				// ����
		UPPER_VERTICAL(-8),			// ��
		UPPER_RIGHT(-7),			// �E��
		HORIZONTAL_RIGHT(1),		// �E
		LOWER_RIGHT(9),				// �E��
		LOWER_VERTICAL(8),			// ��
		LOWER_LEFT(7),				// ����
		HORIZONTAL_LEFT(-1)			// ��
		;
		
		private final int direction;

		private NEXT_DIRECTION(int direction) {
			this.direction = direction;
		}
		
		// ���̈ړ��ʒu��Ԃ�
		public int getOffset() {
			return this.direction;
		}
	};
	
	
	// �Տ�̐΂̐F
	public enum STONE_COLOR {
		NONE(0),				// �u����Ă��Ȃ�
		BLACK(1),				// ��
		WHITE(2)				// ��
		;
		
		private final int color;

		private STONE_COLOR(int color) {
			this.color = color;
		}

		// �΂̐F��Ԃ�
		public int getInt() {
			return this.color;
		}

		// ���]�F��Ԃ�
		public int getReverse() {
			return (this.color ^ 0x03);
		}
	};

	// �Ֆʂ̑�����e
	public enum OPERATION {
		NONE,					// �������Ȃ�(�΂̒P���z�u�Ȃ�)
		PUT,					// �΂�u���ĂЂ�����Ԃ�
		UNDO,					// undo
		REDO					// redo
	};
	
	private	byte			x;			// �����W
	private byte			y;			// �c���W 
	private byte			address;	// 0-63�̘A��
	private STONE_COLOR		color;		// �u����Ă����		 	
	private OPERATION		operation;	// �Ֆʂ̑�����e
	
	
	public OPERATION getOperation() {
		return operation;
	}

	public void setOperation(OPERATION operation) {
		this.operation = operation;
	}

	// �R���X�g���N�^
	// �Ֆʂ̏��������̂݌Ăяo���O��
	public BoardCell(byte address) {
		this.address = address;
		
		// ���A�c���W���Z�o
		byte[] location = getLocation(this.address); 
		x = location[0];
		y = location[1];
		
		// �΂̏�Ԃ�������
		this.color = STONE_COLOR.NONE;
		
		// ������e��������
		this.operation = OPERATION.PUT;
	}

	// �R���X�g���N�^
	// x,y���W��菉��������
	public BoardCell(byte x, byte y) {
		this.x = x;
		this.y = y;
		
		// ��΍��W���Z�o
		this.address = (byte)(x + (y << 3));
		
		// �΂̏�Ԃ�������
		this.color = STONE_COLOR.NONE;
		
		// ������e��������
		this.operation = OPERATION.PUT;
	}

	// x���W��Ԃ�
	public byte getX() {
		return x;
	}
	
	// y���W��Ԃ�
	public byte getY() {
		return y;
	}
	
	// �}�X�̍��W��Ԃ�
	public byte getAddress() {
		return address;
	}

	// ���A�c���W���Z�o����
	private byte[] getLocation(byte address) {
		// ���A�c���W���Z�o
		byte x = (byte) (address & 0x07);	// 8�Ŋ�������]�������W
		byte y = (byte) (address >> 3);		// 8�Ŋ����������c���W

		// �߂�l��Ԃ�
		byte[] location = {x, y};
		return location;
	}
	
	// �ړ���̃A�h���X���擾
	// �ՊO�ɂ͂ݏo���ꍇ��-1��Ԃ�
	public byte findNext(NEXT_DIRECTION direction) {
		// �ړ���̃A�h���X���Z�o
		byte newAddress = (byte)(this.address + direction.getOffset());

		// �ړ���̃A�h���X��0-63�͈͓̔�
		if (newAddress < 0 || newAddress > 63) {
			return -1;
		}

		// �ړ����x,y�l���擾
		byte newLocation[] = this.getLocation(newAddress);
		
		// �ړ����x,y���}1�͈̔͂���O���ꍇ�͔Ֆʂ���͂ݏo���Ă���
		if (newLocation[0] < x-1 || x+1 < newLocation[0]) {
			// x�����̂͂ݏo��
			return -1;
		} else if(newLocation[1] < y-1 || y+1 < newLocation[1]) {
			// y�����̂͂ݏo��
			return -1;
		}
		
		// �ړ���̃A�h���X��Ԃ�
		return newAddress;
	}
	
	// �������g�̃}�X�ڂ��󂢂Ă��邩�̃`�F�b�N
	public boolean isBlank() {
		return (this.color == STONE_COLOR.NONE);
	}
	
	// �΂�u��
	public boolean put(STONE_COLOR color) {
		// �΂��u����Ă���ꍇ�̓X�L�b�v
		if (!this.isBlank()) {
			return false;
		}
		
		// �΂�u��
		this.color = color;
		return true;
	}
	
	// �΂��Ђ�����Ԃ�
	public boolean reverse(STONE_COLOR color) {
		if (this.color.getReverse() != color.getInt()) {
			// ���]�������ʂ������̐F�ƂȂ�Ȃ��ꍇ�͂Ђ�����Ԃ��Ȃ�
			return false;
		}
		
		// �΂��Ђ�����Ԃ�
		this.color = color;
		
		// ������e��ݒ�
		this.operation = OPERATION.PUT;
		
		return true;
	}
	
	// �u����Ă���΂̐F��Ԃ�
	public STONE_COLOR getColor() {
		return this.color;
	}
	
	// ��Ԃ̃R�s�[��Ԃ�
	@Override
	public BoardCell clone() {

		// �C���X�^���X�𐶐�
		BoardCell cloneCell = new BoardCell((byte) 0);

		// �I�u�W�F�N�g�̓��e���R�s�[
		cloneCell.x = this.x;
		cloneCell.y = this.y;
		cloneCell.address = this.address;
		cloneCell.color = this.color;

		return cloneCell;
	}
}
