import ch.aplu.jcardgame.Card;

import java.util.List;

public interface Play {
    public Card selectCard(Card dealt);
    public Card selectCard(Card dealt,List<Card>discardCards,List<Card> publicCards);
}
