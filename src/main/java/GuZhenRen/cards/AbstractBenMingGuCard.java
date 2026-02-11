package GuZhenRen.cards;

import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.badlogic.gdx.graphics.Color;

public abstract class AbstractBenMingGuCard extends AbstractGuZhenRenCard {

    // 【新增】静态开关：用于标记当前是否处于杀招组并阶段
    // static 意味着所有本命蛊共享这个状态
    public static boolean isSynthesizing = false;

    public int maxRank = 9;

    public AbstractBenMingGuCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        this.tags.add(GuZhenRenTags.BEN_MING_GU);
    }

    @Override
    public void onRemoveFromMasterDeck() {
        // 【核心修改】 如果正在组并杀招，直接跳过掉血逻辑
        if (isSynthesizing) {
            return;
        }

        if (AbstractDungeon.isPlayerInDungeon() && AbstractDungeon.player != null) {
            int damageAmount = (int)(AbstractDungeon.player.maxHealth * 0.8F);
            if (damageAmount < 1) damageAmount = 1;

            if (AbstractDungeon.player.currentHealth <= damageAmount) {
                damageAmount = AbstractDungeon.player.currentHealth - 1;
            }

            if (damageAmount > 0) {
                CardCrawlGame.sound.play("BLUNT_HEAVY");
                AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.RED));
                AbstractDungeon.player.damage(new DamageInfo(null, damageAmount, DamageInfo.DamageType.HP_LOSS));
            }
        }
    }

    @Override
    public boolean canUpgrade() {
        return this.rank < this.maxRank;
    }

    @Override
    public void upgrade() {
        if (canUpgrade()) {
            this.upgradeRank(1);
            performUpgradeEffect();
            this.applyPowers();
            if (!this.upgraded) {
                this.upgraded = true;
                this.initializeTitle();
            }
        }
    }

    public abstract void performUpgradeEffect();
}