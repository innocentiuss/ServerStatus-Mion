<template>
  <div>
    <div class="ui teal dividing header" style="padding-top:10px;padding-left: 10px">

      <div class="content">
        节点设置
        <div class="sub header">Sub-header</div>
      </div>
    </div>
    <div class="ui warning message">
      <div class="header">注意事项</div>
      <ul class="list">
        <li>国家那栏只能填写两个大写英文(例如美国就写US)，不然国旗会不能正常渲染</li>
      </ul>
    </div>
    <table class="ui compact celled table">
      <thead>
      <tr>
        <th>启用</th>
        <th>节点名(name)</th>
        <th>类型(type)</th>
        <th>地点(location)</th>
        <th>用户名(username)</th>
        <th>密码(password)</th>
        <th>国家(region)</th>
        <th>设置</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="config in configsData.values" :key="config.username">
        <td class="collapsing">
          <div class="ui toggle checkbox">
            <input type="checkbox" v-model="config.enabled" disabled="disabled"><label></label>
          </div>
        </td>
        <td>{{ config.name }}</td>
        <td>{{ config.type }}</td>
        <td>{{ config.location }}</td>
        <td>{{ config.username }}</td>
        <td>{{ config.password }}</td>
        <td>{{ config.region }}</td>
        <td>
          <div class="ui teal button">修改配置</div>
          <div class="negative ui button">删除配置</div>
        </td>
      </tr>
      <tr>
        <td>
          <div class="ui toggle checkbox">
            <input type="checkbox" v-model="newConfig.enabled"><label></label>
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="展示的节点名" v-model="newConfig.name">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="虚拟化类型/服务商类型" v-model="newConfig.type">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="所在城市/州" v-model="newConfig.location">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="连接的用户名" v-model="newConfig.username">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="连接的密码" v-model="newConfig.password">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="用于渲染国旗" v-model="newConfig.region">
          </div>
        </td>
        <td>
          <div class="ui primary button" @click="addConfigs">
            添加服务器
          </div>
        </td>
      </tr>
      </tbody>
      <tfoot class="full-width">
      <tr>
        <th></th>
        <th colspan="7">
          <div class="ui small button" @click="loadConfigs">
            放弃修改&重新加载配置
          </div>
          <div class="ui small positive button">
            保存&应用
          </div>
        </th>
      </tr>
      </tfoot>
    </table>
  </div>
</template>

<script lang="ts">

import { defineComponent, reactive } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { Config } from 'types/config';


export default defineComponent({

  setup() {
    const router = useRouter();
    const host = window.location.hostname;

    // 登录检查
    function checkLogin() {
      axios({
        url: 'http://' + host + ':8080/checkLogin',
        method: 'post',
        withCredentials: true
      }).then(res => {
        if (res.data.code != 200) {
          alert('请先登录');
          router.push(res.data.data);
        }
      });
    }

    checkLogin();
    // 数据装载
    const configsData = reactive<Array<Config>>([]);

    function loadConfigs() {
      axios({
        url: 'http://' + host + ':8080/getConfigs',
        method: 'get',
        withCredentials: true
      }).then(res => {
        if (res.data.code == 200) {
          configsData.values = JSON.parse(res.data.data);
        } else {
          alert('获取配置文件失败!');
        }
      });
    }

    const newConfig: Config = reactive({
      name: '',
      password: '',
      username: '',
      region: '',
      location: '',
      type: '',
      enabled: true
    });
    
    function addConfigs() {
      axios({
        url: 'http://' + host + ':8080/addConfigs',
        method: 'post',
        withCredentials: true,
        data: newConfig
      }).then(res => {
        if (res.data.code == 200) {
          console.log('添加成功');
          loadConfigs();
        } else {
          alert(res.data.data);
        }
      });
    }

    loadConfigs();

    return {
      addConfigs,
      newConfig,
      configsData,
      loadConfigs
    };
  }
});
</script>

<style scoped>
.ui.toggle.checkbox input:checked ~ .box:before,
.ui.toggle.checkbox input:checked ~ label:before {
  background-color: #21BA45 !important;
}
</style>