<!--
* @FileDescription: 文档导出组件
-->
<template>
    <el-container style="height:100vh;overflow: hidden;">
        <el-header style="cursor:pointer;background-color:white;height:64px;width: 100%;display: flex;justify-content: space-between;align-items: center;padding-left: 15px;padding-right: 15px;box-shadow: 0px 1px 12px 1px lightgray;z-index: 101;">
            <div style="display: flex;justify-content: space-between;align-items: center;" @click="this.$router.push('/hello')">
                <el-image style="width: 32px; height: 32px;padding:2px;" :src="require('@/assets/icon/favicon.png')" fit="fill" />
                <span class="linear-gradient-text" style="text-align: center;line-height: 50px;font-size:20px;font-weight: bold;margin-left: 5px;margin-right: 5px;">码易审</span><div style="font-weight: bold;">智能代码分析溯源系统</div>
            </div>
            <div v-if="!settings.isVerticalMenu"  style="height: 50px;display: flex;padding:5px;">
                <HeaderMenu/>
            </div>
            <div class="toolbar" style="height: 50px;display: flex;padding:5px;">
                <div>
                    <el-tooltip :content="fullscreen?'退出全屏':'全屏'" placement="bottom" effect="light">
                        <el-button text round type="default" size="large" style="width:40px;" @click="screen"><el-icon size="large"><FullScreen /></el-icon></el-button>
                    </el-tooltip>
                    <el-tooltip content="设置" placement="bottom" effect="light">
                        <el-button text round type="default" size="large" style="width:40px;" @click="settingDrawerVisible = true"><el-icon size="large"><Setting /></el-icon></el-button>
                    </el-tooltip>
                </div>
                <el-dropdown style="height: 40px;margin-left: 15px;">
                    <span style="line-height: 40px;font-size:16px;cursor: pointer;"><el-icon style="font-size:18px;vertical-align: middle"><Avatar /></el-icon><span style="vertical-align: middle" >&nbsp;{{user.name}}&nbsp;</span><el-icon style="font-size:16px;vertical-align: middle"><CaretBottom /></el-icon></span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item><el-button link @click="edit"> <el-icon><Tools /></el-icon>修改密码</el-button></el-dropdown-item>
                            <el-dropdown-item><el-button link @click="exit"> <el-icon><SwitchButton /></el-icon>退出登录</el-button></el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </el-header>
        <el-container>
            <el-aside v-if="settings.isVerticalMenu" style="width: fit-content;">
                <AsideMenu :isCollapse="isCollapse"/>
            </el-aside>
            <el-main style="width:100%;height:calc(100vh - 50px);min-width:1600px;background-color: rgb(245,245,245);padding: 0;">
                <el-scrollbar>
                    <router-view />
                </el-scrollbar>
            </el-main>
        </el-container>
    </el-container>
    <el-drawer
            v-model="settingDrawerVisible"
            title="网站设置"
    >
        <el-form :model="settings" label-width="120px">
            <el-form-item label="菜单栏位置" prop="pwd" size="large">
                <el-switch
                        v-model="settings.isVerticalMenu"
                        active-text="垂直菜单"
                        inactive-text="水平菜单"
                />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="settingDrawerVisible = false">关闭</el-button>
        </template>
    </el-drawer >
    <el-drawer
            v-model="pwdDrawerVisible"
            title="修改密码"
            :before-close="handleClose"
    >
        <el-form ref="AsideRuleFormRef" :model="form" :rules="rules" label-width="120px" status-icon>
            <el-form-item label="密码" prop="pwd">
                <el-input type="password" v-model="form.pwd" maxlength="20" style="width: 80%" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="check_pwd">
                <el-input type="password" v-model="form.check_pwd" maxlength="20" style="width: 80%" show-password></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="handleClose">取消</el-button>
            <el-button type="primary" @click="save">确认</el-button>
        </template>
    </el-drawer >
</template>

