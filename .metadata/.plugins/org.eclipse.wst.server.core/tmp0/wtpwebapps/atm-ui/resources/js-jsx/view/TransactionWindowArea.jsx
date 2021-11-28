class TransactionWindowArea extends React.Component{
	constructor(props){
		super(props);
		this.selectForm = this.selectForm.bind(this);
	}
	selectForm(){
		if (this.props.formType){
			switch(this.props.formType){
				case "transfer":
					return(<TransferForm accountNumbers={this.props.accountNumbers} fromAccount={this.props.fromAccount} toAccount={this.props.toAccount} changeTransferDetails={this.props.changeTransferDetails} date={this.props.date} amount={this.props.transferAmount} finalizeTransfer={this.props.finalizeTransfer} statusCode={this.props.transferStatus} />);
				case "history":
					return(<HistoryForm accountNumbers={this.props.accountNumbers} accountNumber={this.props.selectedAccount} startDate={this.props.startDate} endDate={this.props.endDate} changeHistoryDetails={this.props.changeHistoryDetails} getHistory={this.props.getHistory} />);
				case "table":
					return(<HistoryTable accountNumber={this.props.selectedAccount} historyRows={this.props.historyRows} goBack={this.props.goBack} zoomIn={this.props.zoomIn} />); 
				case "deposit":
					return(<DepositForm accountNumbers={this.props.accountNumbers} depositAmount={this.props.depositAmount} depositAccount={this.props.depositAccount} depositChange={this.props.depositChange} takePicture={this.props.takePicture} finalizeDeposit={this.props.finalizeDeposit} />);
				case "payment":
					return(<BillForm accountNumbers={this.props.accountNumbers} statusCode={this.props.withdrawStatus} changePayment={this.props.changePayment} fromAccount={this.props.withdrawalAccount} amount={this.props.withdrawalAmount} payBill={this.props.payBill} />);
				case "create":
					return(<NewAccountForm confirmNew={this.props.confirmNew} rejectNew={this.props.rejectNew} statusCode={this.props.newAccountStatus} />); 
				case "begin":
					return(<div id="beginner-message">Start a transaction by clicking your accounts or any of the relevant buttons!</div>);
			}
		}
	}
	render(){
		const Form = this.selectForm();
		return(
			<div id="transaction-area">
				<div id="transaction-top">
					<h2>Transaction Window</h2>
				</div>
				<div id="transaction-window">
					{Form}
				</div>
			</div>
		);
	}
}