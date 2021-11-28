function FakeCheck(props){
	const date = new Date();
	const today = date.toLocaleDateString();
	return(
		<div id="check">
			<div class="check-top">
				<span class="name-address">
					<div class="check-name">Roald E. Amundsen</div>
					<div class="check-address-1">40 Below Zero Ave.</div>
					<div class="check-address-2">Aurora, ANT 90000</div>
				</span>
				<span class="check-no">No. 549 </span>
			</div>
			<div class="check-date">Date <span id="the-date">{today}</span></div>
			<div class="pay-to">Pay to the Order of:  <span id="user-on-check">{props.username}</span><span id="check-amount">{currencyFormatter.format(Number(props.depositAmount).toFixed(2))}</span></div>
			<div class="logo-area"><img src="resources/images/antarctica-symbol.png"></img>BANK OF ANTARCTICA</div>
			<div class="check-auth">
				<span class="for">For <span id="purpose">Official Business</span></span>
				<span class="signature-area"><img class="signature" src="resources/images/amundsen-signature.png"></img></span>
			</div>
			<div class="numbers">|:523877091 |:   01126539047 || 0549</div>
		</div>
	);
}