package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import java.util.ArrayList;

public class ShaGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("ShaGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ShaGu.png");

    private static final int COST = 0;
    private static final int INITIAL_RANK = 1;

    private static final int[] BASE_DMG =      {0, 6, 7, 8, 9, 10, 10, 11, 12, 13};
    private static final int[] DEATH_DMG_INC = {0, 1, 1, 1, 1,  1,  2,  2,  2,  3};

    public ShaGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.ENEMY);

        this.setDao(Dao.SHA_DAO);
        this.maxRank = 9;
        this.setRank(INITIAL_RANK);
        this.exhaust = false;

        calculateStats();
    }

    public void calculateStats() {
        int rankIndex = Math.min(Math.max(this.rank, 1), 9);
        this.baseDamage = BASE_DMG[rankIndex] + this.misc;
        this.baseMagicNumber = this.magicNumber = 1;
        this.baseSecondMagicNumber = this.secondMagicNumber = DEATH_DMG_INC[rankIndex];
        this.initializeDescription();
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        calculateStats();
        super.calculateCardDamage(mo);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateStats();
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();
        this.upgradedDamage = true;
        this.upgradedSecondMagicNumber = true;
    }

    @Override
    protected void onRankLoaded() {
        calculateStats();
    }


    @SpirePatch(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    public static class ShaGuMonsterDeathPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractMonster __instance, boolean triggerRelics) {
            if (AbstractDungeon.player == null) return;

            if (!__instance.halfDead && !__instance.hasPower("Minion")) {

                if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT) {

                    for (AbstractCard masterCard : AbstractDungeon.player.masterDeck.group) {
                        if (masterCard instanceof ShaGu) {

                            masterCard.misc += ((ShaGu) masterCard).secondMagicNumber;
                            ((ShaGu) masterCard).calculateStats();
                            masterCard.isDamageModified = false;

                            for (AbstractCard battleCard : GetAllInBattleInstances.get(masterCard.uuid)) {
                                battleCard.misc += ((ShaGu) battleCard).secondMagicNumber;
                                ((ShaGu) battleCard).calculateStats();
                                battleCard.applyPowers();
                                battleCard.isDamageModified = false;

                                if (AbstractDungeon.player.hand.contains(battleCard)) {
                                    battleCard.superFlash(Color.PURPLE.cpy());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}