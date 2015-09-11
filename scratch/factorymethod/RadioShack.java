package factorymethod;

/**
 * Class represents the Creator in implementing
 *  the Factory method pattern
 * @author tom
 */
public abstract class RadioShack {
	/**
	 * The operation
	 * @return
	 */
	public Radio orderRadio() {
		// TODO placeholder
		Radio radio = createRadio();
		radio.prep();
		return radio;
	}
	
	/**
	 * instantiation is handled by subclass - OCP
	 * @return
	 */
	protected abstract Radio createRadio();
}
