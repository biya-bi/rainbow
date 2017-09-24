package org.rainbow.asset.explorer.scheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.rainbow.asset.explorer.orm.entities.Alert;
import org.rainbow.asset.explorer.orm.entities.AlertCategory;
import org.rainbow.asset.explorer.orm.entities.AlertType;
import org.rainbow.asset.explorer.orm.entities.EmailRecipient;
import org.rainbow.asset.explorer.orm.entities.EmailTemplate;
import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductAlertInfo;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javafx.util.Pair;

public class AlertJob extends QuartzJobBean {
	private static final String LOCATION_ID_ATTR = "id";
	private static final String PRODUCT_ID_ATTR = "id";
	private static final String PRODUCT_INVENTORY_ID_ATTR = "id";
	private static final String PRODUCT_INVENTORY_LOCATION_ID_ATTR = "locationId";
	private static final String PRODUCT_INVENTORY_PRODUCT_ID_ATTR = "productId";
	private static final String PRODUCT_INVENTORY_QUANTITY_ATTR = "quantity";

	private static final String PRODUCT_REORDER_POINT_ATTR = "reorderPoint";
	private static final String ITEM_ALERT_INFO_ID_ATTR = "id";
	private static final String ITEM_ALERT_INFO_LOCATION_ID_ATTR = "locationId";
	private static final String ITEM_ALERT_INFO_ITEM_ID_ATTR = "productId";
	private static final String ITEM_ALERT_INFO_ALERT_CATEGORY_ATTR = "alertCategory";
	private static final String PRODUCT_ALERT_INFO_ALERT_TYPE_ATTR = "alertType";

	private static final String ITEM_NUMBER_HEADER = "Item Number";
	private static final String ITEM_NAME_HEADER = "Item Name";
	private static final String ITEM_REORDER_POINT_HEADER = "Reorder Point";
	private static final String AVAILABLE_QUANTITY_HEADER = "Available Quantity";
	private static final String STOCK_LEVEL_WARNING_SUBJECT = "Stock Level Warning";
	private static final String STOCK_LEVEL_RECOVERY_SUBJECT = "Stock Level Recovery";
	private static final String LOCATION_NAME_HEADER = "Location Name";

	private static final String ALERT_EMAIL_RECIPIENT_NAME_PLACE_HOLDER = "%%alert.stockLevel.emailRecipient.name%%";
	private static final String ALERT_CURRENT_DATE_PLACE_HOLDER = "%%alert.stockLevel.current.date%%";
	private static final String ALERT_INVENTORY_DETAILS_PLACE_HOLDER = "%%alert.stockLevel.inventory.details%%";

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private AlertCategory alertCategory;
	private AlertType alertType;
	private Long locationId;
	private HashMap<Location, HashMap<Product, Short>> productQuantitiesByLocation;

	private EntityManager em;

	private JavaMailSender mailSender;

	private String applicationName;

	public AlertJob() {
	}

