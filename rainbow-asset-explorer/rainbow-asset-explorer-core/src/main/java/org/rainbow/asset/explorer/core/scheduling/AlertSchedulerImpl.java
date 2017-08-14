/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.scheduling;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.rainbow.asset.explorer.core.entities.Alert;
import org.rainbow.asset.explorer.core.entities.AlertCategory;
import org.rainbow.asset.explorer.core.entities.AlertType;
import org.rainbow.asset.explorer.core.entities.DayOfWeek;
import org.rainbow.asset.explorer.core.entities.Location;
import org.rainbow.asset.explorer.core.entities.Month;
import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.entities.ProductAlertInfo;
import org.rainbow.asset.explorer.core.entities.ProductAlertInfoId;
import org.rainbow.asset.explorer.core.entities.ProductInventory;
import org.rainbow.asset.explorer.core.entities.Schedule;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author Biya-Bi
 */
public class AlertSchedulerImpl implements AlertScheduler {

	private static final String PRODUCT_ID_ATTR = "id";
	private static final String LOCATION_ID_ATTR = "id";
	private static final String PRODUCT_INVENTORY_ID_ATTR = "id";
	private static final String PRODUCT_INVENTORY_LOCATION_ID_ATTR = "locationId";
	private static final String PRODUCT_INVENTORY_PRODUCT_ID_ATTR = "productId";
	private static final String PRODUCT_INVENTORY_QUANTITY_ATTR = "quantity";

	private static final String PRODUCT_REORDER_POINT_ATTR = "reorderPoint";
	private static final String PRODUCT_ALERT_INFO_ID_ATTR = "id";
	private static final String PRODUCT_ALERT_INFO_LOCATION_ID_ATTR = "locationId";
	private static final String PRODUCT_ALERT_INFO_ITEM_ID_ATTR = "productId";
	private static final String PRODUCT_ALERT_INFO_ALERT_CATEGORY_ATTR = "alertCategory";
	private static final String PRODUCT_ALERT_INFO_ALERT_TYPE_ATTR = "alertType";

	private static final String INVENTORY_LEVEL_WARNING_TRIGGER_GROUP = "INVENTORY_LEVEL_WARNING_TRIGGER";
	private static final String INVENTORY_LEVEL_RECOVERY_TRIGGER_GROUP = "INVENTORY_LEVEL_RECOVERY_TRIGGER";

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("schedulerFactoryBean")
	private SchedulerFactoryBean schedulerFactoryBean;

	private String toString(DayOfWeek dayOfWeek) {
		if (dayOfWeek == null) {
			return "*";
		}
		switch (dayOfWeek) {
		case SUNDAY:
			return "SUN";
		case MONDAY:
			return "MON";
		case TUESDAY:
			return "TUE";
		case WEDNESDAY:
			return "WED";
		case THURSDAY:
			return "THU";
		case FRIDAY:
			return "FRI";
		case SATURDAY:
			return "SAT";
		default:
			return "*";
		}
	}

	private String toString(Month month) {
		if (month == null) {
			return null;
		}
		switch (month) {
		case JANUARY:
			return "JAN";
		case FEBRUARY:
			return "FEB";
		case MARCH:
			return "MAR";
		case APRIL:
			return "APR";
		case MAY:
			return "MAY";
		case JUNE:
			return "JUN";
		case JULY:
			return "JUL";
		case AUGUST:
			return "AUG";
		case SEPTEMBER:
			return "SEP";
		case OCTOBER:
			return "OCT";
		case NOVEMBER:
			return "NOV";
		case DECEMBER:
			return "DEC";
		default:
			return null;
		}
	}

	private String buildTriggerName(Alert alert) {
		return "TRIGGER_" + alert.toString();
	}

	private String getTriggerGroup(Alert alert) {
		if (alert.getAlertType() == AlertType.RECOVERY)
			return INVENTORY_LEVEL_RECOVERY_TRIGGER_GROUP;
		return INVENTORY_LEVEL_WARNING_TRIGGER_GROUP;
	}

	private TriggerKey buildTriggerKey(Alert alert) {
		return new TriggerKey(buildTriggerName(alert), getTriggerGroup(alert));
	}

