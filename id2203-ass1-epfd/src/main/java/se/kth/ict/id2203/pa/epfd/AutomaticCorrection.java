package se.kth.ict.id2203.pa.epfd;

import se.kth.ict.id2203.sim.epfd.EpfdTester;

public class AutomaticCorrection {
	public static void main(String[] args) {
		String email = "anestos@kth.se";
		String password = "VNoZpo";
		EpfdTester.correctAndSubmit(email, password);
	}
}
