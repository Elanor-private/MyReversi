package com.example.myreversi;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.example.myreversi.BoardCell.NEXT_DIRECTION;
import com.example.myreversi.BoardCell.STONE_COLOR;

public class BoardCondition {
	// �t�B�[���h��`
	private Map<Byte, BoardCell> blankField = null;		// �󂢂Ă���}�X
	private List<BoardCell> boardState = null;			// �{�[�h�̏��
	private STONE_COLOR stoneColor;						// ���
	private STONE_COLOR passPlayer;						// �Ō�Ƀp�X�����v���[���[
	private int blackCount = 0;							// ���̌�
	private int whiteCount = 0;							// ���̌�
	private STONE_COLOR winner;							// ����
	
	// �ȉ��A�J���x�Z�o�p�̒�`
	private Map<Byte, Integer> openCountMap = null;		// �J���x
	
	// �ȉ��A�m��ΎZ�o�p�̒�`
	private Map<Byte, BoardCell> fixedMap = null;		// �m���Map
	private Map<STONE_COLOR, List<BoardCell>> fixedList = null;	// �������ꂼ��̊m���

	// �Ֆʈ��k����(�m��ΎZ�o�Ɏg�p)
	private class CompressResult {
		private class CompressElement {
			private STONE_COLOR color = null;						// �}�X�̐F
			private List<BoardCell> cellList = null;				// ���k���ꂽ�}�X

			// �R���X�g���N�^
			// �Z�����󂯎���Ĉ��k���ʂ�������
			public CompressElement(BoardCell boardCell) {
				this.color = boardCell.getColor();
				this.cellList = new ArrayList<BoardCell>(8);
				this.cellList.add(boardCell.clone());
			}
			
			public STONE_COLOR getColor() {
				return this.color;
			}
			
			// ���k����
			public void add(BoardCell boardCell) {
				// �F�������ꍇ�݈̂��k���ꂽ�}�X�̃��X�g�ɒǉ�
				STONE_COLOR boardColor = boardCell.getColor();
				if (!STONE_COLOR.NONE.equals(boardColor) && this.color.equals(boardColor)) {
					// ���k���ꂽ�}�X�ɒǉ�
					cellList.add(boardCell);
				}
			}
			
			// ���k���ꂽ�}�X�̃��X�g��Ԃ�
			public List<BoardCell> getCells() {
				return this.cellList;
			}
		};
		
		// �t�B�[���h
		private List<CompressElement> compressResult = null;


		// �R���X�g���N�^
		public CompressResult() {
			// ���k���ʂ�������
			this.compressResult = new ArrayList<CompressElement>(8);
		}

		// ���k����
		public void compress(List<BoardCell> cellList) {
			// ���k�v�f
			CompressElement compressElement = null;
			
			// ���k���ʂ�������
			this.compressResult.clear();

			for (int i = 0; i < cellList.size(); i++) {
				BoardCell boardCell = cellList.get(i);
				
				if (this.compressResult.isEmpty() || STONE_COLOR.NONE.equals(boardCell.getColor())) {
					// ���X�g����A�܂��͋󔒃}�X�̏ꍇ�̓N���X�𐶐����Ēǉ�
					// ����͕K��������ʂ�
					compressElement = new CompressElement(boardCell);
					this.compressResult.add(compressElement);

				} else if (boardCell.getColor().equals(compressElement.getColor())) {
					// ����F�̏ꍇ�͊����̈��k���ʂ̃��X�g�ɒǉ�
					this.compressResult.get(this.compressResult.size()-1).add(boardCell);
					
				} else {
					// �F���قȂ�ꍇ�̓N���X�𐶐����Ēǉ�
					compressElement = new CompressElement(boardCell);
					this.compressResult.add(compressElement);
				}				
			}
		}
		
		// ���k���ʂ�n�Ԗڂ�Ԃ�
		// �͈͊O�̏ꍇ��null��Ԃ�
		public List<BoardCell> get(int index) {
			if (this.compressResult.size() <= index) {
				return null;
			} else {
				return this.compressResult.get(index).getCells();
			}
		}
		
