import { createApp } from 'vue';
import App from './App.vue';
import MyApp from './MyApp.vue';
import { createRouter, createWebHistory } from 'vue-router';
import Setting from '@/Setting.vue';

const routes = [
  { path: '/', component: App },
  { path: '/settings', component: Setting }
];
const router = createRouter({
  routes,
  history: createWebHistory()
});
const req = require.context('./assets/img/client', true, /\.svg$/);
req.keys().map(req);

createApp(MyApp).use(router).mount('#app');
