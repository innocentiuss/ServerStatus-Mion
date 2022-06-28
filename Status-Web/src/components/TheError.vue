<template>
  <div class="ui raised very padded text container segment">
    <div class="ui active centered inline loader"></div>
    <h2 class="ui header"></h2>
    <p class="error">
      正在连接中, 若长时间出现此消息，请确保您已启用Javascript！<br>否则云监控主服务（Java服务端）未启动或已关闭。
    </p>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'TheError'
});
</script>
<style>
p.error {
  text-align: center;
  color: #616366;
  margin-bottom: 0;
}
</style>
