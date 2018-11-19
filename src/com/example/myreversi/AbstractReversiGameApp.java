package com.example.myreversi;

import com.example.myreversi.BoardCell.STONE_COLOR;

public abstract class AbstractReversiGameApp {

	// �t�B�[���h��`
	protected AbstractReversiLogic	reversiLogic = null;		// �Q�[�����W�b�N
	protected ReversiViewLogic		reversiViewLogic = null;	// �r���[�̃R�A���W�b�N
	private   IReversiView 			reversiView = null;			// �r���[�̃C���^�t�F�[�X
	
	// �R���X�g���N�^
	public AbstractReversiGameApp(AbstractReversiLogic reversiLogic,
			IReversiView reversiView) {
		this.reversiLogic = reversiLogic;
		this.reversiView = reversiView;
		this.reversiViewLogic = new ReversiViewLogic(reversiView);
	}

	protected abstract void gameStart();
	
	// �A�v���P�[�V�������s
	public void run() {
		// ���W�b�N�N���X�̏�����
		reversiLogic.doInit();
		
		// �r���[�̏������Ə����Ֆʕ\��
		reversiViewLogic.putAll(reversiLogic.getBoardCondition());
		
		// �r���[�ɏ����̃Q�[����Ԃ�\��
		reversiView.showCondition(reversiLogic.getBoardCondition());

		// �Q�[���J�n
		gameStart();
	}
	
	// ���͑҂�
	protected void doPlay() {
		// Logic�N���X����Ֆʓ��́iAI�Ȃǁj
		BoardCell boardCell = reversiLogic.doThink();

		if (boardCell == null) {
			// ���̒���ӏ������W�b�N����擾�ł��Ȃ��ꍇ�͓��͑҂��Ɉڍs
			boardCell = reversiView.waitForInput();
		}

		if (boardCell != null) {
			// �u�΂�u���v���W�b�N�����s
			if (reversiLogic.doPut(boardCell)) {
				// �΂�u������̉�ʕ\��
				// �܂��͐΂�u��
				STONE_COLOR stoneColor = reversiLogic.getPrevStoneColor();
				reversiViewLogic.putStone(boardCell, stoneColor);
				// �Ђ�����Ԃ�
				reversiViewLogic.reverseAll(reversiLogic.getPrevReverseList(), stoneColor);
				// �Q�[���󋵂�\��
				reversiView.showCondition(reversiLogic.getBoardCondition());
			}
		}
	}
};
