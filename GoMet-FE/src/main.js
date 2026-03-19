import { createApp } from 'vue'
import './assets/styles/design-tokens.css'
import './assets/styles/base.css'
import './assets/styles/main.scss'
import './style.css'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import i18n from './i18n'
import vue3GoogleLogin from 'vue3-google-login'



const app = createApp(App)

app.use(vue3GoogleLogin, {
  clientId: '234763019792-mvr72fupigk7aq9ieuf29thfotcla00c.apps.googleusercontent.com'
})
app.use(createPinia())
app.use(router)
app.use(i18n)

app.mount('#app')
