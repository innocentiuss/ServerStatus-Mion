<template>
  <div>
    <div class="ui teal dividing header" style="padding-top:10px;padding-left: 10px">
      <div class="content" @click="backToIndex">
        节点设置
        <div class="sub header">节点管理后台</div>
      </div>
    </div>
    <div class="ui warning message">
      <div class="header">注意事项</div>
      <ul class="list">
        <li>国家那栏只能填写两个大写英文(例如美国就写US,日本写JP)，不然国旗会不能正常渲染</li>
        <li>为防误点，删除节点配置前，先要打开最下面的允许删除开关，不然按钮不会有反应</li>
        <li>修改和删除需要点击保存&应用，才能同步到配置文件并正式启用，添加不用</li>
      </ul>
    </div>
    <div class="dimmable">
      <div class="ui active inverted dimmer" v-if="loading">
        <div class="ui text loader">Loading</div>
      </div>
    <table class="ui compact celled table">
      <thead>
      <tr>
        <th>展示</th>
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
            <input type="checkbox" v-model="config.enabled"><label></label>
          </div>
        </td>
        <td>
          <template v-if="config._editing">
            <div class="ui input"><input type="text" v-model="config.name" /></div>
          </template>
          <template v-else>
            {{ config.name }}
          </template>
        </td>
        <td>
          <template v-if="config._editing">
            <div class="ui input"><input type="text" v-model="config.type" /></div>
          </template>
          <template v-else>
            {{ config.type }}
          </template>
        </td>
        <td>
          <template v-if="config._editing">
            <div class="ui input"><input type="text" v-model="config.location" /></div>
          </template>
          <template v-else>
            {{ config.location }}
          </template>
        </td>
        <td>
          <template v-if="config._editing">
            <div class="ui input"><input type="text" v-model="config.username" /></div>
          </template>
          <template v-else>
            {{ config.username }}
          </template>
        </td>
        <td>
          <template v-if="config._editing">
            <div class="ui input"><input type="text" v-model="config.password" /></div>
          </template>
          <template v-else>
            {{ config.password }}
          </template>
        </td>
        <td>
          <template v-if="config._editing">
            <div class="ui input"><input type="text" v-model="config.region" /></div>
          </template>
          <template v-else>
            {{ config.region }}
          </template>
        </td>
        <td>
          <template v-if="config._editing">
            <button class="ui positive button" @click="finishEdit(index)">编辑完成</button>
            <button class="ui button" @click="cancelEdit(index)">放弃编辑</button>
          </template>
          <template v-else>
            <button class="ui teal button" @click="startEdit(index)">修改配置</button>
            <button class="negative ui button" @click="deleteConfigs(index)">删除配置</button>
          </template>
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
          <span v-if="modified"><b>←如果有修改，点它生效</b></span>
          <div class="ui slider checkbox" style="margin-left: 10px">
            <input type="checkbox" name="newsletter" v-model="allowDelete">
            <label>允许删除:
              <span 
                v-bind:style="{ fontWeight: allowDelete ? 'bold' : 'normal' }"
                :class="{ 'warning-animation': showWarning }"
              >
                {{ allowDelete ? '是' : '否' }}
              </span>
            </label>
          </div>
        </th>
      </tr>
      </tfoot>
    </table>
  </div>
  </div>
</template>

<script lang="ts">

import { defineComponent, ref } from 'vue';
import {
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
  cancelEdit,
  addConfigs,loadConfigs, deleteConfigs, saveConfigs,
  startEdit, finishEdit, checkLogin, showWarning
} from '@/components/useAdmin';
import { useRouter } from 'vue-router';


export default defineComponent({

  setup() {
    const router = useRouter();
    const backToIndex = () => {
      window.location.href = '/';
    };
    const loading = ref(true);
    checkLogin().then(res => {
      if (res.data.code != 200) {
        alert('please login');
        router.push(res.data.data);
      } else {
        // Call loadConfigs after checkLogin is finished
        loadConfigs();
        loading.value = false;
      }
    });
    
    return {
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
      loading,
      addConfigs,
      deleteConfigs,
      saveConfigs,
      loadConfigs,
      startEdit,
      finishEdit,
      cancelEdit,
      backToIndex,
      showWarning
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
.content {
  cursor: pointer;
}
.warning-animation {
  animation: warningPulse 0.6s ease-in-out 3;
  display: inline-block;
}

@keyframes warningPulse {
  0% {
    font-weight: bold;
    color: #e74c3c;
    transform: scale(1.1);
    text-shadow: 0 0 5px rgba(231, 76, 60, 0.5);
  }
  33% {
    font-weight: normal;
    color: inherit;
    transform: scale(1);
    text-shadow: none;
  }
  66% {
    font-weight: bold;
    color: #e74c3c;
    transform: scale(1.1);
    text-shadow: 0 0 5px rgba(231, 76, 60, 0.5);
  }
  100% {
    font-weight: normal;
    color: inherit;
    transform: scale(1);
    text-shadow: none;
  }
}
</style>