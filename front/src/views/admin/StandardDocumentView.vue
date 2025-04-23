<!--
* @FileDescription: 标准下载页
-->
<template>
  <div class="card-wrapper">
    <div class="header-title">
      规范与标准
    </div>
    <el-card shadow="always" ref="element" style="border-radius: 15px;">
      <el-table :data="documents">
        <el-table-column>
          <template #default="scope">
            <div style="padding: 10px;">
              <span style="font-size: 16px;font-weight: bold;">{{ scope.row.title }}</span>
              <el-link @click="download(scope.row)"> [下载原文]</el-link>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "StandardDocumentView",
  data() {
    return {
      documents: [
        {
          title: "GB/T 36964-2018 《软件工程 软件开发成本度量规范》",
          url: require("@/assets/documents/GBT36964-2018.pdf")
        },
        {
          title: "ISO/IEC 24570:2018 《软件工程 NESMA功能规模测量方法 使用功能点分析的定义和统计准则》",
          url: require("@/assets/documents/ISO-IEC 24570-2018.pdf")
        },
        {
          title: "SJ/T 11619-2016 《软件工程 NESMA功能规模测量方法版本2.1 使用功能点分析的定义和统计准则》",
          url: require("@/assets/documents/SJT11619-2016.pdf")
        },
        {
          title: "CSBMK®-202210 《2022年中国软件行业基准数据》",
          url: require("@/assets/documents/CSBMK-2022年.pdf")},
        {
          title: "T/BSCEA 002-2023 《软件造价评估实施规程》",
          url: require("@/assets/documents/TBSCEA002-2019.pdf")},
      ]
    }
  },
  methods: {
    /**
      * @description 下载标准PDF文件
      * @param {Object} data 数据
      * @return {void}
      */
    download(data) {
      let a = document.createElement('a');
      document.body.appendChild(a);
      a.href = data.url.default;
      a.download = data.title + '.pdf';
      a.style.display = 'none';
      a.click();
      document.body.removeChild(a);
    }
  }
}
</script>

<style scoped>
.card-wrapper {
  padding: 25px;
  width: 100%;
  height: 100%;
}

.header-title {
  font-weight: bold;
  font-size: 24px;
  margin-bottom: 15px;
}
</style>