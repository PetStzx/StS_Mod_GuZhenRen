package GuZhenRen.potions;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomPotion;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class ShengJiYe extends CustomPotion {

    public static final String POTION_ID = GuZhenRen.makeID("ShengJiYe");
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    public ShengJiYe() {
        // ID, Name, Rarity, Size, Color
        super(NAME, POTION_ID, PotionRarity.COMMON, PotionSize.H, PotionColor.GREEN);

        // 是否是可以投掷的药水（比如火焰药水是true，回血药水是false）
        this.isThrown = false;
    }

    @Override
    public void initializeData() {
        this.potency = getPotency();
        // 描述：回复 #b6 点生命。
        this.description = DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];

        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public void use(AbstractCreature target) {
        // 目标通常是玩家自己
        AbstractCreature p = AbstractDungeon.player;
        // 如果是在战斗中
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            this.addToBot(new HealAction(p, p, this.potency));
        } else {
            // 如果是在战斗外使用（直接回血）
            p.heal(this.potency);
        }
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 6; // 基础回复量 6
    }

    @Override
    public AbstractPotion makeCopy() {
        return new ShengJiYe();
    }
}