	private CronTrigger buildCronTrigger(Alert alert) {
		Schedule schedule = alert.getSchedule();

		String second = schedule.getSecond() != null ? schedule.getSecond().toString() : "*";
		String minute = schedule.getMinute() != null ? schedule.getMinute().toString() : "*";
		String hour = schedule.getHour() != null ? schedule.getHour().toString() : "*";
		String dayOfMonth = schedule.getDayOfMonth() != null && !schedule.getDayOfMonth().trim().isEmpty()
				? schedule.getDayOfMonth().trim() : "?";
		String month = schedule.getMonth() != null ? toString(schedule.getMonth()) : "*";
		// We only consider day of week if day of month is not specified.
		String dayOfWeek = schedule.getDayOfWeek() != null && dayOfMonth.equalsIgnoreCase("?")
				? toString(schedule.getDayOfWeek()) : "?";
		String year = schedule.getYear() != null ? schedule.getYear() : null;
		String timezone = null;

		if (schedule.getTimezone() != null && !schedule.getTimezone().trim().isEmpty()) {
			timezone = schedule.getTimezone();
		}
		if (dayOfMonth.equalsIgnoreCase("?") && dayOfWeek.equalsIgnoreCase("?"))
			dayOfMonth = "*";

		String space = " ";
		StringBuilder builder = new StringBuilder();
		builder.append(second);
		builder.append(space);
		builder.append(minute);
		builder.append(space);
		builder.append(hour);
		builder.append(space);
		builder.append(dayOfMonth);
		builder.append(space);
		builder.append(month);
		builder.append(space);
		builder.append(dayOfWeek);
		if (year != null) {
			builder.append(space);
			builder.append(year);
		}
		String cronExpression = builder.toString();
		CronScheduleBuilder cronScheduleBuilder = cronSchedule(cronExpression);
		if (timezone != null)
			cronScheduleBuilder = cronScheduleBuilder.inTimeZone(TimeZone.getTimeZone(timezone));

		return newTrigger().withIdentity(buildTriggerKey(alert)).withSchedule(cronScheduleBuilder).build();
	}

	private JobDataMap createJobData(AlertCategory alertCategory, AlertType alertType, Long locationId,
			HashMap<Location, HashMap<Product, Short>> productQuantitiesByLocation) {
		if (alertCategory == null)
			throw new IllegalArgumentException("The alertCategory argument cannot be null.");
		if (alertType == null)
			throw new IllegalArgumentException("The alertType argument cannot be null.");

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("alertCategory", alertCategory);
		jobDataMap.put("alertType", alertType);
		if (locationId != null)
			jobDataMap.put("locationId", locationId);
		if (productQuantitiesByLocation != null)
			jobDataMap.put("productQuantitiesByLocation", productQuantitiesByLocation);
		return jobDataMap;
	}

	private JobDetail createJobDetail(AlertCategory alertCategory, AlertType alertType, Long locationId,
			HashMap<Location, HashMap<Product, Short>> productQuantitiesByLocation) {
		return newJob(AlertJob.class)
				.usingJobData(createJobData(alertCategory, alertType, locationId, productQuantitiesByLocation)).build();
	}

