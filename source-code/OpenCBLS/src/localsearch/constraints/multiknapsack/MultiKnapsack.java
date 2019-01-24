package localsearch.constraints.multiknapsack;




import localsearch.model.*;
import core.BasicEntity;
import java.util.*;

public class MultiKnapsack extends AbstractInvariant implements IConstraint {

	private VarIntLS[]		_x;
	private int[]			_w;
	private int[]			_c;
	private int				_violations;
	private int[]			_tw;// _tw[i] is the sum of weights of items of the bin i 
	private int				_n;// number of items
	private int				_m;// number of bins
	private HashMap<VarIntLS, Integer> _map;
	private LocalSearchManager	_lsm;
	
	public MultiKnapsack(VarIntLS[] x, int[] w, int[] c){
		_x = x;
		_w = w;
		_c = c;
		_n = x.length;
		_m = c.length;
		_lsm = _x[0].getLocalSearchManager();
		
		post();
		_lsm.post(this);
	}
	private void post(){
		_map = new HashMap<VarIntLS, Integer>();
		for(int j = 0; j < _n; j++){
			_map.put(_x[j],j);
		}
		_tw = new int[_m];
	}
	public VarIntLS[] getVariables(){
		//System.out.println("BasicLocalSearchEntity::getVariables, this must be implemented within subclass");
		//assert(false);
		return _x;
	}
	public void initPropagate(){
		//System.out.println("BasicLocalSearchEntity::initPropagate, this must be implemented within subclass");
		//assert(false);
		for(int i = 0; i < _m; i++){
			_tw[i] = 0;
		}
		for(int j = 0; j < _n; j++){
			int bx = _x[j].getValue();
			_tw[bx] += _w[j];
		}
		_violations = 0;
		for(int i = 0; i < _m; i++){
			int v = _tw[i] - _c[i] > 0 ? _tw[i] - _c[i] : 0;
			_violations += v;
		}
	}
	
	public void propagateInt(VarIntLS x, int val){
		//System.out.println(name() + "::propagateInt(" + x.getOldValue() + "," + val + ")");
		//assert(false);
		if(_map.get(x) == null) return;
		int ov = x.getOldValue();
		int nv = x.getValue();
		int i = _map.get(x);
		
		int vov = _tw[ov] - _c[ov] > 0 ? _tw[ov] - _c[ov] : 0;
		int vnv = _tw[nv] - _c[nv] > 0 ? _tw[nv] - _c[nv] : 0;
		_violations = _violations - vov - vnv;
		_tw[ov] = _tw[ov] - _w[i];
		_tw[nv] = _tw[nv] + _w[i];
		vov = _tw[ov] - _c[ov] > 0 ? _tw[ov] - _c[ov] : 0;
		vnv = _tw[nv] - _c[nv] > 0 ? _tw[nv] - _c[nv] : 0;
		_violations = _violations + vov + vnv;
		
	}
	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	@Override
	public int violations(VarIntLS x) {
		if (_violations != 0)
			return (x.IsElement(_x)) ? 1 : 0;
		else
			return 0;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(_map.get(x) == null) return 0;
		int ob = x.getValue();
		int nb = val;
		if(ob == nb) return 0;
		int j = _map.get(x);
		int newViolations = _violations;
		int vob = _tw[ob] - _c[ob] > 0 ? _tw[ob] - _c[ob] : 0;
		int vnb = _tw[nb] - _c[nb] > 0 ? _tw[nb] - _c[nb] : 0;
		newViolations -= (vob + vnb);
		
		_tw[ob] -= _w[j];
		_tw[nb] += _w[j];
		vob = _tw[ob] - _c[ob] > 0 ? _tw[ob] - _c[ob] : 0;
		vnb = _tw[nb] - _c[nb] > 0 ? _tw[nb] - _c[nb] : 0;
		
		newViolations += (vob + vnb);
		_tw[ob] += _w[j];
		_tw[nb] -= _w[j];
		
		// recover _tw
		
		return newViolations - _violations;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		int vx=x.getValue();
		int vy=y.getValue();
		if(_map.get(x)==null) return getAssignDelta(y,vx);
		if(_map.get(y)==null) return getAssignDelta(x,vy);
		if(vx==vy) return 0;
		int i=_map.get(x);
		int j=_map.get(y);
		int newViolations = _violations;
		int vob = _tw[vx] - _c[vx] > 0 ? _tw[vx] - _c[vx] : 0;
		int vnb = _tw[vy] - _c[vy] > 0 ? _tw[vy] - _c[vy] : 0;
		newViolations -= (vob + vnb);
		_tw[vx]=_tw[vx]-_w[i]+_w[j];
		_tw[vy]=_tw[vy]-_w[j]+_w[i];
		vob = _tw[vx] - _c[vx] > 0 ? _tw[vx] - _c[vx] : 0;
		vnb = _tw[vy] - _c[vy] > 0 ? _tw[vy] - _c[vy] : 0;
		newViolations += (vob + vnb);
		_tw[vx]=_tw[vx]-_w[j]+_w[i];
		_tw[vy]=_tw[vy]-_w[i]+_w[j];
		return newViolations-_violations;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _lsm;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		// compare the incremental update with recomputation from scratch
		int[] tw = new int[_m];
		for(int i = 0; i < _m; i++)
			tw[i] = 0;
		for(int j = 0; j < _n;j++)
			tw[_x[j].getValue()] += _w[j];
		
		int violations = 0;
		for(int i = 0; i < _m; i++){
			if(_tw[i] != tw[i]){
				System.out.println(name() + "::verify failed, _tw[" + i + "] = " + _tw[i] + " differs from tw[" + i + "] = " 
			+ tw[i] + " by recomputation");
				print();
				return false;
			}
			int v = tw[i] - _c[i] > 0 ? tw[i] - _c[i] : 0;
			violations += v;
		}
		if(violations != _violations){
			System.out.println(name() + "::verify failed, _violations = " + _violations + " differs from violations = " + 
		violations + " by recomputation");
			print();
			return false;
			
		}
		return true;
	}

