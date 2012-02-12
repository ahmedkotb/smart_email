
public class Main {

	public static void main(String[] args) {
		String str = "moUstafa Mahmoud    aly... Student at Alex. Uni";
		String[] toks = str.split("[^a-zA-Z]+");
		System.out.println(toks.length);
		for(int i=0; i<toks.length; i++) System.out.println("\"" + toks[i] + "\"");
		System.out.println("Hello World!");
	}
}
