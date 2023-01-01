<template>
  <div>
    <div class="ui teal dividing header" style="padding-top:10px;padding-left: 10px">

      <div class="content">
        节点设置
        <div class="sub header">Sub-header</div>
      </div>
    </div>
    <table class="ui compact celled definition table">
      <thead>
      <tr>
        <th>启用</th>
        <th>节点名</th>
        <th>类型</th>
        <th>host</th>
        <th>地点</th>
        <th>用户名</th>
        <th>密码</th>
        <th>国家</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td class="collapsing">
          <div class="ui fitted slider checkbox">
            <input type="checkbox"> <label></label>
          </div>
        </td>
        <td>John Lilki</td>
        <td>September 14, 2013</td>
        <td>jhlilk22@yahoo.com</td>
        <td>No</td>
      </tr>
      <tr>
        <td class="collapsing">
          <div class="ui fitted slider checkbox">
            <input type="checkbox"> <label></label>
          </div>
        </td>
        <td>Jamie Harington</td>
        <td>January 11, 2014</td>
        <td>jamieharingonton@yahoo.com</td>
        <td>Yes</td>
      </tr>
      <tr>
        <td class="collapsing">
          <div class="ui fitted slider checkbox">
            <input type="checkbox"> <label></label>
          </div>
        </td>
        <td>Jill Lewis</td>
        <td>May 11, 2014</td>
        <td>jilsewris22@yahoo.com</td>
        <td>Yes</td>
      </tr>
      </tbody>
      <tfoot class="full-width">
      <tr>
        <th></th>
        <th colspan="4">
          <div class="ui right floated small primary labeled icon button">
            <i class="user icon"></i> Add User
          </div>
          <div class="ui small button">
            Approve
          </div>
          <div class="ui small  disabled button">
            Approve All
          </div>
        </th>
      </tr>
      </tfoot>
    </table>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';


export default defineComponent({

  mounted() {
    const router = useRouter();
    const checkLogin = () => {

      axios({
        url: 'http://localhost:8080/checkLogin',
        method: 'post',
        withCredentials: true
      }).then(res => {
        if (res.data.code == 301) {
          alert('请先登录');
          router.push(res.data.data);
        }
      });
    };
    checkLogin();
  }
});
</script>

<style scoped>

</style>