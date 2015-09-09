package tom.peregrine.server;

import java.net.URL;

import tom.apps.framework.ChannelFacade;

public interface PassHandler {
	void invoke(ChannelFacade facade, URL url);
}
