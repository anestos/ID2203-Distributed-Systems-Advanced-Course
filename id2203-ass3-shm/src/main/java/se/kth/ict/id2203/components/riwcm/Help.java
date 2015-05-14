package se.kth.ict.id2203.components.riwcm;

public class Help {

	private int a;
	private int b;
	private int c;
	
	public Help(int a, int b, int c){
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public int getTs(){
		return a;
	}
	public int getWr(){
		return b;
	}
	public int getV(){
		return c;
	}
}
