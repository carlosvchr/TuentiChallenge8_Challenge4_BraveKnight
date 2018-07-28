package tc.braveknightchallenge.mainpk;

public class Vec2 {

	private int _row, _col;
	
	public Vec2(int row, int col) {
		_row = row;
		_col = col;
	}
	
	/** Get the row param. */
	public int row() {
		return _row;
	}
	
	/** Get the col param. */
	public int col() {
		return _col;
	}
	
	/** Compare if 2 Vec2 have the same row and col values */
	public boolean eq(Vec2 v) {
		return (_row==v._row && _col==v._col);
	}
	
}
