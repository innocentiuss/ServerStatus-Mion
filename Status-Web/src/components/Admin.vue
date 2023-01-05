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
      <transition-group name="list" tag="tbody">
      <tr v-for="(config, index) in configsData.arr" :key="config.username" class="list-item">
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
      <tr v-if="editVisible" class="list-item" :key="1">
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
      <tr :key="2">
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
          <div :class="addButtonClass" @click="addConfigs">
            {{ addButtonText }}
          </div>
        </td>
      </tr>
      </transition-group>
      <tfoot class="full-width">
      <tr>
        <th></th>
        <th colspan="7">
          <div class="ui buttons">
            <div :class="reloadClass" @click="loadConfigs">
              放弃修改&重新加载配置
            </div>
            <div class="or"></div>
            <div :class="saveButtonClass" @click="saveConfigs">
              {{ saveButtonText }}
            </div>
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

import { computed, defineComponent, reactive, ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { Config } from 'types/config';
import { host, port } from '../../globals';

export default defineComponent({

  setup() {
    const router = useRouter();
    const editVisible = ref(false);
    const allowDelete = ref(false);
    const configsData = reactive<{ arr: Config[] }>({ arr: [] });
    const editIndex = ref(0);
    const modified = ref(false);
    const saveLoading = ref(false);
    const saveButtonText = ref('保存&应用');
    const saveButtonClass = computed(() => {
      if (saveButtonText.value === '保存成功√') return 'ui positive disabled button';
      return saveLoading.value ? 'ui positive disabled loading button' : 'ui positive button';
    });
    const addLoading = ref(false);
    const addButtonText = ref('添加服务器');
    const addButtonClass = computed(() => {
      if (addButtonText.value === '添加成功了√') return 'ui primary disabled button';
      return addLoading.value ? 'ui primary disabled loading button' : 'ui primary button';
    });
    const reloadLoading = ref(false);
    const reloadClass = computed(() => {
      if (reloadLoading.value == true) return 'ui small disabled loading button';
      return 'ui small button';
    });

    // 登录检查
    function checkLogin() {
      axios({
        url: 'http://' + host + ':' + port + '/api/checkLogin',
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
      reloadLoading.value = true;
      axios({
        url: 'http://' + host + ':' + port + '/api/getConfigs',
        method: 'get',
        withCredentials: true
      }).then(res => {
        if (res.data.code == 200) {
          configsData.arr = JSON.parse(res.data.data);
          reloadLoading.value = false;
          modified.value = false;
        } else {
          reloadLoading.value = false;
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
      addLoading.value = true;
      axios({
        url: 'http://' + host + ':' + port + '/api/addConfig',
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
          addLoading.value = false;
          addButtonText.value = '添加成功了√';
          setTimeout(() => {
            addButtonText.value = '添加服务器';
          }, 1000);
        } else {
          alert(res.data.data);
          addLoading.value = false;
        }
      });
    }

    function saveConfigs() {
      saveLoading.value = true;
      axios({
        url: 'http://' + host + ':' + port + '/api/saveConfigs',
        method: 'post',
        withCredentials: true,
        data: configsData.arr
      }).then(res => {
        if (res.data.code == 200) {
          saveLoading.value = false;
          saveButtonText.value = '保存成功√';
          loadConfigs();
          setTimeout(() => {
            saveButtonText.value = '保存&应用';
          }, 1000);
        } else {
          saveLoading.value = false;
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
      saveLoading,
      saveButtonText,
      saveButtonClass,
      addLoading,
      addButtonText,
      addButtonClass,
      reloadLoading,
      reloadClass,
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
.list-enter-active, .list-leave-active {
  transition: all .5s;
}
.list-enter, .list-leave-to{
  opacity: 0;
  transform: translateX(30px);
}
.list-move {
  transition: transform .5s;
}
</style>