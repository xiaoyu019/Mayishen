<!--
* @FileDescription: 代码文件比较组件
-->
<template>
  <div v-loading="codeLoading" element-loading-text="加载源码文件中，请稍等..."
       element-loading-background="rgb(0, 0, 0)">
    <el-row>
      <el-col :span="12" style="text-align: center;font-size: 20px;padding: 12px;font-weight: bold;">
        本项目源码文件
      </el-col>
      <el-col :span="12" style="text-align: center;font-size: 20px;padding: 12px;font-weight: bold;">
        开源项目源码文件
      </el-col>
    </el-row>
    <div id="target" class="exercise">
    </div>
  </div>
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
  name: "CodeHighlightMerge",
  props: {
    originId: {
      type: Number
    },
    comparedId: {
      type: Number
    },
  },
  components: {Codemirror},
  data() {
    return {
      codeLoading: true,
      loading1: false,
      loading2: false,
      originCode: "",
      comparedCode: "",
      type: 0,
      modes: ["text/default", "text/clike", "text/css", "text/javascript", "text/javascript", "text/xml", "text/html", "text/vue", "text/html", "text/xml", "text/php", "text/python"]
    }
  },
  computed: {
    loading() {
      return this.loading1 || this.loading2;
    }
  },
  watch: {
    loading: {
      handler(newValue, oldValue) {
        if (!newValue) {
          CodeMirror.MergeView(
              target, {
                autorefresh: true,
                mode: this.modes[this.type],
                theme: "ayu-mirage",
                readOnly: true, // 只读
                value: this.comparedCode,
                origLeft: null,
                orig: this.originCode,
                lineNumbers: true,
                collapseIdentical: false,
                highlightDifferences: true,
                connect: 'align',
                styleActiveLine: true,
                viewportMargin: Infinity,
              }
          );
          setTimeout(this.codeLoading = false, 500)
        } else
          this.codeLoading = true;
      }
    },
    originId: {
      handler(newValue, oldValue) {
        if (newValue > 0) {
          this.originCode = "";
          this.loading1 = true;
          request.get("project-code-file/getCodeFile/" + newValue).then(res => {
            if (res.code == "0") {
              let target = document.getElementById("target");
              target.innerHTML = "";
              this.originCode = res.data.codes.join("\n");
              this.type = res.data.type;
              this.loading1 = false;
            }else{
              let target = document.getElementById("target");
              target.innerHTML = "";
              this.originCode = "<后端错误>";
              this.type = 1;
              this.loading1 = false;
            }
          })
        }
      },
      immediate: true
    },
    comparedId: {
      handler(newValue, oldValue) {
        if (newValue > 0) {
          this.comparedCode = "";
          this.loading2 = true;
          request.get("open-source-file/getCodeFile/" + newValue).then(res => {
            if (res.code == "0") {
              let target = document.getElementById("target");
              target.innerHTML = "";
              this.comparedCode = res.data.codes.join("\n");
              this.loading2 = false;
            }else{
              let target = document.getElementById("target");
              target.innerHTML = "";
              this.comparedCode = "<后端错误>";
              this.type = 1;
              this.loading2 = false;
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
  width: 100%;
}

::v-deep .CodeMirror {
  height: 70vh;
}

::v-deep .CodeMirror-merge-right {
  left: 0px
}

::v-deep .CodeMirror-merge-gap {
  height: 70vh;
  left: 47%;
}

::v-deep .CodeMirror-merge-editor {
  position: absolute;
  right: 0;
}

::v-deep .CodeMirror-merge-copy {
  transform: rotateY(180deg);
}
</style>