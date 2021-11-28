class TransferController extends React.Component{
	constructor(props){
		super(props);
		this.state = {accountNumbers: props.accountNumbers, statusCode: props.statusCode, 
		date: props.date, amount: props.amount};
	}
	render(){
		return(
			<TransferForm accountNumbers={this.state.accountNumbers} statusCode={this.state.statusCode} date={this.state.date} amount={this.state.amount} changeTransferDetails={this.props.changeTransferDetails} finalizeTransfer={this.props.finalizeTransfer} />
		);
	}
}