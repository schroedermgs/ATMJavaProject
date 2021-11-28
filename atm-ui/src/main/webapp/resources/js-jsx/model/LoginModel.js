function LoginModel(username,password) {
    this.username = username;
	this.password = password;
    this.responseStatus = null;
    this.accounts = new Array();
}