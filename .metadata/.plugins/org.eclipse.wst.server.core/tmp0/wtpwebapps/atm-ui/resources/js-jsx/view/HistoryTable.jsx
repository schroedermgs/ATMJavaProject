function HistoryTable(props) {
	const historyRows = props.historyRows;
	return (
		<div id="history-component">
			<div class="history-top">
				<img id="history-back-button" src="resources/images/back-arrow.png" onClick={props.goBack}></img>
				<h1>Acct #{props.accountNumber}</h1>
			</div>
			<table id="history-table" width="100%" align="center">
				<tr class="header-row account-history-title">
					<th width="25%">Date</th>
					<th width="25%">Description</th>
					<th width="25%">Amount</th>
					<th width="25%">Balance</th>
				</tr>
				{historyRows.length!=0 ? historyRows : <tr width="100%"><td id="no-results-tag" colspan="4">No results for your search.</td></tr>}
			</table>
		</div>
    );
}
