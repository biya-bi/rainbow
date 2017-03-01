package org.rainbow.shopping.cart.ui.web.model;

import javax.validation.constraints.Min;

import org.rainbow.shopping.cart.model.Product;

public class CartLine {

	private Product product;
	private int quantity = 1;

	public CartLine() {
		super();
	}

	public CartLine(Product product, int quantity) {
		super();
		this.product = product;
		this.quantity = quantity;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Min(0)
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quanity) {
		this.quantity = quanity;
	}

	public double getSubTotal() {
		return quantity * product.getPrice();
	}
}