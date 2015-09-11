package factorymethod;

public class FactoryMethodDriver {
	public static void main(String[] args) {
		RadioShack shack = new ShortwaveRadioShack();
		Radio radio = shack.orderRadio();
		System.out.println(radio.status());
		
		RadioShack shack2 = new LongwaveRadioShack();
		Radio radio2 = shack2.orderRadio();
		System.out.println(radio2.status());
	}
}
