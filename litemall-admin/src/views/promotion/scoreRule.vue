<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <!--      <el-input v-model="listQuery.goodsId" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编号"/>-->
      <el-select v-model="listQuery.ruleType" placeholder="请选择">
        <el-option
          v-for="item in ruleTypeOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-button v-permission="['GET /admin/userProperty/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        查找
      </el-button>
<!--      <el-button-->
<!--        :loading="downloadLoading"-->
<!--        class="filter-item"-->
<!--        type="primary"-->
<!--        icon="el-icon-download"-->
<!--        @click="handleDownload"-->
<!--      >导出-->
<!--      </el-button>-->
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="积分规则ID" prop="id"/>

      <el-table-column align="center" label="积分" prop="score"/>

      <el-table-column align="center" min-width="100" label="类型" prop="type">
        <template slot-scope="scope">
          <el-tag>{{ ruleTypeMap[scope.row.type] }}</el-tag>
        </template>
      </el-table-column>


      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/userProperty/update']" type="primary" size="mini"
                     @click="handleUpdate(scope.row)">编辑
          </el-button>
          <!--          <el-button v-permission="['POST /admin/userProperty/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>-->
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="dataForm"
        status-icon
        label-position="left"
        label-width="120px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="积分规则ID" prop="id" >
          <el-input v-model="dataForm.id" :disabled="true"/>
        </el-form-item>
        <el-form-item label="积分" prop="score">
          <el-input v-model="dataForm.score"/>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-input v-model="dataForm.typeName" :disabled="true"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit"
                @pagination="getList"/>

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100"/>
    </el-tooltip>

  </div>
</template>

<script>
    import {listRule,updateRule} from '@/api/userProperty'
    import BackToTop from '@/components/BackToTop'
    import Pagination from '@/components/Pagination' // Secondary package based on el-pagination


    export default {
        name: 'ScoreRule',
        components: {BackToTop, Pagination},
        data() {
            return {
                list: [],
                total: 0,
                listLoading: true,
                listQuery: {
                    page: 1,
                    limit: 20,
                    ruleType: undefined,
                    sort: 'add_time',
                    order: 'desc'
                },
                downloadLoading: false,
                dataForm: {
                    id: undefined,
                    score: '',
                    type: '',
                    typeName: '',
                },
                dialogFormVisible: false,
                dialogStatus: '',
                textMap: {
                    update: '编辑',
                    create: '创建'
                },
                //类型（1分享群，2分享好友，3浏览商品，4广告）
                ruleTypeMap: {
                    1: '分享群',
                    2: '分享好友',
                    3: '浏览商品',
                    4: '广告'
                },
                ruleTypeOptions: [{
                    value: '1',
                    label: '分享群'
                }, {
                    value: '2',
                    label: '分享好友'
                }, {
                    value: '3',
                    label: '浏览商品'
                }, {
                    value: '4',
                    label: '广告'
                }],
                rules: {
                    goodsId: [{required: true, message: '商品不能为空', trigger: 'blur'}],
                    score: [{required: true, message: '团购折扣不能为空', trigger: 'blur'}],
                    type: [{required: true, message: '团购人数不能为空', trigger: 'blur'}],
                    expireTime: [{required: true, message: '过期时间不能为空', trigger: 'blur'}]
                }
            }
        },
        created() {
            this.getList()
        },
        methods: {
            getList() {
                this.listLoading = true
                listRule(this.listQuery).then(response => {
                    this.list = response.data.data.list
                    this.total = response.data.data.total
                    this.listLoading = false
                }).catch(() => {
                    this.list = []
                    this.total = 0
                    this.listLoading = false
                })
            },
            handleFilter() {
                this.listQuery.page = 1
                this.getList()
            },
            resetForm() {
                this.dataForm = {
                    id: undefined,
                    goodsId: '',
                    score: '',
                    type: '',
                    expireTime: undefined
                }
            },
            // handleCreate() {
            //     this.resetForm()
            //     this.dialogStatus = 'create'
            //     this.dialogFormVisible = true
            //     this.$nextTick(() => {
            //         this.$refs['dataForm'].clearValidate()
            //     })
            // },
            // createData() {
            //     this.$refs['dataForm'].validate((valid) => {
            //         if (valid) {
            //             publishGroupon(this.dataForm).then(response => {
            //                 this.list.unshift(response.data.data)
            //                 this.dialogFormVisible = false
            //                 this.$notify.success({
            //                     title: '成功',
            //                     message: '创建团购规则成功'
            //                 })
            //             }).catch(response => {
            //                 this.$notify.error({
            //                     title: '失败',
            //                     message: response.data.errmsg
            //                 })
            //             })
            //         }
            //     })
            // },
            handleUpdate(row) {
                this.dataForm = Object.assign({}, row)
                this.dataForm.typeName = this.ruleTypeMap[row.type]
                this.dialogStatus = 'update'
                this.dialogFormVisible = true
                this.$nextTick(() => {
                    this.$refs['dataForm'].clearValidate()
                })
            },
            updateData() {
                this.$refs['dataForm'].validate((valid) => {
                    if (valid) {
                        updateRule(this.dataForm).then(() => {
                            for (const v of this.list) {
                                if (v.id === this.dataForm.id) {
                                    const index = this.list.indexOf(v)
                                    this.list.splice(index, 1, this.dataForm)
                                    break
                                }
                            }
                            this.dialogFormVisible = false
                            this.$notify.success({
                                title: '成功',
                                message: '更新积分规则成功'
                            })
                        }).catch(response => {
                            this.$notify.error({
                                title: '失败',
                                message: response.data.errmsg
                            })
                        })
                    }
                })
            },
            // handleDelete(row) {
            //     deleteGroupon(row).then(response => {
            //         this.$notify.success({
            //             title: '成功',
            //             message: '删除团购规则成功'
            //         })
            //         const index = this.list.indexOf(row)
            //         this.list.splice(index, 1)
            //     }).catch(response => {
            //         this.$notify.error({
            //             title: '失败',
            //             message: response.data.errmsg
            //         })
            //     })
            // },
            // handleDownload() {
            //     this.downloadLoading = true
            //     import('@/vendor/Export2Excel').then(excel => {
            //         const tHeader = ['商品ID', '名称', '首页主图', '折扣', '人数要求', '活动开始时间', '活动结束时间']
            //         const filterVal = ['id', 'name', 'pic_url', 'score', 'type', 'addTime', 'expireTime']
            //         excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品信息')
            //         this.downloadLoading = false
            //     })
            // }
        }
    }
</script>
