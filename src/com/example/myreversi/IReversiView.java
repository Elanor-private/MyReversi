/**
 * IReversiView�C���^�[�t�F�[�X
 * �r���[�Ɉˑ�����\���������C���^�[�t�F�[�X�Ƃ��Đ錾����
 */
package com.example.myreversi;

import com.example.myreversi.BoardCell.STONE_COLOR;

/**
 * @author IT-career
 *
 */
public interface IReversiView {
	// �u�{�[�h�̏������v���\�b�h(�`����@�ɂ�菉�������e���قȂ邽�ߌʎ������K�v)
	public void doInit();

	// �u�΂�u���v���\�b�h(�`����@���قȂ邽�ߌʎ������K�v)
	public void putStone(byte x, byte y, STONE_COLOR color);

	// �u�Ђ�����Ԃ��v���\�b�h(�`����@�ɂ����e���قȂ�)
	public void reverse(BoardCell boardCell, STONE_COLOR color);
	
	// �u�㏈���v���\�b�h(�`����@�ɂ��K�v�Ȍ㏈�����قȂ�)
	public void doPost();

	// �u���͑҂���Ԃֈڍs�v���\�b�h(�`����@�ɂ��҂������قȂ�)
	public BoardCell waitForInput();
	
	// �u�Q�[���󋵂̕\���v���\�b�h(�`����@�ɂ��\���������قȂ�)
	public void showCondition(BoardCondition boardCondition);

}
