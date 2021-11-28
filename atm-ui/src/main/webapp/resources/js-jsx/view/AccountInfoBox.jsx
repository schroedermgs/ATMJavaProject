function AccountInfoBox(props) {
	let error;
    if (props.statusCode && props.statusCode !== 200)
        error = props.statusCode === 404 ? "DISPLAY ERROR" : "Unexpected Error";
    return (
        <div class="account-info">
			<h3><span onClick={props.onSelectAccount} id={props.accountNumber}>Acct #{props.accountNumber}</span></h3>
			<div class="acct-box">
				<label class="balance-label">Available Balance</label>
				<label class="acct-balance">{error || props.balance}</label>
			</div>
		</div>
    );
}