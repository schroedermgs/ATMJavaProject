function CheckBalanceForm(props) {
    return (
        <div>
            <h2>Check Balance</h2>
            <label for="accountNumber">Account Number: </label>
            <input type="text" name="accountNumber" onChange={props.onChange} value={props.accountNumber} />
            <button onClick={props.onClick}>Check Balance</button>
        </div>
    );
}