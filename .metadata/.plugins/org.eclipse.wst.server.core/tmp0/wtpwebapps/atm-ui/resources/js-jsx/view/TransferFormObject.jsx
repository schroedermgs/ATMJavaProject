class TransferForm extends React.Component {
	constructor(props){
		super(props);
		this.transferError = null;
	    if (props.statusCode && props.statusCode !== 200){
			this.transferError = "Unexpected Error"
			if(props.statusCode===403)
				this.transferError = "Withdrawal Account: Insufficient Funds";
			else if(props.statusCode>=400 && props.statusCode<500)
				this.transferError = "Invalid Transfer Accounts/Amount" 
		}
		this.accountNumbers = props.accountNumbers;
		this.accountChoices = this.accountNumbers.map((acct)=> <option value={acct} key={acct}>#{acct}</option>);
	}
    render(){ 
		
		return (
			<div id="transfer-component" >
				<div class="account-selection-area">
					<label for="from-account">From</label>
					<select id="select-from" onChange={this.props.changeTransferDetails} name="from-account" >
						<option value="-1" selected disabled hidden>Select Account</option>
						{this.accountChoices}
					</select>
				</div>
				<div class="account-selection-area">
					<label for="to-account">To</label>
					<select id="select-to" onChange={this.props.changeTransferDetails} name="to-account" >
						<option value="-1" selected disabled hidden>Select Account</option>
						{this.accountChoices}
					</select>
				</div>
				<div class="date-area">
					<label for="date">Date</label>
					<input id="date" value={this.props.date} onChange={this.props.changeTransferDetails} type="date" name="date" autocomplete="on"></input>
				</div>
				<div class="amount-area">
					<label for="transfer-amount">Amount</label>
					<input id="amount" onChange={this.props.changeTransferDetails} type="number" name="transfer-amount" min="0" step="0.01"></input>
				</div>
				<div id="finalize-transfer-area">
					<div id="transfer-error">{this.transferError}</div>
					<button onClick={this.props.finalizeTransfer} type="submit" id="finalize-transfer-button">Transfer</button>
				</div>
			</div>
	    );
	}
}