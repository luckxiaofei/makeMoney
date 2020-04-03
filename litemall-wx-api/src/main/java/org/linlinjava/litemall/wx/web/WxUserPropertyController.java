package org.linlinjava.litemall.wx.web;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.core.qcode.QCodeService;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.LitemallUserPropertyService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/userProperty")
@Validated
public class WxUserPropertyController {
    @Autowired
    private LitemallUserPropertyService userPropertyService;
    @Autowired
    private QCodeService qCodeService;


    @GetMapping("/getUserMoney")
    public Object getUserMoney(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.badArgumentValue();
        }
        LitemallUserMoney userMoney = userPropertyService.getUserMoney(userId);
        return ResponseUtil.ok(userMoney);
    }

    @GetMapping("/getUserMoneyHistory")
    public Object getUserMoneyHistory(@LoginUser Integer userId, boolean onlyCommission, @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer limit) {
        if (userId == null) {
            return ResponseUtil.badArgumentValue();
        }
        List<LitemallUserMoneyHistory> userMoneyHistory = userPropertyService.getUserMoneyHistory(userId, onlyCommission, page, limit);
        return ResponseUtil.okList(userMoneyHistory);
    }

    @GetMapping("/getSumScoreByUseId")
    public Object getSumScoreByUseId(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.badArgumentValue();
        }
        LitemallScore userFriemds = userPropertyService.queryScoreByUseId(userId);
        return ResponseUtil.ok(userFriemds);
    }

    @GetMapping("/getUserFriemds")
    public Object getUserFriemds(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.badArgumentValue();
        }
        List<LitemallUserFriend> userFriemds = userPropertyService.getUserFriemds(userId);
        return ResponseUtil.okList(userFriemds);
    }

    @GetMapping("/addFriend")
    public Object addFriend(@LoginUser Integer userId, Integer shareUserId) {
        if (userId == null) {
            return ResponseUtil.badArgumentValue();
        }
        userPropertyService.addFriend(userId, shareUserId);
        return ResponseUtil.ok();
    }

    /**
     * @param userId
     * @param type   3浏览商品，4广告，5邀请好友，6签到
     * @return
     */
    @GetMapping("/updateScore")
    public Object updateScore(@LoginUser Integer userId, Integer type) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer integer = userPropertyService.updateScore(userId, type);
        return ResponseUtil.ok(integer);
    }

    /**
     * 青豆前十名
     *
     * @return
     */
    @GetMapping("/getScoreTop")
    public Object getScoreTop() {
        List<LitemallScore> scoreList = userPropertyService.getScoreTop();
        return ResponseUtil.ok(scoreList);
    }

    @GetMapping("/getScoreHistory")
    public Object getScoreHistory(@LoginUser Integer userId, @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer limit) {
        List<LitemallScoreHistory> scoreList = userPropertyService.getScoreHistory(userId, page, limit);
        return ResponseUtil.okList(scoreList);
    }

    @GetMapping("/getScoreBaseInfo")
    public Object getScoreBaseInfo(@LoginUser Integer userId) {
        Map<String, Object> scoreBaseInfo = userPropertyService.getScoreBaseInfo(userId);
        String notice_info = SystemConfig.getSystemConfigs().get(SystemConfig.NOTICE_INFO);
        String rule_info = SystemConfig.getSystemConfigs().get(SystemConfig.RULE_INFO);
        scoreBaseInfo.put("notice_info",notice_info);
        scoreBaseInfo.put("rule_info",rule_info);
        return ResponseUtil.ok(scoreBaseInfo);
    }

    @GetMapping("/createFriendShareImage")
    public Object createFriendShareImage(@LoginUser Integer userId) {
        if (userId==null){
            return ResponseUtil.unlogin();
        }
        String friendShareImageUrl = qCodeService.createFriendShareImage(userId);
        return ResponseUtil.ok(friendShareImageUrl);
    }
}
