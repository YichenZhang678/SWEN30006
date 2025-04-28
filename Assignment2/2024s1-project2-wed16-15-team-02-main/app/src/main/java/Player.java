import ch.aplu.jcardgame.*;

import java.util.List;

public abstract class Player implements Play{
    public abstract Card selectCard(Card dealt);

    //overrides only in clever for now
    public Card selectCard(Card dealt,List<Card>discardCards,List<Card> publicCards){
        return selectCard(dealt);
    }
    protected abstract Card applyAutoMovement(Card dealt);

    protected LuckyThirdteen.Rank getRankFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        Integer rankValue = Integer.parseInt(rankString);

        for (LuckyThirdteen.Rank rank : LuckyThirdteen.Rank.values()) {
            if (rank.getRankCardValue() == rankValue) {
                return rank;
            }
        }

        return LuckyThirdteen.Rank.ACE;
    }

    protected LuckyThirdteen.Suit getSuitFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        String suitString = cardName.substring(cardName.length() - 1, cardName.length());
        Integer rankValue = Integer.parseInt(rankString);

        for (LuckyThirdteen.Suit suit : LuckyThirdteen.Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return LuckyThirdteen.Suit.CLUBS;
    }

    protected Card getCardFromList(List<Card> cards, String cardName) {
        LuckyThirdteen.Rank cardRank = getRankFromString(cardName);
        LuckyThirdteen.Suit cardSuit = getSuitFromString(cardName);
        for (Card card: cards) {
            if (card.getSuit() == cardSuit
                    && card.getRank() == cardRank) {
                return card;
            }
        }


        return null;
    }
    public abstract void calculateScore(List<Card> publicCards);
    public abstract int getScore();
    public abstract void setPlayerAutoMovement(List<String> movement);
    public abstract void setHand(Deck deck);

    public abstract void insertCard(Card card);

    public abstract void sortCard();

    public abstract Hand getHand();
    public abstract String getNextMovement();

    public abstract boolean getIsFinishedAuto();
    public abstract boolean getWinState();


}