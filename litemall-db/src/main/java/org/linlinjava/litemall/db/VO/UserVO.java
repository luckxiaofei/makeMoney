package org.linlinjava.litemall.db.VO;

import org.linlinjava.litemall.db.domain.LitemallUser;

public class UserVO extends LitemallUser {
    private Integer score;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
