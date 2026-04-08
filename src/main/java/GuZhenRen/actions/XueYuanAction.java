package GuZhenRen.actions;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.XueYuanMarkPower;
import GuZhenRen.powers.XueYuanPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;

public class XueYuanAction extends AbstractGameAction {
    private AbstractPlayer p;
    private AbstractMonster m;
    private int magic;

    public XueYuanAction(AbstractPlayer p, AbstractMonster m, int magic) {
        this.p = p;
        this.m = m;
        this.magic = magic;
        this.actionType = ActionType.POWER;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            boolean mHasArtifact = this.m.hasPower(ArtifactPower.POWER_ID);
            boolean pHasImmunity = this.p.hasPower(GuZhenRen.makeID("WanWuDaTongBianPower"));

            if (pHasImmunity) {
                this.addToTop(new ApplyPowerAction(this.p, this.p, new XueYuanPower(this.p,-1)));
            }
            if (mHasArtifact) {
                this.addToTop(new ApplyPowerAction(this.m, this.p, new XueYuanMarkPower(this.m, this.magic), this.magic));
            }

            if (!pHasImmunity && !mHasArtifact) {
                if (!this.p.hasPower(XueYuanPower.POWER_ID)) {
                    this.addToTop(new ApplyPowerAction(this.p, this.p, new XueYuanPower(this.p,-1)));
                }
                this.addToTop(new ApplyPowerAction(this.m, this.p, new XueYuanMarkPower(this.m, this.magic), this.magic));
            }

            this.isDone = true;
        }
    }
}