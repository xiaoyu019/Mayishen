<!--
* @FileDescription: 源码分析项目详情页
-->
<template>
  <el-card shadow="always">
    <el-breadcrumb separator="/" style="padding:15px;border-bottom: 1px solid lightgray;">
      <el-breadcrumb-item :to="{ path: '/hello' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item><a href="/code-projects/">源码分析列表</a></el-breadcrumb-item>
      <el-breadcrumb-item> {{ this.projectData.name }}</el-breadcrumb-item>
    </el-breadcrumb>
    <div style="padding:15px;">
      <div class="card-header">
        <span class="header-title">{{ this.projectData.name }}</span>
        <div>
          <el-button round @click="downloadFilebyName(this.projectData.sourceCodePath)" size="large">
            <el-icon>
              <Download/>
            </el-icon>
            下载源码
          </el-button>
          <el-button round @click="goBack" type="primary" size="large">返回</el-button>
        </div>
      </div>
      <el-row :gutter="15">
        <el-col :span="12">
          <el-descriptions :column="16">
            <el-descriptions-item :span="8" label="送审单位" :min-width="400">
                            <span class="project_item_content">
                                {{ this.projectData.affiliation }}
                            </span>
            </el-descriptions-item>
            <el-descriptions-item :span="8" label="上传时间" :min-width="400">
                            <span class="project_item_content">
                                {{ this.projectData.createDate }}
                            </span>
            </el-descriptions-item>
            <el-descriptions-item :span="16" label="备注" :min-width="400">
              {{ this.projectData.comment && this.projectData.comment.length > 0 ? this.projectData.comment : "无" }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="12">
          <el-descriptions :column="20" direction="vertical">
            <el-descriptions-item :span="5" align="center" label-align="center">
              <template #label>
                <div class="desc_label">
                  原创代码文件占比
                </div>
              </template>
              <div class="desc_content">
                {{
                  this.projectData.validFileNumber == 0 ? 0 : ((this.projectData.validFileNumber - this.projectData.cloneFileNumber) / this.projectData.validFileNumber * 100.0).toFixed(2) + "%"
                }}
              </div>
            </el-descriptions-item>
            <el-descriptions-item :span="5" align="center" label-align="center">
              <template #label>
                <div class="desc_label">
                  原创代码行数占比
                </div>
              </template>
              <div class="desc_content">
                {{
                  this.projectData.totalLineNumber == 0 ? 0 : ((this.projectData.totalLineNumber - this.projectData.cloneLineNumber) / this.projectData.totalLineNumber * 100.0).toFixed(2) + "%"
                }}
              </div>
            </el-descriptions-item>
            <el-descriptions-item :span="4" align="center" label-align="center">
              <template #label>
                <div class="desc_label">
                  依赖库漏洞数量
                </div>
              </template>
              <div class="desc_content">
                {{ this.projectData.bugNum }}
              </div>
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
              <span class="header-title">软件源码分析</span>
            </div>
          </template>
          <div>
            <el-tabs v-model="activeName" @tab-click="handleClick">
              <el-tab-pane label="数据总览" name="overview">
                <div style="border:2px dotted rgba(20,119,253,.2);padding:25px 15px 15px 15px;border-radius: 15px">
                  <el-descriptions :column="24" direction="vertical">
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          总文件数
                        </div>
                      </template>
                      <div class="desc_content">
                        {{ this.projectData.totalFileNumber }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          代码文件数
                        </div>
                      </template>
                      <div class="desc_content">
                        {{ this.projectData.validFileNumber }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          总代码行数
                        </div>
                      </template>
                      <div class="desc_content">
                        {{ this.projectData.totalLineNumber }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          原创代码文件数
                        </div>
                      </template>
                      <div class="desc_content">
                        {{
                          this.projectData.validFileNumber ? this.projectData.validFileNumber - this.projectData.cloneFileNumber : 0
                        }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          原创代码行数
                        </div>
                      </template>
                      <div class="desc_content">
                        {{
                          this.projectData.totalLineNumber ? this.projectData.totalLineNumber - this.projectData.cloneLineNumber : 0
                        }}
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item :span="4" align="center" label-align="center">
                      <template #label>
                        <div class="desc_label">
                          依赖库数量
                        </div>
                      </template>
                      <div class="desc_content">
                        {{ this.projectData.dependencyNum }}
                      </div>
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
                <el-row :gutter="15">
                  <el-col :span="12">
                    <div class="overview-label">
                      源码文件分析
                    </div>
                    <el-row :gutter="15">
                      <el-col :span="12">
                        <div ref="fileCountBar" style="width:100%;height:320px;text-align: center"></div>
                      </el-col>
                      <el-col :span="12">
                        <div ref="fileCountPan" style="width:100%;height:320px;text-align: center"></div>
                      </el-col>
                    </el-row>
                  </el-col>
                  <el-col :span="12">
                    <div class="overview-label">
                      克隆检测
                    </div>
                    <el-row :gutter="15">
                      <el-col :span="12">
                        <div ref="cloneFileCountBar" style="width:100%;height:320px;text-align: center"></div>
                      </el-col>
                      <el-col :span="12">
                        <div ref="cloneFileCountPan" style="width:100%;height:320px;text-align: center"></div>
                      </el-col>
                    </el-row>
                  </el-col>
                  <el-col :span="16">
                    <div class="overview-label">
                      开源代码溯源
                    </div>
                    <el-row :gutter="15">
                      <el-col :span="12">
                        <div ref="cloneProjectBar1" style="width:100%;height:320px;text-align: center"></div>
                      </el-col>
                      <el-col :span="12">
                        <div ref="cloneProjectBar2" style="width:100%;height:320px;text-align: center"></div>
                      </el-col>
                    </el-row>
                  </el-col>
                  <el-col :span="8">
                    <div class="overview-label">
                      开源库漏洞
                    </div>
                    <div ref="bugCountPan" style="width:100%;height:320px;text-align: center"></div>
                  </el-col>
                </el-row>

              </el-tab-pane>
              <el-tab-pane label="源码文件" name="fileTypes">
                <div style="display: flex;justify-content: space-between;">
                  <div>
                    <el-dropdown @click="downloadExcel1(0)" style="border-radius:15px;margin-right:15px;">
                      <el-button :loading="excelDataLoading1" type="success" size="large">
                        <el-icon>
                          <Download/>
                        </el-icon>&nbsp;导出记录
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item @click="downloadExcel1(1)">导出原创文件记录</el-dropdown-item>
                          <el-dropdown-item @click="downloadExcel1(2)">导出相似文件记录</el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                    <el-button size="large" round plain @click="showProjectCodeFileExplorerDialog">文件管理器
                    </el-button>
                  </div>
                  <el-form inline>
                    <el-form-item label="文件类型">
                      <el-select v-if="status==0" v-model="currentType1" @change="loadCodeFiles">
                        <el-option :value="-1" label="不论"/>
                        <template v-for="(option,index) in this.constValue.types">
                          <el-option :value="index"
                                     :label="option+'('+(fileCount.filter(e => e.name === this.constValue.types[index]).length>0?fileCount.filter(e => e.name === this.constValue.types[index])[0].value:0)+')'"/>
                        </template>
                      </el-select>
                      <el-select v-if="status==1" v-model="currentType1" @change="loadCodeFiles">
                        <el-option :value="-1" label="不论"/>
                        <template v-for="(option,index) in this.constValue.types">
                          <el-option v-if="index>0" :value="index"
                                     :label="option+'('+((fileCount.filter(e => e.name === this.constValue.types[index]).length>0?(fileCount.filter(e => e.name === this.constValue.types[index])[0].value):0)-(cloneFileCount.filter(e => e.name === this.constValue.types[index]).length>0?cloneFileCount.filter(e => e.name === this.constValue.types[index])[0].value:0))+')'"/>
                        </template>
                      </el-select>
                      <el-select v-if="status==2" v-model="currentType1" @change="loadCodeFiles">
                        <el-option :value="-1" label="不论"/>
                        <template v-for="(option,index) in this.constValue.types">
                          <el-option v-if="index>0" :value="index"
                                     :label="option+'('+(cloneFileCount.filter(e => e.name === this.constValue.types[index]).length>0?cloneFileCount.filter(e => e.name === this.constValue.types[index])[0].value:0)+')'"/>
                        </template>
                      </el-select>
                    </el-form-item>
                    <el-form-item label="克隆情况">
                      <el-select v-model="status" @change="loadCodeFiles">
                        <el-option :value="0" label="不论"/>
                        <el-option :value="1" label="原创"/>
                        <el-option :value="2" label="克隆"/>
                      </el-select>
                    </el-form-item>
                  </el-form>
                </div>
                <el-table :data="codeFiles" header-row-style="color:black" stripe>
                  <el-table-column prop="name" label="文件名称" sortable width="400"></el-table-column>
                  <el-table-column prop="path" label="文件路径"></el-table-column>
                  <el-table-column label="文件类型" width="100">
                    <template #default="scope">
                      {{ constValue.types[scope.row.type] }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="lineNumber" label="代码行数" width="120" align="right"></el-table-column>
                  <el-table-column label="克隆情况" width="160">
                    <template #default="scope">
                      <span v-if="scope.row.type == 0">-</span>
                      <template v-else>
                        <el-tag v-if="scope.row.openSourceFiles.length > 0" round>克隆</el-tag>
                        <el-tag type="success" size="default" v-else round>原创</el-tag>
                      </template>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="320" fixed="right">
                    <template #default="scope">
                      <el-button v-if="scope.row.type!=0" @click="showCodeFileContentDialog(scope.row.id)" round>查看源码
                      </el-button>
                      <el-button v-if="scope.row.openSourceFiles.length > 0"
                                 @click="showCodeFilesCloneDialog(scope.row.name,scope.row.openSourceFiles)" round>克隆情况
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div style="margin: 10px 0;">
                  <el-pagination
                      align="center"
                      v-model:currentPage="currentPage1"
                      background
                      v-model:page-size="pageSize1"
                      :page-sizes="[5, 10, 20]"
                      layout="total, sizes, prev, pager, next,jumper"
                      :total="total1"
                      @current-change="loadCodeFiles"
                  />
                </div>
              </el-tab-pane>
              <el-tab-pane label="相似代码文件匹配" name="cloneDetection">
                <div style="display: flex;justify-content: space-between;">
                  <div>
                    <el-dropdown @click="downloadExcel2(0)" style="border-radius:15px;margin-right:15px;">
                      <el-button :loading="excelDataLoading2" type="success" size="large">
                        <el-icon>
                          <Download/>
                        </el-icon>&nbsp;导出记录
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item @click="downloadExcel2(1)">按源码文件导出相似代码匹配记录</el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                  <el-form inline>
                    <el-form-item label="文件类型">
                      <el-select v-model="currentType2" @change="loadOpenSourceFiles">
                        <el-option :value="0" label="不论"/>
                        <template v-for="(option,index) in this.constValue.types">
                          <el-option v-if="cloneFileCount.filter(e => e.name == this.constValue.types[index]).length>0"
                                     :value="index"
                                     :label="option+'('+(cloneFileCount.filter(e => e.name === this.constValue.types[index])[0].value)+')'"/>
                        </template>
                      </el-select>
                    </el-form-item>
                  </el-form>
                </div>
                <el-table :data="openSourceFiles" header-row-style="color:black" style="" stripe>
                  <el-table-column prop="projectFileName" label="文件名称" width="200"></el-table-column>
                  <el-table-column prop="projectFilePath" label="文件路径"></el-table-column>
                  <el-table-column prop="openSourceProjectName" label="开源项目名" width="200"></el-table-column>
                  <el-table-column prop="name" label="相似文件名称" width="200"></el-table-column>
                  <el-table-column prop="path" label="相似文件路径"></el-table-column>
                  <el-table-column prop="lineNumber" label="相似代码行数" width="160" align="right"
                                   header-align="left"></el-table-column>
                  <el-table-column label="操作" width="120" fixed="right">
                    <template #default="scope">
                      <el-button @click="showCodeFileComparisionDialog(scope.row.projectFileId,scope.row.id)" round>对比源码
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div style="margin: 10px 0;">
                  <el-pagination
                      align="center"
                      v-model:currentPage="currentPage2"
                      background
                      v-model:page-size="pageSize2"
                      :page-sizes="[5, 10, 20]"
                      layout="total, sizes, prev, pager, next,jumper"
                      :total="total2"
                      @current-change="loadOpenSourceFiles"
                  />
                </div>
              </el-tab-pane>
              <el-tab-pane label="开源项目克隆情况" name="openSourceProject">
                <div style="display: flex;justify-content: space-between;">
                  <div>
                  </div>
                  <el-form inline>
                    <el-form-item label="开源许可证类型">
                      <el-select v-model="currentLicenseType" @change="loadOpenSourceProject">
                        <el-option :value="-1" label="不论"/>
                        <template v-for="(option,index) in this.constValue.licenses">
                          <el-option :value="index" :label="option"/>
                        </template>
                      </el-select>
                    </el-form-item>
                    <el-form-item label="排序方式">
                      <el-select v-model="order3" @change="loadOpenSourceProject">
                        <el-option :value="0" label="按克隆文件占比"/>
                        <el-option :value="1" label="按克隆行数占比"/>
                      </el-select>
                    </el-form-item>
                  </el-form>
                </div>
                <el-table :data="openSourceProjects" header-row-style="color:black" stripe>
                  <el-table-column label="开源项目名称">
                    <template #default="scope">
                      {{ scope.row.name }}
                      <el-tag type="error"
                              v-if="scope.row.cloneFileNumber / scope.row.validFileNumber > 0.5 || scope.row.cloneLineNumber / scope.row.totalLineNumber > 0.5">
                        高克隆风险
                      </el-tag>&nbsp;
                      <el-tag type="warning"
                              v-if="scope.row.license == 3 ||scope.row.license == 4 || scope.row.license == 5 || scope.row.license == 6">
                        必须公开源代码
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="cloneFileNumber" label="克隆代码文件数" width="140"
                                   align="right"></el-table-column>
                  <el-table-column prop="cloneLineNumber" label="克隆代码行数" width="140"
                                   align="right"></el-table-column>
                  <el-table-column label="克隆文件占比" width="140" align="right">
                    <template #default="scope">
                      {{ (scope.row.cloneFileNumber / scope.row.validFileNumber * 100).toFixed(2) }}%
                    </template>
                  </el-table-column>
                  <el-table-column label="克隆行数占比" width="140" align="right">
                    <template #default="scope">
                      {{ (scope.row.cloneLineNumber / scope.row.totalLineNumber * 100).toFixed(2) }}%
                    </template>
                  </el-table-column>
                  <el-table-column label="开源协议" width="300">
                    <template #default="scope">
                      <el-popover placement="top" :width="400" trigger="hover">
                        <template #reference>
                          {{ constValue.licenses[scope.row.license] }}
                        </template>
                        <template
                            v-for="info in this.constValue.licenseInfomation[constValue.licenses[scope.row.license]]">
                          -{{ info }}<br>
                        </template>
                      </el-popover>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="320">
                    <template #default="scope">
                      <el-button @click="moveTo(scope.row.url)">访问托管地址</el-button>
                      <el-button @click="showOpenSourceProjectCloneDialog(scope.row.id,scope.row.name)">克隆情况
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div style="margin: 10px 0;">
                  <el-pagination
                      align="center"
                      v-model:currentPage="currentPage3"
                      background
                      v-model:page-size="pageSize3"
                      :page-sizes="[5, 10, 20]"
                      layout="total, sizes, prev, pager, next,jumper"
                      :total="total3"
                      @current-change="loadOpenSourceProject"
                  />
                </div>
              </el-tab-pane>
              <el-tab-pane label="依赖库漏洞检查" name="openSourceLibraryDetection">
                <el-table v-loading="loading" :data="this.dependencies">
                  <el-table-column prop="name" label="依赖库名称"/>
                  <el-table-column prop="version" label="版本"/>
                  <el-table-column label="漏洞">
                    <template #default="scope">
                      <el-popover
                          v-for="bug in scope.row.bugs"
                          placement="left"
                          :width="400"
                          trigger="hover"
                      >
                        <template #reference>
                          <el-tag :color="getTagColorByBugLevel(bug.level)"
                                  style="margin-right: 5px;margin-bottom: 10px;color:white;border: none;" effect="dark">
                            {{ bug.name }}
                          </el-tag>
                        </template>
                        <el-descriptions>
                          <el-descriptions-item :span="12">
                            <template #label>
                              <span style="font-weight: bold">漏洞名称</span>
                            </template>
                            {{ bug.name }}
                          </el-descriptions-item>
                          <el-descriptions-item :span="12">
                            <template #label>
                              <span style="font-weight: bold">漏洞等级</span>
                            </template>
                            {{ constValue.levels[bug.level] }}
                          </el-descriptions-item>
                          <el-descriptions-item :span="12">
                            <template #label>
                              <span style="font-weight: bold; ">漏洞范围</span>
                            </template>
                            <div
                                style="overflow:hidden; text-overflow:ellipsis;display:-webkit-box; -webkit-box-orient:vertical;-webkit-line-clamp:4;">
                              {{ bug.version.replace("[", "").replace("]", "") }}
                            </div>
                          </el-descriptions-item>
                          <el-descriptions-item :span="6">
                            <template #label>
                              <span style="font-weight: bold">发布时间</span>
                            </template>
                            {{ this.dateFormat(bug.publishedTime, 'yyyy-MM-dd HH:mm:ss') }}
                          </el-descriptions-item>
                          <el-descriptions-item :span="6">
                            <template #label>
                              <span style="font-weight: bold">修改时间</span>
                            </template>
                            {{ this.dateFormat(bug.modifiedTime, 'yyyy-MM-dd HH:mm:ss') }}
                          </el-descriptions-item>
                          <el-descriptions-item :span="12">
                            <template #label>
                              <span style="font-weight: bold">漏洞描述</span>
                            </template>
                            {{ bug.description }}
                          </el-descriptions-item>
                        </el-descriptions>
                      </el-popover>
                    </template>
                  </el-table-column>
                </el-table>
                <div style="margin: 10px 0;">
                  <el-pagination
                      align="center"
                      v-model:currentPage="currentPage4"
                      background
                      v-model:page-size="pageSize4"
                      :page-sizes="[5, 10, 20]"
                      layout="total, sizes, prev, pager, next,jumper"
                      :total="total4"
                      @current-change="loadDependencies"
                  />
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>

        </el-card>
      </el-col>
    </el-row>
  </div>

  <el-dialog
      v-model="codeFileContentDialogVisible"
      title="查看代码文件"
      width="90%"
      top="5%"
  >
    <CodeHighlight :id="codeFileId"/>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="this.codeFileContentDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>


  <el-dialog
      v-model="codeFileComparisionDialogVisible"
      title="对比代码文件"
      width="90%"
      top="5%"
  >
    <CodeHighlightMerge :originId="originId" :comparedId="comparedId"/>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="this.codeFileComparisionDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="codeFilesCloneDialogVisible"
      title="源码文件克隆情况"
      width="80%"
      top="5%"
  >
    <el-alert :title="'检测到本项目源码文件'+showedCodeFileName+'与下列开源代码文件相似'" type="info"
              :closable="false"/>
    <el-table :data="showedOpenSourceFiles" height="600px">
      <el-table-column prop="name" label="文件名称" width="400"/>
      <el-table-column prop="openSourceProjectName" label="来源开源项目名称" width="400"/>
      <el-table-column prop="path" label="文件路径"/>
      <el-table-column prop="lineNumber" label="代码行数" width="240"/>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button @click="showCodeFileComparisionDialog(scope.row.projectFileId,scope.row.id)" round>对比源码
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="this.codeFilesCloneDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="OpenSourceFileCloneDialogVisible"
      title="开源代码文件克隆情况"
      width="80%"
      top="5%"
  >
    <el-alert :title="'检测到开源代码文件'+showedOpenSourceFileName+'与本项目以下代码文件相似'" type="info"
              :closable="false"/>
    <el-table :data="showedCodeFiles" height="600px">
      <el-table-column prop="name" label="文件名称" width="400"/>
      <el-table-column prop="path" label="文件路径"/>
      <el-table-column prop="lineNumber" label="代码行数" width="240"/>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button @click="showCodeFileComparisionDialog(scope.row.id,showedOpenSourceFileID)" round>对比源码
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="this.OpenSourceFileCloneDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="openSourceProjectCloneDialogVisible"
      title="开源项目代码文件克隆情况"
      width="90%"
      top="1%"
  >
    <el-alert :title="'检测到本项目代码文件与开源项目'+showedOpenSourceProjectName+'的下列文件相似'" type="info"
              :closable="false"/>
    <div style="display: flex;justify-content: space-between;padding:5px">
      <div>
      </div>
      <el-form inline>
        <el-form-item label="文件类型">
          <el-select v-model="currentType3" @change="loadCloneOpenSourceFiles">
            <el-option :value="0" label="不论"/>
            <template v-for="(option,index) in this.constValue.types">
              <el-option v-if="index>0" :value="index" :label="option"/>
            </template>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-table v-loading="cloneOpenSourceFilesLoading" :data="showedOpenSourceProjectFiles">
      <el-table-column prop="name" label="文件名称" width="400"/>
      <el-table-column label="文件类型" width="100">
        <template #default="scope">
          {{ constValue.types[scope.row.type] }}
        </template>
      </el-table-column>
      <el-table-column prop="path" label="文件路径"/>
      <el-table-column prop="lineNumber" label="代码行数" width="240"/>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="scope">
          <el-button @click="showOpenSourceFileCloneDialog(scope.row.id,scope.row.name,scope.row.projectCodeFiles)"
                     round>查看相似文件
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin: 10px 0;">
      <el-pagination
          align="center"
          v-model:currentPage="currentPage5"
          background
          v-model:page-size="pageSize5"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next,jumper"
          :total="total5"
          @current-change="loadCloneOpenSourceFiles"
      />
    </div>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="this.openSourceProjectCloneDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="projectCodeFileExplorerDialogVisible"
      title="项目文件管理器"
      width="80%"
      top="5%"
  >
    <div style="display: inline-flex">
      <el-button @click="returnParentDir" style="margin-right: 5px;">
        <el-icon>
          <ArrowLeftBold/>
        </el-icon>
      </el-button>
      <el-button @click="loadProjectFiles" style="margin-right: 15px;">
        <el-icon>
          <Refresh/>
        </el-icon>
      </el-button>
      <el-input v-model="dirPath" readonly style="width: 800px;font-weight: bold;"/>
    </div>
    <el-table :data="sortedProjectFiles" height="600px" empty-text="文件夹为空">
      <el-table-column width="40px">
        <template #default="scope">
          <div v-if="scope.row.isDir">
            <folder-open theme="two-tone" size="28" :fill="['#666' ,'#0D74FF']" :strokeWidth="3"/>
          </div>
          <div v-else>
            <file-code-one theme="filled" size="28" fill="#999" :strokeWidth="3"/>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="文件名称">
        <template #default="scope">
          <el-link v-if="scope.row.isDir" style="font-weight: bold" @click="enterDir(scope.row.name)">
            {{ scope.row.name }}
          </el-link>
          <span v-else>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类型">
        <template #default="scope">
          <span>{{ scope.row.isDir ? '文件夹' : this.constValue.types[scope.row.codeFile.type] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="文件数量">
        <template #default="scope">
          <span>{{ scope.row.isDir ? scope.row.fileCount + '个' : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="代码行数">
        <template #default="scope">
          <span>{{ scope.row.isDir ? '-' : scope.row.codeFile.lineNumber }}</span>
        </template>
      </el-table-column>
      <el-table-column label="克隆情况" width="160">
        <template #default="scope">
          <div v-if="!scope.row.isDir">
            <el-tag v-if="scope.row.codeFile.openSourceFiles.length > 0" round>克隆</el-tag>
            <el-tag type="success" size="default" v-else>原创</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="240px">
        <template #default="scope">
          <div v-if="!scope.row.isDir">
            <el-button v-if="scope.row.codeFile.type!=0" @click="showCodeFileContentDialog(scope.row.codeFile.id)"
                       round>
              查看源码
            </el-button>
            <el-button v-if="scope.row.codeFile.openSourceFiles.length > 0"
                       @click="showCodeFilesCloneDialog(scope.row.codeFile.name,scope.row.codeFile.openSourceFiles)"
                       round>
              克隆情况
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="this.projectCodeFileExplorerDialogVisible = false">关闭</el-button>
          </span>
    </template>
  </el-dialog>
</template>

<script>
import request from "@/utils/request";
import {ElNotification} from 'element-plus'
import {
  Download,
  ArrowLeftBold,
  Refresh
} from '@element-plus/icons-vue'
import * as echarts from "echarts";
import Docxtemplater from '@/components/Docxtemplater'
import CodeHighlight from "@/components/CodeHighlight";
import CodeHighlightMerge from "@/components/CodeHighlightMerge";

var docxx = require("docx-preview");
import {constValue} from "@/assets/js/const.js";
import {exportExcel} from "@/assets/js/ExcelUtils";
import {FolderOpen, FileCodeOne} from '@icon-park/vue-next'

export default {
  name: "CodeProjectView",
  components: {
    Download,
    ArrowLeftBold,
    Refresh,
    echarts,
    Docxtemplater,
    CodeHighlight,
    CodeHighlightMerge,
    constValue,
    FolderOpen,
    FileCodeOne
  },
  data() {
    return {
      constValue: constValue,
      activeName: "overview",
      fileRadio: "文件类型分布",
      pid: 0,
      fileCount: [],
      cloneFileCount: [],
      cloneProjectsOrderByFile: [],
      cloneProjectsOrderByLine: [],
      dependencies: [],
      showedCodeFileName: "",
      showedOpenSourceFiles: [],
      showedOpenSourceFileID: 0,
      showedOpenSourceFileName: "",
      showedCodeFiles: [],
      showedProjectID: 0,
      showedOpenSourceProjectName: "",
      showedOpenSourceProjectFiles: [],
      projectData: {
        totalLineNumber: 0,
        validFileNumber: 0
      },
      bugCounts: [],
      maxHeight: "",
      codeFileContentDialogVisible: false,
      codeFileComparisionDialogVisible: false,
      OpenSourceFileCloneDialogVisible: false,
      codeFilesCloneDialogVisible: false,
      openSourceProjectCloneDialogVisible: false,
      codeFiles: [],
      currentPage1: 1,
      pageSize1: 20,
      total1: 0,
      status: 0,
      totalCodeFileNumber: 0,
      openSourceFiles: [],
      currentPage2: 1,
      pageSize2: 20,
      total2: 0,
      openSourceProjects: [],
      currentPage3: 1,
      pageSize3: 20,
      total3: 0,
      currentPage4: 1,
      pageSize4: 20,
      total4: 0,
      order3: 0,
      currentPage5: 1,
      pageSize5: 20,
      total5: 0,
      codeFileId: 0,
      originId: 0,
      comparedId: 0,
      currentType1: -1,
      currentType2: 0,
      currentType3: 0,
      currentLicenseType: -1,
      documentloading: true,
      cloneOpenSourceFilesLoading: true,
      excelDataLoading1: false,
      excelDataLoading2: false,
      loading: true,
      projectFiles: [],
      projectCodeFileExplorerDialogVisible: false,
      dirPath: "/"
    }
  },
  created() {
    this.pid = this.$route.params.id;
  },
  mounted() {
    this.loadProjectInfo();
  },
  computed: {
    sortedProjectFiles() {
      return this.projectFiles.sort((a, b) => {
        if (a.isDir && b.isDir) {
          return (a.name + '').localeCompare(b.name + '');
        }
        if (a.isDir && !b.isDir) {
          return -1;
        }
        if (!a.isDir && b.isDir) {
          return 1;
        }
        if (!a.isDir && !b.isDir) {
          return (a.name + '').localeCompare(b.name + '');
        }
      });
    }
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
      request.get("/code-project/get/" + this.pid).then(res => {
        if (res.code == "0") {
          this.projectData = res.data;
          this.loading = false;
          window.document.title = this.projectData.name + '-码易审智能代码分析溯源系统';
          this.loadOverview();
          this.loadCodeFiles();
          this.loadOpenSourceFiles();
          this.loadOpenSourceProject();
          this.loadDependencies();
        }
        if (res.code == "600")
          this.$router.push("404");
      })
    },
    /**
     * @description 加载项目文件
     * @return {void}
     */
    loadCodeFiles() {
      request.get("/project-code-file", {
            params: {
              pageNum: this.currentPage1,
              pageSize: this.pageSize1,
              id: this.pid,
              type: this.currentType1,
              status: this.status
            }
          }
      ).then(res => {
        if (res.code == "0") {
          this.codeFiles = res.data.records;
          this.total1 = res.data.total;
          if (this.totalCodeFileNumber == 0 && this.currentType1 == -1)
            this.totalCodeFileNumber = res.data.total;
        } else
          ElNotification({
            title: '加载代码文件列表失败',
            message: res.msg,
            type: 'error',
          });
      })
    },
    /**
     * @description 加载项目总览数据
     * @return {void}
     */
    loadOverview() {
      request.get("/code-project/countFileByType/" + this.pid
      ).then(res => {
        if (res.code == "0") {
          this.fileCount = res.data;
          this.fileCount.forEach((item, index, array) => {
            array[index].name = this.constValue.types[item.name];
          })
          request.get("/code-project/countCloneFileByType/" + this.pid
          ).then(res => {
            if (res.code == "0") {
              this.cloneFileCount = res.data;
              this.cloneFileCount.forEach((item, index, array) => {
                array[index].name = this.constValue.types[item.name];
              })
              this.drawFileChart();
            } else
              ElNotification({
                title: '加载失败',
                message: res.msg,
                type: 'error',
              });
          })
        } else
          ElNotification({
            title: '加载失败',
            message: res.msg,
            type: 'error',
          });
      })
      request.get("open-source-project/getByProjectID", {
            params: {
              pageNum: 1,
              pageSize: 10,
              id: this.pid,
              order: 0
            }
          }
      ).then(res => {
        if (res.code == "0") {
          this.cloneProjectsOrderByFile = res.data.records;
          request.get("open-source-project/getByProjectID", {
                params: {
                  pageNum: 1,
                  pageSize: 10,
                  id: this.pid,
                  order: 1
                }
              }
          ).then(res => {
            if (res.code == "0") {
              this.cloneProjectsOrderByLine = res.data.records;
              this.drawCloneProject();
            } else
              ElNotification({
                title: '加载开源代码列表失败',
                message: res.msg,
                type: 'error',
              });
          })
        } else
          ElNotification({
            title: '加载开源代码列表失败',
            message: res.msg,
            type: 'error',
          });
      })
      request.get("/open-source-library/countBugByLevel/" + this.pid).then(res => {
        if (res.code == "0") {
          this.bugCounts = res.data;
          this.bugCounts.map((value, index, array) => {
            array[index].name = this.constValue.levels[value.name]
          })
          this.drawBugPan();
        } else
          ElNotification({
            title: '加载开源代码列表失败',
            message: res.msg,
            type: 'error',
          });
      })
    },
    /**
     * @description 根据项目ID加载开源项目文件相似匹配记录
     * @return {void}
     */
    loadOpenSourceFiles() {
      request.get("/open-source-file/getByProjectID", {
            params: {
              pageNum: this.currentPage2,
              pageSize: this.pageSize2,
              id: this.pid,
              type: this.currentType2
            }
          }
      ).then(res => {
        if (res.code == "0") {
          this.openSourceFiles = res.data.records;
          this.total2 = res.data.total;
        } else
          ElNotification({
            title: '加载克隆情况失败',
            message: res.msg,
            type: 'error',
          });
      })
    },
    /**
     * @description 根据开源项目ID加载克隆开源项目文件
     * @return {void}
     */
    loadCloneOpenSourceFiles() {
      this.cloneOpenSourceFilesLoading = true;
      request.get("/open-source-file/getByOpenSourceProjectID", {
            params: {
              pageNum: this.currentPage5,
              pageSize: this.pageSize5,
              id: this.showedProjectID,
              type: this.currentType3
            }
          }
      ).then(res => {
        if (res.code == "0") {
          console.log(res.data.records);
          this.showedOpenSourceProjectFiles = res.data.records;
          this.total5 = res.data.total;
          this.cloneOpenSourceFilesLoading = false;
        } else
          ElNotification({
            title: '加载开源项目克隆情况失败',
            message: res.msg,
            type: 'error',
          });
      })
    },
    /**
     * @description 加载开源项目
     * @return {void}
     */
    loadOpenSourceProject() {
      request.get("/open-source-project/getByProjectID", {
            params: {
              pageNum: this.currentPage3,
              pageSize: this.pageSize3,
              id: this.pid,
              license: this.currentLicenseType,
              order: this.order3
            }
          }
      ).then(res => {
        if (res.code == "0") {
          this.openSourceProjects = res.data.records;
          this.total3 = res.data.total;
        } else
          ElNotification({
            title: '加载开源项目列表失败',
            message: res.msg,
            type: 'error',
          });
      })
    },
    /**
     * @description 加载依赖库
     * @return {void}
     */
    loadDependencies() {
      request.get("/open-source-library/getByProjectID", {
        params: {
          pageNum: this.currentPage4,
          pageSize: this.pageSize4,
          id: this.pid,
        }
      }).then(res => {
        if (res.code == "0") {
          this.dependencies = res.data.records;
          this.total4 = res.data.total;
        } else
          ElNotification({
            title: '加载代码文件列表失败',
            message: res.msg,
            type: 'error',
          });
      })
    },
    /**
     * @description 更新源码分析项目信息
     * @return {void}
     */
    save() {
      request.put("/code-project", this.projectData).then(res => {
        if (res.code == "0") {
          ElNotification({
            type: 'success',
            title: "更新成功"
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
     * @description 返回上一页
     * @return {void}
     */
    goBack() {
      this.$router.go(-1);
    },
    /**
     * @description 绘制源码文件分布扇形图
     * @return {void}
     */
    drawFileChart() {
      const chart1 = echarts.init(this.$refs.fileCountBar);
      chart1.setOption({
        title: {
          text: "源码文件统计",
          left: 'center'
        },
        xAxis: {
          type: "category",
          data: this.fileCount.map(e => e.name),
          axisLabel: {
            rotate: 60,
            textStyle: {
              color: '#000',
              fontSize: 10,
              itemSize: ''

            }
          }
        },
        yAxis: {
          type: "value"
        },
        tooltip: {
          trigger: 'item',
        },
        series: [
          {
            type: "bar", //形状为柱状图
            data: this.fileCount.map(e => e.value),
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
        ]
      });
      window.addEventListener("resize", () => {
        chart1.resize();
      });
      const chart2 = echarts.init(this.$refs.fileCountPan);
      chart2.setOption({
        title: {
          text: "源码文件分布",
          left: 'center'
        },
        gird: {
          top: "15%",
          left: "0%",
          right: "0%",
        },
        tooltip: {
          trigger: 'item',
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            label: {
              normal: {
                show: true,
                formatter: '{d}%',
                fontSize: 10
              }

            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            data: this.fileCount
          }
        ]
      });
      window.addEventListener("resize", () => {
        chart2.resize();
      });
      var types = this.fileCount.map(e => e.name), data = new Array();
      for (var type of types) {
        if (type === "其他文件")
          continue;
        var fileCount = this.fileCount.filter(e => e.name == type)[0],
            cloneCount = this.cloneFileCount.filter(e => e.name == type)[0];
        var fileCountNumber = fileCount ? Number(fileCount.value) : 0,
            cloneCountNumber = cloneCount ? Number(cloneCount.value) : 0;
        data.push({name: type, nonCloneCount: fileCountNumber - cloneCountNumber, cloneCount: cloneCountNumber})
      }
      const chart3 = echarts.init(this.$refs.cloneFileCountBar);
      chart3.setOption({
        title: {
          text: "克隆文件占比",
          left: 'center'
        },
        xAxis: {
          type: "category",
          data: data.map(e => e.name),
          axisLabel: {
            rotate: 60,
            textStyle: {
              color: '#000',
              fontSize: 10,
              itemSize: ''

            }
          }
        },
        yAxis: {
          type: "value"
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // Use axis to trigger tooltip
            type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
          }
        },
        legend: {
          top: "10%"
        },
        series: [
          {
            name: '原创文件',
            type: "bar",
            stack: 'total',
            data: data.map(e => e.nonCloneCount),
            barWidth: 25,
            emphasis: {
              focus: 'series'
            },
            itemStyle: {
              normal: {
                label: {
                  show: true,
                  formatter: '{c}',  // 显示数值
                  position: 'top'  // 数值在柱体上方
                }
              }
            }
          },
          {
            name: '克隆文件',
            type: "bar",
            stack: 'total',
            data: data.map(e => e.cloneCount),
            barWidth: 25,
            emphasis: {
              focus: 'series'
            },
            itemStyle: {
              normal: {
                label: {
                  show: true,
                  formatter: '{c}',  // 显示数值
                  position: 'top'  // 数值在柱体上方
                }
              }
            }
          }
        ]
      });
      window.addEventListener("resize", () => {
        chart3.resize();
      });
      const chart4 = echarts.init(this.$refs.cloneFileCountPan);
      chart4.setOption({
        title: {
          text: "克隆文件分布",
          left: 'center'
        },
        gird: {
          top: "15%",
          left: "0%",
          right: "0%",
        },
        tooltip: {
          trigger: 'item',
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            label: {
              normal: {
                show: true,
                formatter: '{d}%',
                fontSize: 10
              }

            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            data: this.cloneFileCount
          }
        ]
      });
      window.addEventListener("resize", () => {
        chart4.resize();
      });
    },
    /**
     * @description 绘制克隆文件分布扇形图
     * @return {void}
     */
    drawCloneProject() {
      const chart1 = echarts.init(this.$refs.cloneProjectBar1);
      chart1.setOption({
        title: {
          text: "按克隆文件数占比统计",
          left: 'center'
        },
        xAxis: {
          type: "category",
          data: this.cloneProjectsOrderByFile.map(e => e.name),
          axisLabel: {
            rotate: 60,
            textStyle: {
              color: '#000',
              fontSize: 10,
              itemSize: ''

            }
          }
        },
        yAxis: {
          type: "value"
        },
        tooltip: {
          trigger: 'item',
        },
        series: [
          {
            type: "bar", //形状为柱状图
            data: this.cloneProjectsOrderByFile.map(e => (e.cloneFileNumber / e.validFileNumber * 100).toFixed(2)),
            barWidth: 25,
          }
        ]
      });
      window.addEventListener("resize", () => {
        chart1.resize();
      });
      const chart2 = echarts.init(this.$refs.cloneProjectBar2);
      chart2.setOption({
        title: {
          text: "按克隆行数占比统计",
          left: 'center'
        },
        xAxis: {
          type: "category",
          data: this.cloneProjectsOrderByLine.map(e => e.name),
          axisLabel: {
            rotate: 60,
            textStyle: {
              color: '#000',
              fontSize: 10,
              itemSize: ''

            }
          }
        },
        yAxis: {
          type: "value"
        },
        tooltip: {
          trigger: 'item',
        },
        series: [
          {
            type: "bar", //形状为柱状图
            data: this.cloneProjectsOrderByLine.map(e => (e.cloneLineNumber / e.totalLineNumber * 100).toFixed(2)),
            barWidth: 25,
          }
        ]
      });
      window.addEventListener("resize", () => {
        chart2.resize();
      });
    },
    /**
     * @description 绘制依赖漏洞分布扇形图
     * @return {void}
     */
    drawBugPan() {
      const chart = echarts.init(this.$refs.bugCountPan);
      chart.setOption({
        title: {
          text: "漏洞分布",
          left: 'center'
        },
        gird: {
          top: "15%",
          left: "0%",
          right: "0%",
        },
        tooltip: {
          trigger: 'item',
        },
        color: ['#580707', '#840606', '#9c1111', '#c15b5b', '#fcd3d3'].reverse(),
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            label: {
              normal: {
                show: true,
                formatter: '{b}:{c}个',
                fontSize: 10
              }

            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            data: this.bugCounts
          }
        ]
      });
      window.addEventListener("resize", () => {
        chart.resize();
      });
    },
    /**
     * @description 根据文件名获取文件
     * @param {String} filename 文件名
     * @return {void}
     */
    downloadFilebyName(filename) {
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
            title: '更新项目信息失败',
            message: res.msg,
            type: 'error',
          });
        });
    },
    /**
     * @description 显示项目文件对话框
     * @param {number} id 项目文件ID
     * @return {void}
     */
    showCodeFileContentDialog(id) {
      this.codeFileId = id;
      this.codeFileContentDialogVisible = true;
    },
    /**
     * @description 显示项目文件与开源项目文件对比对话框
     * @param {number} originId 项目文件ID
     * @param {number} comparedId 开源项目文件ID
     * @return {void}
     */
    showCodeFileComparisionDialog(originId, comparedId) {
      this.originId = originId;
      this.comparedId = comparedId;
      this.codeFileComparisionDialogVisible = true;
    },
    /**
     * @description 显示开源项目文件相关的项目文件对话框
     * @param {number} fileID 开源文件ID
     * @param {String} fileName 开源项目文件文件名
     * @param {Array} codeFiles 项目文件列表
     * @return {void}
     */
    showOpenSourceFileCloneDialog(fileID, fileName, codeFiles) {
      this.showedOpenSourceFileID = fileID;
      this.showedOpenSourceFileName = fileName;
      this.showedCodeFiles = codeFiles;
      this.OpenSourceFileCloneDialogVisible = true;
    },
    /**
     * @description 显示项目文件相关的开源项目文件对话框
     * @param {String} fileName 项目文件文件名
     * @param {Array} openSourceFiles 开源项目文件列表
     * @return {void}
     */
    showCodeFilesCloneDialog(fileName, openSourceFiles) {
      this.showedCodeFileName = fileName;
      this.showedOpenSourceFiles = openSourceFiles;
      this.codeFilesCloneDialogVisible = true;
    },
    /**
     * @description 显示开源项目代码文件克隆情况对话框
     * @param {number} id 项目文件文件名
     * @param {String} projectName 开源项目文件列表
     * @return {void}
     */
    showOpenSourceProjectCloneDialog(id, projectName) {
      this.showedProjectID = id;
      this.showedOpenSourceProjectName = projectName;
      this.openSourceProjectCloneDialogVisible = true;
      this.loadCloneOpenSourceFiles();
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
    },
    /**
     * @description 根据漏洞类型获取颜色
     * @param {number} level 漏洞类型
     * @return {String} 16进制的RGB颜色
     */
    getTagColorByBugLevel(level) {
      const colors = ['#992222', '#c45656', '#f89898', '#fab6b6', '#fcd3d3'].reverse();
      return colors[level];
    },
    /**
     * @description 跳转链接
     * @param {String} url
     * @return {void}
     */
    moveTo(url) {
      window.open(url, "_blank");
    },
    /**
     * @description 将项目源码分析结果导出为EXCEL文件
     * @param {number} status 导出EXCEL的选项
     * @return {void}
     */
    async downloadExcel1(status) {
      this.excelDataLoading1 = true;
      const maxItemNum = 200, pid = this.pid;
      let requestNum = Math.round(this.totalCodeFileNumber / maxItemNum + 0.5);
      var result = [];

      function request1(i) {
        return request.get("/project-code-file", {
              params: {
                pageNum: i,
                pageSize: maxItemNum,
                id: pid,
                status: status
              }
            }
        )
      }

      for (let i = 1; i <= requestNum; i++)
        await request1(i).then(res => {
          if (res.code == "0") {
            result = result.concat(res.data.records.map((value, index, array) => {
              array[index].cloneCount = array[index].openSourceFiles.length;
              return array[index];
            }));
            if (requestNum != res.data.pages)
              requestNum = res.data.pages;
          } else
            ElNotification({
              title: '加载代码文件列表失败',
              message: res.msg,
              type: 'error',
            });
        });
      let titleArr, widthArr;
      if (status == 0) {
        titleArr = {name: "文件名称", path: "文件路径", lineNumber: "代码行数", cloneCount: "相似文件数"};
        widthArr = [240, 400, 240, 240, 120];
      } else {
        titleArr = {name: "文件名称", path: "文件路径", lineNumber: "代码行数"};
        widthArr = [240, 400, 240, 240];
      }
      exportExcel(result, this.projectData.name + '-' + (status == 0 ? '源码文件分析结果表' : (status == 1 ? '原创文件分析结果表' : '相似文件分析结果表')), titleArr, widthArr, status == 0 ? '源码文件分析结果表' : (status == 1 ? '原创文件分析结果表' : '相似文件分析结果表'));
      ElNotification({
        title: '导出Excel成功',
        message: '导出"' + this.projectData.name + '-' + (status == 0 ? '源码文件分析结果表' : (status == 1 ? '原创文件分析结果表' : '相似文件分析结果表')) + '.xlsx"成功',
        type: 'success',
      });
      this.excelDataLoading1 = false;
    },
    /**
     * @description 将相似代码文件匹配记录导出为EXCEL文件
     * @param {number} status 导出EXCEL的选项
     * @return {void}
     */
    async downloadExcel2(status) {
      this.excelDataLoading2 = true;
      const maxItemNum = 200, pid = this.pid;
      let requestNum = Math.round(this.total2 / maxItemNum + 0.5);
      var result = [];

      function request1(i) {
        return request.get("/open-source-file/getByProjectID", {
              params: {
                pageNum: i,
                pageSize: maxItemNum,
                id: pid,
              }
            }
        )
      }

      for (let i = 1; i <= requestNum; i++)
        await request1(i).then(res => {
          if (res.code == "0") {
            result = result.concat(res.data.records);
            requestNum = res.data.pages;
          } else
            ElNotification({
              title: '加载相似文件匹配记录失败',
              message: res.msg,
              type: 'error',
            });
        });
      const titleArr = {
        projectFileName: "相似文件名",
        projectFilePath: "相似文件路径",
        openSourceProjectName: "开源项目名称",
        name: "文件名称",
        path: "文件路径",
        lineNumber: "相似代码行数"
      }, widthArr = [240, 400, 240, 240, 400, 120]
      if (status == 1) {
        var newArr = [];
        var arrId = [];
        for (var item of result) {
          if (arrId.indexOf(item['projectFilePath']) == -1) {
            arrId.push(item['projectFilePath']);
            newArr.push(item);
          }
        }
        result = newArr;
      }
      exportExcel(result, this.projectData.name + '相似文件匹配结果表', titleArr, widthArr, '相似文件匹配结果');
      ElNotification({
        title: '导出Excel成功',
        message: '导出项目相似文件匹配结果成功',
        type: 'success',
      });
      this.excelDataLoading2 = false;
    },
    /**
     * @description 显示项目文件管理器对话框
     * @param {number} status 导出EXCEL的选项
     * @return {void}
     */
    showProjectCodeFileExplorerDialog() {
      this.projectCodeFileExplorerDialogVisible = true;
      this.loadProjectFiles();
    },
    /**
     * @description 加载当前路径项目文件
     * @param {number} status 导出EXCEL的选项
     * @return {void}
     */
    loadProjectFiles() {
      request.get("/files/getDirFiles", {
        params: {
          pid: this.pid,
          dirPath: this.dirPath,
        }
      }).then(res => {
        if (res.code == "0") {
          console.log(res.data)
          this.projectFiles = res.data;
        } else
          ElNotification({
            title: '加载项目目录' + this.dirPath + '失败',
            message: res.msg,
            type: 'error',
          });
      })
    },
    /**
     * @description 加载当前路径项目文件
     * @param {String} dirName 文件夹名
     * @return {void}
     */
    enterDir(dirName) {
      this.dirPath = this.dirPath + '/' + dirName;
      this.dirPath = this.dirPath.replace("//", "/");
      this.loadProjectFiles();
    },
    /**
     * @description 返回父文件夹
     * @return {void}
     */
    returnParentDir() {
      if (this.dirPath !== "/") {
        this.dirPath = this.dirPath.substring(0, this.dirPath.lastIndexOf("/"));
        if (this.dirPath.length == 0)
          this.dirPath = "/";
      }
      this.loadProjectFiles();
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

.overview-label {
  font-weight: bold;
  font-size: 20px;
  margin: 25px 0px;
  padding-left: 15px;
  border-left: 5px solid rgb(19, 119, 255);
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