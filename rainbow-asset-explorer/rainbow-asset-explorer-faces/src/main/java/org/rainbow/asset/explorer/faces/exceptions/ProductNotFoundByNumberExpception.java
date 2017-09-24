package org.rainbow.asset.explorer.faces.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class ProductNotFoundByNumberExpception extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7552665196590077007L;
	private final String productNumber;

    public ProductNotFoundByNumberExpception(String productNumber) {
        super(String.format("No product with number %s was found.", productNumber));
        this.productNumber = productNumber;
    }

    public ProductNotFoundByNumberExpception(String productNumber, String message) {
        super(message);
        this.productNumber = productNumber;
    }

    public String getProductNumber() {
        return productNumber;
    }

}
