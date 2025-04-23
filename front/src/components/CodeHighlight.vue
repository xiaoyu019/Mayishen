<!--
* @FileDescription: 代码文件显示组件
-->
<template>
  <el-container v-loading="loading" element-loading-text="加载文件中..." class="exercise">
    <codemirror v-model:value="codeSnippets" :options="cmOptions" style="height: 70vh"/>
  </el-container>
</template>

<script>
// 编辑器代码格式
import "codemirror/mode/javascript/javascript.js";
import "codemirror/mode/clike/clike.js";
import "codemirror/mode/css/css.js";
import "codemirror/mode/vue/vue.js";
import "codemirror/mode/xml/xml.js";
import "codemirror/mode/htmlmixed/htmlmixed.js";
import "codemirror/mode/php/php.js";
import "codemirror/mode/python/python.js";
// 自动刷新(防止codemirror需要手动刷新才出数据)
import "codemirror/addon/display/autorefresh";
// 主题
import "codemirror/theme/ayu-mirage.css";
import Codemirror from "codemirror-editor-vue3";
import request from "@/utils/request";

export default {
  name: "CodeHighlight",
  props: {
    id: {
      type: Number
    }
  },
  components: {Codemirror},
  data() {
    return {
      loading: true,
      codeSnippets: "",
      cmOptions: {
        autorefresh: true,
        tabSize: 4,
        mode: "text/clike",
        theme: "ayu-mirage",
        line: true,
        viewportMargin: Infinity, //处理高度自适应时搭配使用
        highlightDifferences: true,
        autofocus: false,
        indentUnit: 2,
        smartIndent: true,
        readOnly: true, // 只读
        showCursorWhenSelecting: true,
        firstLineNumber: 1,
      },
      modes: ["text/default", "text/clike", "text/css", "text/javascript", "text/javascript", "text/xml", "text/html", "text/vue", "text/html", "text/xml", "text/php", "text/python"]
    }
  },
  watch: {
    id: {
      handler(newValue) {
        if (newValue > 0) {
          this.loading = true;
          request.get("project-code-file/getCodeFile/" + newValue).then(res => {
            if (res.code == "0") {
              this.codeSnippets = res.data.codes.join("\n");
              this.cmOptions.mode = this.modes[res.data.type];
              setTimeout(this.loading = false, 500);
            }
          })
        }
      },
      immediate: true
    }
  }
}
</script>

<style scoped>
.exercise {
  height: 70vh;
  direction: ltr;
}
</style>