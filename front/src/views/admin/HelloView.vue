<!--
* @FileDescription: 首页
-->
<template>
  <el-row :gutter="15" style="padding:25px;">
    <!-- 数据卡片-->
    <el-col :span="24" style="margin-bottom: 15px;">
      <el-row :gutter="15">
        <el-col :span="4">
          <el-card class="statistic-card"
                   style="background-color:rgb(63,158,255);box-shadow:2px 2px  7px 7px rgba(63,158,255,.2);color: white;"
                   @click.native="this.$router.push({name:'CostProjectListView',params:{activeName:'reviewing'}})"
                   shadow="always">
            <div style="text-align: left;">成本评估项目</div>
            <div style="display: flex;justify-content: space-between;">
              <div style="text-align: left;font-weight: bolder;font-size:24px;padding:5px;">{{ costProjectCount }}</div>
              <div style="margin-top:40px;">
                <template v-if="costProjectThisMonthCount>0">本月 <span
                    style="font-weight: bolder;">{{ costProjectThisMonthCount }}</span>
                  <el-icon>
                    <trending-up theme="outline" size="32" fill="#FFFFFF"/>
                  </el-icon>
                </template>
                <template v-else>本月无新增</template>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="statistic-card" shadow="always"
                   @click.native="this.$router.push({name:'CostProjectListView',params:{activeName:'reviewing'}})">
            <div style="text-align: left;">尚未审查</div>
            <div style="display: flex;justify-content: space-between;">
              <div style="text-align: left;font-weight: bolder;font-size:24px;padding:5px;">
                {{ finishedCostProjectCount }}
              </div>
              <el-progress :width="64" type="circle"
                           :percentage="costProjectCount==0?0:(finishedCostProjectCount/costProjectCount *100).toFixed(0)"/>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="statistic-card" shadow="always">
            <div style="text-align: left;">审查总金额</div>
            <div style="display: flex;justify-content: space-between;">
              <div style="text-align: left;font-weight: bolder;font-size:24px;padding:5px;">
                {{
                  (totalAmount > 1e5 ? (totalAmount > 1e9 ? (totalAmount / 1e8).toFixed(2) + "亿" : (totalAmount / 1e4) + "万") : totalAmount.toString()).replace(/(\d)(?=(?:\d{4})+[\u4e00-\u9fa5]?$)/g, '$1,')
                }}
              </div>
              <div ref="amountChart" style="width: 100px;height:64px;"></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="statistic-card"
                   style="background-color:rgb(103,194,58);box-shadow:2px 2px  7px 7px rgba(103,194,58,.2);color: white;"
                   @click="this.$router.push({name:'CodeProjectListView'})" shadow="always">
            <div style="text-align: left;">源码分析项目</div>
            <div style="display: flex;justify-content: space-between;">
              <div style="text-align: left;font-weight: bolder;font-size:24px;padding:5px;">{{ codeProjectCount }}</div>
              <div style="margin-top:40px;">
                <template v-if="codeProjectThisMonthCount>0">本月 <span
                    style="font-weight: bolder;">{{ codeProjectThisMonthCount }}</span>
                  <el-icon>
                    <trending-up theme="outline" size="32" fill="#FFFFFF"/>
                  </el-icon>
                </template>
                <template v-else>本月无新增</template>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="statistic-card" shadow="always">
            <div style="text-align: left;">总代码文件数</div>
            <div style="display: flex;justify-content: space-between;">
              <div style="text-align: left;font-weight: bolder;font-size:24px;padding:5px;">
                {{
                  (totalValidFileNumber > 1e5 ? (totalValidFileNumber > 1e9 ? (totalValidFileNumber / 1e8).toFixed(2) + "亿" : (totalValidFileNumber / 1e4).toFixed(1) + "万") : totalValidFileNumber.toString()).replace(/(\d)(?=(?:\d{4})+$)/g, '$1,')
                }}
              </div>
              <div ref="fileNumberChart" style="width: 100px;height:64px;"></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="statistic-card" shadow="always">
            <div style="text-align: left;">总代码行数</div>
            <div style="display: flex;justify-content: space-between;">
              <div style="text-align: left;font-weight: bolder;font-size:24px;padding:5px;">
                {{
                  (totalLineNumber > 1e5 ? (totalLineNumber > 1e9 ? (totalLineNumber / 1e8).toFixed(2) + "亿" : (totalLineNumber / 1e4).toFixed(1) + "万") : totalLineNumber.toString()).replace(/(\d)(?=(?:\d{4})+$)/g, '$1,')
                }}
              </div>
              <div ref="lineNumberChart" style="width: 100px;height:64px;"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-col>
    <!--  详细图表卡片-->
    <el-col :span="12" style="margin-bottom: 15px;">
      <el-card class="detail-card" style="" shadow="always">
        <div class="card-header">
          <span class="header-title">成本评估项目统计</span>
        </div>
        <el-row :gutter="15" style="padding:15px 15px 0px 15px;">
          <el-col :span="12" style="margin-bottom: 15px;">
            <div ref="costAffiliationChart" style="min-width:400px;width:100%;height:256px;"></div>
          </el-col>
          <el-col :span="12" style="margin-bottom: 15px;">
            <div ref="costAreaChart" style="width:100%;height:256px;"></div>
          </el-col>
        </el-row>
      </el-card>
    </el-col>
    <el-col :span="12" style="margin-bottom: 15px;">
      <el-card class="detail-card" style="border-radius: 15px;" shadow="always">
        <div class="card-header">
          <span class="header-title">源码分析项目统计</span>
        </div>
        <el-row :gutter="15" style="padding:15px 15px 0px 15px;">
          <el-col :span="12" style="margin-bottom: 15px;">
            <div ref="codeAffiliationChart" style="width:100%;height:256px;"></div>
          </el-col>
          <el-col :span="12" style="margin-bottom: 15px;">
            <div ref="codeCloneChart" style="width:100%;height:256px;"></div>
          </el-col>
        </el-row>
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-row>
        <el-col :span="24">
          <el-card style="border-radius: 15px;margin-bottom: 15px;" shadow="always">
            <div class="card-header">
              <span class="header-title">每日项目统计</span>
            </div>
            <div style="padding:15px;">
              <div v-show="codeProjectCountByDay.length>0||costProjectCountByDay.length>0" ref="projectCountChart"
                   style="min-width:400px;width:100%;height:240px;"></div>
              <el-empty v-show="codeProjectCountByDay.length==0&&costProjectCountByDay.length==0" description="暂无数据"
                        style="width:100%;height:240px;"/>
            </div>
          </el-card>
        </el-col>
        <el-col :span="24" style="margin-bottom: 15px;">
          <el-card class="detail-card" style="height:340px;" shadow="always">
            <div class="card-header">
              <span class="header-title">最新成本评估项目</span>
              <el-button type="primary" round plain @click="showAll(1)">查看所有项目</el-button>
            </div>
            <div style="padding: 15px;">
              <el-table :data="costProjects" stripe header-row-style="color:black">
                <el-table-column prop="name" label="项目名称">
                  <template #default="scope">
                    <el-link style="font-weight: bold" @click="moveTo(1,scope.row.id)">{{ scope.row.name }}</el-link>
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="120">
                  <template #default="scope">
                    <el-tag type="error" round plain v-if="scope.row.status==-1">异常</el-tag>
                    <el-tag type="primary" round plain v-if="scope.row.status==1">正在审查</el-tag>
                    <el-tag type="success" round plain v-if="scope.row.status==2">已审查</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="affiliation" label="送审单位" width="100"/>
                <el-table-column label="送审金额（元）" width="150">
                  <template #default="scope">
                    {{ moneyFormat(scope.row.totalAmount) }}
                  </template>
                </el-table-column>
                <el-table-column prop="createDate" label="上传时间" width="180"/>
              </el-table>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-col>
    <el-col :span="12">
      <el-row :gutter="15">
        <el-col :span="24" style="margin-bottom: 15px;">
          <el-card class="detail-card" shadow="always">
            <div class="card-header">
              <span class="header-title">快捷入口</span>
            </div>
            <el-row :gutter="15" style="padding:15px 15px 0px 15px;">
              <el-col :span="8" style="margin-bottom: 15px;">
                <el-button class="entrance-btn" text
                           @click="this.$router.push({name:'CostProjectListView'})">
                  <paper-money theme="multi-color" size="32" :fill="['#999' ,'#0D74FF' ,'#FFF' ,'#43CCF8']"
                               style="margin-right: 10px;"/>
                  开发成本评估
                </el-button>
              </el-col>
              <el-col :span="8" style="margin-bottom: 15px;">
                <el-button class="entrance-btn" text
                           @click="this.$router.push({name:'CodeProjectListView'})">
                  <source-code theme="multi-color" size="32" :fill="['#999' ,'#0D74FF' ,'#FFF' ,'#43CCF8']"
                               style="margin-right: 10px;"/>
                  源代码分析
                </el-button>
              </el-col>
              <el-col :span="8" v-if="user.role == 0" style="margin-bottom: 15px;">
                <el-button class="entrance-btn" text
                           @click="this.$router.push({name:'OpenSourceCodeListView'})">
                  <seo-folder theme="multi-color" size="32" :fill="['#999' ,'#0D74FF' ,'#FFF' ,'#43CCF8']"
                              style="margin-right: 10px;"/>
                  开源代码库管理
                </el-button>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
        <el-col :span="24" style="margin-bottom: 15px;">
          <el-card class="detail-card" shadow="always">
            <div class="card-header">
              <span class="header-title">帮助中心</span>
            </div>
            <el-row :gutter="15" style="padding:15px 15px 0px 15px;">
              <el-col :span="6" style="margin-bottom: 15px;">
                <el-button class="entrance-btn" text
                           @click="this.$router.push({name:'BuildingView'})">
                  系统介绍
                </el-button>
              </el-col>
              <el-col :span="6" style="margin-bottom: 15px;">
                <el-button class="entrance-btn" text
                           @click="this.$router.push({name:'BuildingView'})">
                  使用指南
                </el-button>
              </el-col>
              <el-col :span="6" style="margin-bottom: 15px;">
                <el-button class="entrance-btn" text
                           @click="this.$router.push({name:'StandardDocumentView'})">
                  规范与标准
                </el-button>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
        <el-col :span="24">
          <el-card class="detail-card" style="height:340px;" shadow="always">
            <div class="card-header">
              <span class="header-title">最新源码分析项目</span>
              <el-button type="primary" round plain @click="showAll(2)">查看所有项目</el-button>
            </div>
            <div style="padding: 15px;">
              <el-table :data="codeProjects" stripe header-row-style="color:black;">
                <el-table-column prop="name" label="项目名称">
                  <template #default="scope">
                    <el-link style="font-weight: bold" @click="moveTo(2,scope.row.id)">{{ scope.row.name }}</el-link>
                  </template>
                </el-table-column>
                <el-table-column prop="affiliation" label="送审单位" width="100"/>
                <el-table-column label="原创文件占比" width="125">
                  <template #default="scope">
                    <el-progress
                        :percentage="((scope.row.validFileNumber - scope.row.cloneFileNumber)/scope.row.validFileNumber *100).toFixed(2)"
                        color="rgb(63,158,255)"/>
                  </template>
                </el-table-column>
                <el-table-column label="原创代码行占比" width="125">
                  <template #default="scope">
                    <el-progress
                        :percentage="((scope.row.totalLineNumber - scope.row.cloneLineNumber)/scope.row.totalLineNumber *100).toFixed(2)"
                        color="rgb(103,194,58)"/>
                  </template>
                </el-table-column>
                <el-table-column prop="createDate" label="上传时间" width="180"/>
              </el-table>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-col>
  </el-row>
