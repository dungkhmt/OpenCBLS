package localsearch.constraints.alldifferent;



//import core.BasicEntity;
import localsearch.model.*;

public class AllDifferent extends AbstractInvariant implements IConstraint {

	//private int _violations;
	//private int _minValue;
	//private int _maxValue;
	//private int[] _occ;
	//private VarIntLS[] _x;
	private IConstraint	_c;
	private LocalSearchManager _ls;
		
	public AllDifferent(VarIntLS[] x) {
		/*
		_x = x;
		_ls = x[0].getLocalSearchManager();
		for (int i = 0; i < x.length; i++) {
			_minValue = Math.min(_minValue, x[i].getMinValue());
			_maxValue = Math.max(_maxValue, x[i].getMaxValue());
		}
		
		post();
		_ls.post(this);
		*/
		_c = new AllDifferentVarIntLS(x);
		_ls = _c.getLocalSearchManager();
		post();
	}
	public AllDifferent(IFunction[] f){
		_c = new AllDifferentFunctions(f);
		_ls = _c.getLocalSearchManager();
		post();
	}
	public String name(){ return "AllDiferent";}
	private void post() {
		//_occ = new int[_maxValue - _minValue + 1];
		//for (int i = 0; i < _occ.length; i++) _occ[i] = 0;
		_ls.post(this);
	}
	
	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _c.violations();//_violations;
	}

	public int violations(VarIntLS x) {
		//int v = _occ[x.getValue() - _minValue];
		//return (x.IsElement(_x) ? Math.max(0, v - 1) : 0);
		return _c.violations(x);
	}
	
	@Override
	public VarIntLS[] getVariables() {
		//return _x;
		return _c.getVariables();
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		/*
		if (!x.IsElement(_x) || x.getValue() == val) return 0;
		int newV = _violations;
		int v1 = x.getValue() - _minValue;
		int v2 = val - _minValue;
		if (_occ[v1] > 1) newV--;
		if (_occ[v2] > 0) newV++;
			return newV - _violations;
		*/
		return _c.getAssignDelta(x, val);
	}
	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		/*
		if (!x.IsElement(_x)) 
			return 0;
		if ((x.IsElement(_x)) && (y.IsElement(_x))) 
			return 0;
		int newV = _violations;
		int v1 = x.getValue() - _minValue;
		int v2 = y.getValue() - _minValue;
		if (_occ[v1] > 1) newV--;
		if (_occ[v2] > 0) newV++;
			return newV - _violations;		
		*/
		return _c.getSwapDelta(x, y);
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		//System.out.println(name() + "::propagateInt(x[" + x.getID() + "], " + val + ")");
		/*
		if (!x.IsElement(_x)) return;
		int v1 = x.getOldValue() - _minValue;
		int v2 = val - _minValue;
		if (v1 == v2) return;
		if (_occ[v1] > 1) _violations--;
		_occ[v1]--;
		if (_occ[v2] > 0) _violations++;
		_occ[v2]++;
		*/
		// DO NOTHING
	}
	
	@Override
	public void initPropagate() {
		//System.out.println(name() + "::initPropagate");
		/*
		_violations = 0;
		for (VarIntLS e : _x) _occ[e.getValue() - _minValue]++;
		for (int i = 0; i < _occ.length; i++) _violations += Math.max(0, _occ[i] - 1);
		*/
		// DO NOHTING
	}
	
	public void print(){
		/*
		for(int i = 0; i < _x.length; i++){
			System.out.println("_x[" + i + "] = " + _x[i].getValue());
		}
		for(int v = _minValue; v <= _maxValue; v++){
			System.out.println("_occ[" + v + "] = " + _occ[v]);
		}
		*/
		
	}
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		/*
		int[] occ = new int[_maxValue - _minValue + 1];
		for(int i = 0; i < occ.length; i++){
			occ[i] = 0;
		}
		for(int i = 0; i < _x.length; i++){
			int v = _x[i].getValue();
			occ[v - _minValue]++;
		}
		for(int v = _minValue; v <= _maxValue; v++){
			if(_occ[v] != occ[v]){
				System.out.println(name() + "::verify failed, _occ[" + v + "] = " + _occ[v] + " differs from occ[" +
			v + "] = " + occ[v] + " by recomputation");
				return false;
			}
			
		}
		int violations = 0;
		for(int v = _minValue; v <= _maxValue; v++){
			violations += Math.max(occ[v] - 1, 0);
		}
		if(violations != _violations){
			System.out.println(name() + "::verify failed, _violations = " + _violations + " differs from violations = " 
		+ violations + " by recomputation");
		}
		return true;
		*/
		return _c.verify();
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int n=5;
		LocalSearchManager _ls = new LocalSearchManager();
		ConstraintSystem S=new ConstraintSystem(_ls);
		VarIntLS[] x=new VarIntLS[n];
		for(int i=0;i<n;i++)
		{
			x[i]=new VarIntLS(_ls,0, 100);
			
		}
		x[0].setValue(1);
		x[1].setValue(2);
		x[2].setValue(2);
		x[3].setValue(2);
		x[4].setValue(3);
		S.post(new AllDifferent(x));
		S.close();
		_ls.close();
		System.out.println(S.violations());
		System.out.println(S.getAssignDelta(x[2],2));
		
		/*
		int oldv=S.violations();
		int dem=0;
		for(int i=0;i<100000;i++)
		{
			int a1=(int)(Math.random()*n);
			int a2=(int)(Math.random()*100);
			int dv=S.getAssignDelta(x[a1],a2);
			
			x[a1].setValuePropagate(a2);
			int dd=S.violations();
			if(dd==dv+oldv)
			{
				oldv=dd;
				dem++;
			}
			else
			{
				System.out.println("----------------");break;
			}
		}
		System.out.println(dem);
			*/
	}		
}
