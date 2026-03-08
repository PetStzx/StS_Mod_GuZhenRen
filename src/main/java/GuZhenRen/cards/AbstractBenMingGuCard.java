package GuZhenRen.cards;

import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.rooms.AbstractRoom; // 确保导入了这个

public abstract class AbstractBenMingGuCard extends AbstractGuZhenRenCard {

    // 静态开关：用于标记当前是否处于杀招组并阶段
    public static boolean isSynthesizing = false;

    public int maxRank = 9;

    public AbstractBenMingGuCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        this.tags.add(GuZhenRenTags.BEN_MING_GU);
    }

    @Override
    public void onRemoveFromMasterDeck() {
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

            // 1. 调用 applyPowers 更新数值
            if (AbstractDungeon.isPlayerInDungeon() && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                // 身份验证
                boolean inCombatGroup = AbstractDungeon.player.hand.contains(this) ||
                        AbstractDungeon.player.drawPile.contains(this) ||
                        AbstractDungeon.player.discardPile.contains(this) ||
                        AbstractDungeon.player.limbo.contains(this) ||
                        AbstractDungeon.player.exhaustPile.contains(this);

                if (inCombatGroup) {
                    this.applyPowers();
                }
            }

            // 2. 清洗大师牌组数据
            if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck.contains(this)) {
                this.damage = this.baseDamage;
                this.isDamageModified = false;

                this.block = this.baseBlock;
                this.isBlockModified = false;

                this.magicNumber = this.baseMagicNumber;
                this.isMagicNumberModified = false;

                this.fenShao = this.baseFenShao;
                this.isFenShaoModified = false;
            }

            if (!this.upgraded) {
                this.upgraded = true;
                this.initializeTitle();
            }
        }
    }

    public abstract void performUpgradeEffect();
}