		// ���k���ʂ̐F��Ԃ�
		// �͈͊O�̏ꍇ��null��Ԃ�
		public STONE_COLOR getColor(int index) {
			if (this.compressResult.size() <= index) {
				return null;
			} else {
				return this.compressResult.get(index).getColor();
			}
		}
		
		// ���k���ʂ̗v�f����Ԃ�
		public int size() {
			return this.compressResult.size();
		}
	};

	// �R���X�g���N�^
	public BoardCondition() {
		// �t�B�[���h�̃C���X�^���X�𐶐�
		this.blankField = new HashMap<Byte, BoardCell>(64);
		this.boardState = new ArrayList<BoardCell>(64);
		this.fixedMap = new HashMap<Byte, BoardCell>(64);
		this.fixedList = new HashMap<STONE_COLOR, List<BoardCell>>(64);
		this.openCountMap = new HashMap<Byte, Integer>(64);
		
		// �t�B�[���h�̏�����
		this.initFields();
		
		this.blackCount = 0;
		this.whiteCount = 0;
		this.passPlayer = STONE_COLOR.NONE;
	}
	
	// �{�[�h�̏�Ԃ�Ԃ�
	public List<BoardCell> getBoardState() {
		return boardState;
	}

	// ���̌���Ԃ�
	public int getBlackCount() {
		return blackCount;
	}

	// ���̌���Ԃ�
	public int getWhiteCount() {
		return whiteCount;
	}

	// ��Ԃ�Ԃ�
	public STONE_COLOR getStoneColor() {
		return this.stoneColor;
	}

	// �p�X������Ԃ�Ԃ�
	public STONE_COLOR getPassPlayer() {
		return passPlayer;
	}

	// ��Ԃ�ݒ�
	public void setStoneColor(STONE_COLOR stoneColor) {
		this.stoneColor = stoneColor;
	}

	// ���҂��擾
	public STONE_COLOR getWinner() {
		return winner;
	}

	// �Q�[����������Ԃɂ���
	public void doInit() {
	
		// �t�B�[���h��������
		this.initFields();

		// �΂̔z�u
		BoardCell boardCell = null;

		// ���΂̔z�u
		boardCell = new BoardCell((byte)4, (byte)3);
		boardCell.put(STONE_COLOR.BLACK);
		this.setCell(boardCell);

		boardCell = new BoardCell((byte)3, (byte)4);
		boardCell.put(STONE_COLOR.BLACK);
		this.setCell(boardCell);
		
		// ���΂̔z�u
		boardCell = new BoardCell((byte)3, (byte)3);
		boardCell.put(STONE_COLOR.WHITE);
		this.setCell(boardCell);

		boardCell = new BoardCell((byte)4, (byte)4);
		boardCell.put(STONE_COLOR.WHITE);
		this.setCell(boardCell);
		
		// ��Ԃ����ɐݒ�
		this.stoneColor = STONE_COLOR.BLACK;
	}
	
	// �t�B�[���h������
	private void initFields() {

		// �Q�[����Ԃ�������
		this.boardState.clear();
		this.blankField.clear();

		for (byte i = 0; i < 64; i++) {
			// �Ֆʂ��N���A
			// 64�}�X�u�����N�ŏ�����
			this.boardState.add(new BoardCell(i));

			// �󂫃}�X��������
			BoardCell boardCell = this.boardState.get(i);
			boardCell.put(STONE_COLOR.NONE);
			this.blankField.put(i, boardCell);

			// �J���x��������
			// 4��=3  ��=5 ���̑�=8
			if (i == 0 || i == 7 || i == 56 || i == 63) {
				// 4��
				this.openCountMap.put(i, 3);
			} else if ((i & 0x07) == 0 || (i & 0x07) == 7) {
				// ���E�̕�
				this.openCountMap.put(i, 5);
			} else if ((i & 0x38) == 0 || (i & 0x38) == 0x38) {
				// �㉺�̕�
				this.openCountMap.put(i, 5);
			} else {
				// ���A�ӈȊO
				this.openCountMap.put(i, 8);
			}
		}

		// �m��΂̏���������
		this.fixedMap.clear();
		this.fixedList.clear();
	}
	
