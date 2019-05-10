package localsearch.model;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;

import core.BasicEntity;
import localsearch.model.VarIntLS;
import java.util.*;
public class LocalSearchManager {

	private int n;
	//private VarIntLS[] _x;
	private ArrayList<VarIntLS>	_x;
	private Vector<AbstractInvariant> _invariants;
	private ConstraintSystem _S = null;
	//private Vector<TreeSet<BasicEntity>> _map;
	private HashMap<VarIntLS, TreeSet<AbstractInvariant>> _map;
	private boolean	_closed;
	public LocalSearchManager() {
		n = 0;
		_invariants = new Vector<AbstractInvariant>();
		_x = new ArrayList<VarIntLS>();
		System.out.println("OpenCBLS library: by dungkhmt@gmail.com, Algorithms & Optimization LAB, SoICT, HUST, 2014-2015");
		_closed = false;
	}
	
	public void post(VarIntLS x){
		
		x.setID(n++);
		_x.add(x);
	}
	public void post(AbstractInvariant e) {
		e.setID(n++);
		_invariants.add(e);
	}
	public void post(ConstraintSystem S){
		if(_S != null){
			System.out.println("LocalSearchManager::post(ConstraintSystem) EXCEPTION: A ConstraintSystem has already instantiated");
			assert(false);
		}
		_S = S;
		//_S.setID(n++);
	}
	public void propagateInt(VarIntLS x, int val) {
		TreeSet<AbstractInvariant> s = _map.get(x);
		if(s == null){
			return;
		}
		for (Invariant e : s){
			e.propagateInt(x, val);
		}
	}
	
	public void initPropagate() {
		//for (int i = 0; i < n; i++)
		for(int i = 0; i < _invariants.size(); i++)
			_invariants.elementAt(i).initPropagate();
	}
	public String name(){
		return "LocalSearchManager";
	}
	public void print(){
		System.out.println(name() + "::print");
		System.out.print("VarIntLS = ");
		for(int i = 0; i < _x.size(); i++)
			System.out.println("_x[" + _x.get(i).getID() + "], ");
		System.out.println();
		System.out.println("_invariants = ");
		for(int i = 0;i < _invariants.size(); i++){
			System.out.println("_invariants[" + i + "] = " + "(id = " + _invariants.get(i).getID() + 
					", name = " + _invariants.get(i).name() + ")");
		}
		
		System.out.println("Dependency = ");
		for(int i = 0; i < _x.size(); i++){
			VarIntLS x = _x.get(i);
			System.out.print("x[" + x.getID() + "] defines: ");
			TreeSet<AbstractInvariant> T = _map.get(x);
			if (T == null){
				System.out.println("Error:: This VarIntLS doesn't define any constraint");
			}
			else
			for(AbstractInvariant invr : T){
				{
					System.out.println(invr.name() + "[" + invr.getID() + "], ");
				}
			}
			System.out.println();
		}
		
	}
	public boolean closed(){
		return _closed;
	}
	public void close() {
		if(closed()) return;
		_closed = true;
		
		if(_S != null){
			_S.setID(n++);
			_invariants.add(_S);
			_S.close();
		}
		
		_map = new HashMap<VarIntLS, TreeSet<AbstractInvariant>>();
		
		//for (int i = 0; i < n; i++) {
		for(int i = 0; i < _invariants.size(); i++){
			VarIntLS[] s = _invariants.elementAt(i).getVariables();
			if (s != null) 
				//for (VarIntLS e : s) {
				for (int j = 0; j < s.length; j++) {
					VarIntLS x = s[j];
					if (_map.get(x) == null) 
						_map.put(x,new TreeSet<AbstractInvariant>(new Compare()));
					_map.get(x).add(_invariants.elementAt(i));
				}
		}	
		//System.out.println(name() + "::close");
		
		
		//print();
		initPropagate();
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

class Compare implements Comparator<BasicEntity> {
	
	@Override
	public int compare(BasicEntity o1, BasicEntity o2) {
		return o1.getID() - o2.getID();
	}
	
}
