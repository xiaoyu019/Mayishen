<!--
* @FileDescription: 成本分析项目详情页
-->
<template>
  <el-card shadow="always">
    <el-breadcrumb separator="/" style="padding:15px;border-bottom: 1px solid lightgray;">
      <el-breadcrumb-item :to="{ path: '/hello' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item><a href="/cost-projects/">成本评估项目列表</a></el-breadcrumb-item>
      <el-breadcrumb-item> {{ this.projectData.name }}</el-breadcrumb-item>
    </el-breadcrumb>
    <div style="padding:15px;">
      <div class="card-header">
        <span class="header-title">{{ this.projectData.name }}</span>
        <div>
          <template v-if="this.projectData.status == 1">
            <el-popconfirm title="确认完成审查？" @confirm="finishProject">
              <template #reference>
                <el-button type="success" round size="large">
                  <el-icon><Select/></el-icon>&nbsp;完成审查
                </el-button>
              </template>
            </el-popconfirm>
            <el-button round @click="showRequireDocumentDialog" size="large">查看需求文档</el-button>
            <el-button round @click="goBack" type="primary" size="large">返回</el-button>
          </template>
          <template v-if="this.projectData.status == 2">
            <Docxtemplater v-if="this.projectData.status == 2" :id="pid">
              <el-icon>
                <Download/>
              </el-icon>&nbsp;导出报告
            </Docxtemplater>
            <el-button round @click="goBack" type="primary" size="large">返回</el-button>
          </template>
        </div>
      </div>
      <el-row :gutter="15">
        <el-col :span="16">
          <el-descriptions :column="24">
            <el-descriptions-item :span="8" label="送审单位" :min-width="300">
                                    <span class="project_item_content">
                                        {{ this.projectData.affiliation }}
                                    </span>
            </el-descriptions-item>
            <el-descriptions-item :span="8" label="所属行业" :min-width="200">
                                    <span class="project_item_content">
                                        {{ constValue.areas[this.projectData.area] }}
                                    </span>
            </el-descriptions-item>
            <el-descriptions-item :span="8" label="软件规模估算方式" :min-width="200">
                                    <span class="project_item_content">
                                        {{ ["预估功能点法", "估算功能点法"][this.projectData.scaleRadio] }}
                                    </span>
            </el-descriptions-item>
            <el-descriptions-item :span="8" label="上传时间">
                                    <span class="project_item_content">
                                        {{ this.projectData.createDate }}
                                    </span>
            </el-descriptions-item>
            <el-descriptions-item :span="16" label="备注" :min-width="400">
              {{ this.projectData.comment && this.projectData.comment.length > 0 ? this.projectData.comment : "无" }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="8">
          <el-descriptions :column="24" direction="vertical">
            <el-descriptions-item :span="8" label="送审金额" :min-width="200">
                                    <span class="project_item_content">
                                        {{ moneyFormat(this.projectData.totalAmount) + " 元" }}
                                    </span>
            </el-descriptions-item>
            <el-descriptions-item :span="8" label="项目状态" :min-width="180">
                                    <span class="project_item_content">
                                        <template v-if="this.projectData.status==-1"><div
                                            style="background-color: var(--el-color-error);box-shadow:0px 0px 5px 1px var(--el-color-error);width: 10px;height:10px;border-radius: 5px;display: inline-flex;margin-right: 5px;"></div>异常状态</template>
                                        <template v-if="this.projectData.status==1"><div
                                            style="background-color: var(--el-color-warning);box-shadow:0px 0px 5px 1px var(--el-color-warning);width: 10px;height:10px;border-radius: 5px;display: inline-flex;margin-right: 5px;"></div>正在审查</template>
                                        <template v-if="this.projectData.status==2"><div
                                            style="background-color: var(--el-color-success);box-shadow:0px 0px 5px 1px var(--el-color-success);width: 10px;height:10px;border-radius: 5px;display: inline-flex;margin-right: 5px;"></div>已审查</template>
                                    </span>
            </el-descriptions-item>
            <el-descriptions-item :span="8" label="软件开发总成本" :min-width="200">
                                    <span class="project_item_content">
                                        {{
                                        this.projectData.totalCost > 0 ? moneyFormat(this.projectData.totalCost) + " 元" : "未计算"
                                      }}
                                    </span>
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
    </div>
  </el-card>
  <div style="padding:25px;">
    <el-row :gutter="15">
      <el-col :span="24">
        <el-card style="padding: 5px;border-radius: 15px;" shadow="always">
          <template #header>
            <div class="card-header">
              <span class="header-title">软件开发成本计算</span>
              <div v-if="this.projectData.status == 1&&this.projectData.totalCost&&this.projectData.totalCost>0">
                <el-button class="button" plain round @click="this.$router.push('/calculate-cost/'+this.projectData.id)"
                           size="large">
                  <el-icon>
                    <Setting/>
                  </el-icon>&nbsp;重新计算软件开发成本
                </el-button>
              </div>
            </div>
          </template>

          <div v-show="this.projectData.totalCost==null||this.projectData.totalCost==0">
            <div style="padding:50px;text-align: center;font-size: 16px;line-height: 24px;">
              <el-link style="font-size: 16px;line-height: 24px;vertical-align: center;cursor:default;" disabled>
                尚未完成软件成本估算，点击前往
              </el-link>
              <el-link @click="this.$router.push('/calculate-cost/'+this.projectData.id)" type="primary"
                       style="font-size: 16px;line-height: 24px;vertical-align: center;">计算软件开发成本
              </el-link>
            </div>
          </div>
          <div v-show="this.projectData.totalCost>0">
            <el-tabs v-model="activeName" @tab-click="handleClick">
              <el-tab-pane label="成本分析结果" name="overview">
                <div style="border:2px dotted rgba(20,119,253,.2);padding:25px 15px 15px 15px;border-radius: 15px">
                  <el-descriptions :column="12" direction="vertical">
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          调整后规模(FP)
                        </div>
                      </template>
                      <div class="desc_content">
                        {{ this.projectData.totalScale }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          未调整工作量(p.h.)
                        </div>
                      </template>
                      <div class="desc_content">
                        {{ this.projectData.totalEffort }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          调整后工作量(p.h.)
                        </div>
                      </template>
                      <div class="desc_content">
                        {{ this.projectData.totalAdaptedEffort }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          <el-popover
                              placement="top-start"
                              :width="220"
                              trigger="hover"
                              content="直接人力成本与间接成本总和"
                          >
                            <template #reference>
                              软件开发成本(元)
                            </template>
                          </el-popover>
                        </div>
                      </template>
                      <div class="desc_content">
                        {{ moneyFormat(this.projectData.totalLabourCost) }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <el-popover
                            placement="top-start"
                            :width="220"
                            trigger="hover"
                            content="直接非人力成本"
                        >
                          <template #reference>
                            <div class="desc_label">
                              其他成本（元）
                            </div>
                          </template>
                        </el-popover>
                      </template>
                      <div class="desc_content">
                        {{ moneyFormat(this.projectData.totalNonlabourCost) }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          软件开发总成本（元）
                        </div>
                      </template>
                      <div class="desc_content">
                        {{ moneyFormat(this.projectData.totalCost) }}
                      </div>
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
                <el-descriptions :column="12" title="软件规模" direction="vertical">
                  <el-descriptions-item :span="4" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        未调整软件规模US (FP)
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ this.projectData.totalUnadaptedScale }}
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item :span="4" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        规模变更因子CF
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ this.projectData.scaleFactor }}
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item :span="4" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        调整后软件规模S=US×CF (FP)
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ this.projectData.totalScale }}
                    </div>
                  </el-descriptions-item>
                </el-descriptions>
                <el-descriptions :column="12" title="软件开发量" direction="vertical">
                  <template #extra>
                    <el-button @click="factorDialogVisible = true" round>查看工作量调整因子</el-button>
                  </template>
                  <el-descriptions-item :span="3" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        行业生产率PDR (p.h/FP)
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ constValue.Area2PDR[this.projectData.area] }}
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item :span="3" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        未调整工作量UE=PDR×S (p.h.)
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ this.projectData.totalEffort }}
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item :span="3" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        工作量调整因子A
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ totalFactor }}
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item :span="3" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        调整后工作量AE=UE×A (p.h.)
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ this.projectData.totalAdaptedEffort }}
                    </div>
                  </el-descriptions-item>
                </el-descriptions>
                <el-descriptions :column="12" title="软件开发成本" direction="vertical">
                  <template #extra>

                  </template>
                  <el-descriptions-item v-if="this.projectData.costRadio==1" :span="12" align="center"
                                        label-align="center">
                    <template #label>
                      <div class="desc_label">
                        工作量分配
                      </div>
                    </template>
                    <div class="desc_content">
                      <el-table :data="this.projectData.effortDistribution" size="small" border>
                        <el-table-column prop="name" label="开发人员类型"/>
                        <el-table-column prop="ratio" label="工作量占比(%)"/>
                        <el-table-column label="工作量E (p.h.)">
                          <template #default="scope">
                            {{ (scope.row.ratio / 100.0 * this.projectData.totalAdaptedEffort).toFixed(2) }}
                          </template>
                        </el-table-column>
                        <el-table-column prop="labourCostRate" label="人时费率F (元/p.h.)"/>
                        <el-table-column label="总成本E×F (元)">
                          <template #default="scope">
                            {{
                              moneyFormat(scope.row.ratio / 100.0 * this.projectData.totalAdaptedEffort * scope.row.labourCostRate)
                            }}
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item v-else :span="3" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        平均人时费率F (元/p.h.)
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ this.projectData.labourCostRate }}
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item :span="3" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        <el-popover
                            placement="top-start"
                            :width="220"
                            trigger="hover"
                            content="直接人力成本与间接成本总和"
                        >
                          <template #reference>
                            软件开发成本{{ this.projectData.costRadio == 1 ? '∑(E×F)' : 'AE×F' }} (元)
                          </template>
                        </el-popover>
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ moneyFormat(this.projectData.totalLabourCost) }}
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item :span="3" align="center" label-align="center">
                    <template #label>
                      <el-popover
                          placement="top-start"
                          :width="220"
                          trigger="hover"
                          content="直接非人力成本"
                      >
                        <template #reference>
                          <div class="desc_label">
                            其他成本DNC（元）
                          </div>
                        </template>
                      </el-popover>
                    </template>
                    <div class="desc_content">
                      {{ moneyFormat(this.projectData.totalNonlabourCost) }}
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item :span="3" align="center" label-align="center">
                    <template #label>
                      <div class="desc_label">
                        软件开发总成本 (元)
                      </div>
                    </template>
                    <div class="desc_content">
                      {{ moneyFormat(this.projectData.totalCost) }}
                    </div>
                  </el-descriptions-item>
                </el-descriptions>
              </el-tab-pane>
              <el-tab-pane label="软件功能点" name="functions">
                <el-row :gutter="15">
                  <el-col :span="12">
                    <div id="functionDistributionChart" style="width:800px;height:400px;text-align: center"></div>
                  </el-col>
                  <el-col :span="12">
                    <div>
                      <label style="margin-right: 5px;font-weight: bold;">功能点类型:</label>
                      <el-checkbox v-for="idx in 5" v-model="functionFilter[idx-1]"
                                   :label="constValue.function_types[idx-1]" border style="margin-right:5px;"/>
                    </div>
                    <el-table v-loading="functionLoading" :data="filterFunctions" :height="400" size="default"
                              header-row-style="color:black">
                      <el-table-column v-model="name" prop="name" label="功能项" sortable></el-table-column>
                      <el-table-column prop="type" label="类型" sortable>
                        <template #default="scope">
                          {{ constValue.function_types[scope.row.type] }}
                        </template>
                      </el-table-column>
                      <el-table-column fixed="right" label="操作">
                        <template #default="scope">
                          <el-button size="default" @click="showFunctionContentDialog(scope.row.id)">
                            <el-icon>
                              <Document/>
                            </el-icon>&nbsp;查看文本
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-col>
                </el-row>
              </el-tab-pane>
            </el-tabs>
          </div>

        </el-card>
      </el-col>
    </el-row>
  </div>

  <el-dialog
      v-model="factorDialogVisible"
      title="查看工作量调整因子"
      style="overflow: hidden"
      width="600px"
      top="35vh"
  >
    <el-descriptions :column="12" size="large">
      <el-descriptions-item :span="12" label="应用类型调整因子">{{ projectData.appFactor }}</el-descriptions-item>
      <el-descriptions-item :span="12" label="非功能性调整因子">{{
          projectData.nonFunctionFactor
        }}
      </el-descriptions-item>
      <el-descriptions-item :span="12" label="软件完整性级别调整因子">{{ projectData.integrityFactor }}
      </el-descriptions-item>
      <el-descriptions-item :span="12" label="开发语言调整因子">{{ projectData.platformFactor }}</el-descriptions-item>
      <el-descriptions-item :span="12" label="开发团队背景调整因子">{{ projectData.backgroundFactor }}
      </el-descriptions-item>
    </el-descriptions>

    <template #footer>
          <span class="dialog-footer">
            <el-button @click="factorDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="functionDialogVisible"
      title="查看功能点文本"
      width="600px"
      top="35vh"
  >
    <div v-html="html"></div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="this.functionDialogVisible = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="documentDialogVisible"
      title="查看需求文档"
      width="75%"
      top="5vh"
  >
    <el-scrollbar v-loading="documentLoading" style="height: calc(95vh - 200px)">
      <div ref="word"></div>
    </el-scrollbar>
    <template #footer>
          <span class="dialog-footer">
               <el-button @click="downloadFileByName(this.projectData.requirementDocumentPath)">下载</el-button>
            <el-button @click="this.documentDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>

