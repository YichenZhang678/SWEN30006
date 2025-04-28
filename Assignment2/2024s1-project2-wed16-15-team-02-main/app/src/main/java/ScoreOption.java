import ch.aplu.jcardgame.Card;
import java.util.List;
public interface ScoreOption {
    public int score(List<Card> privateCards, List<Card> publicCards);
    public boolean sum(List<Card> privateCards, List<Card> publicCards);
}
