
import { computed, reactive, ref } from 'vue';
import { ConfigRow, Config, host, port, protocol } from '../../types/config';
import axios from 'axios';


export const allowDelete = ref(false);
export const configsData = reactive<{ arr: ConfigRow[] }>({ arr: [] });
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
export const showWarning = ref(false)

// 登录检查
export function checkLogin() {
  return axios({
    url: protocol + '//' + host + ':' + port + '/api/checkLogin',
    method: 'post',
    withCredentials: true
  });
}

// 数据装载
export function loadConfigs() {
  reloadLoading.value = true;
  axios({
    url: protocol + '//' + host + ':' + port + '/api/getConfigs',
    method: 'get',
    withCredentials: true
  }).then(res => {
    if (res.data.code == 200) {
      configsData.arr = res.data.data;
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


export function addConfigs() {
  addLoading.value = true;
  axios({
    url: protocol + '//' + host + ':' + port + '/api/addConfig',
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
      alert(res.data.msg);
      addLoading.value = false;
    }
  });
}

export function saveConfigs() {
  saveLoading.value = true;
  axios({
    url: protocol + '//' + host + ':' + port + '/api/saveConfigs',
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
      alert(res.data.msg);
    }
  })
  ;
}

export function deleteConfigs(index: number) {
  if (allowDelete.value == false) {
    // 显示警告动画
    showWarning.value = true
    
    // 动画结束后重置状态
    setTimeout(() => {
      showWarning.value = false
    }, 1800) // 0.6秒 × 3次 = 1.8秒
    
    return;
  }
  
  configsData.arr.splice(index, 1);
  allowDelete.value = false;
  modified.value = true;
}

export function startEdit(index: number) {
  configsData.arr.forEach(row => row._editing = false);
  const row = configsData.arr[index];
  row._backup = { ...row };   // 深拷贝原始数据
  row._editing = true;
}
export function finishEdit(index: number) {
  const row = configsData.arr[index];

  if (!row.username.trim()) {
    alert('username不能为空哦');
    return;
  }

  // username 唯一性校验
  for (let i = 0; i < configsData.arr.length; i++) {
    if (i !== index && configsData.arr[i].username === row.username) {
      alert('发现username有重复, 再检查一下');
      return;
    }
  }

  row._editing = false;
  delete row._backup;
  modified.value = true;
}
export function cancelEdit(index: number) {
  const row = configsData.arr[index];
  Object.assign(row, row._backup);
  row._editing = false;
  delete row._backup;
}

