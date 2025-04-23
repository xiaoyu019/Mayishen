import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Codemirror from "codemirror-editor-vue3";
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import '@/assets/css/global.css'

createApp(App).use(store).use(router).use(ElementPlus, {locale: zhCn}).use(Codemirror).mount('#app')

router.beforeEach((to,from,next) => {
    if(to.meta.title){
        document.title = to.meta.title;
    }
    if(to.meta.requireAuth){
        if (localStorage.getItem('user')) {
            var user = JSON.parse(localStorage.getItem("user"));
            var date = new Date(user.date),today = new Date();
            var diff = (today - date) / 1000 / 3600 / 24;
            if(diff<1) {
                if(to.meta.adminOnly) {
                    if(user.role == 0)
                        next();
                    else
                        next(false);
                }
                else
                    next();
                return;
            }
            localStorage.removeItem("user");
            return;
        }
        next({
                path: '/login',
                query: {redirect: to.fullPath}
        });
        return;
    }
    next();
})