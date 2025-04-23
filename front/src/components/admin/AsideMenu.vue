<!--
* @FileDescription: 边栏菜单
-->
<template>
  <el-menu class="el-menu-vertical" :collapse="isCollapse" :default-active="this.$route.path" background-color="#FFFFF"
           active-text-color="#0D74FF" text-color="#444444"
           style="max-width: 240px;height:100vh;overflow: hidden;border: none;z-index: 100;padding-top: 15px;" router>
    <div style="height: calc(100vh - 120px); -webkit-user-select:none; user-select:none; ">
      <el-menu-item index="/hello">
        <el-icon>
          <Workbench theme="multi-color" size="16" :fill="['#999' ,'#0D74FF' ,'#FFF' ,'#43CCF8']"/>
        </el-icon>
        <template #title> 首页</template>
      </el-menu-item>
      <el-menu-item index="/cost-projects">
        <el-icon>
          <paper-money theme="multi-color" size="16" :fill="['#999' ,'#0D74FF' ,'#FFF' ,'#43CCF8']"/>
        </el-icon>
        <template #title> 成本评估</template>
      </el-menu-item>
      <el-menu-item index="/code-projects">
        <el-icon>
          <source-code theme="multi-color" size="16" :fill="['#999' ,'#0D74FF' ,'#FFF' ,'#43CCF8']"/>
        </el-icon>
        <template #title> 源码分析</template>
      </el-menu-item>
      <el-menu-item v-if="user.role == 0" index="/openSourceCodes">
        <el-icon>
          <seo-folder theme="multi-color" size="16" :fill="['#999' ,'#0D74FF' ,'#FFF' ,'#43CCF8']"/>
        </el-icon>
        <template #title> 开源代码库</template>
      </el-menu-item>
      <el-menu-item v-if="user.role == 0" index="/users">
        <el-icon>
          <peoples theme="multi-color" size="24" :fill="['#999' ,'#0D74FF' ,'#FFF' ,'#43CCF8']"/>
        </el-icon>
        <template #title> 用户管理</template>
      </el-menu-item>
    </div>
    <el-button @click="isCollapse = !isCollapse" link style="width: 100%;height:28px;">
      <el-icon :size="16">
        <Fold v-if="!isCollapse"/>
        <Expand v-else/>
      </el-icon>
    </el-button>
  </el-menu>

</template>

<script>
import {
  Expand,
  Fold,
} from '@element-plus/icons-vue'
import {PaperMoney, SourceCode, SeoFolder, HomeTwo, Peoples, Workbench} from '@icon-park/vue-next';

export default {
  name: "AsideMenu",
  components: {
    Expand,
    Fold,
    PaperMoney,
    SourceCode,
    SeoFolder,
    HomeTwo,
    Peoples,
    Workbench
  },
  props: {
    isCollapse: Boolean
  },
  created() {
    this.isKeyAdmin = this.user.role == 0;
  },
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {name: "未知用户"},
      isKeyAdmin: false,
    }
  },
  methods: {}
}
</script>

<style scoped>
li.el-menu-item {
  font-size: 1rem;
  border-radius: 5px;
  margin: 10px 10px 15px 10px;
  padding: 5px;
  height: 40px;
}

li.el-sub-menu {
  height: 40px;
}

::v-deep .el-sub-menu__title {
  font-size: 1rem;
  border-radius: 5px;
  margin: 10px 10px 15px 10px;
  padding: 5px;
  height: 40px;
}

li.el-menu-item.is-active {
  font-size: 1rem;
  background-color: rgb(236, 245, 255);
  /*color:white;*/
  font-weight: bolder;
  border-radius: 5px;
  margin: 10px 10px 15px 10px;
  padding: 5px;
  height: 40px;
}

li.el-menu-item:hover {
  background-color: rgb(236, 245, 255);
  color: #0D74FF;
}

.el-menu-vertical {
  width: 100px;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 240px;
}
</style>