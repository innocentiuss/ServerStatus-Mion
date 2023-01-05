<template>
  <div class="ui warning form segment">
    <div class="ui warning message">
      <div class="header">请先登录</div>
      <ul class="list">
        <li>如果不知道登录参数, 请在server_config文件中进行配置</li>
      </ul>
    </div>
    <div class="two fields">
      <div class="field">
        <label>用户名</label>
        <input placeholder="请输入用户名" type="text" v-model="loginForm.username">
      </div>
      <div class="field">
        <label>密码</label>
        <input placeholder="请输入密码" type="password" v-model="loginForm.password">
      </div>
    </div>
    <div class="ui submit button" @click="submitForm()">登录</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import { InitData } from '../../types/login';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { Md5 } from 'ts-md5';
import { host, port } from '../../globals';

export default defineComponent({
  setup() {
    const router = useRouter();
    const data = reactive(new InitData());
    const submitForm = () => {
      axios({
        url: 'http://' + host + ':' + port +'/api/doLogin',
        method: 'post',
        data: {
          username: Md5.hashStr(data.loginForm.username),
          password: Md5.hashStr(data.loginForm.password)
        },
        withCredentials: true
      }).then((res) => {
        if (res.data.code == 200) {
          router.push('/admin');
        } else {
          alert('登录失败');
        }
      });
    };
    return {
      ...toRefs(data),
      submitForm
    };
  }
});
</script>

<style scoped>

</style>