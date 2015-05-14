package se.kth.ict.id2203.components.epfd;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.epfd.EventuallyPerfectFailureDetector;
import se.kth.ict.id2203.ports.epfd.Restore;
import se.kth.ict.id2203.ports.epfd.Suspect;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timer;

public class Epfd extends ComponentDefinition {
	private static final Logger logger = LoggerFactory.getLogger(Epfd.class);
	private Positive<Timer> timer = requires(Timer.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Negative<EventuallyPerfectFailureDetector> epfd = provides(EventuallyPerfectFailureDetector.class);

	private long delay;
	private long delta;
	private Address self;
	private Set<Address> topology = null;
	private HashSet<Address> alive;
	private HashSet<Address> suspected;
	private int seqnum =0;


	public Epfd(EpfdInit init) {
		delay = init.getInitialDelay();
		delta = init.getDeltaDelay();

		self = init.getSelfAddress();

		topology =  init.getAllAddresses();
	//	topology.remove(self);
		alive = new HashSet<Address>(topology);

		suspected = new HashSet<Address>();

		subscribe(handleStart, control);
		subscribe(handleTimeout, timer);
		subscribe(heartBeatRequest, pp2p);
		subscribe(heartBeatReply, pp2p);

	}

	private Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			SchedulePeriodicTimeout st = new SchedulePeriodicTimeout(delay, delta);
			st.setTimeoutEvent(new CheckTimeout(st));
			trigger(st, timer);	
			suspected.clear();

		}
	};

	private Handler<CheckTimeout> handleTimeout = new Handler<CheckTimeout>() {
		@Override
		public void handle(CheckTimeout arg0) {
			seqnum++;
			//logger.info("processes alive {}", alive);
			//logger.info("processes suspected {}", suspected);
			HashSet<Address> intersection = new HashSet<Address>(alive);
			intersection.retainAll(suspected);	
			if (!intersection.isEmpty()) {
				delay = delay+delta;
			}


			Object[] array = topology.toArray();

			for(int i=0; i<array.length; i++) {

				Address o = (Address) array[i];

				if (!alive.contains(o) && !suspected.contains(o)) {
					suspected.add(o);
					trigger(new Suspect(o),epfd);
				//	logger.info("process suspected {}", o);
				} 
				else if (alive.contains(o) && suspected.contains(o)) {

					suspected.remove(o);
					trigger(new Restore(o),epfd);
				//	logger.info("process restored {}", o);

				}
				
				trigger(new Pp2pSend(o, new HeartbeatRequestMessage(self, seqnum)), pp2p);
			//	logger.info("request heartbeat from {} with seqnum {}", o , seqnum);

			}
			alive.clear();

			CancelPeriodicTimeout ct = new CancelPeriodicTimeout(arg0.getTimeoutId());
			trigger(ct, timer);			

			SchedulePeriodicTimeout st = new SchedulePeriodicTimeout(delay, delta);
			st.setTimeoutEvent(new CheckTimeout(st));
			trigger(st, timer);	
		//	logger.info("Timeout reached {}, delay now is {}.", seqnum, delay);
		}

	};

	private Handler<HeartbeatRequestMessage> heartBeatRequest = new Handler<HeartbeatRequestMessage>() {
		@Override
		public void handle(HeartbeatRequestMessage arg0) {

			trigger(new Pp2pSend(arg0.getSource(), new HeartbeatReplyMessage(self, arg0.getSeqnum())), pp2p);
		}
	};

	private Handler<HeartbeatReplyMessage> heartBeatReply = new Handler<HeartbeatReplyMessage>() {
		@Override
		public void handle(HeartbeatReplyMessage arg0) {
			//logger.info("heartbeat request from {},with seqnum {}.", arg0.getSource(), arg0.getSeqnum());

			if (seqnum == arg0.getSeqnum() || suspected.contains(arg0.getSource())) {
				alive.add(arg0.getSource());
			}
		}
	};

}