package se.kth.ict.id2203.components.rb;

import java.util.HashSet;

import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.rb.RbBroadcast;
import se.kth.ict.id2203.ports.rb.ReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

public class EagerRb extends ComponentDefinition {

//	private static final Logger logger = LoggerFactory.getLogger(EagerRb.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Negative<ReliableBroadcast> rb = provides(ReliableBroadcast.class);
	private int seqnum;
	private HashSet<RbDataMessage> delivered;
	private Address self;

	public EagerRb(EagerRbInit init) {
		seqnum = 0;
		self = init.getSelfAddress();
		delivered = new HashSet<RbDataMessage>();
		subscribe(bebDeliver, beb);
		subscribe(rbBroadcast, rb);
	}

	private Handler<RbBroadcast> rbBroadcast = new Handler<RbBroadcast>() {
		@Override
		public void handle(RbBroadcast event) {
			seqnum++;
			trigger(new BebBroadcast(new RbDataMessage(self, event.getDeliverEvent(), seqnum)), beb);

		}
	};

	private Handler<RbDataMessage> bebDeliver = new Handler<RbDataMessage>() {
		@Override
		public void handle(RbDataMessage event) {
			if (!checkthis(event, delivered ) ){
				delivered.add(event);
				trigger(event.getMsg(), rb);
				trigger(new BebBroadcast(new RbDataMessage(event.getSource(),event.getMsg(), event.getSeqnum())), beb);

			} 
		}
	};
	private Boolean checkthis(RbDataMessage msg, HashSet<RbDataMessage> set){
		Boolean checkin = false;
			for (RbDataMessage s : set) {
				if(s.getSource().toString().equals(msg.getSource().toString()) && s.getSeqnum() == msg.getSeqnum()){
					checkin = true;
				}
			}
		return checkin;
	};

}
