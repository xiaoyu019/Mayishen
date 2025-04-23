<template>
  <div style="padding:25px;width: 100%; height:100%">
    <div class="header-title">开源代码库</div>
    <el-card shadow="always" ref="element" style="border-radius: 15px;">
      <div class="tool-menu">
        <div>
          <el-button @click="load">
            <el-icon>
              <Refresh/>
            </el-icon>
          </el-button>
          <el-button :loading="drawerVisible" type="primary" @click="handleShow">
            <el-icon>
              <Plus/>
            </el-icon>&nbsp;上传开源代码
          </el-button>
          <el-button :loading="dialogVisible" @click="this.dialogVisible=true">批量上传</el-button>
        </div>
        <div>
          <label class="tool-label">开源项目名称</label>
          <el-input v-model="nameSearch" placeholder="请输入开源项目名称" clearable
                    style="width: 200px;margin-right: 15px;"/>
          <el-button @click="load">
            <el-icon>
              <Search/>
            </el-icon> &nbsp;搜索
          </el-button>
        </div>
      </div>
      <el-table :data="filterTableData" stripe>
        <el-table-column prop="name" label="项目名称">
          <template #default="scope">
            <span style="font-weight: bold">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="200"/>
        <el-table-column prop="url" label="托管网址" width="400"/>
        <el-table-column prop="validFileNumber" label="有效代码文件数量"/>
        <el-table-column prop="totalLineNumber" label="代码行数"/>
        <el-table-column label="开源协议">
          <template #default="scope">
            {{ this.constValue.licenses[scope.row.license] }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template #default="scope">
            <el-popconfirm v-if="scope.row.status!=0" title="确认删除？" @confirm="handleDelete(scope.row.id)">
              <template #reference>
                <el-button type="danger" size="large" round>
                  <el-icon>
                    <Delete/>
                  </el-icon>&nbsp;删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 10px 0;">
        <el-pagination
            align="center"
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            background
            :page-sizes="[5, 10, 20]"
            layout="total, sizes,prev, pager, next, jumper"
            :total="total"
            @size-change="load"
            @current-change="load"
        />
      </div>
    </el-card>
    <el-drawer
        v-model="drawerVisible"
        :before-close="handleClose"
        title="上传开源代码"
        size="600px"
    >
      <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="210px" label-position="top" status-icon>
        <el-form-item label="开源代码名称" prop="name">
          <el-input v-model="form.name"/>
        </el-form-item>
        <el-form-item label="版本号" prop="version">
          <el-input v-model="form.version"/>
        </el-form-item>
        <el-form-item label="托管地址" prop="url">
          <el-input v-model="form.url"/>
        </el-form-item>
        <el-form-item label="开源许可证" prop="license">
          <el-select v-model="form.license" filterable>
            <el-option v-for="(license,index) in this.constValue.licenses" :label="license" :value="index"/>
          </el-select>
        </el-form-item>
        <el-form-item label="源代码" prop="upload">
          <el-upload v-show="this.form.path==null&&!uploading['add']" style="width: 100%;" drag ref="mainUpload"
                     :action="constValue.uploadUrl" accept=".zip" :show-file-list="false" :on-progress="onUpload"
                     :on-success="fileuploadSuccess" :on-change="fileChange" :headers="myheader"
                     :before-upload="beforeZipFileUpload">
            <template #trigger>
              <div>
                <el-icon class="el-icon--upload">
                  <upload-filled/>
                </el-icon>
                <div class="el-upload__text">
                  将文件拖到此处，或<em>点击上传</em>
                </div>
              </div>
            </template>
            <template #tip>
              <div class="el-upload__tip text-red">
                源代码要求为zip格式，文件大小至多为500MB
              </div>
            </template>
          </el-upload>
          <div v-show="uploading['add']||this.form.path"
               style="width:100%;border:1px solid  rgb(103,194,58);text-align:center;padding:15px;box-shadow: inset 0px 0px 10px 5px rgba(103,194,58,.1);border-radius: 5px;">
            <el-progress
                v-if="uploading['add']&&progressPercent['add']!=100"
                :percentage="progressPercent['add']" :text-inside="true"
                :stroke-width="24" status="success">
            </el-progress>
            <template v-if="this.form.path">
              <el-icon size="large" color="rgb(103,194,58)">
                <SuccessFilled/>
              </el-icon>&nbsp;<span style="font-size: 18px;">{{ form.sourceCodeFileName }}</span>&nbsp;已成功上传
              <div style="text-decoration: underline;color: #0D74FF;cursor: pointer;"
                   @click="this.form.path=null;this.uploading['add']=false;">重新上传
              </div>
            </template>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleClose" size="large">取消</el-button>
        <el-popconfirm title="确认上传？" @confirm="save">
          <template #reference>
            <el-button type="primary" size="large">
              <el-icon><Select/></el-icon>&nbsp;确认上传
            </el-button>
          </template>
        </el-popconfirm>
      </template>
    </el-drawer>

    <el-dialog
        v-model="dialogVisible"
        title="批量上传"
        width="90%"
        top="5vh"
        :before-close="this.form = {}"
    >
      <el-alert title="请使用系统提供的EXCEL或JSON模板！" type="success " style="margin: 5px;"/>
      <el-upload v-if="excelData.length == 0" action='' drag :auto-upload="false" accept=".xlsx,.json"
                 :on-change="uploadChange">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text" style="padding:100px 50px">将Excel或JSON文件拖到此处，或<em>点击上传</em></div>
      </el-upload>
      <div v-else>
        <el-table :data="showedExcelData" stripe>
          <el-table-column prop="name" label="项目名称" min-width="600">
            <template #default="scope">
              <span style="font-weight: bold">{{ scope.row.name }}</span>
              <div>版本：<span>{{ scope.row.version }}</span> 托管地址：<span>{{ scope.row.url }}</span></div>
            </template>
          </el-table-column>
          <el-table-column label="确认开源协议" width="400" align="center">
            <template #default="scope">
              <div>{{ scope.row.inputLicense }}</div>
              <el-select v-model="scope.row.license" filterable>
                <el-option v-for="(license,index) in this.constValue.licenses" :label="license" :value="index"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="上传文件" width="320" align="center">
            <template #default="scope">
              <el-progress
                  v-if="uploading[scope.row.name]&&progressPercent[scope.row.name]!=100"
                  :percentage="progressPercent[scope.row.name]" :text-inside="true"
                  :stroke-width="24" status="success">
              </el-progress>
              <template v-else>
                <el-upload
                    :action="constValue.uploadUrl" accept=".zip" :show-file-list="false"
                    :on-success="batchFileuploadSuccess" :on-change="fileChange" :on-progress="batchOnUpload"
                    :headers="myheader" :before-upload="beforeZipFileUpload"
                >
                  <el-button v-if="scope.row.path" type="success" size="large" round>已上传，重新上传</el-button>
                  <el-button v-else @click="this.form = scope.row">点击上传</el-button>
                </el-upload>
              </template>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" size="large" round @click="batchSave(scope.row)">
                <el-icon><Select/></el-icon>&nbsp;保存
              </el-button>
              <el-button type="danger" size="large" round @click="scope.row.isValid = false">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin: 10px 0;">
          <el-pagination
              align="center"
              v-model:current-page="batchPageNum"
              v-model:page-size="batchPageSize"
              background
              :page-sizes="[5, 10, 20]"
              layout="total, sizes,prev, pager, next, jumper"
              :total="this.filterExcelData.length"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="this.dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script>

import request from "@/utils/request";
import {ElNotification} from 'element-plus'
import {
  View,
  Picture as IconPicture,
  EditPen as Edit,
  Delete,
  Search,
  Plus,
  Minus,
  FullScreen,
  Select,
  UploadFilled,
  Refresh,
  SuccessFilled
} from '@element-plus/icons-vue'
import {readExcel} from "@/assets/js/ExcelUtils";
import * as XLSX from "xlsx";
import {constValue} from "@/assets/js/const.js";

export default {
  name: 'MemberManageView',
  components: {
    View,
    IconPicture,
    Edit,
    Delete,
    Search,
    Plus,
    Minus,
    FullScreen,
    Select,
    UploadFilled,
    Refresh,
    SuccessFilled,
    constValue
  },
  data() {
    return {
      constValue: constValue,
      myheader: {
        token: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).token : ""
      },
      nameSearch: "",
      pageNum: 1,
      pageSize: 10,
      total: 0,
      batchPageNum: 1,
      batchPageSize: 10,
      drawerVisible: false,
      dialogVisible: false,
      uploading: {},
      progressPercent: {},
      form: {
        license: 0
      },
      tableData: [],
      excelData: [],
      requirementDocumentName: '',
      sourceCodeFileName: '',
      rules: {
        name: [
          {required: true, message: "请输入项目名称！", trigger: "blur"},
          {min: 3, max: 50, message: "请检查项目名称长度，长度必须为3-50字符！", trigger: "blur"}
        ],
        version: [
          {required: true, min: 1, max: 20, message: "请检查版本号", trigger: "blur"}
        ],
        url: [
          {required: true, message: "请输入托管网址！", trigger: "blur"}
        ],
        license: [
          {required: true, message: "请选择开源协议", trigger: "blur"}
        ],
        upload: [
          {required: true, validator: this.validateUpload, trigger: "change"}
        ],
      }
    }
  },
  created() {
    this.load();
    setInterval(() => {
      this.load()
    }, 5000);
  },
  methods: {
    /**
     * @description 加载开源项目列表
     * @return {void}
     */
    load() {
      request.get("open-source-project", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          nameSearch: this.nameSearch
        }
      }).then(res => {
        if (res.code == "0") {
          this.tableData = res.data.records;
          this.total = res.data.total;
        } else {
          ElNotification({
            title: '加载开源代码库失败',
            message: res.msg,
            type: 'error',
          });
        }
      })
    },
    /**
     * @description 显示抽屉
     * @return {void}
     */
    handleShow() {
      this.drawerVisible = true;
      this.form = {};
    },
    /**
     * @description 关闭抽屉的回调方法
     * @return {void}
     */
    handleClose() {
      this.$refs.mainUpload.abort();
      this.drawerVisible = false;
      this.$refs.ruleFormRef.resetFields();
      this.form = {};
      this.uploading['add'] = false;
      this.progressPercent['add'] = 0;
    },
    /**
     * @description 保存开源项目
     * @return {void}
     */
    save() {
      this.$refs.ruleFormRef.validate((valid, fields) => {
        if (valid) {
          request.post("/open-source-project", this.form).then(res => {
            if (res.code == "0") {
              ElNotification({
                title: '上传开源代码成功',
                type: 'success',
              });
              this.uploading['add'] = false;
              this.progressPercent['add'] = 100;
              this.load();
              this.handleClose();
            } else {
              ElNotification({
                title: '上传开源代码失败',
                message: res.msg,
                type: 'error',
              });
            }
          });
        } else
          ElNotification({
            title: '请检查表单',
            type: 'error',
          });
      });
    },
    /**
     * @description 批处理保存开源项目
     * @return {void}
     */
    batchSave(data) {
      if (data.path)
        request.post("/open-source-project", data).then(res => {
          if (res.code == "0") {
            ElNotification({
              title: '上传开源代码成功',
              type: 'success',
            });
            this.load();
            data.isValid = false;
          } else {
            ElNotification({
              title: '上传开源代码失败',
              message: res.msg,
              type: 'error',
            });
          }
        });
    },
    /**
     * @description 根据ID删除开源项目
     * @param {number} id 开源项目ID
     * @return {void}
     */
    handleDelete(id) {
      request.delete("/open-source-project/delete/" + id).then(res => {
        if (res.code == "0") {
          ElNotification({
            title: '删除成功',
            type: 'success',
          });
          this.load();
        } else {
          ElNotification({
            title: '删除失败',
            message: res.msg,
            type: 'error',
          });
        }
      })
    },
    /**
     * @description 更改文件的回调方法
     * @param {Object} file 文件
     * @return {void}
     */
    fileChange(file) {
      if (file.raw.type !== 'application/x-zip-compressed') {
        ElNotification({
          title: '选择文件错误',
          message: '源代码压缩包必须为ZIP格式!',
          type: 'error',
        });
        this.$refs.mainUpload.clearFiles();
        return;
      }
      if (file.size / 1024 / 1024 > 500) {
        ElNotification({
          title: '文件过大，无法上传',
          message: '源代码压缩包大小不能超过500MB!',
          type: 'error',
        });
        this.$refs.mainUpload.clearFiles();
        return;
      }
      this.form.sourceCodeFileName = file.name;
    },
    /**
     * @description 上传文件前的回调函数
     * @param {Object} file 文件
     * @return {boolean} 是否符合要求
     */
    beforeZipFileUpload(file) {
      if (file.type !== 'application/x-zip-compressed') {
        ElNotification({
          title: '选择文件错误',
          message: '源代码压缩包必须为ZIP格式!',
          type: 'error',
        });
        return false;
      } else if (file.size / 1024 / 1024 > 500) {
        ElNotification({
          title: '文件过大，无法上传',
          message: '源代码压缩包大小不能超过500MB!',
          type: 'error',
        });
        return false;
      }
      this.form.sourceCodeFileName = file.name;
      return true;
    },
    /**
     * @description 上传文件成功的回调函数
     * @param {Object} res 返回结果
     * @return {void}
     */
    fileuploadSuccess(res) {
      if (res.code == "0") {
        ElNotification({
          title: '源代码上传成功',
          type: 'success',
        });
        this.form.path = res.data;
        this.progressPercent['add'] = 100;
      } else {
        ElNotification({
          title: '源代码上传失败',
          message: res.msg,
          type: 'error',
        });
        this.$refs.upload.clearFiles();
        this.uploading['add'] = false;
        this.progressPercent['add'] = 100;
      }
    },
    /**
     * @description 批量上传开源项目时上传文件的回调函数
     * @param {Object} file 文件
     * @return {boolean} 是否符合要求
     */
    batchFileuploadSuccess(res) {
      if (res.code == "0") {
        ElNotification({
          title: '源代码上传成功',
          type: 'success',
        });
        this.progressPercent[this.form.name] = 100;
        this.form.path = res.data;
      } else {
        ElNotification({
          title: '源代码上传失败',
          message: res.msg,
          type: 'error',
        });
        this.uploading[this.form.name] = false;
        this.progressPercent[this.form.name] = 0;
      }
    },
    /**
     * @description 验证是否上传文件
     * @param {Object} rule 规则
     * @param {Object} value 值
     * @param {Object} callback 回调方法
     * @return{void}
     */
    validateUpload(rule, value, callback) {
      if (!this.form.path) {
        callback(new Error('请上传源码'));
      } else {
        callback();
      }
    },
    /**
     * @description 文件上传时的回调方法
     * @param {Object} event 事件
     * @param {Object} file 文件
     * @param {Object} fileList 文件列表
     * @return{void}
     */
    onUpload(event, file, fileList) {
      this.uploading['add'] = true;
      if (Math.floor(event.percent) < 95)
        this.progressPercent['add'] = Math.floor(event.percent);
    },
    /**
     * @description 批量上传开源项目时文件上传时的回调方法
     * @param {Object} event 事件
     * @param {Object} file 文件
     * @param {Object} fileList 文件列表
     * @return{void}
     */
    batchOnUpload(event, file, fileList) {
      this.uploading[this.form.name] = true;
      if (Math.floor(event.percent) < 95)
        this.progressPercent[this.form.name] = Math.floor(event.percent);
    },
    /**
     * @description 批量上传开源项目时导入记录
     * @param {Object} file 文件
     * @return{void}
     */
    async uploadChange(file) {
      function getSimilarity(s, t, f) {
        if (!s || !t) {
          return 0
        }
        var l = s.length > t.length ? s.length : t.length
        var n = s.length
        var m = t.length
        var d = []
        f = f || 3
        var min = function (a, b, c) {
          return a < b ? (a < c ? a : c) : (b < c ? b : c)
        }
        var i, j, si, tj, cost
        if (n === 0) return m
        if (m === 0) return n
        for (i = 0; i <= n; i++) {
          d[i] = []
          d[i][0] = i
        }
        for (j = 0; j <= m; j++) {
          d[0][j] = j
        }
        for (i = 1; i <= n; i++) {
          si = s.charAt(i - 1)
          for (j = 1; j <= m; j++) {
            tj = t.charAt(j - 1)
            if (si === tj) {
              cost = 0
            } else {
              cost = 1
            }
            d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost)
          }
        }
        let res = (1 - d[n][m] / l)
        return res.toFixed(f)
      }

      if (file.name.indexOf('.xlsx') > -1) {
        let dataBinary = await readExcel(file.raw);
        let workBook = XLSX.read(dataBinary, {type: 'binary', cellDates: true});
        let workSheet = workBook.Sheets[workBook.SheetNames[0]];
        this.excelData = XLSX.utils.sheet_to_json(workSheet);
        if (this.excelData.length > 0) {
          this.excelData.forEach((value, index, array) => {
            if (value.hasOwnProperty("name") && value.hasOwnProperty("url") && value.hasOwnProperty("version")) {
              array[index].isValid = true;
              array[index].inputLicense = value.license;
              if (getSimilarity(array[index].license, 'Other') > 0.8) {
                array[index].license = 0;
              } else {
                let maxSimilarityLicense = 0, maxSimilarity = 0.0;
                for (let i = 1; i < this.constValue.licenses.length; i++) {
                  let similarity = getSimilarity(array[index].license, this.constValue.licenses[i]);
                  if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    maxSimilarityLicense = i;
                  }
                }
                array[index].license = maxSimilarityLicense > 0 ? maxSimilarityLicense : 0;
              }
            } else
              array[index].isValid = false;
          });
          await this.checkOpenSourceProject();
        }
      }
      if (file.name.indexOf('.json') > -1) {
        let reader = new FileReader()
        reader.readAsText(file.raw);
        reader.onload = ((e) => {
          let data = JSON.parse(e.target.result);
          if (data instanceof Array)
            this.excelData = data;
          else
            this.excelData = Object.values(data);
          if (this.excelData.length > 0) {
            this.excelData.forEach((value, index, array) => {
              if (value.hasOwnProperty("name") && value.hasOwnProperty("url") && value.hasOwnProperty("version")) {
                array[index].isValid = true;
                array[index].inputLicense = value.license;
                if (getSimilarity(array[index].license, 'Other') > 0.8) {
                  array[index].license = 0;
                } else {
                  let maxSimilarityLicense = 0, maxSimilarity = 0.0;
                  for (let i = 1; i < this.constValue.licenses.length; i++) {
                    let similarity = getSimilarity(array[index].license, this.constValue.licenses[i]);
                    if (similarity > maxSimilarity) {
                      maxSimilarity = similarity;
                      maxSimilarityLicense = i;
                    }
                  }
                  array[index].license = maxSimilarityLicense > 0 ? maxSimilarityLicense : 0;
                }
              } else
                array[index].isValid = false;
            });
            this.checkOpenSourceProject();
          }
        });
      }
    },
    /**
     * @description 检查开源项目是否存在
     * @return{void}
     */
    async checkOpenSourceProject() {
      for (let i = 0; i < this.excelData.length; i++)
        if (this.excelData[i].isValid) {
          const name = this.excelData[i].name, version = this.excelData[i].version;
          await request.get("/open-source-project/check/", {
                params: {
                  name: name,
                  version: version,
                }
              }
          ).then(res => {
            if (res.code == "0") {
              if (res.data)
                this.excelData[i].isValid = false;
            }
          });
        }
    }
  },
  computed: {
    filterTableData() {
      return this.tableData.filter(
          (data) => !this.keyword || data.name.indexOf(this.keyword) != -1)
    },
    filterExcelData() {
      return this.excelData.filter((value) => value.isValid).sort((a, b) => {
        return (a.name + '').localeCompare(b.name + '')
      });
    },
    showedExcelData() {
      const start = (this.batchPageNum - 1) * this.batchPageSize, end = this.batchPageNum * this.batchPageSize - 1;
      return this.filterExcelData.filter((value, index, array) => index >= start && index <= end);
    }
  },
}
</script>

<style scoped>

.header-title {
  font-weight: bold;
  font-size: 24px;
  margin-bottom: 15px;
}

.tool-menu {
  display: flex;
  justify-content: space-between;
  padding: 10px;
}


.tool-label {
  font-size: 14px;
  line-height: 32px;
  margin-right: 5px;
}

::v-deep .el-drawer__header {
  color: black;
  font-size: 20px;
  font-weight: bold;
}
</style>
