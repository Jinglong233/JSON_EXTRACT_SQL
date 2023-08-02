import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        path: '/',
        name: 'EditView',
        component: () => import( '../views/EditView.vue')
    },
    {
        path: '/codemirrorEditor',
        name: 'CodemirrorEditor',
        component: () => import( '../components/CodemirrorEditor.vue')
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
