/*
 * Author: PHAM Quang Dung (dungkhmt@gmail.com)
 */
package core;

import java.util.Set;

import localsearch.model.VarIntLS;



public class VarInt extends BasicEntity {

	/**
	 * @param args
	 */
	protected int _value;
	protected int _min;// minimal value of the domain
	protected int _max;// maximal value of the domain
	protected boolean[] _forbiden;
	protected int _sz_forbiden;
	public int getValue(){ return _value;}
	public void setValue(int v){ _value = v;}
	
	public VarInt(Set<Integer> D){
		boolean first = true;
		for(int v : D){
			if(first){
				_min = v; _max = v;first = false;
			}else{
				_min = _min < v ? _min : v;
				_max = _max > v ? _max : v;
			}
		}
		_sz_forbiden = _max-_min+1;
		_forbiden = new boolean[_sz_forbiden];
		for(int i = 0; i < _sz_forbiden; i++)
			_forbiden[i] = false; 
	
	}
	public VarInt(int min, int max){
		super(-1);
		_min = min;
		_max = max;
		_value = _min;
		_sz_forbiden = _max-_min+1;
		_forbiden = new boolean[_sz_forbiden];
		for(int i = 0; i < _sz_forbiden; i++)
			_forbiden[i] = false; 
	
	}
	public VarInt(int id, int min, int max){
		super(id);
		_min = min;
		_max = max;
		_value = _min;
		_sz_forbiden = _max-_min+1;
		_forbiden = new boolean[_sz_forbiden];
		for(int i = 0; i < _sz_forbiden; i++)
			_forbiden[i] = false; 
	}
	
	public void initBound(int min, int max){
		_min = min;
		_max = max;
		_value = _min;
		_sz_forbiden = _max-_min+1;
		_forbiden = new boolean[_sz_forbiden];
		for(int i = 0; i < _sz_forbiden; i++)
			_forbiden[i] = false; 
	
	}
	public void disableValue(int v){
		if(v < _min || v > _max){
			System.out.println("VarInt.disableValue -> exception, value " + v + " is out of bound " + _min + ".." + _max);
			assert(false);
		}
		_forbiden[v-_min] = true;
	}
	public void enableValue(int v){
		if(v < _min || v > _max){
			System.out.println("VarInt.enableValue -> exception, value " + v + " is out of bound " + _min + ".." + _max);
			assert(false);
		}
		_forbiden[v-_min] = false;
	}
	public int getMinValue(){ return _min;}
	public int getMaxValue(){ return _max;}
	public boolean isValue(int v){ 
		if(v < _min || v > _max){
			System.out.println("VarInt.isValue -> exception, value " + v + " is out of bound " + _min + ".." + _max);
			assert(false);
		}
		return _forbiden[v-_min] == false;
	}
	
	public boolean IsElement(VarInt[] _S){
		for(int i = 0; i < _S.length; i++){
			if (_S[i] == this)
				return true;
		}
		return false;			
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
