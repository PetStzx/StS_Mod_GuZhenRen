package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.relics.ChiXiang;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import com.megacrit.cardcrawl.vfx.combat.HeartMegaDebuffEffect;

public class ChiXin extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("ChiXin");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ChiXin.png");

    private static final int COST = 2;
    private static final int MAX_HP_GAIN = 2;

    public ChiXin() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.SHI_DAO);

        this.baseMagicNumber = this.magicNumber = MAX_HP_GAIN;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int killCount = 0;
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (mo != null && !mo.isDeadOrEscaped() && !mo.halfDead && mo.currentHealth < p.maxHealth) {
                        killCount++;
                    }
                }

                if (killCount > 0) {
                    final int finalKillCount = killCount;

                    AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                        @Override
                        public void update() {
                            int actualGain = ChiXin.this.magicNumber * finalKillCount;

                            if (p.hasRelic(ChiXiang.ID)) {
                                p.getRelic(ChiXiang.ID).flash();
                                actualGain *= 2;
                            }

                            p.increaseMaxHp(actualGain, true);
                            this.isDone = true;
                        }
                    });

                    for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        if (mo != null && !mo.isDeadOrEscaped() && !mo.halfDead && mo.currentHealth < p.maxHealth) {
                            AbstractDungeon.actionManager.addToTop(new InstantKillAction(mo));
                        }
                    }

                    AbstractDungeon.actionManager.addToTop(new WaitAction(0.5F));

                    AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                        @Override
                        public void update() {
                            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                                if (mo != null && !mo.isDeadOrEscaped() && !mo.halfDead && mo.currentHealth < p.maxHealth) {
                                    AbstractDungeon.effectList.add(new BiteEffect(mo.hb.cX, mo.hb.cY - 40.0F * Settings.scale, Color.PURPLE.cpy()));
                                }
                            }
                            this.isDone = true;
                        }
                    });

                    AbstractDungeon.actionManager.addToTop(new VFXAction(new HeartMegaDebuffEffect(), 1.0F));
                }

                this.isDone = true;
            }
        });
    }
}