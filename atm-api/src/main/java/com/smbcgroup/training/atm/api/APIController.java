package com.smbcgroup.training.atm.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smbcgroup.training.atm.ATMService;
import com.smbcgroup.training.atm.ATMServiceException;
import com.smbcgroup.training.atm.ATMServiceException.Type;
import com.smbcgroup.training.atm.Account;
import com.smbcgroup.training.atm.BankTransaction;
import com.smbcgroup.training.atm.User;
import com.smbcgroup.training.atm.dao.AccountDAO;
import com.smbcgroup.training.atm.dao.AccountNotFoundException;
import com.smbcgroup.training.atm.dao.DuplicateAccountException;
import com.smbcgroup.training.atm.dao.DuplicateUserException;
import com.smbcgroup.training.atm.dao.TransactionNotFoundException;
import com.smbcgroup.training.atm.dao.UserNotFoundException;
import com.smbcgroup.training.atm.dao.jpa.AccountJPAImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "ATM API")
public class APIController {

	static ATMService service = new ATMService(new AccountJPAImpl());

	@ApiOperation("Get user")
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getUser(@PathVariable("userId") String userId) {
		try {
			return new ResponseEntity<User>(service.getUser(userId), HttpStatus.OK);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation("Get account")
	@RequestMapping(value = "/accounts/{accountNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> getAccount(@PathVariable("accountNumber") String accountNumber) {
		try {
			return new ResponseEntity<Account>(service.getAccount(accountNumber), HttpStatus.OK);
		} catch (AccountNotFoundException e) {
			return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
		}
	}
	
	@ApiOperation("Get transaction")
	@RequestMapping(value = "/history/{transactionID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BankTransaction> getBankTransaction(@PathVariable("transactionID") String date){
		try {
			return new ResponseEntity<BankTransaction>(service.getBankTransaction(date), HttpStatus.OK);
		} catch(TransactionNotFoundException e) {
			return new ResponseEntity<BankTransaction>(HttpStatus.NOT_FOUND);
		}
	}
	
	@ApiOperation("Get transactions specified by date range")
	@GetMapping(value = "/{accountNumber}/history")
	public List<BankTransaction> getTransactionsList(@PathVariable("accountNumber") String accountNumber, @RequestParam(value="start", defaultValue="0") String startDate, @RequestParam(value="end", defaultValue="0") String endDate) {
		try {
			return service.getTransactionsList(accountNumber, startDate, endDate);
		} catch (Exception e) {
			return new ArrayList<BankTransaction>();
		}
	}
	
	@ApiOperation("Update account balance")
	@PutMapping("/accounts/{accountNumber}")
	public ResponseEntity<Void> updateBalance(@PathVariable("accountNumber") String accountNumber, @RequestParam BigDecimal amount, @RequestParam boolean isWithdrawal, @RequestParam(value="payee", defaultValue="0") String payee){
		try {
			BigDecimal roundedAmount = amount.setScale(2, RoundingMode.DOWN);
			if(isWithdrawal)
				service.withdraw(accountNumber, roundedAmount, payee);
			else
				service.deposit(accountNumber, roundedAmount);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}catch(AccountNotFoundException e) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}catch(ATMServiceException e) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@ApiOperation("Transfer")
	@PostMapping("/accounts/{fromAccountNumber}/transfers")
	public ResponseEntity<Void> transfer(@PathVariable("fromAccountNumber") String fromAccountNumber, @RequestParam("toAccountNumber") String toAccountNumber, @RequestParam("amount") BigDecimal amount){
		try {
			BigDecimal roundedAmount = amount.setScale(2, RoundingMode.DOWN);
			service.transfer(fromAccountNumber, toAccountNumber, roundedAmount);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}catch(AccountNotFoundException e) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}catch(ATMServiceException e) {
			if(e.getType().equals(ATMServiceException.Type.INSUFFICIENT_FUNDS))
				return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
			else
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation("Create a New Account")
	@PostMapping("/users/{userId}/accounts")
	public ResponseEntity<Account> createNewAccount(@PathVariable("userId") String userId){
		try {
			return new ResponseEntity<Account>(service.createNewAccount(userId),HttpStatus.CREATED);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
		} catch (DuplicateAccountException e) {
			return new ResponseEntity<Account>(HttpStatus.CONFLICT);
		}
	}
	
	@ApiOperation("Create NEW USER")
	@PostMapping("/users/{userId}")
	public ResponseEntity<User> createNewUser(@PathVariable("userId") String userId, @RequestParam(value="needsNewAccount", defaultValue="false") boolean needsNewAccount){
		try {
			return new ResponseEntity<User>(service.createNewUser(userId, needsNewAccount),HttpStatus.CREATED);
		}catch(DuplicateAccountException e) {
			return new ResponseEntity<User>(HttpStatus.CONFLICT); 
		}catch(UserNotFoundException e) {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}catch(DuplicateUserException e) {
			return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
		}
	}
	
	@ApiOperation("Get ALL Users in Existence")
	@GetMapping("records/users")
	public List<User> getAllUsers(){
		try {
			return service.getAllUsers();
		}catch(Exception e) {
			return null;
		}
	}
	
	@ApiOperation("Get ALL Accounts in Existence")
	@GetMapping("records/accounts")
	public List<Account> getAllAccounts(){
		try {
			return service.getAllAccounts();
		}catch(Exception e) {
			return null;
		}
	}
	
	@ApiOperation("Delete a specified account")
	@DeleteMapping("/accounts/{accountNumber}")
	public ResponseEntity<Void> deleteAccount(@PathVariable("accountNumber") String accountNumber){
		try {
			service.deleteAccount(accountNumber);
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}catch(AccountNotFoundException e) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
	
	@ApiOperation("Delete a specified transaction")
	@DeleteMapping("/history/{transactionID}")
	public ResponseEntity<Void> deleteTransaction(@PathVariable("transactionID") String date){
		try{
			service.deleteTransaction(date);
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}catch(TransactionNotFoundException e) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
	
	@ApiOperation("DELETE a specified USER (PLEASE BE CAREFUL)")
	@DeleteMapping("users/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId, @RequestParam(value="areYouSure", defaultValue="false") boolean areYouSure){
		if(!areYouSure) {
			return new ResponseEntity<Void>(HttpStatus.PRECONDITION_FAILED);
		}
		try {
			service.deleteUser(userId, areYouSure);
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}catch(UserNotFoundException e) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}
	
	
}
