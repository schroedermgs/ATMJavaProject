function HistoryRow(props) {
	return (
		<tr class="plain-row" onClick={props.zoomIn}>
			<td class="transaction-date">{props.transactionDate}</td>
			<td class="description">{props.transactionDescription}</td>
			<td>{props.transactionAmount}</td>
			<td class="post-balance">{props.postbalance}</td>
		</tr>
    );
}