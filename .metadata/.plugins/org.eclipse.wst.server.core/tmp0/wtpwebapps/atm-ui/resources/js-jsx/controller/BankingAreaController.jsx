var currencyFormatter = new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD',});

class BankingAreaController extends React.Component {
	constructor(props) {
        super(props);
		this.onSelectAccount = this.onSelectAccount.bind(this);
		this.changeHistoryDetails = this.changeHistoryDetails.bind(this);
		this.accountNumbers = props.accountNumbers;
		this.penguin = this.penguin.bind(this);
		this.state = new BankingModel(this.accountNumbers);
		this.state.accountBoxes = this.accountNumbers.map((acct)=><AccountInfoBoxController accountNumber={acct} onSelectAccount={this.onSelectAccount} key={acct} />);
		this.totalBalance = this.state.accountBoxes.forEach
		this.initializeTransfer = this.initializeTransfer.bind(this);
		this.prepareTransferForm = this.prepareTransferForm.bind(this);
		this.changeTransferDetails = this.changeTransferDetails.bind(this);
		this.finalizeTransfer = this.finalizeTransfer.bind(this);
		this.getHistory = this.getHistory.bind(this);
		this.toggleZoom = this.toggleZoom.bind(this);
		this.goBackToSearch = this.goBackToSearch.bind(this);
		this.onDepositChange = this.onDepositChange.bind(this);
		this.takePicture = this.takePicture.bind(this);
		this.initializeDeposit = this.initializeDeposit.bind(this);
		this.finalizeDeposit = this.finalizeDeposit.bind(this);
		this.initializeWithdraw = this.initializeWithdraw.bind(this);
		this.onChangePayment = this.onChangePayment.bind(this);
		this.checkIfValidPayment = this.checkIfValidPayment.bind(this);
		this.payBill = this.payBill.bind(this);
		this.askToConfirmAccount = this.askToConfirmAccount.bind(this);
		this.confirmNew = this.confirmNew.bind(this);
		this.rejectNew = this.rejectNew.bind(this);
		this.updateTotal = this.updateTotal.bind(this);
    }
	componentWillMount(){
		document.getElementById("deposit-link").addEventListener("click",this.initializeDeposit);
		document.getElementById("withdraw-link").addEventListener("click",this.initializeWithdraw);
		document.getElementById("new-link").addEventListener("click",this.askToConfirmAccount);
		$("#logout").show();
	}
	componentDidMount(){
		this.setState({formType:"begin"});
		window.scrollTo(0,0);
		this.updateTotal(1650);
		counting = true;
		clearInterval(timerTick);
		timerTick = setInterval(countdownTimeout,1000);
	}
	updateTotal(fadeInTime){
		const balanceLabels = [].slice.call(document.getElementsByClassName("acct-balance"));
		var balances = null;
		let totalBefore;
		try{
			totalBefore = $("#sum").text().replace("$","").replace(",","");
		}catch(e){}
		var totalBalance = 0;
		setTimeout(()=>{
			balances = balanceLabels.map((acct)=>Number(acct.innerText.replace("$","").replace(",","").replace("DISPLAY ERROR",""))); 
			for(let balance of balances){
				totalBalance += balance;
			}; 
			if(!totalBefore || (totalBefore && totalBefore!=totalBalance)){
				$("#balances-total").empty().append('Balance Totals: <span id="sum">'+currencyFormatter.format(totalBalance)+"</span>").hide().show(400);
			};
		},fadeInTime);
	}
    initializeTransfer() {
		this.setState({formType:"transfer",transferFrom:"-1",transferTo:"-1",transferDate:null,transferAmount:"0",transferStatus:null},()=>{this.prepareTransferForm(); $("#finalize-transfer-button").prop('disabled',false);});
    }
	prepareTransferForm(){
		$("#transfer-component").hide();
		try{
			document.getElementById("select-from").selectedIndex = 0;
			document.getElementById("select-to").selectedIndex = 0;
			document.getElementById("schedule-date").value = null;
			document.getElementById("amount").value = "";
		}catch(e){}
		$("#transfer-component").slideDown(600);
	}
	changeTransferDetails(event){
		if(event.target.name=="from-account")
			this.setState({transferFrom: event.target.value});
		else if(event.target.name=="to-account")
			this.setState({transferTo: event.target.value});
		else if(event.target.name=="schedule-date")
			this.setState({transferDate: event.target.value});
		else if(event.target.name=="transfer-amount"){
			if (event.target.value==null || event.target.value==""){
				this.setState({transferAmount: ""})
			}
			else{
				this.setState({transferAmount: fixTwoDecimals(event.target.value)});
			}
		}
	}
	finalizeTransfer(){
		$("#finalize-transfer-button").prop('disabled',true);
		const _this = this;
		const fromAccount = this.state.transferFrom;
		const toAccount = this.state.transferTo;
		const amount = (this.state.transferAmount==null || this.state.transferAmount=="") ? "0" : this.state.transferAmount;
		let handleResponse = (status)=> this.setState({transferStatus: status});
		handleResponse = handleResponse.bind(this);
		$.ajax({
            url: "/atm-api/accounts/" + fromAccount + "/transfers" + "?toAccountNumber="+toAccount+"&amount="+amount,
            type: "POST",
            success: function(response) {
                handleResponse(200);
				var newAcctBoxes = _this.accountNumbers.map((acct)=><AccountInfoBoxController accountNumber={acct} onSelectAccount={_this.onSelectAccount} key={acct} />)
				_this.setState({accountBoxes: new Array()},()=>_this.setState({accountBoxes:newAcctBoxes}));
				$("#transfer-component").slideUp(500);
				var successNote = '<div id="transfer-success-note" style="text-align: center;"><label>Transfer Successful!</label>'+
									'<div><img src="resources/images/penguin-blue.png"></img></div></div>';
				$("#transaction-window").append(successNote);
				setTimeout(()=>$("#transfer-success-note").slideUp(400),2100);
				setTimeout(()=>{$("#transfer-success-note").remove(); _this.setState({formType:null});},2500);
            },
            error: function(xhr, status, error) {
                handleResponse(xhr.status);
				$("#transfer-error").hide().slideDown(800);
				setTimeout(()=> $("#transfer-error").slideUp(800),2800);
				$("#finalize-transfer-button").prop('disabled',false);
				document.getElementById("penguin-sound").volume = 0.2;
				document.getElementById("penguin-sound").play();
            }
        });
	}
	onSelectAccount(event){
		this.setState({formType:"history", selectedAccount:event.target.id, startDate:null, endDate: null, historyRows:[]},()=>$("#transaction-window").children().hide().slideDown(700));
		try{
			document.getElementById("start-date").value = null;
			document.getElementById("end-date").value = null;
		}catch(e){}
	}
	changeHistoryDetails(event){
		if(event.target.name=="start-date")
			this.setState({startDate: event.target.value});
		else if(event.target.name=="end-date")
			this.setState({endDate: event.target.value});
	}
	getHistory(){
		const _this = this;
		const accountNumber = this.state.selectedAccount;
		var startDate = this.state.startDate;
		var endDate = this.state.endDate;
		try{
			startDate = [startDate.split("-")[1],startDate.split("-")[2],startDate.split("-")[0]].join("-");
		}catch(e){startDate="0"}
		try{
			endDate = [endDate.split("-")[1],endDate.split("-")[2],endDate.split("-")[0]].join("-");
		}catch(e){endDate="0"}
		$.ajax({
            url: "/atm-api/" + accountNumber + "/history" + "?start="+startDate+"&end="+endDate,
            type: "GET",
            success: function(response) {
                var historyRows = response.map((object)=><HistoryRow transactionDate={object.date.replace(/-/g,"/").slice(0,10)} transactionDescription={object.description} transactionAmount={(object.amount<0 ? "–"+currencyFormatter.format(Math.abs(object.amount).toFixed(2)) : "+"+currencyFormatter.format(object.amount.toFixed(2)))} postbalance={currencyFormatter.format(object.postbalance.toFixed(2))} zoomIn={_this.toggleZoom} />)
				_this.setState({formType:"table", historyRows:historyRows},()=>$("#transaction-window").children().hide().slideDown(700));
            },
            error: function(xhr, status, error) {
                console.log(xhr.status);
            }
        });
	}
	toggleZoom(event){
		const rows = event.currentTarget.children;
		for(let row of rows){
			if(row.style.fontSize == "17px"){
				row.style.fontSize = ""; row.style.fontWeight = "";
				row.style.backgroundColor = "";
				row.style.height = ""; row.style.cursor = "";
			}else{
				row.style.fontSize = "17px"; row.style.fontWeight = "bold";
				row.style.backgroundColor = "#deebf7";
				row.style.height = "100px"; row.style.cursor = "zoom-out";
			}
		}
	}
	goBackToSearch(){
		this.setState({formType:"history"});
	}
	initializeDeposit(){
		this.setState({formType:"deposit",depositAmount:"0",depositAccount:"-1"},()=>{$("#deposit-form").hide().slideDown(600); $("#into-account").prop('disabled',false); $("#deposit-amount").prop('disabled',false);});
	}
	onDepositChange(event){
		if(event.target.name=="into-account"){
			this.setState({depositAccount:event.target.value});
		}else if(event.target.name=="deposit-amount"){
			this.setState({depositAmount:event.target.value});
		}
	}
	takePicture(){
		ReactDOM.render(<FakeCheck username={loggedInUser} depositAmount={this.state.depositAmount} />,document.getElementById("check-area")); 
		$("#check").hide().slideDown(600);
		$("#deposit-button").slideDown(600);
		$("#into-account").prop('disabled',true);
		$("#deposit-amount").prop('disabled',true);
	}
	finalizeDeposit(){
		const _this = this;
		const intoAccount = this.state.depositAccount;
		const amount = this.state.depositAmount;
		$.ajax({
            url: "/atm-api/accounts/" + intoAccount + "?amount="+amount+"&isWithdrawal=false",
            type: "PUT",
            success: function(response) {
				var newAcctBoxes = _this.accountNumbers.map((acct)=><AccountInfoBoxController accountNumber={acct} onSelectAccount={_this.onSelectAccount} key={acct} />)
				_this.setState({accountBoxes: new Array()},()=>_this.setState({accountBoxes:newAcctBoxes}));
				$("#deposit-form").slideUp(500);
				var successNote = '<div id="deposit-success-note" style="text-align: center;"><label>Deposit Successful!</label>'+
									'<div><img src="resources/images/penguin-blue.png"></img></div></div>';
				$("#transaction-window").append(successNote);
				setTimeout(()=>$("#deposit-success-note").slideUp(400),2100);
				setTimeout(()=>{$("#deposit-success-note").remove(); _this.setState({formType:null});},2500);
				_this.updateTotal(2100);
            },
            error: function(xhr, status, error) {
				_this.setState({formType:null});
				setTimeout(()=>alert(xhr.status+": Deposit Error! Something went wrong with Account #"+intoAccount+"."),800);
				document.getElementById("penguin-sound").volume = 0.2;
				document.getElementById("penguin-sound").play();
            }
        });
	}
	initializeWithdraw(){
		this.setState({formType:"payment",withdrawStatus:null, payee:"", withdrawalAccount:"-1", withdrawalAmount:"0"},
		()=>{$("#bill-form").hide().slideDown(600); document.getElementById("payee").selectedIndex=0; $("#pay-button").prop('disabled',true);});
	}
	onChangePayment(event){
		if(event.target.name=="payee"){
			this.setState({payee:event.target.value},()=>this.checkIfValidPayment());
		}else if(event.target.name=="out-account"){
			this.setState({withdrawalAccount:event.target.value},()=>this.checkIfValidPayment());
		}else if(event.target.name=="withdraw-amount"){
			this.setState({withdrawalAmount:event.target.value},()=>this.checkIfValidPayment());
		}
	}
	checkIfValidPayment(){
		(this.state.payee!=""&&this.state.withdrawalAccount!="-1"&&this.state.withdrawalAmount>0) ? $("#pay-button").prop('disabled',false) : $("#pay-button").prop('disabled',true);
	}
	payBill(){
		const _this = this;
		const payee = this.state.payee;
		const fromAccount = this.state.withdrawalAccount;
		const amount = this.state.withdrawalAmount;
		let handleResponse = (status)=> this.setState({withdrawStatus: status});
		handleResponse = handleResponse.bind(this);
		$.ajax({
            url: "/atm-api/accounts/" + fromAccount + "?amount="+amount+"&isWithdrawal=true"+"&payee="+payee,
            type: "PUT",
            success: function(response) {
				handleResponse(200);
				var newAcctBoxes = _this.accountNumbers.map((acct)=><AccountInfoBoxController accountNumber={acct} onSelectAccount={_this.onSelectAccount} key={acct} />)
				_this.setState({accountBoxes: new Array()},()=>_this.setState({accountBoxes:newAcctBoxes}));
				$("#bill-form").slideUp(500);
				var successNote = '<div id="withdraw-success-note" style="text-align: center;"><label>Bill Paid Successfully!</label>'+
									'<div><img src="resources/images/penguin-blue.png"></img></div></div>';
				$("#transaction-window").append(successNote);
				setTimeout(()=>$("#withdraw-success-note").slideUp(400),2100);
				setTimeout(()=>{$("#withdraw-success-note").remove(); _this.setState({formType:null});},2500);
            	_this.updateTotal(2100);
			},
            error: function(xhr, status, error) {
				handleResponse(xhr.status);
				$("#withdraw-error-area").hide().slideDown(800);
				setTimeout(()=> $("#withdraw-error-area").slideUp(800),2800);
				document.getElementById("penguin-sound").volume = 0.2;
				document.getElementById("penguin-sound").play();
            }
        });
	}
	askToConfirmAccount(){
		this.setState({formType:"create", newAccountStatus:null},
		()=>{$("#new-acct-form").hide().slideDown(600); $("#yes").prop('disabled',false);});
	}
	confirmNew(){
		const _this = this;
		const userId = loggedInUser;
		$("#yes").prop('disabled',true);
		let handleResponse = (status)=> this.setState({newAccountStatus:status});
		handleResponse = handleResponse.bind(this);
		$.ajax({
			url: "/atm-api/users/" + userId + "/accounts",
			type: "POST",
			success: function(response){
				handleResponse(201);
				_this.accountNumbers.push(response.accountNumber)
				_this.setState({accountNumbers:_this.accountNumbers});
				var newAcctBoxes = _this.accountNumbers.map((acct)=><AccountInfoBoxController accountNumber={acct} onSelectAccount={_this.onSelectAccount} key={acct} />)
				_this.setState({accountBoxes: new Array()},()=>_this.setState({accountBoxes:newAcctBoxes}));
				$("#new-acct-form").slideUp(500);
				var successNote = '<div id="creation-success-note" style="text-align: center;"><label>Account #'+_this.accountNumbers[(_this.accountNumbers).length-1]+' Created Successfully!</label>'+
									'<div><img src="resources/images/penguin-blue.png"></img></div></div>';
				$("#transaction-window").append(successNote);
				setTimeout(()=>$("#creation-success-note").slideUp(400),3000);
				setTimeout(()=>{$("#creation-success-note").remove(); _this.setState({formType:null});},3500);
			},
			error: function(xhr, status, error){
				handleResponse(xhr.status);
				$("#new-acct-message").hide().slideDown(800);
				setTimeout(()=>$("#new-acct-message").slideUp(800),3500);
				document.getElementById("penguin-sound").volume = 0.2;
				document.getElementById("penguin-sound").play();
			}
		})
	}
	rejectNew(){
		$("#transaction-window").children().slideUp(700);
		setTimeout(()=>this.setState({formType:null, newAccountStatus:null}),800);
	}
	penguin(){
		$("#transaction-window").children().slideUp(700);
		setTimeout(()=>this.setState({formType:null}),800);
	}
    render(){
		return (
			<section id="banking-area">
		        <div id="accounts-area">
					<div id="accounts-top">
						<h2>Your Accounts</h2>
						<abbr title="Transfer"><img id="initialize-transfer-button" onClick={this.initializeTransfer} src="resources/images/transfer-arrows.png"></img></abbr>
					</div>
					<div id="accounts-window">
						{this.state.accountBoxes}
						<div id="window-bottom"><p id="balances-total"></p></div>
					</div>
				</div>
				<TransactionWindowArea formType={this.state.formType} selectedAccount={this.state.selectedAccount} startDate={this.state.startDate} endDate={this.state.endDate} changeHistoryDetails={this.changeHistoryDetails} getHistory={this.getHistory} historyRows={this.state.historyRows} goBack={this.goBackToSearch} zoomIn={this.toggleZoom} accountNumbers={this.state.accountNumbers} fromAccount={this.state.transferFrom} toAccount={this.state.transferTo} changeTransferDetails={this.changeTransferDetails} transferAmount={this.state.transferAmount} date={this.state.transferDate} finalizeTransfer={this.finalizeTransfer} transferStatus={this.state.transferStatus} depositAmount={this.state.depositAmount} depositAccount={this.state.depositAccount} depositChange={this.onDepositChange} takePicture={this.takePicture} finalizeDeposit={this.finalizeDeposit} withdrawStatus={this.state.withdrawStatus} changePayment={this.onChangePayment} withdrawalAccount={this.state.withdrawalAccount} withdrawalAmount={this.state.withdrawalAmount} payBill={this.payBill} newAccountStatus={this.state.newAccountStatus} confirmNew={this.confirmNew} rejectNew={this.rejectNew} />
				<MiddleArea penguin={this.penguin} />
			</section>
	    );
	}
	componentWillUnmount(){
		document.getElementById("deposit-link").removeEventListener("click",this.initializeDeposit);
		document.getElementById("withdraw-link").removeEventListener("click",this.initializeWithdraw);
		document.getElementById("new-link").removeEventListener("click",this.askToConfirmAccount);
	}
}


