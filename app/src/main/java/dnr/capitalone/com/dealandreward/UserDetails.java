package dnr.capitalone.com.dealandreward;

/**
 * Created by RichardYan on 7/23/15.
 */
public class UserDetails {


    private String rewardsAmount;
    private String directReferalRewards;
    private String subrefRewards;

    @Override
    public String toString() {
        return "userRewardsAmount is: " + rewardsAmount +
                "secondLevelRewardsAmount is: " + directReferalRewards +
                "thirdLevelRewardsAmount is: " + subrefRewards;
    }

    public String getRewardsAmount() {
        return rewardsAmount;
    }

    public void setRewardsAmount(String rewardsAmount) {
        this.rewardsAmount = rewardsAmount;
    }

    public String getDirectReferralRewards() {
        return directReferalRewards;
    }

    public void setDirectReferralRewards(String directReferralRewards) {
        this.directReferalRewards = directReferralRewards;
    }

    public String getSubrefRewards() {
        return subrefRewards;
    }

    public void setSubrefRewards(String subrefRewards) {
        this.subrefRewards = subrefRewards;
    }
}
