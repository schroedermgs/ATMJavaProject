function NewAccountForm(props){
	let newAccountMessage = null;
	if(props.statusCode && props.statusCode!=201){
		newAccountMessage = "There was a problem creating a new account for you. Please try again later.";
	}else if(props.statusCode && props.statusCode==201){
		newAccountMessage = null;
	}
	return(
		<div id="new-acct-form">
			<h1>Open a New Account</h1>
			<p id="main-message">Would you like to open a new account for yourself, <span id="this-user">{loggedInUser}</span>?</p>
			<p id="new-acct-message"><span>{newAccountMessage}</span></p> 
			<div id="buttons-container">
				<button id="yes" onClick={props.confirmNew}>Yes</button>
				<button id="no" onClick={props.rejectNew}>No</button>
			</div>
		</div>
	);
}