	// �Ђ�����Ԃ���΂̃��X�g��Ԃ�
	//   ����s�̏ꍇnull��Ԃ�
	public Map<NEXT_DIRECTION, List<BoardCell>> getReversible(byte address, STONE_COLOR color) {
		// �߂�l�̐錾
		// �}�b�v�T�C�Y�́A6��/�����~8����=48
		Map<NEXT_DIRECTION, List<BoardCell>> retMap = new HashMap<NEXT_DIRECTION, List<BoardCell>>(48);

		// �w��ʒu���N�_�Ƃ��đS�����𑖍�
		for (NEXT_DIRECTION nextDirection : NEXT_DIRECTION.values()) {
			// �Ђ�����Ԃ���΂̃��X�g��������
			//  1�����ɂ͍ő�6�΂����Ђ�����Ԃ�Ȃ�
			List<BoardCell> reversibleStone = new ArrayList<BoardCell>(6);

			// ���݈ʒu�̃Z�������o��
			BoardCell boardCell = this.boardState.get((int)address);

			// findNext���Ȃ��瑊��̐F�����m�F
			byte nextAddress;
			for (nextAddress = boardCell.findNext(nextDirection); 
					nextAddress != -1;
					nextAddress = boardCell.findNext(nextDirection)) {
				
				// �Z���̈ʒu���X�V
				// -1�̏ꍇ�̓��[�v�𔲂���̂ł����ɂ͓���Ȃ�
				boardCell = this.boardState.get((int)nextAddress);
				
				// �Z���̐F�𔻒�
				if (boardCell.isBlank()) {
					// �u����Ă��Ȃ��ꍇ�͒���s��
					reversibleStone.clear();
					break;
				} else if (boardCell.getColor().getReverse() == color.getInt()) {
					// ����̐F�̏ꍇ�͒u����
					reversibleStone.add(boardCell);
				} else {
					// �����̐F������ꍇ�̓��[�v�𔲂���(���̐�͑������Ȃ�)
					break;
				}
			}
			
			// �u����ꍇ���Ђ�����Ԃ���ꍇ��Map�ɒǉ�
			if (nextAddress != -1 && !reversibleStone.isEmpty()) {
				retMap.put(nextDirection, reversibleStone);
			}
		}
		
		// Map����̏ꍇ��Null��Ԃ�
		if (retMap.isEmpty()) {
			retMap = null;
		} 
		
		return retMap;
	}

	// ���݂̎�Ԃ̒���\�ӏ������߂�
	// �I�ǎ���null��Ԃ�
	public List<BoardCell> getPuttableCell() {
		return this.getPuttableCell(this.stoneColor);
	}
	
	// �w�肳�ꂽ�F�̒���\�ӏ������߂�
	public List<BoardCell> getPuttableCell(STONE_COLOR stoneColor) {
		List<BoardCell> boardCellList = new ArrayList<BoardCell>(64);
		
		// �󂫃}�X���Ȃ��ꍇ�͏I��
		if (this.blankField.isEmpty()) {
			return null;
		}
		
		// ���ׂĂ̋󂫃}�X�ɑ΂��Ē���ۂ𔻒�
		for (BoardCell boardCell : this.blankField.values()) {
			if (getReversible(boardCell.getAddress(), stoneColor) != null) {
				// ����\�ӏ������������̂Ń}�X��ǉ�
				boardCellList.add(boardCell);
			}
		}
		
		// �擾���ʂ�Ԃ�
		return boardCellList;
	}
	
