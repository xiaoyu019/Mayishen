<!--
* @FileDescription: 顶栏菜单
-->
<template>
    <el-menu mode="horizontal" :default-active="this.$route.path" background-color="#FFFFF" active-text-color="#0D74FF" text-color="#444444" style="width: 600px;font-size: 20px;border: none;" router>
        <el-menu-item index="/hello">
            <template #title> 首页</template>
        </el-menu-item>
        <el-menu-item index="/cost-projects/">
            <template #title> 成本评估</template>
        </el-menu-item>
        <el-menu-item index="/code-projects/">
            <template #title> 源码分析</template>
        </el-menu-item>
        <el-menu-item v-if="user.role == 0" index="/openSourceCodes">
            <template #title> 开源代码</template>
        </el-menu-item>
        <el-menu-item v-if="user.role == 0" index="/users">
            <template #title> 用户管理</template>
        </el-menu-item>
    </el-menu>

</template>

<script>
    export default {
        name: "HeaderMenu",
        components:{
        },
        created() {
            this.isKeyAdmin = this.user.role==0;
        },
        data(){
            return {
                user:localStorage.getItem("user")?JSON.parse(localStorage.getItem("user")):{name:"未知用户"},
                isKeyAdmin:false,
            }
        }
    }
</script>

<style scoped>
    .el-menu--horizontal li.el-menu-item:not(.is-disabled):focus,li.el-menu-item:not(.is-disabled):hover{
        background-color:white;
    }
</style>