	public String name(){
		return "MultiKnapsack";
	}
	public void print(){
		for(int j = 0; j < _n; j++)
			System.out.println("_x[" + j + "] = " + _x[j].getValue() + ", _w[" + j + "] = " + _w[j]);
		System.out.println();
		for(int i = 0; i < _m; i++){
			System.out.println(name() + "::print -> _tw[" + i + "] = " + _tw[i] + ", ");
		}
		System.out.println(name() + "::print, _violations = " + violations());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager lsm = new LocalSearchManager();
		int[] w = {2, 3, 4, 3, 5, 2, 2, 3, 1, 3, 3, 4, 5, 2, 3, 6, 7, 3, 2, 1, 4, 2, 4, 5, 3, 6, 6, 7, 8, 2, 3, 4};
		int[] c = {19, 10, 17, 13, 10, 20, 30};
		int n = w.length;
		int m = c.length;
		VarIntLS[] x = new VarIntLS[n];
		for(int i = 0; i < n; i++)
			x[i] = new VarIntLS(lsm,0,m-1);
		
		MultiKnapsack mns = new MultiKnapsack(x,w,c);
		ConstraintSystem S = new ConstraintSystem(lsm);
		S.post(mns);
		S.close();
		lsm.close();
		
		
		int oldV = mns.violations();
		
		System.out.println("init violations = oldV = " + oldV);
		mns.print();
		java.util.Random R = new java.util.Random();
		for(int it = 0; it < 10; it++){
			int i = R.nextInt()%n;
			int v = R.nextInt()%m;
			if(i < 0) i = -i;
			if(v < 0) v = -v;
			
			int delta = mns.getAssignDelta(x[i], v);
			
			x[i].setValuePropagate(v);
			if(oldV + delta != mns.violations()){
				System.out.println(it + ", move --> failed oldV = " + oldV + ", delta = " + delta + 
						" while violations after move = " + mns.violations());
				break;
			}
			if(!mns.verify()){
				break;
			}
			System.out.println("Step " + it + ", assign x[" + i + "] <- " + v + " --> OK");
			oldV = mns.violations();
		}
		
		
		mns.print();
		
		
		 
		 int oldv=S.violations();
		 int c1=w.length;
		 int dem=0;
		 
		 for(int i=0;i<1000;i++)
		 {
			 int r1=(int)(Math.random()*c1);
			 int r2=(int)(Math.random()*c1);
			 int a1=x[r1].getValue();
			 int a2=x[r2].getValue();
			 int dv=S.getSwapDelta(x[r1], x[r2]);
			 x[r1].setValuePropagate(a2);
			 x[r2].setValuePropagate(a1);
			 int dd=S.violations();
			 if(dd==oldv+dv)
			 {
				 oldv=dd;
				 dem++;
			 }
			 else
			 {
				 System.out.println("------------------");
				 break;
			 }
		 }
		 System.out.println(+dem);
		 
		 localsearch.applications.Test T = new localsearch.applications.Test();
		 T.test(S, 10000);
		 
		 if(true) return;
		 localsearch.search.TabuSearch ts = new localsearch.search.TabuSearch();
			ts.search(S, 10, 5, 100000, 100);
		 //localsearch.search.SA_search ts=new localsearch.search.SA_search();
		 //ts.search(S, 2000, 0.00005,0.9);
			System.out.println("Finished S = " + S.violations());
			for(int i=0;i<x.length;i++)
				System.out.print(x[i].getValue()+" ");
		 
		 
	}

}
