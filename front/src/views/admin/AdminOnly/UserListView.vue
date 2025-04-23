<!--
* @FileDescription: 用户管理页面
-->
<template>
  <div style="padding:25px;width: 100%; height:100%">
    <div class="header-title">用户管理</div>
    <el-card shadow="always" ref="element" style="border-radius: 15px;">
      <div style="display: flex;justify-content: space-between;padding:10px;">
        <div>
          <el-button @click="load()">
            <el-icon>
              <Refresh/>
            </el-icon>
          </el-button>
          <el-button type="primary" :loading="drawerVisible" @click="drawerVisible = true">
            <el-icon>
              <Plus/>
            </el-icon>&nbsp;新建用户
          </el-button>
        </div>
        <div>
          <label class="tool-label">用户名称</label>
          <el-input v-model="this.search" placeholder="请输入名称" clearable style="width: 200px;margin-right: 15px;"/>
          <el-button @click="load()">
            <el-icon>
              <Search/>
            </el-icon> &nbsp;搜索
          </el-button>
          <el-button @click="reset()">重置</el-button>
        </div>
      </div>
      <el-table :data="filterTableData" stripe style="width: 100%;">
        <el-table-column prop="name" label="姓名"/>
        <el-table-column prop="username" label="用户名"/>
        <el-table-column fixed="right" label="操作">
          <template #default="scope">
            <el-button type="primary" size="large" @click="handleEdit(scope.row)" round>
              <el-icon>
                <Edit/>
              </el-icon>&nbsp;编辑
            </el-button>
            <el-popconfirm v-if="scope.row.status!=0" title="确认删除？" @confirm="handleDelete(scope.row.id)">
              <template #reference>
                <el-button size="large" type="danger" round>
                  <el-icon>
                    <Delete/>
                  </el-icon>&nbsp;删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 10px 0;">
        <el-pagination
            align="center"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            background
            :page-sizes="[5, 10, 20]"
            layout="total, sizes,prev, pager, next, jumper"
            :total="total"
            @size-change="load"
            @current-change="load"
        />
      </div>
    </el-card>
    <el-drawer
        v-model="drawerVisible"
        :title="this.form.id?'用户信息':'新增用户'"
        width="600px"
        :before-close="handleClose"
    >
      <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="210px" label-position="top" status-icon>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" maxlength="20" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" maxlength="20" style="width: 80%"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="pwd">
          <el-input type="password" v-model="form.pwd" maxlength="20" style="width: 80%" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="check_pwd">
          <el-input type="password" v-model="form.check_pwd" maxlength="20" style="width: 80%" show-password></el-input>
        </el-form-item>
        <el-form-item label="用户角色" prop="check_pwd">
          <el-select v-model="form.role" filterable>
            <el-option label="管理员" :value="0"/>
            <el-option label="普通用户" :value="1"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="save"><el-icon><Select/></el-icon>&nbsp;保存</el-button>
      </span>
      </template>
    </el-drawer>
  </div>
</template>

<script>

import request from "@/utils/request";
import {ElMessage, ElNotification} from 'element-plus'
import {View, EditPen as Edit, Delete, Search, Plus, Minus, FullScreen, Select, Refresh} from '@element-plus/icons-vue'
import md5 from 'js-md5';