	@SuppressWarnings("unchecked")
	private void run(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		if (jobDataMap.containsKey("alertCategory")) {
			alertCategory = (AlertCategory) jobDataMap.get("alertCategory");
		}
		if (jobDataMap.containsKey("alertType")) {
			alertType = (AlertType) jobDataMap.get("alertType");
		}
		if (jobDataMap.containsKey("locationId")) {
			locationId = jobDataMap.getLong("locationId");
		}
		if (jobDataMap.containsKey("productQuantitiesByLocation")) {
			productQuantitiesByLocation = (HashMap<Location, HashMap<Product, Short>>) jobDataMap
					.get("productQuantitiesByLocation");
		}

		if (alertCategory != null && alertType != null && locationId != null && productQuantitiesByLocation != null) {
			// 1. Get an alert of category STOCK_LEVEL and the given type.
			// 2. If the alert is not found, return. Otherwise go to step 3.
			// 3. If the alert is NOT enabled, return. Otherwise go to step
			// 4.
			if (alertCategory == AlertCategory.STOCK_LEVEL) {
				if (alertType == AlertType.WARNING) {
					Alert alert = getAlert(AlertCategory.STOCK_LEVEL, AlertType.WARNING);
					if (alert != null && alert.isEnabled() && alert.isImmediate()) {
						if (!productQuantitiesByLocation.isEmpty()) {
							Location location = em.getReference(Location.class, locationId);
							logger.info(String.format(
									"Attempting to send immediate email warning alerts to recipients for the location '%s'.",
									location.getName()));
							this.sendEmails(alert, productQuantitiesByLocation);
						}
					}
				} else if (alertType == AlertType.RECOVERY) {
					Alert alert = getAlert(AlertCategory.STOCK_LEVEL, AlertType.RECOVERY);
					if (alert != null && alert.isEnabled() && alert.isImmediate()) {
						if (!productQuantitiesByLocation.isEmpty()) {
							Location location = em.getReference(Location.class, locationId);
							logger.info(String.format(
									"Attempting to send immediate email recovery alerts to recipients for the location '%s'.",
									location.getName()));
							// If the alert is immediate, delete the
							// products entry from the ProductAlertInfo
							// table and send email alert.
							List<Long> productIds = new ArrayList<>();
							for (Map.Entry<Location, HashMap<Product, Short>> entry : productQuantitiesByLocation
									.entrySet()) {
								for (Product product : entry.getValue().keySet()) {
									Long productId = product.getId();
									if (!productIds.contains(productId)) {
										productIds.add(productId);
									}
								}
							}
							List<ProductAlertInfo> productAlertInfos = getProductAlertInfo(locationId, productIds,
									AlertCategory.STOCK_LEVEL, AlertType.RECOVERY);
							for (ProductAlertInfo productAlertInfo : productAlertInfos) {
								em.remove(productAlertInfo);
							}
							this.sendEmails(alert, productQuantitiesByLocation);
						}
					}
				}
			}
		} else if (alertCategory != null && alertType != null && locationId == null
				&& productQuantitiesByLocation == null) {
			if (alertCategory == AlertCategory.STOCK_LEVEL) {
				if (alertType == AlertType.WARNING) {
					HashMap<Location, HashMap<Product, Short>> productsBelowReorderPoint = getProductsBelowReorderPoint();
					if (!productsBelowReorderPoint.isEmpty()) {
						logger.info(
								"Attempting to send deffered email warning alerts to recipients for all locations.");
						Alert alert = getAlert(AlertCategory.STOCK_LEVEL, AlertType.WARNING);
						this.sendEmails(alert, productsBelowReorderPoint);
					}
				}
			}
		}
	}

