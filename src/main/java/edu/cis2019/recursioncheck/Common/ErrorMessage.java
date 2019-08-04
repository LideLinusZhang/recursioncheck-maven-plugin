package edu.cis2019.recursioncheck.Common;

public enum ErrorMessage {
	NO_BASE_CASE("Definite infinite recursion: This recursive method contains no base case"),
	MUTUAL_RECURSIVE_WARNING("Possible infinite recursion: This method may be mutually recursive with another method"),
	PARAMETERS_UNCHANGED("Possible infinite recursion: Parameters are unchanged from initial call to recursive call"),
	;
    
	private ErrorMessage(String message) {
		this.errorMessage = message;
	}

    public String getErrorMessage() {
        return errorMessage;
    }
    
	private String errorMessage;    
}
