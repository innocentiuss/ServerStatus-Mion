export interface LoginFormInt {
  username: string;
  password: string;
}

export class InitData {
  loginForm: LoginFormInt = {
    username: '',
    password: ''
  };
}
