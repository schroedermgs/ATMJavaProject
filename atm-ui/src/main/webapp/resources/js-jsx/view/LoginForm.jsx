function LoginForm(props) {
	let error;
    if (props.statusCode && props.statusCode !== 200)
        error = "Invalid User Credentials";
    return (
		<section id="login-area">
			<div id="login-window">
				<h2>Please Log In</h2>
				<br/>
				<div class="icon">
					<img src="resources/images/penguin-blue.png" />
				</div>
				<div class="text-input-area">
					<label for="username">Username</label>
					<input type="text" spellcheck="false" name="username" autofocus="true" onKeyPress={props.onKey} onChange={props.onChange} value={props.username} />
				</div>
				<div class="text-input-area">
					<label for="password">Password</label>
					<input type="password" name="password" onKeyPress={props.onKey} onChange={props.onPassChange} value={props.password} />
				</div>
				<div id="login-error-area">
					{error}
				</div>
				<div id="login-button-area">
					<button onClick={props.onClick} id="login-button">Log In</button>
				</div>
			</div>
		</section>
    );
}
