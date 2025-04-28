import ch.aplu.jcardgame.Card;
import java.util.List;

public class OptionMix implements ScoreOption {
    private static final int THIRTEEN_GOAL = 13;

    @Override
    public boolean sum(List<Card> privateCards, List<Card> publicCards) {
        return isThirteenMixedCards(privateCards, publicCards);
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

        int maxScore = 0;
        if (isThirteenCards(privateCard1, publicCard1)) {
            int score = getScorePrivateCard(privateCard1) + getScorePublicCard(publicCard1);
            if (maxScore < score) {
                maxScore = score;
            }
        }

        if (isThirteenCards(privateCard1, publicCard2)) {
            int score = getScorePrivateCard(privateCard1) + getScorePublicCard(publicCard2);
            if (maxScore < score) {
                maxScore = score;
            }
        }

        if (isThirteenCards(privateCard2, publicCard1)) {
            int score = getScorePrivateCard(privateCard2) + getScorePublicCard(publicCard1);
            if (maxScore < score) {
                maxScore = score;
            }
        }

        if(isThirteenCards(privateCard2, publicCard2)) {
            int score = getScorePrivateCard(privateCard2) + getScorePublicCard(publicCard2);
            if (maxScore < score) {
                maxScore = score;
            }
        }
        return maxScore;
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

    private boolean isThirteenMixedCards(List<Card> privateCards, List<Card> publicCards) {
        for (Card privateCard : privateCards) {
            for (Card publicCard : publicCards) {
                if (isThirteenCards(privateCard, publicCard)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int getScorePublicCard(Card card) {
        LuckyThirdteen.Rank rank = (LuckyThirdteen.Rank) card.getRank();
        return rank.getScoreCardValue() * LuckyThirdteen.Suit.PUBLIC_CARD_MULTIPLICATION_FACTOR;
    }
}
