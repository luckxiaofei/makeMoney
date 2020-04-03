package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.LitemallUserPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/userProperty")
@Validated
public class AdminUserPropertyController {
    private final Log logger = LogFactory.getLog(AdminUserPropertyController.class);

    @Autowired
    private LitemallUserPropertyService userPropertyService;


    @RequiresPermissions("admin:score")
    @RequiresPermissionsDesc(menu = {"推广管理", "积分管理"}, button = "规则查询")
    @GetMapping("/listRule")
    public Object listRule(Integer ruleType,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer limit,
                           @Sort @RequestParam(defaultValue = "add_time") String sort,
                           @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallScoreRule> litemallScoreRules = userPropertyService.queryRuleSelective(ruleType, page, limit, sort, order);
        return ResponseUtil.okList(litemallScoreRules);
    }

    @RequiresPermissions("admin:score")
    @RequiresPermissionsDesc(menu = {"推广管理", "积分管理"}, button = "积分查询")
    @GetMapping("/listScore")
    public Object listScore(Integer userId,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer limit,
                            @Sort @RequestParam(defaultValue = "add_time") String sort,
                            @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallScoreHistory> scoreHistoryList = userPropertyService.queryHistorySelective(userId, page, limit, sort, order);
        return ResponseUtil.okList(scoreHistoryList);
    }

    private Object validate(LitemallScoreRule grouponRules) {
        return null;
    }

    @RequiresPermissions("admin:score")
    @RequiresPermissionsDesc(menu = {"推广管理", "积分管理"}, button = "编辑规则")
    @PostMapping("/updateRule")
    public Object update(@RequestBody LitemallScoreRule scoreRule) {
        Object error = validate(scoreRule);
        if (error != null) {
            return error;
        }

        LitemallScoreRule rules = userPropertyService.queryRuleById(scoreRule.getId());
        if (rules == null) {
            return ResponseUtil.badArgumentValue();
        }

        if (userPropertyService.updateRuleById(scoreRule) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }


}
