package org.rainbow.security.service.reporter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class ChangeReporter {
	private static final String QUOTE = "\"";
	private final JdbcTemplate jdbcTemplate;

	public ChangeReporter(JdbcTemplate jdbcTemplate) {
		Objects.requireNonNull(jdbcTemplate, "The jdbcTemplate argument cannot be null.");
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void report(String userName, boolean isSelfService, ChangeReporterInfo changeReporterInfo) {
		final Date now = Calendar.getInstance().getTime();

		getJdbcTemplate().query(changeReporterInfo.getSelectQuery(), changeReporterInfo.getSelectArgs(),
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						if (changeReporterInfo.getSourceLastUpdateDateColumnLabel() != null
								|| changeReporterInfo.getSourceUpdaterColumnLabel() != null
								|| changeReporterInfo.getSourceVersionColumnLabel() != null) {
							getJdbcTemplate().update(new PreparedStatementCreator() {
								@Override
								public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
									return con.prepareStatement(
											buildUpdateQuery(changeReporterInfo, rs, userName, isSelfService, now));

								}
							});
						}
						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

								return con.prepareStatement(
										buildInsertQuery(changeReporterInfo, rs, userName, isSelfService, now));

							}
						});

					}
				});

	}

	private String getAuthenticatedUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null && context.getAuthentication() != null) {
			return context.getAuthentication().getName();
		}
		return null;
	}

	private static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private String buildUpdateQuery(ChangeReporterInfo changeReporterInfo, ResultSet rs, String userName,
			boolean isSelfService, Date date) throws SQLException {
		Timestamp timestamp = new Timestamp(date.getTime());

		StringBuilder builder = new StringBuilder("UPDATE ");
		builder.append(changeReporterInfo.getSourceTableName());
		builder.append(" SET ");
		boolean appendComma = false;
		if (changeReporterInfo.getSourceLastUpdateDateColumnLabel() != null) {
			builder.append(changeReporterInfo.getSourceLastUpdateDateColumnName());
			builder.append("=");
			builder.append(QUOTE);
			builder.append(timestamp);
			builder.append(QUOTE);
			appendComma = true;
		}
		if (changeReporterInfo.getSourceUpdaterColumnLabel() != null) {
			if (appendComma) {
				builder.append(",");
			}
			builder.append(changeReporterInfo.getSourceUpdaterColumnName());
			builder.append("=");
			builder.append(QUOTE);
			builder.append(isSelfService ? userName : getAuthenticatedUser());
			builder.append(QUOTE);
			appendComma = true;
		}
		if (changeReporterInfo.getSourceVersionColumnLabel() != null) {
			// Only update the version if and only if the write operation is an
			// update.
			if (changeReporterInfo.getTargetWriteOperation() == WriteOperation.UPDATE) {
				if (appendComma) {
					builder.append(",");
				}
				builder.append(changeReporterInfo.getSourceVersionColumnName());
				builder.append("=");

				ResultSetMetaData md = rs.getMetaData();
				Class<?> versionClass = ChangeReporter.getClass(
						md.getColumnClassName(rs.findColumn(changeReporterInfo.getSourceVersionColumnLabel())));

				if (Number.class.isAssignableFrom(versionClass)) {
					builder.append(changeReporterInfo.getSourceVersionColumnName());
					builder.append("+1");
				} else if (Date.class.isAssignableFrom(versionClass)) {
					builder.append(timestamp);
				}

				appendComma = true;
			}
		}

		builder.append(" WHERE ");
		builder.append(changeReporterInfo.getSourceIdColumnName());
		builder.append("=");
		builder.append(rs.getObject(rs.findColumn(changeReporterInfo.getSourceIdColumnLabel())));

		return builder.toString();
	}

	private String buildInsertQuery(ChangeReporterInfo changeReporterInfo, ResultSet rs, String userName,
			boolean isSelfService, Date date) throws SQLException {
		Timestamp timestamp = new Timestamp(date.getTime());
		ResultSetMetaData md = rs.getMetaData();

		final StringBuilder columnListBuilder = new StringBuilder("(");
		final StringBuilder valueListBuilder = new StringBuilder("(");

		final Map<String, String> columnMaps = changeReporterInfo.getColumnMaps();
		if (columnMaps != null) {
			final List<String> sourceColumnLabels = new ArrayList<>(columnMaps.keySet());

			columnListBuilder.append(columnMaps.get(sourceColumnLabels.get(0)));

			appendValue(valueListBuilder, changeReporterInfo, rs, 0, timestamp, userName, isSelfService);

			for (int i = 1; i < sourceColumnLabels.size(); i++) {
				columnListBuilder.append(",");
				columnListBuilder.append(columnMaps.get(sourceColumnLabels.get(i)));

				valueListBuilder.append(",");

				final int columnIndex = rs.findColumn(sourceColumnLabels.get(i));
				appendValue(valueListBuilder, changeReporterInfo, rs, columnIndex, timestamp, userName, isSelfService);
			}
		} else {
			columnListBuilder.append(md.getColumnName(1));

			appendValue(valueListBuilder, changeReporterInfo, rs, 1, timestamp, userName, isSelfService);

			int columnCount = md.getColumnCount();

			for (int i = 2; i <= columnCount; i++) {
				columnListBuilder.append(",");
				final String columnName = md.getColumnName(i);

				columnListBuilder.append(columnName);

				valueListBuilder.append(",");
				appendValue(valueListBuilder, changeReporterInfo, rs, i, timestamp, userName, isSelfService);
			}
		}
		columnListBuilder.append(",");
		columnListBuilder.append(changeReporterInfo.getTargetWriteOperationColumnName());
		columnListBuilder.append(")");

		valueListBuilder.append(",");
		valueListBuilder.append(QUOTE);
		valueListBuilder.append(changeReporterInfo.getTargetWriteOperation());
		valueListBuilder.append(QUOTE);
		valueListBuilder.append(")");

		StringBuilder insertQuery = new StringBuilder("INSERT INTO ");
		insertQuery.append(changeReporterInfo.getTargetTableName());
		insertQuery.append(columnListBuilder.toString());
		insertQuery.append(" VALUES ");
		insertQuery.append(valueListBuilder.toString());
		return insertQuery.toString();
	}

	private void tryAppendQuote(StringBuilder builder, int columnIndex, ResultSet rs) throws SQLException {
		if (areQuotesRequired(columnIndex, rs)) {
			builder.append(QUOTE);
		}
	}

	private boolean areQuotesRequired(int columnIndex, ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		Class<?> columnClass = getClass(md.getColumnClassName(columnIndex));

		if (Number.class.isAssignableFrom(columnClass)) {
			return false;
		}
		if (Boolean.class.isAssignableFrom(columnClass)) {
			return false;
		}
		return true;
	}

	private void appendValue(StringBuilder builder, ChangeReporterInfo changeReporterInfo, ResultSet rs,
			int columnIndex, Timestamp timestamp, String userName, boolean isSelfService) throws SQLException {
		final ResultSetMetaData md = rs.getMetaData();
		final String columnName = md.getColumnName(columnIndex);

		if (columnName.equalsIgnoreCase(changeReporterInfo.getTargetLastUpdateDateColumnName())) {
			tryAppendQuote(builder, columnIndex, rs);
			builder.append(timestamp);
			tryAppendQuote(builder, columnIndex, rs);
		} else if (columnName.equalsIgnoreCase(changeReporterInfo.getTargetUpdaterColumnName())) {
			builder.append(QUOTE);
			builder.append(isSelfService ? userName : getAuthenticatedUser());
			builder.append(QUOTE);
		} else if (columnName.equalsIgnoreCase(changeReporterInfo.getTargetVersionColumnName())) {
			Class<?> versionClass = getClass(md.getColumnClassName(columnIndex));

			if (Number.class.isAssignableFrom(versionClass)) {
				builder.append(rs.getObject(columnIndex));
				// Only increment the version if and only if the write operation
				// is an update.
				if (changeReporterInfo.getTargetWriteOperation() == WriteOperation.UPDATE) {
					builder.append("+1");
				}
			} else if (Date.class.isAssignableFrom(versionClass)) {
				// Only update the version if and only if the write operation is
				// an update.
				if (changeReporterInfo.getTargetWriteOperation() == WriteOperation.UPDATE) {
					tryAppendQuote(builder, columnIndex, rs);
					builder.append(timestamp);
					tryAppendQuote(builder, columnIndex, rs);
				}
			}
		} else {
			tryAppendQuote(builder, columnIndex, rs);
			builder.append(rs.getObject(columnIndex));
			tryAppendQuote(builder, columnIndex, rs);
		}

	}
}
