package se.sics.kompics.tutorial.hello;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;

public class Main extends ComponentDefinition {

	public Main() {
		System.out.println("Main created.");
		Component component1 = create(Component1.class, Init.NONE);
		Component component2 = create(Component2.class, Init.NONE);
		connect(component1.required(HelloWorld.class),
				component2.provided(HelloWorld.class));
	}

	public static void main(String[] args) {
		Kompics.createAndStart(Main.class, 1);
	}
}
