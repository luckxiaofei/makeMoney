package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.dao.*;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.util.MoneyUtil;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.db.util.ScoreUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LitemallUserPropertyService {
    private final Log logger = LogFactory.getLog(LitemallUserPropertyService.class);
    @Resource
    private LitemallScoreRuleMapper scoreRuleMapper;
    @Resource
    private LitemallScoreHistoryMapper scoreHistoryMapper;
    @Resource
    private LitemallScoreMapper scoreMapper;
    @Resource
    private LitemallUserMoneyMapper userMoneyMapper;
    @Resource
    private LitemallUserMoneyHistoryMapper userMoneyHistoryMapper;
    @Resource
    private LitemallUserFriendMapper userFriendMapper;
    @Resource
    private LitemallUserMapper litemallUserMapper;
    @Resource
    private LitemallOrderMapper litemallOrderMapper;
    @Resource
    private LitemallUserMapper userMapper;


    /**
     * @param history
     * @return
     */
    public int createHistory(LitemallScoreHistory history) {
        history.setAddTime(LocalDateTime.now());
        return scoreHistoryMapper.insertSelective(history);
    }

    /**
     * @param ruleType
     * @param page
     * @param size
     * @param sort
     * @param order
     * @return
     */
    public List<LitemallScoreRule> queryRuleSelective(Integer ruleType, Integer page, Integer size, String sort, String order) {
        LitemallScoreRuleExample example = new LitemallScoreRuleExample();
        example.setOrderByClause(sort + " " + order);

        LitemallScoreRuleExample.Criteria criteria = example.createCriteria();
        if (ruleType != null) {
            criteria.andTypeEqualTo(ruleType);
        }
        PageHelper.startPage(page, size);
        return scoreRuleMapper.selectByExample(example);
    }

    /**
     * @param userId
     * @param page
     * @param size
     * @param sort
     * @param order
     * @return
     */
    public List<LitemallScoreHistory> queryHistorySelective(Integer userId, Integer page, Integer size, String sort, String order) {
        LitemallScoreHistoryExample example = new LitemallScoreHistoryExample();
        example.setOrderByClause(sort + " " + order);

        LitemallScoreHistoryExample.Criteria criteria = example.createCriteria();

        if (userId != null) {
            criteria.andUseridEqualTo(userId);
        }
        PageHelper.startPage(page, size);
        return scoreHistoryMapper.selectByExample(example);
    }


    /**
     * @param userId
     * @return
     */
    public LitemallScore queryScoreByUseId(Integer userId) {
        LitemallScoreExample example = new LitemallScoreExample();
        example.or().andUseridEqualTo(userId);
        LitemallScore litemallScores = scoreMapper.selectOneByExample(example);
        return litemallScores;
    }


    public LitemallScoreRule queryRuleById(Integer id) {
        return scoreRuleMapper.selectByPrimaryKey(id);
    }

    public int updateRuleById(LitemallScoreRule scoreRule) {
        return scoreRuleMapper.updateByPrimaryKeySelective(scoreRule);
    }

    /** start  handle userMoeny */

    /**
     * 获取用户钱包金额
     *
     * @param userId
     * @return
     */
    public LitemallUserMoney getUserMoney(Integer userId) {
        LitemallUserMoneyExample example = new LitemallUserMoneyExample();
        example.or().andUseridEqualTo(userId);
        LitemallUserMoney litemallUserMonies = userMoneyMapper.selectOneByExample(example);
        return litemallUserMonies;
    }

    /**
     * 获取用户钱包历史记录
     *
     * @param id
     * @param userId
     * @param onlyCommission
     * @param page
     * @return
     */
    public List<LitemallUserMoneyHistory> getUserMoneyHistory(Integer userId, boolean onlyCommission, Integer page, Integer limit) {
        LitemallUserMoneyHistoryExample example = new LitemallUserMoneyHistoryExample();
        LitemallUserMoneyHistoryExample.Criteria criteria = example.or().andUseridEqualTo(userId);
        if (onlyCommission) {
            //1下级佣金，2分红，3充值
            criteria.andTypeEqualTo(MoneyUtil.share_money);
        }
        example.orderBy("add_time desc");
        PageHelper.startPage(page, limit);
        return userMoneyHistoryMapper.selectByExample(example);
    }

    /**
     * start  handle userFriend
     */
    public List<LitemallUserFriend> getUserFriemds(Integer userId) {
        LitemallUserFriendExample example = new LitemallUserFriendExample();
        LitemallUserFriendExample.Criteria criteria = example.createCriteria();
        criteria.andUseridEqualTo(userId);
        List<LitemallUserFriend> litemallUserFriends = userFriendMapper.selectByExample(example);
        return litemallUserFriends;
    }

    public void addFriend(Integer userId, Integer shareUserId) {
        LitemallUser litemallUser = litemallUserMapper.selectByPrimaryKey(userId);

        LitemallUserFriend litemallUserFriend = new LitemallUserFriend();
        litemallUserFriend.setAddTime(LocalDateTime.now());
        litemallUserFriend.setCommission(BigDecimal.ZERO);
        litemallUserFriend.setFriendUserId(userId);
        litemallUserFriend.setFriendUserName(litemallUser.getUsername());
        litemallUserFriend.setUserid(shareUserId);
        userFriendMapper.insertSelective(litemallUserFriend);
    }

    /**
     * start handle scoreMapper */

    /**
     * @param userId
     * @param type   3浏览商品，4广告，5邀请好友，6签到，7分红
     * @return
     */
    public Integer updateScore(Integer userId, Integer type) {
        LitemallScoreRuleExample ruleExample = new LitemallScoreRuleExample();
        ruleExample.createCriteria();
        List<LitemallScoreRule> scoreRuleList = scoreRuleMapper.selectByExample(ruleExample);

        LitemallScoreHistoryExample historyExample = new LitemallScoreHistoryExample();
        //当天的开始时间
        LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        historyExample.createCriteria().andUseridEqualTo(userId).andAddTimeGreaterThanOrEqualTo(time);
        List<LitemallScoreHistory> scoreHistoryList = scoreHistoryMapper.selectByExample(historyExample);
        if (scoreHistoryList.stream().anyMatch(p -> type.equals(6) && p.getChannel().equals(type))) {
            //已经签到过，不准多次签到
            return 0;
        }
        //积分规则
        LitemallScoreRule scoreRule = scoreRuleList.stream().filter(p -> p.getType().equals(type)).findFirst().orElse(new LitemallScoreRule());
        if (scoreRule.getScore() == null) {
            logger.info("规则缺失，请及时添加规则");
        }
        Integer addScore = Optional.ofNullable(scoreRule.getScore()).orElse(0);
        //跟新积分
        LitemallScoreExample scoreExample = new LitemallScoreExample();
        scoreExample.createCriteria().andUseridEqualTo(userId);
        LitemallScore scoreOld = scoreMapper.selectOneByExample(scoreExample);
        scoreOld.setScore(scoreOld.getScore() + addScore);
        scoreMapper.updateByPrimaryKeySelective(scoreOld);
        //增加积分记录
        LitemallScoreHistory scoreHistory = new LitemallScoreHistory();
        scoreHistory.setAddTime(LocalDateTime.now());
        scoreHistory.setChannel(type);
        scoreHistory.setChannerName(ScoreUtil.getChannelName(type));
        scoreHistory.setScore(addScore);
        scoreHistory.setUserid(userId);
        scoreHistoryMapper.insertSelective(scoreHistory);


        //给上级增加积分
        LitemallUserFriendExample friendExample = new LitemallUserFriendExample();
        friendExample.createCriteria().andFriendUserIdEqualTo(userId);
        LitemallUserFriend userFriend = userFriendMapper.selectOneByExample(friendExample);
        if (userFriend == null) {
            return addScore;
        }
        //上级的用户id
        Integer upUserid = userFriend.getUserid();
        //3浏览商品，4广告，5邀请好友，6签到
        boolean b = scoreHistoryList.stream().anyMatch(p -> p.getChannel().equals(ScoreUtil.browse_goods));
        boolean b2 = scoreHistoryList.stream().anyMatch(p -> p.getChannel().equals(ScoreUtil.ad));
        boolean b3 = scoreHistoryList.stream().anyMatch(p -> p.getChannel().equals(ScoreUtil.sign_in));
        //完成了每日任务,给上级加积分（邀请好友）
        if (b && b2 && b3 && userFriend.getScoreCommission() == 0) {
            scoreExample.clear();
            scoreExample.createCriteria().andUseridEqualTo(upUserid);
            LitemallScore upScoreOld = scoreMapper.selectOneByExample(scoreExample);
            LitemallScoreRule scoreRule2 = scoreRuleList.stream().filter(p -> p.getType().equals(5)).findFirst().orElse(new LitemallScoreRule());
            if (scoreRule.getScore() == null) {
                logger.info("规则缺失，请及时添加规则");
            }
            Integer addScore2 = Optional.ofNullable(scoreRule2.getScore()).orElse(0);
            upScoreOld.setScore(upScoreOld.getScore() + addScore2);
            scoreMapper.updateByPrimaryKeySelective(upScoreOld);
        }
        return addScore;

    }

    public List<LitemallScore> getScoreTop() {
        LitemallScoreExample scoreExample = new LitemallScoreExample();
        scoreExample.orderBy("score desc");
        PageHelper.startPage(1, 10);
        List<LitemallScore> scoreList = scoreMapper.selectByExample(scoreExample);
        return scoreList;
    }

    public List<LitemallScore> getScoreByUserId(List<Integer> userIdList) {
        LitemallScoreExample scoreExample = new LitemallScoreExample();
        scoreExample.createCriteria().andUseridIn(userIdList);
        List<LitemallScore> scoreList = scoreMapper.selectByExample(scoreExample);
        return scoreList;
    }

    public List<LitemallScoreHistory> getScoreHistory(Integer userId, Integer page, Integer size) {
        LitemallScoreHistoryExample scoreExample = new LitemallScoreHistoryExample();
        scoreExample.createCriteria().andUseridEqualTo(userId);
        scoreExample.orderBy("add_time desc");
        PageHelper.startPage(page, size);
        List<LitemallScoreHistory> scoreList = scoreHistoryMapper.selectByExample(scoreExample);
        return scoreList;
    }

    public void shareMoney(String shareProportion) {
        BigDecimal share = new BigDecimal(shareProportion);

        LitemallOrderExample example = new LitemallOrderExample();
        example.createCriteria().andOrderStatusEqualTo(OrderUtil.STATUS_AUTO_CONFIRM);
        List<LitemallOrder> orderList = litemallOrderMapper.selectByExample(example);

        List<Integer> userIdList = orderList.stream().map(LitemallOrder::getUserId).collect(Collectors.toList());
        List<LitemallScore> scoreList = this.getScoreByUserId(userIdList);
        //跟新参与分红的积分
        for (LitemallScore score : scoreList) {
            //跟新积分
            score.setShareScore(score.getShareScore() + score.getScore());
            score.setScore(0);
            scoreMapper.updateByPrimaryKeySelective(score);
            //增加积分记录
            LitemallScoreHistory scoreHistory = new LitemallScoreHistory();
            scoreHistory.setAddTime(LocalDateTime.now());
            scoreHistory.setChannel(ScoreUtil.share_money);//参与分红
            scoreHistory.setChannerName(ScoreUtil.getChannelName(ScoreUtil.share_money));
            scoreHistory.setScore(new BigDecimal(score.getScore()).negate().intValue());
            scoreHistory.setUserid(score.getUserid());
            scoreHistoryMapper.insertSelective(scoreHistory);

        }
        //开始分红

        for (LitemallScore score : scoreList) {
            BigDecimal multiply = share.multiply(new BigDecimal(score.getShareScore()));
            //插入金额记录(（1下级佣金，2分红，3充值）)
            LitemallUserMoneyHistory userMoneyHistory = new LitemallUserMoneyHistory();
            userMoneyHistory.setMoney(multiply);
            userMoneyHistory.setUserid(score.getUserid());
            userMoneyHistory.setType(MoneyUtil.share_money);
            userMoneyHistory.setTypename(MoneyUtil.getName(MoneyUtil.share_money));
            userMoneyHistory.setOrderNumber("");
            userMoneyHistory.setAddTime(LocalDateTime.now());
            userMoneyHistoryMapper.insert(userMoneyHistory);
            //跟新金额
            LitemallUserMoneyExample moneyExample = new LitemallUserMoneyExample();
            moneyExample.createCriteria().andUseridEqualTo(score.getUserid());
            LitemallUserMoney userMoneyOld = userMoneyMapper.selectOneByExample(moneyExample);
            userMoneyOld.setMoney(userMoneyOld.getMoney().add(multiply));
            userMoneyMapper.updateByPrimaryKey(userMoneyOld);
        }
    }

    public Map<String, Object> getScoreBaseInfo(Integer userId) {
        HashMap<String, Object> map = Maps.newHashMap();
        LitemallScore score = this.queryScoreByUseId(userId);

        LitemallScoreHistoryExample scoreExample = new LitemallScoreHistoryExample();
        scoreExample.createCriteria().andUseridEqualTo(userId);
        List<LitemallScoreHistory> scoreList = scoreHistoryMapper.selectByExample(scoreExample);
        //当天的开始时间
        LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        //获取积分历史
        LitemallScoreHistoryExample historyExample = new LitemallScoreHistoryExample();
        historyExample.createCriteria().andUseridEqualTo(userId).andAddTimeGreaterThanOrEqualTo(time);
        List<LitemallScoreHistory> scoreHistoryList = scoreHistoryMapper.selectByExample(historyExample);
        long sum = scoreHistoryList.stream().collect(Collectors.summarizingInt(p -> p.getScore())).getSum();
        //获取金额历史
        LitemallUserMoneyHistoryExample example = new LitemallUserMoneyHistoryExample();
        example.createCriteria().andUseridEqualTo(userId).andAddTimeGreaterThanOrEqualTo(time);
        List<LitemallUserMoneyHistory> moneyHistoryList = userMoneyHistoryMapper.selectByExample(example);
        double sum1 = moneyHistoryList.stream().filter(p -> p.getType().equals(MoneyUtil.share_money)).collect(Collectors.summarizingDouble(p -> p.getMoney().doubleValue())).getSum();

        //排行榜数据
        List<LitemallScore> scoreTop = this.getScoreTop();
        List<Integer> topUserIdList = scoreTop.stream().map(LitemallScore::getUserid).collect(Collectors.toList());
        LitemallUserExample userExample = new LitemallUserExample();
        userExample.createCriteria().andIdIn(topUserIdList);
        List<LitemallUser> topUserList = userMapper.selectByExample(userExample);
        HashMap<String, Object> temp = Maps.newHashMap();
        for (int i = 0; i < topUserIdList.size(); i++) {
            int topNumber = i + 1;
            temp.put("第" + topNumber + "名", scoreTop.get(i).getScore());
        }
        //我的好友总数
        LitemallUserFriendExample userFriendExample = new LitemallUserFriendExample();
        userFriendExample.createCriteria().andUseridEqualTo(userId);
        List<LitemallUserFriend> userFriendList = userFriendMapper.selectByExample(userFriendExample);

        map.put("scoreSum", score.getScore());
        map.put("shareScoreSum", score.getShareScore());
        map.put("todayScoreSum", sum);
        map.put("todayShareScoreSum", sum1);
        map.put("todayShareScoreSum", sum1);
        map.put("scoreTop", temp);
        map.put("friendSum", userFriendList.size());
        return map;
    }

}