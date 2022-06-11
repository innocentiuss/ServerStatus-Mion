<template>
  <div class="column">
    <div class="ui fluid card">
      <div class="card-header">
        <span> {{ server.name }} </span>
        <p>{{ server.type }}</p>
      </div>
      <div class="ui tiny progress success">
        <div class="bar" :style="{width: getStatus ? `${getRAMStatus.toString()}%` : '0%'}">
        </div>
      </div>
      <div class="card-content">
        <p>Network: {{ `${tableRowByteConvert(server.network_rx)} | ${tableRowByteConvert(server.network_tx)}` }}</p>
        <div class="ui three tiny statistics">
          <div :class="getLossColor(server.loss_189) + ' statistic'">
            <div class="value">
              {{getStatus ? server.loss_189 : '-'}}
            </div>
            <div class="label">
              电信loss%
            </div>
          </div>
          <div :class="getLossColor(server.loss_10010) + ' statistic'">
            <div class="value">
              {{getStatus ? server.loss_10010 : '-'}}
            </div>
            <div class="label">
              联通loss%
            </div>
          </div>
          <div :class="getLossColor(server.loss_10086) + ' statistic'">
            <div class="value">
              {{getStatus ? server.loss_10086 : '-'}}
            </div>
            <div class="label">
              移动loss%
            </div>
          </div>
        </div>
        <div class="ui three tiny statistics">
          <div :class="getRTTColor(server.ping_189) + ' statistic'">
            <div class="value">
              {{getStatus ? server.ping_189 : '-'}}
            </div>
            <div class="label">
              电信ms
            </div>
          </div>
          <div :class="getRTTColor(server.ping_10010) + ' statistic'">
            <div class="value">
              {{getStatus ? server.ping_10010 : '-'}}
            </div>
            <div class="label">
              联通ms
            </div>
          </div>
          <div :class="getRTTColor(server.ping_10086) + ' statistic'">
            <div class="value">
              {{getStatus ? server.ping_10086 : '-'}}
            </div>
            <div class="label">
              移动ms
            </div>
          </div>
        </div>
        <div class="ui three tiny statistics">
          <div class="statistic">
            <div class="value">
              {{ getStatus ? server.load1 : '-' }}
            </div>
            <div class="label">
              负载1min
            </div>
          </div>
          <div class="statistic">
            <div class="value">
              {{ getStatus ? server.load5 : '-' }}
            </div>
            <div class="label">
              负载5min
            </div>
          </div>
          <div class="statistic">
            <div class="value">
              {{ getStatus ? server.load15 : '-' }}
            </div>
            <div class="label">
              负载15min
            </div>
          </div>
        </div>
        <p>TCP连接数: {{getStatus ? server.tcp : '-'}} | 进程数: {{getStatus ? server.process : '-'}} | 线程数: {{getStatus ? server.thread : '-'}}</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';
import useStatus from './useStatus';

export default defineComponent({
  name: 'CardItem',
  methods: {
    getLossColor(lossRate: number) {
      if (lossRate < 5) return 'green';
      else if (lossRate < 15) return 'yellow';
      return 'red';
    },
    getRTTColor(ms: number) {
      if (ms < 100) return 'green';
      else if (ms < 200) return 'yellow';
      return 'red';
    }
  },
  props: {
    server: {
      type: Object as PropType<StatusItem | BoxItem>,
      default: () => ({})
    }
  },
  setup(props) {
    const { getStatus, getRAMStatus, tableRowByteConvert } = useStatus(props);
    return {
      getStatus,
      getRAMStatus,
      tableRowByteConvert
    };
  },
});
</script>

<style scoped>
div.card {
  padding: 24px;
  box-shadow: 5px 5px 25px 0 rgba(46, 61, 73, .2);
  border-radius: .5rem;
  background-color: rgba(255, 255, 255, .8);
}

div.card div.card-header span {
  font-size: 1.25rem;
  font-weight: normal;
  vertical-align: middle;
}

div.card div.card-header p {
  color: #919699;
}

div.card div.card-content p {
  margin-bottom: 0;
}

div.card div.progress {
  margin: 1.2em 0;
}

.flag-icon {
  display: inline;
  vertical-align: middle;
  width: 70px;
  margin-right: 8px;
}
</style>
