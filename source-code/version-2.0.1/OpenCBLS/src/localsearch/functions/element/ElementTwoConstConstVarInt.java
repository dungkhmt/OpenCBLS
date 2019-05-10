package localsearch.functions.element;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ElementTwoConstConstVarInt extends AbstractInvariant implements
		IFunction {

	private LocalSearchManager		_ls;
	private VarIntLS[]				_vars;
	private VarIntLS				_x;
	private int[][]					_c;
	private int						_r;
	private int						_value;
	private int						_minValue;
	private int						_maxValue;
	private int						_row;
	private int						_column;
	private final int				MAX_INT = 2147483647;//1000000000;
	
	public ElementTwoConstConstVarInt(int[][] c, int r, VarIntLS x){
		_c = c;
		_r = r;
		_x = x;
		post();
	}
	private void post(){
		_ls = _x.getLocalSearchManager();
		_vars = new VarIntLS[1];
		_vars[0] = _x;
		_row = _c.length;
		_column = _c[0].length;
		_minValue = MAX_INT;
		_maxValue = -MAX_INT;
		for(int i = 0; i < _c[_r].length; i++){
			_minValue = _minValue < _c[_r][i] ? _minValue : _c[_r][i];
			_maxValue = _maxValue > _c[_r][i] ? _maxValue : _c[_r][i];
		}
			
		
		_ls.post(this);
	}
	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return _minValue;
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return _maxValue;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return _value;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(x != _x)
			return 0;
		if(0 > val || val >= _column){
			System.out.println(name() + "::getAssignDelta(x," + val + ") exception, val = " + val + " out of column bound " + _column);
			assert(false);
		}
		int nv = _c[_r][val];
		return nv - _value;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if(x != _x && y != _x)
			return 0;
		else{
			if(x == _x) return getAssignDelta(x,y.getValue());
			else return getAssignDelta(y,x.getValue());
		}
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		if(x != _x) return;
		_value = _c[_r][val];
	}

	public VarIntLS[] getVariables(){ return _vars;}
	@Override
	public void initPropagate() {
		if(_x.getValue() < 0 || _x.getValue() >= _column){
			System.out.println(name() + "::initPropagate exception, _x = " + _x.getValue() + " which is out of column bound = " + _column);
			assert(false);	
		}
		_value = _c[_r][_x.getValue()];
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	public String name(){
		return "ElementTwoConstConstVarInt";
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int value = _c[_r][_x.getValue()];
		if(_value != value){
			System.out.println(name() + "::verify failed, _value = " + _value + " differs from value = " + value + " by recomputation");
			return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int n = 100;
		java.util.Random R = new java.util.Random();
		int[][] c = new int[n][n];
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++){
				c[i][j] = R.nextInt(100);
			}
		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[n];
		for(int i = 0; i < n; i++)
			x[i] = new VarIntLS(ls,0,n-1);
		IFunction[] f = new IFunction[n];
		for(int i = 0; i < n; i++)
			//f[i] = new ElementTwoConstConstVarInt(c,i,x[i]);
			f[i] = new Element(c,i,x[i]);
		
		IFunction S = new localsearch.functions.sum.Sum(f);
		ls.close();
		
		localsearch.applications.Test T = new localsearch.applications.Test();
		//for(int i = 0; i < n; i++)
			//T.test(f[i], 10000);
		T.test(S, 10000);
	}

}
