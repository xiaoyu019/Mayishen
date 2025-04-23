import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from "@/layout/AdminLayout.vue";

const routes = [
  {
    path: '/login',
    name: 'LoginView',
    meta:{
      title:"登陆-码易审智能代码分析溯源系统"
    },
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/',
    name: 'AdminLayout',
    component: AdminLayout,
    redirect:"/hello",
    children:[
      {
        path:'hello',
        name:'HelloView',
        meta:{
          title:"码易审智能代码分析溯源系统",
          requireAuth: true
        },
        component: () => import('@/views/admin/HelloView')
      },
      {
        path:'cost-projects',
        name:'CostProjectListView',
        meta:{
          title:"成本评估-码易审智能代码分析溯源系统",
          requireAuth: true
        },
        component: () => import('@/views/admin/CostProject/CostProjectListView')
      },
      {
        path:'calculate-cost/:id',
        name:'CalculateCostView',
        meta:{
          title:"计算软件开发成本-码易审智能代码分析溯源系统",
          requireAuth: true
        },
        component: () => import('@/views/admin/CostProject/CalculateCostView')
      },
      {
        path:'cost-project/:id',
        name:'CostProjectView',
        meta:{
          title:"码易审智能代码分析溯源系统",
          requireAuth: true
        },
        component: () => import('@/views/admin/CostProject/CostProjectView')
      },
      {
        path:'code-projects',
        name:'CodeProjectListView',
        meta:{
          title:"源码分析-码易审智能代码分析溯源系统",
          requireAuth: true
        },
        component: () => import('@/views/admin/CodeProject/CodeProjectListView')
      },
      {
        path:'code-project/:id',
        name:'CodeProjectView',
        meta:{
          title:"码易审智能代码分析溯源系统",
          requireAuth: true
        },
        component: () => import('@/views/admin/CodeProject/CodeProjectView')
      },
      {
        path:'openSourceCodes',
        name:'OpenSourceCodeListView',
        meta:{
          title:"开源代码库-码易审智能代码分析溯源系统",
          requireAuth: true,
          adminOnly:true
        },
        component: () => import('@/views/admin/AdminOnly/OpenSourceCodeListView.vue')
      },
      {
        path:'users',
        name:'UserListView',
        meta:{
          title:"用户管理-码易审智能代码分析溯源系统",
          requireAuth: true,
          adminOnly:true
        },
        component: () => import('@/views/admin/AdminOnly/UserListView.vue')
      },
      {
        path:'standard-documents',
        name:'StandardDocumentView',
        meta:{
          title:"规范与标准-码易审智能代码分析溯源系统",
          requireAuth: true,
        },
        component: () => import('@/views/admin/StandardDocumentView')
      },
      {
        path: 'building',
        name: 'BuildingView',
        meta:{
          title:"您所访问的页面正在建设中-码易审智能代码分析溯源系统"
        },
        component: () => import('@/views/BuildingView')
      },
      {
        path: '/:pathMatch(.*)*',
        name: 'NotFoundView',
        meta:{
          title:"404-您所访问的页面丢失-码易审智能代码分析溯源系统"
        },
        component: () => import('@/views/NotFoundView')
      },

    ]
  }



]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
