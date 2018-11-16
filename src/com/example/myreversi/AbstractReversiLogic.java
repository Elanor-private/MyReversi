package com.example.myreversi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.myreversi.BoardCell.NEXT_DIRECTION;
import com.example.myreversi.BoardCell.STONE_COLOR;

public abstract class AbstractReversiLogic {

	// �t�B�[���h��`
	BoardCondition boardCondition = null;					// �Ֆʂ̏�
	STONE_COLOR prevStoneColor = STONE_COLOR.NONE;			// ���O�̎��
	Map<NEXT_DIRECTION, List<BoardCell>> prevReverseMap;	// ���O�ɂЂ�����Ԃ����΂�Map
	
	// �������p�̒��ۃ��\�b�h
	// (AI�̏������Ȃǂ��s���j
	protected abstract void doInitExtend();

	// ���̒���ӏ����擾
	// AI���W�b�N�łȂ��ꍇ��null��Ԃ�����
	public abstract BoardCell doThink();
	
	// �R���X�g���N�^
	// ���o�[�V���ʂ̃C���X�^���X�������s��
	public AbstractReversiLogic() {
		// �Ֆʂ̃C���X�^���X�𐶐�
		this.boardCondition = new BoardCondition();
	}
	
	// ����������
	public void doInit() {
		// �Ֆʂ�������
		boardCondition.doInit();

		// �Q�[���ʂ̏��������������s
		doInitExtend();
	}
	
	// �I�ǔ���
	public boolean isFinished() {
		return this.boardCondition.isFinished();
	}
	
	// �΂�u���ĂЂ�����Ԃ�
	public boolean doPut(BoardCell boardCell) {
		// ���O�̎�Ԃ�ޔ�
		this.prevStoneColor = this.boardCondition.getStoneColor();
		
		// �΂�u���ĂЂ�����Ԃ�
		prevReverseMap = this.boardCondition.put(boardCell.getAddress());
		return (prevReverseMap != null);
	}

	// �ŐV�̃Q�[����Ԃ�Ԃ�
	public BoardCondition getBoardCondition() {
		return boardCondition;
	}

	// ���O�ɂЂ�����Ԃ����΂̃��X�g��Ԃ�
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

	// ���O�̎�Ԃ�Ԃ�
	public STONE_COLOR getPrevStoneColor() {
		return prevStoneColor;
	}
}
