package se.sics.kompics.tutorial.hello;

import se.sics.kompics.PortType;

public class HelloWorld extends PortType {
	{
		indication(World.class);
		request(Hello.class);
	}
}
