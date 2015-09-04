package tom.peregrine.client;

import java.net.URL;

import tom.apps.framework.ChannelFacade;


public abstract class PassHandler {
	public abstract void invoke(ChannelFacade facade, URL url);
}
