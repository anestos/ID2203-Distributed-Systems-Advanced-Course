package se.kth.ict.id2203.components.beb;
 
import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;
 
public class BebDataMessage extends Pp2pDeliver {
 
    private static final long serialVersionUID = 4875200623380249528L;
    private final BebDeliver message;
     
    protected BebDataMessage(Address source, BebDeliver message) {
        super(source);
        this.message = message;
    }
    public final BebDeliver getMsg() {
        return message;
    }
 
}