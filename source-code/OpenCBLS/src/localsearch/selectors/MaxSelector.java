package localsearch.selectors;
import localsearch.functions.*;
import localsearch.model.*;
import localsearch.invariants.*;
import java.util.*;
public class MaxSelector {

	/**
	 * @param args
	 */
	
	private ArgMax _argMaxInvr;
	private ArrayList<Integer> _L;
	private Random 	_R;
	
	public MaxSelector(IFunction[] f){
		_argMaxInvr = new ArgMax(f);
		_L = _argMaxInvr.getIndices();
		_R = new Random();
	}
	public int get(){
		int i = _R.nextInt()%_L.size();
		if(i < 0) i = -i;
		return _L.get(i);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