	// �΂�u��
	public Map<NEXT_DIRECTION, List<BoardCell>> put(byte address) {
		// ���ɐ΂��u����Ă���ꍇ�͒u���Ȃ�
		if (!STONE_COLOR.NONE.equals(this.boardState.get(address).getColor())) {
			return null;
		}
		
		// �Ђ�����Ԃ���΂̃��X�g���擾
		Map<NEXT_DIRECTION, List<BoardCell>> reversibleMap = getReversible(address, this.stoneColor);
		
		if (reversibleMap == null) {
			return null;
		}
		
		// ���̏ꏊ�ɐ΂�u��
		this.boardState.get(address).put(this.stoneColor);
		
		// �e�������Ђ�����Ԃ�
		for (List<BoardCell> reversibleList : reversibleMap.values()) {
			this.reverse(reversibleList);
		}

		// �΂�u�����}�X���󂫃}�X����폜
		this.blankField.remove(address);

		// ��Ԃ�ς���
		this.switchPlayer();

		// �p�X����
		List<BoardCell> puttableList = this.getPuttableCell(this.stoneColor);
		if (puttableList == null || puttableList.size() == 0) {
			// ����̒���\�ӏ����Ȃ��ꍇ
			// �p�X������Ԃ�ݒ�
			this.passPlayer = this.stoneColor;
			// ��Ԃ�ς���
			this.switchPlayer();
		} else {
			// �p�X������Ԃ����Z�b�g
			this.passPlayer = STONE_COLOR.NONE;
		}

		// ���͂̊J���x���X�V
		this.updateOpenCount(this.boardState.get(address));

		// �m��΂������X�V
		this.updateFixedCell(false);
		
		// ���A���̌����X�V
		this.blackCount = this.countStone(STONE_COLOR.BLACK);
		this.whiteCount = this.countStone(STONE_COLOR.WHITE);

		// �I�ǔ���Ə��҂̐ݒ�
		this.setWinner();
		
		return reversibleMap;
	}
	
	// ���͂̋󂫃}�X�����J�E���g
	// �J���x=sum(�Ђ�����Ԃ�}�X�̋󂫃}�X��)
	public int countBlankCell(byte address) {
		int			blankCount = 0;
		BoardCell	boardCell = this.boardState.get(address);
		
		// ���͂̋󂫃}�X�����J�E���g
		for (NEXT_DIRECTION nextDirection : NEXT_DIRECTION.values()) {
			byte nextAddress = boardCell.findNext(nextDirection);
			
			if (nextAddress != -1 && this.blankField.containsKey(nextAddress)) {
				// �󂫃}�X�̏ꍇ�C���N�������g
				blankCount++;
			}
		}
		
		// �v�Z���ʂ�Ԃ�
		return blankCount;
	}
	
	// �I�ǔ���Ə��҂̐ݒ�
	private void setWinner() {

		// �I�ǔ���
		if (this.isFinished()) {
			// �I�ǂ̏ꍇ�A���҂�ݒ�
			if (this.blackCount > this.whiteCount) {
				// ���̏���
				this.winner = STONE_COLOR.BLACK;
			} else if (this.blackCount < this.whiteCount) {
				// ���̏���
				this.winner = STONE_COLOR.WHITE;
			} else {
				// ��������
				this.winner = STONE_COLOR.NONE;
			}
		} else {
			// �΋ǒ��͏��Җ��ݒ�
			this.winner = null;
		}
	}
	
	// �}�X�ڂ�P���z�u(�Ђ�����Ԃ��Ȃ�)
	public void setCell(BoardCell boardCell) {
		// �}�b�v���̃}�X�ڂ�u������
		this.boardState.set(boardCell.getAddress(), boardCell);

		// �󂫃}�X�̍X�V
		if ( STONE_COLOR.NONE.equals(boardCell.getColor()) ) {
			// �󂫃}�X�̒ǉ�
			this.blankField.putIfAbsent(boardCell.getAddress(), boardCell);
		} else {
			// �󂫃}�X����폜
			this.blankField.remove(boardCell.getAddress());
		}

		// ���͂̊J���x���X�V
		this.updateOpenCount(boardCell);
		
		// �m��΂�S�X�V
		this.updateFixedCell(true);
		
		// ���A���̌����X�V
		this.blackCount = this.countStone(STONE_COLOR.BLACK);
		this.whiteCount = this.countStone(STONE_COLOR.WHITE);

		// �I�ǔ���Ə��҂̐ݒ�
		this.setWinner();
	}
	
