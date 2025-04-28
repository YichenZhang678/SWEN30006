import ch.aplu.jcardgame.Card;
import java.util.List;

public class OptionPrivate implements ScoreOption {
    private static final int THIRTEEN_GOAL = 13;

    public OptionPrivate(){

    }
    @Override
    public boolean sum(List<Card> privateCards, List<Card> publicCards) {
        Card privateCard1 = privateCards.get(0);
        Card privateCard2 = privateCards.get(1);
        return isThirteenCards(privateCard1, privateCard2);
    }
    @Override
    public int score(List<Card> privateCards, List<Card> publicCards){
        Card privateCard1 = privateCards.get(0);
        Card privateCard2 = privateCards.get(1);
        if(!sum(privateCards, publicCards)){
            return 0;
        }

        return getScorePrivateCard(privateCard1) + getScorePrivateCard(privateCard2);
    }

    private boolean isThirteenCards(Card card1, Card card2) {
        LuckyThirdteen.Rank rank1 = (LuckyThirdteen.Rank) card1.getRank();
        LuckyThirdteen.Rank rank2 = (LuckyThirdteen.Rank) card2.getRank();
        return isThirteenFromPossibleValues(rank1.getPossibleSumValues(), rank2.getPossibleSumValues());
    }

    private boolean isThirteenFromPossibleValues(int[] possibleValues1, int[] possibleValues2) {
        for (int value1 : possibleValues1) {
            for (int value2 : possibleValues2) {
                if (value1 + value2 == THIRTEEN_GOAL) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getScorePrivateCard(Card card) {
        LuckyThirdteen.Rank rank = (LuckyThirdteen.Rank) card.getRank();
        LuckyThirdteen.Suit suit = (LuckyThirdteen.Suit) card.getSuit();

        return rank.getScoreCardValue() * suit.getMultiplicationFactor();
    }
}
