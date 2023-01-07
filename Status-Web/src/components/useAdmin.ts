import { useRouter } from 'vue-router';
import { computed, reactive, ref } from 'vue';
import { Config, host, port } from '../../types/config';
import axios from 'axios';


export const router = useRouter();
export const editVisible = ref(false);
export const allowDelete = ref(false);
export const configsData = reactive<{ arr: Config[] }>({ arr: [] });
export const editIndex = ref(0);
export const modified = ref(false);
export const saveLoading = ref(false);
export const saveButtonText = ref('保存&应用');
export const saveButtonClass = computed(() => {
  if (saveButtonText.value === '保存成功√') return 'ui positive disabled button';
  return saveLoading.value ? 'ui positive disabled loading button' : 'ui positive button';
});
export const addLoading = ref(false);
export const addButtonText = ref('添加服务器');
export const addButtonClass = computed(() => {
  if (addButtonText.value === '添加成功了√') return 'ui primary disabled button';
  return addLoading.value ? 'ui primary disabled loading button' : 'ui primary button';
});
export const reloadLoading = ref(false);
export const reloadClass = computed(() => {
  if (reloadLoading.value == true) return 'ui small disabled loading button';
  return 'ui small button';
});

// 登录检查
export function checkLogin() {
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

// 数据装载
export function loadConfigs() {
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

export const newConfig: Config = reactive({
  name: '',
  password: '',
  username: '',
  region: '',
  location: '',
  type: '',
  enabled: true
});
export const editedConfig: Config = reactive({
  name: '',
  password: '',
  username: '',
  region: '',
  location: '',
  type: '',
  enabled: true
});

export function addConfigs() {
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

export function saveConfigs() {
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

export function deleteConfigs(index: number) {
  if (allowDelete.value == false) return;
  configsData.arr.splice(index, 1);
  allowDelete.value = false;
  editVisible.value = false;
  modified.value = true;
}

export function startEdit(config: Config, index: number) {
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

export function exitEdit() {
  editVisible.value = false;
}

export function finishEdit() {
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