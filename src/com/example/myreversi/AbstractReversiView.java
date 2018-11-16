package com.example.myreversi;

import java.util.List;

import com.example.myreversi.BoardCell.STONE_COLOR;

// �Q�[���Ճr���[�̒��ۃN���X
// �u�{�[�h�̏������v�u�΂�u���v�u�Ђ�����Ԃ��v�𒊏ۃ��\�b�h�Ƃ��Ď���
public abstract class AbstractReversiView {

	// �u�{�[�h�̏������v�͒��ۃ��\�b�h(�`����@�ɂ�菉�������e���قȂ邽��)
	abstract protected void doInit();

	// �u�΂�u���v�͒��ۃ��\�b�h(�P�ɕ`�悷�邾���̂���)
	abstract protected void putStone(byte x, byte y, STONE_COLOR color);
	// �}�X�ڎw��Ő΂�u��
	public void putStone(BoardCell boardCell, STONE_COLOR color) {
		// ���W�n��x,y�ɕϊ����Ē��ۃ��\�b�h���Ăяo��
		putStone(boardCell.getX(), boardCell.getY(), color);
	}
	
	// �u�Ђ�����Ԃ��v�͎����\�b�h(�u�΂�u���v���J��Ԃ�����)
	// �Ђ�����Ԃ��Ώۂ̓r�W�l�X���W�b�N(BoardCondition)�œ��肷��O��Ƃ��A
	// �����ł�View�ւ̕\�����W�b�N�݂̂�����
	public void reverseAll(List<BoardCell>boardCellList, STONE_COLOR color) {
		// ���̓��X�g��null�̏ꍇ�͉������Ȃ�
		if (boardCellList == null) {
			return;
		}
		
		// ���X�g���̑Ώۂ��ׂĂ�`��
		for (BoardCell boardCell : boardCellList) {
			this.reverse(boardCell, color);
		}
		
		// �㏈��
		doPost();
	}
	
	// �u�S�z�u�v�͎����\�b�h(�u�΂�u���v���J��Ԃ�����)
	public void putAll(BoardCondition boardCondition) {
		// �Ֆʂ�������
		doInit();

		// �΂�z�u
		for (BoardCell boardCell : boardCondition.getBoardState()) {
			STONE_COLOR color = boardCell.getColor();
			if (color != STONE_COLOR.NONE) {
				// �΂��u����Ă���ꍇ�̂ݔz�u
				putStone(boardCell, color);
			}
		}
		
		// �㏈��
		doPost();
	}

	// �Ђ�����Ԃ������͒��ۃ��\�b�h(�`����@�ɂ����e���قȂ�)
	abstract protected void reverse(BoardCell boardCell, STONE_COLOR color);
	
	// �㏈���͒��ۃ��\�b�h(�`����@�ɂ��K�v�Ȍ㏈�����قȂ�)
	abstract protected void doPost();

	// ���͑҂���Ԃֈڍs(�`����@�ɂ��҂������قȂ�)
	abstract public BoardCell waitForInput();
	
	// �Q�[���󋵂̕\��(�`����@�ɂ��\���������قȂ�)
	abstract public void showCondition(BoardCondition boardCondition);
}