	// ��������Ђ�����Ԃ�
	private void reverse(List<BoardCell> reversibleList) {
		for (BoardCell boardCell : reversibleList) {
			// �Ђ�����Ԃ�
			this.boardState.get(boardCell.getAddress()).reverse(this.stoneColor);
		}
	}
	
	// ��Ԃ�ς���
	public void switchPlayer() {
		this.stoneColor = (this.stoneColor == STONE_COLOR.BLACK) ? STONE_COLOR.WHITE : STONE_COLOR.BLACK;
	}
	
	// �I�ǔ���
	public boolean isFinished() {
		// ���A�����ꂼ��̒���\�ӏ������擾
		List<BoardCell> black = this.getPuttableCell(STONE_COLOR.BLACK);
		List<BoardCell> white = this.getPuttableCell(STONE_COLOR.WHITE);

		// �I�ǔ���
		if (black == null || white == null) {
			// �����ꂩ��null�̏ꍇ�͏I��
			return true;
		} else if (black.size() == 0 && white.size() == 0) {
			// ���A���Ƃ�����\�ӏ���������ΏI��
			return true;
		}
		
		// ���A�������ꂩ�͒���\�Ȃ̂ŏI�ǂł͂Ȃ�
		return false;
	}
	
	// �΂̌���Ԃ�
	public int countStone(STONE_COLOR color) {
		int countValue = 0;
		
		for (BoardCell boardCell : this.boardState) {
			if (boardCell.getColor().equals(color)) {
				countValue++;
			}
		}
		
		return countValue;
	}
	
