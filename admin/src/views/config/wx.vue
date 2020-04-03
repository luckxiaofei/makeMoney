<template>
  <div class="app-container">
    <el-form
      ref="dataForm"
      :rules="rules"
      :model="dataForm"
      status-icon
      label-width="300px">
      <el-tabs tab-position="left">
        <!--        <el-tab-pane label="首页配置">-->
        <!--          <el-form-item label="新品首发栏目商品显示数量" prop="litemall_wx_index_new">-->
        <!--            <el-input v-model="dataForm.litemall_wx_index_new"/>-->
        <!--          </el-form-item>-->
        <el-form-item label="积分兑换商品显示数量" prop="litemall_wx_index_convert">
          <el-input placeholder="请输入内容" v-model="dataForm.litemall_wx_index_convert" type="number">
            <template slot="append">个</template>
          </el-input>
        </el-form-item>
        <el-form-item label="浏览商品领青豆任务-浏览商品个数" prop="browse_goods_number">
          <el-input placeholder="请输入内容" v-model="dataForm.browse_goods_number" type="number">
            <template slot="append">个</template>
          </el-input>
        </el-form-item>
        <el-form-item label="浏览商品领青豆任务-浏览商品时间" prop="browse_goods_time">
          <el-input placeholder="请输入内容" v-model="dataForm.browse_goods_time" type="number">
            <template slot="append">秒</template>
          </el-input>
        </el-form-item>
        <el-form-item label="小程序广播类容" prop="notice_info">
          <el-input
            type="textarea"
            :rows="2"
            placeholder="请输入内容"
            v-model="dataForm.notice_info">
          </el-input>
        </el-form-item>
        <el-form-item label="小程序规则说明" prop="rule_info">
          <el-input
            type="textarea"
            :rows="2"
            placeholder="请输入内容"
            v-model="dataForm.rule_info">
          </el-input>
        </el-form-item>
        <el-form-item label="青豆分红比例" prop="share_proportion">
          <el-input placeholder="一青豆可分红的钱" v-model="dataForm.share_proportion" type="number">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <!--          <el-form-item label="品牌制造商直供栏目品牌商显示数量" prop="litemall_wx_index_brand">-->
        <!--            <el-input v-model="dataForm.litemall_wx_index_brand"/>-->
        <!--          </el-form-item>-->
        <!--          <el-form-item label="专题精选栏目显示数量" prop="litemall_wx_index_topic">-->
        <!--            <el-input v-model="dataForm.litemall_wx_index_topic"/>-->
        <!--          </el-form-item>-->
        <!--          <el-form-item label="分类栏目显示数量" prop="litemall_wx_catlog_list">-->
        <!--            <el-input v-model="dataForm.litemall_wx_catlog_list"/>-->
        <!--          </el-form-item>-->
        <!--          <el-form-item label="分类栏目商品显示数量" prop="litemall_wx_catlog_goods">-->
        <!--            <el-input v-model="dataForm.litemall_wx_catlog_goods"/>-->
        <!--          </el-form-item>-->
        <!--        </el-tab-pane>-->
        <!--        <el-tab-pane label="其他配置">-->
        <!--          <el-form-item label="商品分享功能" prop="litemall_wx_share">-->
        <!--            <el-switch v-model="dataForm.litemall_wx_share"/>-->
        <!--          </el-form-item>-->
        <!--        </el-tab-pane>-->
      </el-tabs>
      <el-form-item>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="update">确定</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
    import {listWx, updateWx} from '@/api/config'

    export default {
        name: 'ConfigWx',
        data() {
            return {
                dataForm: {
                    litemall_wx_index_new: 0,
                    litemall_wx_index_hot: 0,
                    litemall_wx_index_brand: 0,
                    litemall_wx_index_topic: 0,
                    litemall_wx_catlog_list: 0,
                    litemall_wx_catlog_goods: 0,
                    litemall_wx_share: false,
                    litemall_wx_share: false,
                    litemall_wx_share: false,
                    litemall_wx_share: false,
                },
                rules: {
                    litemall_wx_index_new: [
                        {required: true, message: '不能为空', trigger: 'blur'}
                    ],
                    litemall_wx_index_hot: [
                        {required: true, message: '不能为空', trigger: 'blur'}
                    ],
                    litemall_wx_index_brand: [
                        {required: true, message: '不能为空', trigger: 'blur'}
                    ],
                    litemall_wx_index_topic: [
                        {required: true, message: '不能为空', trigger: 'blur'}
                    ],
                    litemall_wx_catlog_list: [
                        {required: true, message: '不能为空', trigger: 'blur'}
                    ],
                    litemall_wx_catlog_goods: [
                        {required: true, message: '不能为空', trigger: 'blur'}
                    ]
                }
            }
        },
        created() {
            this.init()
        },
        methods: {
            init: function () {
                listWx().then(response => {
                    this.dataForm = response.data.data
                })
            },
            cancel() {
                this.init()
            },
            update() {
                this.$refs['dataForm'].validate((valid) => {
                    if (!valid) {
                        return false
                    }
                    this.doUpdate()
                })
            },
            doUpdate() {
                updateWx(this.dataForm)
                    .then(response => {
                        this.$notify.success({
                            title: '成功',
                            message: '小程序配置成功'
                        })
                    })
                    .catch(response => {
                        this.$notify.error({
                            title: '失败',
                            message: response.data.errmsg
                        })
                    })
            }
        }
    }
</script>
