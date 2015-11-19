package tom.apps.proxy.server;

import java.net.URL;

import tom.frameworks.ChannelFacade;

public interface PassHandler {
	void invoke(ChannelFacade facade, URL url);
}
