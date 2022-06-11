/* eslint-disable */
declare global {
  interface BoxItem {
    'name': string;
    'host': string;
    'type': string;
    'online': boolean;
    'location': string;
    'region': string;
  }

  interface StatusItem extends BoxItem {
    'uptime': string;
    'load1': number;
    'load5': number;
    'load15': number;
    'cpu': number;
    'network_rx': number;
    'network_tx': number;
    'network_in': number;
    'network_out': number;
    'memory_total': number;
    'memory_used': number;
    'swap_total': number;
    'swap_used': number;
    'hdd_total': number;
    'hdd_used': number;
    'custom': string;
    'loss_189': number;
    'loss_10086': number;
    'loss_10010': number;
    'ping_189': number;
    'ping_10010': number;
    'ping_10086': number;
    'tcp': number;
    'thread': number;
    'process': number;
  }

  interface Window {
    __PRE_CONFIG__: {
      header: string;
      subHeader: string;
      interval: number;
      footer: string;
    }
  }
}
export {};
