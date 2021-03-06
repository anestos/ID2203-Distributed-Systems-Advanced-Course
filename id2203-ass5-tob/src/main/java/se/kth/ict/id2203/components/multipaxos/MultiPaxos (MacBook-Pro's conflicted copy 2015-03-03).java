package se.kth.ict.id2203.components.multipaxos;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.asc.AbortableSequenceConsensus;
import se.kth.ict.id2203.ports.asc.AscAbort;
import se.kth.ict.id2203.ports.asc.AscDecide;
import se.kth.ict.id2203.ports.asc.AscPropose;
import se.kth.ict.id2203.ports.fpl.FIFOPerfectPointToPointLink;
import se.kth.ict.id2203.ports.fpl.FplSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

public class MultiPaxos extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(MultiPaxos.class);

	private Negative<AbortableSequenceConsensus> asc = provides(AbortableSequenceConsensus.class);
	private Positive<FIFOPerfectPointToPointLink> fpl = requires(FIFOPerfectPointToPointLink.class);

	private int t;
	private int prepts;
	private int ats;
	private ArrayList<Object> av;
	private int al;
	private int pts;
	private ArrayList<Object> pv;
	private int pl;
	private ArrayList<Object> proposedValues;
	private HashMap<Address, Help> readlist;
	private int[] accepted;
	private int[] decided;
	private Set<Address> topology;
	private Address self;


	public MultiPaxos(MultiPaxosInit event) {
		logger.info("Constructing MultiPaxos component.");
		self = event.getSelfAddress();
		topology = event.getAllAddresses();
		readlist = new HashMap<Address, Help>();
		accepted = new int[topology.size()];
		decided = new int[topology.size()];
		t=0;
		prepts =0;
		ats = 0;
		al = 0;
		pts = 0;
		pl = 0;
		proposedValues = new ArrayList<Object>();
		av = new ArrayList<Object>();
		pv = new ArrayList<Object>();

		subscribe(handleAscPropose, asc);
		subscribe(handleFplPrepare, fpl);
		subscribe(handleFplNack, fpl);
		subscribe(handlefplPrepareNack, fpl);
		subscribe(handleFplAccept, fpl);
		subscribe(handleAcceptAck, fpl);
		subscribe(handleDecide, fpl);


	}


	private Handler<AscPropose> handleAscPropose = new Handler<AscPropose>() {
		@Override
		public void handle(AscPropose event) {
			logger.info("AscPropose");

			t++;
			Object hi = (Object) event.getValue();
			if (pts == 0 ){
				pts  = (t*topology.size()) + self.getId();
				
				pv = new ArrayList<Object>( av.subList(0, al)); 
				pl = 0;
				proposedValues.clear();
				proposedValues.add(hi);
				readlist.clear();
				accepted = new int[topology.size()];
				decided = new int[topology.size()];
				for (Address q : topology){
					trigger(new FplSend(q, new PrepareMessage(self, pts, al, t)), fpl);
				}
			} else if (readlist.size() <= topology.size()/2){
				proposedValues.add(hi);
			} else if (!pv.contains(hi)){
				pv.add(hi);
				for (Address q : topology){
					if (readlist.containsKey(q)){
						ArrayList<Object> hv = new ArrayList<Object>();
						hv.add(hi);
						trigger(new FplSend(q, new AcceptMessage(self,pts, hv, pv.size()-1,t)), fpl);
					}
				}
			}
		}
	};

	private Handler<PrepareMessage> handleFplPrepare = new Handler<PrepareMessage>() {
		@Override
		public void handle(PrepareMessage event) {
			logger.info("PrepareMessage");

			t = Math.max(event.getT(), t)+1;
			int ts = event.getTs();
			Address q = event.getSource();
			if (ts < prepts){
				trigger(new FplSend(q, new NackMessage(self, ts, t)),fpl);
			} else {
				prepts = ts;
				trigger(new FplSend(q, new PrepareAckMessage(self, ts, ats, new ArrayList<Object>( av.subList(0, al)), al, t)),fpl);
			}
		}
	};

	private Handler<NackMessage> handleFplNack = new Handler<NackMessage>() {
		@Override
		public void handle(NackMessage event) {
			logger.info("NackMessage");

			t = Math.max(event.getT(), t)+1;
			if (event.getTs() == pts){
				pts = 0;
				trigger(new AscAbort(), asc);
			}
		}
	};
	private Handler<PrepareAckMessage> handlefplPrepareNack = new Handler<PrepareAckMessage>() {
		@Override
		public void handle(PrepareAckMessage event) {
			logger.info("PrepareAckMessage");

			t = Math.max(event.getT(), t)+1;
			Address q = event.getSource();
			List<Object> usuf = event.getUsuf();
			int l = event.getL();
			if (event.getPts()== pts){
				readlist.put(q, new Help(event.getTs(), usuf));
				decided[q.getId()-1] = l;
				if (readlist.size() == (topology.size()/2) + 1){
					int tsprime = 0;
					List<Object> usufprime = new LinkedList<Object>();
				
					for (Entry<Address, Help> r : readlist.entrySet()){
						int ts2prime = r.getValue().getTs();
						List<Object> usuf2prime = r.getValue().getUsuf();
						
						if (tsprime < ts2prime || (tsprime == ts2prime && usufprime.size() < usuf2prime.size())){
							tsprime = ts2prime;
							usufprime = usuf2prime;	
						}
						for (Object b : usufprime){
							pv.add(b);
						}
					}
					for (Object vv : proposedValues){
						if (!pv.contains(vv)){
							pv.add(vv);
						}
					}
					for (Address p : topology ){
						if (!readlist.containsKey(p)){
							int lprime = decided[p.getId()-1];
							trigger(new FplSend(p, new AcceptMessage(self, pts, new ArrayList<Object>( pv.subList(0, lprime)), lprime, t)), fpl);
						}
					}
				} else if (readlist.size() > (topology.size()/2) + 1){
					trigger(new FplSend(q, new AcceptMessage(self, pts, new ArrayList<Object>( pv.subList(0, l)), l, t)), fpl);
					if (pl != 0){
						trigger(new FplSend(q, new DecideMessage(self, pts, pl, l) ), fpl);
					}
				}
			}

		}
	};

	private Handler<AcceptMessage> handleFplAccept = new Handler<AcceptMessage>() {
		@Override
		public void handle(AcceptMessage event) {
			logger.info("AcceptMessage");

			t = Math.max(event.getT(), t)+1;
			Address q = event.getSource();
			if (event.getPts() != prepts){
				trigger(new FplSend(q, new NackMessage(self, event.getPts(), t)), fpl);
			} else {
				ats = event.getPts();
				int offs = event.getSize();
				if (offs < av.size()){
					av = new ArrayList<Object> (av.subList(0, offs));
				}
				for (Object b : event.getV()){
					av.add(b);
				}
				
				trigger(new FplSend(q, new AcceptAckMessage(self, event.getPts(), av.size(), t)), fpl);
			}
		}
	};
	private Handler<AcceptAckMessage> handleAcceptAck = new Handler<AcceptAckMessage>() {
		@Override
		public void handle(AcceptAckMessage event) {
			logger.info("AcceptAckMessage");
			t = Math.max(event.getT(), t)+1;
			int evl = event.getL();
			int evpts = event.getPts();
			if (evpts == pts){
				accepted[event.getSource().getId()-1] = evl;
				
				if (pl < evl && (helpin(topology, accepted, evl) > topology.size()/2)){
					pl = evl;
					for (Address p : topology){
						if (readlist.containsKey(p)){
							trigger(new FplSend(p, new DecideMessage(self, pts, pl, t)), fpl);
						}
					}
				}
			}

		}
	};
	private Handler<DecideMessage> handleDecide = new Handler<DecideMessage>() {
		@Override
		public void handle(DecideMessage event) {
			t = Math.max(event.getT(), t)+1;
			if (event.getTs() == prepts){
				while (al < event.getL()){
					
					trigger(new AscDecide((Object) av.get(al)), asc);
					al++;
				}
			}

		}
	};
	
	private int helpin(Set<Address> top, int[] accep, int myl ){
		int count = 0;
		for (Address p : top){
			if (accep[p.getId()-1] >= myl){
				count++;
			}
		}
		return count;
	}
}

