package com.example.myreversi;

import java.util.List;

import com.example.myreversi.BoardCell.STONE_COLOR;

// �Q�[���Ճr���[�̒��ۃN���X
// �u�{�[�h�̏������v�u�΂�u���v�u�Ђ�����Ԃ��v�𒊏ۃ��\�b�h�Ƃ��Ď���
public class ReversiViewLogic {

	// �t�B�[���h
	IReversiView reversiView = null;

	// �R���X�g���N�^
	public ReversiViewLogic(IReversiView reversiView) {
		this.reversiView = reversiView;
	}

	// �}�X�ڎw��Ő΂�u��
	public void putStone(BoardCell boardCell, STONE_COLOR color) {
		// ���W�n��x,y�ɕϊ����Ē��ۃ��\�b�h���Ăяo��
		reversiView.putStone(boardCell.getX(), boardCell.getY(), color);
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
			reversiView.reverse(boardCell, color);
		}
		
		// �㏈��
		reversiView.doPost();
	}
	
	// �u�S�z�u�v�͎����\�b�h(�u�΂�u���v���J��Ԃ�����)
	public void putAll(BoardCondition boardCondition) {
		// �Ֆʂ�������
		reversiView.doInit();

		// �΂�z�u
		for (BoardCell boardCell : boardCondition.getBoardState()) {
			STONE_COLOR color = boardCell.getColor();
			if (color != STONE_COLOR.NONE) {
				// �΂��u����Ă���ꍇ�̂ݔz�u
				putStone(boardCell, color);
			}
		}
		
		// �㏈��
		reversiView.doPost();
	}
}
