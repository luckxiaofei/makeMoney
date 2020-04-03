package com.fei.db.service;

import com.fei.db.entity.po.LitemallStorage;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LitemallStorageService {
//    @Autowired
//    private LitemallStorageMapper storageMapper;

    public void deleteByKey(String key) {
//        LitemallStorageExample example = new LitemallStorageExample();
//        example.or().andKeyEqualTo(key);
//        storageMapper.logicalDeleteByExample(example);
    }

    public void add(LitemallStorage storageInfo) {
//        storageInfo.setAddTime(LocalDateTime.now());
//        storageInfo.setUpdateTime(LocalDateTime.now());
//        storageMapper.insertSelective(storageInfo);
    }

    public LitemallStorage findByKey(String key) {
//        LitemallStorageExample example = new LitemallStorageExample();
//        example.or().andKeyEqualTo(key).andDeletedEqualTo(false);
//        return storageMapper.selectOneByExample(example);
        return new LitemallStorage();
    }

    public int update(LitemallStorage storageInfo) {
//        storageInfo.setUpdateTime(LocalDateTime.now());
//        return storageMapper.updateByPrimaryKeySelective(storageInfo);
        return 0;
    }

    public LitemallStorage findById(Integer id) {
//        return storageMapper.selectByPrimaryKey(id);
        return new LitemallStorage();
    }

    public List<LitemallStorage> querySelective(String key, String name, Integer page, Integer limit, String sort, String order) {
//        LitemallStorageExample example = new LitemallStorageExample();
//        LitemallStorageExample.Criteria criteria = example.createCriteria();
//
//        if (!StringUtils.isEmpty(key)) {
//            criteria.andKeyEqualTo(key);
//        }
//        if (!StringUtils.isEmpty(name)) {
//            criteria.andNameLike("%" + name + "%");
//        }
//        criteria.andDeletedEqualTo(false);
//
//        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
//            example.setOrderByClause(sort + " " + order);
//        }
//
//        PageHelper.startPage(page, limit);
//        return storageMapper.selectByExample(example);
        return Lists.newArrayList();
    }
}