</template>

<script>
import {PaperMoney, SourceCode, SeoFolder, TrendingUp} from '@icon-park/vue-next';
import request from "@/utils/request";
import {ElNotification} from "element-plus";
import CodeProjectListView from "@/views/admin/CodeProject/CodeProjectListView";
import * as echarts from "echarts";
import {constValue} from "@/assets/js/const.js";

export default {
  name: "HelloView",
  components: {
    PaperMoney,
    SourceCode,
    SeoFolder,
    TrendingUp,
    constValue
  },
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {name: "未知用户"},
      costProjects: [],
      codeProjects: [],
      codeProjectCount: 0,
      costProjectCount: 0,
      codeProjectThisMonthCount: 0,
      costProjectThisMonthCount: 0,
      finishedCostProjectCount: 0,
      totalAmount: 0,
      totalValidFileNumber: 0,
      totalLineNumber: 0,
      costProjectAffiliationCount: [],
      codeProjectAffiliationCount: [],
      costProjectAreaCount: [],
      codeProjectAreaCount: [],
      costProjectCountByDay: [],
      codeProjectCountByDay: [],
    }
  },
  created() {
    this.load();
  },
  methods: {
    /**
     * @description 加载数据
     * @return {void}
     */
    load() {
      //源码分析项目数量
      request.get("code-project/count").then(res => {
        if (res.code == "0") {
          this.codeProjectCount = res.data;
        }
      })
      //成本分析项目数量
      request.get("cost-project/count").then(res => {
        if (res.code == "0") {
          this.costProjectCount = res.data;
        }
      })
      //源码分析项目本月按天统计
      request.get("code-project/thisMonthCount").then(res => {
        if (res.code == "0") {
          this.codeProjectThisMonthCount = res.data;
        }
      })
      //成本分析项目本月按天统计
      request.get("cost-project/thisMonthCount").then(res => {
        if (res.code == "0") {
          this.costProjectThisMonthCount = res.data;
        }
      })
      //成本分析项目按送审机构统计
      request.get("cost-project/countByQuery/affiliation").then(res => {
        if (res.code == "0") {
          this.costProjectAffiliationCount = res.data;
          this.drawPan(this.$refs.costAffiliationChart, this.costProjectAffiliationCount, "送审单位分布");
        }
      })
      //源码分析项目按送审机构统计
      request.get("code-project/countByQuery/affiliation").then(res => {
        if (res.code == "0") {
          this.codeProjectAffiliationCount = res.data;
          this.drawPan(this.$refs.codeAffiliationChart, this.codeProjectAffiliationCount, "送审单位分布");
        }
      })
      //成本分析项目按领域统计
      request.get("cost-project/countByQuery/area").then(res => {
        if (res.code == "0") {
          this.costProjectAreaCount = res.data;
          this.costProjectAreaCount.forEach((value, index, array) => {
            array[index].name = constValue.areas[value.name];
          })
          this.drawPan(this.$refs.costAreaChart, this.costProjectAreaCount, "行业领域分布");
        }
      })
      //已审查成本分析项目统计
      request.get("cost-project/countByStatus/2").then(res => {
        if (res.code == "0") {
          this.finishedCostProjectCount = res.data;
        }
      })
      //成本分析项目总审查金额统计
      request.get("cost-project/getTotalAmount").then(res => {
        if (res.code == "0") {
          this.totalAmount = res.data == null ? 0 : res.data;
        }
      })
      //总代码文件数统计
      request.get("code-project/getTotalValidFileNumber").then(res => {
        if (res.code == "0") {
          this.totalValidFileNumber = res.data == null ? 0 : res.data;
        }
      })
      //总代码行数统计
      request.get("code-project/getTotalLineNumber").then(res => {
        if (res.code == "0") {
          this.totalLineNumber = res.data == null ? 0 : res.data;
        }
      })
      //按日期统计项目数
      request.get("cost-project/countByDay").then(res => {
        if (res.code == "0") {
          this.costProjectCountByDay = res.data;
          request.get("code-project/countByDay").then(res => {
            if (res.code == "0") {
              this.codeProjectCountByDay = res.data;
              this.drawCountByDay();
            }
          })
        }
      })
     //获取最新成本分析项目
      request.get("cost-project", {
        params: {
          pageNum: 1,
          pageSize: 5
        }
      }).then(res => {
        if (res.code == "0") {
          this.costProjects = res.data.records;
          this.costProjects.reverse();
          this.drawLine(this.$refs.amountChart, this.costProjects.map(e => e.name), this.costProjects.map(e => e.totalAmount), "#0D74FF");
        }
      })
      //获取最新代码分析项目
      request.get("code-project", {
        params: {
          pageNum: 1,
          pageSize: 5
        }
      }).then(res => {
        if (res.code == "0") {
          this.codeProjects = res.data.records;
          this.codeProjects.reverse();
          this.drawCodeNumberLine();
          this.drawCloneLine();
        }
      })
    },
    /**
      * @description 绘制按日期统计项目数折线图
      * @return {void}
      */
    drawCountByDay() {
      const chart = this.$refs.projectCountChart;
      if (chart) {
        var days = Array.from(new Set(this.codeProjectCountByDay.map(e => e.day).concat(this.costProjectCountByDay.map(e => e.day)))).sort(),
            data = new Array();
        for (var day of days) {
          var codeProjectCount = this.codeProjectCountByDay.filter(e => e.day == day)[0],
              costProjectCount = this.costProjectCountByDay.filter(e => e.day == day)[0];
          data.push({
            day: day,
            codeCount: codeProjectCount ? Number(codeProjectCount.count) : 0,
            costCount: costProjectCount ? Number(costProjectCount.count) : 0
          })
        }
        const myChart = echarts.init(chart);
        const option = {
          grid: {
            left: '0%',
            right: '0%',
            bottom: '0%',
            containLabel: true
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          xAxis: {
            type: 'category',
            data: data.map(e => e.day).slice(-10)
          },
          yAxis: {
            type: 'value',
            minInterval: 1,
          },
          legend: {
            data: ['成本分析项目', '源码分析项目'],
          },
          series: [
            {
              name: '成本分析项目',
              data: data.map(e => e.costCount).slice(-10),
              type: 'bar',
              barWidth: '25px',
              label: {
                show: true,
                position: "top",
              },
              itemStyle: {
                color: "#0D74FF",
              },
              areaStyle: {}
            },
            {
              name: '源码分析项目',
              data: data.map(e => e.codeCount).slice(-10),
              type: 'bar',
              barWidth: '25px',
              label: {
                show: true,
                position: "top",
              },
              itemStyle: {
                color: "#67C23A",
              },
              areaStyle: {}
            }
          ]
        };
        myChart.setOption(option);
        window.addEventListener("resize", function () {
          myChart.resize();
        })
      }
      this.$on('hook:destroyed', () => {
        window.removeEventListener("resize", function () {
          myChart.resize();
        });
      })
    },
    /**
     * @description 绘制代码克隆分析折线图
     * @return {void}
     */
    drawCloneLine() {
      const chart = this.$refs.codeCloneChart;
      if (chart) {
        const myChart = echarts.init(chart);
        const option = {
          title: {
            text: "代码克隆分析",
            left: 'center'
          },
          grid: {
            left: '0%',
            right: '0%',
            top: '30%',
            bottom: '0%',
            containLabel: true
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          xAxis: {
            type: 'category',
            data: this.codeProjects.filter(e => e.status == 1).map(e => e.name),
            axisLabel: {
              inside: false,
              textStyle: {
                color: '#000',
                fontSize: 8,
                itemSize: ''

              }
            }
          },
          yAxis: {
            type: 'value',
          },
          legend: {
            data: ["原创代码文件占比", "原创代码行占比"],
            top: '10%',
          },
          series: [
            {
              name: '原创代码文件占比',
              data: this.codeProjects.filter(e => e.status == 1).map(e => ((e.validFileNumber - e.cloneFileNumber) / e.validFileNumber * 100).toFixed(2)),
              type: 'line',
              barWidth: '30%',
              label: {
                show: true,
                position: "top",
              },
              itemStyle: {
                color: "#0D74FF",
              },
            },
            {
              name: '原创代码行占比',
              data: this.codeProjects.filter(e => e.status == 1).map(e => ((e.totalLineNumber - e.cloneLineNumber) / e.totalLineNumber * 100).toFixed(2)),
              type: 'line',
              barWidth: '30%',
              label: {
                show: true,
                position: "top",
              },
              itemStyle: {
                color: "#67C23A",
              },
            }
          ]
        };
        myChart.setOption(option);
        window.addEventListener("resize", function () {
          myChart.resize();
        })
      }
    },
    /**
      * @description 绘制折线图
      * @return {void}
      */
    drawLine(chart, x_data, data, color) {
      if (chart) {
        const myChart = echarts.init(chart);
        const option = {
          tooltip: {
            trigger: 'item',
          },
          grid: {
            left: '0%',
            right: '0%',
            bottom: '50%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: x_data,
            show: false, // 不显示坐标轴线、坐标轴刻度线和坐标轴上的文字
            axisTick: {
              show: false // 不显示坐标轴刻度线
            },
            axisLine: {
              show: false, // 不显示坐标轴线
            },
            axisLabel: {
              show: false, // 不显示坐标轴上的文字
            },
            splitLine: {
              show: false // 不显示网格线
            }
          },
          yAxis: {
            type: 'value',
            show: false, // 不显示坐标轴线、坐标轴刻度线和坐标轴上的文字
            axisTick: {
              show: false // 不显示坐标轴刻度线
            },
            axisLine: {
              show: false, // 不显示坐标轴线
            },
            axisLabel: {
              show: false, // 不显示坐标轴上的文字
            },
            splitLine: {
              show: false // 不显示网格线
            },
          },
          series: [
            {
              data: data,
              type: 'line',
              areaStyle: {},
              smooth: true,
              itemStyle: {
                color: color,
              }
            }
          ]
        };
        myChart.setOption(option);
        window.addEventListener("resize", function () {
          myChart.resize();
        })
      }

    },
    /**
     * @description 绘制代码文件数量折线图
     * @return {void}
     */
    drawCodeNumberLine() {
      const chart1 = this.$refs.fileNumberChart, chart2 = this.$refs.lineNumberChart,
          x_data = this.codeProjects.map(e => e.name), data1 = this.codeProjects.map(e => e.validFileNumber),
          data2 = this.codeProjects.map(e => e.totalLineNumber);
      this.drawLine(chart1, x_data, data1, "#67C23A");
      this.drawLine(chart2, x_data, data2, "#67C23A");
    },
    /**
     * @description 绘制饼状图
     * @return {void}
     */
    drawPan(chart, data, title) {
      if (chart) {
        let myChart = echarts.init(chart);
        const option = {
          title: {
            text: title,
            left: 'center'
          },
          grid: {
            left: '0%',
            right: '0%',
            bottom: '50%',
          },
          tooltip: {
            trigger: 'item',
          },
          legend: {
            type: 'scroll',
            top: 'bottom',
            left: 'center',
            y: 'bottom',
            textStyle: {
              color: '#000',
              fontSize: 10
            },
          },
          series: [
            {
              type: 'pie',
              radius: ['40%', '70%'],
              label: {
                normal: {
                  show: true,
                  formatter: '{d}%'
                }

              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              data: data
            }
          ]
        };
        myChart.setOption(option);
        window.addEventListener("resize", myChart.resize)
      }
    },
    /**
     * @description 格式化金额
     * @return {String}
     */
    moneyFormat(data) {
      if (!data) return '0'
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
     * @description 跳转到对应列表
     * @return {void}
     */
    showAll(flag) {
      if (flag == 1) {
        this.$router.push({name: "CostProjectListView"});
      } else {
        this.$router.push({name: "CodeProjectListView"});
      }
    },
    /**
     * @description 跳转到对应项目
     * @return {void}
     */
    moveTo(flag, pid) {
      if (flag == 1) {
        this.$router.push("/cost-project/" + pid);
      } else {
        this.$router.push("/code-project/" + pid);
      }
    },
  }
}
</script>

<style scoped>
.statistic-card {
  border-radius: 15px;
  height: 120px;
  cursor: pointer
}

.detail-card {
  border-radius: 15px;
}

.detail-card .el-card {
  cursor: pointer;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-weight: bold;
  font-size: 18px;
  padding-left: 15px;
  border-left: 5px solid rgb(19, 119, 255);
}

.entrance-btn {
  width: 100%;
  height: 64px;
  font-size: 18px;
  padding: 10px;
  border-radius: 10px;
  margin-right: 10px;
}
</style>