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
        <li>为防误点，删除节点配置前，先要打开最下面的允许删除开关，不然不会有反应</li>
        <li>修改和删除需要点击保存&应用，才能同步到配置文件并正式启用，添加不用</li>
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
      <tr v-for="(config, index) in configsData.arr" :key="index">
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
          <div class="ui teal button" @click="startEdit(config, index)">修改配置</div>
          <div class="negative ui button" @click="deleteConfigs(index)">删除配置</div>
        </td>
      </tr>
      <tr v-if="editVisible">
        <td>
          <div class="ui toggle checkbox">
            <input type="checkbox" v-model="editedConfig.enabled"><label></label>
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="展示的节点名" v-model="editedConfig.name">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="虚拟化类型/服务商类型" v-model="editedConfig.type">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="所在城市/州" v-model="editedConfig.location">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="连接的用户名" v-model="editedConfig.username">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="连接的密码" v-model="editedConfig.password">
          </div>
        </td>
        <td>
          <div class="ui input">
            <input type="text" placeholder="用于渲染国旗" v-model="editedConfig.region">
          </div>
        </td>
        <td>
          <div class="ui positive button" @click="finishEdit">
            编辑完成
          </div>
          <div class="ui button" @click="exitEdit">
            放弃编辑
          </div>
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
          <div class="ui small positive button" @click="saveConfigs">
            保存&应用
          </div>
          <span v-if="modified"><b>←检测到有修改，点它生效</b></span>
          <div class="ui slider checkbox" style="margin-left: 10px">
            <input type="checkbox" name="newsletter" v-model="allowDelete">
            <label>允许删除:
              <span v-bind:style="{ fontWeight: allowDelete ? 'bold' : 'normal' }">
              {{ allowDelete ? '是' : '否' }}
            </span>
            </label>
          </div>
        </th>
      </tr>
      </tfoot>
    </table>
  </div>
</template>

<script lang="ts">

import { defineComponent, reactive, ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { Config } from 'types/config';

export default defineComponent({

  setup() {
    const router = useRouter();
    const host = window.location.hostname;
    const editVisible = ref(false);
    const allowDelete = ref(false);
    const configsData = reactive<{ arr: Config[] }>({ arr: [] });
    const editIndex = ref(0);
    const modified = ref(false);

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

    function loadConfigs() {
      axios({
        url: 'http://' + host + ':8080/getConfigs',
        method: 'get',
        withCredentials: true
      }).then(res => {
        if (res.data.code == 200) {
          configsData.arr = JSON.parse(res.data.data);
          modified.value = false;
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
    const editedConfig: Config = reactive({
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
        url: 'http://' + host + ':8080/addConfig',
        method: 'post',
        withCredentials: true,
        data: newConfig
      }).then(res => {
        if (res.data.code == 200) {
          console.log('添加成功');
          loadConfigs();
          newConfig.username = '';
          newConfig.password = '';
          newConfig.name = '';
          newConfig.type = '';
          newConfig.location = '';
          newConfig.region = '';
        } else {
          alert(res.data.data);
        }
      });
    }

    function saveConfigs() {
      axios({
        url: 'http://' + host + ':8080/saveConfigs',
        method: 'post',
        withCredentials: true,
        data: configsData.arr
      }).then(res => {
        if (res.data.code == 200) {
          loadConfigs();
          console.log('保存成功');
        } else {
          alert(res.data.data);
        }
      })
      ;
    }

    function deleteConfigs(index: number) {
      if (allowDelete.value == false) return;
      configsData.arr.splice(index, 1);
      allowDelete.value = false;
      editVisible.value = false;
      modified.value = true;
    }

    function startEdit(config: Config, index: number) {
      editVisible.value = true;
      editIndex.value = index;
      editedConfig.enabled = config.enabled;
      editedConfig.name = config.name;
      editedConfig.username = config.username;
      editedConfig.password = config.password;
      editedConfig.location = config.location;
      editedConfig.region = config.region;
      editedConfig.type = config.type;
    }

    function exitEdit() {
      editVisible.value = false;
    }

    function finishEdit() {
      if (editedConfig.username.trim() == '') {
        alert('username不能为空哦');
        return;
      }
      const deepCopy: Config = { ...editedConfig };
      for (let i = 0; i < configsData.arr.length; i++) {
        if (configsData.arr[i].username == deepCopy.username && i != editIndex.value) {
          alert('发现username有重复, 再检查一下');
          return;
        }
      }
      configsData.arr.splice(editIndex.value, 1, deepCopy);
      editVisible.value = false;
      modified.value = true;
    }

    loadConfigs();

    return {
      editedConfig,
      editVisible,
      allowDelete,
      newConfig,
      configsData,
      modified,
      addConfigs,
      deleteConfigs,
      saveConfigs,
      loadConfigs,
      startEdit,
      finishEdit,
      exitEdit
    };
  }
});
</script>

<style scoped>
.ui.toggle.checkbox input:checked ~ .box:before,
.ui.toggle.checkbox input:checked ~ label:before {
  background-color: #21BA45 !important;
}

.ui.slider.checkbox input:checked ~ .box:before,
.ui.slider.checkbox input:checked ~ label:before {
  background-color: red !important;
}
</style>