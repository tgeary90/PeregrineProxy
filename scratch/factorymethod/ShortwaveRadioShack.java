package factorymethod;

public class ShortwaveRadioShack extends RadioShack {

	@Override
	public Radio createRadio() {
		return new ShortwaveRadio(new Id(1), new Description("shortwave"));
	}
}
