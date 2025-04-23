<!--
* @FileDescription: 计算成本分析项目总陈本
-->
<template>
  <div style="padding: 15px;box-shadow: 0px 1px 12px 1px lightgray;width: 100%;background: white;top: 0;z-index:100;">
    <el-page-header @back="goBack">
      <template #content>
        <div style="display: flex;justify-content: space-between;width: 100%">
          <el-skeleton :loading="projectLoading" animated>
            <template #template>
              <el-skeleton-item variant="text" style="width:300px;height:25px;"/>
            </template>
            <template #default>
              <label style="font-weight: bold;font-size: 25px"> {{ this.projectData.name }}</label>
            </template>
          </el-skeleton>
          <div>
            <el-button @click="settingDialogVisible = true" style="width:100px;" :disabled="loading" round>
              成本评估配置
            </el-button>
          </div>
        </div>
      </template>
      <template #extra>
        123
      </template>
    </el-page-header>
  </div>
  <el-row style="margin-top:15px;padding:0 15px;">
    <el-col :span="24" style="padding:0 100px;">
      <el-card shadow="always" style="min-width:1200px;margin:100px 0;border-radius: 15px;margin-top:15px;padding:5px;">
        <el-steps :active="activeStep" finish-status="success"
                  style="background-color: transparent;margin-bottom: 15px;padding:0px 100px ;" align-center>
          <el-step title="第一步：规模与工作量估算">
            <template #icon>
              <find theme="outline" size="28" fill="#0d74ff" v-if="activeStep==0"/>
            </template>
            <template #description>
              智能分析上传需求文档，提取功能点并估算规模与工作量
            </template>
          </el-step>
          <el-step title="第二步：工作量调整">
            <template #icon>
              <equalizer theme="outline" size="28" fill="#0d74ff" v-if="activeStep==1"/>
              <equalizer theme="outline" size="24" fill="#aaaaaa" v-else/>
            </template>
            <template #description>
              配置调整因子，调整工作量
            </template>
          </el-step>
          <el-step title="第三步：计算软件开发成本">
            <template #icon>
              <calculator theme="outline" size="28" fill="#0d74ff" v-if="activeStep==2"/>
              <calculator theme="outline" size="24" fill="#aaaaaa" v-else/>
            </template>
            <template #description>
              分配工时与设置人时费率
            </template>
          </el-step>
          <el-step title="第四步：审核与确认">
            <template #icon>
              <check-correct theme="outline" size="28" fill="#0d74ff" v-if="activeStep==3"/>
              <check-correct theme="outline" size="24" fill="#aaaaaa" v-else/>
            </template>
            <template #description>
              审核规模、工作量与软件开发成本，确认软件开发总成本
            </template>
          </el-step>
        </el-steps>
        <el-divider></el-divider>
        <template v-if="activeStep == 0">
          <!--                <span class="header_label">查看功能点列表</span>-->
          <el-descriptions :column="6" direction="vertical">
            <el-descriptions-item align="center" label="功能点数" :width="400">
                            <span class="desc_content">
                                {{ functionCount }}
                            </span>
            </el-descriptions-item>
            <el-descriptions-item align="center" label="未调整软件规模US (FP)" :width="400">
                            <span class="desc_content">
                                {{ UnadaptedScale }}
                            </span>
            </el-descriptions-item>
            <el-descriptions-item align="center" label="规模变更因子CF" :width="400">
                            <span class="desc_content">
                                {{ this.projectData.scaleFactor ? this.projectData.scaleFactor : 0 }}
                            </span>
            </el-descriptions-item>
            <el-descriptions-item align="center" label="调整后软件规模S=US×CF FP" :width="400">
                            <span class="desc_content">
                                {{ (UnadaptedScale * this.projectData.scaleFactor).toFixed(2) }}
                            </span>
            </el-descriptions-item>
            <el-descriptions-item align="center" label="行业生产率PDR (p.h/FP)" :width="400">
                            <span class="desc_content">
                                {{ constValue.Area2PDR[this.projectData.area] }}
                            </span>
            </el-descriptions-item>
            <el-descriptions-item align="center" label="未调整工作量UE=PDR×S (p.h.)" :width="400">
                            <span class="desc_content">
                                {{ UE }}
                            </span>
            </el-descriptions-item>
          </el-descriptions>
          <div style="margin-top:15px;margin-bottom: 15px;display: flex;justify-content: space-between;">
            <div>
              <el-button type="primary" @click="this.functionDialogVisible = true" style="width:100px;"
                         :disabled="loading">
                <el-icon>
                  <Plus/>
                </el-icon>&nbsp;新增功能点
              </el-button>
              <el-button @click="showDocumentDialog" style="width:100px;" :disabled="loading">查看需求文档</el-button>
            </div>
            <div>
              <label style="margin-right: 5px;font-weight: bold;">功能点类型:</label>
              <el-checkbox v-for="idx in 5" v-model="functionFilter[idx-1]" :label="constValue.function_types[idx-1]"
                           border style="margin-right:5px;"/>
            </div>
          </div>
          <el-auto-resizer>
            <template #default="{ height, width }">
              <el-table-v2
                  :cache="2"
                  :width="width"
                  :height="800"
                  :columns="this.columns"
                  :data="filterFunctions"
                  :h-scrollbar-size="1"
                  fixed
              />
            </template>
          </el-auto-resizer>
          <div style="text-align: center;padding-top:15px;">
            <el-button type="primary" @click="activeStep++" :disabled="loading">下一步</el-button>
          </div>
        </template>
        <template v-if="activeStep == 1">
          <el-descriptions direction="vertical">
            <el-descriptions-item align="center" label="未调整工作量UE (p.h.)" :width="400">
                            <span class="desc_content">
                                {{ UE }}
                            </span>
            </el-descriptions-item>
            <el-descriptions-item align="center" label="工作量调整因子a" :min-width="400">
                            <span class="desc_content">
                                {{ totalFactor }}
                            </span>
            </el-descriptions-item>
            <el-descriptions-item align="center" label="调整后工作量AE (p.h.)" :width="400">
                            <span class="desc_content">
                                {{ AE }}
                            </span>
            </el-descriptions-item>
          </el-descriptions>
          <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="210px" label-position="left" status-icon
                   inline-message style="padding:15px;max-width: 1200px;">
            <div class="form-label">选择配置方式</div>
            <el-form-item label="配置方式">
              <el-radio-group v-model="projectData.factorRadio" size="default">
                <el-radio-button :label="0">手动输入</el-radio-button>
                <el-radio-button :label="1">查表配置</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <div class="form-label">配置调整因子</div>
            <template v-if="projectData.factorRadio==0">
              <el-form-item label="应用类型调整因子">
                <el-input v-model="projectData.appFactor" placeholder="请输入" size="large" style="width: 100%;"
                          @input="limitFloat($event, 'appFactor')"/>
              </el-form-item>
              <el-form-item label="非功能性调整因子">
                <el-input v-model="projectData.nonFunctionFactor" placeholder="请输入" size="large" style="width: 100%;"
                          @input="limitFloat($event, 'nonFunctionFactor')"/>
              </el-form-item>
              <el-form-item label="软件完整性级别调整因子">
                <el-input v-model="projectData.integrityFactor" placeholder="请输入" size="large" style="width: 100%;"
                          @input="limitFloat($event, 'integrityFactor')"/>
              </el-form-item>
              <el-form-item label="开发语言调整因子">
                <el-input v-model="projectData.platformFactor" placeholder="请输入" size="large" style="width: 100%;"
                          @input="limitFloat($event, 'platformFactor')"/>
              </el-form-item>
              <el-form-item label="开发团队背景调整因子">
                <el-input v-model="projectData.backgroundFactor" placeholder="请输入" size="large" style="width: 100%;"
                          @input="limitFloat($event, 'backgroundFactor')"/>
              </el-form-item>
            </template>
            <template v-else>
              <el-alert title="参考T/BSCEA 002-2019《软件造价评估实施规程》和CSBMK-202210《2022年中国软件行业基准数据》"
                        type="success" :closable="false" style="margin-bottom: 15px;"/>
              <el-form-item label="应用类型" prop="appFactorCate">
                <el-select v-model="projectData.appFactorCate" placeholder="请选择" size="large" style="width: 100%;"
                           filterable>
                  <el-option v-for="(item,idx) in constValue.factorCategories['app']" :label="item" :value="idx"/>
                </el-select>
              </el-form-item>
              <el-form-item label="完整性级别" prop="integrityFactorCate">
                <el-select v-model="projectData.integrityFactorCate" placeholder="请选择" size="large"
                           style="width: 100%;"
                           filterable>
                  <el-option v-for="(item,idx) in constValue.factorCategories['integrity']" :label="item" :value="idx"/>
                </el-select>
              </el-form-item>
              <el-form-item label="分布式处理" prop="distributedFactorCate">
                <el-select v-model="projectData.distributedFactorCate" placeholder="请选择" size="large"
                           style="width: 100%;" filterable>
                  <el-option v-for="(item,idx) in  constValue.factorCategories['distributed']" :label="item"
                             :value="idx"/>
                </el-select>
              </el-form-item>
              <el-form-item label="性能" prop="performanceFactorCate">
                <el-select v-model="projectData.performanceFactorCate" placeholder="请选择" size="large"
                           style="width: 100%;" filterable>
                  <el-option v-for="(item,idx) in constValue.factorCategories['performance']" :label="item"
                             :value="idx"/>
                </el-select>
              </el-form-item>
              <el-form-item label="可靠性" prop="reliabilityFactorCate">
                <el-select v-model="projectData.reliabilityFactorCate" placeholder="请选择" size="large"
                           style="width: 100%;" filterable>
                  <el-option v-for="(item,idx) in constValue.factorCategories['reliability']" :label="item"
                             :value="idx"/>
                </el-select>
              </el-form-item>
              <el-form-item label="多重站点" prop="multisiteFactorCate">
                <el-select v-model="projectData.multisiteFactorCate" placeholder="请选择" size="large"
                           style="width: 100%;"
                           filterable>
                  <el-option v-for="(item,idx) in constValue.factorCategories['multisite']" :label="item" :value="idx"/>
                </el-select>
              </el-form-item>
              <el-form-item label="开发平台" prop="platformFactorCate">
                <el-select v-model="projectData.platformFactorCate" placeholder="请选择" size="large"
                           style="width: 100%;"
                           filterable>
                  <el-option v-for="(item,idx) in constValue.factorCategories['platform']" :label="item" :value="idx"/>
                </el-select>
              </el-form-item>
              <el-form-item label="团队开发背景" prop="backgroundFactorCate">
                <el-select v-model="projectData.backgroundFactorCate" placeholder="请选择" size="large"
                           style="width: 100%;"
                           filterable>
                  <el-option v-for="(item,idx) in constValue.factorCategories['background']" :label="item"
                             :value="idx"/>
                </el-select>
              </el-form-item>
            </template>
          </el-form>
          <div style="text-align: center;padding-top:15px;">
            <el-button @click="activeStep--">上一步</el-button>
            <el-button type="primary" @click="activeStep++">下一步</el-button>
          </div>
        </template>
        <template v-if="activeStep == 2">
          <template v-if="this.projectData.costRadio==1">
            <el-descriptions direction="vertical">
              <el-descriptions-item align="center" label="调整后工作量AE(p.h.)" :width="400">
                            <span class="desc_content">
                                {{ AE }}
                            </span>
              </el-descriptions-item>
              <el-descriptions-item align="center" label="已分配工作量(%)" :min-width="400">
                <el-progress
                    :text-inside="true"
                    :stroke-width="16"
                    :percentage="phRatioCount"
                    status="success"
                />
              </el-descriptions-item>
              <el-descriptions-item align="center" label="软件开发成本(元)" :width="400">
                            <span class="desc_content">
                                {{ moneyFormat(labourCost) }}
                            </span>
              </el-descriptions-item>
            </el-descriptions>
            <div style="margin: 15px;">
              <el-button type="primary"
                         @click="this.projectData.effortDistribution.push({name:'开发人员',ratio:0,labourCostRate:0})"
                         style="width:160px;">
                <el-icon>
                  <Plus/>
                </el-icon>&nbsp;新增开发人员类型
              </el-button>
              <el-button @click="this.costTabledialogVisible = true">
                <el-icon>
                  <Document/>
                </el-icon>&nbsp;查看软件开发基准人月费率
              </el-button>
            </div>
            <el-table :data="this.projectData.effortDistribution" :height="getHeight-400" size="small"
                      header-row-style="color:black" @change="validatePHDistribution">
              <el-table-column prop="name" label="开发人员类型" sortable>
                <template #default="scope">
                  <el-input v-model="scope.row.name"/>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="工作量占比(%)" sortable>
                <template #default="scope">
                  <el-slider v-model="scope.row.ratio" show-input
                             @change="scope.row.ratio = phRatioCount>100?100- phRatioCount+scope.row.ratio :scope.row.ratio"
                             @input="scope.row.ratio = phRatioCount>100?100- phRatioCount+scope.row.ratio :scope.row.ratio"/>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="工作量E (p.h.)" sortable>
                <template #default="scope">
                  {{ (scope.row.ratio / 100.0 * AE).toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="name" label="人时费率F (元/p.h.)" sortable>
                <template #default="scope">
                  <el-input type="number"
                            @input="scope.row.labourCostRate = scope.row.labourCostRate>10000?10000:scope.row.labourCostRate"
                            v-model.number="scope.row.labourCostRate" :min="1"/>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="开发成本E×F (元)" sortable>
                <template #default="scope">
                  {{ moneyFormat(scope.row.ratio / 100.0 * AE * scope.row.labourCostRate) }}
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="操作">
                <template #default="scope">
                  <el-popconfirm title="确认删除？"
                                 @confirm="this.projectData.effortDistribution.splice(scope.$index,1)">
                    <template #reference>
                      <el-button type="danger" size="small">
                        <el-icon>
                          <Minus/>
                        </el-icon>&nbsp;移除
                      </el-button>
                    </template>
                  </el-popconfirm>
                </template>
              </el-table-column>
            </el-table>
            <div style="text-align: center;padding-top:15px;">
              <el-button @click="activeStep--">上一步</el-button>
              <el-button @click="projectData.costRadio = 0">切换至"按平均人时费率计算"</el-button>
              <el-button type="primary" @click="activeStep++" :disabled="validatePHDistribution">
                <template v-if="phRatioCount!=100">尚有未分配工时</template>
                <template v-if="phRatioCount==100&&validatePHDistribution">请检查开发人员信息</template>
                <template v-if="!validatePHDistribution">下一步</template>
              </el-button>
            </div>
          </template>
          <template v-else>
            <el-descriptions :column="2" direction="vertical">
              <el-descriptions-item align="center" label="调整后工作量AE(p.h.) " :width="400">
                            <span class="desc_content">
                                {{ AE }}
                            </span>
              </el-descriptions-item>
              <el-descriptions-item align="center" :width="400">
                <template #label>
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
                </template>
                <span class="desc_content">
                                    {{ moneyFormat(labourCost) }}
                                </span>
              </el-descriptions-item>
            </el-descriptions>
            <!--                        <el-button @click="this.costTabledialogVisible = true" ><el-icon><Document/></el-icon>&nbsp;查看软件开发基准人月费率</el-button>-->
            <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="210px" label-position="left"
                     status-icon inline-message style="padding:15px;min-height: 720px">
              <div class="form-label">按平均人时费率计算</div>
              <el-form-item label="计算方式">
                <el-radio-group v-model="projectData.rateRadio" size="default">
                  <el-radio :label="0" border>按城市平均人时费率算</el-radio>
                  <el-radio :label="1" border>手动输入</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="平均人时费率(元/p.h.)">
                <el-select v-if="projectData.rateRadio==0" v-model="city" placeholder="请选择" size="large"
                           style="width: 300px;margin-right: 5px;" filterable>
                  <el-option label="请选择城市" :value="0"></el-option>
                  <el-option v-for="(item,idx) in constValue.CostRateTable"
                             :label="item.city+' ('+(item.rate/174.0).toFixed(2)+' 元/p.h.)'" :value="idx+1"/>
                </el-select>
                <el-input v-else v-model="projectData.labourCostRate"
                          @input="projectData.labourCostRate = projectData.labourCostRate>10000?10000:projectData.labourCostRate"
                          placeholder="请输入平均人时费率" size="large" style="width: 600px;"/>
              </el-form-item>
            </el-form>
            <div style="text-align: center;padding-top:15px;">
              <el-button @click="activeStep--">上一步</el-button>
              <el-button @click="projectData.costRadio = 1">切换至"按开发人员计算"</el-button>
              <el-button type="primary" @click="activeStep++"
                         :disabled="projectData.labourCostRate==0||(projectData.rateRadio==0&&city==0)">下一步
              </el-button>
            </div>
          </template>
        </template>
        <template v-if="activeStep == 3">
          <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="320px" label-position="left"
                   style="min-height: calc(100vh - 274px);padding:25px;" hide-required-asterisk>
            <div class="form-label">软件规模</div>
            <el-form-item label="未调整规模 US">
              {{ UnadaptedScale }} (FP)
            </el-form-item>
            <el-form-item label="规模变更因子 CF">
              {{ this.projectData.scaleFactor }}
            </el-form-item>
            <el-form-item label="调整后规模 S=US×CF">
              {{ UE }} (FP)
            </el-form-item>
            <div class="form-label">软件工作量</div>
            <el-form-item label="行业基准生产率 PDR">
              {{ UE }} (p.h./FP)
            </el-form-item>
            <el-form-item label="未调整工作量 UE=PDR×S">
              {{ UE }} (p.h.)
            </el-form-item>
            <el-form-item label="工作量调整因子 a">
              {{ totalFactor }}
            </el-form-item>
            <el-form-item label="调整后工作量 AE =UE×a">
              {{ AE }} (p.h.)
            </el-form-item>
            <div class="form-label">软件开发成本</div>
            <el-form-item>
              <template #label>
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
              </template>
              {{ moneyFormat(labourCost) }}
            </el-form-item>
            <el-form-item>
              <template #label>
                <el-popover
                    placement="top-start"
                    :width="220"
                    trigger="hover"
                    content="直接非人力成本"
                >
                  <template #reference>
                    其它成本(元)
                  </template>
                </el-popover>
              </template>
              <el-input v-model="totalNonlabourCost" size="large" style="width: 600px;"/>
            </el-form-item>
            <el-form-item label="软件开发总成本(元)">
              {{ moneyFormat(totalCost) }}
            </el-form-item>
          </el-form>

          <div style="text-align: center;padding-top:15px;">
            <el-button @click="activeStep--">上一步</el-button>
            <el-button type="primary" @click="saveProject(function(){})">暂存</el-button>
            <el-button type="primary" @click="saveAndBack">保存并返回</el-button>
          </div>
        </template>
      </el-card>
    </el-col>
  </el-row>

  <el-dialog
      v-model="settingDialogVisible"
      title="成本评估配置"
      width="800px"
      top="5vh"
  >
    <el-form label-width="200px" style="padding:20px;" label-position="center">
      <el-form-item label="规模估算阶段" prop="scaleFactor">
        <!--              <el-select v-model="form.scaleFactor" style="width: 400px;">-->
        <!--                <el-option v-for="scaleFactor in scaleFactors" :label="scaleFactor.key" :value="scaleFactor.value" />-->
        <!--              </el-select>-->
        <el-radio-group class="vertical-radio-group" v-model="projectData.scaleFactor" size="default"
                        style="width: 100%">
          <el-radio v-for="scaleFactor in constValue.scaleFactors" :label="scaleFactor.value" border>
            <div class="radio-title">
              <div>{{ scaleFactor.key }}</div>
              <el-icon v-if="projectData.scaleFactor==scaleFactor.value" style="font-size: 20px;padding-top: 6px;">
                <Check/>
              </el-icon>
            </div>
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="所属行业(行业生产率)" prop="area">
        <!--              <el-select v-model="form.area" placeholder="请选择" size="large" style="width: 600px;" filterable><el-option v-for="(item,idx) in constValue.areas" :label="item+' ('+constValue.Area2PDR[idx]+' p.h./FP)'" :value="idx"/></el-select>-->
        <el-radio-group class="vertical-radio-group" v-model="projectData.area" size="default" style="width: 100%">
          <el-radio v-for="(item,idx) in constValue.areas" :label="idx" border>
            <div class="radio-title">
              <div>{{ item }}-{{ constValue.Area2PDR[idx] + ' p.h./FP' }}</div>
              <el-icon v-if="projectData.area==idx" style="font-size: 20px;padding-top: 6px;">
                <Check/>
              </el-icon>
            </div>
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="软件规模估算方式" prop="scaleRadio">
        <el-radio-group class="vertical-radio-group-detail" v-model="projectData.scaleRadio" size="default"
                        style="width: 100%">
          <el-radio :label="0" border>
            <div class="radio-title">
              <div>预估功能点法</div>
              <el-icon v-if="projectData.scaleRadio==0">
                <Check/>
              </el-icon>
            </div>
            <div class="radio-content">
              当需求文档中功能点不明确（如项目早期）时，可使用预估功能点法，只识别内部逻辑文件ILF和外部接口文件EIF并计算功能点数UFP，公式为：UFP=35×ILF+15×EIF
            </div>
          </el-radio>
          <el-radio :label="1" border>
            <div class="radio-title">
              <div>估算功能点法</div>
              <el-icon v-if="projectData.scaleRadio==1">
                <Check/>
              </el-icon>
            </div>
            <div class="radio-content">
              当需求文档中功能点清晰时，可使用估算功能点法，统计所有功能点类型并计算功能点数UFP，公式为：UFP=7×ILF+5×EIL+4×EI+5×EO+4×EQ
            </div>
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="软件开发成本计算方式" prop="costRadio">
        <el-radio-group class="vertical-radio-group-detail" v-model="projectData.costRadio" size="default"
                        style="width: 100%">
          <el-radio :label="0" border>
            <div class="radio-title">
              <div>按平均人时费率计算</div>
              <el-icon v-if="projectData.costRadio==0">
                <Check/>
              </el-icon>
            </div>
            <div class="radio-content">
              按照软件行业的基准数据或送审单位的参考数据确定软件开发平均人时费率，直接计算软件开发成本
            </div>
          </el-radio>
          <el-radio :label="1" border>
            <div class="radio-title">
              <div>按开发人员计算</div>
              <el-icon v-if="projectData.costRadio==1">
                <Check/>
              </el-icon>
            </div>
            <div class="radio-content">
              按照开发人员岗位分配调整后工作量，并分别确定软件开发平均人时费率，计算软件开发成本
            </div>
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="this.settingDialogVisible = false">关闭</el-button>
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
            <el-button @click="this.documentDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="functionDialogVisible"
      title="新增功能点"
      width="50%"
      top="25vh"
  >
    <el-form ref="functionRuleFormRef" :model="form" :rules="rules" label-width="210px" label-position="left"
             status-icon inline-message>
      <el-form-item label="功能点计数项" prop="name">
        <el-input v-model="form.name" style="width: 400px;" maxlength="20" show-word-limit/>
      </el-form-item>
      <el-form-item label="功能点类型" prop="type">
        <el-select v-model="form.type" placeholder="请选择" size="large" style="width: 400px;" filterable>
          <el-option v-for="(item,idx) in constValue.function_types" :label="item" :value="idx"/>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
              <span class="dialog-footer">
                <el-button @click="this.functionDialogVisible = false">关闭</el-button>
                  <el-button @click="addFunction">新增</el-button>
              </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="functionContentDialogVisible"
      title="查看功能点文本"
      width="500px"
      top="25vh"
  >
    <div v-html="html"></div>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="this.functionContentDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="costTabledialogVisible"
      title="典型城市软件开发人月费率基准数据明细"
      width="75%"
      top="5vh"
  >
    <label>城市名称</label>
    <el-input v-model="keyword" placeholder="请输入城市名称" clearable style="width: 200px;margin-right: 15px;"/>
    <el-table :data="this.filterCostRateTable" :height="600" size="small" header-row-style="color:black">
      <el-table-column prop="city" label="城市名称" sortable/>
      <el-table-column label="基准人月费率（元/p.m.）" sortable>
        <template #default="scope">
          {{ scope.row.rate }}
        </template>
      </el-table-column>
      <el-table-column label="基准人时费率（元/p.h.）" sortable>
        <template #default="scope">
          {{ (scope.row.rate / 174.0).toFixed(2) }}
          <el-button @click="copy((scope.row.rate/174.0).toFixed(2))" link>
            <el-icon>
              <CopyDocument/>
            </el-icon>
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="cate" label="城市类别" sortable/>
    </el-table>
    <el-alert
        title="CSBMK-202210《2022年中国软件行业基准数据》说明：表中人月费率代表该地区统计数据中位数（P50）,一人月以 21.75 天计。费用包含软件开发的直接人力成本、间接人力成本、间接非人力成本及合理利润（含税），但不包括直接非人力成本。其中 A 类城市基准人月费率超过 3.0 万元，包括北京、上海、深圳，平均基准人月费率为 3.16 万元；B 类城市基准人月费率超过 2.5 万元，如广州、天津、南京、厦门等，平均基准人月费率为 2.71 万元；C 类城市基准人月费率超过 2.2 万元，如重庆、哈尔滨、济南、南宁等，平均基准人月费率为 2.38 万元；其他为 D 类城市，如石家庄、呼和浩特、兰州、西宁等，平均基准人月费率为 2.05 万元。"
        type="info" show-icon :closable="false"/>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="this.costTabledialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>


</template>

<script>
import {h} from "vue";
import request from "@/utils/request";
import {
  ElInput,
  ElSelect,
  ElOption,
  ElButton,
  ElNotification,
  TableV2FixedDir,
  ElPopconfirm,
  ElIcon
} from 'element-plus'
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
  Document,
  CopyDocument
} from '@element-plus/icons-vue'

var docxx = require("docx-preview");
import {constValue} from "@/assets/js/const.js"
import {ElMessageBox} from 'element-plus'
import {Calculator, Equalizer, Find, CheckCorrect} from '@icon-park/vue-next';

export default {
  name: "CalculateCostView",
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
    Document,
    CopyDocument,
    constValue,
    ElMessageBox,
    Calculator,
    Equalizer,
    Find,
    CheckCorrect
  },
  data() {
    return {
      constValue: constValue,
      functionContentDialogVisible: false,
      documentDialogVisible: false,
      costTabledialogVisible: false,
      settingDialogVisible: false,
      functionDialogVisible: false,
      form: {},
      functionFilter: [false, false, false, false, false],
      html: "",
      columns: [
        {
          key: 'index',
          width: 50,
          title: '序号',
          cellRenderer: ({rowIndex}) => `${rowIndex + 1}`,
          align: 'center',
        },
        {
          key: 'name',
          dataKey: 'name',
          title: '功能项',
          width: 400,
          cellRenderer: ({rowData, column}) =>
              rowData.isEdit ?
                  h(
                      ElInput, {
                        modelValue: rowData[column.dataKey],
                        'onUpdate:modelValue': value => rowData[column.dataKey] = value
                      }
                  ) : rowData[column.dataKey]
        },
        {
          key: 'type',
          dataKey: 'type',
          title: '功能项类型',
          width: 300,
          align: 'center',
          cellRenderer: ({rowData, column}) =>
              rowData.isEdit ?
                  h(
                      ElSelect,
                      {
                        modelValue: rowData[column.dataKey],
                        'onUpdate:modelValue': value => rowData[column.dataKey] = value
                      },
                      [
                        h(ElOption, {label: 'ILF', value: 0}),
                        h(ElOption, {label: 'EIF', value: 1}),
                        h(ElOption, {label: 'EI', value: 2}),
                        h(ElOption, {label: 'EO', value: 3}),
                        h(ElOption, {label: 'EQ', value: 4}),
                      ]
                  ) : this.constValue.function_types[rowData[column.dataKey]]
        },
        {
          key: 'scale',
          dataKey: 'type',
          title: '功能点规模',
          width: 300,
          align: 'center',
          cellRenderer: ({rowData, column}) =>
              (this.constValue.scales[this.projectData.scaleRadio][rowData[column.dataKey]])

        },
        {
          key: 'content',
          dataKey: 'id',
          title: '操作',
          width: 300,
          fixed: TableV2FixedDir.RIGHT,
          cellRenderer: ({rowData, column}) =>
              [
                h(
                    ElButton,
                    {
                      onClick: $event => {
                        if (rowData.isEdit) {
                          rowData.isChanged = true;
                        }
                        rowData.isEdit = !rowData.isEdit
                      },
                      text: true
                    },
                    [rowData.isEdit ? "保存" : "编辑"]
                ),
                h(
                    ElButton,
                    {
                      onClick: $event => this.showFunctionContentDialog(rowData[column.dataKey]),
                      text: true
                    },
                    ["查看文本"]
                ),
                h(
                    ElPopconfirm,
                    {
                      title: "确认删除？",
                    },
                    {
                      reference: () => h(
                          ElButton,
                          {
                            type: 'danger',
                            text: true,
                            onClick: () => {
                              rowData.isDelete = true;
                            }
                          },
                          ["移除"]
                      )
                    }
                )
              ]

        },
      ],
      activeStep: 0,
      pid: 0,
      isEdit: false,
      keyword: "",
      totalNonlabourCost: "0.00",
      city: 0,
      functions: [],
      projectData: {
        id: 0,
        appFactor: 1.0,
        nonFunctionFactor: 1.0,
        integrityFactor: 1.0,
        platformFactor: 1.0,
        backgroundFactor: 1.0,
        totalNonlabourCost: 0,
        effortDistribution: [],
        rateRadio: 0,
        city: 0,
      },
      projectLoading: true,
      loading: true,
      documentLoading: true,
      rules: {
        name: [
          {required: true, min: 1, max: 10, message: "请重新输入", trigger: "blur"}
        ],
        type: [
          {required: true, message: "请选择", trigger: "change"}
        ],
        appFactor: [
          {required: true, message: "请输入影响因子", trigger: "blur"}
        ],
        nonFunctionFactor: [
          {required: true, message: "请输入影响因子", trigger: "blur"}
        ],
        integrityFactor: [
          {required: true, message: "请输入影响因子", trigger: "blur"}
        ],
        platformFactor: [
          {required: true, message: "请输入影响因子", trigger: "blur"}
        ],
        backgroundFactor: [
          {required: true, message: "请输入影响因子", trigger: "blur"}
        ],
        appFactorCate: [
          {required: true, message: "请选择一个选项", trigger: "blur"}
        ],
        integrityFactorCate: [
          {required: true, message: "请选择一个选项", trigger: "blur"}
        ],
        distributedFactorCate: [
          {required: true, message: "请选择一个选项", trigger: "blur"}
        ],
        performanceFactorCate: [
          {required: true, message: "请选择一个选项", trigger: "blur"}
        ],
        reliabilityFactorCate: [
          {required: true, message: "请选择一个选项", trigger: "blur"}
        ],
        multisiteFactorCate: [
          {required: true, message: "请选择一个选项", trigger: "blur"}
        ],
        platformFactorCate: [
          {required: true, message: "请选择一个选项", trigger: "blur"}
        ],
        backgroundFactorCate: [
          {required: true, message: "请选择一个选项", trigger: "blur"}
        ]
      }
    }
  },
  created() {
    this.pid = this.$route.params.id;
    this.loadProjectInfo();
  },
  beforeRouteLeave(to, form, next) {
    if (this.isEdit)
      ElMessageBox.confirm(
          '您可能丢失尚未保存的数据，确认离开？',
          '请注意',
          {
            confirmButtonText: '确认离开',
            cancelButtonText: '取消',
            type: 'warning',
            callback: function (action, instance) {
              if (action === 'confirm') {
                // window.removeEventListener('beforeunload', listener);
                next();
              } else
                next(false);
            }
          }
      )
    else next();
  },
  computed: {
    filterFunctions() {
      return this.functions.filter((data) => !data.isDelete & this.functionFilter[data.type]);
    },
    filterCostRateTable() {
      return this.constValue.CostRateTable.filter(
          (data) => !this.keyword || data.city.indexOf(this.keyword) != -1);
    },
    UnadaptedScale() {
      var scale = 0, that = this;
      var functions = this.functions.filter((data) => !data.isDelete);
      if (functions && functions.length > 0) {
        functions.forEach(function (item, index, arr) {
          scale += that.constValue.scales[that.projectData.scaleRadio][item.type];
        })
      }
      return scale;
    },
    UE() {
      var UE = 0;
      UE = this.UnadaptedScale * this.projectData.scaleFactor * this.constValue.Area2PDR[this.projectData.area];
      return UE.toFixed(2);
    },
    totalFactor() {
      var totalFacotor = 0.0;
      if (this.projectData.factorRadio != 0) {
        this.projectData.appFactor = this.constValue.category2Factor['app'][this.projectData.appFactorCate];
        this.projectData.integrityFactor = this.constValue.category2Factor['integrity'][this.projectData.integrityFactorCate];
        this.projectData.nonFunctionFactor = (this.projectData.distributedFactorCate + this.projectData.performanceFactorCate + this.projectData.reliabilityFactorCate + this.projectData.multisiteFactorCate - 4) * 0.025 + 1
        this.projectData.platformFactor = this.constValue.category2Factor['platform'][this.projectData.platformFactorCate];
        this.projectData.backgroundFactor = this.constValue.category2Factor['background'][this.projectData.backgroundFactorCate];
      }
      totalFacotor = this.projectData.appFactor * this.projectData.integrityFactor * this.projectData.nonFunctionFactor * this.projectData.platformFactor * this.projectData.backgroundFactor;
      return totalFacotor.toFixed(2);
    },
    AE() {
      var AE = 0;
      AE = this.UE * this.totalFactor;
      return AE.toFixed(2);
    },
    labourCost() {
      var cost = 0, AE = this.AE;
      if (this.projectData.costRadio == 1) {
        if (this.projectData.effortDistribution && this.projectData.effortDistribution.length > 0) {
          this.projectData.effortDistribution.forEach(function (item, index, arr) {
            cost += item.ratio / 100.0 * AE * item.labourCostRate;
          })
        }
      } else
        cost = AE * this.projectData.labourCostRate;
      return cost;
    },
    totalCost() {
      this.projectData.totalCost = this.labourCost + this.projectData.totalNonlabourCost;
      return this.projectData.totalCost;
    },
    functionCount() {
      if (this.functions && this.functions.length > 0) {
        var functions = this.functions.filter((data) => !data.isDelete);
        return functions.length;
      }
      return 0;
    },
    phRatioCount() {
      var cnt = 0;
      console.log(this.projectData.effortDistribution);
      if (this.projectData.effortDistribution && this.projectData.effortDistribution.length > 0) {
        this.projectData.effortDistribution.forEach(function (item, index, arr) {
          cnt += item.ratio;
        })
      }
      return cnt;
    },
    getHeight() {
      const height = document.documentElement.clientHeight;
      return height;
    },
    validatePHDistribution() {
      if (this.projectData.effortDistribution && this.projectData.effortDistribution.length > 0 && this.phRatioCount == 100 && this.labourCost > 0) {
        var cnt = 0;
        this.projectData.effortDistribution.forEach(function (item, index, arr) {
          if (item.name == null || item.name.length == 0 || item.ratio == 0 || item.labourCostRate == 0) {
            cnt++;
          }
        })
        if (cnt == 0) return false;
      }
      return true;
    }
  },
  watch: {
    totalNonlabourCost(newVal, oldVal) {
      var cost = Number(newVal.toString().replace(/[a-zA-Z]/g, '').replace(/[`~!@#$^\-&*()=|{}':;',\\\[\]\.<>\/?~！@#￥……&*（）——|{}【】'；：""'。，、？\s]/g, '').replace(/\$\s?|(,*)/g, ''));
      if (cost > 1e8)
        cost = 1e8;
      this.totalNonlabourCost = cost.toString().replace(/\B(?=(\d{4})+(?!\d))/g, ',');
      this.projectData.totalNonlabourCost = cost;
    },
    city(newVal, oldVal) {
      if (newVal > 0) {
        this.projectData.city = newVal;
        if (this.projectData.labourCostRate != (this.constValue.CostRateTable[this.projectData.city - 1].rate / 174.0).toFixed(2))
          this.projectData.labourCostRate = (this.constValue.CostRateTable[this.projectData.city - 1].rate / 174.0).toFixed(2);
      }
    },
    project: {
      handler(newVal, oldVal) {
        if (oldVal.id > 0)
          this.isEdit = true;
      },
      deep: true
    },
    functions: {
      handler(newVal, oldVal) {
        if (oldVal.length > 0)
          this.isEdit = true;
      },
      deep: true
    }
  },
  methods: {
    /**
     * @description 加载项目信息
     * @return {void}
     */
    loadProjectInfo() {
      if (this.pid == undefined || this.pid == 0) {
        this.$router.push("/cost-projects");
        return;
      }
      request.get("/cost-project/get/" + this.pid).then(res => {
        if (res.code == "0") {
          this.projectData = res.data;
          window.document.title = '计算软件开发成本-' + this.projectData.name + '-智能代码分析系统';
          if (this.projectData.effortDistribution == null || this.projectData.effortDistribution.length == 0)
            this.projectData.effortDistribution = [
              {name: "需求分析员", ratio: 20, labourCostRate: 0},
              {name: "前端工程师", ratio: 30, labourCostRate: 0},
              {name: "后端工程师", ratio: 30, labourCostRate: 0},
              {name: "测试人员", ratio: 20, labourCostRate: 0}];
          this.isEdit = false;
          this.city = this.projectData.city;
          this.functions = [];
          if (this.projectData.scaleRadio == 0)
            this.functionFilter = [true, true, false, false, false];
          else
            this.functionFilter = [true, true, true, true, true];
          this.projectLoading = false;
          this.loadFunction();
        } else {
          if (res.code == "600")
            this.$router.push("404");
          else
            this.$router.push("/hello");
        }

      })
    },
    /**
     * @description 保存项目
     * @param {function} callback 回调方法
     * @return {void}
     */
    saveProject(callback) {
      this.projectData.totalUnadaptedScale = this.UnadaptedScale;
      this.projectData.totalScale = this.UnadaptedScale * this.projectData.scaleFactor;
      this.projectData.totalEffort = this.UE;
      if (this.projectData.factorRadio != 0) {
        this.projectData.appFactor = this.constValue.category2Factor['app'][this.projectData.appFactorCate];
        this.projectData.integrityFactor = this.constValue.category2Factor['integrity'][this.projectData.integrityFactorCate];
        this.projectData.nonFunctionFactor = (this.projectData.distributedFactorCate + this.projectData.performanceFactorCate + this.projectData.reliabilityFactorCate + this.projectData.multisiteFactorCate - 4) * 0.025 + 1
        this.projectData.platformFactor = this.constValue.category2Factor['platform'][this.projectData.platformFactorCate];
        this.projectData.backgroundFactor = this.constValue.category2Factor['background'][this.projectData.backgroundFactorCate];
      }
      this.projectData.totalAdaptedEffort = this.AE;
      this.projectData.totalLabourCost = this.labourCost;
      this.projectData.totalCost = this.totalCost;
      this.functions.filter((data) => data.isChanged).forEach(
          item => {
            this.saveFunction(item);
          });
      this.functions.filter((data) => data.isDelete).forEach(
          item => {
            this.deleteFunction(item.id)
          });
      request.put("/cost-project", this.projectData).then(res => {
        if (res.code == "0") {
          this.isEdit = false;
          ElNotification({
            title: '更新项目信息成功',
            type: 'success',
          });
          callback();
        } else {
          ElNotification({
            title: '更新项目信息失败',
            message: res.msg,
            type: 'error',
          });
        }
      });
    },
    /**
     * @description 保存项目并返回详情页
     * @return {void}
     */
    saveAndBack() {
      const that = this;
      this.saveProject(function () {
        that.$router.push("/cost-project/" + that.pid)
      });
    },
    /**
     * @description 加载功能点
     * @return {void}
     */
    loadFunction() {
      request.get("function/getByProjectID/" + this.pid).then(res => {
        if (res.code == "0") {
          this.functions = res.data;
          this.loading = false;
        }
      })
    },
    /**
     * @description 添加功能点
     * @return {void}
     */
    addFunction() {
      this.$refs.functionRuleFormRef.validate((valid, fields) => {
        if (valid) {
          this.form.projectId = this.pid;
          request.post("/function", this.form).then(res => {
            if (res.code == "0") {
              ElNotification({
                title: '新增成功',
                type: 'success',
              });
              this.loadFunction();
            } else {
              ElNotification({
                title: '新增失败',
                message: res.msg,
                type: 'error',
              });
            }
          });
          this.loadFunction();
          this.form = {};
          this.functionDialogVisible = false;
        }
      });
    },
    /**
     * @description 保存功能点
     * @param {Object} data 功能点数据
     * @return {void}
     */
    saveFunction(data) {
      request.put("/function", data);
    },
    /**
     * @description 根据ID删除功能点
     * @param {number} id 功能点ID
     * @return {void}
     */
    deleteFunction(id) {
      request.delete("/function/delete/" + id);
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
          this.functionContentDialogVisible = true;
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
     * @description 显示需求文档对话框
     * @return {void}
     */
    showDocumentDialog() {
      this.documentDialogVisible = true;
      request.get("/files/get/" + this.projectData.requirementDocumentPath, {responseType: 'blob'}).then(res => {
        docxx.renderAsync(res, this.$refs.word).then(() => {
          this.documentLoading = false;
        });
      }).catch((error) => {
        this.$message({
          type: "error",
          message: error,
        });
      });
    },
    /**
     * @description 未保存返回回调方法
     * @return {String} 提示信息
     */
    beforeunloadHandler(event) {
      if (this.isEdit) {
        event.preventDefault();
        // Chrome requires returnValue to be set.
        event.returnValue = '您在页面编辑了未保存，是否确认离开';
        return '您在页面编辑了未保存，是否确认离开';
      }
    },
    /**
     * @description 返回上一页
     * @return {void}
     */
    goBack() {
      this.$router.go(-1);
    },
    /**
     * @description 格式化日期
     * @param {String} value 未格式化日期
     * @param {String} args 格式
     * @return {String} 格式化日期
     */
    dateFormate(value, args) {
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
    },
    /**
     * @description 格式化金额
     * @param {Date} data 未格式金额
     * @return {String} 格式化金额
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
     * @description 限制小数点回调方法
     * @param {String} name 键名
     * @param {number} value 键值
     * @return {void}
     */
    limitFloat(value, name) {
      this.projectData[name] = ('' + value) // 第一步：转成字符串
          .replace(/[^\d^\.]+/g, '') // 第二步：把不是数字，不是小数点的过滤掉
          .replace(/^0+(\d)/, '$1') // 第三步：第一位0开头，0后面为数字，则过滤掉，取后面的数字
          .replace(/^\./, '0.') // 第四步：如果输入的第一位为小数点，则替换成 0. 实现自动补全
          .match(/^([0-1]\.(?!0+$)\d|2(\.0)?)/g)[0] || '' // 第五步：最终匹配得到结果 以数字开头，只有一个小数点，	而且小数点后面只能有0到2位小数
    },
    /**
     * description 复制文本
     * @param {String} text 文本内容
     * @return {void}
     */
    copy(text) {
      const el = document.createElement('input')
      el.setAttribute('value', text)
      document.body.appendChild(el)
      el.select()
      document.execCommand('copy')
      document.body.removeChild(el)
    }
  }
}
</script>

<style scoped>

.desc_content {
  font-weight: bolder;
  font-size: 18px;
  line-height: 32px;
}


.form-label {
  font-weight: bold;
  font-size: 20px;
  margin-bottom: 25px;
  padding-left: 15px;
  border-left: 5px solid rgb(19, 119, 255);
}


::v-deep .k {
  font-weight: bolder;
  color: red;
  text-decoration: underline;
}

::v-deep .el-page-header__title {
  padding-top: 5px;
}

::v-deep .el-page-header__content {
  width: calc(100% - 100px);
}

::v-deep .el-step__title {
  font-weight: bold;
}

::v-deep .el-step__title.is-process {
  color: rgb(19, 119, 255);
  font-size: 20px;
}

::v-deep .el-step__description {
  margin-top: 1px;
}

::v-deep .el-step__description.is-process {
  color: rgba(19, 119, 255);
}

::v-deep .el-step__icon.is-text {
  border: none;
  width: 64px;
  height: 64px;
}

.
::v-deep .el-step.is-horizontal .el-step__line {
  top: 30px;
  background: none;
  border-bottom: 2px dotted #a8abb2;
}

::v-deep i.is-status {
  font-size: 28px;
}

::v-deep .el-radio__input {
  display: none;
}

.vertical-radio-group-detail .el-radio, .el-radio__label {
  border-radius: 5px;
  height: 128px;
  vertical-align: text-top;
  width: 400px;
}


.vertical-radio-group-detail .radio-title {
  font-weight: bold;
  font-size: 18px;
  padding: 5px;
  justify-content: space-between;
  display: flex;
}

.vertical-radio-group-detail .radio-content {
  font-size: 14px;
  padding: 5px;
  white-space: normal;
  word-break: break-all;
  line-height: 20px;
}

.vertical-radio-group .el-radio, .el-radio__label {
  border-radius: 5px;
  height: 40px;
  vertical-align: text-top;
  width: 400px;
  margin-bottom: 5px;
}

.vertical-radio-group .is-checked .radio-title {
  font-weight: bold;
}

.vertical-radio-group .radio-title {
  font-size: 16px;
  padding: 5px;
  justify-content: space-between;
  display: flex;
  width: 360px;
}
</style>