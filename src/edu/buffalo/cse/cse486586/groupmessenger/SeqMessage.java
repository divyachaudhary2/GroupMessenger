package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;

public class SeqMessage implements Serializable{
	String seq;
	String message;
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
