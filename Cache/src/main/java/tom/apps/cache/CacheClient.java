/**
 * File: Handler.java
 * Creation Date: 24/07/2012
 * Last Modification Date: 05/08/2012
 * @author: Tom Geary 
 * @version 1.1
 *
 * Description: Defines an interface that client handlers should implement 
 * to provide handling services to the proxy server. Handlers are designed
 * to be run concurrently, fielding HTTP requests from clients, so Handler
 * extends Runnable. In parallel processing terms a Handler represents a 
 * task boundary.
 */

package tom.apps.cache;

import tom.apps.cache.Resource;


public interface CacheClient
{
    public void validated(Resource resource);
}