package GuZhenRen.cards;

import GuZhenRen.util.IProbabilityCard;
import GuZhenRen.util.ProbabilityHelper;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public abstract class AbstractXuYingCard extends AbstractGuZhenRenCard implements IProbabilityCard {

    public float baseChanceFloat;
    public boolean isPhantomExecuting = false;
    protected AbstractCard animatedPhantomCard;

    public AbstractXuYingCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, CardRarity.SPECIAL, target);
        this.selfRetain = true;
        this.setDao(Dao.LI_DAO);
    }

    @Override
    public void increaseBaseChance(float amount) {
        this.baseChanceFloat += amount;
        if (this.baseChanceFloat > 1.0f) this.baseChanceFloat = 1.0f;
        this.initializeDescription();
    }

    @Override
    public float getBaseChance() {
        return this.baseChanceFloat;
    }

    @Override
    public AbstractGuZhenRenCard makeStatEquivalentCopy() {
        AbstractXuYingCard c = (AbstractXuYingCard) super.makeStatEquivalentCopy();
        c.baseChanceFloat = this.baseChanceFloat;
        return c;
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void update() {
        super.update();
        if (this.isPhantomExecuting && !this.isGlowing) {
            this.glowColor = Color.CYAN.cpy();
            this.beginGlowing();
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        if (this.isPhantomExecuting) {
            this.glowColor = Color.CYAN.cpy();
            this.beginGlowing();
        }
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        this.isPhantomExecuting = false;
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (!AbstractDungeon.player.hand.contains(this)) return;
        if (c.tags.contains(GuZhenRenTags.XU_YING_COPY)) return;

        if (c.type == CardType.ATTACK && !(c instanceof AbstractXuYingCard)) {
            if (ProbabilityHelper.rollProbability(this, this.baseChanceFloat)) {
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        queuePhantomAnimationAndEffect(m);
                        this.isDone = true;
                    }
                });
            }
        }
    }

    protected AbstractCard getCardForAnimation() {
        return this;
    }

    public void queuePhantomAnimationAndEffect(final AbstractMonster originalTarget) {
        this.isPhantomExecuting = true;
        this.glowColor = Color.CYAN.cpy();
        this.beginGlowing();
        this.superFlash(Color.CYAN.cpy());

        AbstractCard sourceCard = getCardForAnimation();
        if (sourceCard == null) {
            this.isPhantomExecuting = false;
            this.stopGlowing();
            return;
        }

        final AbstractCard copy = sourceCard.makeStatEquivalentCopy();
        this.animatedPhantomCard = copy;

        copy.current_x = this.current_x;
        copy.current_y = this.current_y;
        copy.drawScale = this.drawScale;
        copy.angle = 0.0f;
        copy.targetAngle = 0.0f;
        copy.target_x = Settings.WIDTH / 2.0F + MathUtils.random(-400.0F, 400.0F) * Settings.scale;
        copy.target_y = Settings.HEIGHT / 2.0F + MathUtils.random(-250.0F, 250.0F) * Settings.scale;
        copy.targetDrawScale = 0.71F;

        AbstractDungeon.player.limbo.addToTop(copy);

        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            private boolean first = true;
            {
                this.actionType = ActionType.WAIT;
                this.duration = Settings.FAST_MODE ? 0.3F : 0.5F;
            }
            @Override
            public void update() {
                if (first) {
                    if (AbstractDungeon.player.limbo.contains(copy)) {
                        AbstractDungeon.player.limbo.removeCard(copy);
                        AbstractDungeon.player.limbo.addToTop(copy);
                    }
                    copy.target_x = Settings.WIDTH / 2.0F;
                    copy.target_y = Settings.HEIGHT / 2.0F;
                    copy.targetDrawScale = 0.9F;
                    first = false;
                }
                this.tickDuration();
            }
        });

        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractMonster validTarget = originalTarget;
                if (validTarget == null || validTarget.isDeadOrEscaped() || validTarget.currentHealth <= 0) {
                    validTarget = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                }
                triggerPhantomEffect(validTarget);
                this.isDone = true;
            }
        });

        AbstractDungeon.actionManager.addToBottom(new WaitAction(Settings.FAST_MODE ? 0.2F : 0.4F));

        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.limbo.removeCard(copy);
                AbstractXuYingCard.this.isPhantomExecuting = false;
                AbstractXuYingCard.this.stopGlowing();
                this.isDone = true;
            }
        });

        AbstractDungeon.actionManager.addToBottom(new WaitAction(Settings.FAST_MODE ? 0.2F : 0.35F));
    }

    public abstract void triggerPhantomEffect(AbstractMonster m);

    @Override
    protected String constructRawDescription() {
        if (this.myBaseDescription == null) return "";

        StringBuilder sb = new StringBuilder();
        String separator = (TEXT.length > 9) ? TEXT[9] : " . ";

        sb.append("guzhenren:").append(TEXT[12].replace(" ", "_")).append(separator);
        sb.append("guzhenren:").append(TAG_TEXT[3].replace(" ", "_")).append(separator).append(" NL ");

        sb.append(this.myBaseDescription.replace("{CHANCE}", ProbabilityHelper.getDynamicColorString(this, this.baseChanceFloat)));

        return sb.toString();
    }
}