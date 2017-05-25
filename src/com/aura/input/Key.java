package com.aura.input;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public enum Key {

	NZERO(0x60, "[Insert]", "0"),
	NONE(0x61, "[End]", "1"),
	NTWO(0x62,"[Down]", "2"), 
	NTHREE(0x63, "[Page down]", "3"), 
	NFOUR(0x64,"[Left]", "4"),
	NFIVE(0x65, "", "5"), 
	NSIX(0x66, "[Right]", "6"),
	NSEVEN(0x67, "[Home]", "7"),
	NEIGHT(0x68, "[Up]", "8"), 
	NNINE(0x69,"[Page up]", "9"),
	ZERO(0x30, ")", "0"), 
	ONE(0x31, "!", "1"), 
	TWO(0x32, "@", "2"), 
	THREE(0x33, "#", "3"),
	FOUR(0x34, "$", "4"), 
	FIVE(0x35, "%", "5"),
	SIX(0x36, "^", "6"),
	SEVEN(0x37, "&", "7"),
	EIGHT(0x38, "*", "8"),
	NINE(0x39, "(", "9"),
	A(0x41, "A", "a"),
	B(0x42, "B", "b"),
	C(0x43, "C", "c"), 
	D(0x44, "D","d"), 
	E(0x45, "E", "e"),
	F(0x46, "F", "f"),
	G(0x47, "G", "g"),
	H(0x48, "H", "h"),
	I(0x49, "I", "i"),
	J(0x4A, "J", "j"), 
	K(0x4B, "K", "k"), 
	L(0x4C, "L", "l"), 
	M(0x4D, "M", "m"),
	N(0x4E, "N","n"), 
	O(0x4F, "O", "o"),
	P(0x50, "P", "p"),
	Q(0x51, "Q", "q"),
	R(0x52, "R", "r"),
	S(0x53, "S", "s"),
	T(0x54, "T", "t"),
	U(0x55, "U", "u"),
	V(0x56, "V", "v"),
	W(0x57, "W", "w"),
	X(0x58, "X", "x"),
	Y(0x59, "Y", "y"),
	Z(0x5A, "Z", "z"),
	UP(KeyEvent.VK_UP, null, null),
	DOWN(KeyEvent.VK_DOWN, null, null),
	QUESTION(KeyEvent.VK_SLASH, "?", "/"),
	PERIOD(KeyEvent.VK_PERIOD, ">", "."), 
	TAB(KeyEvent.VK_TAB, null, null), 
	BACKSPACE(KeyEvent.VK_BACK_SPACE, null, null), 
	SPACE(KeyEvent.VK_SPACE, " ", " "),
	ENTER(KeyEvent.VK_ENTER, null, null),
	COMMA(KeyEvent.VK_COMMA, "<", ","), 
	MINUS(KeyEvent.VK_MINUS, "_", "-"), 
	ADD(KeyEvent.VK_ADD, "+", "="), 
	DASH(KeyEvent.VK_SLASH, "?", "/"),
	SEMICOLON(KeyEvent.VK_COLON, ":", ";"),
	TIDLE(0xC0, "~", "`"),
	LBRACKET(KeyEvent.VK_BRACELEFT, "{", "["), 
	BACKSLASH(KeyEvent.VK_BACK_SLASH, "|","\\"),
	RBRACKET(KeyEvent.VK_BRACERIGHT, "}", "]"),
	QOUTE(KeyEvent.VK_QUOTE, "\"", "'"), 
	MOUSE_CLICK(0x01, null, null), 
	CAPSLOCK(KeyEvent.VK_CAPS_LOCK, null, null),
	ESCAPE(KeyEvent.VK_ESCAPE, null, null),
	SHIFT(KeyEvent.VK_SHIFT, null, null), 
	CONTROL(KeyEvent.VK_CONTROL, null, null), 
	DELETE(KeyEvent.VK_DELETE, null, null);
	
	public static final HashMap<Integer, Key> KEYS = new HashMap<Integer, Key>();

	private Key(int indentifer, String shiftOutput, String output) {
		this.indentifer = indentifer;
		this.shiftOutput = shiftOutput;
		this.output = output;
	}

	private final int indentifer;
	private final String shiftOutput;
	private final String output;

	public int getIndentifer() {
		return indentifer;
	}

	public String getShiftOutput() {
		return shiftOutput;
	}

	public String getOutput() {
		return output;
	}
	
	static {
		for(Key key : values()) {
			KEYS.put(key.getIndentifer(), key);
		}
	}
};