<!--
* @FileDescription: 正在建设页面提示
-->
<template>
  <el-card class="warning-card" shadow="always">
    <div class="warning-title">
      正在建设中
    </div>
    <div class="warning-content">抱歉，您所访问的页面正在建设中，给您造成不便请原谅。</div>
    <el-button type="primary" size="large" @click="back" round>返回上一页</el-button>
  </el-card>
</template>

<script>
export default {
  name: "BuildingView",
  methods: {
    /**
     * @description 返回上一页
     * @return {void}
     */
    back() {
      this.$router.go(-1);
    }
  }
}
</script>

<style scoped>
.warning-card {
  margin: 25px auto;
  width: 800px;
  height: 600px;
  border-radius: 15px;
  text-align: center;
}

.warning-title {
  font-size: 32px;
  font-weight: bold;
  height: 64px;
  padding: 15px;
  border-bottom: 1px solid lightgray;
}

.warning-content {
  height: 400px;
  padding: 50px
}
</style>