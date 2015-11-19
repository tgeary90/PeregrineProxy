package tom.apps.cache.impl;

import tom.apps.cache.CacheClient;
import tom.apps.cache.Logger;
import tom.apps.cache.Resource;

/**
 * Class returns a Validator if reference is null otherwise the mock is returned.
 */
public class ValidatorFactory
{
	Runnable validator = null;
	
	public void setValidator(Runnable mockValidator)
	{
		validator = mockValidator;
	}
	
	public void resetValidator()
	{
		validator = null;
	}
	
	public Runnable getValidator(WebResource resource, CacheClient handler, Logger logger)
	{
		if (validator == null)
		{
			return new Validator(resource, handler, logger);
		}
		else
		{
			return validator;
		}
	}
}
