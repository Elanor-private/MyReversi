package com.example.myreversi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.example.myreversi.BoardCell.NEXT_DIRECTION;
import com.example.myreversi.BoardCell.STONE_COLOR;

public abstract class AbstractReversiLogic {

	// �t�B�[���h��`
	BoardCondition boardCondition = null;					// �Ֆʂ̏�
	STONE_COLOR prevStoneColor = STONE_COLOR.NONE;			// ���O�̎��
	Map<NEXT_DIRECTION, List<BoardCell>> prevReverseMap;	// ���O�ɂЂ�����Ԃ����΂�Map
	private Stack<BoardCondition> undoBuffer = null;		// undo�o�b�t�@
	private Stack<BoardCondition> redoBuffer = null;		// redo�o�b�t�@
	
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
		
		// undo,redo�o�b�t�@��������
		this.undoBuffer = new Stack<BoardCondition>();
		this.redoBuffer = new Stack<BoardCondition>();
	}
	
	// ����������
	public void doInit() {
		// �Ֆʂ�������
		this.boardCondition.doInit();

		// undo,redo�o�b�t�@��������
		this.undoBuffer.clear();
		this.undoBuffer.push(this.boardCondition.clone());
		this.redoBuffer.clear();
		
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
		
		// ����\�ȏꍇ�Aundo/redo�o�b�t�@��ݒ�
		if (prevReverseMap != null) {
			// �����̏�Ԃ̃R�s�[��undo�o�b�t�@�ɓ����
			this.undoBuffer.push(this.boardCondition.clone());

			// redo�o�b�t�@�̓N���A
			this.redoBuffer.clear();
			
			return true;
			
		} else {
			return false;
		}
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
	
	// undo�ۂ�Ԃ�
	public boolean canUndo() {
		if ( this.undoBuffer == null || this.undoBuffer.size() <= 1 ) {
			return false;
		} else {
			return true;
		}
	}
	
	// undo����
	public void doUndo() {
		// undo�ł��Ȃ��ꍇ�͉������Ȃ�
		if ( !this.canUndo() ) {
			return;
		}
		
		// undo�o�b�t�@�̖��������o��
		BoardCondition undoCondition = this.undoBuffer.pop();
		
		// ���o�����v�f��redo�o�b�t�@�ɒǉ�
		this.redoBuffer.push(undoCondition);
		
		// undo�o�b�t�@�̖�������Ֆʂ̏�Ԃ��擾
		undoCondition = this.undoBuffer.peek();
		
		// �Ֆʂ̏�Ԃ��t�B�[���h�ɍĐݒ�
		// �o�b�t�@���e������ŏ�����������i��Ԃ�ێ��ł��Ȃ��j���߃R�s�[��n��
		this.boardCondition = undoCondition.clone();
	}
	
	// redo�ۂ�Ԃ�
	public boolean canRedo() {
		if (this.redoBuffer == null || this.redoBuffer.size() == 0 ) {
			return false;
		} else {
			return true;
		}
	}
	
	// redo����
	public void doRedo() {
		// redo�ł��Ȃ��ꍇ�͉������Ȃ�
		if ( !this.canRedo() ) {
			return;
		}
		
		// redo�X�^�b�N�����o��
		BoardCondition redoCondition = this.redoBuffer.pop();
		
		// ���o�������e��undo�X�^�b�N�ɐݒ�
		this.undoBuffer.push(redoCondition);
		
		// �Ֆʂ̏�Ԃ��t�B�[���h�ɍĐݒ�
		// �o�b�t�@���e������ŏ�����������i��Ԃ�ێ��ł��Ȃ��j���߃R�s�[��n��
		this.boardCondition = redoCondition.clone();
	}
}
