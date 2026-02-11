package GuZhenRen.cards;

import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // 【新增】
import com.megacrit.cardcrawl.cards.AbstractCard;
import java.util.ArrayList;

public abstract class AbstractShaZhaoCard extends AbstractGuZhenRenCard {

    public AbstractShaZhaoCard(String id, String name, String img, int cost, String rawDescription,
                               CardType type, CardTarget target) {
        super(id, name, img, cost, rawDescription,
                type,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL, // 【修改】逻辑上设为特殊，防止掉落
                target);

        this.tags.add(GuZhenRenTags.SHA_ZHAO);
        this.baseRank = 0;
        this.rank = 0;
    }

    // =========================================================================
    //  【核心新增】渲染欺诈：所有杀招在显示时都伪装成金卡
    // =========================================================================
    @Override
    public void render(SpriteBatch sb) {
        // 1. 保存真实稀有度
        CardRarity originalRarity = this.rarity;
        // 2. 伪装
        this.rarity = CardRarity.RARE;
        // 3. 绘制
        super.render(sb);
        // 4. 还原
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

    // 双重保险
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

        if (guPathString != null && !guPathString.isEmpty()) {
            sb.append(guPathString).append(" 。 ");
        }

        sb.append("guzhenren:杀招 。");
        sb.append(" NL ").append(this.myBaseDescription);

        return sb.toString();
    }

    @Override
    protected void setRank(int amount) {
        this.baseRank = 0;
        this.rank = 0;
    }
}