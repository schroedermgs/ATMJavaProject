class HistoryForm extends React.Component {
	constructor(props){
		super(props);
	}
	render(){
		return(
			<div id="history-form">
				<div>
					<h1>Acct #{this.props.accountNumber}</h1>
				</div>
				<div class="history-date-area">
					<label for="start-date">Start Date <span>(optional)</span></label>
					<input id="start-date" value={this.props.startDate} onChange={this.props.changeHistoryDetails} type="date" name="start-date" autocomplete="off"></input>
				</div>
				<div class="history-date-area">
					<label for="end-date">End Date <span>(optional)</span></label>
					<input id="end-date" value={this.props.endDate} onChange={this.props.changeHistoryDetails} type="date" name="end-date" autocomplete="off"></input>
				</div>
				<div id="finalize-history-area">
					<button onClick={this.props.getHistory} type="submit" id="show-history-button">Show History</button>
				</div>
			</div>
    	);
	}
}