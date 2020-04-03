package org.linlinjava.litemall.db.util;

public class ScoreUtil {
    //3浏览商品，4广告，5邀请好友，6签到，7分红
    public static final Integer browse_goods = 3;
    public static final Integer ad = 4;
    public static final Integer share_friend = 5;
    public static final Integer sign_in = 6;
    public static final Integer share_money = 7;


    public static String getChannelName(Integer channel) {
        if (channel == 3) {
            return "浏览商品";
        }
        if (channel == 4) {
            return "广告";
        }
        if (channel == 5) {
            return "邀请好友";
        }
        if (channel == 6) {
            return "签到";
        }
        if (channel == 7) {
            return "分红";
        }
        return "";
    }


}