export default {
  name: 'UserListView',
  components: {
    View, Edit, Delete, Search, Plus, Minus, FullScreen, Select, Refresh
  },
  data() {
    return {
      search: "",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      drawerVisible: false,
      form: {},
      tableData: [],
      rules: {
        name: [
          {required: true, max: 20, message: "请检查姓名，长度至多20字符！", trigger: "blur"}
        ],
        username: [
          {required: true, message: "请输入用户名！", trigger: "blur"},
          {min: 5, max: 20, message: "请检查用户名，长度必须为5-20字符！", trigger: "blur"}
        ],
        pwd: [
          {required: true, message: "请输入密码！", trigger: "blur"},
          {min: 8, max: 20, message: "请检查密码，长度必须为8-20字符！", trigger: "blur"},
          {
            pattern: /^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\W_]+$)(?![a-z0-9]+$)(?![a-z\W_]+$)(?![0-9\W_]+$)[a-zA-Z0-9\W_]{8,30}$/,
            message: '密码为数字，小写字母，大写字母，特殊符号至少包含三种'
          }
        ],
        check_pwd: [
          {required: true, validator: this.validatePass, trigger: "blur"}
        ],
      }
    }
  },
  computed: {
    filterTableData() {
      return this.tableData.filter(
          (data) => !this.keyword || data.name.indexOf(this.keyword) != -1)
    },
  },
  created() {
    this.load();
  },
  methods: {
    /**
     * @description 加载用户列表
     * @return {void}
     */
    load() {
      request.get("user", {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          search: this.search
        }
      }).then(res => {
        if (res.code == "0") {
          this.tableData = res.data.records;
          this.total = res.data.total;
        } else {
          ElNotification({
            title: '加载失败',
            message: res.msg,
            type: 'error',
          });
        }
      })
    },
    /**
     * @description 重置搜索条件
     * @return {void}
     */
    reset() {
      this.search = '';
      this.load();
    },
    /**
     * @description 显示新增用户抽屉
     * @return {void}
     */
    handleShow() {
      this.drawerVisible = true;
      this.form = {};
    },
    /**
     * @description 新增或修改用户信息
     * @return {void}
     */
    save() {
      this.$refs.ruleFormRef.validate((valid, fields) => {
        if (valid) {
          this.form.password = md5(this.form.pwd);
          if (this.form.id) {
            request.put("/user", this.form).then(res => {
              if (res.code == "0") {
                ElNotification({
                  title: '更新成功',
                  type: 'success',
                });
                this.load();
                this.drawerVisible = false;
              } else {
                ElNotification({
                  title: '更新失败',
                  message: res.msg,
                  type: 'error',
                });
              }
            });
          } else {
            request.post("/user", this.form).then(res => {
              if (res.code == "0") {
                ElNotification({
                  title: '新增成功',
                  type: 'success',
                });
                this.load();
                this.drawerVisible = false;
              } else {
                ElNotification({
                  title: '新增失败',
                  message: res.msg,
                  type: 'error',
                });
              }
            });
          }
        }
      });
    },
    /**
     * @description 编辑用户信息
     * @return {void}
     */
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.drawerVisible = true;
    },
    /**
     * @description 根据ID删除用户
     * @param {number} id 用户ID
     * @return {void}
     */
    handleDelete(id) {
      request.delete("/user/delete/" + id).then(res => {
        if (res.code == "0") {
          ElNotification({
            title: '删除成功',
            type: 'success',
          });
          this.load();
        } else {
          ElNotification({
            title: '删除失败',
            message: res.msg,
            type: 'error',
          });
        }
      })
    },
    /**
     * @description 关闭抽屉的回调方法
     * @return {void}
     */
    handleClose() {
      this.drawerVisible = false;
      this.form = {};
    },
    /**
     * @description 关闭抽屉的回调方法
     * @param {Object} rule 规则
     * @param {Object} value 值
     * @param {Object} callback 回调方法
     * @return {void}
     */
    validatePass(rule, value, callback) {
      if (value === '' || value == undefined) {
        callback(new Error("请再次输入密码"))
      } else if (value !== this.form.pwd) {
        callback(new Error("两次输入不一致！"))
      } else {
        callback()
      }
    }

  }
}
</script>

<style scoped>
.header-title {
  font-weight: bold;
  font-size: 24px;
  margin-bottom: 15px;
}


.tool-label {
  font-size: 14px;
  line-height: 32px;
  margin-right: 5px;
}

::v-deep .el-drawer__header {
  color: black;
  font-size: 20px;
  font-weight: bold;
}
</style>