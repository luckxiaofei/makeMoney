package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.HomeCacheManager;
import org.linlinjava.litemall.wx.service.WxGrouponRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 首页服务
 */
@RestController
@RequestMapping("/wx/home")
@Validated
public class WxHomeController {
    private final Log logger = LogFactory.getLog(WxHomeController.class);

    @Autowired
    private LitemallAdService adService;

    @Autowired
    private LitemallGoodsService goodsService;

    @Autowired
    private LitemallBrandService brandService;

    @Autowired
    private LitemallTopicService topicService;

    @Autowired
    private LitemallCategoryService categoryService;

    @Autowired
    private WxGrouponRuleService grouponService;

    @Autowired
    private LitemallCouponService couponService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @GetMapping("/cache")
    public Object cache(@NotNull String key) {
        if (!key.equals("litemall_cache")) {
            return ResponseUtil.fail();
        }

        // 清除缓存
        HomeCacheManager.clearAll();
        return ResponseUtil.ok("缓存已清除");
    }

    /**
     * 首页数据
     *
     * @param userId 当用户已经登录时，非空。为登录状态为null
     * @return 首页数据
     */
    @GetMapping("/index")
    public Object index(@LoginUser Integer userId) {
        //优先从缓存中读取
        if (HomeCacheManager.hasData(HomeCacheManager.INDEX)) {
            return ResponseUtil.ok(HomeCacheManager.getCacheData(HomeCacheManager.INDEX));
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        //一级分类
        Callable<List> channelListCallable = () -> categoryService.queryChannel();

        //滚动区商品
        Callable<List> newGoodsListCallable = () -> goodsService.queryByNew(0, SystemConfig.getNewLimit());
        //兑换区商品
        Callable<List> convertGoodsListCallable = () -> goodsService.queryByConvert(0, "", 0, SystemConfig.getConvertLimit());
        //剩余区商品
        Callable<List> otherGoodsListCallable = () -> goodsService.queryByHot(0, 50);

        FutureTask<List> channelTask = new FutureTask<>(channelListCallable);
        FutureTask<List> newGoodsListTask = new FutureTask<>(newGoodsListCallable);
        FutureTask<List> convertGoodsListTask = new FutureTask<>(convertGoodsListCallable);
        FutureTask<List> otherGoodsListTask = new FutureTask<>(otherGoodsListCallable);

        executorService.submit(channelTask);
        executorService.submit(newGoodsListTask);
        executorService.submit(convertGoodsListTask);
        executorService.submit(otherGoodsListTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("channel", channelTask.get());
            entity.put("newGoodsList", newGoodsListTask.get());
            entity.put("convertGoodsList", convertGoodsListTask.get());
            entity.put("otherGoodsList", otherGoodsListTask.get());
            //缓存数据
            HomeCacheManager.loadData(HomeCacheManager.INDEX, entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
        return ResponseUtil.ok(entity);
    }

    private List<Map> getCategoryList() {
        List<Map> categoryList = new ArrayList<>();
        List<LitemallCategory> catL1List = categoryService.queryL1WithoutRecommend(0, SystemConfig.getCatlogListLimit());
        for (LitemallCategory catL1 : catL1List) {
            List<LitemallCategory> catL2List = categoryService.queryByPid(catL1.getId());
            List<Integer> l2List = new ArrayList<>();
            for (LitemallCategory catL2 : catL2List) {
                l2List.add(catL2.getId());
            }

            List<LitemallGoods> categoryGoods;
            if (l2List.size() == 0) {
                categoryGoods = new ArrayList<>();
            } else {
                categoryGoods = goodsService.queryByCategory(l2List, 0, SystemConfig.getCatlogMoreLimit());
            }

            Map<String, Object> catGoods = new HashMap<>();
            catGoods.put("id", catL1.getId());
            catGoods.put("name", catL1.getName());
            catGoods.put("goodsList", categoryGoods);
            categoryList.add(catGoods);
        }
        return categoryList;
    }

    /**
     * 商城介绍信息
     *
     * @return 商城介绍信息
     */
    @GetMapping("/systemInfo")
    public Object about() {
        Map<String, String> systemConfigs = SystemConfig.getSystemConfigs();
        return ResponseUtil.ok(systemConfigs);
    }
}