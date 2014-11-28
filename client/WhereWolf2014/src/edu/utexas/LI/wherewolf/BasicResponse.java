package edu.utexas.LI.wherewolf;

public abstract class BasicResponse {

	protected String status;
	protected String errorMessage;

	public BasicResponse(String status, String errorMessage)
	{
		this.status = status;
		this.errorMessage = errorMessage;

	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}