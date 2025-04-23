<!--
* @FileDescription: 登录页面
-->
<template>
  <div class="login_pane">
    <!-- 登录页面背景 -->
    <div class="pane_background_wrapper">
      <div class="pane_background">
        <div style="width: 160vw;height: 80vw;border-radius: 150px;float: left;margin:5vw;box-shadow: 5px 5px 100px 1px rgba(47,117,182,.2);background-color: rgb(47,117,182)"></div>
        <div style="width: 80vw;height: 80vw;border-radius: 150px;float: left;margin:5vw;box-shadow: 5px 5px 100px 10px rgba(51,141,205,.2);background-color: rgb(51,141,205)"></div>
        <div style="width: 160vw;height: 80vw;background-color: white;border-radius: 150px;float: left;margin:5vw 5vw 5vw 20vw;box-shadow: 5px 5px 100px 10px lightgray;"></div>
        <div style="width: 80vw;height: 80vw;border-radius: 150px;float: left;margin:5vw;box-shadow: 5px 5px 100px 10px rgba(237,125,49,.2);background-color: rgb(237,125,49)"></div>
      </div>
    </div>
    <!-- 登录对话框 -->
    <div class="login_dialogue_wrapper">
      <el-row class="login_dialogue">
        <el-col class="dialogue_left" :span="16">
          <el-image style="width: 100%; height: 100%;" :src="require('@/assets/img/bg.png')" fit="cover"/>
        </el-col>
        <el-col class="dialogue_right" :span="8">
          <div class="login_form">
            <div class="form_title">
              登录你的账户
            </div>
            <el-divider>
              <span style="font-size: 1rem;">账号密码登陆</span>
            </el-divider>
            <el-form :model="form" ref="form" :rules="rules" style="padding:5px;">
              <el-form-item prop="username">
                <el-input placeholder="请输入用户名" v-model="form.username" :readonlu="loading">
                  <template #prefix>
                    <el-icon class="el-input__icon">
                      <Avatar/>
                    </el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item prop="pwd">
                <el-input type="password" placeholder="请输入密码" show-password  v-model="form.pwd"  @keyup.enter.native="login" :readonlu="loading">
                  <template #prefix>
                    <el-icon class="el-input__icon">
                      <Lock/>
                    </el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button class="login_btn" type="primary" @click="login" :loading="loading" round>登 陆
                </el-button>
              </el-form-item>
              <div style="text-align: center;">
                <el-link>忘记密码</el-link>
              </div>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import {Avatar, Lock} from '@element-plus/icons-vue'
import {ElNotification} from "element-plus";
import request from "@/utils/request";
import md5 from 'js-md5';

export default {
  name: "LoginView",
  components: {
    Avatar, Lock
  },
  data() {
    return {
      loading: false,
      form: {},
      rules: {
        username: [
          {required: true, message: "请输入用户名", trigger: "blur"}
        ],
        pwd: [
          {required: true, message: "请输入密码", trigger: "blur"}
        ],
      }
    }
  },
  beforeCreate() {
    if (JSON.parse(localStorage.getItem("user"))) {
      this.$router.go(-1);
    }
  },
  methods: {
    /**
     * @description 登录方法
     * @return void
     */
    login() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.loading = true;
          this.form.password = md5(this.form.pwd);
          let formData = JSON.parse(JSON.stringify(this.form));
          delete formData.pwd;
          request.post('/user/login', formData).then(res => {
            if (res.code == "0") {
              ElNotification({
                title: '登陆成功',
                type: 'success',
                duration: 1500
              });
              var user = res.data;
              user.date = new Date();
              localStorage.setItem("user", JSON.stringify(user))
              setTimeout(this.loginSuccess, 1000);
            } else {
              this.loading = false;
              ElNotification({
                title: '登陆失败',
                message: res.msg,
                type: 'error',
              });
            }
          })
        } else {
          return false;
        }
      })
    },
    /**
     * @description 登陆成功回调方法
     * @return void
     */
    loginSuccess() {
      this.loading = false;
      this.$router.push(this.$route.query.redirect ? this.$route.query.redirect : "/hello");
    }
  }
}
</script>

<style scoped>
.login_pane {
  background-color: rgb(240, 240, 240);
  display: flex;
  height: 100vh;
  width: 100vw;
  min-width: 1200px;
  min-height: 800px;
}

.pane_background_wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  min-width: 1200px;
  min-height: 800px;
}

.pane_background {
  position: relative;
  top: -80vw;
  left: -90vw;
  width: 300vw;
  height: 200vw;
  overflow: hidden;
  transform: rotate(45deg);
}

.login_dialogue_wrapper {
  margin-top: calc(50vh - 300px);
  margin-left: calc(50vw - 700px);
  width: 1200px;
  height: 600px;
  box-shadow: 5px 5px 100px 10px rgb(200, 200, 200);
  border-radius: 25px;
  z-index: 10;
}

.login_dialogue {
  background-color: white;
  width: 100%;
  height: 100%;
  border-radius: 25px;
}

.dialogue_left {
  overflow: hidden;
  border-radius: 25px 0 0 25px;
}

.login_form {
  margin-top: 50px;
  background: white;
  padding: 50px;
  border-radius: 25px;
}

.form_title {
  padding: 15px;
  margin-bottom: 50px;
  font-weight: bold;
  font-size: 32px;
  text-align: center;
}

.login_btn {
  width: 100%;
  font-family: sans-serif;
  font-size: 18px;
  font-weight: bold;
  background-color: #0D74FF;
}

.el-form-item {
  margin: 25px 0;
}

.el-input, .el-button {
  height: 50px;
}

::v-deep .el-card__body {
  padding: 0;
}

::v-deep .el-card {
  border: none;
}
</style>