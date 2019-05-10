package localsearch.search;
import localsearch.model.*;
public class OneVariableValueMove extends Move{

	/**
	 * @param args
	 */
	private VarIntLS	_x;
	private int			_value;
	
	public OneVariableValueMove(MoveType type, double eval, VarIntLS x, int value){
		super(type,eval);
		_x = x; _value = value;
	}
	public VarIntLS getVariable(){ return _x;}
	public int getValue(){ return _value;}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
