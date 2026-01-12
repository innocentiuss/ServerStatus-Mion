export interface Config {
  enabled: boolean;
  name: string;
  type: string;
  location: string;
  username: string;
  password: string;
  region: string;
}
export interface ConfigRow extends Config {
  _editing?: boolean;
  _backup?: Config;
}

export const protocol = window.location.protocol;
export const port = process.env.NODE_ENV === 'development' ? '8080' : protocol === 'https:' ? '443' : '80';
export const host = window.location.hostname;