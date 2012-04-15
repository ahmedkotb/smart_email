package wordFrequencyTools;

public class WordScore implements Comparable<WordScore> {
	public String word;
	public double score;
	private double eps = 1e-10;

	public WordScore(String word, double score) {
		this.word = word;
		this.score = score;
	}

	@Override
	public int compareTo(WordScore s) {
		if (score > s.score + eps)
			return -1;
		else if (score + eps < s.score)
			return 1;
		return 0;
	}
}
