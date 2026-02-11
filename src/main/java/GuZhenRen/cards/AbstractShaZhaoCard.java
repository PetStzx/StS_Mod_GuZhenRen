package GuZhenRen.cards;

import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard; // 确保导入
import java.util.ArrayList; // 确保导入

public abstract class AbstractShaZhaoCard extends AbstractGuZhenRenCard {

    public AbstractShaZhaoCard(String id, String name, String img, int cost, String rawDescription,
                               CardType type, CardTarget target) {
        super(id, name, img, cost, rawDescription,
                type,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE, // 虽然是金卡，但因为 canSpawn 返回 false，所以不会掉落
                target);

        this.tags.add(GuZhenRenTags.SHA_ZHAO);
        this.baseRank = 0;
        this.rank = 0;
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    // =========================================================================
    //  【核心修复】禁止自然掉落
    // =========================================================================
    @Override
    public boolean canSpawn(ArrayList<AbstractCard> rewardCards) {
        // 返回 false，表示这张牌绝对不会出现在战斗奖励、商店或随机生成的卡池中
        return false;
    }

    // =========================================================================
    //  描述构建逻辑
    // =========================================================================
    @Override
    protected String constructRawDescription() {
        if (this.myBaseDescription == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        // 1. 添加流派
        if (guPathString != null && !guPathString.isEmpty()) {
            sb.append(guPathString).append(" 。 ");
        }

        // 2. 添加杀招标签
        sb.append("guzhenren:杀招 。");

        // 3. 添加正文
        sb.append(" NL ").append(this.myBaseDescription);

        return sb.toString();
    }

    @Override
    protected void setRank(int amount) {
        this.baseRank = 0;
        this.rank = 0;
    }
}