package se.kth.ict.id2203.components.rb;
 
import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.sics.kompics.address.Address;
 
public class RbDataMessage extends BebDeliver {

	private static final long serialVersionUID = 4269721526238570280L;
	private final RbDeliver message;
    private final int seqnum;
    
    protected RbDataMessage(Address source, RbDeliver message, int seqnum) {
        super(source);
        this.message = message;
        this.seqnum = seqnum;
    }
    
    public final RbDeliver getMsg() {
        return message;
    }
    public final int getSeqnum() {
        return seqnum;
    }
}