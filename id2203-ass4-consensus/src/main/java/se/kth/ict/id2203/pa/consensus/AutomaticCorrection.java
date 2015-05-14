package se.kth.ict.id2203.pa.consensus;

import se.kth.ict.id2203.sim.consensus.ConsensusTester;

public class AutomaticCorrection {
	public static void main(String[] args) {
		String email = "anestos@kth.se";
		String password = "VNoZpo";
		ConsensusTester.correctAndSubmit(email, password);
	}
}
