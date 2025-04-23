<template>
  <div style="padding:25px;width: 100%; height:100%">
    <div class="header-title">源码分析项目</div>
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
            </el-icon>&nbsp;新建项目
          </el-button>
        </div>
        <div>
          <label class="tool-label">项目名称</label>
          <el-input v-model="nameSearch" placeholder="请输入项目名称" clearable
                    style="width: 200px;margin-right: 15px;"/>
          <label class="tool-label">送审单位名称</label>
          <el-input v-model="affiliationSearch" placeholder="请输入送审单位名称" clearable
                    style="width: 200px;margin-right: 15px;"/>
          <el-button @click="load">
            <el-icon>
              <Search/>
            </el-icon> &nbsp;搜索
          </el-button>
          <el-button @click="reset" style="margin-right: 15px;">重置</el-button>
          <el-radio-group v-model="viewRadio" style="height: 32px;" fill="rgb(220,220,220)">
            <el-radio-button :label="0">
              <list-two theme="outline" size="14" fill="#444"/>
            </el-radio-button>
            <el-radio-button :label="1">
              <view-grid-card theme="outline" size="14" fill="#444"/>
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <el-table v-if="viewRadio==0" :data="tableData" stripe header-row-style="color:black">
        <el-table-column fixed prop="name" label="项目名称" sortable min-width="200">
          <template #default="scope">
            <el-link style="font-weight: bold" @click="moveto(scope.row.id)" :disabled="scope.row.status!=1">
              {{ scope.row.name }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="affiliation" label="送审单位" width="140"/>
        <el-table-column label="总文件数" width="100" align="right">
          <template #default="scope">
            <template v-if="scope.row.status==1">
              {{ scope.row.totalFileNumber }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="代码文件数" width="100" align="right">
          <template #default="scope">
            <template v-if="scope.row.status==1">
              {{ scope.row.validFileNumber }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="原创代码文件数" width="125" align="right">
          <template #default="scope">
            <template v-if="scope.row.status==1">
              {{ scope.row.validFileNumber - scope.row.cloneFileNumber }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="原创代码文件占比" width="140" align="center">
          <template #default="scope">
            <template v-if="scope.row.status==1">
              <el-progress
                  :percentage="((scope.row.validFileNumber - scope.row.cloneFileNumber)/scope.row.validFileNumber *100).toFixed(2)"
                  color="rgb(63,158,255)"/>
            </template>
          </template>
        </el-table-column>
        <el-table-column label="总代码行数" width="100" align="right">
          <template #default="scope">
            <template v-if="scope.row.status==1">
              {{ scope.row.totalLineNumber }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="原创代码行数" width="120" align="right">
          <template #default="scope">
            <template v-if="scope.row.status==1">
              {{ scope.row.totalLineNumber - scope.row.cloneLineNumber }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="原创代码行数占比" width="140" align="center">
          <template #default="scope">
            <template v-if="scope.row.status==1">
              <el-progress
                  :percentage="((scope.row.totalLineNumber - scope.row.cloneLineNumber)/scope.row.totalLineNumber *100).toFixed(2)"
                  color="rgb(103,194,58)"/>
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="createDate" label="上传时间" sortable width="200"/>
        <el-table-column fixed="right" label="操作" width="240">
          <template #default="scope">
            <el-button v-if="scope.row.status==-1" @click="restart(scope.row.id)" size="large" text>重启</el-button>
            <el-progress v-if="scope.row.status==0" :percentage="scope.row.progress" width="64" style="height:40px;"/>
            <template v-else>
              <el-button v-if="scope.row.status>0" type="default" @click="moveto(scope.row.id)" size="large" round>
                <el-icon>
                  <FullScreen/>
                </el-icon>&nbsp;查看报告
              </el-button>
              <el-popconfirm v-if="scope.row.status!=0" title="确认删除？" @confirm="handleDelete(scope.row.id)">
                <template #reference>
                  <el-button size="large" type="danger" round>
                    <el-icon>
                      <Delete/>
                    </el-icon>&nbsp;删除
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <el-table v-else :data="tableData" stripe header-row-style="color:black">
        <el-table-column width="400px">
          <template #default="scope">
            <el-link style="font-weight: bold;font-size: 20px;margin:15px 0;" @click="moveto(scope.row.id)">
              {{ scope.row.name }}
            </el-link>
            <el-row :column="24">
              <el-col :span="6" :min-width="180" style="height: 32px;">
                <peoples theme="outline" size="16" fill="#444444" style="margin-right: 5px;"/>
                <span>
                      {{ scope.row.affiliation }}
                    </span>
              </el-col>
              <el-col :span="12" label="上传时间" :min-width="180">
                <time-icon theme="outline" size="16" fill="#444444" style="margin-right: 5px;"/>
                <span>
                      {{ scope.row.createDate }}
                </span>
              </el-col>
            </el-row>
          </template>
        </el-table-column>
        <el-table-column align="center" min-width="400px">
          <template #default="scope">
            <el-progress v-if="scope.row.status==0" :percentage="scope.row.progress" width="480"/>
            <el-descriptions v-else :column="24" direction="vertical">
              <el-descriptions-item :span="6" label="总文件数" :min-width="100" align="center">
                <span class="project_item_content">
                  {{ scope.row.totalFileNumber }}
                </span>
              </el-descriptions-item>
              <el-descriptions-item :span="9" label="原创/总代码文件数" :min-width="120" align="center">
                  <span class="project_item_content">
                     {{ scope.row.validFileNumber - scope.row.cloneFileNumber }}/{{ scope.row.validFileNumber }}
                  </span>
              </el-descriptions-item>
              <el-descriptions-item :span="9" label="原创/总代码行数" :min-width="120" align="center">
                  <span class="project_item_content">
                    {{ scope.row.totalLineNumber - scope.row.cloneLineNumber }}/{{ scope.row.totalLineNumber }}
                  </span>
              </el-descriptions-item>
            </el-descriptions>
          </template>
        </el-table-column>
        <el-table-column align="center" min-width="280px">
          <template #default="scope">
            <template v-if="scope.row.status==1">
              <el-progress type="dashboard"
                           :percentage="((scope.row.validFileNumber - scope.row.cloneFileNumber)/scope.row.validFileNumber *100).toFixed(2)"
                           color="rgb(63,158,255)" width="114">
                <template #default="{ percentage }">
                  <div class="percentage-value">{{ percentage }}%</div>
                  <div class="percentage-label">原创文件数占比</div>
                </template>
              </el-progress>
              <el-progress type="dashboard"
                           :percentage="((scope.row.totalLineNumber - scope.row.cloneLineNumber)/scope.row.totalLineNumber *100).toFixed(2)"
                           color="rgb(103,194,58)" width="114">
                <template #default="{ percentage }">
                  <div class="percentage-value">{{ percentage }}%</div>
                  <div class="percentage-label">原创行数占比</div>
                </template>
              </el-progress>
            </template>
          </template>
        </el-table-column>
        <el-table-column align="center" min-width="280px">
          <template #default="scope">
            <template v-if="scope.row.status==1">
              <el-descriptions :column="12" direction="vertical">
                <el-descriptions-item :span="6" label="依赖库数" :min-width="120" align="center">
                  <span class="project_item_content">
                    {{ scope.row.dependencyNum }}
                  </span>
                </el-descriptions-item>
                <el-descriptions-item :span="6" label="漏洞数" :min-width="120" align="center">
                  <span class="project_item_content">
                    {{ scope.row.bugNum }}
                  </span>
                </el-descriptions-item>
              </el-descriptions>
            </template>
          </template>
        </el-table-column>
        <el-table-column min-width="200px">
          <template #default="scope">
            备注：{{ scope.row.comment && scope.row.comment.length > 0 ? scope.row.comment : "无" }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="240" align="center">
          <template #default="scope">
            <div v-if="scope.row.status==-1">
              <el-button @click="restart(scope.row.id)" size="large" text>重启</el-button>
            </div>
            <div v-if="scope.row.status==1">
              <el-button v-if="scope.row.status>0" type="default" @click="moveto(scope.row.id)" size="large" round>
                <el-icon>
                  <FullScreen/>
                </el-icon>&nbsp;查看报告
              </el-button>
            </div>
            <div v-if="scope.row.status!=-1" style="margin-top: 5px;">
              <el-popconfirm v-if="scope.row.status!=0" title="确认删除？" @confirm="handleDelete(scope.row.id)">
                <template #reference>
                  <el-button size="large" type="danger" round>
                    <el-icon>
                      <Delete/>
                    </el-icon>&nbsp;删除
                  </el-button>
                </template>
              </el-popconfirm>
            </div>

          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <div style="margin: 10px 0;">
      <el-pagination
          align="center"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          background
          :page-sizes="[5, 10, 20]"
          layout="total, sizes,prev, pager, next, jumper"
          :total="total"
          @size-change="load"
          @current-change="load"
      />
    </div>
    <el-drawer
        v-model="drawerVisible"
        :before-close="handleClose"
        size="600px"
        title="新建项目"
    >
      <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="210px" label-position="top" status-icon>
        <div class="step-pane">
          <div class="form-label" style="margin-top: 0px;">项目信息</div>
          <div class="pane-body">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="form.name" style="width: 600px;" maxlength="20" show-word-limit/>
            </el-form-item>
            <el-form-item label="送审单位" prop="affiliation">
              <el-input v-model="form.affiliation" style="width: 600px;" maxlength="20" show-word-limit/>
            </el-form-item>
            <el-form-item label="备注" prop="comment">
              <el-input type="textarea" v-model="form.comment" style="width: 600px;" maxlength="100" show-word-limit/>
            </el-form-item>
          </div>
        </div>
        <div class="step-pane">
          <div class="form-label">源代码上传</div>
          <div class="pane-body">
            <el-form-item label="上传文件" prop="upload">
              <el-upload v-show="this.form.sourceCodePath==null&&!uploading" style="width: 100%;" drag ref="upload"
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
              <div v-show="this.form.sourceCodePath || uploading"
                   style="width:100%;border:1px solid  rgb(103,194,58);text-align:center;padding:15px;box-shadow: inset 0px 0px 10px 5px rgba(103,194,58,.1);border-radius: 5px;">
                <el-progress
                    v-if="uploading&&progressPercent!=100"
                    :percentage="progressPercent" :text-inside="true"
                    :stroke-width="24" status="success">
                </el-progress>
                <template v-if="this.form.sourceCodePath">
                  <el-icon size="large" color="rgb(103,194,58)">
                    <SuccessFilled/>
                  </el-icon>&nbsp;<span style="font-size: 18px;">{{ form.sourceCodeFileName }}</span>&nbsp;已成功上传
                  <div style="text-decoration: underline;color: #0D74FF;cursor: pointer;"
                       @click="this.form.sourceCodePath=null;this.uploading=false;">重新上传
                  </div>
                </template>
              </div>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="handleClose" size="large">取消</el-button>
        <el-popconfirm title="确认新建？" @confirm="save">
          <template #reference>
            <el-button type="primary" size="large">
              <el-icon><Select/></el-icon>&nbsp;新建项目
            </el-button>
          </template>
        </el-popconfirm>
      </template>
    </el-drawer>
  </div>
</template>

<script>

import request from "@/utils/request";
import {ElNotification} from 'element-plus';
import {
  Delete,
  Search,
  Plus,
  FullScreen,
  Select,
  UploadFilled,
  Download,
  Refresh,
  SuccessFilled
} from '@element-plus/icons-vue';
import Docxtemplater from '@/components/Docxtemplater.vue';
import {ListTwo, ViewGridCard, Peoples, Time as TimeIcon} from '@icon-park/vue-next';
import {constValue} from "@/assets/js/const"

export default {
  name: 'CodeListView',
  components: {
    Delete,
    Search,
    Plus,
    FullScreen,
    Select,
    UploadFilled,
    Download,
    Refresh,
    SuccessFilled,
    Docxtemplater,
    ListTwo,
    ViewGridCard,
    Peoples,
    TimeIcon,
    constValue
  },
  data() {
    return {
      constValue,
      myheader: {
        token: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).token : ""
      },
      drawerVisible: false,
      form: {},
      rules: {
        name: [
          {required: true, message: "请输入项目名称！", trigger: "blur"},
          {min: 3, max: 20, message: "请检查项目名称长度，长度必须为3-20字符！", trigger: "blur"}
        ],
        affiliation: [
          {required: true, min: 2, max: 20, message: "请检查送审单位名称，长度必须为10-20字符！", trigger: "blur"}
        ],
        upload: [
          {required: true, validator: this.validateUpload, trigger: "change"}
        ],
      },
      keyword: "",
      nameSearch: "",
      affiliationSearch: "",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      viewRadio: 0,
      tableLayout: "list",
      tableData: [],
      uploading: false,
      progressPercent: 0
    }
  },
  computed: {},
  created() {
    this.load();
  },
  methods: {
    /**
     * @description 加载源码分析项目列表
     * @return {void}
     */
    load() {
      request.get("code-project", {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          nameSearch: this.nameSearch,
          affiliationSearch: this.affiliationSearch
        }
      }).then(res => {
        if (res.code == "0") {
          this.tableData = res.data.records;
          this.total = res.data.total;
          for (let data of this.tableData)
            if (data.status == 0) {
              setTimeout(this.load, 2000);
              break;
            }
        }
      })
    },
    /**
     * @description 重置源码分析项目列表搜索条件
     * @return {void}
     */
    reset() {
      this.nameSearch = '';
      this.affiliationSearch = '';
      this.load();
    },
    /**
     * @description 根据ID删除源码分析项目列表
     * @param {number} id 源码分析项目列表ID
     * @return {void}
     */
    handleDelete(id) {
      request.delete("/code-project/delete/" + id).then(res => {
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
     * @description 根据ID跳转到源码分析项目详情页
     * @param {number} pid 源码分析项目列表ID
     * @return {void}
     */
    moveto(pid) {
      this.$router.push("/code-project/" + pid);
    },
    /**
     * @description 根据ID重启源码分析任务
     * @param {number} id 源码分析项目列表ID
     * @return {void}
     */
    restart(id) {
      request.post("/code-project/restartTask/" + id).then(res => {
        if (res.code == "0") {
          ElNotification({
            title: '重启成功',
            type: 'success',
          });
          this.load();
        } else {
          ElNotification({
            title: '重启失败',
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
      this.form = {};
      this.drawerVisible = true;
    },
    /**
     * @description 抽屉关闭的回调方法
     * @return {void}
     */
    handleClose() {
      this.$refs.upload.abort();
      this.drawerVisible = false;
      this.form = {};
      this.$refs.ruleFormRef.resetFields();
      this.uploading = false;
      this.progressPercent = 0;
    },
    /**
     * @description 新增源码分析项目
     * @return {void}
     */
    save() {
      this.$refs.ruleFormRef.validate((valid, fields) => {
        if (valid) {
          request.post("/code-project", this.form).then(res => {
            if (res.code == "0") {
              ElNotification({
                title: '新建项目成功',
                type: 'success',
              });
              this.handleClose();
              this.load();
            } else {
              ElNotification({
                title: '新建项目失败',
                message: res.msg,
                type: 'error',
              });
            }
          });
        } else
          ElNotification({
            title: '请检查表单输入',
            type: 'error',
          });
      });
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
        this.$refs.upload.clearFiles();
        this.progressPercent = 0;
        this.uploading = false;
        return;
      }
      if (file.size / 1024 / 1024 > 500) {
        ElNotification({
          title: '文件过大，无法上传',
          message: '源代码压缩包大小不能超过500MB!',
          type: 'error',
        });
        this.$refs.upload.clearFiles();
        this.progressPercent = 0;
        this.uploading = false;
        return;
      }
      this.form.sourceCodeFileName = file.name;
    },
    /**
     * @description 文件过程回调方法
     * @param {Object} event 事件
     * @param {Object} file 文件
     * @param {Array} fileList 文件列表
     */
    onUpload(event, file, fileList) {
      this.uploading = true;
      if (Math.floor(event.percent) < 95)
        this.progressPercent = Math.floor(event.percent);
    },
    /**
     * @description 文件上传成功回调方法
     * @param {Object} res 返回结果
     */
    fileuploadSuccess(res) {
      if (res.code == "0") {
        this.form.sourceCodePath = res.data;
        ElNotification({
          title: '源代码上传成功',
          type: 'success',
        });
        this.progressPercent = 100;
      } else {
        ElNotification({
          title: '源代码上传失败',
          message: res.msg,
          type: 'error',
        });
        this.form.sourceCodeFileName = "";
        this.$refs.upload.clearFiles();
        this.progressPercent = 0;
        this.uploading = false;
      }
    },
    submitUpload() {
      this.$refs.upload.submit();
    },
    /**
     * @description 文件上传前回调方法
     * @param {Object} file 文件
     * @return {boolean} 是否符合要求
     */
    beforeZipFileUpload(file) {
      if (file.type !== 'application/x-zip-compressed') {
        ElNotification({
          title: '上传源代码错误',
          message: '源代码压缩包必须为ZIP格式!',
          type: 'error',
        });
        return false;
      } else if (file.size / 1024 / 1024 > 500) {
        ElNotification({
          title: '上传源代码错误',
          message: '源代码压缩包大小不能超过500MB!',
          type: 'error',
        });
        return false;
      }
      this.form.sourceCodeFileName = file.name;
      return true;
    },
    /**
     * @description 文件是否上传的校验方法
     * @param {Object} rule 规则
     * @param {Object} value 值
     * @param {Object} callback 回调函数
     */
    validateUpload(rule, value, callback) {
      if (!this.form.sourceCodePath) {
        callback(new Error('请上传源码'));
      } else {
        callback();
      }
    }


  }
}
</script>

<style scoped>

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

.header-title {
  font-weight: bold;
  font-size: 24px;
  margin-bottom: 15px;
}

.form-label {
  font-weight: bold;
  font-size: 20px;
  margin: 25px 0px;
  padding-left: 15px;
  border-left: 5px solid rgb(19, 119, 255);
}

.project_item_content {
  font-weight: bold;
  font-size: 16px;
  line-height: 32px;
}

.percentage-value {
  margin-top: 5px;
  font-size: 20px;
  font-weight: bold;
}

.percentage-label {
  margin-top: 5px;
  font-size: 5px;
}

::v-deep .el-button--primary {
  border: none;
  --el-button-bg-color: rgb(19, 119, 255);
}

::v-deep .el-tabs__item.is-active {
  color: gray;
}

::v-deep .el-tabs__item:hover {
  color: rgb(19, 119, 255);
}

::v-deep .el-tabs__item.is-active {
  font-weight: bold;
  color: black;
}

::v-deep .el-tabs__nav-wrap::after {
  background-color: transparent;
}

::v-deep .el-tabs__active-bar {
  background-color: rgb(19, 119, 255);
}

::v-deep label.el-radio-button {
  height: 32px;
  font-size: var(--el-font-size-base);
}

::v-deep .el-descriptions__body, ::v-deep .el-table tr {
  background-color: transparent;
}

::v-deep .el-drawer__header {
  color: black;
  font-size: 20px;
  font-weight: bold;
}
</style>
