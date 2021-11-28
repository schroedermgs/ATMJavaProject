function BankingModel(userAccounts){
	this.accountNumbers = userAccounts;
	this.formType = null;
	this.depositAmount = "0";
	this.depositAccount = "-1";
	this.selectedAccount = null;
	this.startDate = null;
	this.endDate = null;
	this.historyRows = new Array();
	this.transferFrom = "-1";
	this.transferTo = "-1";
	this.transferDate = null;
	this.transferAmount = "0";
	this.transferStatus = null;
	this.withdrawStatus = null;
	this.payee = "";
	this.withdrawalAccount = "-1";
	this.withdrawalAmount = "0";
	this.newAccountStatus = null;
}

