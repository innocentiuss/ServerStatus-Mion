export interface Config {
  enabled: boolean;
  name: string;
  type: string;
  location: string;
  username: string;
  password: string;
  region: string;
}

export const port = process.env.NODE_ENV === 'development' ? '8080' : '80';
export const host = window.location.hostname;