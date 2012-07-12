package jboost.learner;

class InsufficientSparseMatrixColumns extends Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = -5193616929705902242L;

InsufficientSparseMatrixColumns(String message) {
    this.message = message;
  }

  public String getMessage() {
    return (message);
  }

  private String message;
}
