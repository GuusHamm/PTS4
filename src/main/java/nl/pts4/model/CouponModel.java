package nl.pts4.model;

public class CouponModel {

    private AccountModel receiver;
    private float percent;

    public CouponModel(AccountModel receiver, float percent) {
        this.receiver = receiver;
        this.percent = percent;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
