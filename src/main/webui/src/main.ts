import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from '@/App'
import router from '@/router'
import '@/assets/styles.css'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

createApp(App).use(pinia).use(router).mount('#app')
