package org.rainbow.shopping.cart.ui.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.primefaces.event.DragDropEvent;
import org.primefaces.event.FlowEvent;
import org.rainbow.service.api.IService;
import org.rainbow.shopping.cart.model.Category;
import org.rainbow.shopping.cart.model.Order;
import org.rainbow.shopping.cart.model.OrderDetail;
import org.rainbow.shopping.cart.model.OrderStatus;
import org.rainbow.shopping.cart.model.Product;
import org.rainbow.shopping.cart.ui.web.model.CartLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Named
@SessionScoped
public class StoreController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1715872331359355404L;

	private static final Logger _log = Logger.getLogger(StoreController.class.getName());

	@Autowired
	@Qualifier("categoryService")
	private IService<Category> categoryService;

	@Autowired
	@Qualifier("orderService")
	private IService<Order> orderService;

	private List<Product> products = new ArrayList<>();

	private Order order = new Order();

	private boolean skip;

	private double cartTotal = 0;

	private List<CartLine> cartLines = new ArrayList<>();

	private enum CartOperation {
		ADD, REMOVE
	}

	private CartLine bulkCartLine = new CartLine();

	public List<Product> getProducts() {
		return products;
	}

	public void populateProductsForCategory(Category category) throws Exception {
		this.products = categoryService.findById(category.getId()).getProducts();
	}

	public void addToCart(Product product) {
		adjustCart(product, CartOperation.ADD, 1);
	}

	public void onProductDrop(DragDropEvent ddEvent) {
		if (ddEvent.getData() instanceof Product) {
			Product product = ((Product) ddEvent.getData());
			adjustCart(product, CartOperation.ADD, 1);
		}
	}

	private void adjustCart(Product product, CartOperation cartOperation, int quantity) {
		if (quantity < 1)
			return;
		CartLine cartLine = null;
		for (CartLine line : cartLines) {
			if (product.equals(line.getProduct())) {
				cartLine = line;
				break;
			}
		}
		int newQuantity = 0;
		if (cartOperation == CartOperation.ADD) {
			if (cartLine == null) {
				cartLine = new CartLine(product, 0);
				cartLines.add(cartLine);
			}
			newQuantity = cartLine.getQuantity() + quantity;

			cartTotal += product.getPrice() * quantity;
			_log.info("Successfully added " + quantity + " product(s) with name '" + product.getName()
					+ "' to cart. Total is: " + cartTotal);
		} else {
			if (cartLine != null) {
				quantity = Math.min(cartLine.getQuantity(), quantity);

				newQuantity = cartLine.getQuantity() - quantity;
				if (newQuantity <= 0)
					cartLines.remove(cartLine);

				cartTotal -= product.getPrice() * quantity;
				if (cartTotal <= 0)
					cartTotal = 0;
				_log.info("Successfully removed " + quantity + " product(s) with name '" + product.getName()
						+ "' from cart. Total is: " + cartTotal);
			}
		}
		cartLine.setQuantity(newQuantity);
	}

	public void removeFromCart(Product product) {
		adjustCart(product, CartOperation.REMOVE, 1);
	}

	public double getCartTotal() {
		return cartTotal;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void checkoutOrder() throws Exception {

		List<OrderDetail> details = new ArrayList<>();

		for (CartLine line : cartLines) {
			OrderDetail detail = new OrderDetail();
			detail.setOrder(order);
			detail.setProduct(line.getProduct());
			detail.setPrice(line.getProduct().getPrice());
			detail.setQuantity(line.getQuantity());
			detail.setAmount(line.getSubTotal());
			details.add(detail);
		}
		order.setDetails(details);
		order.setOrderDate(new Date());
		order.setStatus(OrderStatus.OPEN);

		orderService.create(order);

		_log.info("Clearing after Check out..");

		cartTotal = 0;
		order = new Order();
		cartLines.clear();

	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public String onFlowProcess(FlowEvent event) {
		_log.info("Current wizard step:" + event.getOldStep());
		_log.info("Next step:" + event.getNewStep());

		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			return event.getNewStep();
		}
	}

	public void initBulkCartLine(Product product) {
		bulkCartLine = new CartLine();
		bulkCartLine.setProduct(product);
	}

	public List<CartLine> getCartLines() {
		return cartLines;
	}

	public CartLine getBulkCartLine() {
		return bulkCartLine;
	}

	public void bulkAddToCart() {
		adjustCart(bulkCartLine.getProduct(), CartOperation.ADD, bulkCartLine.getQuantity());
	}

	public void bulkRemoveFromCart() {
		adjustCart(bulkCartLine.getProduct(), CartOperation.REMOVE, bulkCartLine.getQuantity());
	}
}
