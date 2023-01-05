import { createApp } from 'vue';
import App from './App.vue';
import MyApp from './MyApp.vue';
import { createRouter, createWebHistory } from 'vue-router';
import Admin from '@/components/Admin.vue';
import LoginView from '@/components/Login.vue';

const routes = [
  { path: '/', component: App },
  { path: '/admin', component: Admin },
  { path: '/login', component: LoginView }
];
const router = createRouter({
  routes,
  history: createWebHistory()
});
const req = require.context('./assets/img/client', true, /\.svg$/);
req.keys().map(req);

createApp(MyApp).use(router).mount('#app');
