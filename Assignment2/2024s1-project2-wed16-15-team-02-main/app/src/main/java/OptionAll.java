import ch.aplu.jcardgame.Card;
import java.util.List;

public class OptionAll implements ScoreOption {
    private static final int THIRTEEN_GOAL = 13;

    public OptionAll() {
    }

    @Override
    public boolean sum(List<Card> privateCards, List<Card> publicCards) {
        Card privateCard1 = privateCards.get(0);
        Card privateCard2 = privateCards.get(1);
        Card publicCard1 = publicCards.get(0);
        Card publicCard2 = publicCards.get(1);
        return isThirteenAllCards(privateCard1, privateCard2, publicCard1, publicCard2);
    }
    @Override
    public int score(List<Card> privateCards, List<Card> publicCards){
        Card privateCard1 = privateCards.get(0);
        Card privateCard2 = privateCards.get(1);
        Card publicCard1 = publicCards.get(0);
        Card publicCard2 = publicCards.get(1);
        if(!sum(privateCards, publicCards)){
            return 0;
        }
        int score = getScorePrivateCard(privateCard2) + getScorePublicCard(publicCard2) + getScorePrivateCard(privateCard1) + getScorePublicCard(publicCard1);
        return score;
    }

    private int getScorePrivateCard(Card card) {
        LuckyThirdteen.Rank rank = (LuckyThirdteen.Rank) card.getRank();
        LuckyThirdteen.Suit suit = (LuckyThirdteen.Suit) card.getSuit();

        return rank.getScoreCardValue() * suit.getMultiplicationFactor();
    }

    private int getScorePublicCard(Card card) {
        LuckyThirdteen.Rank rank = (LuckyThirdteen.Rank) card.getRank();
        return rank.getScoreCardValue() * LuckyThirdteen.Suit.PUBLIC_CARD_MULTIPLICATION_FACTOR;
    }

    private boolean isThirteenAllCards(Card card1, Card card2, Card card3, Card card4) {
        LuckyThirdteen.Rank rank1 = (LuckyThirdteen.Rank) card1.getRank();
        LuckyThirdteen.Rank rank2 = (LuckyThirdteen.Rank) card2.getRank();
        LuckyThirdteen.Rank rank3 = (LuckyThirdteen.Rank) card3.getRank();
        LuckyThirdteen.Rank rank4 = (LuckyThirdteen.Rank) card4.getRank();
        return isThirteenFromALlPossibleValues(rank1.getPossibleSumValues(), rank2.getPossibleSumValues(), rank3.getPossibleSumValues(), rank4.getPossibleSumValues());
    }

    private boolean isThirteenFromALlPossibleValues(int[] possibleValues1, int[] possibleValues2, int[] possibleValues3, int[] possibleValues4){
        for (int value1 : possibleValues1) {
            for (int value2 : possibleValues2) {
                for (int value3 : possibleValues3) {
                    for (int value4 : possibleValues4) {
                        if (value1 + value2 + value3 + value4 == THIRTEEN_GOAL) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
