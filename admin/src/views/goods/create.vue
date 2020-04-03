<template>
  <div class="app-container">

    <el-card class="box-card">
      <h3>商品介绍</h3>
      <el-form ref="goods" :rules="rules" :model="goods" label-width="150px">
        <el-form-item label="商品编号" prop="goodsSn">
          <el-input v-model="goods.goodsSn"/>
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="goods.name"/>
        </el-form-item>

        <el-form-item label="是否新品" prop="isNew">
          <el-radio-group v-model="goods.isNew">
            <el-radio :label="true">新品</el-radio>
            <el-radio :label="false">非新品</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否在售" prop="isOnSale">
          <el-radio-group v-model="goods.isOnSale">
            <el-radio :label="true">在售</el-radio>
            <el-radio :label="false">未售</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否兑换" prop="isConvert">
          <el-radio-group v-model="goods.isConvert">
            <el-radio :label="true">可兑换</el-radio>
            <el-radio :label="false">不可兑换</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="原价" prop="counterPrice">
          <el-input v-model="goods.counterPrice" placeholder="0.00" type="number">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item label="实际销售价" prop="retailPrice">
          <el-input v-model="goods.retailPrice" placeholder="0.00" type="number">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item label="兑换所需青豆" prop="convertScore">
          <el-input v-model="goods.convertScore" type="number"/>
        </el-form-item>

        <el-form-item label="购买商品赠送青豆" prop="giveScore">
          <el-input v-model="goods.giveScore" type="number"/>
        </el-form-item>

        <el-form-item label="购买商品返上级佣金" prop="commission">
          <el-input v-model="goods.commission" type="number"/>
        </el-form-item>


        <el-form-item label="商品单位" prop="unit">
          <el-input v-model="goods.unit" placeholder="件 / 个 / 盒"/>
        </el-form-item>


        <el-form-item label="关键字">
          <el-tag v-for="tag in keywords" :key="tag" closable type="primary" @close="handleClose(tag)">
            {{ tag }}
          </el-tag>
          <el-input
            v-if="newKeywordVisible"
            ref="newKeywordInput"
            v-model="newKeyword"
            class="input-new-keyword"

            @keyup.enter.native="handleInputConfirm"
            @blur="handleInputConfirm"
          />
          <el-button v-else class="button-new-keyword" type="primary" @click="showInput">+ 增加</el-button>
        </el-form-item>

        <el-form-item label="所属分类" prop="categoryId">
          <el-cascader :options="categoryList" expand-trigger="hover" clearable @change="handleCategoryChange"/>
        </el-form-item>

        <el-form-item label="所属品牌商">
          <el-select v-model="goods.brandId" clearable>
            <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>

        <el-form-item label="商品简介" prop="brief">
          <el-input v-model="goods.brief"/>
        </el-form-item>
        <el-form-item label="商品图片" prop="picUrl">
          <el-upload
            :action="uploadPath"
            :show-file-list="false"
            :headers="headers"
            :on-success="uploadPicUrl"
            class="avatar-uploader"
            accept=".jpg,.jpeg,.png,.gif"
          >
            <img v-if="goods.picUrl" :src="goods.picUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"/>
          </el-upload>
        </el-form-item>

        <el-form-item label="宣传画廊" prop="gallery">
          <el-upload
            :action="uploadPath"
            :limit="5"
            :headers="headers"
            :on-exceed="uploadOverrun"
            :on-success="handleGalleryUrl"
            :on-remove="handleRemove"
            multiple
            accept=".jpg,.jpeg,.png,.gif"
            list-type="picture-card"
          >
            <i class="el-icon-plus"/>
          </el-upload>
        </el-form-item>
        <el-form-item label="商品详细介绍">
          <editor v-model="goods.detail" :init="editorInit"/>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="box-card">
      <h3>商品规格</h3>
      <el-row :gutter="20" type="flex" align="middle" style="padding:20px 0;">
        <el-col :span="10">
          <el-button :plain="true" type="primary" @click="handleSpecificationShow">添加</el-button>
        </el-col>
      </el-row>

      <el-table :data="specifications">
        <el-table-column property="specification" label="规格值">
          <template slot-scope="scope">
            <el-tag type="primary">
              {{ scope.row.specification }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="picUrl" label="规格图片">
          <template slot-scope="scope">
            <img v-if="scope.row.picUrl" :src="scope.row.picUrl" width="40">
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="danger" size="mini" @click="handleSpecificationDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="specVisiable" title="设置规格">
        <el-form
          ref="specForm"
          :rules="rules"
          :model="specForm"
          status-icon
          label-position="left"
          label-width="100px"
          style="width: 400px; margin-left:50px;"
        >
          <el-form-item label="规格名" prop="specification">
            <el-input v-model="specForm.specification" placeholder="标准 / 大 / 中 / 小 ……"/>
          </el-form-item>
          <el-form-item label="规格图片" prop="specPicUrl">
            <el-upload
              :action="uploadPath"
              :show-file-list="false"
              :headers="headers"
              :on-success="uploadSpecPicUrl"
              class="avatar-uploader"
              accept=".jpg,.jpeg,.png,.gif"
            >
              <img v-if="specForm.picUrl" :src="specForm.picUrl" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"/>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="specVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleSpecificationAdd">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <el-card class="box-card">
      <h3>商品库存</h3>
      <el-table :data="products">
        <el-table-column property="value" label="货品规格">
          <template slot-scope="scope">
            <el-tag type="primary">
              {{ scope.row.specification }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="number" width="100" label="货品数量"/>
        <el-table-column property="url" width="100" label="货品图片">
          <template slot-scope="scope">
            <img v-if="scope.row.url" :src="scope.row.url" width="40">
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" width="100" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleProductShow(scope.row)">设置</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="productVisiable" title="添加货品">
        <el-form
          ref="productForm"
          :model="productForm"
          status-icon
          label-position="left"
          label-width="100px"
          style="width: 400px; margin-left:50px;"
        >
          <el-form-item label="货品规格" prop="specification">
            <el-tag>
              {{ productForm.specification }}
            </el-tag>
          </el-form-item>
          <el-form-item label="货品数量" prop="number">
            <el-input v-model="productForm.number" type="number"/>
          </el-form-item>
          <el-form-item label="货品图片" prop="url">
            <el-upload
              :action="uploadPath"
              :show-file-list="false"
              :headers="headers"
              :on-success="uploadProductUrl"
              class="avatar-uploader"
              accept=".jpg,.jpeg,.png,.gif"
            >
              <img v-if="productForm.url" :src="productForm.url" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"/>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="productVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleProductEdit">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <el-card class="box-card">
      <h3>商品参数</h3>
      <el-button type="primary" @click="handleAttributeShow">添加</el-button>
      <el-table :data="attributes">
        <el-table-column property="attribute" label="商品参数名称"/>
        <el-table-column property="value" label="商品参数值"/>
        <el-table-column align="center" label="操作" width="100" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="danger" size="mini" @click="handleAttributeDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="attributeVisiable" title="添加商品参数">
        <el-form
          ref="attributeForm"
          :model="attributeForm"
          status-icon
          label-position="left"
          label-width="100px"
          style="width: 400px; margin-left:50px;"
        >
          <el-form-item label="商品参数名称" prop="attribute">
            <el-input v-model="attributeForm.attribute"/>
          </el-form-item>
          <el-form-item label="商品参数值" prop="value">
            <el-input v-model="attributeForm.value"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="attributeVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleAttributeAdd">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <div class="op-container">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handlePublish">上架</el-button>
    </div>

  </div>
</template>

<style>
  .el-card {
    margin-bottom: 10px;
  }

  .el-tag + .el-tag {
    margin-left: 10px;
  }

  .input-new-keyword {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }

  .avatar-uploader .el-upload {
    width: 145px;
    height: 145px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  .avatar-uploader .el-upload:hover {
    border-color: #20a0ff;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center;
  }

  .avatar {
    width: 145px;
    height: 145px;
    display: block;
  }
</style>

<script>
    import {publishGoods, listCatAndBrand} from '@/api/goods'
    import {createStorage, uploadPath} from '@/api/storage'
    import Editor from '@tinymce/tinymce-vue'
    import {MessageBox} from 'element-ui'
    import {getToken} from '@/utils/auth'


    export default {
        name: 'GoodsCreate',
        components: {Editor},

        data() {
            return {
                uploadPath,
                newKeywordVisible: false,
                newKeyword: '',
                keywords: [],
                categoryList: [],
                brandList: [],
                goods: {picUrl: '', gallery: [], isHot: false, isNew: true, isOnSale: true, isConvert: false},
                specVisiable: false,
                specForm: {specification: '', picUrl: ''},
                specifications: [{specification: '标准', picUrl: ''}],
                productVisiable: false,
                productForm: {specification: '', number: 0, url: ''},
                products: [{specification: '标准',number: 0}],
                attributeVisiable: false,
                attributeForm: {attribute: '', value: ''},
                attributes: [],
                rules: {
                    goodsSn: [{required: true, message: '商品编号不能为空', trigger: 'blur'}],
                    name: [{required: true, message: '商品名称不能为空', trigger: 'blur'}],
                    counterPrice: [{required: true, message: '原价不能为空', trigger: 'blur'}],
                    retailPrice: [{required: true, message: '实际销售价不能为空', trigger: 'blur'}],
                    convertScore: [{required: true, message: '兑换所需青豆不能为空', trigger: 'blur'}],
                    giveScore: [{required: true, message: '购买商品赠送青豆不能为空', trigger: 'blur'}],
                    commission: [{required: true, message: '购买商品返上级佣金不能为空', trigger: 'blur'}],
                    picUrl: [{required: true, message: '商品图片不能为空', trigger: 'blur'}],
                    gallery: [{required: true, message: '宣传画廊不能为空', trigger: 'blur'}],
                    unit: [{required: true, message: '商品单位不能为空', trigger: 'blur'}],
                    categoryId: [{required: true, message: '所属分类不能为空', trigger: 'blur'}],
                    brief: [{required: true, message: '所属分类不能为空', trigger: 'blur'}],
                },
                // specForm
                specRules: {
                    specification: [{required: true, message: '规格名不能为空', trigger: 'blur'}],

                },
                //productForm
                productRules: {
                    specification: [{required: true, message: '规格名不能为空', trigger: 'blur'}],
                    number: [{required: true, message: '货品数量不能为空', trigger: 'blur'}],
                },
                //attributeForm
                attributeRules: {
                    attribute: [{required: true, message: '商品参数名称不能为空', trigger: 'blur'}],
                    value: [{required: true, message: '商品参数值不能为空', trigger: 'blur'}],
                },
                editorInit: {
                    language: 'zh_CN',
                    height: 500,
                    convert_urls: false,
                    plugins: ['advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'],
                    toolbar: ['searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample', 'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'],
                    images_upload_handler: function (blobInfo, success, failure) {
                        const formData = new FormData()
                        formData.append('file', blobInfo.blob())
                        createStorage(formData).then(res => {
                            success(res.data.data.url)
                        }).catch(() => {
                            failure('上传失败，请重新上传')
                        })
                    }
                }
            }
        },
        computed: {
            headers() {
                return {
                    'X-Litemall-Admin-Token': getToken()
                }
            }
        },
        created() {
            this.init()
        },


        methods: {
            init: function () {
                listCatAndBrand().then(response => {
                    this.categoryList = response.data.data.categoryList
                    this.brandList = response.data.data.brandList
                })
            },
            handleCategoryChange(value) {
                this.goods.categoryId = value[value.length - 1]
            },
            handleCancel: function () {
                this.$router.push({path: '/goods/goods'})
            },
            handlePublish: function () {
                const finalGoods = {
                    goods: this.goods,
                    specifications: this.specifications,
                    products: this.products,
                    attributes: this.attributes
                }
                if (this.specifications.length < 1) {
                    MessageBox.alert('请添加商品规格', '警告', {
                        confirmButtonText: '确定',
                        type: 'error'
                    })
                    return
                }
                if (this.products.length < 1) {
                    MessageBox.alert('请添加商品库存', '警告', {
                        confirmButtonText: '确定',
                        type: 'error'
                    })
                    return
                }
                this.$refs['goods'].validate(valid => {
                    if (valid) {
                        publishGoods(finalGoods).then(response => {
                            this.$notify.success({
                                title: '成功',
                                message: '创建成功'
                            })
                            this.$router.push({path: '/goods/list'})
                        }).catch(response => {
                            MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
                                confirmButtonText: '确定',
                                type: 'error'
                            })
                        })
                    }
                })

            },
            handleClose(tag) {
                this.keywords.splice(this.keywords.indexOf(tag), 1)
                this.goods.keywords = this.keywords.toString()
            },
            showInput() {
                this.newKeywordVisible = true
                this.$nextTick(_ => {
                    this.$refs.newKeywordInput.$refs.input.focus()
                })
            },
            handleInputConfirm() {
                const newKeyword = this.newKeyword
                if (newKeyword) {
                    this.keywords.push(newKeyword)
                    this.goods.keywords = this.keywords.toString()
                }
                this.newKeywordVisible = false
                this.newKeyword = ''
            },
            uploadPicUrl: function (response) {
                this.goods.picUrl = response.data.url
            },
            uploadOverrun: function () {
                this.$message({
                    type: 'error',
                    message: '上传文件个数超出限制!最多上传5张图片!'
                })
            },
            handleGalleryUrl(response, file, fileList) {
                if (response.errno === 0) {
                    this.goods.gallery.push(response.data.url)
                }
            },
            handleRemove: function (file, fileList) {
                for (var i = 0; i < this.goods.gallery.length; i++) {
                    // 这里存在两种情况
                    // 1. 如果所删除图片是刚刚上传的图片，那么图片地址是file.response.data.url
                    //    此时的file.url虽然存在，但是是本机地址，而不是远程地址。
                    // 2. 如果所删除图片是后台返回的已有图片，那么图片地址是file.url
                    var url
                    if (file.response === undefined) {
                        url = file.url
                    } else {
                        url = file.response.data.url
                    }

                    if (this.goods.gallery[i] === url) {
                        this.goods.gallery.splice(i, 1)
                    }
                }
            },
            uploadSpecPicUrl: function (response) {
                this.specForm.picUrl = response.data.url
            },
            handleSpecificationShow() {
                this.specForm = {specification: '', value: '', picUrl: ''}
                this.specVisiable = true
            },
            handleSpecificationAdd() {
                this.$refs['specForm'].validate(valid => {
                    if (valid) {
                        var index = this.specifications.length - 1
                        for (var i = 0; i < this.specifications.length; i++) {
                            const v = this.specifications[i]
                            if (v.specification === this.specForm.specification) {
                                this.$message({
                                    type: 'warning',
                                    message: '已经存在规格值:' + v.specification
                                })
                                this.specForm = {}
                                this.specVisiable = false
                            }
                        }

                        this.specifications.push(this.specForm)
                        this.specVisiable = false

                        this.specToProduct()
                    }
                })
            },
            handleSpecificationDelete(row) {
                const index = this.specifications.indexOf(row)
                this.specifications.splice(index, 1)
                this.specToProduct()
            },
            specToProduct() {
                if (this.specifications.length === 0) {
                    this.products = []
                    return
                }
                let specifications = this.specifications;
                var products = []
                for (let i = 0; i < specifications.length; i++) {
                    var spec = this.specifications[i]
                    products[i] = {specification: spec.specification, number: 0}
                }
                this.products = products
            },
            handleProductShow(row) {
                this.productForm = Object.assign({}, row)
                this.productVisiable = true
            },
            uploadProductUrl: function (response) {
                this.productForm.url = response.data.url
            },
            handleProductEdit() {
                this.$refs['productForm'].validate(valid => {
                    if (valid) {
                        for (var i = 0; i < this.products.length; i++) {
                            const v = this.products[i]
                            if (v.specification === this.productForm.specification) {
                                this.products.splice(i, 1, this.productForm)
                                break
                            }
                        }
                        this.productVisiable = false
                    }
                })
            },
            handleAttributeShow() {
                this.attributeForm = {}
                this.attributeVisiable = true
            },
            handleAttributeAdd() {
                this.$refs['attributeForm'].validate(valid => {
                    if (valid) {
                        this.attributes.unshift(this.attributeForm)
                        this.attributeVisiable = false
                    }
                })
            },
            handleAttributeDelete(row) {
                const index = this.attributes.indexOf(row)
                this.attributes.splice(index, 1)
            }
        }
    }
</script>
