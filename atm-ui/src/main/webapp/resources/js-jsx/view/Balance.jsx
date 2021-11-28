function Balance(props) {
    let error;
    if (props.statusCode && props.statusCode !== 200)
        error = props.statusCode === 404 ? "Account Not Found" : "Unexpected Error";
    return <p>Balance: <span style={{color: error ? "red" : "green"}}>{error || props.balance}</span></p>;
}