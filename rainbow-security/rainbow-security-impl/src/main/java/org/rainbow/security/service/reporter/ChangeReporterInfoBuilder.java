package org.rainbow.security.service.reporter;

import java.util.Map;

public class ChangeReporterInfoBuilder {
	private String selectQuery;
	private Object[] selectArgs;
	private String sourceTableName;
	private String sourceIdColumnLabel;
	private String sourceIdColumnName;
	private String sourceLastUpdateDateColumnLabel;
	private String sourceLastUpdateDateColumnName;
	private String sourceUpdaterColumnLabel;
	private String sourceUpdaterColumnName;
	private String sourceVersionColumnLabel;
	private String sourceVersionColumnName;
	private Map<String, String> columnMaps;
	private String targetTableName;
	private String targetLastUpdateDateColumnName;
	private String targetUpdaterColumnName;
	private String targetVersionColumnName;
	private String targetWriteOperationColumnName;
	private WriteOperation targetWriteOperation;

	public ChangeReporterInfoBuilder selectQuery(String selectQuery) {
		this.selectQuery = selectQuery;
		return this;
	}

	public ChangeReporterInfoBuilder selectArgs(Object[] selectArgs) {
		this.selectArgs = selectArgs;
		return this;
	}

	public ChangeReporterInfoBuilder sourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
		return this;
	}

	public ChangeReporterInfoBuilder sourceIdColumnLabel(String sourceIdColumnLabel) {
		this.sourceIdColumnLabel = sourceIdColumnLabel;
		return this;
	}

	public ChangeReporterInfoBuilder sourceIdColumnName(String sourceIdColumnName) {
		this.sourceIdColumnName = sourceIdColumnName;
		return this;
	}

	public ChangeReporterInfoBuilder sourceLastUpdateDateColumnLabel(String sourceLastUpdateDateColumnLabel) {
		this.sourceLastUpdateDateColumnLabel = sourceLastUpdateDateColumnLabel;
		return this;
	}

	public ChangeReporterInfoBuilder sourceLastUpdateDateColumnName(String sourceLastUpdateDateColumnName) {
		this.sourceLastUpdateDateColumnName = sourceLastUpdateDateColumnName;
		return this;
	}

	public ChangeReporterInfoBuilder sourceUpdaterColumnLabel(String sourceUpdaterColumnLabel) {
		this.sourceUpdaterColumnLabel = sourceUpdaterColumnLabel;
		return this;
	}

	public ChangeReporterInfoBuilder sourceUpdaterColumnName(String sourceUpdaterColumnName) {
		this.sourceUpdaterColumnName = sourceUpdaterColumnName;
		return this;
	}

	public ChangeReporterInfoBuilder sourceVersionColumnLabel(String sourceVersionColumnLabel) {
		this.sourceVersionColumnLabel = sourceVersionColumnLabel;
		return this;
	}

	public ChangeReporterInfoBuilder sourceVersionColumnName(String sourceVersionColumnName) {
		this.sourceVersionColumnName = sourceVersionColumnName;
		return this;
	}

	public ChangeReporterInfoBuilder columnMaps(Map<String, String> columnMaps) {
		this.columnMaps = columnMaps;
		return this;
	}

	public ChangeReporterInfoBuilder targetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
		return this;
	}

	public ChangeReporterInfoBuilder targetLastUpdateDateColumnName(String targetLastUpdateDateColumnName) {
		this.targetLastUpdateDateColumnName = targetLastUpdateDateColumnName;
		return this;
	}

	public ChangeReporterInfoBuilder targetUpdaterColumnName(String targetUpdaterColumnName) {
		this.targetUpdaterColumnName = targetUpdaterColumnName;
		return this;
	}

	public ChangeReporterInfoBuilder targetVersionColumnName(String targetVersionColumnName) {
		this.targetVersionColumnName = targetVersionColumnName;
		return this;
	}

	public ChangeReporterInfoBuilder targetWriteOperationColumnName(String targetWriteOperationColumnName) {
		this.targetWriteOperationColumnName = targetWriteOperationColumnName;
		return this;
	}

	public ChangeReporterInfoBuilder targetWriteOperation(WriteOperation targetWriteOperation) {
		this.targetWriteOperation = targetWriteOperation;
		return this;
	}

	public ChangeReporterInfo build() {
		return new ChangeReporterInfo(selectQuery, selectArgs, sourceTableName, sourceIdColumnLabel, sourceIdColumnName,
				sourceLastUpdateDateColumnLabel, sourceLastUpdateDateColumnName, sourceUpdaterColumnLabel,
				sourceUpdaterColumnName, sourceVersionColumnLabel, sourceVersionColumnName, columnMaps, targetTableName,
				targetLastUpdateDateColumnName, targetUpdaterColumnName, targetVersionColumnName,
				targetWriteOperationColumnName, targetWriteOperation);
	}

}
