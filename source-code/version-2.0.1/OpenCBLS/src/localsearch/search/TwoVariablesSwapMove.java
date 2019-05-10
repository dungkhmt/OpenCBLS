package localsearch.search;

import localsearch.model.*;
public class TwoVariablesSwapMove extends Move{

	private VarIntLS	_x;
	private VarIntLS	_y;
	
	public TwoVariablesSwapMove(MoveType type, double eval, VarIntLS x, VarIntLS y){
		super(type,eval);
		_x = x; _y = y;
	}
	public VarIntLS getVar1(){ return _x;}
	public VarIntLS getVar2(){ return _y;}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