<script>
    import { Setting,SwitchButton,Tools,CaretBottom,Bell,Avatar,FullScreen} from '@element-plus/icons-vue'
    import AsideMenu from "@/components/admin/AsideMenu";
    import HeaderMenu from "@/components/admin/HeaderMenu";
    import md5 from "js-md5";
    import request from "@/utils/request";
    import {ElMessage} from "element-plus";

    export default {
        name: "AdminLayout",
        components: {
            AsideMenu,HeaderMenu,Setting,SwitchButton,Tools,CaretBottom,Bell,Avatar,FullScreen
        },
        data(){
            return {
                user:localStorage.getItem("user")?JSON.parse(localStorage.getItem("user")):{name:"未知用户"},
                settings:{
                    isVerticalMenu:true
                },
                isCollapse:false,
                form:{},
                settingDrawerVisible:false,
                pwdDrawerVisible:false,
                fullscreen: false,
                rules:{
                    pwd:[
                        {required:true, min: 8, max: 20, message:"请检查密码，长度必须为8-20字符！",trigger:"blur"}
                    ],
                    check_pwd:[
                        {required:true, validator: this.validatePass,trigger:"change"}
                    ],
                },
            }
        },
        created() {
            if(localStorage.getItem("user")){
                var info = JSON.parse(localStorage.getItem("user"));
                var date = new Date(info.date),today = new Date();
                var diff = (today - date) / 1000 / 3600 / 24;
                if(diff>7) {
                    localStorage.removeItem("user");
                }
            }
            console.log(localStorage.getItem("settings"))
            if(localStorage.getItem("settings")){
                console.log(JSON.parse(localStorage.getItem("settings")));
                this.settings = JSON.parse(localStorage.getItem("settings"));
            }
        },
        watch:{
            settings: {
                handler: (val, olVal) => {
                    localStorage.setItem("settings",JSON.stringify(val));
                },
                deep: true
            }
        },
        methods:{
            edit(){
                this.pwdDrawerVisible = true;
            },
            save(){
                this.$refs.AsideRuleFormRef.validate((valid, fields) => {
                    if(valid) {
                        this.form.password = md5(this.form.pwd);
                        this.form.pwd="";
                        this.form.check_pwd = "";
                        request.put("/user",this.form).then(res => {
                            if(res.code == "0") {
                                ElMessage({
                                    type:'success',
                                    message:"更新成功"
                                });
                                localStorage.setItem("user", JSON.stringify(res.data))
                                this.pwdDrawerVisible = false;
                            }else{
                                ElMessage.error("更新错误");
                            }
                        });
                    }else{
                        ElMessage.error("请填写完成");
                    }
                });
            },
            handleClose(){
                this.pwdDrawerVisible = false;
                this.form = {};
                this.$refs.AsideRuleFormRef.resetFields();
            },
            exit(){
                localStorage.removeItem("user");
                this.$router.push("/login");
            },
            validatePass(rule, value, callback){
                if (value === ''|| value.length==0) {
                    callback(new Error("请再次输入密码"))
                } else if (value !== this.form.pwd) {
                    callback(new Error("两次输入不一致！"))
                } else {
                    callback()
                }
            },
            screen() {
                let element = document.documentElement;
                if (this.fullscreen) {
                    if (document.exitFullscreen) {
                        document.exitFullscreen();
                    } else if (document.webkitCancelFullScreen) {
                        document.webkitCancelFullScreen();
                    } else if (document.mozCancelFullScreen) {
                        document.mozCancelFullScreen();
                    } else if (document.msExitFullscreen) {
                        document.msExitFullscreen();
                    }
                } else {
                    if (element.requestFullscreen) {
                        element.requestFullscreen();
                    } else if (element.webkitRequestFullScreen) {
                        element.webkitRequestFullScreen();
                    } else if (element.mozRequestFullScreen) {
                        element.mozRequestFullScreen();
                    } else if (element.msRequestFullscreen) {
                        // IE11
                        element.msRequestFullscreen();
                    }
                }
                this.fullscreen = !this.fullscreen;
            },
        }
    }
</script>

<style scoped>
    .linear-gradient-text{
        background-image: -webkit-linear-gradient(bottom, rgb(84,111,197), rgb(0,141,255), rgb(104,194,59));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
</style>