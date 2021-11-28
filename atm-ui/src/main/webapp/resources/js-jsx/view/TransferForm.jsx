function TransferForm(props) {
	let transferError;
	if (props.statusCode && props.statusCode !== 200){
		transferError = "Unexpected Error";
		if(props.statusCode===403)
			transferError = "Withdrawal Account: Insufficient Funds";
		else if(props.statusCode>=400 && props.statusCode<500)
			transferError = "Invalid Transfer Accounts/Amount";
	}
	const accountNumbers = props.accountNumbers;
	const accountChoices = accountNumbers.map((acct)=> <option value={acct} key={acct}>#{acct}</option>);
	return (
		<div id="transfer-component" >
			<div class="account-selection-area">
				<label for="from-account">From</label>
				<select id="select-from" value={props.fromAccount} onChange={props.changeTransferDetails} name="from-account" >
					<option value="-1" selected disabled hidden>Select Account</option>
					{accountChoices}
				</select>
			</div>
			<div class="account-selection-area">
				<label for="to-account">To</label>
				<select id="select-to" value={props.toAccount} onChange={props.changeTransferDetails} name="to-account" >
					<option value="-1" selected disabled hidden>Select Account</option>
					{accountChoices}
				</select>
			</div>
			<div class="date-area transfer-date-area">
				<label for="date">Schedule Date <span>(optional)</span></label>
				<input id="schedule-date" value={props.date} onChange={props.changeTransferDetails} type="date" name="date" autocomplete="on"></input>
			</div>
			<div class="amount-area">
				<label for="transfer-amount">Amount</label>
				<input id="amount" value={props.amount} onChange={props.changeTransferDetails} type="number" name="transfer-amount" min="0" step="0.01"></input>
			</div>
			<div id="finalize-transfer-area">
				<div id="transfer-error">{transferError}</div>
				<button onClick={props.finalizeTransfer} type="submit" id="finalize-transfer-button">Transfer</button>
			</div>
		</div>
    );
}