package se.kth.ict.id2203.components.multipaxos;

import java.util.List;

public class Help {
	private int ts;
	private List<Object> usuf;
	
	public Help(int ts, List<Object> usuf2) {
		this.ts = ts;
		this.usuf = usuf2;
	}
	public int getTs(){
		return ts;
	}
	public List<Object> getUsuf(){
		return usuf;
	}

}