	private void schedule(Alert alert, Long locationId,
			HashMap<Location, HashMap<Product, Short>> productQuantitiesByLocation) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobDetail jobDetail = createJobDetail(alert.getAlertCategory(), alert.getAlertType(), locationId,
				productQuantitiesByLocation);
		CronTrigger trigger = buildCronTrigger(alert);
		scheduler.scheduleJob(jobDetail, trigger);
	}

	private void scheduleJob(AlertCategory alertCategory, AlertType alertType, Long locationId,
			HashMap<Location, HashMap<Product, Short>> productQuantitiesByLocation) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobDetail jobDetail = createJobDetail(alertCategory, alertType, locationId, productQuantitiesByLocation);
		Trigger trigger = newTrigger().usingJobData(
				createJobData(AlertCategory.STOCK_LEVEL, AlertType.WARNING, locationId, productQuantitiesByLocation))
				.build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	public void schedule(Alert alert) throws SchedulerException {
		schedule(alert, null, null);
	}

	public void unschedule(Alert alert) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		TriggerKey triggerKey = buildTriggerKey(alert);
		if (scheduler.checkExists(triggerKey)) {
			scheduler.unscheduleJob(triggerKey);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.persistence.dao.AlertScheduler#schedule(
	 * org.rainbow.asset.explorer.core.entities.AlertType, java.lang.Long,
	 * java.util.List)
	 */
	@Override
	public void schedule(AlertType alertType, Long locationId, List<Long> productIds) throws SchedulerException {
		if (alertType == AlertType.WARNING)
			scheduleWarning(locationId, productIds);
		else if (alertType == AlertType.RECOVERY)
			scheduleRecovery(locationId, productIds);
	}

	private void scheduleWarning(Long locationId, List<Long> productIds) throws SchedulerException {
		// 1. Get products below the reorder point.
		// 2. If any of them were marked as recovered, mark them as warning.
		// 3. If none of them were marked as warning, simply add them to the
		// ProductAlertInfo table.
		// The next steps are for the timer.
		// 4. Get the alert with the given alert category and of type warning.
		// 5. If the alert is enabled, go to step 6, else, return.
		// 6. Since the alert is enabled, check if it is immediate. If yes, send
		// an alert now, else, return. The alert will be sent at the scheduled
		// time.
		HashMap<Location, HashMap<Product, Short>> criticalProductQuantitiesByLocation = (HashMap<Location, HashMap<Product, Short>>) getEntries(
				locationId, productIds, AlertType.WARNING);
		// If the inventory of the given location does not contain one of the
		// products for which the Id was supplied in productIds, no entries for
		// that product will be found. Since this is a warning, we therefore
		// need to explicitly say that the quantities in the inventory of the
		// specified location, of those products is 0. To achieve our goal, from
		// the given list of product IDs, we
		// first need to get a list of all those product IDs that are available
		// in
		// the inventory. We will then use that list to get the product IDs that
		// are not in the inventory.
		List<Long> availableProductIds = getAvailableProductIds(locationId, productIds);

		List<Long> inventoryMissingProductIds = new ArrayList<>();

		if (availableProductIds.isEmpty()) {
			inventoryMissingProductIds.addAll(productIds);
		} else {
			for (Long productId : productIds) {
				boolean found = false;

				for (Long availableProductId : availableProductIds) {
					if (availableProductId.equals(productId)) {
						found = true;
						break;
					}
				}

				if (!found) {
					inventoryMissingProductIds.add(productId);
				}
			}
		}

		Location location = new Location(locationId);

		if (!criticalProductQuantitiesByLocation.containsKey(location)) {
			location = em.getReference(Location.class, locationId);
			criticalProductQuantitiesByLocation.put(location, new HashMap<Product, Short>());
		}
		if (!inventoryMissingProductIds.isEmpty()) {
			HashMap<Product, Short> productsQuantities = criticalProductQuantitiesByLocation.get(location);
			for (Product product : getProducts(inventoryMissingProductIds)) {
				productsQuantities.put(product, (short) 0);
			}
		}

		List<ProductAlertInfo> productAlertInfos = getProductAlertInfo(locationId, productIds,
				AlertCategory.STOCK_LEVEL, null);

		Date now = new Date();
		for (Map.Entry<Location, HashMap<Product, Short>> entry : criticalProductQuantitiesByLocation.entrySet()) {
			for (Product product : entry.getValue().keySet()) {
				ProductAlertInfoId productAlertInfoId = new ProductAlertInfoId(product.getId(), locationId,
						AlertCategory.STOCK_LEVEL);
				ProductAlertInfo productAlertInfo = null;
				boolean found = false;
				for (ProductAlertInfo p : productAlertInfos) {
					if (productAlertInfoId.equals(p.getId())) {
						productAlertInfo = p;
						found = true;
						break;
					}
				}
				if (productAlertInfo == null) {
					productAlertInfo = new ProductAlertInfo(productAlertInfoId);
				}
				productAlertInfo.setAlertDate(now);
				productAlertInfo.setAlertType(AlertType.WARNING);
				productAlertInfo.setAvailableQuantity(entry.getValue().get(product));
				productAlertInfo.setReorderPoint(product.getReorderPoint());
				productAlertInfo.setLastUpdateDate(now);
				if (found) {
					em.merge(productAlertInfo);
				} else {
					productAlertInfo.setCreationDate(now);
					em.persist(productAlertInfo);
				}
			}
		}

		if (!criticalProductQuantitiesByLocation.isEmpty()) {
			scheduleJob(AlertCategory.STOCK_LEVEL, AlertType.WARNING, locationId, criticalProductQuantitiesByLocation);
		}
	}

	private void scheduleRecovery(Long locationId, List<Long> productIds) throws SchedulerException {
		// 1. Get products above the reorder point.
		// 2. If any of them were marked as warning, mark them as recovered,
		// else ignore the product (it wasn't marked as warning).
		// The next steps are for the timer.
		// 3. Get an alert of category STOCK_LEVEL and type RECOVERY.
		// 4. If the alert is not found, return. Otherwise go to step 5.
		// 5. If the alert is NOT enabled, return. Otherwise go to step 6.
		// 6. If the alert is immidiate, delete the product entry from the
		// ProductAlertInfo table and send email alert. Otherwise, go to step 7.
		// 7. Since the alert is not immediate, add the product to
		// ProductAlertInfo table if it does not exist there, or update it if it
		// does.
		HashMap<Location, HashMap<Product, Short>> productsAboveReorderPoint = (HashMap<Location, HashMap<Product, Short>>) getEntries(
				locationId, productIds, AlertType.RECOVERY);
		List<ProductAlertInfo> productAlertInfos = getProductAlertInfo(locationId, productIds,
				AlertCategory.STOCK_LEVEL, null);

		HashMap<Location, HashMap<Product, Short>> productQuantitiesByLocation = new HashMap<>();

		Date now = new Date();
		for (Map.Entry<Location, HashMap<Product, Short>> entry : productsAboveReorderPoint.entrySet()) {
			for (Product product : entry.getValue().keySet()) {
				ProductAlertInfoId productAlertInfoId = new ProductAlertInfoId(product.getId(), locationId,
						AlertCategory.STOCK_LEVEL);
				for (ProductAlertInfo productAlertInfo : productAlertInfos) {
					if (productAlertInfoId.equals(productAlertInfo.getId())) {
						productAlertInfo.setAlertDate(now);
						productAlertInfo.setAlertType(AlertType.RECOVERY);
						productAlertInfo.setAvailableQuantity(entry.getValue().get(product));
						productAlertInfo.setReorderPoint(product.getReorderPoint());
						productAlertInfo.setLastUpdateDate(now);
						em.merge(productAlertInfo);

						if (!productQuantitiesByLocation.containsKey(entry.getKey())) {
							productQuantitiesByLocation.put(entry.getKey(), entry.getValue());
						} else {
							HashMap<Product, Short> productsCount = productQuantitiesByLocation.get(entry.getKey());
							if (!productsCount.containsKey(product)) {
								productsCount.put(product, entry.getValue().get(product));
							}
						}
						break;
					}
				}
			}
		}
		if (!productQuantitiesByLocation.isEmpty()) {
			scheduleJob(AlertCategory.STOCK_LEVEL, AlertType.RECOVERY, locationId, productQuantitiesByLocation);
		}
	}

	private HashMap<Location, HashMap<Product, Short>> getEntries(Long locationId, List<Long> productIds,
			AlertType alertType) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<ProductInventory> productInventoryRoot = cq.from(ProductInventory.class);
		Root<Product> productRoot = cq.from(Product.class);
		Root<Location> locationRoot = cq.from(Location.class);

		cq.select(cb.tuple(locationRoot, productRoot, productInventoryRoot.get(PRODUCT_INVENTORY_QUANTITY_ATTR)));

		Predicate p1 = cb.equal(
				productInventoryRoot.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_LOCATION_ID_ATTR),
				locationId);
		Predicate p2 = cb.equal(
				productInventoryRoot.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_LOCATION_ID_ATTR),
				locationRoot.get(LOCATION_ID_ATTR));
		Predicate p3 = cb.equal(
				productInventoryRoot.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_PRODUCT_ID_ATTR),
				productRoot.get(PRODUCT_ID_ATTR));
		Predicate p4 = productRoot.get(PRODUCT_ID_ATTR).in(productIds);
		Predicate p5;
		if (alertType == AlertType.WARNING) {
			p5 = cb.lessThanOrEqualTo(productInventoryRoot.<Short>get(PRODUCT_INVENTORY_QUANTITY_ATTR),
					productRoot.<Short>get(PRODUCT_REORDER_POINT_ATTR));
		} else {
			p5 = cb.greaterThan(productInventoryRoot.<Short>get(PRODUCT_INVENTORY_QUANTITY_ATTR),
					productRoot.<Short>get(PRODUCT_REORDER_POINT_ATTR));
		}
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(p1);
		predicates.add(p2);
		predicates.add(p3);
		predicates.add(p4);
		predicates.add(p5);
		Predicate predicate = null;
		for (Predicate p : predicates) {
			if (predicate == null) {
				predicate = p;
			} else {
				predicate = cb.and(predicate, p);
			}
		}
		cq.where(predicate);

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

	private List<ProductAlertInfo> getProductAlertInfo(Long locationId, List<Long> productIds,
			AlertCategory alertCategory, AlertType alertType) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductAlertInfo> cq = cb.createQuery(ProductAlertInfo.class);
		Root<ProductAlertInfo> rt = cq.from(ProductAlertInfo.class);
		cq.select(rt);
		Predicate p1 = cb.equal(rt.get(PRODUCT_ALERT_INFO_ID_ATTR).get(PRODUCT_ALERT_INFO_LOCATION_ID_ATTR),
				locationId);
		Predicate p2 = rt.get(PRODUCT_ALERT_INFO_ID_ATTR).get(PRODUCT_ALERT_INFO_ITEM_ID_ATTR).in(productIds);
		Predicate p3 = cb.equal(rt.get(PRODUCT_ALERT_INFO_ID_ATTR).get(PRODUCT_ALERT_INFO_ALERT_CATEGORY_ATTR),
				alertCategory);
		Predicate p4 = null;
		if (alertType != null) {
			p4 = cb.equal(rt.get(PRODUCT_ALERT_INFO_ALERT_TYPE_ATTR), alertType);
		}
		cq.where(p4 == null ? cb.and(p1, p2, p3) : cb.and(p1, p2, p3, p4));
		TypedQuery<ProductAlertInfo> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	private List<Product> getProducts(List<Long> productIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		Root<Product> rt = cq.from(Product.class);
		cq.select(rt);
		cq.where(rt.get(PRODUCT_ID_ATTR).in(productIds));
		TypedQuery<Product> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	/**
	 * Return the IDs of the {@link Product}s whose IDs appear in the supplied
	 * {@code productIds} list, and available in the inventory of the
	 * {@link Location} with ID {@code locationId}
	 * 
	 * @param locationId
	 *            the ID of the location for which the inventory will be checked
	 * @param productIds
	 *            the list of {@link Product} IDs to check in the inventory of
	 *            the {@link Location} with the given ID
	 * @return the IDs of the {@link Product}s whose IDs appear in the supplied
	 *         {@code productIds} list, and available in the inventory of the
	 *         {@link Location} with ID {@code locationId}
	 */
	private List<Long> getAvailableProductIds(Long locationId, List<Long> productIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<ProductInventory> productInventoryRoot = cq.from(ProductInventory.class);

		cq.select(cb.tuple(productInventoryRoot.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_PRODUCT_ID_ATTR)));

		Predicate p1 = cb.equal(
				productInventoryRoot.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_LOCATION_ID_ATTR),
				locationId);

		Predicate p2 = productInventoryRoot.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_PRODUCT_ID_ATTR)
				.in(productIds);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(p1);
		predicates.add(p2);

		cq.where(cb.and(p1, p2));

		TypedQuery<Tuple> tq = em.createQuery(cq);

		List<Tuple> tuples = tq.getResultList();
		List<Long> result = new ArrayList<>();
		for (Tuple tuple : tuples) {
			result.add((Long) tuple.get(0));
		}
		return result;
	}

}
