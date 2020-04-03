package org.linlinjava.litemall.db.util;

public class MoneyUtil {
//1下级佣金，2分红，3充值


    public static final Integer commission = 1;
    public static final Integer share_money = 2;
    public static final Integer cz = 3;
    public static final Integer tx = 3;


    public static String getName(Integer type) {
        if (type == 1) {
            return "下级佣金";
        }
        if (type == 2) {
            return "分红";
        }
        if (type == 3) {
            return "充值";
        }
        if (type == 4) {
            return "提现";
        }
        return "";
    }


}
