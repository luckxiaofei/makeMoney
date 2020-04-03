package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.linlinjava.litemall.db.VO.UserVO;
import org.linlinjava.litemall.db.dao.LitemallScoreMapper;
import org.linlinjava.litemall.db.dao.LitemallUserFriendMapper;
import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.dao.LitemallUserMoneyMapper;
import org.linlinjava.litemall.db.domain.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LitemallUserService {
    @Resource
    private LitemallUserMapper userMapper;
    @Resource
    private LitemallUserMoneyMapper userMoneyMapper;
    @Resource
    private LitemallScoreMapper scoreMapper;
    @Resource
    private LitemallUserFriendMapper userFriendMapper;

    public LitemallUser findById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public UserVo findUserVoById(Integer userId) {
        LitemallUser user = findById(userId);
        UserVo userVo = new UserVo();
        userVo.setNickname(user.getNickname());
        userVo.setAvatar(user.getAvatar());
        return userVo;
    }

    public LitemallUser queryByOid(String openId) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public void initAddUserInfo(LitemallUser user, Integer shareId) {
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);

        LitemallUserMoney litemallUserMoney = new LitemallUserMoney();
        litemallUserMoney.setAddTime(LocalDateTime.now());
        litemallUserMoney.setUserid(user.getId());
        userMoneyMapper.insertSelective(litemallUserMoney);

        LitemallScore litemallScore = new LitemallScore();
        litemallScore.setUserid(user.getId());
        litemallScore.setScore(0);
        litemallScore.setShareScore(0);
        litemallScore.setAddTime(LocalDateTime.now());
        scoreMapper.insertSelective(litemallScore);

        if (shareId != null && shareId > 0) {
            LitemallUserFriend userFriend = new LitemallUserFriend();
            userFriend.setAddTime(LocalDateTime.now());
            userFriend.setCommission(BigDecimal.ZERO);
            userFriend.setFriendUserId(user.getId());
            userFriend.setFriendUserName(user.getUsername());
            userFriend.setUserid(shareId);
            userFriendMapper.insertSelective(userFriend);
        }
    }

    public int updateById(LitemallUser user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public List<UserVO> querySelective(String username, String mobile, Integer page, Integer size, String sort, String order) {
        LitemallUserExample example = new LitemallUserExample();
        LitemallUserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(mobile)) {
            criteria.andMobileEqualTo(mobile);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        List<LitemallUser> userList = userMapper.selectByExample(example);

        List<Integer> usrId = userList.stream().map(LitemallUser::getId).collect(Collectors.toList());
        List<UserVO> userVOlist = Lists.newArrayList();
        if (usrId != null && usrId.size() > 0) {
            LitemallScoreExample scoreExample = new LitemallScoreExample();
            scoreExample.createCriteria().andUseridIn(usrId);
            List<LitemallScore> scoreList = scoreMapper.selectByExample(scoreExample);
            userVOlist = userList.stream().map(p -> {
                LitemallScore score = scoreList.stream().filter(p2 -> p2.getUserid().equals(p.getId())).findFirst().orElse(new LitemallScore());
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(p,userVO);
                Integer scoreNumber = Optional.ofNullable(score.getScore()).orElse(0);
                userVO.setScore(scoreNumber);
                return userVO;
            }).collect(Collectors.toList());
        }


        return userVOlist;
    }

    public int count() {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andDeletedEqualTo(false);

        return (int) userMapper.countByExample(example);
    }

    public List<LitemallUser> queryByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public boolean checkByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.countByExample(example) != 0;
    }

    public List<LitemallUser> queryByMobile(String mobile) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public List<LitemallUser> queryByOpenid(String openid) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        userMapper.logicalDeleteByPrimaryKey(id);
    }
}
