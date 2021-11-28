var userAccounts = new Array();
var loggedInUser = "";

class LoginController extends React.Component{
	constructor(props){
		super(props);
		this.state = new LoginModel("","");
		this.onChange = this.onChange.bind(this);
		this.onPassChange = this.onPassChange.bind(this);
		this.getUser = this.getUser.bind(this);
		this.onKey = this.onKey.bind(this);
		this.alertMustLogIn = this.alertMustLogIn.bind(this);
	}
	componentWillMount(){
		document.getElementById("new-link").addEventListener("click",this.alertMustLogIn);
		document.getElementById("deposit-link").addEventListener("click",this.alertMustLogIn);
		document.getElementById("withdraw-link").addEventListener("click",this.alertMustLogIn);
	}
	componentDidMount(){
		window.scrollTo(0,0);
	}
	onChange(event){
		this.setState(new LoginModel(event.target.value,this.state.password));
	}
	onPassChange(event){
		this.setState(new LoginModel(this.state.username,event.target.value));
	}
	onKey(event){
		event.charCode==13 ? this.getUser() : null;
	}
	getUser(){
		const userId = this.state.username;
        let handleResponse = (status, accounts) => this.setState({responseStatus: status, accounts: accounts});
        handleResponse = handleResponse.bind(this);
        $.ajax({
            url: "/atm-api/users/" + userId.replace(" ","").toLowerCase(),
            type: "GET",
            success: function(response) {
                handleResponse(200, response.accounts);
				loggedInUser = userId.replace(" ","").toLowerCase();
				document.getElementById("logged-user").innerHTML = loggedInUser;
				userAccounts = new Array();
				response.accounts.forEach(function(acct){
					userAccounts.push(acct);
				});
				window.scrollTo(0,0);
				$("header h3").fadeIn(500);
				$("#app").slideUp(800);
				setTimeout(()=>ReactDOM.render(<BankingAreaController accountNumbers={userAccounts} />, document.getElementById("app")),800);
				$("#app").slideDown(800);
            },
            error: function(xhr, status, error) {
                handleResponse(xhr.status);
				$("header h3").hide();
				$("#login-error-area").hide().slideDown(500);
				loggedInUser = "";
				document.getElementById("logged-user").innerHTML = "";
				userAccounts = new Array();
				document.getElementById("penguin-sound").volume = 0.2;
				document.getElementById("penguin-sound").play();
				setTimeout(()=> $("#login-error-area").slideUp(800),2800);
            }
        });
	}
	alertMustLogIn(){
		$("#login-error-area").empty().append("Please log in first").hide().slideDown(500);
		document.getElementById("penguin-sound").volume = 0.2;
		document.getElementById("penguin-sound").play();
		setTimeout(()=>$("#login-error-area").slideUp(800),2500);
	}
	render(){
		return(
			<LoginForm onChange={this.onChange} onKey={this.onKey} username={this.state.username} onPassChange={this.onPassChange} password={this.state.password} onClick={this.getUser} statusCode={this.state.responseStatus} />
		);
	}
	componentWillUnmount(){
		document.getElementById("new-link").removeEventListener("click",this.alertMustLogIn);
		document.getElementById("deposit-link").removeEventListener("click",this.alertMustLogIn);
		document.getElementById("withdraw-link").removeEventListener("click",this.alertMustLogIn);
	}
}
ReactDOM.render(<LoginController />, document.getElementById("app"));


function logout(){
	$("header h3").fadeOut(500); 
	userAccounts = new Array(); 
	loggedInUser = ""; 
	setTimeout(()=>$("#logged-user").empty(),500); 
	$("#logout").hide(); 
	ReactDOM.render(<LoginController />, document.getElementById("app"));
	window.scrollTo(0,0);
	clearInterval(timerTick);
	resetTimer();
	}

document.getElementById("logout").addEventListener("click",logout);