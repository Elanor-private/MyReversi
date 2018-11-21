package com.example.myreversi;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.example.myreversi.BoardCell.NEXT_DIRECTION;
import com.example.myreversi.BoardCell.STONE_COLOR;

public class BoardCondition {
	// �t�B�[���h��`
	private Map<Byte, BoardCell> blankField = null;	// �󂢂Ă���}�X
	private List<BoardCell> boardState = null;		// �{�[�h�̏��
	private STONE_COLOR stoneColor;					// ���
	private STONE_COLOR passPlayer;					// �Ō�Ƀp�X�����v���[���[
	private int blackCount = 0;						// ���̌�
	private int whiteCount = 0;						// ���̌�
	private STONE_COLOR winner;						// ����


	// �R���X�g���N�^
	public BoardCondition() {
		// �󂫃}�X�̏�����
		this.blankField = new HashMap<Byte, BoardCell>(64);

		// �{�[�h��Ԃ̏�����
		this.boardState = new ArrayList<BoardCell>(64);
		for (byte i = 0; i < 64; i++) {
			// 64�}�X�u�����N�ŏ�����
			this.boardState.add(new BoardCell(i));
		}
		
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
		// �Ֆʂ��N���A
		this.blankField.clear();
		for (byte i = 0; i < 64; i++) {
			BoardCell boardCell = this.boardState.get(i);
			boardCell.put(STONE_COLOR.NONE);
			this.blankField.put(i, boardCell);
		}
		
		// ���΂̔z�u
		this.boardState.get(4 + 3*8).put(STONE_COLOR.BLACK);
		this.boardState.get(3 + 4*8).put(STONE_COLOR.BLACK);

		// ���΂̔z�u
		this.boardState.get(3 + 3*8).put(STONE_COLOR.WHITE);
		this.boardState.get(4 + 4*8).put(STONE_COLOR.WHITE);

		// �󂫃}�X�𒲐�
		this.blankField.remove((byte)(4 + 3*8));
		this.blankField.remove((byte)(3 + 4*8));
		this.blankField.remove((byte)(3 + 3*8));
		this.blankField.remove((byte)(4 + 4*8));
		
		// ��Ԃ����ɐݒ�
		this.stoneColor = STONE_COLOR.BLACK;

		// ���A���̌���������(4�ƕ�����؂��Ă��邪�c)
		this.blackCount = this.countStone(STONE_COLOR.BLACK);
		this.whiteCount = this.countStone(STONE_COLOR.WHITE);
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
		if (this.boardState.get(address).getColor() != STONE_COLOR.NONE) {
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

		// ���A���̌����X�V
		this.blackCount = this.countStone(STONE_COLOR.BLACK);
		this.whiteCount = this.countStone(STONE_COLOR.WHITE);

		// �I�ǔ���Ə��҂̐ݒ�
		this.setWinner();
		
		return reversibleMap;
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
		if ( boardCell.getColor() == STONE_COLOR.NONE ) {
			// �󂫃}�X�̒ǉ�
			this.blankField.putIfAbsent(boardCell.getAddress(), boardCell);
		} else {
			// �󂫃}�X����폜
			this.blankField.remove(boardCell.getAddress());
		}
		
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
			if (boardCell.getColor() == color) {
				countValue++;
			}
		}
		
		return countValue;
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
