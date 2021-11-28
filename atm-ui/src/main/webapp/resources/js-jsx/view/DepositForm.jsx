class DepositForm extends React.Component{
	constructor(props){
		super(props);
		this.displayPicturePrompt = this.displayPicturePrompt.bind(this);
	} 
	displayPicturePrompt(){
		if(this.props.depositAmount>0 && this.props.depositAccount!="-1"){
			return(
				<div>
					<div id="picture-prompt-area">
						<label for="camera-button">Take a Picture</label>
						<img id="camera-button" onClick={this.props.takePicture} src="resources/images/camera-button.png"></img>
					</div>
					<div id="check-area"></div>
					<button id="deposit-button" onClick={this.props.finalizeDeposit} hidden>Confirm</button>
				</div>
			);
		}
	}
	render(){
		const accountChoices = this.props.accountNumbers.map((acct)=> <option value={acct} key={acct}>#{acct}</option>);
		const PicturePrompt = this.displayPicturePrompt();
		return(
			<div id="deposit-form">
				<h1>Deposit a Check</h1>
				<div class="account-selection-area">
					<label for="into-account">Into</label>
					<select id="into-account" name="into-account" value={this.props.depositAccount} onChange={this.props.depositChange}>
						<option value="-1" selected hidden disabled>Select Account</option>
						{accountChoices}
					</select>
				</div>
				<div class="amount-area">
					<label for="deposit-amount">Amount</label>
					<input id="deposit-amount" type="number" onChange={this.props.depositChange} value={this.props.depositAmount} name="deposit-amount" min="0" step="0.01"></input>
				</div>
				{PicturePrompt}
			</div>
		);
	}
}


				
				
				