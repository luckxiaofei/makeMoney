package org.linlinjava.litemall.wx.service;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.core.task.TaskService;
import org.linlinjava.litemall.db.dao.LitemallScoreMapper;
import org.linlinjava.litemall.db.dao.LitemallUserFriendMapper;
import org.linlinjava.litemall.db.dao.LitemallUserMoneyHistoryMapper;
import org.linlinjava.litemall.db.dao.LitemallUserMoneyMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.task.OrderUnpaidTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WxOrderServiceTest {

    private final Log logger = LogFactory.getLog(WxOrderServiceTest.class);
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallUserMoneyHistoryMapper userMoneyHistoryMapper;
    @Autowired
    private LitemallUserFriendMapper userFriendMapper;
    @Autowired
    private LitemallUserMoneyMapper userMoneyMapper;
    @Autowired
    private LitemallScoreMapper scoreMapper;
    @Autowired
    private TaskService taskService;
    @Test
    public void prepay() {
        payNotify();
    }




    public String payNotify() {
        logger.info("处理腾讯支付平台的订单支付");

        String orderSn = "";
        String payId = "1212";

        // 分转化成元
        String totalFee = BaseWxPayResult.fenToYuan(12);
        LitemallOrder order = orderService.findBySn(orderSn);
        if (order != null) {
            Integer giveScore = order.getGiveScore();
            BigDecimal commission = order.getCommission();
            Integer userId = order.getUserId();
            LitemallUserFriendExample example = new LitemallUserFriendExample();
            example.createCriteria().andFriendUserIdEqualTo(userId);
            LitemallUserFriend userFriend = userFriendMapper.selectOneByExample(example);
            if (userFriend != null && userFriend.getFriendUserId() != null) {
                //上级的用户id
                Integer upUserid = userFriend.getUserid();

                //佣金添加到上级
                LitemallUserMoneyExample moneyExample = new LitemallUserMoneyExample();
                moneyExample.createCriteria().andUseridEqualTo(upUserid);
                LitemallUserMoney userMoneyOld = userMoneyMapper.selectOneByExample(moneyExample);
                userMoneyOld.setMoney(userMoneyOld.getMoney().add(commission));
                userMoneyMapper.updateByPrimaryKey(userMoneyOld);

                LitemallUserMoneyHistory userMoneyHistory = new LitemallUserMoneyHistory();
                userMoneyHistory.setAddTime(LocalDateTime.now());
                userMoneyHistory.setOrderNumber(orderSn);
                userMoneyHistory.setTypename("佣金");
                userMoneyHistory.setType(1);
                userMoneyHistory.setUserid(upUserid);
                userMoneyHistory.setMoney(commission);
                userMoneyHistoryMapper.insertSelective(userMoneyHistory);
            }
            if (orderSn.contains("CZ")){
                LitemallUserMoneyHistory userMoneyHistory = new LitemallUserMoneyHistory();
                userMoneyHistory.setMoney(new BigDecimal(totalFee));
                userMoneyHistory.setUserid(order.getUserId());
                userMoneyHistory.setType(3);
                userMoneyHistory.setTypename("充值");
                userMoneyHistory.setOrderNumber(orderSn);
                userMoneyHistory.setAddTime(LocalDateTime.now());
                userMoneyHistoryMapper.insert(userMoneyHistory);
            }
            LitemallScoreExample scoreExample = new LitemallScoreExample();
            scoreExample.createCriteria().andUseridEqualTo(userId);
            LitemallScore litemallScoreOld = scoreMapper.selectOneByExample(scoreExample);
            litemallScoreOld.setScore(litemallScoreOld.getScore() + giveScore - order.getOrderScore());
            scoreMapper.insertSelective(litemallScoreOld);
        } else {

        }
        // 检查这个订单是否已经处理过
        if (OrderUtil.hasPayed(order)) {
            return WxPayNotifyResponse.success("订单已经处理成功!");
        }

        // 检查支付订单金额
        if (!totalFee.equals(order.getActualPrice().toString())) {
            return WxPayNotifyResponse.fail(order.getOrderSn() + " : 支付金额不符合 totalFee=" + totalFee);
        }

        order.setPayId(payId);
        order.setPayTime(LocalDateTime.now());
        order.setOrderStatus(OrderUtil.STATUS_PAY);
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return WxPayNotifyResponse.fail("更新数据已失效");
        }

        taskService.removeTask(new OrderUnpaidTask(order.getId()));

        return WxPayNotifyResponse.success("处理成功!");
    }
}
