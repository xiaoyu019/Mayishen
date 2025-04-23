<!--
* @FileDescription: 文档导出组件
-->
<template>
  <el-button :loading="loading" @click="renderDoc" size="large" round>
    <el-icon>
      <Download/>
    </el-icon>&nbsp;导出报告
  </el-button>
</template>
<script>
import Docxtemplater from 'docxtemplater'
import PizZip from 'pizzip'
import PizZipUtils from 'pizzip/utils/index.js'
import {saveAs} from 'file-saver'
import {Download} from '@element-plus/icons-vue'
import request from "@/utils/request";
import {ElNotification} from "element-plus";

function loadFile(url, callback) {
  PizZipUtils.getBinaryContent(url, callback)
}

export default {
  name: "Docxtemplater",
  props: ['id'],
  components: {Download},
  data() {
    return {
      loading: false,
    }
  },
  methods: {
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
     * @description 替换错误
     * @param {Obejct} key 键
     * @param {Obejct} value 值
     * @return {Obejct} 值
     */
    replaceErrors(key, value) {
      if (value instanceof Error) {
        return Object.getOwnPropertyNames(value).reduce(function (error, key) {
          error[key] = value[key]
          return error
        }, {})
      }
      return value
    },
    /**
     * @description 错误处理器
     * @param {Obejct} error 错误
     * @throw {Obejct} error
     */
    errorHandler(error) {
      console.log(JSON.stringify({error: error}, this.replaceErrors()))
      if (error.properties && error.properties.errors instanceof Array) {
        const errorMessages = error.properties.errors
            .map(function (error) {
              return error.properties.explanation
            })
            .join('\n')
        console.log('errorMessages', errorMessages)
        // errorMessages is a humanly readable message looking like this :
        // 'The tag beginning with "foobar" is unopened'
      }
      throw error
    },
    /**
     * @description 渲染Word文档
     * @return {void}
     */
    renderDoc() {
      this.loading = true;
      const that = this;
      Date.prototype.format = function (fmt) {
        var o = {
          "M+": this.getMonth() + 1, //月份
          "d+": this.getDate(), //日
          "H+": this.getHours(), //小时
          "m+": this.getMinutes(), //分
          "s+": this.getSeconds(), //秒
          "q+": Math.floor((this.getMonth() + 3) / 3), //季度
          "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
          if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
      }
      // 解决异步参数传递问题
      const _this = this;
      loadFile('/template.docx', function (
          error, content) {
        if (error) {
          throw error
        }
        var zip = new PizZip(content)
        var doc = new Docxtemplater().loadZip(zip)
        request.get("/cost-project/get/" + _this.id).then(res => {
          if (res.code == "0") {
            var data = res.data;
            request.get("function/getByProjectID/" + _this.id).then(res => {
              if (res.code == "0") {
                data.functions = res.data;
                const Area2PDR = [6.72, 10.96, 10.83, 8.05, 7.01, 7.56];
                const scales = [[35, 15, 0, 0, 0], [10, 7, 4, 5, 4]];
                var user = JSON.parse(localStorage.getItem("user"));
                data.userName = user.name;
                data.outputDate = new Date().format("yyyy-MM-dd HH:mm:ss");
                data.totalLabourCost = _this.moneyFormat(data.totalLabourCost);
                data.totalNonlabourCost = _this.moneyFormat(data.totalNonlabourCost);
                data.totalCost = _this.moneyFormat(data.totalCost);
                data.PDR = Area2PDR[data.area]
                data.functions.forEach((value, index, array) => {
                  array[index].scale = scales[data.scaleRadio][value.type];
                  if (value.type == 0) {
                    array[index].type = "ILF";
                    return;
                  }
                  if (value.type == 1) {
                    array[index].type = "EIF";
                    return;
                  }
                  if (value.type == 2) {
                    array[index].type = "EI";
                    return;
                  }
                  if (value.type == 3) {
                    array[index].type = "EO";
                    return;
                  }
                  if (value.type == 4)
                    array[index].type = "EQ";
                  else
                    array[index].type1 = "error type";
                });
                data.scaleRadio = data.scaleRadio == 0;
                data.totalFactor = (data.appFactor * data.integrityFactor * data.nonFunctionFactor * data.platformFactor * data.backgroundFactor).toFixed(2);
                data.costRadio = data.costRadio == 0;
                data.averageLabourCostRate = data.labourCostRate;
                data.effortDistribution.forEach((value, index, array) => {
                  array[index].effort = array[index].ratio / 100.0 * data.totalAdaptedEffort;
                  array[index].cost = _this.moneyFormat(array[index].effort * array[index].labourCostRate);
                });
                doc.setData(data);
                try {
                  // render the document (replace all occurences of {first_name} by John, {last_name} by Doe, ...)
                  doc.render();
                } catch (error) {
                  // The error thrown here contains additional information when logged with JSON.stringify (it contains a properties object containing all suberrors).
                  this.errorHandler(error)
                }
                var out = doc.getZip().generate({
                  type: 'blob',
                  mimeType:
                      'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
                }) // Output the document using Data-URI
                saveAs(out, data.name + '_成本分析报告.docx');
                that.loading = false;
              }
            })
          } else {
            ElNotification({
              title: '加载项目信息失败',
              message: res.msg,
              type: 'error',
            });
          }
        })
      })
    }
  }
}
</script>
