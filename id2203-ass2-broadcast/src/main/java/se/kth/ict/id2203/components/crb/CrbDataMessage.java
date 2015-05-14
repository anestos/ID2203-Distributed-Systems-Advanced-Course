package se.kth.ict.id2203.components.crb;

import se.kth.ict.id2203.ports.crb.CrbDeliver;
import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.sics.kompics.address.Address;

public class CrbDataMessage extends RbDeliver{

	private static final long serialVersionUID = 8583506957344939640L;
	private final CrbDeliver message;
    private final int[] vector;
    
	public CrbDataMessage(Address source, CrbDeliver message, int[] vector) {
        super(source);
        this.message = message;
        this.vector = vector;
    }
    
    public final CrbDeliver getMsg() {
        return message;
    }

    public final int[] getVector() {
        return vector;
    }
}