	// �m��΂̏����X�V
	//   ������true���n���ꂽ�ꍇ�̓t�B�[���h�̊m���Map�����������čČv�Z����
	private void updateFixedCell(boolean initFixedMap) {

		//*************************************************
		// ��������
		//*************************************************
		if (initFixedMap) {
			// �m���Map�A�����̊m��΃��X�g��������
			this.fixedMap.clear();
			this.fixedList.clear();
		}

		if (this.fixedList.size() < 2) {
			// �����̊m��΃��X�g�����ݒ�̏ꍇ�͍����̃��X�g���Đݒ�
			this.fixedList.put(STONE_COLOR.BLACK, new ArrayList<BoardCell>(64));
			this.fixedList.put(STONE_COLOR.WHITE, new ArrayList<BoardCell>(64));
		}
		
		
		//*************************************************
		// ������
		//*************************************************
		// �m��΃`�F�b�N�pMap
		Map<Byte, BoardCell> checkMap = new HashMap<Byte, BoardCell>(64);

		// �`�F�b�N�pMap�Ƀ`�F�b�N�Ώۂ̃Z�����l�߂�
		for (BoardCell boardCell : this.boardState) {
			// �󔒃Z���łȂ��A���m���Map�Ɋ܂܂�Ă��Ȃ��ꍇ�̂݃`�F�b�N�Ώ�
			if (!STONE_COLOR.NONE.equals(boardCell.getColor()) && 
					!this.fixedMap.containsKey(boardCell.getAddress())) {
				checkMap.put(boardCell.getAddress(), boardCell);
			}
		}
		
		// �`�F�b�N�pMap����ɂȂ�܂Ń��[�v
		while (!checkMap.isEmpty()) {
			List<BoardCell> line = null;
			List<BoardCell> noFixedCellList = null;
			
			// Map�̐擪�v�f���擾
			BoardCell boardCell = (BoardCell)checkMap.values().toArray()[0];
			
			// �㉺�����̃`�F�b�N
			line = getVerticalLine(boardCell);
			noFixedCellList = this.getNoFixedCell(line);
			if (noFixedCellList != null && !noFixedCellList.isEmpty()) {
				// �m��΂łȂ��v�f��Map�����菜��
				for (BoardCell noFixedCell : noFixedCellList) {
					checkMap.remove(noFixedCell.getAddress());
				}
			}
			
			// ���E�����̃`�F�b�N
			line = getHorizontalLine(boardCell);
			noFixedCellList = this.getNoFixedCell(line);
			if (noFixedCellList != null && !noFixedCellList.isEmpty()) {
				// �m��΂łȂ��v�f��Map�����菜��
				for (BoardCell noFixedCell : noFixedCellList) {
					checkMap.remove(noFixedCell.getAddress());
				}
			}

			// �u���b�N���C��(�E�と����)�����̃`�F�b�N
			line = getBlackLine(boardCell);
			noFixedCellList = this.getNoFixedCell(line);
			if (noFixedCellList != null && !noFixedCellList.isEmpty()) {
				// �m��΂łȂ��v�f��Map�����菜��
				for (BoardCell noFixedCell : noFixedCellList) {
					checkMap.remove(noFixedCell.getAddress());
				}
			}
			
			// �z���C�g���C��(���と�E��)�����̃`�F�b�N
			line = getWhiteLine(boardCell);
			noFixedCellList = this.getNoFixedCell(line);
			if (noFixedCellList != null && !noFixedCellList.isEmpty()) {
				// �m��΂łȂ��v�f��Map�����菜��
				for (BoardCell noFixedCell : noFixedCellList) {
					checkMap.remove(noFixedCell.getAddress());
				}
			}
			
			// 4�����Ƃ��������߂Ȃ��΂ł���Ίm���
			if (checkMap.containsKey(boardCell.getAddress())) {
				
				// �m���Map�֒ǉ�
				this.fixedMap.put(boardCell.getAddress(), boardCell);
				
				// ���܂��͔��̊m��΃��X�g�֒ǉ�
				this.fixedList.get(boardCell.getColor()).add(boardCell);
			}
			
			// �`�F�b�N�ς݂̐΂��`�F�b�N�pMap�����菜��
			// (����Ȃ��Ɩ������[�v����̂�)
			checkMap.remove(boardCell.getAddress());
		}
	}

	// �㉺�����̔Ֆʃ��X�g���擾
	private List<BoardCell> getVerticalLine(BoardCell targetCell) {
		return getLine(
				targetCell, 
				NEXT_DIRECTION.UPPER_VERTICAL, 
				NEXT_DIRECTION.LOWER_VERTICAL);
	}

	// ���E�����̔Ֆʃ��X�g���擾
	private List<BoardCell> getHorizontalLine(BoardCell targetCell) {
		return getLine(
				targetCell, 
				NEXT_DIRECTION.HORIZONTAL_LEFT, 
				NEXT_DIRECTION.HORIZONTAL_RIGHT);
	}
	
	// �u���b�N���C�������̔Ֆʃ��X�g���擾
	private List<BoardCell> getBlackLine(BoardCell targetCell) {
		return getLine(
				targetCell, 
				NEXT_DIRECTION.UPPER_RIGHT, 
				NEXT_DIRECTION.LOWER_LEFT);
	}
	
	// �z���C�g���C�������̔Ֆʃ��X�g���擾
	private List<BoardCell> getWhiteLine(BoardCell targetCell) {
		return getLine(
				targetCell, 
				NEXT_DIRECTION.UPPER_LEFT, 
				NEXT_DIRECTION.LOWER_RIGHT);
	}

