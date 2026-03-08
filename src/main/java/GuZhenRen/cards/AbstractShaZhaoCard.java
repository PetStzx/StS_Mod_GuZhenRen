package GuZhenRen.cards;

import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import java.util.ArrayList;

public abstract class AbstractShaZhaoCard extends AbstractGuZhenRenCard {

    public AbstractShaZhaoCard(String id, String name, String img, int cost, String rawDescription,
                               CardType type, CardTarget target) {
        super(id, name, img, cost, rawDescription,
                type,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                target);

        this.tags.add(GuZhenRenTags.SHA_ZHAO);
        this.baseRank = 0;
        this.rank = 0;
    }

    @Override
    public void render(SpriteBatch sb) {
        CardRarity originalRarity = this.rarity;
        this.rarity = CardRarity.RARE;
        super.render(sb);
        this.rarity = originalRarity;
    }

    @Override
    public void renderInLibrary(SpriteBatch sb) {
        CardRarity originalRarity = this.rarity;
        this.rarity = CardRarity.RARE;
        super.renderInLibrary(sb);
        this.rarity = originalRarity;
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    @Override
    public boolean canSpawn(ArrayList<AbstractCard> rewardCards) {
        return false;
    }

    @Override
    protected String constructRawDescription() {
        if (this.myBaseDescription == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        String separator = (TEXT.length > 9) ? TEXT[9] : " . ";

        if (guPathString != null && !guPathString.isEmpty()) {
            sb.append(guPathString).append(separator);
        }

        // TAG_TEXT[2] 即 "杀招"
        sb.append("guzhenren:").append(TAG_TEXT[2]).append(separator);
        sb.append(" NL ").append(this.myBaseDescription);

        return sb.toString();
    }

    @Override
    protected void setRank(int amount) {
        this.baseRank = 0;
        this.rank = 0;
    }
}