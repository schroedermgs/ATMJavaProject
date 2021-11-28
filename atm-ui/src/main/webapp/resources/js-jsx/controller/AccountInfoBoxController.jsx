class AccountInfoBoxController extends React.Component {
    constructor(props) {
        super(props);
        this.state = new AccountInfoBoxModel(props.accountNumber);
        this.getInfo = this.getInfo.bind(this);
    }
	componentWillMount(){
		this.setState({responseStatus:null}, this.getInfo());
		this.forceUpdate();
	}
    getInfo() {
        const accountNumber = this.state.accountNumber;
        let handleResponse = (status, balance, transactions) => this.setState({responseStatus: status, balance: balance, history: transactions});
        handleResponse = handleResponse.bind(this);
        $.ajax({
            url: "/atm-api/accounts/" + accountNumber,
            type: "GET",
            success: function(response) {
                handleResponse(200, currencyFormatter.format(response.balance.toFixed(2)), response.transactions);
            },
            error: function(xhr, status, error) {
                handleResponse(xhr.status);
            }
        });
    }
    render() {
        return (
			<AccountInfoBox accountNumber={this.state.accountNumber} statusCode={this.state.responseStatus} 
			onSelectAccount={this.props.onSelectAccount} balance={this.state.balance} />
        );
    }
}