	// �[����̃��X�g���쐬���鋤�ʃ��\�b�h
	private List<BoardCell> getLine(
			BoardCell targetCell, 
			NEXT_DIRECTION backward, 
			NEXT_DIRECTION forward
			) {
		
		byte tmpAddress = targetCell.getAddress();
		BoardCell boardCell = targetCell;
		List<BoardCell> resultLine = new ArrayList<BoardCell>(8);

		
		// �O�����ɒ[�܂ő���
		while (tmpAddress != -1) {
			// ���̃A�h���X���擾
			tmpAddress = boardCell.findNext(backward);

			if (tmpAddress != -1) {
				// �ׂ̃}�X������ꍇ�͎��̃}�X���擾
				boardCell = this.boardState.get(tmpAddress);
			}
		}

		// �������ɒ[�܂ő���
		tmpAddress = boardCell.getAddress();
		resultLine.add(boardCell);

		while (tmpAddress != -1) {
			// ���̃A�h���X���擾
			tmpAddress = boardCell.findNext(forward);

			if (tmpAddress != -1) {
				// �ׂ̃}�X������ꍇ�͎��̃}�X���擾���Ēǉ�
				boardCell = this.boardState.get(tmpAddress);
				resultLine.add(boardCell);
			}
		}
		
		return resultLine;
	}
	
	
	// 1�����ɑ΂���Ֆʈ��k�ƃ`�F�b�N����
	// �m��΂ł͂Ȃ��}�X�̃��X�g��Ԃ�
	public List<BoardCell> getNoFixedCell(List<BoardCell> line) {
		// ���[�N�ϐ�
		List <BoardCell> nonFixedCell = new ArrayList<BoardCell>(16);		// �m��ΈȊO�̃��X�g
		CompressResult compressResult = new CompressResult();	// ���k����
		int 			blankCount = 0;							// �󔒃}�X�̐�
		int 			compressEndPos = 0;						// ���k���ʂ̏I�[

		
		if (line == null || line.size() <= 2) {
			// ���̓��X�g��2�}�X�ȉ��̏ꍇ�͂Ђ�����Ԃ��Ȃ��̂Ń`�F�b�N���Ȃ�
			return nonFixedCell;
		}
		
		// �Ֆʂ����k
		compressResult.compress(line);
		compressEndPos = compressResult.size() - 1;

		// �`�F�b�N����(������)
		if (STONE_COLOR.NONE.equals(compressResult.getColor(0))) {
			// �擪���󔒃Z���̏ꍇ�̓J�E���g��1����n�߂�
			blankCount = 1;
		} 
		
		// ���[�͊m��΂܂��͋󔒃}�X�Ȃ̂Ń��[�v����O��
		for (int i = 1; i < compressEndPos; i++) {
			if (STONE_COLOR.NONE.equals(compressResult.getColor(i))) {
				// �󔒃}�X�̓J�E���g�̂�
				blankCount++;
				continue;

			} else {
				// �΂̒u����Ă���Z���̏ꍇ�A���̈ʒu���������m��΂ł͂Ȃ��Ȃ�
				for (int j = 0; j < blankCount; j++) {
					// ��[�ɒB�����ꍇ�̓��[�v��ł��؂�(��[�͏�Ɋm��΂���)
					if ( (i+j) == (compressEndPos) ) {
						break;
					}
					
					if (!STONE_COLOR.NONE.equals(compressResult.getColor(i+j))) {
						// �ΏۃZ�����󔒂łȂ��ꍇ�͖߂�l�ɒǉ�
						nonFixedCell.addAll(compressResult.get(i+j));
					} else {
						// �ΏۃZ�����󔒂̏ꍇ�̓��[�v��ł��؂�
						break;
					}
				}
				
				// �󔒃}�X�ȊO�����o�����̂ŁA�󔒃J�E���g�����Z�b�g
				blankCount = 0;
			}
		}


		// �`�F�b�N����(������)
		if (STONE_COLOR.NONE.equals(compressResult.getColor(compressEndPos))) {
			// �������󔒃Z���̏ꍇ�̓J�E���g��1����n�߂�
			blankCount = 1;
		} else {
			// �������󔒂łȂ��ꍇ�̓J�E���g��0����n�߂�
			blankCount = 0;
		}
		
		// ���[�͊m��΂܂��͋󔒃}�X�Ȃ̂Ń��[�v����O��
		for (int i = compressEndPos - 1; i > 0; i--) {
			if (STONE_COLOR.NONE.equals(compressResult.getColor(i))) {
				// �󔒃}�X�̓J�E���g�̂�
				blankCount++;
				continue;

			} else {
				// �΂̒u����Ă���Z���̏ꍇ�A���̈ʒu����O�����m��΂ł͂Ȃ��Ȃ�
				for (int j = 0; j < blankCount; j++) {
					// �O�[�ɒB�����ꍇ�̓��[�v��ł��؂�(�O�[�͏�Ɋm��΂���)
					if ( i <= j ) {
						break;
					}

					if (!STONE_COLOR.NONE.equals(compressResult.getColor(i-j))) {
						// �ΏۃZ�����󔒂łȂ��ꍇ�͖߂�l�ɒǉ�
						nonFixedCell.addAll(compressResult.get(i-j));
					} else {
						// �ΏۃZ�����󔒂̏ꍇ�̓��[�v��ł��؂�
						break;
					}
				}
				
				// �󔒃}�X�ȊO�����o�����̂ŁA�󔒃J�E���g�����Z�b�g
				blankCount = 0;
			}
		}
		
		// �߂�l��Ԃ�
		return nonFixedCell;
	}
	
