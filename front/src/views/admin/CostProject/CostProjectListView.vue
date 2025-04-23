<!--
* @FileDescription: 成本分析项目列表页
-->
<template>
  <div style="padding:25px;width: 100%; height:100%">
    <div class="header-title">成本评估项目</div>
    <el-card shadow="always" ref="element" style="border-radius: 15px;">
      <el-tabs v-model="activeName" @tab-click="handleClick" style="padding:10px;">
        <el-tab-pane label="待审查项目" name="reviewing">
          <div class="tool-menu">
            <div>
              <el-button @click="load1">
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
              <el-input v-model="nameSearchInput1" placeholder="请输入项目名称" clearable
                        style="width: 200px;margin-right: 15px;"/>
              <label class="tool-label">送审机构名称</label>
              <el-input v-model="affiliationSearchInput1" placeholder="请输入送审机构名称" clearable
                        style="width: 200px;margin-right: 15px;"/>
              <el-button @click="search1">
                <el-icon>
                  <Search/>
                </el-icon> &nbsp;搜索
              </el-button>
              <el-button @click="reset1" style="margin-right: 15px;">重置</el-button>
              <el-radio-group v-model="viewRadio1" style="height: 32px;" fill="rgb(220,220,220)">
                <el-radio-button :label="0">
                  <list-two theme="outline" size="14" fill="#444"/>
                </el-radio-button>
                <el-radio-button :label="1">
                  <view-grid-card theme="outline" size="14" fill="#444"/>
                </el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <el-table v-if="viewRadio1==0" :data="tableData1" stripe header-row-style="color:black">
            <el-table-column fixed prop="name" label="项目名称" sortable>
              <template #default="scope">
                <el-link style="font-weight: bold" @click="moveto(scope.row.id)" :disabled="scope.row.status<1">
                  {{ scope.row.name }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column label="送审单位" prop="affiliation" width="140" align="center"/>
            <el-table-column label="所属行业" width="140" align="center">
              <template #default="scope">
                {{ this.constValue.areas[scope.row.area] }}
              </template>
            </el-table-column>
            <el-table-column label="送审金额（元）" sortable width="200" align="center">
              <template #default="scope">
                {{ moneyFormat(scope.row.totalAmount) }}
              </template>
            </el-table-column>
            <el-table-column label="估算方式" sortable width="200" align="center">
              <template #default="scope">
                {{ ["预估功能点法", "估算功能点法"][scope.row.scaleRadio] }}
              </template>
            </el-table-column>
            <el-table-column label="上传时间" prop="createDate" sortable width="200"/>
            <el-table-column fixed="right" label="操作" width="240">
              <template #default="scope">
                <el-progress v-if="scope.row.status==0" :percentage="scope.row.progress" width="64"
                             style="height:40px;"/>
                <el-button v-if="scope.row.status==-1" @click="restart(scope.row.id)" size="large" round>重启
                </el-button>
                <el-button v-if="scope.row.status>0" type="success" @click="moveto(scope.row.id)" size="large" round>
                  <el-icon>
                    <FullScreen/>
                  </el-icon>&nbsp;审查
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
            </el-table-column>
          </el-table>
          <el-table v-else :data="tableData1" header-row-style="color:black" :show-header="false">
            <el-table-column width="540px">
              <template #default="scope">
                <el-link style="font-weight: bold;font-size: 20px;margin:15px 0;" @click="moveto(scope.row.id)">
                  {{ scope.row.name }}
                </el-link>
                <el-row :column="24">
                  <el-col :span="6" :min-width="120" style="height: 32px;">
                    <peoples theme="outline" size="16" fill="#444444" style="margin-right: 5px;"/>
                    <span>{{ scope.row.affiliation }}</span>
                  </el-col>
                  <el-col :span="6" label="所属行业" :min-width="80">
                    <category-management theme="outline" size="16" fill="#444444" style="margin-right: 5px;"/>
                    <span>{{ constValue.areas[scope.row.area] }}</span>
                  </el-col>
                  <el-col :span="12" label="上传时间" :min-width="180">
                    <time-icon theme="outline" size="16" fill="#444444" style="margin-right: 5px;"/>
                    <span>{{ scope.row.createDate }}</span>
                  </el-col>
                </el-row>
              </template>
            </el-table-column>
            <el-table-column align="center">
              <template #default="scope">
                <el-descriptions :column="8" direction="vertical">
                  <el-descriptions-item :span="8" label="软件规模估算方式" :min-width="200">
                    <span class="project_item_content">
                      {{ ["预估功能点法", "估算功能点法"][scope.row.scaleRadio] }}
                    </span>
                  </el-descriptions-item>
                </el-descriptions>
              </template>
            </el-table-column>
            <el-table-column align="center">
              <template #default="scope">
                <el-descriptions :column="8" direction="vertical">
                  <el-descriptions-item :span="8" label="送审金额" :min-width="200">
                    <span class="project_item_content">
                      {{ moneyFormat(scope.row.totalAmount) + " 元" }}
                    </span>
                  </el-descriptions-item>
                </el-descriptions>
              </template>
            </el-table-column>
            <el-table-column min-width="200px">
              <template #default="scope">
                备注：{{ scope.row.comment && scope.row.comment.length > 0 ? scope.row.comment : "无" }}
              </template>
            </el-table-column>
            <el-table-column fixed="right" width="240">
              <template #default="scope">
                <div v-if="scope.row.status==-1">
                  <el-button @click="restart(scope.row.id)" size="large" round>重启</el-button>
                </div>
                <div v-if="scope.row.status==0">
                  <el-progress type="circle" :percentage="scope.row.progress" width="64"/>
                </div>
                <div v-if="scope.row.status==1">
                  <el-button type="success" @click="moveto(scope.row.id)" size="large" round>
                    <el-icon>
                      <FullScreen/>
                    </el-icon>&nbsp;审查
                  </el-button>
                </div>
                <div v-if="scope.row.status!=0" style="margin-top: 5px;">
                  <el-popconfirm title="确认删除？" @confirm="handleDelete(scope.row.id)">
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
          <div style="margin: 10px 0;">
            <el-pagination
                align="center"
                v-model:current-page="currentPage1"
                v-model:page-size="pageSize1"
                background
                :page-sizes="[5, 10, 20]"
                layout="total, sizes,prev, pager, next, jumper"
                :total="total1"
                @size-change="load1"
                @current-change="load1"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="已审查项目" name="finished">
          <div class="tool-menu">
            <div>
              <el-button @click="load2">
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
              <el-input v-model="nameSearchInput2" placeholder="请输入项目名称" clearable
                        style="width: 200px;margin-right: 15px;"/>
              <label class="tool-label">送审机构名称</label>
              <el-input v-model="affiliationSearchInput2" placeholder="请输入送审机构名称" clearable
                        style="width: 200px;margin-right: 15px;"/>
              <el-button @click="search2">
                <el-icon>
                  <Search/>
                </el-icon> &nbsp;搜索
              </el-button>
              <el-button @click="reset2" style="margin-right: 15px;">重置</el-button>
              <el-radio-group v-model="viewRadio2" style="height: 32px;" fill="rgb(220,220,220)">
                <el-radio-button :label="0">
                  <list-two theme="outline" size="14" fill="#444"/>
                </el-radio-button>
                <el-radio-button :label="1">
                  <view-grid-card theme="outline" size="14" fill="#444"/>
                </el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <el-table v-if="viewRadio2==0" :data="tableData2" stripe header-row-style="color:black">
            <el-table-column prop="name" label="项目名称">
              <template #default="scope">
                <el-link style="font-weight: bold" @click="moveto(scope.row.id)" :disabled="scope.row.status<1">
                  {{ scope.row.name }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="affiliation" label="送审单位" width="160"/>
            <el-table-column label="送审金额（元）" sortable width="160">
              <template #default="scope">
                {{ moneyFormat(scope.row.totalAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalScale" label="软件规模" width="200"/>
            <el-table-column label="软件开发总成本(元)" sortable width="200">
              <template #default="scope">
                {{ moneyFormat(scope.row.totalCost) }}
              </template>
            </el-table-column>
            <el-table-column prop="createDate" label="上传时间" width="180"/>
            <el-table-column fixed="right" label="操作" width="400">
              <template #default="scope">
                <el-button v-if="scope.row.status>0" @click="moveto(scope.row.id)" size="large" round>
                  <el-icon>
                    <FullScreen/>
                  </el-icon>&nbsp;查看报告
                </el-button>
                <Docxtemplater v-if="scope.row.status>0" :id="scope.row.id"/>
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
          <el-table v-else :data="tableData2" stripe header-row-style="color:black">
            <el-table-column width="540px">
              <template #default="scope">
                <el-link style="font-weight: bold;font-size: 20px;margin:15px 0;" @click="moveto(scope.row.id)">
                  {{ scope.row.name }}
                </el-link>
                <el-row :column="24">
                  <el-col :span="6" :min-width="120" style="height: 32px;">
                    <peoples theme="outline" size="16" fill="#444444" style="margin-right: 5px;"/>
                    <span>
                      {{ scope.row.affiliation }}
                    </span>
                  </el-col>
                  <el-col :span="6" label="所属行业" :min-width="80">
                    <category-management theme="outline" size="16" fill="#444444" style="margin-right: 5px;"/>
                    <span>
                      {{ constValue.areas[scope.row.area] }}
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
            <el-table-column align="center">
              <template #default="scope">
                <el-descriptions :column="8" direction="vertical">
                  <el-descriptions-item :span="8" label="软件规模估算方式" :min-width="200">
                    <span class="project_item_content">
                      {{ ["预估功能点法", "估算功能点法"][scope.row.scaleRadio] }}
                    </span>
                  </el-descriptions-item>
                </el-descriptions>
              </template>
            </el-table-column>
            <el-table-column align="center">
              <template #default="scope">
                <el-descriptions :column="8" direction="vertical">
                  <el-descriptions-item :span="8" label="送审金额" :min-width="200">
                    <span class="project_item_content">
                      {{ moneyFormat(scope.row.totalAmount) + " 元" }}
                    </span>
                  </el-descriptions-item>
                </el-descriptions>
              </template>
            </el-table-column>
            <el-table-column width="240">
              <template #default="scope">
                <el-descriptions :column="8" direction="vertical">
                  <el-descriptions-item :span="8" label="软件规模" :min-width="200">
                                    <span class="project_item_content">
                                        {{ scope.row.totalScale }}
                                    </span>
                  </el-descriptions-item>
                </el-descriptions>
              </template>
            </el-table-column>
            <el-table-column width="240">
              <template #default="scope">
                <el-descriptions :column="8" direction="vertical">
                  <el-descriptions-item :span="8" label="软件开发总成本" :min-width="200">
                                    <span class="project_item_content">
                                        {{ moneyFormat(scope.row.totalCost) + " 元" }}
                                    </span>
                  </el-descriptions-item>
                </el-descriptions>
              </template>
            </el-table-column>
            <el-table-column>
              <template #default="scope">
                <el-descriptions :column="8" direction="vertical">
                  <el-descriptions-item :span="8" label="备注" :min-width="200">
                    <span>
                      {{ scope.row.comment && scope.row.comment.length > 0 ? scope.row.comment : "无" }}
                    </span>
                  </el-descriptions-item>
                </el-descriptions>
              </template>
            </el-table-column>
            <el-table-column fixed="right" width="240" align="center">
              <template #default="scope">
                <div v-if="scope.row.status>0">
                  <el-button @click="moveto(scope.row.id)" size="large" round>
                    <el-icon>
                      <FullScreen/>
                    </el-icon>&nbsp;查看报告
                  </el-button>
                </div>
                <div v-if="scope.row.status>0">
                  <Docxtemplater :id="scope.row.id"/>
                </div>
                <div v-if="scope.row.status!=0" style="margin-top: 5px;">
                  <el-popconfirm title="确认删除？" @confirm="handleDelete(scope.row.id)">
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
          <div style="margin: 10px 0;">
            <el-pagination
                align="center"
                v-model:current-page="currentPage2"
                v-model:page-size="pageSize2"
                background
                :page-sizes="[5, 10, 20]"
                layout="total, sizes,prev, pager, next, jumper"
                :total="total2"
                @size-change="load2"
                @current-change="load2"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-drawer
        v-model="drawerVisible"
        :before-close="handleClose"
        size="600px"
        title="新建项目"
    >
      <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="210px" label-position="top" status-icon
               style="padding:0 15px;">
        <div class="step-pane">
          <div class="form-label" style="margin-top: 0px;">项目信息</div>
          <div class="pane-body">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="form.name" style="width: 600px;" maxlength="20" show-word-limit/>
            </el-form-item>
            <el-form-item label="送审单位" prop="affiliation">
              <el-input v-model="form.affiliation" style="width: 600px;" maxlength="20" show-word-limit/>
            </el-form-item>
            <el-form-item label="送审金额（元）" prop="totalAmount">
              <el-input type="text" v-model="totalAmount" style="width: 600px;"/>
            </el-form-item>
            <el-form-item label="备注" prop="comment">
              <el-input type="textarea" v-model="form.comment" style="width: 600px;" maxlength="100" show-word-limit/>
            </el-form-item>
          </div>
        </div>
        <div class="step-pane">
          <div class="form-label">需求文档上传</div>
          <div class="pane-body">
            <el-form-item label="上传文件" prop="upload">
              <el-upload v-show="this.form.requirementDocumentPath==null&&!uploading" style="width: 100%;" drag
                         ref="upload" :action="constValue.uploadUrl" accept=".doc,.docx" :show-file-list="false"
                         :on-progress="onUpload" :on-success="fileuploadSuccess" :on-change="fileChange"
                         :headers="myheader" :before-upload="beforeWordUpload">
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
                    源代码要求为doc或docx格式，文件大小至多为500MB
                  </div>
                </template>
              </el-upload>
              <div v-show="this.form.requirementDocumentPath || uploading"
                   style="width:100%;border:1px solid  rgb(103,194,58);text-align:center;padding:15px;box-shadow: inset 0px 0px 10px 5px rgba(103,194,58,.1);border-radius: 5px;">
                <el-progress
                    v-if="uploading&&progressPercent!=100"
                    :percentage="progressPercent" :text-inside="true"
                    :stroke-width="24" status="success">
                </el-progress>
                <template v-if="this.form.requirementDocumentPath">
                  <el-icon size="large" color="rgb(103,194,58)">
                    <SuccessFilled/>
                  </el-icon>&nbsp;<span style="font-size: 18px;">{{ form.requirementDocumentName }}</span>&nbsp;已成功上传
                  <div style="text-decoration: underline;color: #0D74FF;cursor: pointer;"
                       @click="this.form.requirementDocumentPath=null;this.uploading=false;">重新上传
                  </div>
                </template>
              </div>
            </el-form-item>
          </div>
          <div class="step-pane">
            <div class="form-label">成本评估配置</div>
            <el-form-item label="规模估算阶段" prop="scaleFactor">
              <el-radio-group class="vertical-radio-group" v-model="form.scaleFactor" size="default"
                              style="width: 100%">
                <el-radio v-for="scaleFactor in constValue.scaleFactors" :label="scaleFactor.value" border>
                  <div class="radio-title">
                    <div>{{ scaleFactor.key }}</div>
                    <el-icon v-if="form.scaleFactor==scaleFactor.value" style="font-size: 20px;padding-top: 6px;">
                      <Check/>
                    </el-icon>
                  </div>
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="所属行业(行业生产率)" prop="area">
              <el-radio-group class="vertical-radio-group" v-model="form.area" size="default" style="width: 100%">
                <el-radio v-for="(item,idx) in constValue.areas" :label="idx" border>
                  <div class="radio-title">
                    <div>{{ item }}-{{ constValue.Area2PDR[idx] + ' p.h./FP' }}</div>
                    <el-icon v-if="form.area==idx" style="font-size: 20px;padding-top: 6px;">
                      <Check/>
                    </el-icon>
                  </div>
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="软件规模估算方式" prop="scaleRadio">
              <el-radio-group class="vertical-radio-group-detail" v-model="form.scaleRadio" size="default"
                              style="width: 100%">
                <el-radio :label="0" border>
                  <div class="radio-title">
                    <div>预估功能点法</div>
                    <el-icon v-if="form.scaleRadio==0">
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
                    <el-icon v-if="form.scaleRadio==1">
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
              <el-radio-group class="vertical-radio-group-detail" v-model="form.costRadio" size="default"
                              style="width: 100%">
                <el-radio :label="0" border>
                  <div class="radio-title">
                    <div>按平均人时费率计算</div>
                    <el-icon v-if="form.costRadio==0">
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
                    <el-icon v-if="form.costRadio==1">
                      <Check/>
                    </el-icon>
                  </div>
                  <div class="radio-content">
                    按照开发人员岗位分配调整后工作量，并分别确定软件开发平均人时费率，计算软件开发成本
                  </div>
                </el-radio>
              </el-radio-group>
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
  UploadFilled,
  Download,
  Refresh,
  SuccessFilled,
  Select,
  Check
} from '@element-plus/icons-vue';
import Docxtemplater from '@/components/Docxtemplater.vue';
import {constValue} from "@/assets/js/const.js"
import {
  ListTwo,
  ViewGridCard,
  Peoples,
  CategoryManagement,
  PaperMoney,
  FigmaComponent,
  Time as TimeIcon,
  FinancingTwo
} from '@icon-park/vue-next';

export default {
  name: 'ProjectListView',
  components: {
    Delete,
    Search,
    Plus,
    FullScreen,
    UploadFilled,
    Download,
    SuccessFilled,
    Refresh,
    Select,
    Check,
    Docxtemplater,
    constValue,
    ListTwo,
    ViewGridCard,
    Peoples,
    CategoryManagement,
    PaperMoney,
    FigmaComponent,
    FinancingTwo,
    TimeIcon
  },
  data() {
    return {
      constValue: constValue,
      myheader: {
        token: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).token : ""
      },
      activeName: "reviewing",
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
        totalAmount: [
          {required: true, message: "请输入送审金额", trigger: "blur"}
        ],
        comment: [
          {max: 100, message: "备注长度不能超过100字符！", trigger: "blur"}
        ],
        area: [
          {required: true, message: "请选择行业", trigger: "change"}
        ],
        scaleFactor: [
          {required: true, message: "请选择评估时期", trigger: "change"}
        ],
        scaleRadio: [
          {required: true, message: "请选择", trigger: "change"}
        ],
        costRadio: [
          {required: true, message: "请选择", trigger: "change"}
        ],
        upload: [
          {required: true, validator: this.validateUpload, trigger: "change"}
        ],
      },
      totalAmount: "",
      nameSearchInput1: "",
      affiliationSearchInput1: "",
      nameSearchInput2: "",
      affiliationSearchInput2: "",
      nameSearch1: "",
      affiliationSearch1: "",
      currentPage1: 1,
      pageSize1: 10,
      total1: 0,
      nameSearch2: "",
      affiliationSearch2: "",
      currentPage2: 1,
      pageSize2: 10,
      total2: 0,
      active: 0,
      viewRadio1: 0,
      viewRadio2: 0,
      tableLayout: "list",
      tableData1: [],
      tableData2: [],
      uploading: false,
      progressPercent: 0
    }
  },
  computed: {},
  watch: {
    /**
     * @description 监听送审金额
     * @param {String} newVal
     * @param {String} oldVal
     * @return {void}
     */
    totalAmount(newVal, oldVal) {
      var amount = Number(newVal.replace(/\$\s?|(,*)/g, ''));
      if (amount > 1e8)
        amount = 1e8;
      this.totalAmount = amount.toString().replace(/[a-zA-Z]/g, '').replace(/[`~!@#$^\-&*()=|{}':;',\\\[\]\.<>\/?~！@#￥……&*（）——|{}【】'；：""'。，、？\s]/g, '').replace(/\B(?=(\d{4})+(?!\d))/g, ',');
      this.form.totalAmount = Number(this.totalAmount.replace(/\$\s?|(,*)/g, ''));
    }
  },
  created() {
    this.load1();
    this.load2();
  },
  methods: {
    /**
     * @description 加载审查中的成本分析项目列表
     * @return {void}
     */
    load1() {
      request.get("cost-project", {
        params: {
          pageNum: this.currentPage1,
          pageSize: this.pageSize1,
          nameSearch: this.nameSearch1,
          affiliationSearch: this.affiliationSearch1,
        }
      }).then(res => {
        if (res.code == "0") {
          this.tableData1 = res.data.records;
          this.total1 = res.data.total;
          for (let data of this.tableData1) {
            if (data.status == 0) {
              setTimeout(this.load1, 2000);
              break;
            }
          }
        }
      })
    },
    /**
     * @description 加载已审查的成本分析项目列表
     * @return {void}
     */
    load2() {
      request.get("cost-project", {
        params: {
          pageNum: this.currentPage2,
          pageSize: this.pageSize2,
          nameSearch: this.nameSearch2,
          affiliationSearch: this.affiliationSearch2,
          status: 2
        }
      }).then(res => {
        if (res.code == "0") {
          this.tableData2 = res.data.records;
          this.total2 = res.data.total;
        }
      })
    },
    /**
     * @description 搜索符合条件的审查中的成本分析项目列表
     * @return {void}
     */
    search1() {
      this.nameSearch1 = this.nameSearchInput1;
      this.affiliationSearch1 = this.affiliationSearchInput1;
      this.load1();
    },
    /**
     * @description 搜索符合条件的已审查的成本分析项目列表
     * @return {void}
     */
    search2() {
      this.nameSearch2 = this.nameSearchInput2;
      this.affiliationSearch2 = this.affiliationSearchInput2;
      this.load1();
    },
    /**
     * @description 重置审查中的成本分析项目列表搜索条件
     * @return {void}
     */
    reset1() {
      this.nameSearch1 = '';
      this.affiliationSearch1 = '';
      this.load1()
    },
    /**
     * @description 重置已审查的成本分析项目列表搜索条件
     * @return {void}
     */
    reset2() {
      this.nameSearch2 = '';
      this.affiliationSearch2 = '';
      this.load2()
    },
    /**
     * @description 根据ID删除成本分析项目
     * @param {number} id 成本分析项目id
     * @return {void}
     */
    handleDelete(id) {
      request.delete("/cost-project/delete/" + id).then(res => {
        if (res.code == "0") {
          ElNotification({
            type: 'success',
            title: "删除成功"
          });
          this.load1();
          this.load2();
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
     * @description 抽屉关闭回调方法
     * @return {void}
     */
    handleClose() {
      this.$refs.upload.abort();
      this.uploading = false;
      this.progressPercent = 0;
      this.drawerVisible = false;
      this.form = {};
      this.totalAmount = "";
      this.$refs.ruleFormRef.resetFields();
    },
    /**
     * @description 根据id跳转到成本分析项目详情页
     * @param {number} pid 成本分析项目id
     * @return {void}
     */
    moveto(pid) {
      this.$router.push("/cost-project/" + pid);
    },
    /**
     * @description 根据id重启需求文档分析任务
     * @param {number} id 成本分析项目id
     * @return {void}
     */
    restart(id) {
      request.post("/cost-project/restartTask/" + id).then(res => {
        if (res.code == "0") {
          ElNotification({
            type: 'success',
            title: "重启成功"
          });
          this.load1();
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
     * @description 格式化金额
     * @param {number} data 金额数值
     * @return {String} 格式化的金额字符串
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
     * @description 添加成本分析项目
     */
    save() {
      this.$refs.ruleFormRef.validate((valid, fields) => {
        if (valid) {
          request.post("/cost-project", this.form).then(res => {
            if (res.code == "0") {
              ElNotification({
                type: 'success',
                title: "新建项目成功"
              });
              this.load1();
              this.handleClose();
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
            type: 'error',
            title: "请检查输入"
          });
      });
    },
    /**
     * @description 文件上传成功回调方法
     * @param {Object} res 返回结果
     */
    fileuploadSuccess(res) {
      if (res.code == "0") {
        ElNotification({
          type: 'success',
          title: "需求文档上传成功"
        });
        this.form.requirementDocumentPath = res.data;
        this.uploading = false;
        this.progressPercent = 0;
      } else {
        ElNotification({
          type: 'error',
          message: res.msg,
          title: "需求文档上传失败"
        });
        this.form.requirementDocumentName = "";
        this.uploading = false;
        this.progressPercent = 0;
        this.$refs.upload.clearFiles();
      }
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
     * @description 文件过程回调方法
     * @param {Object} event 事件
     * @param {Object} file 文件
     * @param {Array} fileList 文件列表
     */
    fileChange(file) {
      if (file.raw.type !== 'application/msword' && file.raw.type !== 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
        ElNotification({
          type: 'error',
          message: '需求文档必须是doc或docx格式!',
          title: "需求文档上传失败"
        });
        this.$refs.upload.clearFiles();
        this.progressPercent = 0;
        this.uploading = false;
        return;
      } else if (file.size / 1024 / 1024 > 500) {
        ElNotification({
          type: 'error',
          message: '需求文档大小不能超过500MB!!',
          title: "需求文档上传失败"
        });
        this.$refs.upload.clearFiles();
        this.progressPercent = 0;
        this.uploading = false;
        return;
      }
      this.form.requirementDocumentName = file.name;
      return;
    },
    /**
     * @description 文件上传前回调方法
     * @param {Object} file 文件
     * @return {boolean} 返回是否符合要求
     */
    beforeWordUpload(file) {
      if (file.type !== 'application/msword' && file.type !== 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
        ElNotification({
          type: 'error',
          message: '需求文档必须是doc或docx格式!',
          title: "需求文档上传失败"
        });
        return false;
      } else if (file.size / 1024 / 1024 > 500) {
        ElNotification({
          type: 'error',
          message: '需求文档大小不能超过500MB!!',
          title: "需求文档上传失败"
        });
        return false;
      }
      this.form.requirementDocumentName = file.name;
      return true;
    },
    /**
     * @description 文件是否上传的校验方法
     * @param {Object} rule 规则
     * @param {Object} value 值
     * @param {Object} callback 回调函数
     */
    validateUpload(rule, value, callback) {
      if (!this.form.requirementDocumentPath) {
        callback(new Error('请上传需求文档'));
      } else {
        callback();
      }
    }
  }
}
</script>

<style scoped>

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

.tool-menu {
  display: flex;
  justify-content: space-between;
  padding: 10px;
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

::v-deep .el-radio__input {
  display: none;
}

.vertical-radio-group-detail .el-radio, .el-radio__label {
  border-radius: 5px;
  height: 128px;
  vertical-align: text-top;
  width: 400px;
  margin-bottom: 5px;
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