	private Alert getAlert(AlertCategory alertCategory, AlertType alertType) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Alert> cq = cb.createQuery(Alert.class);
		Root<Alert> rt = cq.from(Alert.class);
		cq.select(rt);
		Predicate p1 = cb.equal(rt.get("alertCategory"), alertCategory);
		Predicate p2 = cb.equal(rt.get("alertType"), alertType);
		cq.where(cb.and(p1, p2));
		TypedQuery<Alert> tq = em.createQuery(cq);
		List<Alert> alerts = tq.getResultList();
		if (alerts != null && !alerts.isEmpty()) {
			return alerts.get(0);
		}
		return null;
	}

	private void sendEmails(Alert alert, HashMap<Location, HashMap<Product, Short>> productsCount)
			throws JobExecutionException {
		if (productsCount.isEmpty()) {
			return;
		}
		List<EmailRecipient> emailRecipients = alert.getEmailRecipients();

		if (emailRecipients == null || emailRecipients.isEmpty()) {
			logger.info(String.format(
					"Stock Level alert emails will not be sent because no email recipients for an alert of type %s were found.",
					alert.getAlertType()));
			return;
		}
		List<MimeMessage> messages = new ArrayList<>();

		for (EmailRecipient emailRecipient : emailRecipients) {

			Pair<String, String> subjectContentPair = getEmailSubjectContentPair(alert, emailRecipient, productsCount);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper;
			try {
				helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
				mimeMessage.setContent(subjectContentPair.getValue(), "text/html");
				helper.setTo(emailRecipient.getEmail());
				// Set the message's subject

				helper.setSubject(subjectContentPair.getKey());
				// This is not mandatory, however, it is a good
				// practice to indicate the software which
				// constructed the message.
				mimeMessage.setHeader("X-Mailer", applicationName);
				// Adjust the date of sending the message
				Date timeStamp = new Date();
				mimeMessage.setSentDate(timeStamp);
			} catch (MessagingException e) {
				throw new JobExecutionException(e);
			}

			messages.add(mimeMessage);
		}
		if (!messages.isEmpty()) {
			mailSender.send(messages.toArray(new MimeMessage[messages.size()]));
			logger.info("Email alerts sent successfully to recipients.");
		}

	}

	private Pair<String, String> getEmailSubjectContentPair(Alert alert, EmailRecipient emailRecipient,
			HashMap<Location, HashMap<Product, Short>> productsCount) {
		String alertDetails = getStockLevelAlertDetails(LOCATION_NAME_HEADER, ITEM_NUMBER_HEADER, ITEM_NAME_HEADER,
				ITEM_REORDER_POINT_HEADER, AVAILABLE_QUANTITY_HEADER, productsCount);
		String subject = null;
		String content = null;
		Locale recipientLocale = emailRecipient.getLocale();
		if (recipientLocale != null) {
			List<EmailTemplate> emailTemplates = alert.getEmailTemplates();
			if (emailTemplates != null && !emailTemplates.isEmpty()) {
				for (EmailTemplate emailTemplate : emailTemplates) {
					if (emailTemplate.getLocale() != null) {
						if (emailTemplate.getLocale().equals(recipientLocale)) {
							subject = emailTemplate.getSubject();
							content = emailTemplate.getContent();
							break;
						}
					}
				}
				if (subject == null) {
					subject = emailTemplates.get(0).getSubject();
				}
				if (content == null) {
					content = emailTemplates.get(0).getContent();
				}
			}
		}
		if (subject == null) {
			switch (alert.getAlertType()) {
			case WARNING:
				subject = STOCK_LEVEL_WARNING_SUBJECT;
				break;
			case RECOVERY:
				subject = STOCK_LEVEL_RECOVERY_SUBJECT;
				break;
			default:
				subject = "";
				break;
			}
		}
		if (content == null) {
			switch (alert.getAlertType()) {
			case WARNING:
				content = getDefaultWarningEmailTemplate();
				break;
			case RECOVERY:
				content = getDefaultRecoveryEmailTemplate();
				break;
			default:
				content = "";
				break;
			}
		}

		content = content.replace(ALERT_EMAIL_RECIPIENT_NAME_PLACE_HOLDER, emailRecipient.getName())
				.replace(ALERT_CURRENT_DATE_PLACE_HOLDER, new Date().toString())
				.replace(ALERT_INVENTORY_DETAILS_PLACE_HOLDER, alertDetails);

		return new Pair<>(subject, content);
	}

	private List<ProductAlertInfo> getProductAlertInfo(Long locationId, List<Long> productIds,
			AlertCategory alertCategory, AlertType alertType) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductAlertInfo> cq = cb.createQuery(ProductAlertInfo.class);
		Root<ProductAlertInfo> rt = cq.from(ProductAlertInfo.class);
		cq.select(rt);
		Predicate p1 = cb.equal(rt.get(ITEM_ALERT_INFO_ID_ATTR).get(ITEM_ALERT_INFO_LOCATION_ID_ATTR), locationId);
		Predicate p2 = rt.get(ITEM_ALERT_INFO_ID_ATTR).get(ITEM_ALERT_INFO_ITEM_ID_ATTR).in(productIds);
		Predicate p3 = cb.equal(rt.get(ITEM_ALERT_INFO_ID_ATTR).get(ITEM_ALERT_INFO_ALERT_CATEGORY_ATTR),
				alertCategory);
		Predicate p4 = null;
		if (alertType != null) {
			p4 = cb.equal(rt.get(PRODUCT_ALERT_INFO_ALERT_TYPE_ATTR), alertType);
		}
		cq.where(p4 == null ? cb.and(p1, p2, p3) : cb.and(p1, p2, p3, p4));
		TypedQuery<ProductAlertInfo> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	private HashMap<Location, HashMap<Product, Short>> getProductsBelowReorderPoint() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<ProductInventory> productInventoryRoot = cq.from(ProductInventory.class);
		Root<Product> productRoot = cq.from(Product.class);
		Root<Location> locationRoot = cq.from(Location.class);
		cq.select(cb.tuple(locationRoot, productRoot, productInventoryRoot.get(PRODUCT_INVENTORY_QUANTITY_ATTR)));
		Predicate p1 = cb.equal(
				productInventoryRoot.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_PRODUCT_ID_ATTR),
				productRoot.get(PRODUCT_ID_ATTR));
		Predicate p2 = cb.equal(
				productInventoryRoot.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_LOCATION_ID_ATTR),
				locationRoot.get(LOCATION_ID_ATTR));
		Predicate p3 = cb.lessThanOrEqualTo(productInventoryRoot.<Short>get(PRODUCT_INVENTORY_QUANTITY_ATTR),
				productRoot.<Short>get(PRODUCT_REORDER_POINT_ATTR));
		cq.where(cb.and(p1, p2, p3));

		TypedQuery<Tuple> tq = em.createQuery(cq);

		List<Tuple> tuples = tq.getResultList();
		HashMap<Location, HashMap<Product, Short>> result = new HashMap<>();
		for (Tuple tuple : tuples) {
			Location location = (Location) tuple.get(0);
			if (!result.containsKey(location)) {
				HashMap<Product, Short> value = new HashMap<>();
				result.put(location, value);
			}
			HashMap<Product, Short> value = result.get(location);
			value.put((Product) tuple.get(1), (short) tuple.get(2));
		}
		return result;
	}

	private String getStockLevelAlertDetails(String locationNameHeader, String productNumberHeader,
			String productNameHeader, String productReorderPointHeader, String AvailableQuantityHeader,
			HashMap<Location, HashMap<Product, Short>> productsCount) {
		StringBuilder builder = new StringBuilder();
		builder.append("<hr>");
		builder.append("<table class=\"TFtable\">");
		builder.append(getTableStyle());
		builder.append("<th>");
		builder.append(locationNameHeader);
		builder.append("</th>");
		builder.append("<th>");
		builder.append(productNumberHeader);
		builder.append("</th>");
		builder.append("<th>");
		builder.append(productNameHeader);
		builder.append("</th>");
		builder.append("<th>");
		builder.append(productReorderPointHeader);
		builder.append("</th>");
		builder.append("<th>");
		builder.append(AvailableQuantityHeader);
		builder.append("</th>");

		for (Map.Entry<Location, HashMap<Product, Short>> entry : productsCount.entrySet()) {
			Location location = entry.getKey();
			HashMap<Product, Short> entry1 = productsCount.get(location);
			for (Product product : entry1.keySet()) {
				builder.append("<tr>");
				builder.append("<td>");
				builder.append(location.getName());
				builder.append("</td>");
				builder.append("<td>");
				builder.append(product.getNumber());
				builder.append("</td>");
				builder.append("<td>");
				builder.append(product.getName());
				builder.append("</td>");
				builder.append("<td>");
				builder.append(product.getReorderPoint());
				builder.append("</td>");
				builder.append("<td>");
				builder.append(entry1.get(product));
				builder.append("</td>");
				builder.append("</tr>");
			}
		}
		builder.append("</table>");
		return builder.toString();
	}

	private String getDefaultWarningEmailTemplate() {
		return "Dear %%alert.stockLevel.emailRecipient.name%%,\n" + "<br/>\n" + "<br/>\n"
				+ "Below is a summary of items for which the quantities in the inventory dropped below the reorder point.\n"
				+ "<br/>\n" + "%%alert.stockLevel.inventory.details%%\n" + "<br/>\n" + "<br/>\n" + "Regards";
	}

	private String getDefaultRecoveryEmailTemplate() {
		return "Dear %%alert.stockLevel.emailRecipient.name%%,\n" + "<br/>\n" + "<br/>\n"
				+ "Below is a summary of items for which the quantities in the inventory recovered above the reorder point.\n"
				+ "<br/>\n" + "%%alert.stockLevel.inventory.details%%\n" + "<br/>\n" + "<br/>\n" + "Regards";
	}

	private String getTableStyle() {
		return "<style type=\"text/css\">\n" + "	.TFtable{\n" + "		width:100%; \n"
				+ "		border-collapse:collapse; \n" + "	}\n" + "	.TFtable td{ \n"
				+ "		padding:7px; border:#4e95f4 1px solid;\n" + "	}\n"
				+ "	/* provide some minimal visual accomodation for IE8 and below */\n" + "	.TFtable tr{\n"
				+ "		background: #b8d1f3;\n" + "	}\n"
				+ "	/*  Define the background color for all the ODD background rows  */\n"
				+ "	.TFtable tr:nth-child(odd){ \n" + "		background: #b8d1f3;\n" + "	}\n"
				+ "	/*  Define the background color for all the EVEN background rows  */\n"
				+ "	.TFtable tr:nth-child(even){\n" + "		background: #dae5f4;\n" + "	}\n" + "</style>";
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		setDependencies(context);
		run(context);
	}

	private void setDependencies(JobExecutionContext context) throws JobExecutionException {
		SchedulerContext schedulerContext = null;

		try {
			schedulerContext = context.getScheduler().getContext();
		} catch (SchedulerException e) {
			throw new JobExecutionException(e);
		}

		this.em = ((EntityManagerFactory) schedulerContext.get("entityManagerFactory")).createEntityManager();
		this.mailSender = (JavaMailSender) schedulerContext.get("mailSender");
		this.applicationName = (String) schedulerContext.get("applicationName");
	}

}
