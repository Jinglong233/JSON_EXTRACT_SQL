import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import JsonEditorVue from 'json-editor-vue3'
import {InstallCodemirro} from "codemirror-editor-vue3"

const app = createApp(App);
app.use(store)
app.use(router)
app.use(JsonEditorVue)
app.use(InstallCodemirro)
app.use(ElementPlus)
app.mount('#app')
