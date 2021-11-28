function BillForm(props) {
	let withdrawError;
	if (props.statusCode && props.statusCode !== 200){
		withdrawError = "Unexpected Error";
		if(props.statusCode===400)
			withdrawError = "Withdrawal Account: Insufficient Funds";
	}
	const accountNumbers = props.accountNumbers;
	const accountChoices = accountNumbers.map((acct)=> <option value={acct} key={acct}>#{acct}</option>);
	return (
		<div id="bill-form">
			<h1>Pay a Bill</h1>
			<div class="payee-selection-area">
				<label for="payee">Pay To</label>
				<select id="payee" onChange={props.changePayment} name="payee">
					<option value="-1" selected hidden disabled>Select Payee</option>
					<option value="Frozen Accounts, LLC">Frozen Accounts, LLC</option>
					<option value="Cold Hard Cash Lenders">Cold Hard Cash Lenders</option>
					<option value="Nuclear Winter Electric Company">Nuclear Winter Electric Company</option>
					<option value="Cold Sweat Gym">Cold Sweat Gym</option>
					<option value="Southern Cross Research Station">Southern Cross Research Station</option>
					<option value="Ice Cube Music Fund">Ice Cube Music Fund</option>
					<option value="Robert Frostbite Poetry Association">Robert Frostbite Poetry Society</option>
					<option value="Midnight Sun Eyecare">Midnight Sun Eyecare</option>
					<option value="Under the Weather Snow Removal">Under the Weather Snow Removal</option>
					<option value="Gone South Expeditions">Gone South Expeditions</option>
					<option value="Chill Pill Health Services">Chill Pill Health Services</option>
					<option value="Ozone Hole Air Conditioning">Ozone Hole Air Conditioning</option>
				</select>
			</div>
			<div class="account-selection-area">
				<label for="out-account">From Account</label>
				<select value={props.fromAccount} onChange={props.changePayment} name="out-account">
					<option value="-1" selected hidden disabled>Select Account</option>
					{accountChoices}
				</select>
			</div>
			<div class="amount-area">
				<label for="withdraw-amount">Amount</label>
				<input type="number" value={props.amount} onChange={props.changePayment} name="withdraw-amount" min="0" step="0.01"></input>
			</div>
			<div id="withdraw-error-area">{withdrawError}</div>
			<button onClick={props.payBill} id="pay-button">Complete</button>
		</div>
    );
}