	// �J���x�̍X�V
	private void updateOpenCount(BoardCell boardCell) {
		for (NEXT_DIRECTION nextDirection : NEXT_DIRECTION.values()) {
			// �אڂ���}�X���擾
			byte neighborAddress = boardCell.findNext(nextDirection);
			
			if (neighborAddress != -1) {
				// �אڂ���}�X�����݂���ꍇ�͊J���x���X�V
				Integer openCount = this.openCountMap.get(neighborAddress);
				openCount--;
				this.openCountMap.put(neighborAddress, openCount);
			}
		}
	}
	
	// �ǖʂ�]��
	// �����猩���]���l��Ԃ�
	public int validate() {
		
		// �]���l
		int blackValue = this.validate(STONE_COLOR.BLACK);	// ���̕]���l
		int whiteValue = this.validate(STONE_COLOR.WHITE);	// ���̕]���l
		
		return blackValue - whiteValue;
	}
	
	// �]���l�Z�o�̃R�A���W�b�N
	private int validate(STONE_COLOR color) {
		
		int validateValue = 0;				// �]���l
		List <BoardCell> cellList = null;	// Map�擾�p���[�N�ϐ�
		
		final int fixedWeight = 5;			// �m��΂ɑ΂���d��
		
		
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
		
		// �Z�o���ʂ�Ԃ�
		return validateValue;
	}
	
	// �Q�[���Ղ̃R�s�[�𐶐�
	@Override
	public BoardCondition clone() {
		// �C���X�^���X�𐶐�
		BoardCondition cloneBoardCondition = new BoardCondition();
		
		// �󂫃}�X�����R�s�[
		cloneBoardCondition.blankField.clear();
		for (Map.Entry<Byte, BoardCell> entry : this.blankField.entrySet()) {
			cloneBoardCondition.blankField.put(entry.getKey(), entry.getValue().clone());
		}
		
		// �{�[�h�̏�Ԃ��R�s�[
		cloneBoardCondition.boardState.clear();
		for (BoardCell boardCell : this.boardState) {
			cloneBoardCondition.boardState.add(boardCell.clone());
		}
		
		// ��Ԃ�ݒ�
		cloneBoardCondition.stoneColor = this.stoneColor;

		// �p�X������Ԃ�ݒ�
		cloneBoardCondition.passPlayer = this.passPlayer;
		
		// ���A���̌���ݒ�
		cloneBoardCondition.blackCount = this.blackCount;
		cloneBoardCondition.whiteCount = this.whiteCount;

		// ���҂�ݒ�
		cloneBoardCondition.winner = this.winner;
		
		return cloneBoardCondition;
	}
}