</template>

<script>
import {constValue} from "@/assets/js/const.js";
import request from "@/utils/request";
import {ElNotification} from 'element-plus'
import {
  Document,
  Select,
  Setting,
  Download
} from '@element-plus/icons-vue'
import * as echarts from "echarts";
import Docxtemplater from '@/components/Docxtemplater'

var docxx = require("docx-preview");

export default {
  name: "CostProjectView",
  components: {
    Document,
    Select,
    Setting,
    echarts,
    Docxtemplater,
    constValue,
    Download
  },
  data() {
    return {
      constValue: constValue,
      activeName: "overview",
      functionRadio: "功能点分布",
      pid: 0,
      projectData: {
        totalCost: 0,
        totalOSCRatio: 0,
        createDate: "0000-00-00 00:00:00",
        openSourceLibraries: [],
        requirementDocumentPath: "--------------------------------.docx",
        sourceCodePath: "--------------------------------.zip",
        totalEffort: 0,
        totalAdaptedEffort: 0,
      },
      functions: [],
      tableData: [],
      maxHeight: "",
      functionDialogVisible: false,
      factorDialogVisible: false,
      documentDialogVisible: false,
      html: "",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      documentLoading: true,
      loading: true,
      functionLoading: true,
      functionFilter: [false, false, false, false, false],
      tab: "",
      form: {},
      distributionChart: '',
      chart2: '',
      rules: {
        name: [
          {required: true, message: "请输入项目名称！", trigger: "blur"},
          {min: 3, max: 20, message: "请检查项目名称长度，长度必须为3-20字符！", trigger: "blur"}
        ],
        version: [
          {required: true, min: 1, max: 20, message: "请检查用户名，长度必须为10-20字符！", trigger: "blur"}
        ],
        city: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        area: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        appFactor: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        integrityFactor: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        distributedFactor: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        performanceFactor: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        reliabilityFactor: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        multisiteFactor: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        platformFactor: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        backgroundFactor: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        type: [
          {required: true, message: "请选择一个选项", trigger: "change"}
        ],
        refreshData: ""
      },
    }
  },
  created() {
    this.pid = this.$route.params.id;
  },
  mounted() {
    this.loadProjectInfo();
  },
  computed: {
    filterFunctions() {
      return this.functions.filter((data) => this.functionFilter[data.type]);
    },
    totalFactor() {
      return (this.projectData.appFactor * this.projectData.integrityFactor * this.projectData.nonFunctionFactor * this.projectData.platformFactor * this.projectData.backgroundFactor).toFixed(2);
    },
    opinionData() {
      return [
        {value: this.projectData.totalCloneRatio * 100, name: '相似代码', itemStyle: 'red'},
        {value: 100 - this.projectData.totalCloneRatio * 100, name: '非相似代码', itemStyle: '#1FC48D'}
      ]
    },
  },
  methods: {
    /**
     * @description 加载项目信息
     * @return {void}
     */
    loadProjectInfo() {
      if (this.pid == undefined || this.pid == 0) {
        this.$router.push("/projects");
        return;
      }
      request.get("cost-project/get/" + this.pid).then(res => {
        if (res.code == "0") {
          this.projectData = res.data;
          this.functions = [];
          if (this.projectData.scaleRadio == 0)
            this.functionFilter = [true, true, false, false, false];
          else
            this.functionFilter = [true, true, true, true, true];
          window.document.title = this.projectData.name + '-码易审智能代码分析溯源系统';
          this.loading = false;
          this.loadFunction();
          setTimeout(this.drawFunctionDistributionChart, 100);
        }
        if (res.code == "600")
          this.$router.push("404");
      })
    },
    /**
     * @description 加载功能点
     * @return {void}
     */
    loadFunction() {
      request.get("function/getByProjectID/" + this.pid).then(res => {
        if (res.code == "0") {
          this.functions = res.data;
          this.functionLoading = false;
        }
      })
    },
    /**
     * @description 完成审核
     * @return {void}
     */
    finishProject() {
      this.projectData.status = 2;
      this.save();
    },
    /**
     * @description 格式化金额
     * @param {number} data 金额数值
     * @return {String} 格式化的金额字符串
     */
    moneyFormat(data) {
      if (!data) return '0.00'
      // 将数据分割，保留两位小数
      data = data.toFixed(2)
      // 获取整数部分
      const intPart = Math.trunc(data)
      // 整数部分处理，增加,
      const intPartFormat = intPart.toString().replace(/(\d)(?=(?:\d{4})+$)/g, '$1,')
      // 预定义小数部分
      let floatPart = '.00'
      // 将数据分割为小数部分和整数部分
      const newArr = data.toString().split('.')
      if (newArr.length === 2) { // 有小数部分
        floatPart = newArr[1].toString() // 取得小数部分
        return intPartFormat + '.' + floatPart
      }
      return intPartFormat + floatPart
    },
    /**
     * @description 更新成本分析项目信息
     * @return {void}
     */
    save() {
      request.put("/cost-project", this.projectData).then(res => {
        if (res.code == "0") {
          ElNotification({
            title: '更新成功',
            type: 'success',
          });
          this.loadProjectInfo();
        } else {
          ElNotification({
            title: '更新失败',
            message: res.msg,
            type: 'error',
          });
        }
      });
    },
    /**
     * @description 返回成本分析项目列表
     * @return {void}
     */
    goBack() {
      this.$router.push("/cost-projects/")
    },
    /**
      * @description 绘制功能点分布
      * @return {void}
      */
    drawFunctionDistributionChart() {
      this.distributionChart = echarts.init(document.getElementById('functionDistributionChart'));
      this.distributionChart.setOption({
        xAxis: {
          type: "category",
          data: ["ILF", "EIF", "EI", "EO", "EQ"]
        },
        yAxis: {type: "value"},
        series: {
          type: "bar", //形状为柱状图
          data: this.projectData.functionCount,
          barWidth: 25,
          itemStyle: {
            normal: {
              color: function (params) {
                let colorList = ["#EA5353", "#DB8D4D", "#9DD530", "#38CFCA", "#6C54E2", "#749f83", "#ca8622", "#bda29a", "#6e7074", "#546570"];
                return colorList[params.dataIndex];
              },
              label: {
                show: true,
                formatter: '{c}',  // 显示数值
                position: 'top'  // 数值在柱体上方
              }
            }
          }
        }
      });
      window.addEventListener("resize", () => {
        this.distributionChart.resize();
      });
    },
    /**
     * @description 显示需求文件对话框
     * @param {String} filename 文件名称
     * @return {void}
     */
    showRequireDocumentDialog() {
      this.documentDialogVisible = true;
      request.get("/files/get/" + this.projectData.requirementDocumentPath, {responseType: 'blob'}).then(res => {
        docxx.renderAsync(res, this.$refs.word).then(() => {
          this.documentLoading = false;
        });
      }).catch((error) => {
        ElNotification({
          title: '请求需求文件错误',
          message: error,
          type: 'error',
        });
      });
    },
    /**
     * @description 根据文件名称下载文件
     * @param {String} filename 文件名称
     * @return {void}
     */
    downloadFileByName(filename) {
      if (!this.loading)
        request.get("/files/get/" + filename, {responseType: 'blob'}).then(res => {
          var downloadUrl = window.URL.createObjectURL(res);
          var anchor = document.createElement('a');
          anchor.href = downloadUrl;
          anchor.setAttribute("download", filename);
          document.body.appendChild(anchor);
          anchor.click();
          document.body.removeChild(anchor);
        }).catch((error) => {
          ElNotification({
            title: '获取文件失败',
            message: error,
            type: 'error',
          });
        });
    },
    /**
      * @description 显示功能点文本对话框
      * @param {number} id 功能点ID
      * @return {void}
      */
    showFunctionContentDialog(id) {
      request.get("function/getFunctionContentById/" + id).then(res => {
        if (res.code == "0") {
          this.html = res.data;
          this.functionDialogVisible = true;
        } else {
          ElNotification({
            title: '加载功能点文本失败',
            message: res.msg,
            type: 'error',
          });
        }
      })
    },
    /**
     * @description 格式化日期
     * @param {String} value 未格式化的日期
     * @param {String} args 格式
     * @return {String} 格式化的日期
     */
    dateFormat(value, args) {
      var dt = new Date(value);
      if (args == 'yyyy-M-d') {// yyyy-M-d
        let year = dt.getFullYear();
        let month = dt.getMonth() + 1;
        let date = dt.getDate();
        return `${year}-${month}-${date}`;
      } else if (args == 'yyyy-M-d H:m:s') {// yyyy-M-d H:m:s
        let year = dt.getFullYear();
        let month = dt.getMonth() + 1;
        let date = dt.getDate();
        let hour = dt.getHours();
        let minute = dt.getMinutes();
        let second = dt.getSeconds();
        return `${year}-${month}-${date} ${hour}:${minute}:${second}`;
      } else if (args == 'yyyy-MM-dd') {// yyyy-MM-dd
        let year = dt.getFullYear();
        let month = (dt.getMonth() + 1).toString().padStart(2, '0');
        let date = dt.getDate().toString().padStart(2, '0');
        return `${year}-${month}-${date}`;
      } else {// yyyy-MM-dd HH:mm:ss
        let year = dt.getFullYear();
        let month = (dt.getMonth() + 1).toString().padStart(2, '0');
        let date = dt.getDate().toString().padStart(2, '0');
        let hour = dt.getHours().toString().padStart(2, '0');
        let minute = dt.getMinutes().toString().padStart(2, '0');
        let second = dt.getSeconds().toString().padStart(2, '0');
        return `${year}-${month}-${date} ${hour}:${minute}:${second}`;
      }
    }
  }
}
</script>

<style scoped>

.desc_label {
  color: #18191c;
  font-size: 14px;
  line-height: 18px;
}

.desc_content {
  font-weight: bolder;
  font-size: 18px;
  line-height: 32px;
}

.project_item_content {
  font-weight: bold;
  font-size: 16px;
  line-height: 32px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.header-title {
  font-weight: bold;
  font-size: 24px;
}

::v-deep .k {
  font-weight: bolder;
  color: red;
  text-decoration: underline;
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

::v-deep .el-descriptions__title {
  font-weight: bold;
  font-size: 20px;
  margin: 25px 0px;
  padding-left: 15px;
  border-left: 5px solid rgb(19, 119, 255);
}
</style>