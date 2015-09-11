package factorymethod;

public class LongwaveRadioShack extends RadioShack {

	@Override
	public Radio createRadio() {
		return new LongwaveRadio(new Id(2), new Description("Long wave radio"));
	}
	
}
