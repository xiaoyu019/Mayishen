import axios from 'axios'
import router from "@/router";
import { ElNotification } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 5000
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';

    const user= localStorage.getItem("user")?JSON.parse(localStorage.getItem("user")):{};
    if(user)
        config.headers['token'] = user.token;
    else{
        router.push("/login");
    }

    return config;
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 如果是返回的文件
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        if(res.code=="402"&&localStorage.getItem("user"))
        {
            localStorage.removeItem("user");
            ElNotification({
                title: '验证失败',
                message: res.msg,
                type: 'error',
            });
            router.push("/login");
        }
        return res;
    },
    error => {
        console.log(error);
        if(error.code === "ERR_BAD_RESPONSE"&&localStorage.getItem("user")){
            console.log({
                title: '后端错误',
                message: '请稍后重试',
                type: 'error',
            });
        }
        return Promise.reject(error);
    }
)

export default request

