package tom.cache.impl;

import tom.cache.Handler;
import tom.cache.Logger;
import tom.cache.Resource;

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
	
	public Runnable getValidator(WebResource resource, Handler handler, Logger logger)
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
